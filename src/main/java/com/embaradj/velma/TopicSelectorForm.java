package com.embaradj.velma;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;

///**
// * Inner class representing each topic's selection and controls in the GUI
// */
//class TopicSelectionPanel extends JPanel {
//
//    JCheckBox checkbox = new JCheckBox("", true);
//    JTextField topicContentTextField = new JTextField();
//    JTextField topicNameTextField = new JTextField();
//
//    /**
//     * Constructor
//     * @param topic Name of the topic
//     * @param content Content (words) of the topic
//     */
//    public TopicSelectionPanel(String topic, String content) {
//
//
//
//        topicContentTextField.setText(content);
//        topicNameTextField.setText(topic);
//
//        topicContentTextField.setEnabled(false);
//        topicNameTextField.setEnabled(true);
//
//        add(checkbox);
//        add(topicContentTextField);
//        add(topicNameTextField);
//    }
//
//}

/**
 * Form which allows the user to see, select, and change names of the topics that have been identified
 */
public class TopicSelectorForm extends JFrame {
    private JPanel mainPanel;
    private JTextArea textArea1;
    private JScrollPane scrollPane;

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
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(-6251589));
        mainPanel.setForeground(new Color(-16449020));
        mainPanel.setPreferredSize(new Dimension(500, 500));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "test", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        scrollPane = new JScrollPane();
        scrollPane.setBackground(new Color(-12500974));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 10.0;
        gbc.weighty = 5.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(scrollPane, gbc);
        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        Font textArea1Font = this.$$$getFont$$$("Courier 10 Pitch", Font.BOLD, 26, textArea1.getFont());
        if (textArea1Font != null) textArea1.setFont(textArea1Font);
        textArea1.setRows(1);
        textArea1.setText("Identified topics");
        scrollPane.setViewportView(textArea1);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
