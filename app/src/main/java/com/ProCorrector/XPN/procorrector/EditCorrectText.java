package com.ProCorrector.XPN.procorrector;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;


public class EditCorrectText extends AppCompatActivity {

    private EditText content;                                           /// content View of the document
    private EditText title;                                             /// title View of the document
    private LinearLayout correctionBox;                                 /// correction View of suggestions
    private LinearLayout continuationBox;                               /// continuation View of suggestions
    private ScrollView editTextScroll;                                  /// the whole EditText is placed in ScrollVew to make scrolling smooth, without it scrolling is done by moving the cursor
    private ListView correctionListView;                                /// the ListView of suggested corrections
    private ListView continuationListView;                              /// the ListView of suggested continuations
    private CardView suggestionsCard;                                   /// all the suggestions are placed in CardView to have a shadow
    private float suggestionWidth;                                      /// the width of one block of suggestions
    private CharSequence text;                                          /// the whole text typed in document
    private ArrayList <String> continuationList = new ArrayList<>();    /// the list of suggested continuations of a word that are displayed in continuationListView
    private ArrayList <String> correctionList = new ArrayList<>();      /// the list of suggested corrections   of a word that are displayed in correctionListView
    private CharSequence displayedSuggestions = "";                     /// current word for which the suggestions are displayed
    private DatabaseHelper database;                                    /// the database from which the queries are done
    private int documentID;                                             /// ID of the document in TextDatabase

    private CorrectionListAdapter correctionListAdapter = new CorrectionListAdapter();          /// keep it to notifyDatasetChanged insted of creating new adapter every time
    private ContinuationListAdapter continuationListAdapter = new ContinuationListAdapter();    /// keep it to notifyDatasetChanged insted of creating new adapter every time

    private final String CACHE = "data";                                /// the name of SharedPreferences file
    private static boolean suggestionsTipShown;                         /// kept for not searching it every time from SharedPreferences -> gets its initial value in onCreate
    private static boolean addToDictionaryTipShown;                     /// kept for not searching it every time from SharedPreferences -> gets its initial value in onCreate

    private existsTask ExistsTask = null;                               /// checks if the word is correct or not and marks wrong words with a red color
    private textChangedTask TextChangedTask = null;                     /// called whenever change happens in the text
    private underliningTask underliningTask = null;                     /// called to underline wrong words
    private correctionAndContinuationTask CorrectionAndContinuationTask = null; /// gets all suggestions at once


    private Deque< ArrayList <Integer> > history = new ArrayDeque <>(); /// history of made changes on the document -> needed to detect the right indices for underliningTask
    private static final int MAX_HISTORY_EPISODES = 500;                /// after 500 iterations the history is deleted
    private int historyTimer = 0;                                       /// timer to know at which stage we currently are

    private boolean justCreated = true;                                 /// some work is skipped when we just launch the app (making suggestions for the first word for example, as it takes time and is not needed because of being annoying)
    private boolean showSuggestions = true;                             /// a flag that can be changed by pressing the yellow bulb in the action-bar
    private Menu myMenu = null;                                         /// action-bar menu

    /// the default language of the app is English and this part is dedicated for recoding utility
    private static String CurrentRecordingLanguageLocale = Locale.ENGLISH.toString();
    private static String CurrentRecordingLanguageCode = "ENG";
    private static String CurrentRecordingLanguageName = "English";
    private static int CurrentRecordingLanguageID = -1;
    private static final int SPEECH_REQUEST_CODE = 0;

    /*******************************actions support functions**************************************/
    /**
     * Set the recording language according to its ID
     * @param languageID    the ID got from the class R
     */
    private void setLanguage( int languageID ) {

        MenuItem flag = myMenu.findItem(R.id.action_choose_language);
        CurrentRecordingLanguageCode = RecordingLanguage.getLanguageCodeFromLanguageID( languageID );
        CurrentRecordingLanguageLocale = RecordingLanguage.getLanguageLocaleFromLanguageID( languageID );
        CurrentRecordingLanguageName = RecordingLanguage.getLanguageNameFromLanguageID( languageID );
        int flagID = RecordingLanguage.getLanguageFlagFromLanguageID( languageID );
        flag.setIcon( flagID );

        /// some languages can be recorded but don't have their database of words so the program may make wrong corrections
        /// the word Schwein is a German word but the program will consider it as English and will find it incorrect
        if( !Language.supportsLanguage( CurrentRecordingLanguageCode ) ) {
            Toast.makeText( EditCorrectText.this, CurrentRecordingLanguageName + " is not fully supported!\nThe program may make wrong corrections.", Toast.LENGTH_LONG ).show();
        }
    }

