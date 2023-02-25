package com.embaradj.velma;

import com.embaradj.velma.models.Hve;
import com.embaradj.velma.results.SearchHit;
import javax.swing.*;

/**
 * Menu to show details about a HVE or a Job
 */
public class DetailsForm extends JFrame{
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
}
