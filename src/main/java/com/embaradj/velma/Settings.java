package com.embaradj.velma;

import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Eager init Singleton to keep settings and global variables
 */
public class Settings {

    private static final Settings settings = new Settings();

    protected static final double VERSION = 1.0D;
    private static final String HELP_FILE_PATH = "resources/help.rtf";
    private static final boolean DEBUG = true;
    private static final String SUSA_API_BASE_URI =
            "https://susanavet2.skolverket.se/api/1.1/infos?configuration=program&degree=true&organisationForm=yrkesh%C3%B6gskoleutbildning&subjectIds=395&size=";
    private static final String MYH_URI = "https://w3d3-integration-service.myh.se/1.0/search";
    private static final int SUSA_MAX_HITS = 1000;
    private static final int SUSA_DEBUG_MAX_HITS = 5;

    private static double alpha, beta;
    private static int numTopics, threads, iterations;

    private final ArrayList<Ssyk> ssyk = new ArrayList<>();
    private final HashMap<String, Boolean> languages = new HashMap<>();

    /**
     * Constructor, initiates all default values
     */
    private Settings() {
        setDefaultSsyk();
        setDefaultLang();
        setDefaultAnalyser();
    }

    public static Settings getInstance() { return settings; }
    public static boolean debug() { return DEBUG; }

    /**
     * Initiate the default SSYK codes and their selections
     * If program is run in debug mode, only set the first SSYK code as chosen by default
     */
    private void setDefaultSsyk() {
        ssyk.add(new Ssyk("UXKZ_3zZ_ipB", "Systemanalytiker och IT-arkitekter m.fl.", true));
        ssyk.add(new Ssyk("DJh5_yyF_hEM", "Mjukvaru- och systemutvecklare m.fl.", !debug()));
        ssyk.add(new Ssyk("Q5DF_juj_8do", "Utvecklare inom spel och digitala media", !debug()));
        ssyk.add(new Ssyk("D9SL_mtn_vGM", "Systemtestare och testledare", !debug()));
        ssyk.add(new Ssyk("cBBa_ngH_fCx", "Systemförvaltare m.fl.", !debug()));
        ssyk.add(new Ssyk("BAeH_eg8_T2d", "IT-säkerhetsspecialister", !debug()));
        ssyk.add(new Ssyk("UxT1_tPF_Kbg", "Övriga IT-specialister", !debug()));
    }

    public ArrayList<Ssyk> getSsyk() { return ssyk; }

    /**
     * Get selected SSYK codes
     * @return An array of SSYK codes
     */
    public String[] getSelectedSsyk() {

        ArrayList<String> selected = new ArrayList<>();
        ssyk.forEach((code) -> {
            if (code.isSelected()) selected.add(code.getCode());
        });

        return selected.toArray(new String[0]);
    }

    /**
     * Select or unselect a CSYK code
     * @param selection The SSYK code
     * @param select True = select, false = unselect
     */
    public void selectSsyk(Ssyk selection, boolean select) {
        ssyk.get(ssyk.indexOf(selection)).select(select);
    }

    /**
     * Set the default languages
     */
    private void setDefaultLang() {
        languages.put("English", true);
        languages.put("Swedish", true);
    }

    public void selectLang(String lang, boolean select) {
        languages.replace(lang, select);
    }

    /**
     * Get selected Languages
     * @return A lits of selected languages
     */
    public String[] getSelectedLang() {
        ArrayList<String> selected = new ArrayList<>();
        languages.forEach((lang, select) -> { if (select) selected.add(lang); });
        return selected.toArray(new String[0]);
    }

    public HashMap<String, Boolean> getLang() { return this.languages; }

    /**
     * Set the default Analyser settings
     */
    private void setDefaultAnalyser() {
        alpha = 0.01;
        beta = 0.01;
        numTopics = 10;
        threads = 4;
        iterations = 1000;
    }

    public static String getSusaApiUri() {
        return SUSA_API_BASE_URI + (debug()? SUSA_DEBUG_MAX_HITS : SUSA_MAX_HITS);
    }

    public static String getMyhUri() {
        return MYH_URI;
    }

    public double getAlpha() { return alpha; }
    public void setAlpha(double alpha) { this.alpha = alpha; }
    public double getBeta() { return beta; }
    public void setBeta(double beta) { this.beta = beta; }
    public int getNumTopics() { return numTopics;}
    public void setNumTopics(int numTopics) { this.numTopics = numTopics; }
    public int getThreads() { return threads; }
    public void setThreads(int threads) { this.threads = threads; }
    public int getIterations() { return iterations; }
    public void setIterations(int iterations) { this.iterations = iterations; }


    /**
     * Read the help file and return it as a Document
     * @return The help file as a Document
     */
    public Document getHelpDocument() {

        try {
            FileInputStream stream = new FileInputStream(HELP_FILE_PATH);
            RTFEditorKit kit = new RTFEditorKit();
            Document doc = kit.createDefaultDocument();
            kit.read(stream, doc, 0);
            return doc;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
