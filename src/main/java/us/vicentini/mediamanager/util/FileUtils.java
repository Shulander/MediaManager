package us.vicentini.mediamanager.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.WordUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class FileUtils {


    public static File[] listFiles(File file) {
        return listFiles(file, null);
    }


    public static File[] listFiles(File file, FileFilter filter) {
        return Optional.ofNullable(file)
                .map(f -> f.listFiles(filter))
                .orElse(new File[0]);
    }


    public Optional<File> findMostProbablySubDirectories(String[] subNames, File basePath) {
        if (basePath.isDirectory()) {
            int fromIndex = 0;
            List<File> baseList = Arrays.asList(FileUtils.listFiles(basePath, File::isDirectory));
            for (String subName : subNames) {
                List<File> subList = findMatchingFolders(fromIndex, baseList, subName);
                if (subList.size() == 1) {
                    return Optional.of(subList.get(0));
                }
                baseList = subList;
                fromIndex += subName.length() + 1;
            }
        }
        return Optional.empty();
    }


    public List<File> findMatchingFolders(int fromIndex, List<File> baseList, String subName) {
        return baseList.stream()
                .filter(directory -> {
                    int index = directory.getName().toLowerCase().indexOf(subName.toLowerCase(), fromIndex);
                    return index >= 0 && index <= fromIndex + subName.length();
                })
                .collect(Collectors.toList());
    }


    public File createNewSubDir(String[] subnames, File basePath) {
        String newDirectory = Arrays.stream(subnames)
                .map(WordUtils::capitalize)
                .collect(Collectors.joining("."));

        return new File(basePath, newDirectory);
    }
}
