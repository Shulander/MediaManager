package us.vicentini.mediamanager.filefilter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.WordUtils;
import us.vicentini.mediamanager.actions.CopyFileAction;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
    public CopyFileAction createFileAction(File mediaPath, File basePath) {
        for (Pattern pattern : patterns) {
            Matcher m = pattern.matcher(mediaPath.getName());
            if (m.matches()) {
                String[] subnames = m.group(1).split("(\\.|\\s)");
                File newBasePath = findMostProbablySubdir(subnames, basePath);
                return new CopyFileAction(mediaPath, new File(newBasePath, "S" + m.group(2)));
            }
        }
        return null;
    }


    @Override
    protected Collection<? extends CopyFileAction> reviewFiles(File mediaPath, File basePath) {
        List<CopyFileAction> returnValue = new LinkedList<>();
        if (mediaPath == null || !mediaPath.exists()) {
            return returnValue;
        }
        if (mediaPath.isDirectory()) {
            for (File subdir : mediaPath.listFiles()) {
                returnValue.addAll(reviewFiles(subdir, basePath));
            }
        } else {
            if (includeFile(mediaPath)) {
                returnValue.add(createFileAction(mediaPath, basePath));
            }
        }

        return returnValue;
    }


    public File findMostProbablySubdir(String[] subnames, File basePath) {
        if (basePath.exists()) {
            int fromIndex = 0;
            List<File> baseList = new LinkedList<>();
            baseList.addAll(Arrays.asList(basePath.listFiles()));
            List<File> subList = new LinkedList<>();
            for (int i = 0; i < subnames.length; i++) {
                for (File serie : baseList) {
                    int index = serie.getName().toLowerCase().indexOf(subnames[i].toLowerCase(), fromIndex);
                    if (index >= 0 && index <= (fromIndex + subnames[i].length())) {
                        subList.add(serie);
                    }
                }
                if (subList.size() == 1) {
                    return subList.get(0);
                }
                baseList.clear();
                baseList.addAll(subList);
                subList.clear();
                fromIndex += subnames[i].length() + 1;
            }
        }
        return createNewSubDir(subnames, basePath);
    }


    private File createNewSubDir(String[] subnames, File basePath) {
        StringBuilder sb = new StringBuilder(WordUtils.capitalize(subnames[0]));

        for (int i = 1; i < subnames.length; i++) {
            sb.append(".").append(WordUtils.capitalize(subnames[i]));
        }
        return new File(basePath, sb.toString());
    }

}
