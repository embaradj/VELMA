package com.embaradj.velma;

import java.util.LinkedList;

/**
 * Represents the model of the MVC pattern
 * The View subscribes to the members of this object
 */
public class DataModel extends java.util.Observable {

    private final LinkedList<Hve> hves = new LinkedList<>();
    private final LinkedList<Job> jobs = new LinkedList<>();

    protected void addHve(Hve hve) {
        this.hves.add(hve);
        setChanged();
        notifyObservers(hve);
    }

    protected void addJob(Job job) {
        this.jobs.add(job);
        setChanged();
        notifyObservers(job);
    }
}
