package com.ProCorrector.XPN.procorrector;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Language {

    /**
     * The order of languages in languageCodes has to be the same as the order in alphabet
     * */
    private static final String[] languageCodes = { "ARM", "ENG", "RUS"/*, "GRE"*//*, "GEO"*/ };
    private static final char[][] alphabet = {  /*ARM*/ { 'ա', 'բ', 'գ', 'դ', 'ե', 'զ', 'է', 'ը', 'թ', 'ժ', 'ի', 'լ', 'խ', 'ծ', 'կ', 'հ', 'ձ', 'ղ', 'ճ', 'մ', 'յ', 'ն', 'շ', 'ո', 'չ', 'պ', 'ջ', 'ռ', 'ս', 'վ', 'տ', 'ր', 'ց', 'ւ', 'փ', 'ք', 'և', 'օ', 'ֆ' },
                                                /*ENG*/ { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\'' },
                                                /*RUS*/ { 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я' },
                                                /*GRE { 'α', 'β', 'γ', 'δ', 'ε', 'ζ', 'η', 'θ', 'ι', 'κ', 'λ', 'μ', 'ν', 'ξ', 'ο', 'π', 'ρ', 'σ', 'τ', 'υ', 'φ', 'χ', 'ψ', 'ω' },*/
                                                /*GEO { 'ა', 'ბ', 'გ', 'დ', 'ე', 'ვ', 'ზ', 'თ', 'ი', 'კ', 'ლ', 'მ', 'ნ', 'ო', 'პ', 'ჟ', 'რ', 'ს', 'ტ', 'უ', 'ფ', 'ქ', 'ღ', 'ყ', 'შ', 'ჩ', 'ც', 'ძ', 'წ', 'ჭ', 'ხ', 'ჯ', 'ჰ' }*/ };

    private static HashSet <Character> allAlphabets = new HashSet<>();
    private static HashMap <String, HashSet <Character> > languageCodeToLetters = new HashMap<>();
    private static HashMap <Character, String> lettersToLanguageCode = new HashMap<>();
    private static final String defaultLanguage = "ENG";


    public Language() {

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

    /**
     * @return HashSet of characters if languageCode exists, null otherwise
     */
    public static HashSet <Character> getAlphabet( String languageCode ) {
        return languageCodeToLetters.get(languageCode);
    }

    /**
     * @return  languageCode of the letter if it is known, null otherwise
     */
    public static String getLanguageCode( char letter ) {

        letter = Character.toLowerCase( letter );
        //Log.d( "Language getCode", lettersToLanguageCode.get( letter ) );
        return lettersToLanguageCode.get(letter);
    }
    /**
     * @return      default language (English) if the word contains several languages
     */
    public static String getLanguageCode( String word ) {

        if( !isUniqueLanguage( word ) )
            return defaultLanguage;
        return getLanguageCode(Character.toLowerCase(word.charAt(0)));
    }


    /**
     * @return  true if the letter is contained in any alphabet
     */
    public static boolean isCorrectLetter( char letter ) {

        letter = Character.toLowerCase( letter );
        //Log.d( "Language isCorrect", "" + allAlphabets.contains( letter ) );
        return allAlphabets.contains(letter);
    }

    /**
     * @return true if the alphabet of the specified language contains the letter, false otherwise
     */
    public static boolean isCorrectLetter( char letter, String languageCode ) {

        letter = Character.toLowerCase( letter );

        if( languageCode == null || !languageCodeToLetters.containsKey( languageCode ) )
            return false;
        return languageCodeToLetters.get( languageCode ).contains(letter);
    }

    /**
     * @return true if both charaters belong to the same langauge alphabet, false otherwise
     */
    public static boolean isSameLanguage( char a, char b ) {

        a = Character.toLowerCase( a );
        b = Character.toLowerCase( b );
        String languageCode = getLanguageCode( a );

        return languageCode != null && languageCodeToLetters.get( languageCode ).contains( b );
    }

    /**
     * @param   s the initial string
     * @return  true if all the letters of the string belong to the same language alphabet, false otherwise
     */
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

    /**
     * @param s the initial string
     * @return  list of words that are different in 1 place from the initial string
     *          difference -> deletion, insertion, replacing a character
     */
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

        //Log.d( "Language editDistance", res.toString() );
        return res;
    }

    /**
     * @param s the initial string
     * @return  list of words that are different in 1 place from the initial string
     *          difference -> deletion, insertion, replacing a character
     */
    public static ArrayList <String> editDistance( String s ) {

        if( s.isEmpty() )
            return new ArrayList<>();
        return editDistance(s, getLanguageCode(s.charAt(0)));
    }
    /**
     * @param s the initial string
     * @return  list of words that are different in 2 places from the initial string
     *          difference -> deletion, insertion, replacing a character
     */
    public static ArrayList <String> editDistance2( String s ) {

        ArrayList <String> res = new ArrayList<>();
        ArrayList <String> first = editDistance( s );

        for( int i=0; i < first.size(); i++ )
            res.addAll( editDistance( first.get( i ) ) );

        //Log.d( "Language editDistance2", res.toString() );
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

        //Log.d( "Language getCurrent", text.subSequence( start+1, end ).toString() );
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
        //Log.d( "getCurrent", "start: " + start + " end: " + end + " text is " + text + " text.length() = " + text.length() );
        //Log.d( "Language getCurrent", text.subSequence( start+1, end ).toString() );
        return text.subSequence(start + 1, end).toString();
    }


    /**
     * The method divides into words using language codes
     * @param text                  the text from which we need to get words
     * @param useOnlyUnknownWords   checked via DatabaseHelper
     * @return                      HashMap of <LanguageCode, words> mapping
     */
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

        //Log.d( "Language divideIntoWord", res.toString() );
        return res;
    }

    /**
     * The method divides into words by looking at known and unknown characters
     * @param text                  the text from which we need to get words
     * @param useOnlyUnknownWords   checked via DatabaseHelper
     * @return                      HashMap of <LanguageCode, words> mapping
     */
    public static HashMap <String, ArrayList <String> > divideIntoWordsUsingUnknownCharacters( String text, boolean useOnlyUnknownWords ) {

        HashMap <String, ArrayList <String> > res = new HashMap<>();
        String currentLanguage;
        StringBuilder currentWord;

        /// loop through the text and add words to Map
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

        //Log.d( "Language divideIntoWord", res.toString() );
        return res;
    }

    public static boolean contains( String s, char c ) {

        for( int i=0; i < s.length(); i++ )
            if( s.charAt( i ) == c ) {
                return true;
            }

        return false;
    }

    /**
     * Uses Knutt-Moris-Pratt's algorithm
     * @param word              the small string that has to be inside text
     * @param text              the big text inside which there has to be the word
     * @return                  true if text contains word and false otherwise
     */
    public static boolean contains( String word, String text ) {

        return contains( word, text, '#' );
    }

    /**
     * Uses Knutt-Moris-Pratt's algorithm
     * @param word              the small string that has to be inside text
     * @param text              the big text inside which there has to be the word
     * @param unusedCharacter   some character that is not used both in text and in word
     * @return                  true if text contains word and false otherwise
     */
    public static boolean contains( String word, String text, char unusedCharacter ) {

        String all = word + unusedCharacter + text;
        Integer p[] = new Integer[ word.length() + 1 ];
        p[0] = 0;
        int previous = 0;

        for( int i=1; i < all.length(); i++ ) {

            int j = previous;
            while( j > 0 && all.charAt( i ) != all.charAt( j ) )
                j = p[ j-1 ];

            if( all.charAt( i ) == all.charAt( j ) )
                j++;
            if( i < p.length )
                p[i] = j;
            previous = j;

            if( j == word.length() )
                return true;
        }
        return false;
    }
}