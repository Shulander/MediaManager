package us.vicentini.mediamanager.filefilter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.WordUtils;
import us.vicentini.mediamanager.actions.CopyFileAction;
import us.vicentini.mediamanager.util.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    public CopyFileAction createFileAction(File mediaPath, File basePath) {
        for (Pattern pattern : patterns) {
            Matcher m = pattern.matcher(mediaPath.getName());
            if (m.matches()) {
                String[] subnames = m.group(1).split("[.|\\s]");
                File newBasePath = defineTargetDirectory(subnames, basePath);
                return new CopyFileAction(mediaPath, new File(newBasePath, "S" + m.group(2)));
            }
        }
        return null;
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
                returnValue.add(createFileAction(mediaPath, basePath));
            }
        }

        return returnValue;
    }


    private File defineTargetDirectory(String[] subNames, File basePath) {
        if (basePath.isDirectory()) {
            int fromIndex = 0;
            List<File> baseList = Arrays.asList(FileUtils.listFiles(basePath, File::isDirectory));
            for (String subName : subNames) {
                List<File> subList = findMatchingFolders(fromIndex, baseList, subName);
                if (subList.size() == 1) {
                    return subList.get(0);
                }
                baseList = subList;
                fromIndex += subName.length() + 1;
            }
        }
        return createNewSubDir(subNames, basePath);
    }


    private List<File> findMatchingFolders(int fromIndex, List<File> baseList, String subName) {
        return baseList.stream()
                .filter(directory -> {
                    int index = directory.getName().toLowerCase().indexOf(subName.toLowerCase(), fromIndex);
                    return index >= 0 && index <= fromIndex + subName.length();
                })
                .collect(Collectors.toList());
    }


    private File createNewSubDir(String[] subnames, File basePath) {
        String newDirectory = Arrays.stream(subnames)
                .map(WordUtils::capitalize)
                .collect(Collectors.joining("."));

        return new File(basePath, newDirectory);
    }

}