    /**
     * Saves the document to database
     * Parameters: title | text | date
     */
    private void saveNoteToDatabase() {

        String contentValue = content.getText().toString();
        String titleValue = title.getText().toString();

        if (contentValue.matches("") && titleValue.matches(""))     return;
        if (titleValue.matches(""))                                 titleValue = "Untitled";

        DateFormat dateFormat = new SimpleDateFormat( "MMM dd\nHH:mm", Locale.US );
        String creationDate = dateFormat.format( new Date() );

        TextDatabase db = new TextDatabase(getApplicationContext());
        if( documentID == -1 )  { db.insert( titleValue, contentValue, creationDate ); }
        else                    { db.update(titleValue, contentValue, documentID);   }

        //Log.d("Note", "Was saved to database" );
    }

    private void copyTextToClipboard() {

        ClipboardManager clipboard = (ClipboardManager) getSystemService( CLIPBOARD_SERVICE );
        ClipData clip = ClipData.newPlainText("Spell Checker", text);
        clipboard.setPrimaryClip( clip );
        Toast.makeText( EditCorrectText.this, "Text copied to clipboard", Toast.LENGTH_LONG ).show();
    }

    /**
     * Called when the bulb is clicked
     * it changes the parameter showSuggestions to its opposite and changes the color of the bulb
     * @param item  the MenuItem of the bulb
     */
    private void showOrHideSuggestions( MenuItem item ) {

        if( !suggestionsTipShown ) {
            suggestionsTipShown = true;
            SharedPreferences.Editor editor = getSharedPreferences(CACHE, MODE_PRIVATE).edit();
            editor.putBoolean( "suggestionsTipShown", true );
            editor.apply();
        }
        showSuggestions = !showSuggestions;
        if( CorrectionAndContinuationTask != null )
            CorrectionAndContinuationTask.cancel( true );

        suggestWord();
        if( showSuggestions )   item.setIcon( R.mipmap.show_suggestions_on_icon );
        else                    item.setIcon( R.mipmap.show_suggestions_off_icon );
    }

    /**
     * Shows "Ignore Once" and "Add to dictionary" icons if the current word is marked red
     */
    private void showOrHideIgnoreAddIcons() {

        SpannableString text = new SpannableString( content.getText() );
        int now = content.getSelectionStart();
        ForegroundColorSpan[] spans=text.getSpans(now, now+1, ForegroundColorSpan.class);
        try {
            MenuItem ignoreOnce = myMenu.findItem(R.id.action_ignore_once);
            MenuItem addToLibrary = myMenu.findItem(R.id.action_add_to_library);

            if (spans.length != 0) {
                ignoreOnce.setVisible(true);
                addToLibrary.setVisible(true);

                if( !addToDictionaryTipShown && suggestionsTipShown )
                    showAddToDictionaryTip();
            }
            else {
                ignoreOnce.setVisible(false);
                addToLibrary.setVisible(false);
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Takes the current word and adds to correctWords in DatabaseHelper
     * That causes the program to think that this word is actually correct
     */
    private void ignoreOnce() {

        String word = Language.getCurrentSubstring(text, content.getSelectionStart());

        DatabaseHelper.wrongWords.remove( DatabaseHelper.convertToSQLiteFormat(word) );
        DatabaseHelper.correctWords.add( DatabaseHelper.convertToSQLiteFormat(word) );
        underliningTask = new underliningTask();
        underliningTask.execute(0, content.getText().length());
        Toast.makeText( EditCorrectText.this, word + " will be ignored in this document", Toast.LENGTH_SHORT ).show();
    }

    /**
     * Adds the current word to library
     * Done in background because working with database may take a lot of time which is impossible in the main UI thread
     */
    private void addToLibrary() {

        String word = Language.getCurrentSubstring(text, content.getSelectionStart());
        addToLibraryTask task = new addToLibraryTask();
        task.execute(word);
        Toast.makeText(EditCorrectText.this, word + " added to dictionary", Toast.LENGTH_SHORT).show();
    }



    private void displaySpeechRecognizer() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, CurrentRecordingLanguageLocale);    /// set the language here
        startActivityForResult(intent, SPEECH_REQUEST_CODE);    // Start the activity, the intent will be populated with the speech text
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText

            int l = content.getSelectionStart();
            int r = content.getSelectionEnd();
            replaceFragment(spokenText, l, r);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Sends the note via messenger or e-mail
     */
    private void sendNote() {

        String messageBody = content.getText().toString();
        String messageTitle = title.getText().toString();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, messageTitle);
        i.putExtra(Intent.EXTRA_TEXT, messageBody);
        try {
            startActivity(Intent.createChooser( i, "Send Message...") );
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( EditCorrectText.this, "There are no messaging applications installed", Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * Write feedback by E-mail to XPNInc@gmail.com
     */
    private void writeFeedback() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "Spell Checker Feedback");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"XPNInc@gmail.com"});
        try {
            startActivity(Intent.createChooser(i, "Choose an Email client :"));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( EditCorrectText.this, "There are no Email applications installed", Toast.LENGTH_SHORT ).show();
        }
    }


