package us.vicentini.mediamanager.filefilter;

import java.io.File;
import us.vicentini.mediamanager.actions.CopyFileAction;

/**
 *
 * @author Shulander
 */
public class MovieFileFilter extends AbstractFileFilter {

    @Override
    protected boolean anyMatchingMedia(File mediaPath) {
        return fileFilterList.stream()
                .anyMatch(fileFilter -> mediaPath.getName().contains(fileFilter));
    }


    @Override
    public CopyFileAction createFileAction(File mediaPath, File basePath) {
        return new CopyFileAction(mediaPath, basePath);
    }
    
}
