package com.example.user.procorrector;


import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Language {

    /**
     * the order of languages in languageCodes has to be the same as the order in alphabet
     * */
    private static final String[] languageCodes = { "ARM", "ENG", "RUS" };
    private static final char[][] alphabet = {  /*ARM*/ { 'ա', 'բ', 'գ', 'դ', 'ե', 'զ', 'է', 'ը', 'թ', 'ժ', 'ի', 'լ', 'խ', 'ծ', 'կ', 'հ', 'ձ', 'ղ', 'ճ', 'մ', 'յ', 'ն', 'շ', 'ո', 'չ', 'պ', 'ջ', 'ռ', 'ս', 'վ', 'տ', 'ր', 'ց', 'ւ', 'փ', 'ք', 'և', 'օ', 'ֆ' },
                                                /*ENG*/ { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\'' },
                                                /*RUS*/ { 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я' } };

    private static HashSet <Character> allAlphabets = new HashSet<>();
    private static HashMap <String, HashSet <Character> > languageCodeToLetters = new HashMap<>();
    private static HashMap <Character, String> lettersToLanguageCode = new HashMap<>();
    private static HashMap <Integer, String> recordingLangIdToLanguageCode = new HashMap<>();
    private static HashMap <Integer, String> recordingLangIdToLanguageName = new HashMap<>();
    private static HashMap <Integer, Integer> recordingLangIdToLanguageFlag = new HashMap<>();
    private static HashMap <Integer, String> recordingLangIdToLanguageLocale = new HashMap<>();
    private static final String defaultLanguage = "ENG";
    private static Context context;


    public Language( Context currentContext ) {

        context = currentContext;
        int ID;
        Resources res = context.getResources();

        ID = R.id.action_language_ENG;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_ENG ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_ENG ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_ENG_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_eng_icon );
        ID = R.id.action_language_BUL;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_BUL ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_BUL ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_BUL_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_bul_icon );
        ID = R.id.action_language_CZE;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_CZE ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_CZE ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_CZE_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_cze_icon );
        ID = R.id.action_language_DAN;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_DAN ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_DAN ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_DAN_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_dan_icon );
        ID = R.id.action_language_DUT;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_DUT ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_DUT ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_DUT_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_dut_icon );
        ID = R.id.action_language_FIL;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_FIL ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_FIL ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_FIL_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_fil_icon );
        ID = R.id.action_language_FIN;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_FIN ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_FIN ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_FIN_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_fin_icon );
        ID = R.id.action_language_FRA;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_FRA ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_FRA ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_FRA_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_fra_icon );
        ID = R.id.action_language_GER;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_GER ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_GER ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_GER_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_ger_icon );
        ID = R.id.action_language_GRE;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_GRE ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_GRE ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_GRE_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_gre_icon );
        ID = R.id.action_language_HEB;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_HEB ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_HEB ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_HEB_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_heb_icon );
        ID = R.id.action_language_HUN;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_HUN ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_HUN ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_HUN_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_hun_icon );
        ID = R.id.action_language_ICE;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_ICE ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_ICE ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_ICE_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_ice_icon );
        ID = R.id.action_language_ITL;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_ITL ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_ITL ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_ITL_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_itl_icon );
        ID = R.id.action_language_IND;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_IND ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_IND ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_IND_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_ind_icon );
        ID = R.id.action_language_JAP;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_JAP ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_JAP ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_JAP_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_jap_icon );
        ID = R.id.action_language_KOR;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_KOR ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_KOR ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_KOR_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_kor_icon );
        ID = R.id.action_language_LIT;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_LIT ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_LIT ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_LIT_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_lit_icon );
        ID = R.id.action_language_MAL;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_MAL ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_MAL ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_MAL_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_mal_icon );
        ID = R.id.action_language_NOR;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_NOR ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_NOR ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_NOR_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_nor_icon );
        ID = R.id.action_language_POL;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_POL ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_POL ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_POL_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_pol_icon );
        ID = R.id.action_language_POR;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_POR ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_POR ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_POR_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_por_icon );
        ID = R.id.action_language_ROM;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_ROM ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_ROM ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_ROM_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_rom_icon );
        ID = R.id.action_language_RUS;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_RUS ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_RUS ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_RUS_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_rus_icon );
        ID = R.id.action_language_SER;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_SER ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_SER ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_SER_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_ser_icon );
        ID = R.id.action_language_SLO;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_SLO ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_SLO ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_SLO_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_slo_icon );
        ID = R.id.action_language_SPA;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_SPA ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_SPA ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_SPA_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_spa_icon );
        ID = R.id.action_language_SWE;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_SWE ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_SWE ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_SWE_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_swe_icon );
        ID = R.id.action_language_VIE;  recordingLangIdToLanguageCode.put( ID, res.getString( R.string.language_VIE ) );     recordingLangIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_VIE ) );     recordingLangIdToLanguageName.put( ID, res.getString( R.string.action_language_VIE_title ) );  recordingLangIdToLanguageFlag.put( ID, R.drawable.flag_vie_icon );

        for( String languageCode : languageCodes ) {
            languageCodeToLetters.put( languageCode, new HashSet<Character>() );
        }

        for( int i=0; i < languageCodes.length; i++ )
            for( int j=0; j < alphabet[i].length; j++ ) {

                allAlphabets.add( alphabet[i][j] );
                languageCodeToLetters.get( languageCodes[i]).add( alphabet[i][j] );
                lettersToLanguageCode.put( alphabet[i][j], languageCodes[i] );
            }
    }

    public static boolean supportsLanguage(String languageCode) {
        return languageCodeToLetters.containsKey( languageCode );
    }

    public static boolean isRecordingLanguageID( int id ) {
        return recordingLangIdToLanguageCode.containsKey(id);
    }
    public static String getLanguageCodeFromRecordingLangID( int languageID ) {
        return recordingLangIdToLanguageCode.get(languageID);
    }
    public static String getLanguageLocaleFromRecordingLangID( int languageID ) {
        return recordingLangIdToLanguageLocale.get( languageID );
    }
    public static String getLanguageNameFromRecordingLanguageID( int languageID ) {
        return recordingLangIdToLanguageName.get( languageID );
    }
    public static int getLanguageFlagFromRecordingLanguageID( int languageID ) {
        return recordingLangIdToLanguageFlag.get( languageID );
    }
    public static int getRecordingLanguageIDFromLanguageCode( String languageCode ) {

        for( Integer id : recordingLangIdToLanguageCode.keySet() ) {
            if( recordingLangIdToLanguageCode.get( id ).contains( languageCode ) )
                return id;
        }
        return 0;
    }


    public static HashSet <Character> getAlphabet( String languageCode ) {
        return languageCodeToLetters.get(languageCode);
    }
    public static String getLanguageCode( char letter ) {

        letter = Character.toLowerCase( letter );
        //Log.d( "Language getCode", lettersToLanguageCode.get( letter ) );
        return lettersToLanguageCode.get( letter );
    }
    public static String getLanguageCode( String word ) {

        if( !isUniqueLanguage( word ) )
            return defaultLanguage;
        return getLanguageCode(Character.toLowerCase(word.charAt(0)));
    }
    public static String getLanguageCodeWithoutCheckingValidity( String word ) {

        return getLanguageCode( word.charAt(0) );
    }


    public static boolean isCorrectLetter( char letter ) {

        letter = Character.toLowerCase( letter );
        //Log.d( "Language isCorrect", "" + allAlphabets.contains( letter ) );
        return allAlphabets.contains( letter );
    }
    public static boolean isCorrectLetter( char letter, String languageCode ) {

        letter = Character.toLowerCase( letter );

        if( languageCode == null || !languageCodeToLetters.containsKey( languageCode ) )
            return false;
        return languageCodeToLetters.get( languageCode ).contains(letter);
    }

    public static boolean isSameLanguage( char a, char b ) {

        a = Character.toLowerCase( a );
        b = Character.toLowerCase( b );
        String languageCode = getLanguageCode( a );

        return languageCode != null && languageCodeToLetters.get( languageCode ).contains( b );
    }
    public static boolean isUniqueLanguage( String s ) {

        if( s.isEmpty() )
            return true;

        String languageCode = getLanguageCode( s.charAt( 0 ) );
        if( languageCode == null )
            return false;

        s = s.toLowerCase();
        for( int i=0; i < s.length(); i++ ) {
            if( !languageCodeToLetters.get( languageCode ).contains( s.charAt( i ) ) )
                return false;
        }

        return true;
    }


    public static ArrayList<String> editDistance( String s, String languageCode ) {

        if( languageCode == null || !isUniqueLanguage( s ) )
            return new ArrayList<>();

        s = s.toLowerCase();
        StringBuilder now = new StringBuilder( s );
        StringBuilder tmp;

        ArrayList <String> res = new ArrayList<>();
        HashSet <Character> alphabet = getAlphabet(languageCode);
        for( int i=0; i < s.length(); i++ ) {
            for( char c : alphabet ) {

                tmp = new StringBuilder( now );
                res.add( tmp.insert( i, c ).toString() );

                if( s.charAt(i) != c ) {
                    tmp = new StringBuilder( now );
                    tmp.setCharAt( i, c );
                    res.add( tmp.toString() );
                }
            }

            tmp = new StringBuilder( now );
            res.add(tmp.deleteCharAt(i).toString());
        }

        Log.d( "Language editDistance", res.toString() );
        return res;
    }
    public static ArrayList <String> editDistance( String s ) {

        if( s.isEmpty() )
            return new ArrayList<>();
        return editDistance(s, getLanguageCode(s.charAt(0)));
    }
    public static ArrayList <String> editDistance2( String s ) {

        ArrayList <String> res = new ArrayList<>();
        ArrayList <String> first = editDistance( s );

        for( int i=0; i < first.size(); i++ )
            res.addAll( editDistance( first.get( i ) ) );

        Log.d( "Language editDistance2", res.toString() );
        return res;
    }

    public static String getCurrentLanguageSubstring(CharSequence text, int position) {

        int start = position - 1;
        int end = position;
        if( text == null || text.length() == 0 || position > text.length() )
            return "";

        String currentLanguage = getLanguageCode( text.charAt( position ) );
        if( currentLanguage == null )
            return "";

        /// determine the best length
        while( start < text.length() && start >= 0 && isCorrectLetter( text.charAt( start ), currentLanguage ) )    start--;
        while( end < text.length() && isCorrectLetter( text.charAt( end ), currentLanguage ) )                      end++;

        if( start < 0 )             start = -1;
        if( end > text.length() )   end = text.length();
        if( start == end )          return "";

        Log.d( "Language getCurrent", text.subSequence( start+1, end ).toString() );
        return text.subSequence(start + 1, end).toString();
    }
    public static String getCurrentSubstring(CharSequence text, int position) {

        int start = position - 1;
        int end = position;
        if( text == null || text.length() == 0 || position > text.length() )
            return "";

        /// determine the best length
        while( start < text.length() && start >= 0 && isCorrectLetter( text.charAt( start ) ) )    start--;
        while( end < text.length() && isCorrectLetter( text.charAt( end ) ) )                      end++;

        if( start < 0 )             start = -1;
        if( end > text.length() )   end = text.length();
        if( start == end )          return "";
        Log.d( "getCurrent", "start: " + start + " end: " + end + " text is " + text + " text.length() = " + text.length() );
        Log.d( "Language getCurrent", text.subSequence( start+1, end ).toString() );
        return text.subSequence(start + 1, end).toString();
    }



    public static HashMap <String, ArrayList <String> > divideIntoWordsUsingLanguage( String text, boolean useOnlyUnknownWords ) {

        HashMap <String, ArrayList <String> > res = new HashMap<>();
        String currentLanguage;
        StringBuilder currentWord;

        for( int i=0; i < text.length(); i++ ) {

            if( !isCorrectLetter( text.charAt( i ) ) )
                continue;

            currentLanguage = getLanguageCode( text.charAt( i ) );
            currentWord = new StringBuilder();
            while( i < text.length() && isCorrectLetter( text.charAt( i ), currentLanguage ) ) {
                currentWord.append( text.charAt( i ) );
                i++;
            }

            if( useOnlyUnknownWords && DatabaseHelper.isKnownWord( currentWord.toString() ) )
                continue;

            if( !res.containsKey(currentLanguage) ) {
                res.put( currentLanguage, new ArrayList<String>() );
            }
            res.get( currentLanguage ).add( currentWord.toString() );
        }

        Log.d( "Language divideIntoWord", res.toString() );
        return res;
    }
    public static HashMap <String, ArrayList <String> > divideIntoWordsUsingUnknownCharacters( String text, boolean useOnlyUnknownWords ) {

        HashMap <String, ArrayList <String> > res = new HashMap<>();
        String currentLanguage;
        StringBuilder currentWord;

        for( int i=0; i < text.length(); i++ ) {

            if( !isCorrectLetter( text.charAt( i ) ) )
                continue;

            currentLanguage = getLanguageCode( text.charAt( i ) );
            currentWord = new StringBuilder();
            while( i < text.length() && isCorrectLetter( text.charAt( i ) ) ) {
                currentWord.append( text.charAt( i ) );
                i++;
            }

            if( useOnlyUnknownWords && DatabaseHelper.isKnownWord( currentWord.toString() ) )
                continue;

            if( !res.containsKey(currentLanguage) ) {
                res.put( currentLanguage, new ArrayList<String>() );
            }
            res.get( currentLanguage ).add( currentWord.toString() );
        }

        Log.d( "Language divideIntoWord", res.toString() );
        return res;
    }

    public static boolean contains( String s, char c ) {

        for( int i=0; i < s.length(); i++ )
            if( s.charAt( i ) == c ) {
                return true;
            }

        return false;
    }
}