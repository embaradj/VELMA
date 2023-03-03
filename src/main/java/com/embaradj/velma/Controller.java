package com.embaradj.velma;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.embaradj.velma.apis.APIJobStream;
import com.embaradj.velma.apis.APIMyh;
import com.embaradj.velma.models.DataModel;
import javax.swing.*;
import static javax.swing.SwingUtilities.isEventDispatchThread;

public class Controller implements ActionListener {

    private JFrame viewFrame;
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

            // Close all open frames
            for (Frame frame : viewFrame.getFrames()) {
                frame.dispose();
            }
        }
    }

    public void searchJobs() {
        apiJobStream.doSearch();
    }

    public void searchHve() {
        apiMyh.doSearch();
    }

    // Used by the View to listen for changes in the Controller (Progressbar)
    public void addListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

}
