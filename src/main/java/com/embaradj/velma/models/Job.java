package com.embaradj.velma.models;

import com.embaradj.velma.results.SearchHit;

/**
 * Represents one job
 */
public class Job implements SearchHit {
    private String type = "job";
    private String id;
    private String title;
    private String description;

    public Job(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getType() { return this.type; }
    public String getId() { return this.id; }
    public String toString() { return this.title; }
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }

}