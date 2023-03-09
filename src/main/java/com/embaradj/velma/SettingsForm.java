package com.embaradj.velma;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsForm extends JFrame {
    private Settings settings = Settings.getInstance();
    private JPanel mainPanel;
    private JPanel jobLangPanel;
    private JPanel analyserSettingsPanel;
    private JPanel jobSsykPanel;

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
    }

    private void createSsykCheckboxes() {
        settings.getSsyk().forEach((Ssyk ssyk) -> {
            String checkText = ssyk.getCode() + "   " + ssyk.getDescription();
            JCheckBox checkBox = new JCheckBox(checkText, ssyk.isSelected());
            checkBox.addItemListener(ie -> {
                settings.selectSsyk(ssyk, (ie.getStateChange() == 1) ? true : false);
                if (settings.getSelectedSsyk().length == 0) {
                    showWarning("You must select at least one SSYK code!");
                }
            });

            jobSsykPanel.add(checkBox);
        });
    }

    private void createLangCheckboxes() {
        settings.getLang().forEach((lang, sel) -> {
            JCheckBox checkbox = new JCheckBox(lang, sel);
            checkbox.addItemListener(ie -> {
                settings.selectLang(lang, (ie.getStateChange() == 1) ? true : false);
                if (settings.getSelectedLang().length == 0) {
                    showWarning("You must select at least one language!");
                }
            });

            jobLangPanel.add(checkbox);
        });
    }

    private void createAnalyserOptions() {

        GridBagConstraints c = new GridBagConstraints();

        // Alpha
        SpinnerNumberModel alphaSpinnerModel = new SpinnerNumberModel(settings.getAlpha(), 0.01, 100, 0.01);
        JSpinner alphaSpinner = new JSpinner(alphaSpinnerModel);
        alphaSpinner.setEditor(new JSpinner.NumberEditor(alphaSpinner, "0.00"));

        JLabel alphaLabel = new JLabel("alpha");
        alphaLabel.setLabelFor(alphaSpinner);

        // Beta
        SpinnerNumberModel betaSpinnerModel = new SpinnerNumberModel(settings.getBeta(), 0.01, 100, 0.01);
        JSpinner betaSpinner = new JSpinner(betaSpinnerModel);
        betaSpinner.setEditor(new JSpinner.NumberEditor(betaSpinner, "0.00"));
        String betaToolTipText = "High beta = Each topic is more likely to contain " +
                "a mixture of words\nLow beta = Each topic may contain a mixture of only a few words";
        betaSpinner.setToolTipText(betaToolTipText);
        JLabel betaLabel = new JLabel("beta");
//        betaLabel.setLabelFor(betaSpinner);
        betaLabel.setToolTipText(betaToolTipText);

        // Number of topics
        SpinnerNumberModel topicsSpinnerModel = new SpinnerNumberModel(settings.getNumTopics(), 1, 10000, 1);
        JSpinner topicsSpinner = new JSpinner(topicsSpinnerModel);
        JLabel topicsLabel = new JLabel("Number of topics");
        topicsLabel.setLabelFor(topicsSpinner);

        // Number of threads
        SpinnerNumberModel threadsSpinnerModel = new SpinnerNumberModel(settings.getThreads(), 1, 256, 1);
        JSpinner threadsSpinner = new JSpinner(threadsSpinnerModel);
        JLabel threadsLabel = new JLabel("Number of threads");
        threadsLabel.setLabelFor(threadsSpinner);

        // Number of iterations
        SpinnerNumberModel iterationsSpinnerModel = new SpinnerNumberModel(settings.getIterations(), 1, 1000000, 100);
        JSpinner iterationsSpinner = new JSpinner(iterationsSpinnerModel);
        iterationsSpinner.setEditor(new JSpinner.NumberEditor(iterationsSpinner, "#"));
        JLabel iterationsLabel = new JLabel("Number of iterations");
        iterationsLabel.setLabelFor(iterationsSpinner);

        c.insets = new Insets(2, 2, 2, 2);

        // Add everything to the container
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 0;
        analyserSettingsPanel.add(alphaSpinner, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        analyserSettingsPanel.add(alphaLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 1;
        analyserSettingsPanel.add(betaSpinner, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 1;
        analyserSettingsPanel.add(betaLabel, c);

        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 2;
        analyserSettingsPanel.add(topicsSpinner, c);

        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 2;
        analyserSettingsPanel.add(topicsLabel, c);

        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 3;
        analyserSettingsPanel.add(threadsSpinner, c);

        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 3;
        analyserSettingsPanel.add(threadsLabel, c);

        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 4;
        analyserSettingsPanel.add(iterationsSpinner, c);

        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 4;
        analyserSettingsPanel.add(iterationsLabel, c);
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
        jobSsykPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
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
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        analyserSettingsPanel = new JPanel(new GridBagLayout());
    }
}
