package us.vicentini.mediamanager.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileFilter;
import java.util.Optional;

@UtilityClass
public class FileUtils {


    public static File[] listFiles(File file) {return listFiles(file, null);
    }

    public static File[] listFiles(File file, FileFilter filter) {
        return Optional.ofNullable(file)
                .map(fileFilter -> fileFilter.listFiles(filter))
                .orElse(new File[0]);
    }
}
