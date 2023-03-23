package com.embaradj.velma.models;

import com.embaradj.velma.results.SearchHit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLOutput;
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
    private HashMap<String, Integer> processed = new HashMap<>();
    private HashMap<String, Integer> total = new HashMap<>();
    private HashMap<String, Boolean> isSearched = new HashMap<>();

    // Used by the View to listen for changes in the Model
    public void addListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public boolean isSearched(String type) {
        return isSearched.getOrDefault(type, false);
    }

    public void clearHve() {
        hves.clear();
        processed.replace("hve", 0);
        support.firePropertyChange("reset", null, new SearchHitWrapper("hve", true));
    }

    public void clearJob() {
        jobs.clear();
        processed.replace("job", 0);
        support.firePropertyChange("reset", null, new SearchHitWrapper("job", true));
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

    /**
     * Sets the total number of hits for a certain type
     * @param type Type such as "job" or "hve"
     * @param hits Number of hits
     */
    public void setTotalHits(String type, int hits) {
        total.putIfAbsent(type, hits);
    }

    /**
     * Signals to the View that a search hit has been added
     * @param hit The search hit
     */
    public void addAndUpdate(SearchHit hit) {
        String type = hit.getType();
        processed.putIfAbsent(type, 0);
        processed.compute(type, (k,v) -> ++v);
        if (processed.get(type) >= total.get(type)) isSearched.put(type, true);
        support.firePropertyChange("progress", null, new SearchHitWrapper(hit, total.get(type), processed.get(type)));
    }


}
