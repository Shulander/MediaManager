package us.vicentini.mediamanager.actions;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Shulander
 */
@Slf4j
@ToString
@RequiredArgsConstructor
public class CopyFileAction {
    private final File fromFile;
    private final File destinationPath;


    public void process() throws IOException {
        if (!destinationPath.exists()) {
            Files.createDirectories(destinationPath.toPath());
        }
        Path source = fromFile.toPath();
        File toFile = new File(destinationPath, fromFile.getName());
        Path target = toFile.toPath();
        log.info("Copy from:{}, to:{}", source, target);
        if (!toFile.exists()) {
            Files.copy(source, target);
        } else {
            log.warn("Target file already exists: {}", target);
        }
        log.info("Done");
    }


    public File getFromFile() {
        return fromFile;
    }


    public File getDestinationPath() {
        return destinationPath;
    }
}
