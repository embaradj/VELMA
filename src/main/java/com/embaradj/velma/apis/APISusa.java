package com.embaradj.velma.apis;

import com.embaradj.velma.Settings;
import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.results.SusaResult;
import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Observable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class APISusa {

    public static SusaResult getResult(DataModel model) {
        Gson gson = new Gson();

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(Settings.getSusaApiUri()))
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            SusaResult susaResult = gson.fromJson(response.body(), SusaResult.class);

            // Notify the model about total number of HVE's found, in order to calculate the progressbar
            model.setTotalHves(susaResult.getResults().size());
            model.updateProgressBarHve(false);
            return susaResult;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Observable<SusaResult.SusaHit> getObservableResult(DataModel model) {
        return Observable.create((emitter) -> {
            Objects.requireNonNull(getResult(model)).getResults().forEach(emitter::onNext);
        });
    }


}