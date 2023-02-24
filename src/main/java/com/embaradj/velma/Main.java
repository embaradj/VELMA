package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;

import java.awt.*;

public class Main {
    public static void main(String[] args) {



        // Make the view observe changes to the model
//        model.addObserver(viewFrame);

        DataModel model = new DataModel();
        MainForm viewFrame = new MainForm(model);
        Controller controller = new Controller(model, viewFrame);
        viewFrame.addController(controller);
        viewFrame.addActionListeners();

        // Give the view an instance of the controller


    }
}