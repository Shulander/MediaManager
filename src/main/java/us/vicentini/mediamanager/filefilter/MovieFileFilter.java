package us.vicentini.mediamanager.filefilter;

import java.io.File;
import us.vicentini.mediamanager.actions.CopyFileAction;

/**
 *
 * @author Shulander
 */
public class MovieFileFilter extends AbstractFileFilter {

    @Override
    public CopyFileAction createFileAction(File mediaPath, File basePath) {
        return new CopyFileAction(mediaPath, basePath);
    }
    
}
