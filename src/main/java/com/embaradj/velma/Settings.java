package com.embaradj.velma;

public class Settings {

    public boolean debug = true;

    private static Settings settings = new Settings();

    public static Settings getInstance() { return settings; }


}
