package com.ProCorrector.XPN.procorrector;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class About extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        /// use toolbar as actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Html.fromHtml("<font color='#ffffff'>ProCorrector </font>"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /// if the device's API level is higher than LOLLIPOP then set the status-bar color to primary_dark
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Window window = About.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(About.this.getResources().getColor(R.color.view_documents_primary_dark));
        }

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