    /**
     * Shows tips to new user
     * @param layoutResID   which layout to show from res/layout.xml
     * @param viewResID     clicking on this view results in dismissing this dialog
     * @param hintType      needed to turn the flag off in SharedPreferences
     */
    private void showOverlay( int layoutResID, int viewResID, final String hintType ) {

        final Dialog dialog = new Dialog(EditCorrectText.this, R.style.TransparentStyle);
        dialog.setContentView( layoutResID );
        final RelativeLayout layout = (RelativeLayout) dialog.findViewById( viewResID );
        Animation animation = AnimationUtils.loadAnimation( EditCorrectText.this, R.anim.fade_in );
        dialog.show();
        layout.startAnimation(animation);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( hintType.matches( "suggestionsTipShown" ) )             suggestionsTipShown = true;
                else if( hintType.matches( "addToDictionaryTipShown" ) )    addToDictionaryTipShown = true;
                SharedPreferences sp = getSharedPreferences(CACHE, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean( hintType, true);
                editor.apply();

                Animation animation = AnimationUtils.loadAnimation(EditCorrectText.this, R.anim.fade_out);
                layout.startAnimation(animation);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 250);
            }
        });
    }
    /**
     * Shown during the first launch or when the user clicks on Help & Tips
     */
    private void showSuggestionsTip() {
        showOverlay( R.layout.help_and_tips_suggestion, R.id.help_and_tips_suggestion, "suggestionsTipShown" );
    }
    /**
     * Shown during the first launch or when the user clicks on Help & Tips
     */
    private void showAddToDictionaryTip() {
        showOverlay(R.layout.help_and_tips_ignore_add, R.id.help_and_tips_ignore_add, "addToDictionaryTipShown" );
    }
    /**
     * Shown during the first launch or when the user clicks on Help & Tips
     */
    private void showHelpAndTips() {

        SharedPreferences sp = getSharedPreferences(CACHE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        addToDictionaryTipShown = false;
        suggestionsTipShown = false;
        editor.remove("addToDictionaryTipShown");
        editor.remove("suggestionsTipShown");
        editor.apply();

        showSuggestionsTip();
    }

    /*******************************actions support functions**************************************/
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_correct_text);
        Bundle bundle = getIntent().getExtras();

        /// these are done because findViewById takes a lot of time
        content = (EditText) findViewById( R.id.text );
        title = (EditText) findViewById( R.id.title );
        correctionBox = (LinearLayout) findViewById( R.id.correction_box );
        continuationBox = (LinearLayout) findViewById( R.id.continuation_box );
        editTextScroll = (ScrollView) findViewById( R.id.scroll );
        correctionListView = (ListView) findViewById( R.id.correction_list );
        continuationListView = (ListView) findViewById( R.id.continuation_list );
        suggestionsCard = (CardView) findViewById( R.id.suggestions_card );
        suggestionWidth = getResources().getDimension(R.dimen.suggestion_box_width);

        /// initialize variables from SharedPreferences because every time making a query will be time-consuming
        suggestionsTipShown = getSharedPreferences( CACHE, MODE_PRIVATE ).contains( "suggestionsTipShown" );
        addToDictionaryTipShown = getSharedPreferences( CACHE, MODE_PRIVATE ).contains( "addToDictionaryTipShown" );



        /// use toolbar as actionbar
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");           /// we don't need a title in this activity
        setSupportActionBar(toolbar);

        /// click on the done icon has to save notes and exit
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNoteToDatabase();
                finish();
            }
        });

        /// initialize helper classes
        new Language();
        new RecordingLanguage( EditCorrectText.this );
        if( CurrentRecordingLanguageID == -1 )
            CurrentRecordingLanguageID = RecordingLanguage.getLanguageIDFromLanguageCode( CurrentRecordingLanguageCode );


        /// get the info about this document with bundle
        documentID = bundle.getInt( "id" );
        String previous_title = bundle.getString( "title" );
        String previous_content = bundle.getString( "content" );
        if( previous_title != null && previous_title.matches( "Untitled" ) )
            previous_title = "";


        /// DatabaseHelper is single-tone
        database = DatabaseHelper.getInstance(this);
        try                     { database.createDataBase(); }
        catch (IOException e)   { e.printStackTrace(); }



        /*************************** Listeners ****************************************************/
        /// clicking on the text has to produce the process of showing the suggestions
        content.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (CorrectionAndContinuationTask != null)
                    CorrectionAndContinuationTask.cancel(true);
                suggestWord();
                showOrHideIgnoreAddIcons();
                //Log.d("EditCorrectText", "content is clicked");
            }
        });
        /// most part is done in TextChangeTask to save time for UI thread
        content.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Log.d("text changed", s + " START=" + start + " B=" + before + " C=" + count);
                text = s;

                if (before == count) {
                    //Log.d( "EditCorrectText", "color is changed" );
                    return;
                }

                setSuggestionsInvisibleAndClearLists();
                if (CorrectionAndContinuationTask != null)
                    CorrectionAndContinuationTask.cancel(true);

                if (TextChangedTask != null)
                    TextChangedTask.cancel(true);

                if (start != 0 || count != 0) {

                    TextChangedTask = new textChangedTask();
                    TextChangedTask.execute(start, before, count, s);
                }
            }
        });

        /// display previous notes
        /// written after content.listener to ensure that the wrong words will be detected and marked
        content.setText(previous_content);
        title.setText(previous_title);


        /// set adapters to suggestion lists
        correctionListView.setAdapter( correctionListAdapter );
        continuationListView.setAdapter( continuationListAdapter );

        /// click on a suggestion item has to replace the current word with the clicked one
        correctionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { replaceWord(correctionList.get(position)); }
        });
        continuationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { replaceWord(continuationList.get(position)); }
        });


        /// when the view is scrolled the suggestions have to be hidden
        editTextScroll.setSmoothScrollingEnabled(true);
        editTextScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            int prevY = 0;

            @Override
            public void onScrollChanged() {

                int scrollY = editTextScroll.getScrollY();
                if (Math.abs(prevY - scrollY) > 30) {
                    setSuggestionsInvisible();
                    prevY = scrollY;
                }
            }
        });



        /// this prevents the program from crashing
        /// it closes the current page so that the user thinks that he touched the back button 3:)
        /*Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                saveNoteToDatabase();
                Thread.dumpStack();
                System.exit( 2 );
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        saveNoteToDatabase();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        int id = item.getItemId();

        if( id == R.id.action_show_suggestions )        { showOrHideSuggestions(item); }
        else if( id == R.id.action_ignore_once )        { ignoreOnce(); }
        else if( id == R.id.action_add_to_library )     { addToLibrary(); }
        else if( id == R.id.action_copy )               { copyTextToClipboard(); }
        else if( id == R.id.action_record )             { displaySpeechRecognizer(); }
        else if( id == R.id.action_send )               { sendNote(); }
        else if( RecordingLanguage.isLanguageID(id) )   { setLanguage( id );    displaySpeechRecognizer(); }
        //else if( documentID == R.documentID.action_settings )       { openSettings(); } //TODO
        else if( id == R.id.action_help )               { showHelpAndTips(); }
        else if( id == R.id.action_feedback )           { writeFeedback(); }
        else if( id == R.id.action_about )              { Intent i = new Intent(EditCorrectText.this, About.class);     startActivity(i); }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Menu icons are inflated just as they were with actionbar
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_correct_text, menu);
        myMenu = menu;
        if( CurrentRecordingLanguageID == -1 )
            CurrentRecordingLanguageID = R.id.action_language_ENG;
        setLanguage(CurrentRecordingLanguageID);

        return true;
    }


    /**
     * Sets correctionList + continuationList under cursor
     * Also handles the case when one of them or both are empy
     */
    private void setSuggestionsUnderCursor() {

        /// we don't have to do extra work if showing suggestions is not permitted
        if( !showSuggestions )
            return;

        //Log.d( "EditCorrectText", "setSuggestionsUnderCursor" );
        Layout content_layout = content.getLayout();
        int pos = content.getSelectionStart();
        int line = content_layout.getLineForOffset(pos);
        float x = content_layout.getPrimaryHorizontal(pos);
        float h = editTextScroll.getScrollY();
        float y = content_layout.getLineBottom(line);
        float boxWidth = suggestionsCard.getPaddingLeft() + suggestionsCard.getPaddingRight();

        if( !correctionList.isEmpty() )     boxWidth += suggestionWidth;        /// + length of one suggestion block to boxWidth
        if( !continuationList.isEmpty() )   boxWidth += suggestionWidth;        /// + length of one suggestion block to boxWidth

        y -= h + suggestionsCard.getPaddingTop();                               /// move y coordinate to its real place on the screen
        y += content.getPaddingTop();                                           /// content EditText has a padding from the top
        x += content.getPaddingLeft();                                          /// content EditText has a padding from the left

        x -= boxWidth / 2;                                                      /// the cursor has to be in the middle of suggestions
        x = Math.min( x, editTextScroll.getWidth() - boxWidth );                /// we can't get out of the screen
        x = Math.max( x, 0 );                                                   /// we can't get our of the screen

        suggestionsCard.setX(x);                                                /// finally set the x coordinate
        suggestionsCard.setY(y);                                                /// finally set the y coordinate


        /// if the app is launched the first time show that its possible to turn suggestions on/off
        if( !suggestionsTipShown ) {
            showSuggestionsTip();
        }
    }


    private void setContinuationListVisible() {

        suggestionsCard.setVisibility(View.VISIBLE);
        continuationBox.setVisibility(View.VISIBLE);
    }
    private void setCorrectionListVisible() {

        suggestionsCard.setVisibility(View.VISIBLE);
        correctionBox.setVisibility(View.VISIBLE);
    }
    private void setSuggestionsVisible() {

        setCorrectionListVisible();
        setContinuationListVisible();
    }
    private void setSuggestionsInvisible() {

        suggestionsCard.setVisibility(View.GONE);
        correctionBox.setVisibility(View.GONE);
        continuationBox.setVisibility(View.GONE);
    }
    private void setSuggestionsInvisibleAndClearLists() {

        if( CorrectionAndContinuationTask != null )
            CorrectionAndContinuationTask.cancel( true );

        setSuggestionsInvisible();
        correctionList.clear();
        continuationList.clear();
        displayedSuggestions = "";
    }

    /**
     * Done in main thread
     * Starts an AsyncTask if there is a need of making a database query to get suggestions
     * Aligns current suggestions under cursor if displayed suggestions are made for the current word and are not empty
     */
    public void suggestWord() {

        /// no need of doing extra work if showing suggestions is not allowed
        if( !showSuggestions ) {
            //Log.d( "EditCorrectText", "we don't need suggestions" );
            setSuggestionsInvisibleAndClearLists();
            return;
        }

        String needed = Language.getCurrentSubstring(text, content.getSelectionStart());
        if( needed.length() >= 2 ) {

            /// the first part of if() is written for speed
            if( needed.length() == displayedSuggestions.length() && needed.matches( displayedSuggestions.toString() ) ) {

                setSuggestionsUnderCursor();
                return;
            }

            /// if we have a new word => we have to start a new suggestion task and clear previous lists
            setSuggestionsInvisibleAndClearLists();
            displayedSuggestions = needed;

            CorrectionAndContinuationTask = new correctionAndContinuationTask();
            CorrectionAndContinuationTask.execute( needed );
        }
        else {
            /// words that have too small length are not queried because there are a lot of words starting with a for example and it will be slow
            setSuggestionsInvisibleAndClearLists();
        }
    }

    /**
     * Done in background and is called by underliningTask
     * @param start starting position
     * @param count number of characters
     */
    public void underlineWrongWords( int start, int count ) {

        CharSequence s = text;
        if( s.length() == 0 )
            return;

        int end = start + count;
        start -= 3;                                                 /// to be safe
        end   += 3;                                                 /// to be safe
        if( start < 0 )             start = 0;                      /// not to go out of the string
        if( end < 0 )               end = 0;                        /// not to go out of the string
        if( start > s.length()-1 )  start = s.length()-1;           /// not to go out of the string
        if( end > s.length()-1 )    end = s.length()-1;             /// not to go out of the string


        /// come to the right place if the endpoints are in a whitespace or so
        while (start < s.length() && !Language.isCorrectLetter(s.charAt(start)))            start++;
        while (end >= 0 && end < s.length() && !Language.isCorrectLetter(s.charAt(end)))    end--;
        if (end < start || start >= s.length() || end < 0 )                                 return;

        if( ExistsTask != null && count == 1 && start > 0 && Language.isCorrectLetter( s.charAt(start-1) ) )    ExistsTask.cancel( true );
        else                                                                                                    ExistsTask = null;

        /// determine the best length
        while (start >= 0 && start < s.length() && Language.isCorrectLetter(s.charAt(start)))   start--;
        while (end < s.length() && Language.isCorrectLetter(s.charAt(end)))                     end++;


        /// executed only when number of unknown words is > 2
        checkCorrectnessOfkMultipleWordsTask task = new checkCorrectnessOfkMultipleWordsTask();
        task.execute( s, start, end );

        /// distribute tasks for making the wrong words underlined
        for( int i = start + 1; i < end && end <= text.length(); ) {

            String word = "";
            int j,  begin = i, finish;
            for( j = i; j < end && end <= text.length() && Language.isCorrectLetter( s.charAt(j) ); j++ )
                word += s.charAt(j);
            finish = j;

            for( i = j; i < end && end <= text.length() && !Language.isCorrectLetter( s.charAt(i) ); i++ ) ;
            ExistsTask = new existsTask();
            ExistsTask.execute( word, begin, finish, historyTimer, text );
        }
    }

    /**
     * Replaces some part of the text with another string
     * @param newString the new string
     * @param l         the left offset of the initial text - inclusive
     * @param r         the right offset of the initial text - exclusive
     */
    public void replaceFragment( String newString, int l, int r ) {

        SpannableStringBuilder res = new SpannableStringBuilder( content.getText());
        res.delete(l, r);
        res.insert(l, newString);
        content.setText(res);
        content.setSelection(l + newString.length());

        history.clear();
        historyTimer = 0;
    }

    /**
     * Replaces current word with the new one
     * Done in main thread
     * @param newWord   the new word
     */
    public void replaceWord( String newWord )  {

        setSuggestionsInvisibleAndClearLists();
        int position = content.getSelectionStart();
        int l = position;
        int r = position;

        /// check if we are not at the end of a word
        if( position >= text.length() || !Language.isCorrectLetter( text.charAt( position ) ) )
            r--;

        String currentLanguage = Language.getLanguageCode( text.charAt( r ) );
        while( r+1 < text.length() && Language.isCorrectLetter( text.charAt( r+1 ), currentLanguage ) ) r++;
        while( l-1 >= 0 && Language.isCorrectLetter( text.charAt( l-1 ), currentLanguage)) l--;

        replaceFragment(newWord, l, Math.min(r + 1, text.length()));
        setSuggestionsInvisibleAndClearLists();
    }

    private void showCorrectionList() {

        if( correctionList.isEmpty() )
            return;

        setCorrectionListVisible();
        correctionListAdapter.notifyDataSetChanged();
    }
    private void showContinuationList() {

        if( continuationList.isEmpty() )
            return;

        setContinuationListVisible();
        continuationListAdapter.notifyDataSetChanged();
    }
    private void capitalizeFirstLetter(ArrayList<String> list) {

        for (int i = 0; i < list.size(); i++)
            list.set(i, Character.toUpperCase(list.get(i).charAt(0)) + list.get(i).substring(1));
    }
    private void capitalizeIfNeeded( ArrayList <String> list ) {

        if( displayedSuggestions.length() > 0 && Character.isUpperCase( displayedSuggestions.charAt(0) ) ) {
            capitalizeFirstLetter(list);
            return;
        }

        int cursorPosition = content.getSelectionStart();
        int i = cursorPosition - 1;
        while(  i >= 0 && i < text.length() && Language.isCorrectLetter( text.charAt( i ) ) )
            i--;

        for( ; i >= 0 && i < text.length(); i-- ) {
            if( text.charAt( i ) == '.' || text.charAt( i ) == '!' || text.charAt( i ) == '?' ) {
                capitalizeFirstLetter( list );
                return;
            }
            else if( Language.isCorrectLetter( text.charAt( i ) ) )
                return;
        }

        /// if its the first word in the sentence
        capitalizeFirstLetter( list );
    }


