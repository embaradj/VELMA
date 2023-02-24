package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;

public class Main {

    public static void main(String[] args) {

        DataModel model = new DataModel();
        MainForm viewFrame = new MainForm(model);
        Controller controller = new Controller(model, viewFrame);
        viewFrame.addController(controller);
        viewFrame.addActionListeners();

    }
}