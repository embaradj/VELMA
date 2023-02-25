package com.embaradj.velma.models;

import com.embaradj.velma.results.SearchHit;

/**
 * Represents one job
 */
public class Job implements SearchHit {
    private String type = "job";
    private String title;
    private String description;

    public Job(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getType() { return this.type; }
    public String toString() { return this.title; }
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }

}