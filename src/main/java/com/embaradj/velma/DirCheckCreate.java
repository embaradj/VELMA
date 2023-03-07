package com.embaradj.velma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public enum DirCheckCreate {
    INSTANCE;

    public void checkDir(String folder) {
        Path path = Path.of(folder);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
