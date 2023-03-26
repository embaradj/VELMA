package com.embaradj.velma.lda;

import com.embaradj.velma.Settings;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the filter to only choose files ending with '.txt.'.
 */
public class TxtFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        List<String> pre = Settings.getCorporapreFixes();
        return pre.stream().anyMatch(s -> file.getName().startsWith(s) && file.getName().endsWith(".txt"));
    }
}
