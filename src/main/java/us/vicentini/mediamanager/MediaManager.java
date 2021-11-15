package us.vicentini.mediamanager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import us.vicentini.mediamanager.actions.CopyFileAction;
import us.vicentini.mediamanager.filefilter.AbstractFileFilter;
import us.vicentini.mediamanager.notification.AbstractNotification;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Shulander
 */
@Slf4j
public class MediaManager {

    private List<AbstractFileFilter> fileFilters;

    private List<AbstractNotification> notifications;


    void load(Configuration config, String mainConfigurationSection) {
        fileFilters = new LinkedList<>();

        for (String mediaSections : config.getStringArray(mainConfigurationSection + ".mediasections")) {
            try {
                AbstractFileFilter newFileFilter =
                        (AbstractFileFilter) Class.forName(config.getString(mediaSections)).newInstance();
                newFileFilter.load(config, mediaSections);
                fileFilters.add(newFileFilter);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                log.error("Error instantiating " + mediaSections + ": " + ex.getMessage(), ex);
            }
        }

        notifications = new LinkedList<>();
        for (String notificationSection : config.getStringArray(mainConfigurationSection + ".notifications")) {

            try {
                AbstractNotification newNotification =
                        (AbstractNotification) Class.forName(config.getString(notificationSection)).newInstance();
                newNotification.load(config, notificationSection);
                notifications.add(newNotification);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                log.error("Error instantiating " + notificationSection + ": " + ex.getMessage(), ex);
            }
        }
    }


    public void process(File mediaPath) {
        List<CopyFileAction> filesToCopy = new LinkedList<>();
        log.info("searching files to copy");
        for (AbstractFileFilter fileFilter : fileFilters) {
            filesToCopy.addAll(fileFilter.process(mediaPath));
            if (!filesToCopy.isEmpty()) {
                break;
            }
        }

        log.info("process copy");
        for (CopyFileAction action : filesToCopy) {
            try {
                log.info("[COPY] " + action.toString());
                action.process();
            } catch (IOException ex) {
                log.error("Error while coping the file {}: {}", action, ex.getMessage(), ex);
            }
        }
        log.info("send notifications");
        for (AbstractNotification notification : notifications) {
            notification.sendNotification(filesToCopy);
        }
        log.info("Done");
    }

}
