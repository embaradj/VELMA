package com.embaradj.velma.apis;

import com.embaradj.velma.FileDownloader;
import com.embaradj.velma.PDFReader;
import com.embaradj.velma.models.Hve;
import com.embaradj.velma.results.MyhSearchRequest;
import com.embaradj.velma.results.MyhSearchResult;
import com.embaradj.velma.results.SusaResult;
import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import com.embaradj.velma.models.DataModel;
import static java.lang.System.out;

public class APIMyh {
    public boolean DEBUG = true;
    private int processedHves = 0;
    private int totalHves = 0;
    private DataModel model;
    private PropertyChangeSupport support;
    private boolean searched = false;

    public APIMyh(DataModel model, PropertyChangeSupport support) {
        this.model = model;
        this.support = support;
    }

    public boolean searched() { return this.searched; }

    public void doSearch() {
        this.model.clearHve();
        this.searched = true;

        // Create instance of Susa API parser in order to get list of HVEs (codes)
        APISusa susa = new APISusa();

        // Everything in here will be run on other thread (subscribeOn)
        Observable<SusaResult.SusaHit> susaObs = Observable.create(emitter -> {
            List<SusaResult.SusaHit> susaResults = susa.getResult().getResults();

            // Progressbar
            processedHves = 0;
            totalHves = susaResults.size();
            updateProgressBar(false);

            // Generate an emission for each HVE found in Susa
            susaResults.forEach(hit -> emitter.onNext(hit));
        });

//        susaObs
//                .subscribeOn(Schedulers.io())
//                .doOnNext(susaHit -> {
//                    String localFilePath = FileDownloader.download(getPdfUrl(susaHit.getCode()));
//                    if (Objects.isNull(localFilePath)) System.out.println("Could not download pdf " + susaHit);
//                    else {
//                        PDFReader pdfReader = new PDFReader(localFilePath);
//                        Hve hve = new Hve(susaHit.getCode(), susaHit.getTitle(), pdfReader.getCourses());
//                        model.addHve(hve);
//                    }
//                    updateProgressBar();
//                })
//                .subscribe(susaHit -> System.out.println("Could not download pdf " + susaHit)
//                        , err -> {err.printStackTrace(); });
        susaObs
            .subscribeOn(Schedulers.io())
            .subscribe(susaHit -> {
                String pdfUrl = getPdfUrl(susaHit.getCode());
                String localFilePath = FileDownloader.download(pdfUrl);

                if (!Objects.isNull(localFilePath)) {
                    PDFReader pdfReader = new PDFReader(localFilePath);
                    Hve hve = new Hve(susaHit.getCode(), susaHit.getTitle(), pdfReader.getCourses());
                    model.addHve(hve);
                }
                else {
                    System.out.println("Could not download pdf " + susaHit);
                }

                updateProgressBar(true);

                System.out.println(susaHit + "\n" + pdfUrl + "\n----------------");
            }, error -> {
                error.printStackTrace();
            });


    }

    /**
     * Get the URL to the syllabus of a HVE program
     * @param
     * @return A string containing the URL
     */
    public String getPdfUrl(String query) {
        List<MyhSearchResult.Hit> hits = search(query);

        if (hits.isEmpty()) {
            System.out.println("Could not get a PDF Url for query " + query);
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
    protected List<MyhSearchResult.Hit> search(String title) {
        MyhSearchRequest myhSearch = new MyhSearchRequest(title);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(myhSearch);
        out.println("Searching for " + title);

        List<MyhSearchResult.Hit> results = new LinkedList<MyhSearchResult.Hit>();

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://w3d3-integration-service.myh.se/1.0/search"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            MyhSearchResult searchResult = gson.fromJson(response.body(), MyhSearchResult.class);
            results = searchResult.getResult();

            if (DEBUG) {
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

    /**
     * Ask the GUI to update a progressbar
     * @param increase Whether the number of processed elements should be increased (false to reset)
     */
    public void updateProgressBar(boolean increase) {
        int progress = 0;
        if (increase) processedHves++;
        if (totalHves > 0) progress = ((100) * processedHves) / totalHves;
        support.firePropertyChange("hveProgress", null, progress);
    }
}
