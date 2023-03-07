package com.embaradj.velma.lda;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Responsible for converting collected job ads and curriculums to '.txt' files.
 */
public class ToTxt {
    public ToTxt(String type, String filename, String fullText) {
        try {
            Path path; // Check whether to place the file in job or hve directory
            if (type.contains("job")) { // If job ad, include id in text file for identification purpose
                path = Path.of("resources/rawdata/job/" + filename + ".txt");
                String text = filename + "\n" + fullText;
                Files.writeString(path, text, StandardCharsets.UTF_8);
            } else {
                path = Path.of("resources/rawdata/hve/" + filename + ".txt");
                Files.writeString(path, fullText, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }

    }
}
