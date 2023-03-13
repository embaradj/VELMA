package com.embaradj.velma;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Responsible for parsing the PDF-file using {@link org.apache.pdfbox}.
 */
public class PDFReader {
    private String[] lines;
    private final HashMap<String, List<String>> courses = new HashMap<>();
    private String fullText;
    StringBuilder builder = new StringBuilder();
    private final String[] filter = {"Kursöversikt", "Obligatoriska kurser", "Poäng", "Summa",
            "bokstavsordning", "Sida", "Ansökan", "Diarienummer", "Insänd"};

    /**
     * Initiate the file to be read then calls the method for reading and extracting information.
     */
    public PDFReader(String path) {
        File file = new File(path);
        read(file);
        extractCourses();
        setPartText();

        if (Settings.debug()) {
            String name = getText(getLine("Utbildningens namn") + 1);
            System.out.println("Name: " + name);

            String institute = getText(getLine("Ansvarig utbildningsanordnare") + 1);
            System.out.println("Institute: " + institute);

            String slots = getText(getLine("Totalt antal platser") + 1);
            System.out.println("Slots available: " + slots);

            System.out.println("Courses");
            courses.forEach((key, value) -> System.out.println(key + " >> " + value));
        }
    }

    /**
     * Parse the PDF-file and initiate the array holding all text.
     * @param file to be read
     */
    private void read(File file) {
        try {
            PDDocument doc = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            fullText = stripper.getText(doc);
            lines = fullText.split(System.getProperty("line.separator"));
            doc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches knowledge the graduates' should have after completed programme.
     */
    private void setPartText() {
        int start = getLine("Mål och krav för examen");
        int stop = getLine("Kursöversikt");
        for (int i = start; i < stop; i++) {
            if (Arrays.stream(filter).noneMatch(lines[i]::contains)) {
                builder.append(lines[i]);
            }
        }
    }

    /**
     * Getter for the courses + graduates' knowledge
     * @return String
     */
    public String getPartText() {
        return builder.toString();
    }

    /**
     * Fetch all courses by only looking at the sections 'Kurser i bokstavsordning'.
     * Filters unnecessary terms and symbols separating the courses
     * And adds all courses a map.
     */
    private void extractCourses() {
        List<String> tempList = new ArrayList<>();
        int start = getLine("Kurser i bokstavsordning");
        int stop = getLine("Yrkesroller");
        String separator = ":";
        for (int i = start; i < stop; i++) {
            if (Arrays.stream(filter).noneMatch(lines[i]::contains)) {
                tempList.add(lines[i]);
                if (lines[i].contains(".....")) {
                    tempList.removeIf(s -> s.contains("....."));
                    int pos = tempList.get(0).indexOf(separator);
                    String temp = tempList.get(0).substring(pos + separator.length()).trim();
                    courses.put(temp, tempList.stream().toList());
                    builder.append(tempList.stream().toList());
                    tempList.clear();
                }
            }
        }
    }

    /**
     * Getter for courses.
     * @return courses
     */
    public HashMap<String, List<String>> getCourses() { return this.courses; }

    /**
     * Getter for full text.
     * @return full text
     */
    public String getFullText() { return this.fullText; }

    /**
     * Getter for the line a specific text is on.
     * @param filter text
     * @return index in array where text exist
     */
    private int getLine(String filter) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(filter)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Getter for the text at a specific line, or index in array.
     * @param line to get, or index in array
     * @return the text on that line, or at the index of the array
     */
    private String getText(int line) {
        return lines[line];
    }
}