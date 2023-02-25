package com.embaradj.velma;

import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.models.Hve;
import com.embaradj.velma.models.Job;
import com.embaradj.velma.results.SearchHit;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observer;

import static javax.swing.SwingUtilities.isEventDispatchThread;

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
    private DefaultListModel<SearchHit> listModel1 = new DefaultListModel<>(); // Used for the JLists
    private DefaultListModel<SearchHit> listModel2 = new DefaultListModel<>(); // Used for the JLists
    private JList list1;
    private JList list2;

    private ActionListener controller;

    public MainForm(Controller controller, DataModel model) {
        this.controller = controller;
        this.model = model;
        setTitle("HVE Matcher alpha");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        setListener();
        controller.setView(this);
        addActionListeners();

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

        class CustomMouseAdapter extends MouseAdapter {
            JList list;

            CustomMouseAdapter(JList list) {
                this.list = list;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int index = list.locationToIndex(e.getPoint());
                Object hit = list.getModel().getElementAt(index);
                System.out.println("Object type in list: " + hit.getClass().getName());
                SearchHit sh = (SearchHit)  hit;
                System.out.println("Clicked: " + index + "  " + sh.getTitle());
                DetailsForm detailsForm = new DetailsForm(sh);
                detailsForm.setTitle("Job details for " + sh.getTitle());
            }
        }

        /*
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int index = list2.locationToIndex(e.getPoint());
                Object hit = list2.getModel().getElementAt(index);
                System.out.println("Object type in list: " + hit.getClass().getName());
                SearchHit sh = (SearchHit)  hit;
                System.out.println("Clicked: " + index + "  " + sh.getTitle());
                DetailsForm detailsForm = new DetailsForm(sh);
                detailsForm.setTitle("Job details for " + sh.getTitle());
            }
        };

         */

//        list2.addMouseListener(mouseListener);
        list1.addMouseListener(new CustomMouseAdapter(list1));
        list2.addMouseListener(new CustomMouseAdapter(list2));
    }

    /**
     * Listen to changes in the Model and updates the Lists of HVEs and Jobs
     */
    private void setListener() {
        model.addListener(e -> {

            SearchHit searchHit = (SearchHit) e.getNewValue();

            // Update the JList on the EDT thread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (e.getPropertyName().equals("hve")) listModel1.addElement(searchHit);
                    if (e.getPropertyName().equals("job")) listModel2.addElement(searchHit);
                }
            });
        });
    }

}
