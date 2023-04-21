package com.embaradj.velma;

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Displays an animation during progress
 */
public class LoadingPane extends JOptionPane {
    private JLabel iconLabel;
    private JPanel iconPanel;
    private JProgressBar progBar;
    private JDialog dialog;

    public LoadingPane() {
        ImageIcon icon;
        try {
            InputStream is = this.getClass().getResourceAsStream("/load.gif");
            Image image = Toolkit.getDefaultToolkit().createImage(IOUtils.toByteArray(is));
            icon = new ImageIcon(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        iconLabel = new JLabel(icon);
        iconLabel.setVerticalTextPosition(JLabel.BOTTOM);
        iconLabel.setHorizontalTextPosition(JLabel.CENTER);

        iconPanel = new JPanel();
        iconPanel.setLayout(new BorderLayout());
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        setOptionType(JOptionPane.DEFAULT_OPTION);
        setMessageType(JOptionPane.PLAIN_MESSAGE);
        setOptions(new Object[] { });
    }

    public LoadingPane(String action) {
        this();
        iconLabel.setText(action);
        setMessage(iconPanel);
    }

    public LoadingPane(String action, StatusProvider status) {
        this();
        iconLabel.setText(action);

        progBar = new JProgressBar(0, 100);
        progBar.setValue(0);
        progBar.setStringPainted(true);
        progBar.setString("0 %");

        status.addListener((e) -> {
            if (e.getPropertyName().equals("loadingprogress")) {
                int progress = (int) e.getNewValue();
                progBar.setValue(progress);
                progBar.setString(progress + " %");
                if (progress >= 99) dialog.dispose();
            }
        });

        setMessage(new Object[] {iconPanel, progBar});

        dialog = createDialog("Please wait");
        dialog.setVisible(true);
    }

    public void close() {
        dialog.dispose();
    }
}
