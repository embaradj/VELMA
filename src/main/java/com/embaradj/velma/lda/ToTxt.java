package com.embaradj.velma.lda;

import com.embaradj.velma.models.Hve;
import com.embaradj.velma.models.Job;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Responsible for converting collected job ads and curriculums to '.txt' files.
 */
public class ToTxt {
    String path;

    /**
     * Creates three corpora of HVE curriculums.
     * Consisting of fulltext, aim and courses.
     * @param hve to create corpora of
     */
    public ToTxt(Hve hve) {
        try {
            // Write fulltext text files
            path = "resources/rawdata/hve/";
            Files.writeString(Path.of(path + "full/" + "full_" + hve.code() + ".txt"), hve.fullText(), StandardCharsets.UTF_8);
            // Write aim text files
            Files.writeString(Path.of(path + "aim/"  + "aim_" + hve.code() + ".txt"), hve.partText(), StandardCharsets.UTF_8);
            // Write courses text files
            Files.writeString(Path.of(path + "courses/"  + "courses_" + hve.code() + ".txt"), hve.courses(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }

    }

    /**
     * Creates two corpora of job ads.
     * One for Swedish ads and one for English ads.
     * @param job to create corpora of
     */
    public ToTxt(Job job) {
        try {
            path = "resources/rawdata/job/";
            if (job.lang().contains("sv")) {
                Files.writeString(Path.of(path + "swe/" + "se_" + job.id() + ".txt"), job.getDescription(), StandardCharsets.UTF_8);
            } else {
                Files.writeString(Path.of(path + "eng/" + "en_" + job.id() + ".txt"), job.getDescription(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
