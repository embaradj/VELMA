package com.embaradj.velma;

import java.util.ArrayList;

/**
 * Eager init Singleton to keep settings and global variables
 */
public class Settings {

    private static Settings settings = new Settings();
    public boolean DEBUG = true;
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

    public void selectSsyk(Ssyk selection, boolean select) {
        ssyk.get(ssyk.indexOf(selection)).select(select);
    }







}
