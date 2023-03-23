package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.models.SearchHitWrapper;
import com.embaradj.velma.results.SearchHit;
import javax.swing.*;
import java.awt.*;

/**
 * Represents the view of the MVC pattern
 */
public class MainForm extends JFrame {

    private DataModel model;
    protected JPanel panel1;
    private JButton srcHveBtn;
    private JButton srcJobsBtn;
    private JButton settingsBtn;
    private JButton analyseBtn;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JButton quitBtn;
    private JButton helpBtn;
    private JScrollPane scrollPaneLeft;
    private JScrollPane scrollPaneRight;
    private DefaultListModel<SearchHit> listModel1 = new DefaultListModel<>(); // Used for the JLists
    private DefaultListModel<SearchHit> listModel2 = new DefaultListModel<>(); // Used for the JLists
    private JList list1;
    private JList list2;
    private Controller controller;
    private Settings settings = Settings.getInstance();

    public MainForm(Controller controller, DataModel model) {
        this.controller = controller;
        this.model = model;
        setTitle("VELMA " + Settings.VERSION);
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);    // Position the frame in the center of the screen
        setVisible(true);

        setListeners();
        controller.setView(this);
        addActionListeners();

        list1.setModel(listModel1);
        list2.setModel(listModel2);

        progressBar1.setStringPainted(true);
        progressBar2.setStringPainted(true);

    }

    /**
     * Let the controller setup listeners for the components
     */
    protected void addActionListeners() {
        srcHveBtn.addActionListener(controller);
        srcJobsBtn.addActionListener(controller);
        analyseBtn.addActionListener(controller);
        settingsBtn.addActionListener(controller);
        quitBtn.addActionListener(controller);
        helpBtn.addActionListener(controller);

        list1.addMouseListener(new CustomMouseAdapter(list1));
        list2.addMouseListener(new CustomMouseAdapter(list2));
    }

    /**
     * Listen to changes in the Model and update GUI accordingly..
     */
    private void setListeners() {
        model.addListener(e -> {

            // Update the GUI on the EDT thread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
/*
                    // Resets a search
                    if (e.getNewValue() == null) {
                        if (e.getPropertyName().equals("hve")) {
                            listModel1.clear();
                            progressBar1.setValue(0);
                            progressBar1.setString("");
                        }
                        if (e.getPropertyName().equals("job")) {
                            listModel2.clear();
                            progressBar2.setValue(0);
                            progressBar2.setString("");
                        }
                        return;
                    }

 */


                    if (e.getNewValue() instanceof SearchHitWrapper) {

                        SearchHitWrapper searchHitWrapper = ((SearchHitWrapper) e.getNewValue());

                        // Check for reset
                        if (searchHitWrapper.isReset()) {
                            if (searchHitWrapper.getType().equals("hve")) {
                                listModel1.clear();
                                progressBar1.setValue(0);
                                progressBar1.setString("Searching..");
                            }
                            else if (searchHitWrapper.getType().equals("job")) {
                                listModel2.clear();
                                progressBar2.setValue(0);
                                progressBar2.setString("Searching..");
                            }
                            return;
                        }

                        // Just started a search
//                        if (searchHitWrapper.isJustStarted()) {
//                            if (searchHitWrapper.getType().equals("hve")) progressBar1.setString("Searching..");
//                            if (searchHitWrapper.getType().equals("job")) progressBar2.setString("Searching..");
//                            return;
//                        }

                        SearchHit searchHit = searchHitWrapper.getSearchHit();

                        int progress = 0;
                        if (searchHitWrapper.getTotal() > 0) progress = 100 * (searchHitWrapper.getProcessed()) / searchHitWrapper.getTotal();
                        boolean finished = (progress == 100);
                        String progressText = "";
                        if (!finished) progressText = "Downloading " + progress + "%";

                        if (searchHit.getType().equals("hve")) {
                            listModel1.addElement(searchHit);
                            if (finished) progressText = "Finished downloading " + searchHitWrapper.getTotal() + " HVEs";
                            progressBar1.setValue(progress);
                            progressBar1.setString(progressText);
                            srcHveBtn.setEnabled(finished);
                            return;
                        }

                        if (searchHit.getType().equals("job")) {
                            listModel2.addElement(searchHit);
                            if (finished) progressText = "Finished downloading " + searchHitWrapper.getTotal() + " job ads";
                            progressBar2.setValue(progress);
                            progressBar2.setString(progressText);
                            srcJobsBtn.setEnabled(finished);
                            return;
                        }

                    }

                    /*
                    if (e.getNewValue() instanceof SearchHit) {
                        SearchHit searchHit = (SearchHit) e.getNewValue();
                        if (e.getPropertyName().equals("hve")) listModel1.addElement(searchHit);
                        if (e.getPropertyName().equals("job")) listModel2.addElement(searchHit);
                        return;
                    }

                    if (e.getNewValue() instanceof Integer) {
                        int progress = (int) e.getNewValue();
//                        String progressText = (progress >= 100) ? "Download complete" : "Downloading " + progress + "%";
                        String progressText = "Downloading " + progress + "%";
                        boolean finished = false;

                        if (progress >= 100) {
                            //progressText = "Download complete";
                            finished = true;
                        } else { progressText = "Downloading " + progress + "%"; }

                        if (e.getPropertyName().equals("hveProgress")) {
                            if (finished) progressText = "Finished downloading " + listModel1.size() + " HVEs";
                            progressBar1.setValue(progress);
                            progressBar1.setString(progressText);
                            srcHveBtn.setEnabled(finished);
                        }

                        if (e.getPropertyName().equals("jobProgress")) {
                            if (finished)  progressText = "Finished downloading " + listModel2.size() + " Jobs ads";
                            progressBar2.setValue(progress);
                            progressBar2.setString(progressText);
                            srcJobsBtn.setEnabled(finished);
                        }
                    }


                     */
                }
            });
        });


    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setPreferredSize(new Dimension(800, 500));
        scrollPaneLeft = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(scrollPaneLeft, gbc);
        list1 = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        list1.setModel(defaultListModel1);
        list1.setSelectionMode(0);
        scrollPaneLeft.setViewportView(list1);
        srcHveBtn = new JButton();
        srcHveBtn.setActionCommand("srcHve");
        srcHveBtn.setText("Search Curriculum");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 2, 2, 1);
        panel1.add(srcHveBtn, gbc);
        progressBar1 = new JProgressBar();
        progressBar1.setString("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 2, 0, 1);
        panel1.add(progressBar1, gbc);
        progressBar2 = new JProgressBar();
        progressBar2.setString("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 1, 0, 2);
        panel1.add(progressBar2, gbc);
        scrollPaneRight = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(scrollPaneRight, gbc);
        list2 = new JList();
        list2.setSelectionMode(0);
        scrollPaneRight.setViewportView(list2);
        srcJobsBtn = new JButton();
        srcJobsBtn.setActionCommand("srcJobs");
        srcJobsBtn.setText("Search Job Ads");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 1, 2, 2);
        panel1.add(srcJobsBtn, gbc);
        analyseBtn = new JButton();
        analyseBtn.setActionCommand("analyse");
        analyseBtn.setText("Analyse");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 1, 2, 1);
        panel1.add(analyseBtn, gbc);
        settingsBtn = new JButton();
        settingsBtn.setActionCommand("settings");
        settingsBtn.setText("Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 1, 2, 1);
        panel1.add(settingsBtn, gbc);
        helpBtn = new JButton();
        helpBtn.setActionCommand("help");
        helpBtn.setText("Help");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 1, 2, 1);
        panel1.add(helpBtn, gbc);
        quitBtn = new JButton();
        quitBtn.setActionCommand("quit");
        quitBtn.setText("Quit");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 1, 2, 2);
        panel1.add(quitBtn, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
