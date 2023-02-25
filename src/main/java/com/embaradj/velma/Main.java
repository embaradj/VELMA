package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        DataModel model = new DataModel();
        Controller controller = new Controller(model);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainForm viewFrame = new MainForm(controller, model);
            }
        });

    }

}