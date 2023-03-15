package com.embaradj.velma;

import java.awt.*;
import java.awt.event.*;
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

public class Controller implements ActionListener {
    private final Settings settings = Settings.getInstance();
    private JFrame view;
    private final DataModel model;

    public Controller(DataModel model) {
        this.model = model;
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
            new DetailsForm("Help", settings.getHelpDocument());
        });
    }

    private void settings() {
        EventQueue.invokeLater(() -> {
            new SettingsForm();
        } );
    }

    /**
     * Processes the collected data
     * And starts topic modelling with {@link Modeller}.
     */
    private void analyse() {
        if (!model.searchedHve() || !model.searchedJobs()) {
            JOptionPane.showMessageDialog(null, "You must run a HVE- and a Jobs search first!");
            return;
        }
        model.clearLDATopics();
        ImageIcon icon = new ImageIcon("resources/conf/load.gif");
        JLabel iconLabel = new JLabel(icon);
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new BorderLayout());
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        JOptionPane pane = new JOptionPane();
        pane.setMessage(iconPanel);
        pane.setOptionType(JOptionPane.DEFAULT_OPTION);
        pane.setMessageType(JOptionPane.PLAIN_MESSAGE);
        pane.setOptions(new Object[] { });
        File file = new File("resources/rawdata/");
        Dialog dia = pane.createDialog(null ,"Please wait");
        Modeller modeller = new Modeller(model);

        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                dia.setVisible(true);
            });

            modeller.worker(file);
            modeller.saveModel();

            SwingUtilities.invokeLater(() -> {
                dia.setVisible(false);
                new DetailsForm(model.getLDATopics());
            });
        }).start();
    }

    private void quit() {
        if (confirmYesNo("Quit application?","Are you sure you want to quit?")) {
            for (Frame frame : view.getFrames()) frame.dispose();
        }
    }

    public void searchJobs() {
        if (model.searchedJobs()) {
            if (!confirmYesNo("Search again?","Are you sure you want to download Job ads again?")) return;
        }

        model.clearJob();
        APIJobStream.doSearch(model);
    }

    public void searchHve() {
        if (model.searchedHve()) {
            if (!confirmYesNo("Search again?", "Are you sure you want to download HVEs again?")) return;
        }

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

                if (Settings.debug()) {
                    System.out.println(susaHit + "\n" + pdfUrl + "\n----------------");
                }
            }, Throwable::printStackTrace);


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

        return (userInput == 0);   // YES

    }

}
