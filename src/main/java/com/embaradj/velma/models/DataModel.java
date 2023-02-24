package com.embaradj.velma.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;

/**
 * Represents the model of the MVC pattern
 * The View subscribes to the members of this object
 */
public class DataModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final LinkedList<Hve> hves = new LinkedList<>();
    private final LinkedList<Job> jobs = new LinkedList<>();

    public void addListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void addHve(Hve hve) {
        Hve oldHve;
        if (!hves.isEmpty()) {
            oldHve = hves.getLast();
        } else {
            oldHve = hve;
        }
        this.hves.add(hve);
        support.firePropertyChange("hve", oldHve, hve);
//        setChanged();
//        notifyObservers(hve);
    }

    public void addJob(Job job) {
        Job oldJob;
        if (!jobs.isEmpty()) {
            oldJob = jobs.getLast();
        } else {
            oldJob = job;
        }
        this.jobs.add(job);
        support.firePropertyChange("job", oldJob, job);
//        setChanged();
//        notifyObservers(job);
    }
}
