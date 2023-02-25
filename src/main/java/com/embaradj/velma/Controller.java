package com.embaradj.velma;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import static javax.swing.SwingUtilities.isEventDispatchThread;
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

    private JFrame viewFrame;
    private final DataModel model;

    public Controller(DataModel model) {
        this.model = model;
    }

    protected void setView(JFrame viewFrame) {
        this.viewFrame = viewFrame;
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
        if (e.getActionCommand().equals("analyse")) analyse();
        if (e.getActionCommand().equals("settings")) analyse();     // todo
        if (e.getActionCommand().equals("help")) analyse();         // todo
        if (e.getActionCommand().equals("quit")) quit();
    }

    private void analyse() {
        System.out.println("Running on EDT? " + isEventDispatchThread());
        System.out.println(Thread.currentThread().getName());
    }

    private void quit() {

        int userInput = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to quit?",
                "Quit application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null);

        if (userInput == 0) {

            // Close all open forms
            for (Frame frame : viewFrame.getFrames()) {
                frame.dispose();
            }
        }
    }

    public void searchJobs() {

        APIJobStream jobStream = new APIJobStream();

        Observable<JobResults> jobOBs = Observable.create(emitter -> {
            System.out.println(Thread.currentThread().getName());
            for (JobResults result : jobStream.getResults()) emitter.onNext(result);
        });

        jobOBs
                .subscribeOn(Schedulers.io())
                .map(ad -> new Job(ad.getTitle(), ad.getText()))
                .doOnNext(model::addJob)
                .subscribe();
    }

    public void searchHve() {

        // Create instance of Susa API parser
        APISusa susa = new APISusa();

        // Create instance of MYH API parser
        APIMyh myh = new APIMyh();

        Observable<SusaResult.SusaHit> susaObs = Observable.create(emitter -> {

            // Call getResult() in here in order to run it on another thread, subscribeOn
            for (SusaResult.SusaHit hit : susa.getResult().getResults()) emitter.onNext(hit);
        });

        susaObs.subscribeOn(Schedulers.io())
                .subscribe(susaHit -> {
                    String pdfUrl = myh.getPdfUrl(susaHit.getCode());
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

        // todo: update progressbar or something to show when we are finished..

    }


}
