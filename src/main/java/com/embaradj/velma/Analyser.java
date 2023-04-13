package com.embaradj.velma;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Analyser {

    private final static boolean DEBUG = true;
    private final String hvePath = "resources/rawdata/hve";
    private final String jobsPath = "resources/rawdata/job";
    private HashMap<String, String> jobs = new HashMap<>();
    private HashMap<String, String> hves = new HashMap<>();
    private HashMap<String, String> topics = new HashMap<>();

    public static void main(String[] args) {
        new Analyser();
    }

    public Analyser() {
        // index 0: Job ads Swe, 1: Job ads Eng, 2: HVE full, 3: HVE Goals, 4: HVE Courses
        boolean[] settings = Settings.getAnalyserSelection();

        if (settings[0]) readFiles(jobs, jobsPath + "/swe");     // Job ads Swe
        if (settings[1]) readFiles(jobs, jobsPath + "/eng");     // Job ads Eng
        if (settings[2]) readFiles(hves, hvePath + "/full");     // HVE full
        if (settings[3]) readFiles(hves, hvePath + "/aim");      // HVE aims
        if (settings[4]) readFiles(hves, hvePath + "/courses");  // HVE courses

        if (DEBUG) createTestTopics();

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
                System.out.println("Reading file " + file.getName());
                map.put(file.getName(), Files.readString(file.toPath()));
            }

        } catch (Exception e) {
            System.out.println("Problem reading files in " + path);
            e.printStackTrace();
        }
    }

    private void createTestTopics() {
        topics.put("TOPIC 0", "utveckling, hos, kunder, erfarenhet, arbeta, projekt, se");
        topics.put("TOPIC 1", "experience, team, work, development, working, software, skills");
        topics.put("TOPIC 2", "personalexpressen, konsulter, uppdrag, kunder, offentliga, gentemot, tid");
        topics.put("TOPIC 3", "erfarenhet, ansökan, arbeta, arbete, arbetar, se, utveckling");
        topics.put("TOPIC 4", "erfarenhet, arbeta, hos, kunder, söker, team, tjänsten");
    }

    private void doAnalyse() {
        HashMap<String, Integer[]> results = new HashMap<>();

        for (String topic : topics.values()) {
            // Each topic

            int jobHits = 0;
            int hveHits = 0;

            String[] words = topic.split(", ");

            for (int i = 0; i < words.length; i++) {
                // Each word

                // Count number of occurrences of each word
                jobHits += count(words[i], jobs);
                hveHits += count(words[i], hves);
            }

            if (results.containsKey(topic)) {
                System.out.printf("warning overwriting");
            }
            results.put(topic, new Integer[]{jobHits, hveHits});
        }

        printResults(results);

    }

    private int count(String keyword, HashMap<String, String> dataset) {
        int counter = 0;

        for (Map.Entry<String, String> set : dataset.entrySet()) {      // Each file
            String file = set.getKey();
            //String[] value = set.getValue().split("[^A-ö]+");
            String[] words = set.getValue().split("[\s\n\\.\\,\\!]+");

            for (int i = 0; i < words.length; i++) {                    // Each word
                if (keyword.toLowerCase().equals(words[i].toLowerCase())) {
                    counter++;
                    break;  // If the word is found jump to next file
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
        printSpaces(margin);
        System.out.println("Jobs           HVEs");
        results.forEach((topic, hits) -> {

            String jobResult = hits[0] + " (" + hits[0] / (jobs.size() * topicSize) + "%)";
            String hveResult = hits[1] + " (" + hits[1] / (hves.size() * topicSize) + "%)";

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
