package com.embaradj.velma.models;

import com.embaradj.velma.results.SearchHit;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents one HVE
 */
public class Hve implements SearchHit {
    private String type = "hve";
    private String code;
    private String title;
    HashMap<String, List<String>> courses;
    private String fullText;

    public Hve(String code, String title, HashMap<String, List<String>> courses, String fullText) {
        this.code = code;
        this.title = title;
        this.courses = courses;
        this.fullText = fullText;
    }

    public String getType() { return this.type; }
    public String toString() { return this.title; }
    public String getCode() { return this.code; }
    public String getTitle() { return this.title; }
    public HashMap<String, List<String>> getCourses() { return this.courses; }
    public String getFullText() { return this.fullText; }
    public String getDescription() {
        StringBuilder sb = new StringBuilder();

        getCourses().forEach((course, contents) -> {
            sb.append(course + "\n");
            contents.forEach(i -> sb.append("\t" + i + "\n"));
            sb.append("\n");
        });

        return sb.toString();
    }

}
