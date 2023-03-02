package com.embaradj.velma;

import com.embaradj.velma.models.Hve;
import com.embaradj.velma.results.SearchHit;
import javax.swing.*;
import java.awt.*;

/**
 * Menu to show details about a HVE or a Job
 */
public class DetailsForm extends JFrame {
    private JTextField detailsTextField;
    private JPanel mainPanel;

    public DetailsForm(SearchHit searchHit) {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
        showText(searchHit);
    }

    private void showText(SearchHit searchHit) {
        boolean isHve = (searchHit.getType() == "hve");

        setTitle((isHve ? "HVE: " : "Job: ") + searchHit.getTitle());
        detailsTextField.setText(searchHit.getTitle());
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        detailsTextField = new JTextField();
        detailsTextField.setText("Details..");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(detailsTextField, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
