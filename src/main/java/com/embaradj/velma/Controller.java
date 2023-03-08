package com.embaradj.velma;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import cc.mallet.types.InstanceList;
import com.embaradj.velma.apis.APIJobStream;
import com.embaradj.velma.apis.APIMyh;
import com.embaradj.velma.lda.Importer;
import com.embaradj.velma.lda.Modeller;
import com.embaradj.velma.models.DataModel;
import javax.swing.*;
import static javax.swing.SwingUtilities.isEventDispatchThread;

public class Controller implements ActionListener {

    private JFrame view;
    private final DataModel model;
    private final APIMyh apiMyh;
    private final APIJobStream apiJobStream;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private int totalHves = 0;
    private int processedHves = 0;

    public Controller(DataModel model) {
        this.model = model;

        // Initiate APIs
        apiMyh = new APIMyh(model, support);
        apiJobStream = new APIJobStream(model, support);
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
        System.out.println("Clicked: " + e.getActionCommand());
        if (e.getActionCommand().equals("srcHve")) searchHve();
        if (e.getActionCommand().equals("srcJobs")) searchJobs();
        if (e.getActionCommand().equals("analyse")) analyse();
        if (e.getActionCommand().equals("settings")) analyse();     // todo
        if (e.getActionCommand().equals("help")) analyse();         // todo
        if (e.getActionCommand().equals("quit")) quit();
    }

    /**
     * Processes the collected data with {@link Importer}
     * And starts modelling the data with {@link Modeller}.
     */
    private void analyse() {
        System.out.println("Running on EDT? " + isEventDispatchThread());
        System.out.println(Thread.currentThread().getName());
        File file = new File("resources/rawdata/");
//        Path data = Path.of("resources/processeddata/data.mallet");

        Modeller modeller = new Modeller();

        modeller.worker(file);


        // Run the importer which will read files in resources/ and create a '.mallet' file
//        Importer importer = new Importer();
//        InstanceList inst = importer.readDir(new File("resources/rawdata/"));
//        inst.save(new File("resources/processeddata/data.mallet"));
        // Run the modeller which will do the topic modelling on the '.mallet' file
//        if (Files.exists(data)) {
//            Modeller modeller = new Modeller();
//            modeller.worker("resources/processeddata/data.mallet");
//        } else {
//            JOptionPane.showMessageDialog(null,
//                    "Could not find any data file to run modelling on!",
//                    "No data file found",
//                    JOptionPane.WARNING_MESSAGE);
//        }
    }

    private void quit() {
        if (confirmYesNo("Quit application?","Are you sure you want to quit?")) {
            for (Frame frame : view.getFrames()) frame.dispose();
        }
    }

    public void searchJobs() {
        if (apiJobStream.searched()) {
            if (confirmYesNo("Search again?","Are you sure you want to download Job ads again?")) apiJobStream.doSearch();
        } else apiJobStream.doSearch();
    }

    public void searchHve() {
        if (apiMyh.searched()) {
            if (confirmYesNo("Search again?", "Are you sure you want to download HVEs again?")) apiMyh.doSearch();
        } else apiMyh.doSearch();
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


    // Used by the View to listen for changes in the Controller (Progressbar)
    public void addListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

}
