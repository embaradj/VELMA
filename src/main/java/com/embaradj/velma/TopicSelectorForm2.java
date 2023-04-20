package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Inner class representing each topic's selection and controls in the GUI
 */
class TopicSelectionPanel extends JPanel {

    String topic;
    JCheckBox checkbox = new JCheckBox("", true);
    JTextArea topicContentTextField;
    JTextField topicNameTextField ;

    /**
     * Constructor
     * @param topic Name of the topic
     * @param content Content (words) of the topic
     */
    public TopicSelectionPanel(String topic, String content) {

        this.topic = topic;
        topicContentTextField = new JTextArea(content, 1, 40);
        topicNameTextField = new JTextField(topic,20);

        setBorder(BorderFactory.createTitledBorder("test_1"));

        topicContentTextField.setEnabled(false);
        topicNameTextField.setEnabled(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        add(checkbox, gbc);

        // Make the textbox scrollable if all words don't fit
        JScrollPane hScroll = new JScrollPane(topicContentTextField);
        hScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(hScroll, gbc);
        add(topicNameTextField, gbc);
    }

    protected String getTopicName() {
        return topicNameTextField.getText();
    }

    protected boolean checked() {
        return checkbox.getModel().isSelected();
    }

}

public class TopicSelectorForm2 extends JFrame {


    public static void main(String[] args) {
        // If the class is run on its own (for testing)..
        new TopicSelectorForm2(new DataModel(), getDummyTopics());
    }

    private static HashMap<String, String> getDummyTopics() {
        HashMap<String, String> map = new HashMap<>();
        map.put("TOPIC 0", "HELLO, HI, GOODBYE");
        map.put("TOPIC 1", "JUMP, SIT, WALK");
        map.put("TOPIC 2", "DANCE, RUN, DRIVE");
        return map;
    }
    private HashMap<String, TopicSelectionPanel> topicSelector = new HashMap<>();
    private HashMap<String, String> topics;
    private DataModel model;

    public TopicSelectorForm2(DataModel model, HashMap<String, String> topics) {
        this.model = model;
        this.topics = topics;
        setupGui();
    }

    private void setupGui() {
        setTitle("Select and edit topics");
        setSize(750, 700);
        setLocationRelativeTo(null);    // Position the frame in the center of the screen
        JPanel mainPanel = new JPanel();

        mainPanel.setBorder(BorderFactory.createTitledBorder("mainPanel"));

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridBagLayout());
        createSelection(selectionPanel, topics);

        JScrollPane scrollPane = new JScrollPane(selectionPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setBorder(BorderFactory.createTitledBorder("scrollPane"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        mainPanel.add(scrollPane, gbc);

        JButton okBtn = new JButton("OK");
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(okBtn, gbc);
        okBtn.addActionListener((e) -> { okClicked(); });
        setContentPane(mainPanel);
        setVisible(true);

    }

    private void okClicked() {

        // Check legal inputs
        Set<String> topicNames = new HashSet<>();

        boolean oneSelected = false;

        for (TopicSelectionPanel topicSelection : topicSelector.values()) {

            // Check that at least one topic is selected
            if (topicSelection.checked()) { oneSelected = true; }

            String topicName = topicSelection.getTopicName();  // The name currently in the textbox

            // Check if any selected topic has an empty name
            if (topicSelection.checked() && topicName.isEmpty()) {
                showWarning("All topics need to have a name");
                return;
            }

            // Check if several selected topics has the same name
            if (topicSelection.checked() && !topicNames.add(topicName)) {
                showWarning("All topics need to have unique names");
                return;
            }
        }

        if (!oneSelected) {
            showWarning("You must select at least one topic");
            return;
        }

        for (Map.Entry<String, TopicSelectionPanel> map : topicSelector.entrySet()) {

            if (!map.getValue().checked()) {
                System.out.println("Topic " + map.getKey() + " was unchecked.. removing..");
                topics.remove(map.getKey());
                continue;
            }

            String topicName = map.getValue().getTopicName();  // The name currently in the textbox

            if (!map.getKey().equals(topicName)) {
                System.out.println("Updating topic name " + map.getKey() + " to " + topicName);

                // These two lines renames the key
                String temp = topics.remove(map.getKey());
                topics.put(topicName, temp);

            }

        }

        model.updateLDATopics(topics);
        dispose();
    }

    private void createSelection(JComponent panel, HashMap<String, String> topics) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        int row = 0;

        for (Map.Entry<String, String> map : topics.entrySet()) {
            // Key: topicName, Value: words
            System.out.println("Creating selection for " + map.getKey());

            gbc.gridy = row;
            TopicSelectionPanel tPanel = new TopicSelectionPanel(map.getKey(), map.getValue());
            panel.add(tPanel, gbc);
            topicSelector.put(map.getKey(), tPanel);
//            panel.add(new TopicSelectionPanel(map.getKey(), map.getValue()), gbc);
            row++;
        }



    }

    private void showWarning(String warning) {
        JOptionPane.showMessageDialog(null, warning);
    }
}
