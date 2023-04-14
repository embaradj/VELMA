package com.embaradj.velma;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;

/**
 * Inner class representing each topic's selection and controls in the GUI
 */
class TopicSelectionPanel extends JPanel {

    JCheckBox checkbox = new JCheckBox("", true);
    JTextField topicContentTextField = new JTextField();
    JTextField topicNameTextField = new JTextField();

    /**
     * Constructor
     * @param topic Name of the topic
     * @param content Content (words) of the topic
     */
    public TopicSelectionPanel(String topic, String content) {

        topicContentTextField.setText(content);
        topicNameTextField.setText(topic);

        topicContentTextField.setEnabled(false);
        topicNameTextField.setEnabled(true);

        add(checkbox);
        add(topicContentTextField);
        add(topicNameTextField);
    }

}

/**
 * Form which allows the user to see, select, and change names of the topics that have been identified
 */
public class TopicSelectorForm extends JFrame {
    private JPanel mainPanel;
    private JTextArea textArea1;

    public static void main(String[] args) {
        new TopicSelectorForm(getDummyTopics());
    }

    private static HashMap<String, String> getDummyTopics() {
        HashMap<String, String> map = new HashMap<>();
        map.put("TOPIC 0", "HELLO, HI, GOODBYE");
        map.put("TOPIC 1", "JUMP, SIT, WALK");
        map.put("TOPIC 2", "DANCE, RUN, DRIVE");
        return map;
    }

    public TopicSelectorForm(HashMap<String, String> topics) {
        $$$setupUI$$$();
        textArea1.setText("Identified Topics");
        mainPanel.add(textArea1);
        add(mainPanel);
        textArea1.setVisible(true);
        setSize(600, 700);
        setLocationRelativeTo(null);    // Position the frame in the center of the screen
        createSelection(mainPanel, topics);
        setVisible(true);
    }

    private void createSelection(JPanel panel, HashMap<String, String> topics) {
        topics.forEach((topicName, words) -> {
            System.out.println("Creating selection for " + topicName);
            panel.add(new TopicSelectionPanel(topicName, words));
        });

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
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(-6251589));
        mainPanel.setForeground(new Color(-16449020));
        mainPanel.setPreferredSize(new Dimension(500, 500));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "test", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        mainPanel.add(textArea1, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
