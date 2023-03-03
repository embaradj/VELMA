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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class APIJobStream {
    private DataModel model;
    private PropertyChangeSupport support;
    private List<JobResults> jobResults = new LinkedList<JobResults>() {
    };
    private final String query = "https://jobstream.api.jobtechdev.se/stream?date=2022-01-01T00:01:00";
    private final String prefix = "&occupation-concept-id=";
    private final String[] ssykCodes = {"UXKZ_3zZ_ipB", "DJh5_yyF_hEM", "Q5DF_juj_8do", "D9SL_mtn_vGM", "cBBa_ngH_fCx", "BAeH_eg8_T2d", "UxT1_tPF_Kbg"};
    private int processedJobs = 0;

    public APIJobStream(DataModel model, PropertyChangeSupport support) {
        this.model = model;
        this.support = support;
    }

    public void doSearch() {
        Observable<JobResults> jobOBs = Observable.create(emitter -> {

            for (JobResults result : getResults()) emitter.onNext(result);
        });

        jobOBs
                .subscribeOn(Schedulers.io())
                .map(ad -> new Job(ad.getTitle(), ad.getText()))
                .doOnNext(model::addJob)
                .subscribe();
    }


    private void fetchAds() {
        Gson gson = new Gson();
//        String parameter = Arrays.stream(ssykCodes).map(code -> prefix + code).collect(Collectors.joining());
//        System.out.println(query + parameter);

        processedJobs = 0;
        jobResults.clear();

        try {

            for (String param : ssykCodes) {

                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(new URI(query + prefix + param))
                        .header("Accept", "application/json")
                        .build();

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                updateProgressBar();

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

    public void updateProgressBar() {
        int progress = ((100) * ++processedJobs) / ssykCodes.length;
        support.firePropertyChange("jobProgress", null, progress);
    }
}
