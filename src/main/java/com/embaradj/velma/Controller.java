package com.embaradj.velma;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import static java.lang.System.out;

import com.embaradj.velma.apis.APIJobStream;
import com.embaradj.velma.apis.APISusa;
import com.embaradj.velma.apis.APIMyh;
import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.models.Hve;
import com.embaradj.velma.models.Job;
import com.embaradj.velma.results.JobResults;
import com.embaradj.velma.results.SusaResult;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;

public class Controller implements ActionListener {

//    private final MainForm rootframe;
    private final JFrame rootframe;
    private final DataModel model;

    public Controller(DataModel model, JFrame rootFrame) {
        this.rootframe = rootFrame;
        this.model = model;
    }

    /**
     * Invoked when a button / object is clicked
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Clicked: " + e.getActionCommand());
        if (e.getActionCommand().equals("srcHve")) searchHve();
        if (e.getActionCommand().equals("srcJobs")) searchJobs();
    }

    public void searchJobs() {
        out.println("search jobs button clicked");
        APIJobStream jobStream = new APIJobStream();

        Observable<JobResults> jobOBs = Observable.create(emitter -> {
            for (JobResults result : jobStream.getResults()) emitter.onNext(result);
        });

        jobOBs.subscribeOn(Schedulers.io())
                .map(ad -> new Job(ad.getTitle(), ad.getText()))
                .doOnNext(model::addJob)
                .subscribe();
    }

    public void searchHve() {
        out.println("button clicked");

        // Create instance of Susa API parser and fetch all HVE's
        APISusa susa = new APISusa();

        // Create instance of MYH API parser
        APIMyh myh = new APIMyh();


/*
        // For each found HVE in Susa use the MYH API to fetch the URL of corresponding syllabus PDF
        List<SusaResult.SusaHit> susaHits = susa.getResult().getResults();

        int downloaded = 0;
        for (SusaResult.SusaHit susaHit : susaHits) {

            // Get the URL of the PDF
            String pdfUrl = myh.getPdfUrl(susaHit.getCode());
            System.out.println(susaHit + "\n" + pdfUrl + "\n----------------");
//            susaHit.setPdfUrl(pdfUrl);
//            FileDownloader.download(susaHit.getPdfUrl());
//            if (!Objects.isNull(FileDownloader.download(pdfUrl))) {
            String localFilePath = FileDownloader.download(pdfUrl);

            if (!Objects.isNull(localFilePath)) {
                downloaded++;
                PdfReader pdfReader = new PdfReader(localFilePath);
                Hve hve = new Hve(susaHit.getCode(), susaHit.getTitle(), pdfReader.getCourses());
                model.addHve(hve);
            }
            else {
                System.out.println("could not download pdf " + susaHit);
            }

        }
*/
//        System.out.println("Susanav hits: " + susaHits.size() + "\nDownloaded " + downloaded + " PDF's");



//        Observable.fromIterable(susa.getResult().getResults())
//                .subscribeOn(Schedulers.io())
//                .subscribe(susaHit -> {
//                    String pdfUrl = myh.getPdfUrl(susaHit.getCode());
//                    susaHit.setPdfUrl(pdfUrl);
//                    System.out.println(susaHit + "\n" + pdfUrl + "\n----------------");
//                });


        Observable<SusaResult.SusaHit> susaObs = Observable.create(emitter -> {
            for (SusaResult.SusaHit hit : susa.getResult().getResults()) emitter.onNext(hit);
        });

        susaObs.subscribeOn(Schedulers.io())
                .subscribe(susaHit -> {
                    String pdfUrl = myh.getPdfUrl(susaHit.getCode());
//                    susaHit.setPdfUrl(pdfUrl);

                    String localFilePath = FileDownloader.download(pdfUrl);

                    if (!Objects.isNull(localFilePath)) {
                        PDFReader pdfReader = new PDFReader(localFilePath);
                        Hve hve = new Hve(susaHit.getCode(), susaHit.getTitle(), pdfReader.getCourses());
                        model.addHve(hve);
                    }
                    else {
                        System.out.println("could not download pdf " + susaHit);
                    }

                    System.out.println(susaHit + "\n" + pdfUrl + "\n----------------");
                });



        System.out.println("Finished");

    }

}
