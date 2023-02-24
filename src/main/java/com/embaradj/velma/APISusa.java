package com.embaradj.velma;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APISusa {
    final String searchUrl = "https://susanavet2.skolverket.se/api/1.1/infos?configuration=program&degree=true&organisationForm=yrkesh%C3%B6gskoleutbildning&subjectIds=395&size=1000";
    private SusaResult searchResult;

    protected APISusa() {}

    private void fetchPrograms() {
        Gson gson = new Gson();

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(this.searchUrl))
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            searchResult = gson.fromJson(response.body(), SusaResult.class);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected SusaResult getResult() {
        fetchPrograms();
        return this.searchResult;
    }


}