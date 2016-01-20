package com.ProCorrector.XPN.procorrector;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.lang.Object;


public class EditCorrectText extends AppCompatActivity {

    private EditText content;
    private EditText title;
    private CharSequence text;
    private ArrayList <String> continuationList = new ArrayList<>();
    private ArrayList <String> correctionList = new ArrayList<>();
    private CharSequence displayedSuggestions = "";
    private DatabaseHelper database;
    private int documentID;

    private existsTask ExistsTask = null;
    private textChangedTask TextChangedTask = null;
    private underliningTask underliningTask = null;
    private correctionAndContinuationTask CorrectionAndContinuationTask = null;


    private Deque< ArrayList <Integer> > history = new ArrayDeque <>();
    private static final int MAX_HISTORY_EPISODES = 500;
    private int historyTimer = 0;

    private boolean justCreated = true;
    private boolean showSuggestions = true;
    private Menu myMenu = null;

    private static String CurrentRecordingLanguageLocale = Locale.ENGLISH.toString();
    private static String CurrentRecordingLanguageCode = "ENG";
    private static String CurrentRecordingLanguageName = "English";
    private static int CurrentRecordingLanguageID = -1;
    private static final int SPEECH_REQUEST_CODE = 0;


    /*******************************actions support functions**************************************/
    private void setLanguage( int languageID ) {

        MenuItem flag = myMenu.findItem( R.id.action_choose_language );
        CurrentRecordingLanguageCode = RecordingLanguage.getLanguageCodeFromLanguageID( languageID );
        CurrentRecordingLanguageLocale = RecordingLanguage.getLanguageLocaleFromLanguageID( languageID );
        CurrentRecordingLanguageName = RecordingLanguage.getLanguageNameFromLanguageID( languageID );
        int flagID = RecordingLanguage.getLanguageFlagFromLanguageID( languageID );
        flag.setIcon( flagID );

        if( !Language.supportsLanguage( CurrentRecordingLanguageCode ) ) {
            Toast.makeText( EditCorrectText.this, CurrentRecordingLanguageName + " is not fully supported!\nThe program may make wrong corrections.", Toast.LENGTH_LONG ).show();
        }
    }

    private void saveNoteToDatabase() {

        String contentValue = content.getText().toString();
        String titleValue = title.getText().toString();

        if (contentValue.matches("") && titleValue.matches(""))     return;
        if (titleValue.matches(""))                                 titleValue = "Untitled";

        DateFormat dateFormat = new SimpleDateFormat( "MMM dd\nHH:mm", Locale.US );
        String creationDate = dateFormat.format( new Date() );

        TextDatabase db = new TextDatabase(getApplicationContext());
        if( documentID == -1 )  { db.insert( titleValue, contentValue, creationDate );    }
        else                    { db.update( titleValue, contentValue, documentID ); }

        //Log.d("Note", "Was saved to database" );
    }

    private void copyTextToClipboard() {

        ClipboardManager clipboard = (ClipboardManager) getSystemService( CLIPBOARD_SERVICE );
        ClipData clip = ClipData.newPlainText( "ProCorrector", text );
        clipboard.setPrimaryClip( clip );
        Toast.makeText( EditCorrectText.this, "Text copied to clipboard", Toast.LENGTH_LONG ).show();
    }

    ////////////////////////////////show or hide////////////////////////////////////////////////////
    private void showOrHideSuggestions( MenuItem item ) {

        showSuggestions = !showSuggestions;
        if( CorrectionAndContinuationTask != null )
            CorrectionAndContinuationTask.cancel( true );

        suggestWord();
        if( showSuggestions )   item.setIcon( R.mipmap.show_suggestions_on_icon );
        else                    item.setIcon( R.mipmap.show_suggestions_off_icon );
    }
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

