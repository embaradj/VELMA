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

import javax.swing.*;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Responsible for querying the JobStream API for job ads.
 */
public class APIJobStream {
//    private Settings settings = Settings.getInstance();
    private static List<JobResults> jobResults;


    /**
     * Execute the search and create a new {@link Job} for each ad
     * And add the job to the {@link DataModel} and save to file {@link ToTxt}.
     */
    public static void doSearch(DataModel model) {
        model.clearJob();

        Observable<JobResults> jobOBs = Observable.create(emitter -> {
            getResults(model).forEach(emitter::onNext);
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
    private static void fetchAds(DataModel model) {

        String[] ssykCodes = Settings.getSelectedSsyk();

        if (ssykCodes.length < 1) {
            JOptionPane.showMessageDialog(
                    null,
                    "No SSYK codes are selected!\nGo to the settings and select at least one SSYK code"
            );
            return;
        }

        Gson gson = new Gson();

        // Check the number of SSYK codes that are selected
        model.setTotalJobs(Settings.getSelectedSsyk().length);
        model.updateProgressBarJob(false);

        jobResults = new ArrayList<>();

        try {
            for (String param : ssykCodes) {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(new URI(Settings.getJobStreamUri() + param))
                        .header("Accept", "application/json")
                        .build();

                if (Settings.debug()) System.out.println("URI: " + Settings.getJobStreamUri() + param);

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                jobResults.addAll(gson.fromJson(response.body(), new TypeToken<List<JobResults>>() {}.getType()));
                model.updateProgressBarJob(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for results, will call {@link #fetchAds(DataModel model)}
     * And {@link #filter()} which will remove inactive ads.
     * @return results
     */
    public static List<JobResults> getResults(DataModel model) {
        fetchAds(model);
        filter();
        return jobResults;
    }

    /**
     * Removes inactive ads which lacks any job description
     * And filter ads based on the chosen language.
     */
    private static void filter() {
        String lang = Arrays.toString(Settings.getSelectedLang()).replaceAll("[\\[\\]]", "");
        jobResults = jobResults
                .stream()
                .filter(JobResults::isRemoved)
                .filter(ad -> {
                    long counter = Pattern
                            .compile("\\w*[åäöÅÄÖ]\\w*\\b")
                            .matcher(ad.getText())
                            .results()
                            .count();

                    int wordCount = ad.getText().split("[?!^.*A-Za-zåäöÅÄÖ/]+").length;

                    if (lang.matches("Swedish")) {
                        return ((double) counter * 100 / wordCount) > 5.0;
                    } if (lang.matches("English")) {
                        return ((double) counter * 100 / wordCount) < 5.0;
                    } else {
                        return true;
                    }
                })
                .toList();
    }

}
