package com.embaradj.velma;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Keyword analyser for topics extracted with LDA.
 */
public class Analyser {

    private final String hvePath = "resources/rawdata/hve";
    private final String jobsPath = "resources/rawdata/job";
    private HashMap<String, String> jobs = new HashMap<>();
    private HashMap<String, String> hves = new HashMap<>();
    private HashMap<String, String> topics = new HashMap<>();

    /**
     * If class is run by itself it will use some fake topics
     * @param args
     */
    public static void main(String[] args) {
        new Analyser(createTestTopics());
    }

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

    public void setTopics(HashMap<String, String> topics) {
        this.topics = topics;
    }

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

    private void doAnalyse() {
        System.out.println("Analyser running..\nProgress 0 %");

        // Number of hits [0] jobs, [1] HVEs
        HashMap<String, Integer[]> results = new HashMap<>();

        int counter = 0;
        int totalTopics = topics.values().size();
        int topicSize = getTopicSize();

        for (String topic : topics.values()) {              // Each topic

            int jobHits = 0;
            int hveHits = 0;

            String[] words = topic.split(", ");

            for (int i = 0; i < words.length; i++) {        // Each word
                // Count number of occurrences of each word
                jobHits += count(words[i], jobs);
                hveHits += count(words[i], hves);

                counter++;
                System.out.println("Progress " + 100 * counter / (totalTopics * topicSize) + " %");
            }

            results.put(topic, new Integer[]{jobHits, hveHits});
        }

        printResults(results);

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

    private void printResults(HashMap<String, Integer[]> results) {
        int topicSize = getTopicSize();

        System.out.println("\n\nTotal number of jobs: " + jobs.size());
        System.out.println("Total number of HVEs: " + hves.size());

        int margin = 80;
        int distance = 15;
        System.out.print("\nTopic containing");
        printSpaces(margin - 16);
        System.out.println("Jobs           HVEs");
        results.forEach((topic, hits) -> {

            String jobResult = hits[0] + " (" + 100 * hits[0] / (jobs.size() * topicSize) + "%)";
            String hveResult = hits[1] + " (" + 100 * hits[1] / (hves.size() * topicSize) + "%)";

            System.out.print(topic);
            printSpaces(margin-topic.length());

            System.out.print(jobResult);
            printSpaces(distance - jobResult.length());

            System.out.println(hveResult);
        });
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

}
