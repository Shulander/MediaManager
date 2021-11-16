package us.vicentini.mediamanager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import us.vicentini.mediamanager.util.FileUtils;

import java.io.File;

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

        log.info("************************************************************");
        log.info("******************** Media Manager Started *****************");
        log.info("************************************************************");

        log.info("args: " + args.length);
        if (args.length == 0 || args.length > 2) {
            log.warn("This program supports 1 or 2 arguments:");
            log.warn("1 argument: it uses the direct path to the media");
            log.warn("2 argument: the first argument has the base media path and the second as specific path");
            return;
        }

        Configuration config = new PropertiesConfiguration("config/config-dev.properties");
        MediaManager mediaManager = new MediaManager();
        mediaManager.load(config, "main");

        File mediaPath = getMediaPath(args);

        log.info("processing media: " + mediaPath.getAbsolutePath());
        if (mediaPath.exists()) {
            mediaManager.process(mediaPath);
        }
        log.info("********************* Media Manager Ended ******************");
    }


    private File getMediaPath(String[] args) {
        File mediaPath = new File(args[0].replace("\"", ""));
        if (args.length > 1 && mediaPath.isDirectory()) {
            String regex = "[.|\\s]";
            String[] subNames = args[1].replace("\"", "").split(regex);
            return FileUtils.findMostProbablySubDirectories(subNames, mediaPath)
                    .orElse(mediaPath);
        }
        return mediaPath;
    }

}
