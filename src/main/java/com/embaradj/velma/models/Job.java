package com.embaradj.velma.models;

/**
 * Represents one job
 */
public class Job {
    private String title;
    private String description;

    public Job(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }

}