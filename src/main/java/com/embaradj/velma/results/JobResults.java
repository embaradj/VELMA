package com.embaradj.velma.results;

/**
 * Represents one result from the JobStream API.
 */
public class JobResults {
    private String id;
    private String headline;
    private Description description;

    private boolean removed;

    public String getId() { return this.id; }
    public String getTitle() { return this.headline; }
    public String getText() { return this.description.getText(); }
    public boolean isRemoved() { return !this.removed; }

    public class Description {
        private String text;
        private String getText() { return text; }
    }

}
