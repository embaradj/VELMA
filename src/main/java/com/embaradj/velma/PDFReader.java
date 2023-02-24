package com.embaradj.velma;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PDFReader {
    private String[] lines;
    private final HashMap<String, List<String>> courses = new HashMap<>();
    private final String[] filter = {"Kursöversikt", "Obligatoriska kurser", "Poäng", "Summa",
            "bokstavsordning", "Sida", "Ansökan", "Diarienummer", "Insänd"};

    /**
     * Initierar filen som skall läsas samt kallar på relevanta metoder för att läsa och hitta kurser.
     * Hämtar även namnet på utbildningen, namnet på institutionen och hur många platser utbildningen har.
     */
    public PDFReader(String path) {
        File file = new File(path);
        read(file);
        extractCourses();

        String name = getText(getLine("Utbildningens namn") + 1);
        System.out.println("Name: " + name);

        String institute = getText(getLine("Ansvarig utbildningsanordnare") + 1);
        System.out.println("Institute: " + institute);

        String slots = getText(getLine("Totalt antal platser") + 1);
        System.out.println("Slots available: " + slots);

        System.out.println("Courses");
        courses.forEach((key, value) -> System.out.println(key + " >> " + value));
    }

    protected HashMap<String, List<String>> getCourses() { return this.courses; }

    /**
     * Hämtar alla kurser genom att bara hämta text i sektionen "Kurser i bokstavsordning".
     * Filtrerar bort onödiga termer som ligger i arrayen "filter" samt punkterna som separerar
     * Dom olika kurserna. Lägger in kurserna samt all information kring dem i en HashMap.
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
                    tempList.clear();
                }
            }
        }
    }

    /**
     * Läser PDF-filen och initierar arrayen "lines" som håller i all text.
     * @param file filen som skall läsas
     */
    private void read(File file) {
        try {
            PDDocument doc = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text  = stripper.getText(doc);
            lines = text.split(System.getProperty("line.separator"));
            doc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hämtar den specifika line som en viss text finns på.
     * @param filter filtret som ska användas
     * @return index i arrayen där texten återfinns
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
     * Hämtar texten från en viss "rad", alltså index i arrayen
     * @param line vilken "rad" som skall hämtas, alltså index
     * @return texten på "raden"
     */
    private String getText(int line) {
        return lines[line];
    }
}