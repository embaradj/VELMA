package com.embaradj.velma.models;

import com.embaradj.velma.results.SearchHit;

/**
 * Represents one job.
 */
public record Job(String id, String title, String description, String lang) implements SearchHit {
    @Override
    public String toString() { return title; }
    @Override
    public String getType() { return "job"; }
    @Override
    public String getDescription() { return description; }
}