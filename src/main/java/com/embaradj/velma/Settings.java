package com.embaradj.velma;

import java.util.ArrayList;

/**
 * Eager init Singleton to keep settings and global variables
 */
public class Settings {

    private static Settings settings = new Settings();
    private boolean DEBUG = true;
    private ArrayList<Ssyk> ssyk = new ArrayList<>();

    private Settings() {
        ssyk.add(new Ssyk("UXKZ_3zZ_ipB", "des1", true));
        ssyk.add(new Ssyk("DJh5_yyF_hEM", "des1", true));
        ssyk.add(new Ssyk("Q5DF_juj_8do", "des1", true));
        ssyk.add(new Ssyk("D9SL_mtn_vGM", "des1", true));
        ssyk.add(new Ssyk("cBBa_ngH_fCx", "des1", true));
        ssyk.add(new Ssyk("BAeH_eg8_T2d", "des1", true));
        ssyk.add(new Ssyk("UxT1_tPF_Kbg", "des1", true));
    }

    public static Settings getInstance() { return settings; }

    public ArrayList<Ssyk> getSsyk() { return ssyk; }

    public String[] getSelectedSsyk() {

        ArrayList<String> selected = new ArrayList<>();
        ssyk.forEach((code) -> {
            if (code.isSelected()) selected.add(code.getCode());
        });

        return selected.toArray(new String[0]);

    }

    public boolean debug() { return this.DEBUG; }

    public void selectSsyk(Ssyk selection, boolean select) {
        ssyk.get(ssyk.indexOf(selection)).select(select);
    }

    public String getHelpText() {

        // todo: Load from RTF document
        return "THIS IS HELPTEXT";
    }






}
