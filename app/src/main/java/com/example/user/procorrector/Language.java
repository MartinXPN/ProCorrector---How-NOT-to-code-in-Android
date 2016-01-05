package com.example.user.procorrector;


import android.content.Context;
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
    private static final String defaultLanguage = "ENG";
    private static Context context;


    public Language( Context currentContext ) {

        context = currentContext;

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

    public static boolean supportsLanguage( String languageCode ) {
        return languageCodeToLetters.containsKey( languageCode );
    }

    public static String getLanguageLocaleFromCode(String languageCode) {

        if( languageCode.matches( context.getResources().getString(R.string.language_ENG ) ) )          { return context.getResources().getString( R.string.language_locale_ENG ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_BUL ) ) )    { return context.getResources().getString( R.string.language_locale_BUL ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_CZE ) ) )    { return context.getResources().getString( R.string.language_locale_CZE ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_DAN ) ) )    { return context.getResources().getString( R.string.language_locale_DAN ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_DUT ) ) )    { return context.getResources().getString( R.string.language_locale_DUT ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FIL ) ) )    { return context.getResources().getString( R.string.language_locale_FIL ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FIN ) ) )    { return context.getResources().getString( R.string.language_locale_FIN ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FRA ) ) )    { return context.getResources().getString( R.string.language_locale_FRA ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_GER ) ) )    { return context.getResources().getString( R.string.language_locale_GER ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_GRE ) ) )    { return context.getResources().getString( R.string.language_locale_GRE ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_HEB ) ) )    { return context.getResources().getString( R.string.language_locale_HEB ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_HUN ) ) )    { return context.getResources().getString( R.string.language_locale_HUN ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ICE ) ) )    { return context.getResources().getString( R.string.language_locale_ICE ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ITL ) ) )    { return context.getResources().getString( R.string.language_locale_ITL ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_IND ) ) )    { return context.getResources().getString( R.string.language_locale_IND ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_JAP ) ) )    { return context.getResources().getString( R.string.language_locale_JAP ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_KOR ) ) )    { return context.getResources().getString( R.string.language_locale_KOR ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_LIT ) ) )    { return context.getResources().getString( R.string.language_locale_LIT ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_MAL ) ) )    { return context.getResources().getString( R.string.language_locale_MAL ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_NOR ) ) )    { return context.getResources().getString( R.string.language_locale_NOR ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_POL ) ) )    { return context.getResources().getString( R.string.language_locale_POL ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_POR ) ) )    { return context.getResources().getString( R.string.language_locale_POR ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ROM ) ) )    { return context.getResources().getString( R.string.language_locale_ROM ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_RUS ) ) )    { return context.getResources().getString( R.string.language_locale_RUS ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SER ) ) )    { return context.getResources().getString( R.string.language_locale_SER ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SLO ) ) )    { return context.getResources().getString( R.string.language_locale_SLO ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SPA ) ) )    { return context.getResources().getString( R.string.language_locale_SPA ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SWE ) ) )    { return context.getResources().getString( R.string.language_locale_SWE ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_VIE ) ) )    { return context.getResources().getString( R.string.language_locale_VIE ); }

        return "";
    }
    public static String getLanguageFromCode(String languageCode) {

        if( languageCode.matches( context.getResources().getString( R.string.language_ENG ) ) )         { return context.getResources().getString( R.string.action_language_ENG_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_BUL ) ) )    { return context.getResources().getString( R.string.action_language_BUL_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_CZE ) ) )    { return context.getResources().getString( R.string.action_language_CZE_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_DAN ) ) )    { return context.getResources().getString( R.string.action_language_DAN_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_DUT ) ) )    { return context.getResources().getString( R.string.action_language_DUT_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FIL ) ) )    { return context.getResources().getString( R.string.action_language_FIL_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FIN ) ) )    { return context.getResources().getString( R.string.action_language_FIN_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FRA ) ) )    { return context.getResources().getString( R.string.action_language_FRA_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_GER ) ) )    { return context.getResources().getString( R.string.action_language_GER_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_GRE ) ) )    { return context.getResources().getString( R.string.action_language_GRE_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_HEB ) ) )    { return context.getResources().getString( R.string.action_language_HEB_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_HUN ) ) )    { return context.getResources().getString( R.string.action_language_HUN_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ICE ) ) )    { return context.getResources().getString( R.string.action_language_ICE_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ITL ) ) )    { return context.getResources().getString( R.string.action_language_ITL_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_IND ) ) )    { return context.getResources().getString( R.string.action_language_IND_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_JAP ) ) )    { return context.getResources().getString( R.string.action_language_JAP_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_KOR ) ) )    { return context.getResources().getString( R.string.action_language_KOR_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_LIT ) ) )    { return context.getResources().getString( R.string.action_language_LIT_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_MAL ) ) )    { return context.getResources().getString( R.string.action_language_MAL_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_NOR ) ) )    { return context.getResources().getString( R.string.action_language_NOR_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_POL ) ) )    { return context.getResources().getString( R.string.action_language_POL_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_POR ) ) )    { return context.getResources().getString( R.string.action_language_POR_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ROM ) ) )    { return context.getResources().getString( R.string.action_language_ROM_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_RUS ) ) )    { return context.getResources().getString( R.string.action_language_RUS_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SER ) ) )    { return context.getResources().getString( R.string.action_language_SER_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SLO ) ) )    { return context.getResources().getString( R.string.action_language_SLO_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SPA ) ) )    { return context.getResources().getString( R.string.action_language_SPA_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SWE ) ) )    { return context.getResources().getString( R.string.action_language_SWE_title ); }
        else if( languageCode.matches( context.getResources().getString( R.string.language_VIE ) ) )    { return context.getResources().getString( R.string.action_language_VIE_title ); }

        return "";
    }
    public static int getLanguageFlagIDFromCode(String languageCode) {

        if( languageCode.matches( context.getResources().getString( R.string.language_ENG ) ) )         { return R.drawable.flag_eng_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_BUL ) ) )    { return R.drawable.flag_bul_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_CZE ) ) )    { return R.drawable.flag_cze_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_DAN ) ) )    { return R.drawable.flag_dan_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_DUT ) ) )    { return R.drawable.flag_dut_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FIL ) ) )    { return R.drawable.flag_fil_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FIN ) ) )    { return R.drawable.flag_fin_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_FRA ) ) )    { return R.drawable.flag_fra_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_GER ) ) )    { return R.drawable.flag_ger_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_GRE ) ) )    { return R.drawable.flag_gre_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_HEB ) ) )    { return R.drawable.flag_heb_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_HUN ) ) )    { return R.drawable.flag_hun_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ICE ) ) )    { return R.drawable.flag_ice_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ITL ) ) )    { return R.drawable.flag_itl_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_IND ) ) )    { return R.drawable.flag_ind_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_JAP ) ) )    { return R.drawable.flag_jap_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_KOR ) ) )    { return R.drawable.flag_kor_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_LIT ) ) )    { return R.drawable.flag_lit_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_MAL ) ) )    { return R.drawable.flag_mal_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_NOR ) ) )    { return R.drawable.flag_nor_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_POL ) ) )    { return R.drawable.flag_pol_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_POR ) ) )    { return R.drawable.flag_por_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_ROM ) ) )    { return R.drawable.flag_rom_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_RUS ) ) )    { return R.drawable.flag_rus_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SER ) ) )    { return R.drawable.flag_ser_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SLO ) ) )    { return R.drawable.flag_slo_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SPA ) ) )    { return R.drawable.flag_spa_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_SWE ) ) )    { return R.drawable.flag_swe_icon; }
        else if( languageCode.matches( context.getResources().getString( R.string.language_VIE ) ) )    { return R.drawable.flag_vie_icon; }

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