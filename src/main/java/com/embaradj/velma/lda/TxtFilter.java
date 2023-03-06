package com.embaradj.velma.lda;

import java.io.File;
import java.io.FileFilter;

/**
 * Represents the filter to only choose files ending with '.txt.'.
 */
public class TxtFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return file.toString().endsWith(".txt");
    }
}
