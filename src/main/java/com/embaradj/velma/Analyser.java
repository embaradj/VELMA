package com.embaradj.velma;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Keyword analyser for topics extracted with LDA.
 * The percentages calculated for each topic in regard to possible max outcome, which is when;
 * all words of a topic is found at least once in every document of the dataset, which gives 100%
 */
public class Analyser {

    private final String hvePath = "resources/rawdata/hve";
    private final String jobsPath = "resources/rawdata/job";
    private HashMap<String, String> jobs = new HashMap<>();
    private HashMap<String, String> hves = new HashMap<>();
    private HashMap<String, String> topics;

    /**
     * If class is run by itself it will use some fake topics
     * @param args
     */
    public static void main(String[] args) {
        new Analyser(createTestTopics());
    }

    /**
     * Constructor, parses the global settings and starts the analysing
     * @param topics Topics to analyse
     */
    public Analyser(HashMap<String, String> topics) {
        this.topics = topics;
        boolean[] settings = Settings.getAnalyserSelection();

        if (settings[0]) readFiles(jobs, jobsPath + "/swe");     // Job ads Swe
        if (settings[1]) readFiles(jobs, jobsPath + "/eng");     // Job ads Eng
        if (settings[2]) readFiles(hves, hvePath + "/full");     // HVE full
        if (settings[3]) readFiles(hves, hvePath + "/aim");      // HVE aims
        if (settings[4]) readFiles(hves, hvePath + "/courses");  // HVE courses

        doAnalyse();
    }

    /**
     * Read all files located in the supplied path, and put the content of each file as a value in the HashMap
     * @param map HashMap to populate with file input
     * @param path Path pointing to the folder with the files to read
     */
    private void readFiles(HashMap<String, String> map, String path) {

        try {
            File folder = new File(path);
            File[] files = folder.listFiles();

            for (File file : files) {
                if (!file.isFile()) continue;
//                System.out.println("Reading file " + file.getName());
                map.put(file.getName(), Files.readString(file.toPath()));
            }

        } catch (Exception e) {
            System.out.println("Problem reading files in " + path);
            e.printStackTrace();
        }
    }

    /**
     * Creates some dummy topics for testing
     * @return topics
     */
    private static HashMap<String, String> createTestTopics() {
        HashMap<String, String> testTopics = new HashMap<>();
        testTopics.put("TOPIC 0", "utveckling, hos, kunder, erfarenhet, arbeta, projekt, se");
        testTopics.put("TOPIC 1", "experience, team, work, development, working, software, skills");
        testTopics.put("TOPIC 2", "personalexpressen, konsulter, uppdrag, kunder, offentliga, gentemot, tid");
        testTopics.put("TOPIC 3", "erfarenhet, ansökan, arbeta, arbete, arbetar, se, utveckling");
        testTopics.put("TOPIC 4", "erfarenhet, arbeta, hos, kunder, söker, team, tjänsten");
        return testTopics;
    }

    /**
     * Runs the analysing process
     */
    private void doAnalyse() {
        System.out.println("Analyser running..\nProgress 0 %");

        // Number of hits [0] jobs, [1] HVEs
        HashMap<String, Integer[]> wordsNum = new HashMap<>();

        int counter = 0;
        int totalTopics = topics.values().size();
        int topicSize = getTopicSize();

        for (String topic : topics.values()) {              // Each topic

            String[] words = topic.split(", ");

            for (int i = 0; i < words.length; i++) {        // Each word
                // Count number of occurrences of each word
                int jh = count(words[i], jobs);
                int hh = count(words[i], hves);

                // If word already exists it has already been counted. No purpose of counting it again.
                wordsNum.putIfAbsent(words[i], new Integer[]{jh, hh});

                counter++;
                System.out.println("Progress " + 100 * counter / (totalTopics * topicSize) + " %");
            }
        }

        printResults(wordsNum);
    }

    /**
     * Counts the number of files (HashMap values) in which the supplied word is found
     * @param keyword The word to look for
     * @param dataset The HashMap
     * @return Number of files the keyword exists in
     */
    private int count(String keyword, HashMap<String, String> dataset) {
        int counter = 0;

        for (Map.Entry<String, String> set : dataset.entrySet()) {      // Each file
            String file = set.getKey();                                 // Variable only for debugging
            //String[] value = set.getValue().split("[^A-ö]+");
            String[] words = set.getValue().split("[\s\n\\.\\,\\!]+");

            for (int i = 0; i < words.length; i++) {                    // Each word
                if (keyword.toLowerCase().equals(words[i].toLowerCase())) {
                    counter++;
                    break;  // If the word is found jump to the next file
                }
            }
        }

        return counter;
    }

    private void printResults(HashMap<String, Integer[]> wordsNum) {

        int topicSize = getTopicSize();
        final int margin = 30;

        System.out.println("\n\nTotal number of jobs: " + jobs.size());
        System.out.println("Total number of HVEs: " + hves.size());

        // HEADER
        printSpaces(margin);
        System.out.print("Jobs");
        printSpaces(11);
        System.out.println("HVEs");

        topics.forEach((topicName, topic) -> {            // Each topic
            String[] words = topic.split(", ");
            int[] total = sum(words, wordsNum);
            System.out.print(topicName);
            printSpaces(margin - topicName.length());
            String jSumString = total[0] + " (" + 100 * total[0] / (jobs.size() * topicSize) + "%)";
            System.out.print(jSumString);
            printSpaces(15 - jSumString.length());
            System.out.println(total[1] + " (" + 100 * total[1] / (hves.size() * topicSize) + "%)");

            for (int i = 0; i < words.length; i++) {    // Each word in the topic

                Integer sum[] = wordsNum.get(words[i]);
                String jobString = sum[0] + " (" + 100 * sum[0] / jobs.size() + "%)";
                String hveString = sum[1] + " (" + 100 * sum[1] / hves.size() + "%)";

                printSpaces(3);
                System.out.print(words[i]);
                printSpaces(30-(3 + words[i].length()));

                System.out.print(jobString);
                printSpaces(15 - jobString.length());
                System.out.println(hveString);
            }

        });
    }

    private int[] sum(String[] words, HashMap<String, Integer[]> map) {

        int j = 0;
        int h = 0;

        for (int i = 0; i < words.length; i++) {
            j += map.get(words[i])[0];
            h += map.get(words[i])[1];
        }

        return new int[]{j,h};
    }

    private void printSpaces(int spaces){
        for (int i = 0; i < spaces; i++) System.out.print(" ");
    }

    private int getTopicSize() {
        int size = 0;

        for (String topic : topics.values()) {
            int newSize = topic.split(", ").length;
            if (size == 0) size = newSize;
            else if (size != newSize) System.out.println("Warning: inconsistent topic size!");
        }

        return size;
    }

    public void setTopics(HashMap<String, String> topics) {
        this.topics = topics;
    }

}
