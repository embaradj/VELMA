package com.embaradj.velma;


import javax.swing.*;
import org.apache.commons.io.FileUtils;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Eager init Singleton
 * Contains settings and global variables, helpers
 */
public class Settings {
    private static final Settings settings = new Settings();
    protected static final double VERSION = 1.0D;
    private final String HELPFILE = "/help.rtf";
    private static final boolean DEBUG = true;
    private static final String SUSA_API_BASE_URI =
            "https://susanavet2.skolverket.se/api/1.1/infos?configuration=program&degree=true&organisationForm=yrkesh%C3%B6gskoleutbildning&subjectIds=395&size=";
    private static final String MYH_URI = "https://w3d3-integration-service.myh.se/1.0/search";
    private static final String JOBSTREAM_URI = "https://jobstream.api.jobtechdev.se/stream?date=2022-01-01T00:01:00&occupation-concept-id=";
    private static final int SUSA_MAX_HITS = 1000;
    private static final int SUSA_DEBUG_MAX_HITS = 5;
    private static double alpha, beta;
    private static int numTopics, threads, iterations, words;
    private static ArrayList<Ssyk> ssyk;
    private static boolean[] analyserSelection;

    /**
     * Constructor, initiates all default values
     */
    private Settings() {
        setDefaultSsyk();
        setDefaultAnalyser();
    }

    public static Settings getInstance() { return settings; }
    public static boolean debug() { return DEBUG; }

    /**
     * Initiate the default SSYK codes and their selections
     * If program is run in debug mode, only set the first SSYK code as chosen by default
     */
    private static void setDefaultSsyk() {
        ssyk = new ArrayList<>();
        ssyk.add(new Ssyk("UXKZ_3zZ_ipB", "Systemanalytiker och IT-arkitekter m.fl.", true));
        ssyk.add(new Ssyk("DJh5_yyF_hEM", "Mjukvaru- och systemutvecklare m.fl.", true));
        ssyk.add(new Ssyk("Q5DF_juj_8do", "Utvecklare inom spel och digitala media", true));
        ssyk.add(new Ssyk("D9SL_mtn_vGM", "Systemtestare och testledare", true));
        ssyk.add(new Ssyk("cBBa_ngH_fCx", "Systemförvaltare m.fl.", true));
        ssyk.add(new Ssyk("BAeH_eg8_T2d", "IT-säkerhetsspecialister", true));
        ssyk.add(new Ssyk("UxT1_tPF_Kbg", "Övriga IT-specialister", true));
    }

    public ArrayList<Ssyk> getSsyk() { return ssyk; }

    /**
     * Get selected SSYK codes
     * @return An array of SSYK codes
     */
    public static String[] getSelectedSsyk() {

        ArrayList<String> selected = new ArrayList<>();
        ssyk.forEach((code) -> {
            if (code.isSelected()) selected.add(code.getCode());
        });

        return selected.toArray(new String[0]);
    }

    /**
     * Select or unselect a SSYK code
     * @param selection The SSYK code
     * @param select True = select, false = unselect
     */
    public void selectSsyk(Ssyk selection, boolean select) {
        ssyk.get(ssyk.indexOf(selection)).select(select);
    }

    /**
     * Set the default Analyser settings
     */
    private static void setDefaultAnalyser() {
        alpha = 0.01;
        beta = 0.01;
        numTopics = 5;
        threads = 16; // 16 threads used for testing
        iterations = 2000; // 2000 iterations used for testing
        // Analyser: Jobs Swe, Jobs Eng, HVE Full, HVE goals, HVE Courses
        analyserSelection = new boolean[]{true, true, true, false, false};
//        analyserSelection = new boolean[]{true, true, false, true, true};
        words = 7;
    }

    public static String getSusaApiUri() {
        return SUSA_API_BASE_URI + (debug()? SUSA_DEBUG_MAX_HITS : SUSA_MAX_HITS);
    }

    public static String getMyhUri() {
        return MYH_URI;
    }

    public static String getJobStreamUri() { return JOBSTREAM_URI; }

    // Analyser settings setters & getters
    public static double getAlpha() { return alpha; }
    public static void setAlpha(double alpha) { Settings.alpha = alpha; }
    public static double getBeta() { return beta; }
    public static void setBeta(double beta) { Settings.beta = beta; }
    public static int getNumTopics() { return numTopics;}
    public static void setNumTopics(int numTopics) { Settings.numTopics = numTopics; }
    public static int getThreads() { return threads; }
    public static void setThreads(int threads) { Settings.threads = threads; }
    public static int getIterations() { return iterations; }
    public static void setIterations(int iterations) { Settings.iterations = iterations; }

    /**
     * Get analyser selections
     * @return index 0: Job ads Swe, 1: Job ads Eng, 2: HVE full, 3: HVE Goals, 4: HVE Courses
     */
    public static boolean[] getAnalyserSelection() { return analyserSelection; }

    /**
     * Set analyser selections
     * @param selection index 0: Job ads Swe, 1: Job ads Eng, 2: HVE full, 3: HVE Goals, 4: HVE Courses
     */
    public static void setAnalyserSelection(boolean[] selection) { analyserSelection = selection; }

    /**
     * Number of words a topic can contain
     * @return Number of words
     */
    public static int getWords() {
        return words;
    }
    public static void setWords(int words) {
        Settings.words = words;
    }

    public static List<String> getCorporapreFixes() {
        //String lang = Arrays.toString(getSelectedLang()).replaceAll("[\\[\\]]", "");
        List<String> preFixes = new ArrayList<>();

//        if (lang.matches(".*Swedish.*") && getAnalyserSelection()[0]) {
//            preFixes.add("se");
//        } if (lang.matches(".*English.*") && getAnalyserSelection()[0]) {
//            preFixes.add("en");
//        }

        if (getAnalyserSelection()[0]) preFixes.add("se");
        if (getAnalyserSelection()[1]) preFixes.add("en");

        if (getAnalyserSelection()[2]) {
            preFixes.add("full");
        } if (getAnalyserSelection()[3]) {
            preFixes.add("aim");
        } if (getAnalyserSelection()[4]) {
            preFixes.add("courses");
        }
        return preFixes;
    }


    /**
     * Read the help file and return it as a Document
     * @return The help file as a Document
     */
    public Document getHelpDocument() {
        try {
            InputStream helpFileStream = getClass().getResourceAsStream(HELPFILE);
            File helpFile = File.createTempFile(String.valueOf(helpFileStream.hashCode()), ".tmp");
            FileUtils.copyInputStreamToFile(helpFileStream, helpFile);
            FileInputStream stream = new FileInputStream(helpFile);
            RTFEditorKit kit = new RTFEditorKit();
            Document doc = kit.createDefaultDocument();
            kit.read(stream, doc, 0);
            return doc;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean confirmYesNo(String title, String question) {
        int userInput = JOptionPane.showConfirmDialog(
                null,
                question,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null
        );

        return (userInput == 0);   // YES

    }

}
