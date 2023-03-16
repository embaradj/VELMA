package com.embaradj.velma.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents the model of the MVC pattern
 * The View subscribes to the members of this object
 */
public class DataModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final LinkedList<Hve> hves = new LinkedList<>();
    private final LinkedList<Job> jobs = new LinkedList<>();
    private final HashMap<String, String> LDATopics = new HashMap<>();
    private int totalHves, totalJobs;
    private int processedHves = 0;
    private int processedjobs = 0;
    private boolean searchedHve = false;
    private boolean searchedJobs = false;

    // Used by the View to listen for changes in the Model
    public void addListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public boolean searchedHve() { return searchedHve; }
    public boolean searchedJobs() { return searchedJobs; }

    public void clearHve() {
        hves.clear();
        processedHves = 0;
        support.firePropertyChange("hve", 1, null);
    }

    public void clearJob() {
        jobs.clear();
        processedjobs = 0;
        support.firePropertyChange("job", 1, null);
    }

    public void addHve(Hve hve) {
        Hve oldHve = (hves.isEmpty()) ? null : hves.getLast();
        this.hves.add(hve);
        // Notice the View about change in the model
        support.firePropertyChange("hve", oldHve, hve);
    }

    public void addJob(Job job) {
        Job oldJob = (jobs.isEmpty()) ? null : jobs.getLast();
        this.jobs.add(job);
        // Notice the View about change in the model
        support.firePropertyChange("job", oldJob, job);
    }

    public void addLDATopics(String topic, String words) {
        LDATopics.put(topic, words);
    }

    public HashMap<String, String> getLDATopics() {
        return LDATopics;
    }

    public void clearLDATopics() {
        LDATopics.clear();
    }

    public void setTotalHves(int total) { totalHves = total; }
    public void setTotalJobs(int total) { totalJobs = total; }


    /**
     * Ask the GUI to update a progressbar
     * @param increase whether the number of processed elements should be increased
     */
    public void updateProgressBarHve(boolean increase) {
        int progress = 0;
        if (increase) processedHves++;
        if (totalHves > 0) progress = ((100) * processedHves) / totalHves;
        support.firePropertyChange("hveProgress", null, progress);
        if (progress > 99) searchedHve = true;
    }

    public void updateProgressBarJob(boolean increase) {
        int progress = 0;
        if (increase) processedjobs++;
        if (totalJobs > 0) progress = ((100) * processedjobs) / totalJobs;
        support.firePropertyChange("jobProgress", null, progress);
        if (progress > 99) searchedJobs = true;
    }
}
