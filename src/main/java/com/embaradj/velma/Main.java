package com.embaradj.velma;

public class Main {
    public static void main(String[] args) {

        DataModel model = new DataModel();
        MainForm viewFrame = new MainForm();
        Controller controller = new Controller(model, viewFrame);

        // Make the view observe changes to the model
        model.addObserver(viewFrame);

        // Give the view an instance of the controller
        viewFrame.addController(controller);
        viewFrame.addActionListeners();

    }
}