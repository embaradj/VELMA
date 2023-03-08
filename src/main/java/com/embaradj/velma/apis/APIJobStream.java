package com.embaradj.velma.apis;

import com.embaradj.velma.Settings;
import com.embaradj.velma.lda.ToTxt;
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
import java.util.List;

/**
 * Responsible for querying the JobStream API for job ads.
 */
public class APIJobStream {
    private Settings settings = Settings.getInstance();
    private DataModel model;
    private PropertyChangeSupport support;
    private List<JobResults> jobResults;
    private final String query = "https://jobstream.api.jobtechdev.se/stream?date=2022-01-01T00:01:00";
    private final String prefix = "&occupation-concept-id=";
    private int processedJobs = 0;
    private boolean searched = false;

    public APIJobStream(DataModel model, PropertyChangeSupport support) {
        this.model = model;
        this.support = support;
    }

    /**
     * Whether a search has been done already
     * @return true if searched
     */
    public boolean searched() { return this.searched; }

    /**
     * Execute the search and create a new {@link Job} for each ad
     * And add the job to the {@link DataModel} and save to file {@link ToTxt}.
     */
    public void doSearch() {
        this.model.clearJob();
        searched = true;
        Observable<JobResults> jobOBs = Observable.create(emitter -> {
            getResults().forEach(emitter::onNext);
        });

        jobOBs
            .subscribeOn(Schedulers.io())
            .map(ad -> new Job(ad.getId(), ad.getTitle(), ad.getText()))
            .doOnNext(ad -> {
                model.addJob(ad);
                new ToTxt(ad.getType(), ad.getId(), ad.getDescription());
            })
            .subscribe();
    }

    /**
     * Query the API and save results.
     */
    private void fetchAds() {
        Gson gson = new Gson();

        processedJobs = 0;
        updateProgressBar(false);
        jobResults = new ArrayList<>();

        try {
            for (String param : settings.getSelectedSsyk()) {
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

    /**
     * Getter for results, will call {@link #fetchAds()}
     * And {@link #filter()} which will remove inactive ads.
     * @return results
     */
    public List<JobResults> getResults() {
        fetchAds();
        filter();
        return jobResults;
    }

    /**
     * Removes inactive ads which lacks any job description.
     */
    private void filter() {
        jobResults = jobResults.stream().filter(JobResults::isRemoved).toList();
    }

    /**
     * Updates the view's progress bar.
     * @param increase
     */
    public void updateProgressBar(boolean increase) {
        if (increase) processedJobs++;
        int progress = ((100) * processedJobs) / settings.getSelectedSsyk().length;
        support.firePropertyChange("jobProgress", null, progress);
    }
}
