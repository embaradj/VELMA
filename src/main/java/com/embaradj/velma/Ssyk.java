package com.embaradj.velma;

public class Ssyk {

    private String code, description;
    private boolean selected;

    public Ssyk(String code, String description, boolean selected) {
        this.code = code;
        this.description = description;
        this.selected = selected;
    }

    public String toString() {
        return this.code + "\t" + this.description + "\t Selected: " + ((selected)? "yes" : "no");
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSelected() {
        return selected;
    }

    // Select / Unselect this ssyk code
    public void select (boolean sel) { this.selected = sel; }
}
