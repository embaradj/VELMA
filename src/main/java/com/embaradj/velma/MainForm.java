package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.models.Hve;
import com.embaradj.velma.models.Job;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Observer;

/**
 * Represents the view of the MVC pattern
 */
public class MainForm extends JFrame {

    private DataModel model;
    protected JPanel panel1;
    private JButton srcHveBtn;
    private JButton srcJobsBtn;
    private JButton settingsBtn;
    private JButton analyseBtn;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JButton quitBtn;
    private JButton helpBtn;
    private JScrollPane scrollPaneLeft;
    private JScrollPane scrollPaneRight;
    private DefaultListModel<String> listModel1 = new DefaultListModel<>(); // Used for the JLists
    private DefaultListModel<String> listModel2 = new DefaultListModel<>(); // Used for the JLists
    private JList list1;
    private JList list2;

    private ActionListener controller;

    public MainForm(DataModel model) {
        this.model = model;
        setTitle("HVE Matcher alpha");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        setListener();

        list1.setModel(listModel1);
        list2.setModel(listModel2);
    }

    protected void addController(ActionListener controller) {
        this.controller = controller;
    }

    /**
     * Let the controller setup listeners for the components
     */
    protected void addActionListeners() {
        srcHveBtn.addActionListener(controller);
        srcJobsBtn.addActionListener(controller);
        analyseBtn.addActionListener(controller);
        settingsBtn.addActionListener(controller);
        quitBtn.addActionListener(controller);
        helpBtn.addActionListener(controller);

        // temp for testing
//        srcJobsBtn.addActionListener(listener -> tempTest());
    }

    private void setListener() {
        model.addListener(e -> {
            if (e.getPropertyName().contains("hve")) {
                String desc = ((Hve) e.getNewValue()).getDescription();
                listModel1.addElement(desc);
                System.out.println("view: " + Thread.currentThread().getName());
            } else {
                String title = ((Job) e.getNewValue()).getTitle();
//                System.out.println(title);
//                System.out.println(Thread.currentThread().getName());
//                listModel2.addElement(title);
                System.out.println("view: " + Thread.currentThread().getName());
                SwingUtilities.invokeLater(new Runnable() {
                                               @Override
                                               public void run() {
                                                   System.out.println("runnable: " + Thread.currentThread().getName());
                                                   listModel2.addElement(title);
                                               }
                                           });
//                        listModel2.addElement(title);
            }
        });
    }

}
