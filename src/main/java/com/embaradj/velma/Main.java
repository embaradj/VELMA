package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;

import javax.swing.*;
import java.awt.*;

public class Main {
    static DirCheckCreate dirCheck = DirCheckCreate.INSTANCE;
    public static void main(String[] args) {
        // Create needed folders before program starts
        dirCheck.checkDir("resources/");
        dirCheck.checkDir("resources/pdfs");
        dirCheck.checkDir("resources/processeddata");
        dirCheck.checkDir("resources/rawdata/");
        dirCheck.checkDir("resources/rawdata/hve");
        dirCheck.checkDir("resources/rawdata/job");

        EventQueue.invokeLater(() -> {
            DataModel model = new DataModel();
            Controller controller = new Controller(model);
            MainForm viewFrame = new MainForm(controller, model);
        });
    }

}