package com.ProCorrector.XPN.procorrector;


import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;

public class RecordingLanguage {

    private static HashMap<Integer, String> languageIdToLanguageCode = new HashMap<>();
    private static HashMap <Integer, String> languageIdToLanguageName = new HashMap<>();
    private static HashMap <Integer, Integer> languageIdToLanguageFlag = new HashMap<>();
    private static HashMap <Integer, String> languageIdToLanguageLocale = new HashMap<>();

    /**
     * The most awful implementation that one could think of
     */
    public RecordingLanguage( Context context ) {

        int ID;
        Resources res = context.getResources();

        ID = R.id.action_language_ENG;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_ENG ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_ENG ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_ENG_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_eng_icon );
        ID = R.id.action_language_BUL;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_BUL ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_BUL ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_BUL_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_bul_icon );
        ID = R.id.action_language_CZE;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_CZE ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_CZE ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_CZE_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_cze_icon );
        ID = R.id.action_language_DAN;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_DAN ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_DAN ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_DAN_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_dan_icon );
        ID = R.id.action_language_DUT;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_DUT ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_DUT ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_DUT_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_dut_icon );
        ID = R.id.action_language_FIL;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_FIL ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_FIL ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_FIL_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_fil_icon );
        ID = R.id.action_language_FIN;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_FIN ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_FIN ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_FIN_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_fin_icon );
        ID = R.id.action_language_FRA;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_FRA ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_FRA ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_FRA_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_fra_icon );
        ID = R.id.action_language_GER;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_GER ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_GER ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_GER_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_ger_icon );
        ID = R.id.action_language_GRE;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_GRE ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_GRE ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_GRE_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_gre_icon );
        ID = R.id.action_language_HEB;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_HEB ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_HEB ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_HEB_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_heb_icon );
        ID = R.id.action_language_HUN;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_HUN ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_HUN ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_HUN_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_hun_icon );
        ID = R.id.action_language_ICE;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_ICE ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_ICE ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_ICE_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_ice_icon );
        ID = R.id.action_language_ITL;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_ITL ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_ITL ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_ITL_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_itl_icon );
        ID = R.id.action_language_IND;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_IND ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_IND ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_IND_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_ind_icon );
        ID = R.id.action_language_JAP;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_JAP ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_JAP ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_JAP_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_jap_icon );
        ID = R.id.action_language_KOR;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_KOR ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_KOR ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_KOR_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_kor_icon );
        ID = R.id.action_language_LIT;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_LIT ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_LIT ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_LIT_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_lit_icon );
        ID = R.id.action_language_MAL;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_MAL ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_MAL ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_MAL_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_mal_icon );
        ID = R.id.action_language_NOR;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_NOR ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_NOR ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_NOR_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_nor_icon );
        ID = R.id.action_language_POL;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_POL ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_POL ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_POL_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_pol_icon );
        ID = R.id.action_language_POR;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_POR ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_POR ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_POR_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_por_icon );
        ID = R.id.action_language_ROM;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_ROM ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_ROM ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_ROM_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_rom_icon );
        ID = R.id.action_language_RUS;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_RUS ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_RUS ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_RUS_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_rus_icon );
        ID = R.id.action_language_SER;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_SER ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_SER ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_SER_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_ser_icon );
        ID = R.id.action_language_SLO;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_SLO ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_SLO ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_SLO_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_slo_icon );
        ID = R.id.action_language_SPA;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_SPA ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_SPA ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_SPA_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_spa_icon );
        ID = R.id.action_language_SWE;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_SWE ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_SWE ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_SWE_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_swe_icon );
        ID = R.id.action_language_VIE;  languageIdToLanguageCode.put( ID, res.getString( R.string.language_VIE ) );     languageIdToLanguageLocale.put( ID, res.getString( R.string.language_locale_VIE ) );     languageIdToLanguageName.put( ID, res.getString( R.string.action_language_VIE_title ) );  languageIdToLanguageFlag.put( ID, R.drawable.flag_vie_icon );
    }

    public static boolean isLanguageID( int id ) {
        return languageIdToLanguageCode.containsKey(id);
    }
    public static String getLanguageCodeFromLanguageID( int languageID ) {
        return languageIdToLanguageCode.get(languageID);
    }
    public static String getLanguageLocaleFromLanguageID( int languageID ) {
        return languageIdToLanguageLocale.get( languageID );
    }
    public static String getLanguageNameFromLanguageID( int languageID ) {
        return languageIdToLanguageName.get( languageID );
    }
    public static int getLanguageFlagFromLanguageID( int languageID ) {
        return languageIdToLanguageFlag.get( languageID );
    }
    public static int getLanguageIDFromLanguageCode( String languageCode ) {

        for( Integer id : languageIdToLanguageCode.keySet() ) {
            if( languageIdToLanguageCode.get( id ).contains( languageCode ) )
                return id;
        }
        return 0;
    }
}
