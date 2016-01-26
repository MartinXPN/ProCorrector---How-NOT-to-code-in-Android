package com.ProCorrector.XPN.procorrector;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Main extends AppCompatActivity {

    public static List <ArrayList> list = new ArrayList<>();
    TextDatabase myNotesDB = new TextDatabase( this );
    private final int EDIT_CORRECT_TEXT_ACTIVITY_CODE = 1;
    private final String CACHE = "data";
    MyAdapter adapter = null;


    /*******************************actions support functions**************************************/

    private void copyTextToClipboard( String text ) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService( CLIPBOARD_SERVICE );
        ClipData clip = ClipData.newPlainText("Spell Checker", text);
        clipboard.setPrimaryClip( clip );
        Toast.makeText( Main.this, "Text copied to clipboard", Toast.LENGTH_SHORT ).show();
    }
    private void sendNote( String messageTitle, String messageBody ) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, messageTitle);
        i.putExtra(Intent.EXTRA_TEXT, messageBody);
        try {
            startActivity(Intent.createChooser( i, "Send Message...") );
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( Main.this, "There are no messaging applications installed", Toast.LENGTH_SHORT ).show();
        }
    }

    private void writeFeedback() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "Spell Checker Feedback");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"XPNInc@gmail.com"});
        try {
            startActivity(Intent.createChooser(i, "Choose an Email client:"));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Main.this, "There are no Email applications installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWelcomePage(){

        final Dialog dialog = new Dialog(Main.this, R.style.WelcomeTheme);
        dialog.setContentView(R.layout.welcome_page);
        final RelativeLayout layout = (RelativeLayout) dialog.findViewById(R.id.welcome_page);
        Animation animation = AnimationUtils.loadAnimation( Main.this, R.anim.fade_in );
        dialog.show();
        layout.startAnimation(animation);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                SharedPreferences sp = getSharedPreferences(CACHE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putBoolean("firstLaunch", false);
                editor.apply();

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                layout.startAnimation(animation);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        Handler my = new Handler();
                        my.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showHelpAndTips();
                            }
                        }, 400);
                    }
                }, 250);
            }
        });
    }

    private void showHelpAndTips() {

        final LinearLayout background = (LinearLayout) findViewById( R.id.semitransparent_background );
        final ImageView arrow = (ImageView) findViewById( R.id.help_show_suggestions_arrow );
        final TextView hint = (TextView) findViewById( R.id.help_create_new_document_hint );
        final SharedPreferences sp = getSharedPreferences( CACHE, MODE_PRIVATE );
        final SharedPreferences.Editor editor = sp.edit();
        editor.remove( "newDocumentHintShown" );
        editor.apply();


        Animation animation = AnimationUtils.loadAnimation( Main.this, R.anim.fade_in );
        background.setVisibility( View.VISIBLE );
        arrow.setVisibility( View.VISIBLE );
        hint.setVisibility( View.VISIBLE );

        background.startAnimation(animation);
        arrow.startAnimation(animation);
        hint.startAnimation(animation);

        android.support.design.widget.FloatingActionButton createNewNote = ( android.support.design.widget.FloatingActionButton ) findViewById(R.id.new_text_button);
        createNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( sp.contains( "newDocumentHintShown" ) ) {
                    Intent i = new Intent(Main.this, EditCorrectText.class);
                    i.putExtra("id", -1);
                    i.putExtra("title", "");
                    i.putExtra("content", "");
                    startActivityForResult(i, EDIT_CORRECT_TEXT_ACTIVITY_CODE);
                    return;
                }

                editor.putBoolean( "newDocumentHintShown", true );
                editor.apply();

                Animation animation = AnimationUtils.loadAnimation( Main.this, R.anim.fade_out );
                background.startAnimation(animation);
                arrow.startAnimation(animation);
                hint.startAnimation(animation);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        background.setVisibility( View.GONE );
                        arrow.setVisibility( View.GONE );
                        hint.setVisibility( View.GONE );

                        Intent i = new Intent(Main.this, EditCorrectText.class);
                        i.putExtra("id", -1);
                        i.putExtra("title", "");
                        i.putExtra("content", "");
                        startActivityForResult(i, EDIT_CORRECT_TEXT_ACTIVITY_CODE);
                    }
                }, 250 );
            }
        });
    }
    /*******************************actions support functions**************************************/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( requestCode == EDIT_CORRECT_TEXT_ACTIVITY_CODE )
        {
            list = myNotesDB.getAll();
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /// use toolbar as actionbar
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Spell Checker</font>"));
        setSupportActionBar(toolbar);

        android.support.design.widget.FloatingActionButton createNewNote = ( android.support.design.widget.FloatingActionButton ) findViewById(R.id.new_text_button);
        createNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Main.this, EditCorrectText.class);
                i.putExtra("id", -1);
                i.putExtra("title", "");
                i.putExtra("content", "");
                startActivityForResult(i, EDIT_CORRECT_TEXT_ACTIVITY_CODE);
            }
        });


        list = myNotesDB.getAll();
        final ListView myPreviousNotes = ( ListView ) findViewById(R.id.list );
        adapter = new MyAdapter();
        myPreviousNotes.setAdapter(adapter);


        myPreviousNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList row = list.get(position);

                Intent i = new Intent(Main.this, EditCorrectText.class);
                i.putExtra("id", (Integer) row.get(0));
                i.putExtra("title", (String) row.get(1));
                i.putExtra("content", (String) row.get(2));
                startActivityForResult(i, EDIT_CORRECT_TEXT_ACTIVITY_CODE);
            }
        });


        SharedPreferences sp = getSharedPreferences(CACHE, Context.MODE_PRIVATE);
        if( !sp.contains( "firstLaunch" ) ) {
            showWelcomePage();
        }
        else if( !sp.contains("newDocumentHintShown" ) ) {
            showHelpAndTips();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu icons are inflated just as they were with actionbar
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //if( id == R.id.action_settings )        { openSettings(); } //TODO
        if( id == R.id.action_help )            { showHelpAndTips(); }
        else if( id == R.id.action_feedback )   { writeFeedback(); }
        else if( id == R.id.action_about )      { Intent i = new Intent(Main.this, About.class);    startActivity(i); }

        return super.onOptionsItemSelected(item);
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public void notifyDataSetChanged()  { super.notifyDataSetChanged(); }
        public int getCount()               { return list.size(); }
        public Object getItem(int position) { return null; }
        public long getItemId(int position) { return position; }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ArrayList row = list.get(position);
            final String title_value = (String) row.get(1);
            final String text_value = (String) row.get(2);
            final String creationDate = (String) row.get(3);

            final LayoutInflater in = getLayoutInflater();
            final View res = in.inflate(R.layout.main_list_item, null);

            TextView title = ( TextView ) res.findViewById( R.id.title );   title.setText( title_value );
            TextView text = ( TextView ) res.findViewById( R.id.text );     text.setText(text_value);
            TextView date = (TextView ) res.findViewById( R.id.date );      date.setText(creationDate);
            ImageView copy = (ImageView) res.findViewById( R.id.copy );
            ImageView trash = (ImageView) res.findViewById( R.id.trash );
            ImageView send = (ImageView) res.findViewById( R.id.send );

            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyTextToClipboard( text_value );
                }
            });
            trash.setOnClickListener(new View.OnClickListener() {

                int currentPosition = position;
                @Override
                public void onClick(View v) {
                    TextDatabase db = new TextDatabase(Main.this);
                    String title = (String) list.get(currentPosition).get( 1 );
                    db.delete((int) list.get(currentPosition).get(0));
                    list.remove(currentPosition);
                    adapter.notifyDataSetChanged();
                    Toast.makeText( Main.this, title + " removed", Toast.LENGTH_SHORT ).show();
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendNote( title_value, text_value );
                }
            });

            return res;
        }
    }
}