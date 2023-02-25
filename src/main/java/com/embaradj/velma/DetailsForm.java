package com.embaradj.velma;

import com.embaradj.velma.models.Job;
import com.embaradj.velma.results.SearchHit;

import javax.swing.*;

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

//        detailsTextField.setText(((Job) searchHit).getTitle() + "\n\n" + ((Job) obj).getDescription());
        detailsTextField.setText(searchHit.getTitle());
    }
}
