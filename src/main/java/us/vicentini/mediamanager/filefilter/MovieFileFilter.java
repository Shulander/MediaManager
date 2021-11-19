package us.vicentini.mediamanager.filefilter;

import us.vicentini.mediamanager.actions.CopyFileAction;

import java.io.File;
import java.util.Optional;

/**
 * @author Shulander
 */
public class MovieFileFilter extends AbstractFileFilter {

    @Override
    protected boolean anyMatchingMedia(File mediaPath) {
        return fileFilterList.stream()
                .anyMatch(fileFilter -> mediaPath.getName().contains(fileFilter));
    }


    @Override
    public Optional<CopyFileAction> createFileAction(File mediaPath, File basePath) {
        return Optional.of(new CopyFileAction(mediaPath, basePath));
    }
}
