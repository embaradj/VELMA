package com.embaradj.velma.apis;

import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.models.Job;
import com.embaradj.velma.results.JobResults;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class APIJobStream {
    private DataModel model;
    private PropertyChangeSupport support;
    private List<JobResults> jobResults;
    private final String query = "https://jobstream.api.jobtechdev.se/stream?date=2022-01-01T00:01:00";
    private final String prefix = "&occupation-concept-id=";
    private final String[] ssykCodes = {"UXKZ_3zZ_ipB", "DJh5_yyF_hEM", "Q5DF_juj_8do", "D9SL_mtn_vGM", "cBBa_ngH_fCx", "BAeH_eg8_T2d", "UxT1_tPF_Kbg"};
    private int processedJobs = 0;
    private boolean searched = false;

    public APIJobStream(DataModel model, PropertyChangeSupport support) {
        this.model = model;
        this.support = support;
    }

    /**
     * Whether search has been done already
     * @return
     */
    public boolean searched() { return this.searched; }

    public void doSearch() {
        this.model.clearJob();
        searched = true;
        Observable<JobResults> jobOBs = Observable.create(emitter -> {
            getResults().forEach(emitter::onNext);
        });

        jobOBs
                .subscribeOn(Schedulers.io())
                .map(ad -> new Job(ad.getTitle(), ad.getText()))
                .doOnNext(model::addJob)
                .subscribe();
    }

    private void fetchAds() {
        Gson gson = new Gson();

        processedJobs = 0;
        updateProgressBar(false);
        jobResults = new ArrayList<>();

        try {
            for (String param : ssykCodes) {

                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(new URI(query + prefix + param))
                        .header("Accept", "application/json")
                        .build();

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                updateProgressBar(true);

                jobResults.addAll(gson.fromJson(response.body(), new TypeToken<List<JobResults>>() {}.getType()));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<JobResults> getResults() {
        fetchAds();
        filter();
        return jobResults;
    }

    private void filter() {
        jobResults = jobResults.stream().filter(JobResults::isRemoved).toList();
    }

    public void updateProgressBar(boolean increase) {
        if (increase) processedJobs++;
        int progress = ((100) * processedJobs) / ssykCodes.length;
        support.firePropertyChange("jobProgress", null, progress);
    }
}
