package com.ProCorrector.XPN.procorrector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Main extends AppCompatActivity {

    public static List <ArrayList> list = new ArrayList<>();
    TextDatabase myNotesDB = new TextDatabase( this );
    private final int EDIT_CORRECT_TEXT_ACTIVITY_CODE = 1;
    MyAdapter adapter = null;


    /*******************************actions support functions**************************************/

    //////////////////////////////////send feedback/////////////////////////////////////////////////
    private void writeFeedback() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "ProCorrector Feedback" );
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"XPNProCorrector@gmail.com"} );
        try {
            startActivity(Intent.createChooser(i, "Choose an Email client:"));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Main.this, "There are no Email applications installed", Toast.LENGTH_SHORT).show();
        }
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
        if( id == R.id.action_help )            { Intent i = new Intent(Main.this, Help.class);     startActivity(i); }
        else if( id == R.id.action_feedback )   { writeFeedback(); }
        else if( id == R.id.action_about )      { Intent i = new Intent(Main.this, About.class);    startActivity(i); }

        return super.onOptionsItemSelected(item);
    }


    class MyAdapter extends BaseAdapter
    {
        @Override
        public void notifyDataSetChanged()  { super.notifyDataSetChanged(); }
        public int getCount()               { return list.size(); }
        public Object getItem(int position) { return null; }
        public long getItemId(int position) { return position; }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ArrayList row = list.get(position);
            String title_value = (String) row.get(1);
            String text_value = (String) row.get(2);
            String creationDate = (String) row.get(3);

            final LayoutInflater in = getLayoutInflater();
            final View res = in.inflate(R.layout.main_list_item, null);

            TextView title = ( TextView ) res.findViewById( R.id.title );   title.setText( title_value );
            TextView text = ( TextView ) res.findViewById( R.id.text );     text.setText(text_value);
            TextView date = (TextView ) res.findViewById( R.id.date );      date.setText(creationDate);
            ImageView trash = (ImageView) res.findViewById( R.id.trash );

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

            return res;
        }
    }
}