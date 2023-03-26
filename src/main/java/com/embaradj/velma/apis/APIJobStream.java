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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Responsible for querying the JobStream API for job ads.
 */
public class APIJobStream {
    private static List<JobResults> jobResults;

    /**
     * Execute the search and create a new {@link Job} for each ad.
     * Checks the language of each ad and add the job to the {@link DataModel}
     * And save to file {@link ToTxt}.
     */
    public static void doSearch(DataModel model) {
        Observable<JobResults> jobOBs = Observable.create(emitter -> {
            getResults(model).forEach(emitter::onNext);
        });

        jobOBs
                .subscribeOn(Schedulers.io())
                .map(ad -> {
                    String lang = checkLang(ad) ? "sv" : "en";
                    return new Job(ad.getId(), ad.getTitle(), ad.getText(), lang);
                })
                .doOnNext(ad -> {
                    model.addAndUpdate(ad);
                    new ToTxt(ad);
                })
                .subscribe();
    }

    /**
     * Query the API and save results.
     */
    private static void fetchAds() {

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
        //model.setTotalHits("job", Settings.getSelectedSsyk().length);

        //model.updateProgressBarJob(false);
        //todo: fixa

        jobResults = new ArrayList<>();

        try {
            for (String param : ssykCodes) {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .timeout(Duration.ofSeconds(10))
                        .uri(new URI(Settings.getJobStreamUri() + param))
                        .header("Accept", "application/json")
                        .build();

                if (Settings.debug()) System.out.println("URI: " + Settings.getJobStreamUri() + param);

                HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() < 200 || response.statusCode() > 299) {

                    if (Settings.getInstance().confirmYesNo("Connection issue", "There was a problem with the JobStream API." + "\nResponse code: " + response.statusCode() + "\nTry again?")) {
                        jobResults.clear();
                        fetchAds();
                    }

                    return;
                }

                jobResults.addAll(gson.fromJson(response.body(), new TypeToken<List<JobResults>>() {}.getType()));
                //model.updateProgressBarJob(true);
            }

        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "There was a problem with the JobStream API.\nSee the console for details.");

            if (Settings.getInstance().confirmYesNo("Connection issue", "There was a problem with the JobStream API.\nTry again?")) {
                jobResults.clear();
                fetchAds();
            }

            e.printStackTrace();
        }
    }

    /**
     * Getter for results, will call {@link #fetchAds()}
     * And {@link #filter()} which will remove inactive ads.
     * @return results
     */
    public static List<JobResults> getResults(DataModel model) {
        fetchAds();
        filter();
        model.setTotalHits("job", jobResults.size());
        return jobResults;
    }

    /**
     * Removes inactive ads which lacks any job description
     */
    private static void filter() {
        jobResults = jobResults
                .stream()
                .filter(JobResults::isRemoved)
                .toList();

    }

    /**
     * Checks if the language of the ad is Swedish
     * @param job description to check
     * @return true if Swedish
     */
    private static boolean checkLang(JobResults job) {
        long counter = Pattern
                .compile("\\w*[åäöÅÄÖ]\\w*\\b")
                .matcher(job.getText())
                .results()
                .count();

        int wordCount = job.getText().split("[?!^.*A-zåäöÅÄÖ/]+").length;

        return ((double) counter * 100 / wordCount) > 5.0;
    }
}
