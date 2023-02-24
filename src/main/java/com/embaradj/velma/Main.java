package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        DataModel model = new DataModel();

        MainForm viewFrame = new MainForm(model);
        Controller controller = new Controller(model, viewFrame);
        viewFrame.addController(controller);
        viewFrame.addActionListeners();

        // We dont want to Controller to run on the EDT!!
//        EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                MainForm viewFrame = new MainForm(model);
//                Controller controller = new Controller(model, viewFrame);
//                viewFrame.addController(controller);
//                viewFrame.addActionListeners();
//            }
//        });

    }
}