package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            DataModel model = new DataModel();
            Controller controller = new Controller(model);
            MainForm viewFrame = new MainForm(controller, model);
        });
    }

}