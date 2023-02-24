package com.embaradj.velma;

/**
 * Represents one job
 */
public class Job {
    private String title;
    private String description;

    protected Job(String title, String description) {
        this.title = title;
        this.description = description;
    }

    protected String getTitle() { return this.title; }
    protected String getDescription() { return this.description; }

}