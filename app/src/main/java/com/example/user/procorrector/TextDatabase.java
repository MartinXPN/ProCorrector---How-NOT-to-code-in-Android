package com.example.user.procorrector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class TextDatabase extends SQLiteOpenHelper
{
    private static final String TABLE_NAME = "data";
    private static final String DB_NAME = "mytexts";
    private static final String TITLE_COLUMN = "title";
    private static final String TEXT_COLUMN = "content";
    private static final String DATE_COLUMN = "date";


    public TextDatabase( Context context ) {
        super( context, DB_NAME, null, 1 );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE_COLUMN + " TEXT, " + TEXT_COLUMN + " TEXT, " + DATE_COLUMN + " TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void insert( String title, String content, String date )
    {
        SQLiteDatabase db = super.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( TITLE_COLUMN, title );
        values.put( TEXT_COLUMN, content );
        values.put( DATE_COLUMN, date );

        db.insert( TABLE_NAME, null, values );
        db.close();
    }

    public void update( String title, String content, int id )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( TITLE_COLUMN, title );
        values.put( TEXT_COLUMN, content );
        //values.put( DATE_COLUMN, date );

        db.update(TABLE_NAME, values, "id=" + id, null);
        db.close();
    }

    public void delete( int id )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE ID = " + id + ";";
        db.execSQL( query );
        db.close();
    }

    public List <ArrayList> getAll()
    {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List <ArrayList> res = new ArrayList<>();

        if (cursor.moveToFirst())
            do
            {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String text = cursor.getString(2);
                String date = cursor.getString(3);

                ArrayList <Object> one = new ArrayList<>();
                one.add( id );
                one.add( title );
                one.add( text );
                one.add( date );
                res.add( one );
            } while( cursor.moveToNext() );

        cursor.close();
        db.close();
        return res;
    }
}