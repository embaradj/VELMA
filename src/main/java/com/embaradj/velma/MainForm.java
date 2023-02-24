package com.embaradj.velma;

import io.reactivex.rxjava3.core.Observable;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Observer;

/**
 * Represents the view of the MVC pattern
 */
public class MainForm extends JFrame implements Observer {
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

    public MainForm() {

        setTitle("HVE Matcher alpha");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

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
    }

    private void tempTest() {
        System.out.println("search jbos clicked");
        listModel2.addElement("test");
    }

    public void update(java.util.Observable obs, Object obj) {

        System.out.println("Observer invoked by " + obj.getClass());

        if (obj.getClass() == Hve.class) {
            String desc = ((Hve) obj).getDescription();
            listModel1.addElement(desc);
        }

        if (obj.getClass() == Job.class) {
            String desc = ((Job) obj).getTitle();
            listModel2.addElement(desc);
        }

    }


}
