package com.embaradj.velma;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Objects;

import com.embaradj.velma.apis.APIJobStream;
import com.embaradj.velma.apis.APIMyh;
import com.embaradj.velma.apis.APISusa;
import com.embaradj.velma.lda.Modeller;
import com.embaradj.velma.lda.ToTxt;
import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.models.Hve;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import static javax.swing.SwingUtilities.isEventDispatchThread;

public class Controller implements ActionListener {
    private Settings settings = Settings.getInstance();
    private JFrame view;
    private final DataModel model;
    private int totalHves = 0;
    private int processedHves = 0;
    private boolean searchedHve = false;
    private boolean searchedJobs = false;

    public Controller(DataModel model) {
        this.model = model;
//        apiJobStream = new APIJobStream(model);
    }

    protected void setView(JFrame viewFrame) {
        this.view = viewFrame;
    }

    /**
     * Invoked when a button / object is clicked
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println("Clicked: " + e.getActionCommand());
        if (e.getActionCommand().equals("srcHve")) searchHve();
        if (e.getActionCommand().equals("srcJobs")) searchJobs();
        if (e.getActionCommand().equals("analyse")) analyse();
        if (e.getActionCommand().equals("settings")) settings();
        if (e.getActionCommand().equals("help")) help();
        if (e.getActionCommand().equals("quit")) quit();
    }

    private void help() {
        EventQueue.invokeLater(() -> {
            DetailsForm helpForm = new DetailsForm("Help", settings.getHelpDocument());
        });
    }

    private void settings() {
        EventQueue.invokeLater(() -> {
            SettingsForm settingsForm = new SettingsForm();
        } );
    }

    /**
     * Processes the collected data
     * And starts topic modelling with {@link Modeller}.
     */
    private void analyse() {
        System.out.println("Running on EDT? " + isEventDispatchThread());
        System.out.println(Thread.currentThread().getName());
        File file = new File("resources/rawdata/");
//        Path data = Path.of("resources/processeddata/data.mallet");

        Modeller modeller = new Modeller();

        modeller.worker(file);
        modeller.saveModel();
    }

    private void quit() {
        if (confirmYesNo("Quit application?","Are you sure you want to quit?")) {
            for (Frame frame : view.getFrames()) frame.dispose();
        }
    }

    public void searchJobs() {
        if (searchedJobs) {
            if (!confirmYesNo("Search again?","Are you sure you want to download Job ads again?")) return;
        }

        searchedJobs = true;
        model.clearJob();
        APIJobStream.doSearch(model);
    }

    public void searchHve() {

        if (searchedHve) {
            if (!confirmYesNo("Search again?", "Are you sure you want to download HVEs again?")) return;
        }

        searchedHve = true;
        model.clearHve();

        // The dataModel instance is needed for the Susa Nav to report back total number of hits
        APISusa.getObservableResult(model)
            .subscribeOn(Schedulers.io())
            .subscribe(susaHit -> {

                // Use the HVE code retrieved from Susa API to search in MYH API
                String pdfUrl = APIMyh.getPdfUrl(susaHit.getCode());
                String localFilePath = FileDownloader.download(pdfUrl);

                // If localFilePath is not null means successful download
                if (!Objects.isNull(localFilePath)) {
                    PDFReader pdfReader = new PDFReader(localFilePath);
                    Hve hve = new Hve(susaHit.getCode(), susaHit.getTitle(), pdfReader.getCourses(), pdfReader.getFullText(), pdfReader.getPartText());
                    new ToTxt(hve.getType(), hve.getCode(), hve.getPartText());
                    model.addHve(hve);
                }
                else {
                    System.out.println("Could not download pdf " + susaHit);
                }

                model.updateProgressBarHve(true);

                System.out.println(susaHit + "\n" + pdfUrl + "\n----------------");
            }, error -> {
                error.printStackTrace();
            });


    }

    private boolean confirmYesNo(String title, String question) {
        int userInput = JOptionPane.showConfirmDialog(
                null,
                question,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null
        );

        if (userInput == 0) return true;   // YES

        return false;
    }

}
