package us.vicentini.mediamanager.filefilter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import us.vicentini.mediamanager.actions.CopyFileAction;
import us.vicentini.mediamanager.util.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shulander
 */
@Slf4j
public class SeriesFileFilter extends AbstractFileFilter {

    private List<Pattern> patterns;


    @Override
    public void load(Configuration config, String section) {
        super.load(config, section);
        patterns = new LinkedList<>();
        for (String fileFilter : fileFilterList) {
            Pattern p = Pattern.compile(fileFilter);
            patterns.add(p);
        }
    }


    @Override
    public boolean anyMatchingMedia(File mediaPath) {
        for (Pattern pattern : patterns) {
            Matcher m = pattern.matcher(mediaPath.getName());
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Optional<CopyFileAction> createFileAction(File mediaPath, File basePath) {
        for (Pattern pattern : patterns) {
            Matcher m = pattern.matcher(mediaPath.getName());
            if (m.matches()) {
                String[] subnames = m.group(1).split("[.|\\s]");
                File newBasePath = defineTargetDirectory(subnames, basePath);
                CopyFileAction copyFileAction = new CopyFileAction(mediaPath, new File(newBasePath, "S" + m.group(2)));
                return Optional.of(copyFileAction);
            }
        }
        return Optional.empty();
    }


    @Override
    protected Collection<CopyFileAction> findFilesToCopy(File mediaPath, File basePath) {
        List<CopyFileAction> returnValue = new LinkedList<>();
        if (mediaPath == null || !mediaPath.exists()) {
            return returnValue;
        }
        if (mediaPath.isDirectory()) {
            for (File subdir : FileUtils.listFiles(mediaPath)) {
                returnValue.addAll(findFilesToCopy(subdir, basePath));
            }
        } else {
            if (includeFile(mediaPath)) {
                createFileAction(mediaPath, basePath)
                        .ifPresent(returnValue::add);
            }
        }

        return returnValue;
    }


    private File defineTargetDirectory(String[] subNames, File basePath) {
        return FileUtils.findMostProbablySubDirectories(subNames, basePath)
                .orElseGet(() -> FileUtils.createNewSubDir(subNames, basePath));
    }


}
