package com.example.user.procorrector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class DatabaseHelper extends SQLiteOpenHelper
{
    private static String DB_PATH = "";
    private static String DB_NAME ="mydb.db";
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    private static final int MAX_WORDS_SUGGESTIONS = 15;
    public static HashSet <String> correctWords = new HashSet<>();
    public static HashSet <String> wrongWords = new HashSet<>();
    /********************************** MY FUNCTIONS **********************************************/

    public static boolean isKnownWord( String word ) {

        word = convertToSQLiteFormat( word );
        return correctWords.contains( word ) || wrongWords.contains( word );
    }
    public boolean isCorrectWord( String word ) {

        if( !Language.isUniqueLanguage( word ) )
            return false;

        word = convertToSQLiteFormat( word );
        if( correctWords.contains(word) )   return true;
        if( wrongWords.contains(word) )     return false;

        String language = Language.getLanguageCode( word.charAt( 0 ) );
        Cursor c = getReadableDatabase().rawQuery("SELECT word FROM " + language + " WHERE word = '" + word + "';", null);
        boolean exists =  c.moveToFirst();
        c.close();

        if( exists )    correctWords.add( word );
        else            wrongWords.add( word );

        //Log.d(word, "isCorrect = " + String.valueOf(exists));
        return exists;
    }


    /********************************* get suggestion functions ***********************************/

    public ArrayList <String> getMoreCorrectionOptions( String word ) {

        if( !Language.isUniqueLanguage( word ) )    return new ArrayList<>();
        return getValidWords( Language.editDistance2(word), Language.getLanguageCode( word.charAt( 0 ) ), MAX_WORDS_SUGGESTIONS );
    }

    /**
     * Function helps to get two suggestions at once to optimize queries in SQLite
     * @param word          the word for which we search the correction and continuation suggestions
     * @param correction    this parameter is passed to function to get filled after the function has finished its work
     * @param continuation  this parameter is passed to function to get filled after the function has finished its work
     */
    public void getCorrectionAndContinuation( String word, ArrayList <String> correction, ArrayList <String> continuation ) {

        /// clear lists in case of other thread may have filled them
        correction.clear();
        continuation.clear();

        /// word may contain some characters from other languages which is impossible for a real word
        /// therefore the query LIKE % or editDistance will be senseless
        if( !Language.isUniqueLanguage( word ) )
            return;

        String language = Language.getLanguageCode( word.charAt( 0 ) );
        ArrayList <String> possible = Language.editDistance( word );
        String query =  "SELECT word FROM " + language +
                        " WHERE word LIKE '" + convertToSQLiteFormat( word ) + "%'" +
                        " OR word IN( " + convertToSQLiteFormat( possible ) + " )" +
                        " ORDER BY usage DESC;";

        /// long words contain lots of corrections and few continuations
        /// therefore it's reasonable to store few elements in HashSet to save memory
        boolean wordIsLong = word.length() > 4;
        HashSet <String> h = new HashSet<>();
        SQLiteDatabase db = super.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        /// if the word is not long => we can keep all the correction options in HashSet
        /// otherwise we will compare the prefix of every string
        if( !wordIsLong )
            h.addAll( possible );


        /// all words returned by the query are either going to be in correction or continuation list
        if( c.moveToFirst() ) {
            do
            {
                String now = c.getString( 0 );

                if( wordIsLong ) {

                    boolean isFromCorrection = true;
                    if( now.length() > word.length() ) {    /// few words pass this if statement

                        boolean isPrefix = true;
                        for( int j=0; j < word.length(); j++ )      /// don't get substring because it takes more time
                            if( word.charAt(j) != now.charAt(j) ) { /// words that are to be in correctionList will have some difference
                                isPrefix = false;
                                break;
                            }

                        if( isPrefix )
                            isFromCorrection = false;
                    }

                    if( isFromCorrection )  { if( correction.size() < MAX_WORDS_SUGGESTIONS )       correction.add( now ); }
                    else                    { if( continuation.size() < MAX_WORDS_SUGGESTIONS )     continuation.add(now); }
                }
                else {
                    /// if HashSet contains the word than the word was the candidate for adding to correction list
                    /// otherwise the word was the candidate for continuation list
                    if( h.contains( now ) ) { if( correction.size() < MAX_WORDS_SUGGESTIONS )       correction.add( now ); }
                    else                    { if( continuation.size() < MAX_WORDS_SUGGESTIONS )     continuation.add( now ); }
                }

            } while( c.moveToNext() && ( correction.size() < MAX_WORDS_SUGGESTIONS || continuation.size() < MAX_WORDS_SUGGESTIONS ) );
        }
        c.close();
    }

    /********************************* get suggestion functions ***********************************/

    public static String convertToSQLiteFormat( String word ) {

        if( Language.contains( word, '\'' ) ) {

            char[] arr = word.toLowerCase().toCharArray();
            StringBuilder res = new StringBuilder();
            for (char c : arr) {
                res.append(c);
                if (c == '\'')
                    res.append(c);
            }
            return res.toString();
        }

        return word.toLowerCase();
    }
    public static String convertToSQLiteFormat( ArrayList <String> words ) {

        StringBuilder query = new StringBuilder();
        for( int i=0; i < words.size()-1; i++ )
            query.append( '\'' ).append(convertToSQLiteFormat( words.get(i)) ).append( "', " );
        query.append( '\'' ).append(convertToSQLiteFormat( words.get(words.size() - 1)) ).append("'");

        return query.toString();
    }


    private ArrayList <String> getValidWords( ArrayList <String> words, String language, int maxNumberOfWords ) {

        SQLiteDatabase db = super.getReadableDatabase();
        String query = "SELECT word FROM " + language + " WHERE word IN( " + convertToSQLiteFormat(words) + " ) ORDER BY usage DESC;";

        ArrayList <String> ans = new ArrayList<>();
        HashSet <String> validWords = new HashSet<>();
        Cursor c = db.rawQuery(query, null);
        if( c.moveToFirst() ) {
            do {
                String now = c.getString( 0 );
                validWords.add( now );
                if( ans.size() < maxNumberOfWords )
                    ans.add( now );

            } while( c.moveToNext() );
        }
        c.close();

        for( int i=0; i < words.size(); i++ )
            if( validWords.contains( words.get(i).toLowerCase() ) ) correctWords.add( convertToSQLiteFormat( words.get(i) ) );
            else                                                    wrongWords.add( convertToSQLiteFormat( words.get(i) ) );

        return ans;
    }

    private void checkCorrectnessOfWords( ArrayList <String> words, String language ) {

        SQLiteDatabase db = super.getReadableDatabase();
        String query = "SELECT word FROM " + language + " WHERE word IN( " + convertToSQLiteFormat( words ) + " );";

        HashSet <String> validWords = new HashSet<>();
        Cursor c = db.rawQuery( query, null );
        if( c.moveToFirst() ) {
            do {
                validWords.add( c.getString( 0 ) );
            } while( c.moveToNext() );
        }
        c.close();

        for( int i=0; i < words.size(); i++ )
            if( validWords.contains( words.get(i).toLowerCase() ) ) correctWords.add( convertToSQLiteFormat( words.get(i) ) );
            else                                                    wrongWords.add( convertToSQLiteFormat( words.get(i) ) );
    }
    public void checkCorrectnessOfParagraph( String text ) {

        HashMap <String, ArrayList<String> > wordsWithLanguage = Language.divideIntoWordsUsingUnknownCharacters( text, true );
        Set <String> languageCodes = wordsWithLanguage.keySet();

        for( String languageCode : languageCodes ) {

            ArrayList <String> unknownWords = wordsWithLanguage.get( languageCode );
            if( unknownWords.size() > 2 )
                checkCorrectnessOfWords( unknownWords, languageCode );
        }
    }
    /********************************** MY FUNCTIONS **********************************************/


    /******************* DATABASE FUNCTIONS ***************************************/
    public boolean insert( String word )
    {
        if( !Language.isUniqueLanguage( word ) )
            return false;

        String language = Language.getLanguageCode( word.charAt( 0 ) );
        SQLiteDatabase db = super.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( "word", convertToSQLiteFormat(word) );
        values.put( "usage", 100 );

        db.insert(language, null, values);
        db.close();
        return true;
    }

    public boolean update( String word, int usage, int id )
    {
        if( !Language.isUniqueLanguage( word ) )
            return false;

        String language = Language.getLanguageCode( word.charAt(0) );
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( "word", convertToSQLiteFormat(word) );
        values.put( "usage", usage );

        db.update(language, values, "id=" + id, null);
        db.close();
        return true;
    }

    public boolean delete( String word )
    {
        if( !Language.isUniqueLanguage( word ) )
            return false;

        String language = Language.getLanguageCode( word.charAt(0) );
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + language + " WHERE word = " + convertToSQLiteFormat(word) + ";";
        db.execSQL(query);
        db.close();
        return true;
    }
    /******************* DATABASE FUNCTIONS ***************************************/



    private static DatabaseHelper selfReference  = null;
    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1); // 1? its Database Version
        if (android.os.Build.VERSION.SDK_INT >= 17) DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else                                        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;
    }

    public static DatabaseHelper getInstance(Context context){
        if (selfReference == null) {
            selfReference = new DatabaseHelper(context);
        }
        return selfReference;
    }
    public void createDataBase() throws IOException
    {
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist)
        {
            this.getReadableDatabase();
            this.close();

            try {
                copyDataBase();
            }
            catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    public boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException
    {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException
    {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close()
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}