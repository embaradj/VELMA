package com.embaradj.velma;

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

    protected Hve(String code, String description, HashMap<String, List<String>> courses) {
        this.code = code;
        this.description = description;
        this.courses = courses;
    }

    protected String getCode() { return this.code; }
    protected String getDescription() { return this.description; }
    protected HashMap<String, List<String>> getCourses() { return this.courses; }

}
