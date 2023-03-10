package com.embaradj.velma.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;

import static javax.swing.SwingUtilities.isEventDispatchThread;

/**
 * Represents the model of the MVC pattern
 * The View subscribes to the members of this object
 */
public class DataModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final LinkedList<Hve> hves = new LinkedList<>();
    private final LinkedList<Job> jobs = new LinkedList<>();
    private final HashMap<String, String> LDATopics = new HashMap<>();

    // Used by the View to listen for changes in the Model
    public void addListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void clearHve() {
        hves.clear();
        support.firePropertyChange("hve", null, null);
    }

    public void clearJob() {
        jobs.clear();
        support.firePropertyChange("job", null, null);
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
}
