package com.embaradj.velma;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

/**
 * Responsible for downloading the PDF-files.
 */
public class FileDownloader {
    final static int CONNECT_TIMEOUT = 5000;
    final static int READ_TIMEOUT = 5000;

    public static String download(String urlString) {
        String localPath = null;

        try {

            // Change to https as the http-urls redirects to https and that is not supported by FileUtils
            if (urlString.startsWith("http:")) urlString = "https" + urlString.substring(4);

            URL url = new URL(urlString);
            String filename = url.getFile().split("/")[url.getFile().split("/").length - 1];
            localPath = "resources/pdfs/" + filename + ".pdf";

            if (Settings.debug()) {
                System.out.println("Downloading " + url.toString() + " to " + localPath + "...");
            }

            FileUtils.copyURLToFile(
                    url,
                    new File(localPath),
                    CONNECT_TIMEOUT,
                    READ_TIMEOUT
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return localPath;
    }
}