package com.embaradj.velma;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsForm extends JFrame {
    private Settings settings = Settings.getInstance();
    private JPanel mainPanel;
    private JPanel jobLangPanel;
    private JPanel analyserSettingsPanel;
    private JPanel jobSsykPanel;
    private JPanel buttonPanel;
    private JSpinner alphaSpinner, betaSpinner, iterationsSpinner, threadsSpinner, topicsSpinner;
    private HashMap<String, JCheckBox> langCheckBoxes = new HashMap<>();
    private HashMap<Ssyk, JCheckBox> ssykCheckBoxes = new HashMap<>();

    public SettingsForm() {
        $$$setupUI$$$();
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);    // Position the frame in the center of the screen
        setVisible(true);

        createSsykCheckboxes();
        createLangCheckboxes();
        createAnalyserOptions();
        createButtons();
    }

    private void createSsykCheckboxes() {

//        settings.getSsyk().forEach((Ssyk ssyk) -> {
//            String checkText = ssyk.getCode() + "   " + ssyk.getDescription();
//            JCheckBox checkBox = new JCheckBox(checkText, ssyk.isSelected());
//            checkBox.addItemListener(ie -> {
//                settings.selectSsyk(ssyk, (ie.getStateChange() == 1) ? true : false);
//                if (settings.getSelectedSsyk().length == 0) {
//                    showWarning("You must select at least one SSYK code!");
//                }
//            });
//
//            ssykCheckBoxes.put(ssyk, checkBox); // Save these we later can check wheter they are checked
//            jobSsykPanel.add(checkBox);
//
//        });

        int row = 0;
        for (Ssyk ssyk : settings.getSsyk()) {
            JCheckBox checkbox = new JCheckBox(ssyk.getCode(), ssyk.isSelected());
            JLabel label = new JLabel(ssyk.getDescription());
            putRowInGrid(row, jobSsykPanel, checkbox, label);
            ssykCheckBoxes.put(ssyk, checkbox); // Save these for later so we can check whether they are checked
            row++;
        }
    }

    private void createLangCheckboxes() {
        settings.getLang().forEach((lang, sel) -> {
            JCheckBox checkbox = new JCheckBox(lang, sel);
//            checkbox.addItemListener(ie -> {
//                settings.selectLang(lang, (ie.getStateChange() == 1) ? true : false);
//                if (settings.getSelectedLang().length == 0) {
//                    showWarning("You must select at least one language!");
//                }
//            });

//            langCheckBoxes.add(checkbox);  // Save these we later can check wheter they are checked
            langCheckBoxes.put(lang, checkbox);
            jobLangPanel.add(checkbox);
        });
    }

    private void createAnalyserOptions() {

        // Alpha
        SpinnerNumberModel alphaSpinnerModel = new SpinnerNumberModel(settings.getAlpha(), 0.01, 100, 0.01);
        alphaSpinner = new JSpinner(alphaSpinnerModel);
        alphaSpinner.setEditor(new JSpinner.NumberEditor(alphaSpinner, "0.00"));

        JLabel alphaLabel = new JLabel("alpha");
        alphaLabel.setLabelFor(alphaSpinner);

        // Beta
        SpinnerNumberModel betaSpinnerModel = new SpinnerNumberModel(settings.getBeta(), 0.01, 100, 0.01);
        betaSpinner = new JSpinner(betaSpinnerModel);
        betaSpinner.setEditor(new JSpinner.NumberEditor(betaSpinner, "0.00"));
        String betaToolTipText = "High beta = Each topic is more likely to contain " +
                "a mixture of words\nLow beta = Each topic may contain a mixture of only a few words";
        betaSpinner.setToolTipText(betaToolTipText);
        JLabel betaLabel = new JLabel(betaToolTipText);
        betaLabel.setLabelFor(betaSpinner);
        betaLabel.setToolTipText(betaToolTipText);

        // Number of topics
        SpinnerNumberModel topicsSpinnerModel = new SpinnerNumberModel(settings.getNumTopics(), 1, 10000, 1);
        topicsSpinner = new JSpinner(topicsSpinnerModel);
        JLabel topicsLabel = new JLabel("Number of topics");
        topicsLabel.setLabelFor(topicsSpinner);

        // Number of threads
        SpinnerNumberModel threadsSpinnerModel = new SpinnerNumberModel(settings.getThreads(), 1, 256, 1);
        threadsSpinner = new JSpinner(threadsSpinnerModel);
        JLabel threadsLabel = new JLabel("Number of threads");
        threadsLabel.setLabelFor(threadsSpinner);

        // Number of iterations
        SpinnerNumberModel iterationsSpinnerModel = new SpinnerNumberModel(settings.getIterations(), 1, 1000000, 100);
        iterationsSpinner = new JSpinner(iterationsSpinnerModel);
        iterationsSpinner.setEditor(new JSpinner.NumberEditor(iterationsSpinner, "#"));
        JLabel iterationsLabel = new JLabel("Number of iterations");
        iterationsLabel.setLabelFor(iterationsSpinner);

        // Put everything on the Panel
        putRowInGrid(0, analyserSettingsPanel, alphaSpinner, alphaLabel);
        putRowInGrid(1, analyserSettingsPanel, betaSpinner, betaLabel);
        putRowInGrid(2, analyserSettingsPanel, topicsSpinner, topicsLabel);
        putRowInGrid(3, analyserSettingsPanel, threadsSpinner, threadsLabel);
        putRowInGrid(4, analyserSettingsPanel, iterationsSpinner, iterationsLabel);

    }

    /**
     * Puts a component followed by a label in a GridBagLayout pane
     * @param row
     * @param container
     * @param component
     * @param label
     */
    private void putRowInGrid(int row, Container container, Component component, JLabel label) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = row;
        container.add(component, c);

        c.weightx = 1;
        c.gridx = 1;
        container.add(label, c);

        label.setLabelFor(component);
    }

    private void createButtons() {
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener((l) -> {
            if (checkSettings()) saveSettings();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener((l) -> dispose());

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
    }

    /**
     * Check if input is legal
     *
     * @return Whether input legal
     */
    private boolean checkSettings() {

        int checked = 0;

        // Check that at least one language is selected
        for (JCheckBox checkbox : langCheckBoxes.values()) {
            if (checkbox.getModel().isSelected()) checked++;
        }
        if (checked == 0) {
            showWarning("You must select at least one language!");
            return false;
        }

        // Check that at least one SSYK code is selected
        checked = 0;
        for (JCheckBox checkbox : ssykCheckBoxes.values()) {
            if (checkbox.getModel().isSelected()) checked++;
        }
        if (checked == 0) {
            showWarning("You must select at least one SSYK code!");
            return false;
        }

        return true;
    }

    /**
     * Saves the settings and disposes the frame
     */
    private void saveSettings() {
        System.out.println("Saving settings..");

        langCheckBoxes.forEach((lang, checkbox) -> {
            settings.selectLang(lang, checkbox.getModel().isSelected());
        });

        ssykCheckBoxes.forEach((ssyk, checkbox) -> {
            settings.selectSsyk(ssyk, checkbox.getModel().isSelected());
        });

        settings.setAlpha((Double) alphaSpinner.getValue());
        settings.setBeta((Double) betaSpinner.getValue());
        settings.setIterations(Integer.parseInt(iterationsSpinner.getValue().toString()));
        settings.setThreads(Integer.parseInt(threadsSpinner.getValue().toString()));
        settings.setNumTopics(Integer.parseInt(topicsSpinner.getValue().toString()));

        dispose();
    }

    private void showWarning(String warning) {
        JOptionPane.showMessageDialog(null, warning);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(500, 500));
        mainPanel.setRequestFocusEnabled(true);
        jobLangPanel = new JPanel();
        jobLangPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 10.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(jobLangPanel, gbc);
        jobLangPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Job Ads languages", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        jobSsykPanel = new JPanel();
        jobSsykPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 10.0;
        gbc.weighty = 5.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(jobSsykPanel, gbc);
        jobSsykPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Job Ads SSYK codes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        analyserSettingsPanel = new JPanel();
        analyserSettingsPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 10.0;
        gbc.weighty = 5.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(analyserSettingsPanel, gbc);
        analyserSettingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Analyser Settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
