package com.embaradj.velma.apis;

import com.embaradj.velma.Settings;
import com.embaradj.velma.results.MyhSearchRequest;
import com.embaradj.velma.results.MyhSearchResult;
import com.google.gson.Gson;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class APIMyh {

    /**
     * Get the URL to the syllabus of a HVE program
     * @param
     * @return A string containing the URL
     */
    public static String getPdfUrl(String query) {
        List<MyhSearchResult.Hit> hits = search(query);

        if (hits.isEmpty()) {
            System.out.println("Could not get a PDF Url for query" + query);
            return null;
        }

        if (hits.size() == 1 && hits.get(0).isApproved()) return hits.get(0).getSyllabusUrl();

        // If there are several results, get the latest approved
        int maxYear = 0;
        MyhSearchResult.Hit mostRecent = null;

        for (MyhSearchResult.Hit hit : hits) {

            if (hit.isApproved() && hit.getYear() > maxYear) {
                maxYear = hit.getYear();
                mostRecent = hit;
            }
        }

        if (Settings.debug()) {
            System.out.println("Several hits.. Returning the latest one that is approved:  " + mostRecent.getId() + "..");
        }

        assert mostRecent != null;
        return mostRecent.getSyllabusUrl();

    }

    /**
     * Searches the myh database for any text. Passing the code from a YH program (code: YHxxxx) seems to give
     * very good results..
     * @param title Any String, but recommended the YHxxxx code
     * @return Results..
     */
    public static List<MyhSearchResult.Hit> search(String title) {
        MyhSearchRequest myhSearch = new MyhSearchRequest(title);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(myhSearch);

        List<MyhSearchResult.Hit> results = new LinkedList<MyhSearchResult.Hit>();

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(Settings.getMyhUri()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            //HttpClient httpClient = HttpClient.newHttpClient();
            HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() > 299) {
//                JOptionPane.showMessageDialog(null, "There was a problem with the JobStream API.\nHTTP error " + response.statusCode());
                if (Settings.getInstance().confirmYesNo("Connection issue", "There was a problem with the MYH API.\n" + response.statusCode() + "Try again?")) {
                    //model.clearHve();
                    return search(title);
                }
                return null;
            }

            MyhSearchResult searchResult = gson.fromJson(response.body(), MyhSearchResult.class);
            results = searchResult.getResult();

            if (Settings.debug()) {
                int approved = 0;
                int count = 1;
                for (MyhSearchResult.Hit hit : results) {
                    if (hit.isApproved()) approved++;
                    if (Settings.debug()) {
                        System.out.println(count + ":  " + hit.getId() + "\t" + hit.getSyllabusUrl()
                                + "\t year: " + hit.getYear() + "\t" + ((hit.isApproved()) ? "approved" : "declined"));
                    }
                    count++;
                }

                if (Settings.debug()) {
                    System.out.println("Results: " + results.size() + "\t Approved: " + approved);
                }
            }


        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "There was a problem with the MYH API.\nSee the console for details.");
            if (Settings.getInstance().confirmYesNo("Connection issue", "There was a problem with the MYH API.\nTry again?")) {
                return search(title);
            }
            e.printStackTrace();
        }

        return results;

    }


}
