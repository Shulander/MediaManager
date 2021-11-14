package us.vicentini.mediamanager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
@SpringBootApplication
public class MediaManagerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication.run(MediaManagerApplication.class, args);
        log.info("APPLICATION FINISHED");
    }


    @Override
    public void run(String... args) throws ConfigurationException {
        log.info("EXECUTING : command line runner");

        for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
        }

        Configuration config = new PropertiesConfiguration("config/config-dev.properties");
        MediaManager mediaManager = new MediaManager();
        mediaManager.load(config, "main");

        log.info("************************************************************");
        log.info("******************** Media Manager Started *****************");
        log.info("************************************************************");

        log.info("args: " + args.length);
        if (args.length == 0 || args.length > 2) {
            log.info("This program supporsts 1 or 2 arguments:");
            log.info("1 argument: it uses the full for the media");
            log.info("2 argument: the first argument as a base path and the second as specific path");
            return;
        }

        for (String arg : args) {
            log.info("arg: " + arg);
        }

        File mediaPath = getMediaPath(args);

        log.info("processing media: " + mediaPath.getAbsolutePath());
        if (mediaPath.exists()) {
            mediaManager.process(mediaPath);
        }
        log.info("********************* Media Manager Ended ******************");
    }


    private File getMediaPath(String[] args) {
        File mediaPath = new File(args[0].replace("\"", ""));
        if (args.length > 1) {
            String[] subNames = args[1].replace("\"", "").split("(\\.|\\s)");
            File newBasePath = findMostProbablySubDirectories(subNames, mediaPath);
            if (mediaPath.isDirectory() && newBasePath != null) {
                log.info("concatenating the subdirectory");
                mediaPath = newBasePath;
            }
        }
        return mediaPath;
    }


    public static File findMostProbablySubDirectories(String[] subDirectoryNames, File basePath) {
        if (basePath.isDirectory()) {
            int fromIndex = 0;
            File[] files = Objects.requireNonNull(basePath.listFiles());
            List<File> baseList = new LinkedList<>(Arrays.asList(files));
            List<File> subList = new LinkedList<>();
            for (String subName : subDirectoryNames) {
                for (File serie : baseList) {
                    int index = serie.getName().toLowerCase().indexOf(subName.toLowerCase(), fromIndex);
                    if (serie.isDirectory() && index >= 0 && index <= (fromIndex + subName.length())) {
                        subList.add(serie);
                    }
                }
                if (subList.size() == 1) {
                    return subList.get(0);
                }
                baseList.clear();
                baseList.addAll(subList);
                subList.clear();
                fromIndex += subName.length() + 1;
            }
        }
        return null;
    }
}
