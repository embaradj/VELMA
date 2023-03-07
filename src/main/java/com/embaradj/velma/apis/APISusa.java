package com.embaradj.velma.apis;

import com.embaradj.velma.results.SusaResult;
import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Observable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APISusa {
    private final boolean DEBUG = true;
//    final String searchUrl = "https://susanavet2.skolverket.se/api/1.1/infos?configuration=program&degree=true&organisationForm=yrkesh%C3%B6gskoleutbildning&subjectIds=395&size=1000";
//final String searchUrl = "https://susanavet2.skolverket.se/api/1.1/infos?configuration=program&degree=true&organisationForm=yrkesh%C3%B6gskoleutbildning&subjectIds=395&size=5";
    private String searchUrl;
    private SusaResult searchResult;

    public APISusa() {
        if (DEBUG) { // Only look up 5 curriculums
            searchUrl = "https://susanavet2.skolverket.se/api/1.1/infos?configuration=program&degree=true&organisationForm=yrkesh%C3%B6gskoleutbildning&subjectIds=395&size=5";
        } else {
            searchUrl = "https://susanavet2.skolverket.se/api/1.1/infos?configuration=program&degree=true&organisationForm=yrkesh%C3%B6gskoleutbildning&subjectIds=395&size=1000";
        }
    }

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

    public SusaResult getResult() {
        fetchPrograms();
        return this.searchResult;
    }


}