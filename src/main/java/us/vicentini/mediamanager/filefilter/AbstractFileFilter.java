package us.vicentini.mediamanager.filefilter;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import us.vicentini.mediamanager.actions.CopyFileAction;
import us.vicentini.mediamanager.util.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Shulander
 */
@Slf4j
@ToString
public abstract class AbstractFileFilter {

    protected String fileFilterName;
    protected List<String> fileFilterList;
    protected List<String> fileExtensions;
    protected File destinationFolder;
    protected LinkedList<String> fileFilterExclude;


    public void load(Configuration config, String section) {
        fileFilterName = section;
        this.fileFilterList = new LinkedList<>();
        config.getList(section + ".fileFilter")
                .forEach(fileFilter -> fileFilterList.add(fileFilter.toString()));

        this.fileExtensions = new LinkedList<>();
        config.getList(section + ".fileextensions")
                .forEach(fileFilter -> fileExtensions.add(fileFilter.toString()));

        fileFilterExclude = new LinkedList<>();

        if (config.containsKey(section + ".fileFilterExclude")) {
            config.getList(section + ".fileFilterExclude")
                    .forEach(fileFilter -> fileFilterExclude.add(fileFilter.toString()));
        }

        this.destinationFolder = new File(config.getString(section + ".destinationPath"));
    }


    public final boolean hasMedia(File mediaPath) {
        if (fileFilterExclude.stream().anyMatch(fileExclude -> mediaPath.getName().contains(fileExclude))) {
            log.info("File found in the excluded list: " + mediaPath.getName());
            return false;
        }

        if (mediaPath.isDirectory()) {
            for (File subdir : FileUtils.listFiles(mediaPath)) {
                if (hasMedia(subdir))
                    return true;
            }
        }
        return anyMatchingMedia(mediaPath);
    }


    protected abstract boolean anyMatchingMedia(File mediaPath);


    public boolean includeFile(File mediaPath) {
        if (mediaPath.isDirectory()) {
            for (File subdir : FileUtils.listFiles(mediaPath)) {
                if (includeFile(subdir))
                    return true;
            }
        }
        return fileExtensions.stream()
                .anyMatch(fileFilter -> mediaPath.getName().endsWith(fileFilter));
    }


    public List<CopyFileAction> process(File mediaPath) {
        List<CopyFileAction> returnValue = new LinkedList<>();
        if (hasMedia(mediaPath)) {
            returnValue.addAll(findFilesToCopy(mediaPath, destinationFolder));
        }
        return returnValue;
    }


    protected Collection<CopyFileAction> findFilesToCopy(File mediaPath, File basePath) {
        List<CopyFileAction> returnValue = new LinkedList<>();
        if (mediaPath == null || !mediaPath.exists()) {
            return returnValue;
        }
        if (mediaPath.isDirectory()) {
            for (File subdir : FileUtils.listFiles(mediaPath)) {
                returnValue.addAll(findFilesToCopy(subdir, new File(basePath, mediaPath.getName())));
            }
        } else {
            if (includeFile(mediaPath)) {
                createFileAction(mediaPath, basePath)
                        .ifPresent(returnValue::add);
            }
        }

        return returnValue;
    }


    public abstract Optional<CopyFileAction> createFileAction(File mediaPath, File basePath);

}
