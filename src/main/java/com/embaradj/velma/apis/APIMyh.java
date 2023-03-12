package com.embaradj.velma.apis;

import com.embaradj.velma.Settings;
import com.embaradj.velma.results.MyhSearchRequest;
import com.embaradj.velma.results.MyhSearchResult;
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

        System.out.println("Several hits.. Returning the latest one that is approved:  " + mostRecent.getId() + "..");

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

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            MyhSearchResult searchResult = gson.fromJson(response.body(), MyhSearchResult.class);
            results = searchResult.getResult();

            if (Settings.debug()) {
                int approved = 0;
                int count = 1;
                for (MyhSearchResult.Hit hit : results) {
                    if (hit.isApproved()) approved++;
                    System.out.println(count + ":  " + hit.getId() + "\t" + hit.getSyllabusUrl()
                            + "\t year: " + hit.getYear() + "\t" + ((hit.isApproved()) ? "approved" : "declined"));
                    count++;
                }

                System.out.println("Results: " + results.size() + "\t Approved: " + approved);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;

    }


}
