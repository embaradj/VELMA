package com.embaradj.velma;

import com.embaradj.velma.apis.APIJobStream;
import com.embaradj.velma.apis.APIMyh;
import com.embaradj.velma.apis.APISusa;
import com.embaradj.velma.lda.Modeller;
import com.embaradj.velma.lda.ToTxt;
import com.embaradj.velma.models.DataModel;
import com.embaradj.velma.models.Hve;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents the controller which binds the logic to each button.
 */
public class Controller implements ActionListener {
    private final Settings settings = Settings.getInstance();
    private JFrame view;
    private final DataModel model;

    public Controller(DataModel model) {
        this.model = model;
    }

    protected void setView(JFrame viewFrame) {
        this.view = viewFrame;
        // Add a listener for the 'X' button to confirm yes or no on exit
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
    }

    /**
     * Invoked when a button / object is clicked
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("srcHve")) searchHve();
        if (e.getActionCommand().equals("srcJobs")) searchJobs();
        if (e.getActionCommand().equals("analyse")) analyse();
        if (e.getActionCommand().equals("settings")) settings();
        if (e.getActionCommand().equals("help")) help();
        if (e.getActionCommand().equals("quit")) exit();
    }

    /**
     * Creates the help window.
     */
    private void help() {
        EventQueue.invokeLater(() -> new DetailsForm("Help", settings.getHelpDocument()));
    }

    /**
     * Creates the settings window.
     */
    private void settings() {
        EventQueue.invokeLater(SettingsForm::new);
    }

    /**
     * Processes the collected data
     * And starts topic modelling with {@link Modeller}.
     */
    private void analyse() {
//        if (!Settings.debug() && (!model.isSearched("hve") || !model.isSearched("job"))) {
////        if (!model.isSearched("hve") || !model.isSearched("job")) {
//            JOptionPane.showMessageDialog(null, "You must run a curriculum- and job ads search first!");
//            return;
//        }
        model.clearLDATopics();
        ImageIcon icon = null;
        try {
            InputStream is = this.getClass().getResourceAsStream("/load.gif");
            Image image = Toolkit.getDefaultToolkit().createImage(IOUtils.toByteArray(is));
            icon = new ImageIcon(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JLabel iconLabel = new JLabel(icon);
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new BorderLayout());
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        JOptionPane pane = new JOptionPane();
        pane.setMessage(iconPanel);
        pane.setOptionType(JOptionPane.DEFAULT_OPTION);
        pane.setMessageType(JOptionPane.PLAIN_MESSAGE);
        pane.setOptions(new Object[] { });
        File file = new File("resources/rawdata/");
        Dialog dia = pane.createDialog(null ,"Please wait");
        Modeller modeller = new Modeller(model);

        // Begin the analyzing off the EDT
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                dia.setVisible(true);
            });

            modeller.worker(file);
            modeller.saveModel();

            SwingUtilities.invokeLater(() -> {
                dia.setVisible(false);
                new DetailsForm(model.getLDATopics());

                new Thread(() -> new Analyser(model.getLDATopics())).start();

                // Used for printing out the found words in a copy-paste friendly manner
//                String out = String.join("", model.getLDATopics().values()).replaceAll("\\[", "");
//                String out2 = out.replaceAll("\\]", ", ");
//                System.out.println(out2.replaceAll(", ", "\n"));
            });
        }).start();
    }

    /**
     * Spawn the dialog if the user wants to quit
     * @return true if quitting
     */
    public boolean quit() {
        return Settings.confirmYesNo("Quit application?","Are you sure you want to quit?");
    }

    /**
     * Exit the program
     */
    public void exit() {
        if (quit()) {
            for (Frame frame : Frame.getFrames()) frame.dispose();
        } else {
            view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }

    /**
     * Begin querying the {@link APIJobStream}.
     */
    public void searchJobs() {
        if (model.isSearched("job")) {
            if (!Settings.confirmYesNo("Search again?","Are you sure you want to download Job ads again?")) return;
        }
        model.clearJob();
        APIJobStream.doSearch(model);
    }

    /**
     * Begin querying the {@link APISusa} & {@link APIMyh}.
     */
    public void searchHve() {
        if (model.isSearched("hve")) {
            if (!Settings.confirmYesNo("Search again?", "Are you sure you want to download HVEs again?")) return;
        }
        model.clearHve();

        // The dataModel instance is needed for the Susa Nav to report back total number of hits
        APISusa.getObservableResult(model)
                .subscribeOn(Schedulers.io())
                .subscribe(susaHit -> {

                    // Use the HVE code retrieved from Susa API to search in MYH API
                    String pdfUrl = APIMyh.getPdfUrl(susaHit.getCode());
                    String localFilePath = FileDownloader.download(pdfUrl);

                    // If localFilePath is not null means successful download
                    if (!Objects.isNull(localFilePath)) {
                        PDFReader pdfReader = new PDFReader(localFilePath);
                        Hve hve = new Hve(susaHit.getCode(), susaHit.getTitle(), pdfReader.getCourses(), pdfReader.getFullText(), pdfReader.getAimText());
                        model.addAndUpdate(hve);
                        new ToTxt(hve);
                    } else {
                        System.out.println("Could not download pdf " + susaHit);
                    }

                    if (Settings.debug()) {
                        System.out.println(susaHit + "\n" + pdfUrl + "\n----------------");
                    }
                }, Throwable::printStackTrace);
    }
}