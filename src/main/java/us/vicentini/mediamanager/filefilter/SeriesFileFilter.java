package us.vicentini.mediamanager.filefilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import us.vicentini.mediamanager.Main;
import us.vicentini.mediamanager.actions.CopyFileAction;

/**
 *
 * @author Shulander
 */
public class SeriesFileFilter extends AbstractFileFilter {
    private final static Log log = LogFactory.getLog(SeriesFileFilter.class);

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

    public static void main(String[] args) {
        Pattern p = Pattern.compile("(.*?)[.\\s][sS](\\d{2})[eE](\\d{2})(.*)");

        String[] tests = {"xyz title name S01E02 bla bla",
            "bla bla title name.S03E04",
            "the season title name s05e03",
            "Big Hero   6 3D S01E03 2014 1080p BRRip Half-OU x264 AC3-JYK"};

        for (String s : tests) {
            Matcher m = p.matcher(s);
            if (m.matches()) {
                System.out.printf("Name: %-23s Season: %s Episode: %s [%s]%n",
                        m.group(1), m.group(2), m.group(3), m.group(4));
            }
        }

        for (String split : "Big.Hero 6".split("(\\.|\\s)")) {
            System.out.println(split);
        }

    }

    @Override
    public boolean hasMedia(File mediaPath) {
        if(fileFilterExclude.stream().anyMatch((fileExclude)-> (mediaPath.getName().contains(fileExclude)))) {
            log.info("File found in the excluded list: "+mediaPath.getName());
            return false;
        }
        
        if (mediaPath.isDirectory()) {
            for (File subdir : mediaPath.listFiles()) {
                if (hasMedia(subdir)) {
                    return true;
                }
            }
        }

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
        if(basePath.exists()) {
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
