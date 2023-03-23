package com.embaradj.velma.apis;

import com.embaradj.velma.Settings;
import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.results.SusaResult;
import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Observable;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

public class APISusa {

    public static SusaResult getResult(DataModel model) {
        Gson gson = new Gson();

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(Settings.getSusaApiUri()))
                    .header("Content-Type", "application/json")
                    .build();

//            HttpClient httpClient = HttpClient.newHttpClient();
            HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() > 299) {
                //JOptionPane.showMessageDialog(null, "There was a problem with the SusaNav API.\nHTTP error " + response.statusCode());
                if (Settings.getInstance().confirmYesNo("Connection issue", "There was a problem with the Susanav API." + "\nResponse code: " + response.statusCode() + "\nTry again?")) {
//                    model.clearHve();
                    return getResult(model);
                }
                return null;
            }

            SusaResult susaResult = gson.fromJson(response.body(), SusaResult.class);

            // Notify the model about total number of HVE's found, in order to calculate the progressbar
            //model.setTotalHves(susaResult.getResults().size());
            model.setTotalHits("hve", susaResult.getResults().size());
            // todo: fixa så baren uppdateras vid sökning nr 2
            //model.updateProgressBarHve(false);

            return susaResult;

        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "There was a problem with the SusaNav API.\nSee the console for details.");
            if (Settings.getInstance().confirmYesNo("Connection issue", "There was a problem with the SusaNav API.\nTry again?")) {
//                model.clearHve();
                return getResult(model);
            }
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