package com.embaradj.velma;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsForm extends JFrame {
    private final Settings settings = Settings.getInstance();
    private JPanel mainPanel;
    private JPanel analyserSettingsPanel;
    private JPanel jobSsykPanel;
    private JPanel buttonPanel;
    private JPanel analyserSelectionPanel;
    private JSpinner alphaSpinner, betaSpinner, iterationsSpinner, threadsSpinner, topicsSpinner, wordsSpinner;
    private final HashMap<Ssyk, JCheckBox> ssykCheckBoxes = new HashMap<>();
    private JCheckBox jobCheck, hveCheck, fullHveCheck, goalsHveCheck, courseHveCheck, sweJobCheck, engJobCheck;

    private static class CustomWrapper {
        public CustomWrapper(JComponent component, JLabel label) {
            this.component = component;
            this.label = label;
        }

        protected JComponent component;
        protected JLabel label;
    }

    /**
     * Logics for checkbox behaviour
     */
    private class CheckBoxLogic implements ActionListener {
        private final String checkbox;

        public CheckBoxLogic(String checkbox) {
            this.checkbox = checkbox;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (checkbox) {
                case "hve" -> {
                    // Unchecks HVE related checkboxes if HVE is unchecked
                    if (!hveCheck.getModel().isSelected()) {
                        fullHveCheck.getModel().setSelected(false);
                        goalsHveCheck.getModel().setSelected(false);
                        courseHveCheck.getModel().setSelected(false);
                        // Check default option if HVE is checked
                    } else fullHveCheck.getModel().setSelected(true);
                }
                case "full" -> {
                    // Uncheck goals and courses if "full" is checked
                    goalsHveCheck.getModel().setSelected(false);
                    courseHveCheck.getModel().setSelected(false);
                    noHveCheck();
                }
                case "goal", "courses" -> {
                    // Uncheck "full HVE text"  if goals or courses are checked
                    fullHveCheck.getModel().setSelected(false);
                    noHveCheck();
                }
                case "job" -> {
                    // Select / Unselect Swedish and English depending on job checkbox
                    sweJobCheck.getModel().setSelected(jobCheck.getModel().isSelected());
                    engJobCheck.getModel().setSelected(jobCheck.getModel().isSelected());
                }
                case "job_swe", "job_eng" ->
                    // Select / Unselect jobs depending on Swe / Eng selection
                        jobCheck.getModel().setSelected(sweJobCheck.getModel().isSelected() || engJobCheck.getModel().isSelected());
            }
        }

        // Selects or unselects HVE depending on its sub-options
        private void noHveCheck() {
            hveCheck.getModel().setSelected(fullHveCheck.getModel().isSelected() ||
                    goalsHveCheck.getModel().isSelected() ||
                    courseHveCheck.getModel().isSelected());
        }
    }

    public SettingsForm() {
        $$$setupUI$$$();
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Settings");

        createSsykCheckboxes();
        createAnalyserOptions();
        createAnalyserSelection();
        createButtons();


        setSize(450, 725);
        setLocationRelativeTo(null);    // Position the frame in the center of the screen
        setVisible(true);
//        pack();
    }

    private void createSsykCheckboxes() {
        ArrayList<CustomWrapper> checkBoxRows = new ArrayList<>();

        for (Ssyk ssyk : settings.getSsyk()) {
            JCheckBox checkBox = new JCheckBox(ssyk.getCode(), ssyk.isSelected());
            JLabel label = new JLabel(ssyk.getDescription());
            checkBoxRows.add(new CustomWrapper(checkBox, label));
            ssykCheckBoxes.put(ssyk, checkBox); // Save these for later so we can check whether they are checked
        }

        // Add all these checkboxes with labels to the panel
        addRowsToPanel(checkBoxRows, jobSsykPanel);
    }

    private void createAnalyserOptions() {

        // Alpha
        SpinnerNumberModel alphaSpinnerModel = new SpinnerNumberModel(Settings.getAlpha(), 0.01, 100, 0.01);
        alphaSpinner = new JSpinner(alphaSpinnerModel);
        alphaSpinner.setEditor(new JSpinner.NumberEditor(alphaSpinner, "0.00"));
        String alphaText = "Controls the number of topics a document can contain";
        alphaSpinner.setToolTipText(alphaText);
        JLabel alphaLabel = new JLabel(alphaText);
        alphaLabel.setToolTipText(alphaText);

        // Beta
        SpinnerNumberModel betaSpinnerModel = new SpinnerNumberModel(Settings.getBeta(), 0.01, 100, 0.01);
        betaSpinner = new JSpinner(betaSpinnerModel);
        betaSpinner.setEditor(new JSpinner.NumberEditor(betaSpinner, "0.00"));
        String betaText = "Controls the number of words a topic can contain";
        betaSpinner.setToolTipText(betaText);
        JLabel betaLabel = new JLabel(betaText);
        betaLabel.setToolTipText(betaText);

        // Number of topics
        SpinnerNumberModel topicsSpinnerModel = new SpinnerNumberModel(Settings.getNumTopics(), 1, 10000, 1);
        topicsSpinner = new JSpinner(topicsSpinnerModel);
        JLabel topicsLabel = new JLabel("Number of topics");

        // Number of threads
        SpinnerNumberModel threadsSpinnerModel = new SpinnerNumberModel(Settings.getThreads(), 1, 256, 1);
        threadsSpinner = new JSpinner(threadsSpinnerModel);
        JLabel threadsLabel = new JLabel("Number of threads");

        // Number of iterations
        SpinnerNumberModel iterationsSpinnerModel = new SpinnerNumberModel(Settings.getIterations(), 1, 1000000, 100);
        iterationsSpinner = new JSpinner(iterationsSpinnerModel);
        iterationsSpinner.setEditor(new JSpinner.NumberEditor(iterationsSpinner, "#"));
        JLabel iterationsLabel = new JLabel("Number of iterations");

        // Number of words per topic
        SpinnerNumberModel wordsSpinnerModel = new SpinnerNumberModel(Settings.getWords(), 1, 100, 1);
        wordsSpinner = new JSpinner(wordsSpinnerModel);
        JLabel wordsLabel = new JLabel("Number of words per topic");

        // Put everything on the Panel
        ArrayList<CustomWrapper> checkBoxRows = new ArrayList<>();
        checkBoxRows.add(new CustomWrapper(alphaSpinner, alphaLabel));
        checkBoxRows.add(new CustomWrapper(betaSpinner, betaLabel));
        checkBoxRows.add(new CustomWrapper(topicsSpinner, topicsLabel));
        checkBoxRows.add(new CustomWrapper(threadsSpinner, threadsLabel));
        checkBoxRows.add(new CustomWrapper(iterationsSpinner, iterationsLabel));
        checkBoxRows.add(new CustomWrapper(wordsSpinner, wordsLabel));

        addRowsToPanel(checkBoxRows, analyserSettingsPanel);
    }

    private void createAnalyserSelection() {

        // Get the selected options
        boolean[] selection = Settings.getAnalyserSelection();

        // Create checkboxes

        jobCheck = new JCheckBox("Job ads", (selection[0] || selection[1]));
        sweJobCheck = new JCheckBox("Swedish", selection[0]);
        engJobCheck = new JCheckBox("English", selection[1]);

        hveCheck = new JCheckBox("HVE", (selection[2] || selection[3]) || selection[4]);
        fullHveCheck = new JCheckBox("Full curricula", selection[2]);
        goalsHveCheck = new JCheckBox("Aim of programmes", selection[3]);
        courseHveCheck = new JCheckBox("Courses of programmes", selection[4]);

        // Visually group these checkboxes by adding an extra left-margin to them
        Insets extraMargin = new Insets(0, 15, 0, 0);
        sweJobCheck.setMargin(extraMargin);
        engJobCheck.setMargin(extraMargin);
        fullHveCheck.setMargin(extraMargin);
        goalsHveCheck.setMargin(extraMargin);
        courseHveCheck.setMargin(extraMargin);

        // Add the checkboxes to the container
        ArrayList<CustomWrapper> checkBoxRows = new ArrayList<>();
        checkBoxRows.add(new CustomWrapper(jobCheck, null));
        checkBoxRows.add(new CustomWrapper(sweJobCheck, null));
        checkBoxRows.add(new CustomWrapper(engJobCheck, null));
        checkBoxRows.add(new CustomWrapper(hveCheck, null));
        checkBoxRows.add(new CustomWrapper(fullHveCheck, null));
        checkBoxRows.add(new CustomWrapper(goalsHveCheck, null));
        checkBoxRows.add(new CustomWrapper(courseHveCheck, null));
        addRowsToPanel(checkBoxRows, analyserSelectionPanel);

        // Set logic behaviour for the checkboxes
        jobCheck.addActionListener(new CheckBoxLogic("job"));
        sweJobCheck.addActionListener(new CheckBoxLogic("job_swe"));
        engJobCheck.addActionListener(new CheckBoxLogic("job_eng"));

        hveCheck.addActionListener(new CheckBoxLogic("hve"));
        fullHveCheck.addActionListener(new CheckBoxLogic("full"));
        goalsHveCheck.addActionListener(new CheckBoxLogic("goal"));
        courseHveCheck.addActionListener(new CheckBoxLogic("courses"));
    }

    /**
     * Adds a list of components with labels to a panel, then fills the rest with empty space in order
     * to push the contents to the top
     *
     * @param boxes List of "boxes", which is a wrapper for a control component and a label
     * @param panel The panel to which the components are being added.
     */
    private void addRowsToPanel(ArrayList<CustomWrapper> boxes, Container panel) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        for (CustomWrapper box : boxes) {
            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0.1;
            panel.add(box.component, c);

            if (box.label != null) {
                c.weightx = 1;
                c.gridx = 1;
                panel.add(box.label, c);
                box.label.setLabelFor(box.component);
            }

            row++;
        }

        // Fill up remaining vertical space in order to push the above stuff towards the top
        c.anchor = GridBagConstraints.PAGE_END;
        c.weighty = 1;
        c.gridy = row;
        panel.add(new JLabel(), c);
//        panel.revalidate();
    }

    private void createButtons() {
        JButton okBtn = new JButton("Save");
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

        // Check that at least one SSYK code is selected
        for (JCheckBox checkbox : ssykCheckBoxes.values()) {
            if (checkbox.getModel().isSelected()) checked++;
        }
        if (checked == 0) {
            showWarning("You must select at least one SSYK code!");
            return false;
        }

        // Check that at least one option for the Analyser selection is checked
        if (!jobCheck.getModel().isSelected() && !hveCheck.getModel().isSelected()) {
            showWarning("You must select at least one input option for the Analyser!");
            return false;
        }

        // todo unnecessary check..?
        // Check that one HVE-suboption is checked if main option is cheked
        if (hveCheck.getModel().isSelected()) {
            if (!fullHveCheck.getModel().isSelected() &&
                    !goalsHveCheck.getModel().isSelected() &&
                    !courseHveCheck.getModel().isSelected()) {

                showWarning("You must select at least one sub-option to HVE, or unselect HVE");
                return false;
            }
        }

        return true;
    }

    /**
     * Saves the settings and disposes the frame
     */
    private void saveSettings() {

        ssykCheckBoxes.forEach((ssyk, checkbox) -> {
            settings.selectSsyk(ssyk, checkbox.getModel().isSelected());
        });

        Settings.setAlpha((Double) alphaSpinner.getValue());
        Settings.setBeta((Double) betaSpinner.getValue());
        Settings.setIterations(Integer.parseInt(iterationsSpinner.getValue().toString()));
        Settings.setThreads(Integer.parseInt(threadsSpinner.getValue().toString()));
        Settings.setNumTopics(Integer.parseInt(topicsSpinner.getValue().toString()));
        Settings.setWords((int) wordsSpinner.getValue());

        Settings.setAnalyserSelection(
                new boolean[]{
                        sweJobCheck.getModel().isSelected(),
                        engJobCheck.getModel().isSelected(),
                        fullHveCheck.getModel().isSelected(),
                        goalsHveCheck.getModel().isSelected(),
                        courseHveCheck.getModel().isSelected()
                });

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
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(scrollPane1, gbc);
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        jobSsykPanel = new JPanel();
        jobSsykPanel.setLayout(new GridBagLayout());
        scrollPane1.setViewportView(jobSsykPanel);
        jobSsykPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Job Ads SSYK codes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane2 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(scrollPane2, gbc);
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        analyserSettingsPanel = new JPanel();
        analyserSettingsPanel.setLayout(new GridBagLayout());
        scrollPane2.setViewportView(analyserSettingsPanel);
        analyserSettingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Analyser Settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane3 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(scrollPane3, gbc);
        scrollPane3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        analyserSelectionPanel = new JPanel();
        analyserSelectionPanel.setLayout(new GridBagLayout());
        scrollPane3.setViewportView(analyserSelectionPanel);
        analyserSelectionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Analyser input selection", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
