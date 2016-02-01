package com.ProCorrector.XPN.procorrector;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Class shows the information about app and links to the project and the author
 */
public class About extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        /// use toolbar as actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        TextView about_author = (TextView) findViewById( R.id.about_author );
        about_author.setMovementMethod(LinkMovementMethod.getInstance());

        TextView about_app = (TextView) findViewById( R.id.about_app );
        about_app.setMovementMethod( LinkMovementMethod.getInstance() );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        int id = item.getItemId();

        if (id==android.R.id.home)      { finish(); }

        return super.onOptionsItemSelected( item );
    }
}