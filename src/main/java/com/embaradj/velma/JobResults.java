package com.embaradj.velma;

public class JobResults {
    protected String headline;
    protected Description description;
    protected boolean removed;

    protected String getTitle() { return this.headline; }
    protected String getText() { return this.description.getText(); }
    protected boolean isRemoved() { return !this.removed; }

    class Description {
        protected String text;
        protected String getText() { return text; }
    }

}

