package us.vicentini.mediamanager.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Shulander
 */
public class CopyFileAction {
    private final File fromFile;
    private final File destinationPath;

    public CopyFileAction(File mediaPath, File basePath) {
        this.fromFile = mediaPath;
        this.destinationPath = basePath;
    }

    public void process() throws IOException {
        if(!destinationPath.exists()) {
            Files.createDirectories(destinationPath.toPath());
        }
        
        Files.copy(fromFile.toPath(), (new File(destinationPath, fromFile.getName())).toPath());
    }
 
    @Override
    public String toString(){
        return fromFile.getAbsolutePath()+" to "+destinationPath.getAbsolutePath();
    }
}