/*******************************background tasks***************************************************/
    class textChangedTask extends AsyncTask<Object, Object, Pair <Integer, Integer> > {

        protected Pair <Integer, Integer> doInBackground(Object... params) {

            int start = Integer.parseInt(params[0].toString());
            int before = Integer.parseInt(params[1].toString());
            int count = Integer.parseInt(params[2].toString());
            CharSequence s = (CharSequence) params[3];

            if (Math.abs(count - before) >= 1 && (count != s.length() || justCreated)) {

                ArrayList<Integer> episode = new ArrayList<>();    /// one episode in the history
                episode.add(historyTimer);
                episode.add(start);
                episode.add(before);
                episode.add(count);

                history.addLast(episode);
                while (history.size() > MAX_HISTORY_EPISODES)
                    history.pollFirst();

                historyTimer++;
            }

            return new Pair( start, count );
        }

        protected void onPostExecute( Pair p ) {

            if( !justCreated )
                suggestWord();
            justCreated = false;
            underliningTask = new underliningTask();
            underliningTask.execute(p.first, p.second);
        }
    }
    class underliningTask extends  AsyncTask <Object, Object, Object> {

        protected Object doInBackground(Object... params) {

            int start = Integer.parseInt(params[0].toString());
            int count = Integer.parseInt( params[1].toString() );
            underlineWrongWords(start, count);
            return null;
        }
    }
    class checkCorrectnessOfkMultipleWordsTask extends AsyncTask <Object, Object, Object>{
        @Override
        protected Object doInBackground(Object ... params) {
            String s = params[0].toString();
            int start = Integer.parseInt(params[1].toString());
            int end = Integer.parseInt(params[2].toString());
            database.checkCorrectnessOfParagraph(s.substring(start + 1, end - 1));
            return null;
        }
    }
    class correctionAndContinuationTask extends AsyncTask <String, Object, Object> {

        @Override
        protected Object doInBackground(String... params) {

            String word = params[0];
            database.getCorrectionAndContinuation( word, correctionList, continuationList );

            capitalizeIfNeeded( correctionList );
            capitalizeIfNeeded( continuationList );
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            setSuggestionsUnderCursor();
            showCorrectionList();
            showContinuationList();
        }
    }
    class existsTask extends AsyncTask <Object, Object, ArrayList<Object> > {

        @Override
        protected ArrayList<Object> doInBackground(Object... params) {
            String word = params[0].toString();
            int start  = Integer.parseInt(params[1].toString());
            int finish = Integer.parseInt( params[2].toString() );
            int taskID = Integer.parseInt( params[3].toString() );
            int result = database.isCorrectWord(word) ? 1 : 0;

            /// get real positions of start and finish
            for( ArrayList <Integer> episode : history )
            {
                int timer    = episode.get( 0 );
                int changeID = episode.get( 1 );
                int before   = episode.get( 2 );
                int count    = episode.get( 3 );

                if( timer > taskID )
                {
                    if( changeID > start && changeID < finish )     return null; /// something is changed in the string itself
                    else if( changeID < finish )                    { finish += count-before;   start += count-before; }
                }
            }

            if( finish < start || start < 0 || finish > text.length() )
                return null;

            ArrayList<Object> res = new ArrayList<>();
            res.add(start);
            res.add(finish);
            res.add( result );

            return res;
        }

        @Override
        protected void onPostExecute(ArrayList <Object> res) {

            if( this.isCancelled() || res == null )
                return;

            int start  = Integer.parseInt( res.get(0).toString() );
            int finish = Integer.parseInt( res.get(1).toString() );
            int result = Integer.parseInt( res.get(2).toString() );


            SpannableString text = new SpannableString( content.getText() );
            if( result == 0 ) {
                boolean isAlreadySet = true;
                for( int i=start; i < finish; i++ ) {
                    ForegroundColorSpan[] spans = text.getSpans(i, i+1, ForegroundColorSpan.class);
                    if( spans.length == 0 ) {
                        isAlreadySet = false;
                        break;
                    }
                }

                finish = Math.min( finish, text.length() );
                if( isAlreadySet )  return;
                else                text.setSpan(new ForegroundColorSpan(Color.parseColor("#D20000")), start, finish, 0);
            }
            else {
                ForegroundColorSpan[] spans=text.getSpans(start, finish, ForegroundColorSpan.class);
                if( spans.length == 0 )
                    return;

                for (ForegroundColorSpan span : spans)
                    text.removeSpan(span);
            }

            int cursorStartPosition = content.getSelectionStart();
            int cursorEndPosition = content.getSelectionEnd();
            content.setText(text);
            content.setSelection(cursorStartPosition, cursorEndPosition);
        }
    }
    class addToLibraryTask extends AsyncTask <Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {

            String word = params[0].toString();
            database.insert(word);
            DatabaseHelper.wrongWords.remove( DatabaseHelper.convertToSQLiteFormat(word) );
            DatabaseHelper.correctWords.add( DatabaseHelper.convertToSQLiteFormat(word) );
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            underliningTask = new underliningTask();
            underliningTask.execute(0, text.length());
        }
    }
