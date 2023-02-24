package com.embaradj.velma.results;

public class JobResults {
    private String headline;
    private Description description;

    private boolean removed;

    public String getTitle() { return this.headline; }
    public String getText() { return this.description.getText(); }
    public boolean isRemoved() { return !this.removed; }

    public class Description {
        private String text;
        private String getText() { return text; }
    }

}
