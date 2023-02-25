package com.embaradj.velma.models;

import com.embaradj.velma.results.SearchHit;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents one HVE
 */
public class Hve implements SearchHit {
    private String code;
    private String title;
    HashMap<String, List<String>> courses;

    public Hve(String code, String title, HashMap<String, List<String>> courses) {
        this.code = code;
        this.title = title;
        this.courses = courses;
    }

    public String toString() { return this.title; }
    public String getCode() { return this.code; }
    public String getTitle() { return this.title; }
    public HashMap<String, List<String>> getCourses() { return this.courses; }

}
