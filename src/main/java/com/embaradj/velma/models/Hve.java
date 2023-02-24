package com.embaradj.velma.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents one HVE
 */
public class Hve {
    private String code;
    private String description;
    HashMap<String, List<String>> courses;

    public Hve(String code, String description, HashMap<String, List<String>> courses) {
        this.code = code;
        this.description = description;
        this.courses = courses;
    }

    public String getCode() { return this.code; }
    public String getDescription() { return this.description; }
    public HashMap<String, List<String>> getCourses() { return this.courses; }

}