    private void ignoreOnce() {

        String word = Language.getCurrentSubstring(text, content.getSelectionStart());

        DatabaseHelper.wrongWords.remove( DatabaseHelper.convertToSQLiteFormat(word) );
        DatabaseHelper.correctWords.add( DatabaseHelper.convertToSQLiteFormat(word) );
        underliningTask = new underliningTask();
        underliningTask.execute(0, content.getText().length());
        Toast.makeText( EditCorrectText.this, word + " will be ignored in this document", Toast.LENGTH_SHORT ).show();
    }
    private void addToLibrary() {

        String word = Language.getCurrentSubstring(text, content.getSelectionStart());
        addToLibraryTask task = new addToLibraryTask();
        task.execute(word);
        Toast.makeText(EditCorrectText.this, word + " added to dictionary", Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////record//////////////////////////////////////////////////////////
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
            replaceFragment( spokenText, l, r );
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //////////////////////////////////send message//////////////////////////////////////////////////
    private void sendNote() {

        String messageBody = content.getText().toString();
        String messageTitle = title.getText().toString();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType( "text/plain" );
        i.putExtra(Intent.EXTRA_SUBJECT, messageTitle);
        i.putExtra(Intent.EXTRA_TEXT, messageBody);
        try {
            startActivity(Intent.createChooser( i, "Send Message...") );
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( EditCorrectText.this, "There are no messaging applications installed", Toast.LENGTH_SHORT ).show();
        }
    }
    private void writeFeedback() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "ProCorrector Feedback" );
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"XPNProCorrector@gmail.com"});
        try {
            startActivity(Intent.createChooser(i, "Choose an Email client :"));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( EditCorrectText.this, "There are no Email applications installed", Toast.LENGTH_SHORT ).show();
        }
    }
    /*******************************actions support functions**************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_correct_text);
        Bundle bundle = getIntent().getExtras();

        content = (EditText) findViewById( R.id.text );
        title = (EditText) findViewById( R.id.title );

        /// use toolbar as actionbar
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNoteToDatabase();
                finish();
            }
        });

        new Language();
        new RecordingLanguage( EditCorrectText.this );
        if( CurrentRecordingLanguageID == -1 )
            CurrentRecordingLanguageID = RecordingLanguage.getLanguageIDFromLanguageCode( CurrentRecordingLanguageCode );


        documentID = bundle.getInt( "id" );
        String previous_title = bundle.getString( "title" );
        String previous_content = bundle.getString( "content" );
        if( previous_title != null && previous_title.matches( "Untitled" ) )
            previous_title = "";


        database = DatabaseHelper.getInstance(this);
        try                     { database.createDataBase(); }
        catch (IOException e)   { e.printStackTrace(); }


        /*************************** Listeners ****************************************************/
        content.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (CorrectionAndContinuationTask != null)
                    CorrectionAndContinuationTask.cancel(true);
                suggestWord();
                showOrHideIgnoreAddIcons();
                //Log.d("EditCorrectText", "content is clicked");
            }
        });
        content.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Log.d("text changed", s + " START=" + start + " B=" + before + " C=" + count);
                text = s;

                if( before == count ) {
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
        content.setText(previous_content);
        title.setText(previous_title);


        ListView myCorrectionList = (ListView) findViewById( R.id.correction_list );
        myCorrectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                replaceWord(correctionList.get(position));
            }
        });

        ListView myContinuationList = (ListView) findViewById( R.id.continuation_list );
        myContinuationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                replaceWord(continuationList.get(position));
            }
        });


        final ScrollView scroll = (ScrollView) findViewById( R.id.scroll );
        scroll.setSmoothScrollingEnabled(true);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            int prevY = 0;

            @Override
            public void onScrollChanged() {

                int scrollY = scroll.getScrollY();
                if (Math.abs(prevY - scrollY) > 30) {
                    setSuggestionsInvisible();
                    prevY = scrollY;
                }
            }
        });

        Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                saveNoteToDatabase();
                Thread.dumpStack();
                System.exit( 2 );
            }
        });
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
        else if( RecordingLanguage.isLanguageID(id) )   { setLanguage( id ); }
        //else if( documentID == R.documentID.action_settings )       { openSettings(); } //TODO
        else if( id == R.id.action_help )               { Intent i = new Intent( EditCorrectText.this, Help.class );    startActivity(i); }
        else if( id == R.id.action_feedback )           { writeFeedback(); }
        else if( id == R.id.action_about )              { Intent i = new Intent(EditCorrectText.this, About.class);     startActivity(i); }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu( Menu menu ) {
        // Menu icons are inflated just as they were with actionbar
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_edit_correct_text, menu );
        myMenu = menu;
        if( CurrentRecordingLanguageID == -1 )
            CurrentRecordingLanguageID = R.id.action_language_ENG;
        setLanguage( CurrentRecordingLanguageID );

        return true;
    }


    private void setSuggestionsUnderCursor() {

        if( !showSuggestions )
            return;

        //Log.d( "EditCorrectText", "setSuggestionsUnderCursor" );
        Layout content_layout = content.getLayout();
        ScrollView scroll = (ScrollView) findViewById( R.id.scroll );
        LinearLayout suggestions = (LinearLayout) findViewById(R.id.all_suggestions);
        ViewGroup.LayoutParams suggestions_layout = suggestions.getLayoutParams();

        int pos = content.getSelectionStart();
        int line = content_layout.getLineForOffset(pos);
        float x = content_layout.getPrimaryHorizontal(pos);
        float h = scroll.getScrollY();
        float y = content_layout.getLineBottom(line);

        y -= h;
        y += content.getPaddingTop();
        x += content.getPaddingLeft();

        if( !correctionList.isEmpty() && !continuationList.isEmpty() ) {
            x -= suggestions_layout.width / 2;
            x = Math.min( x, scroll.getWidth() - suggestions_layout.width );
            x = Math.max(x, 0);
            setSuggestionsVisible();
        }
        else if( !correctionList.isEmpty() ) {
            x -= suggestions_layout.width / 4;
            x = Math.min( x, scroll.getWidth() - suggestions_layout.width / 2 );
            x = Math.max(x, 0);
            setCorrectionListVisible();
        }
        else if( !continuationList.isEmpty() ) {
            x -= 3*suggestions_layout.width / 4;
            x = Math.min( x, scroll.getWidth() - suggestions_layout.width );
            x = Math.max( x, -suggestions_layout.width / 2 );
            setContinuationListVisible();
        }

        if( suggestions.getX() != x || suggestions.getY() != y ) {
            suggestions.setX(x);
            suggestions.setY(y);
        }
    }


    private void setContinuationListVisible() {

        LinearLayout myContinuationBox = (LinearLayout) findViewById(R.id.continuation_box);
        myContinuationBox.setVisibility(View.VISIBLE);
    }
    private void setCorrectionListVisible() {

        LinearLayout myCorrectionBox = (LinearLayout) findViewById(R.id.correction_box);
        myCorrectionBox.setVisibility(View.VISIBLE);
    }
    private void setSuggestionsVisible() {

        setCorrectionListVisible();
        setContinuationListVisible();
    }
    private void setSuggestionsInvisible() {

        LinearLayout myCorrectionBox = (LinearLayout) findViewById(R.id.correction_box);
        LinearLayout myContinuationBox = (LinearLayout) findViewById(R.id.continuation_box);
        myCorrectionBox.setVisibility(View.INVISIBLE);
        myContinuationBox.setVisibility(View.INVISIBLE);
    }
    private void setSuggestionsInvisibleAndClearLists() {

        if( CorrectionAndContinuationTask != null )
            CorrectionAndContinuationTask.cancel( true );

        setSuggestionsInvisible();
        correctionList.clear();
        continuationList.clear();
        displayedSuggestions = "";
    }

    public void suggestWord() {                                  /// done in main thread

        if( !showSuggestions ) {
            //Log.d( "EditCorrectText", "we don't need suggestions" );
            setSuggestionsInvisibleAndClearLists();
            return;
        }

        String needed = Language.getCurrentSubstring(text, content.getSelectionStart());
        if( needed.length() >= 2 ) {

            if( needed.length() == displayedSuggestions.length() && needed.matches( displayedSuggestions.toString() ) ) {
                //Log.d( "suggestWord", "the same word" );
                setSuggestionsUnderCursor();
                return;
            }

            //Log.d( "suggestWord", "we have a new word--> previous was: " + displayedSuggestions + "\tnow: " + needed );
            setSuggestionsInvisibleAndClearLists();
            displayedSuggestions = needed;

            CorrectionAndContinuationTask = new correctionAndContinuationTask();
            CorrectionAndContinuationTask.execute( needed );
        }
        else
            setSuggestionsInvisibleAndClearLists();
    }

    public void underlineWrongWords( int start, int count ) {   /// done in background and is called by underliningTask

        CharSequence s = text;
        if( s.length() == 0 )
            return;

        int end = start + count;
        start -= 3;
        end   += 3;
        if( start < 0 )             start = 0;
        if( end < 0 )               end = 0;
        if( start > s.length()-1 )  start = s.length()-1;
        if( end > s.length()-1 )    end = s.length()-1;


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

    public void replaceFragment( String newString, int l, int r ) {

        SpannableStringBuilder res = new SpannableStringBuilder( content.getText());
        res.delete(l, r);
        res.insert(l, newString);
        content.setText(res);
        content.setSelection(l + newString.length());

        history.clear();
        historyTimer = 0;
    }

    public void replaceWord( String newWord )  {                /// done in main thread!

        setSuggestionsInvisibleAndClearLists();
        int position = content.getSelectionStart();
        int l = position;
        int r = position;

        /// check if we are not at the end of a word
        if( position >= text.length() || !Language.isCorrectLetter( text.charAt( position ) ) )
            r--;

        String currentLanguage = Language.getLanguageCode( text.charAt( r ) );
        while( r+1 < text.length() && Language.isCorrectLetter( text.charAt( r+1 ), currentLanguage ) ) r++;
        while( l-1 >= 0 && Language.isCorrectLetter( text.charAt( l-1 ), currentLanguage ) )            l--;

        replaceFragment( newWord, l, Math.min(r+1, text.length()) );
        setSuggestionsInvisibleAndClearLists();
    }

    private void showCorrectionList() {

        if( correctionList.isEmpty() )
            return;

        LinearLayout myCorrectionBox = (LinearLayout) findViewById(R.id.correction_box);
        myCorrectionBox.setVisibility(View.VISIBLE);

        ListView correctionList = (ListView) findViewById(R.id.correction_list);
        correctionList.setAdapter(new MyCorrectionListAdapter());
    }
    private void showContinuationList() {

        if( continuationList.isEmpty() )
            return;

        LinearLayout myContinuationBox = (LinearLayout) findViewById(R.id.continuation_box);
        myContinuationBox.setVisibility(View.VISIBLE);

        ListView continuationList = (ListView) findViewById(R.id.continuation_list);
        continuationList.setAdapter(new MyContinuationListAdapter());
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

            int start = Integer.parseInt( params[0].toString() );
            int count = Integer.parseInt( params[1].toString() );
            underlineWrongWords(start, count);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
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
    class MyCorrectionListAdapter extends BaseAdapter {
        public int getCount()               { return correctionList.size(); }
        public Object getItem(int position) { return null; }
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            String row = correctionList.get(position);

            LayoutInflater in = getLayoutInflater();
            View res = in.inflate(R.layout.suggestion_list_item, null);

            TextView suggestion = ( TextView ) res.findViewById( R.id.suggestion );
            suggestion.setText(row);

            return res;
        }
    }
    class MyContinuationListAdapter extends BaseAdapter {
        public int getCount()               { return continuationList.size(); }
        public Object getItem(int position) { return null; }
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            String row = continuationList.get(position);

            LayoutInflater in = getLayoutInflater();
            View res = in.inflate(R.layout.suggestion_list_item, null);

            TextView suggestion = ( TextView ) res.findViewById( R.id.suggestion );
            suggestion.setText(row);

            return res;
        }
    }
/**********************************Adapters********************************************************/
}