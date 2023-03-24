package com.embaradj.velma.models;

import com.embaradj.velma.results.SearchHit;

/**
 * Represents one HVE.
 */
public record Hve(String code, String title, String courses, String fullText, String partText) implements SearchHit {
    @Override
    public String toString() { return title; }
    @Override
    public String getType() { return "hve"; }
    @Override
    public String getDescription() { return fullText(); }
}