/*******************************background tasks***************************************************/


/**********************************Adapters********************************************************/

    class CorrectionListAdapter extends BaseAdapter {

        ViewHolder viewHolder;
        public int getCount()               { return correctionList.size(); }
        public Object getItem(int position) { return null; }
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if( convertView == null ) {

                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate( R.layout.suggestion_list_item, parent, false );
                viewHolder.suggestion = (TextView) convertView.findViewById( R.id.suggestion );
                convertView.setTag( viewHolder );
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String row = correctionList.get(position);
            viewHolder.suggestion.setText(row);

            return convertView;
        }
    }
    class ContinuationListAdapter extends BaseAdapter {

        ViewHolder viewHolder;
        public int getCount()               { return continuationList.size(); }
        public Object getItem(int position) { return null; }
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if( convertView == null ) {

                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate( R.layout.suggestion_list_item, parent, false );
                viewHolder.suggestion = (TextView) convertView.findViewById( R.id.suggestion );
                convertView.setTag( viewHolder );
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String row = continuationList.get(position);
            viewHolder.suggestion.setText(row);

            return convertView;
        }
    }

    /**
     * ViewHolder for correction and continuation suggestions
     */
    class ViewHolder {
        TextView suggestion;
    }

/**********************************Adapters********************************************************/
}