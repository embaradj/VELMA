package com.embaradj.velma.apis;

import com.embaradj.velma.Settings;
import com.embaradj.velma.results.SusaResult;
import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Observable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APISusa {
    private Settings settings = Settings.getInstance();
    private SusaResult searchResult;

    private void fetchPrograms() {
        Gson gson = new Gson();

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(settings.getSusaApiUri()))
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            searchResult = gson.fromJson(response.body(), SusaResult.class);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SusaResult getResult() {
        fetchPrograms();
        return this.searchResult;
    }


}