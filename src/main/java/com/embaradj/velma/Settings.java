package com.embaradj.velma;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Eager init Singleton to keep settings and global variables
 */
public class Settings {

    private static Settings settings = new Settings();
    private boolean DEBUG = true;
    private ArrayList<Ssyk> ssyk = new ArrayList<>();
    private HashMap<String, Boolean> languages = new HashMap<>();

    private Settings() {

        setDefaultSsyk();
        setDefaultLang();
    }

    public static Settings getInstance() { return settings; }
    public boolean debug() { return this.DEBUG; }

    // SSYK Settings
    private void setDefaultSsyk() {
        ssyk.add(new Ssyk("UXKZ_3zZ_ipB", "Systemanalytiker och IT-arkitekter m.fl.", true));
        ssyk.add(new Ssyk("DJh5_yyF_hEM", "Mjukvaru- och systemutvecklare m.fl.", true));
        ssyk.add(new Ssyk("Q5DF_juj_8do", "Utvecklare inom spel och digitala media", true));
        ssyk.add(new Ssyk("D9SL_mtn_vGM", "Systemtestare och testledare", true));
        ssyk.add(new Ssyk("cBBa_ngH_fCx", "Systemförvaltare m.fl.", true));
        ssyk.add(new Ssyk("BAeH_eg8_T2d", "IT-säkerhetsspecialister", true));
        ssyk.add(new Ssyk("UxT1_tPF_Kbg", "Övriga IT-specialister", true));
    }
    public ArrayList<Ssyk> getSsyk() { return ssyk; }
    public String[] getSelectedSsyk() {

        ArrayList<String> selected = new ArrayList<>();
        ssyk.forEach((code) -> {
            if (code.isSelected()) selected.add(code.getCode());
        });

        return selected.toArray(new String[0]);

    }
    public void selectSsyk(Ssyk selection, boolean select) {
        ssyk.get(ssyk.indexOf(selection)).select(select);
    }

    // LANGUAGE SETTINGS
    private void setDefaultLang() {
        languages.put("English", true);
        languages.put("Swedish", true);
    }
    public void selectLang(String lang, boolean select) {
        languages.replace(lang, select);
    }
    public String[] getSelectedLang() {
        ArrayList<String> selected = new ArrayList<>();
        languages.forEach((lang, select) -> { if (select) selected.add(lang); });
        return selected.toArray(new String[0]);
    }
    public HashMap<String, Boolean> getLang() { return this.languages; }

    // ANALYSER SETTINGS


    public String getHelpText() {
        // todo: Load from RTF document
        return "THIS IS HELPTEXT";
    }

}
