package us.vicentini.mediamanager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import us.vicentini.mediamanager.actions.CopyFileAction;
import us.vicentini.mediamanager.filefilter.AbstractFileFilter;
import us.vicentini.mediamanager.notification.AbstractNotification;

/**
 *
 * @author Shulander
 */
public class MediaManager {

    private final static Log log = LogFactory.getLog(MediaManager.class);

    private List<AbstractFileFilter> fileFilters;
    
    private List<AbstractNotification> notificiations;

    void load(Configuration config, String mainmediasections) {
        fileFilters = new LinkedList<>();

        for (String mediaSections : config.getStringArray(mainmediasections + ".mediasections")) {
            try {
                AbstractFileFilter newFileFilter = (AbstractFileFilter) Class.forName(config.getString(mediaSections)).newInstance();
                newFileFilter.load(config, mediaSections);
                fileFilters.add(newFileFilter);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                log.error("Error instantiating " + mediaSections + ": " + ex.getMessage(), ex);
            }
        }

        notificiations = new LinkedList<>();
        for (String notificationSection : config.getStringArray(mainmediasections + ".notifications")) {

            try {
                AbstractNotification newNotification = (AbstractNotification) Class.forName(config.getString(notificationSection)).newInstance();
                newNotification.load(config, notificationSection);
                newNotification.sendNotification(null);
                notificiations.add(newNotification);
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
        };

        log.info("proccess copy");
        for (CopyFileAction action : filesToCopy) {
            try {
                log.info("[COPY] " + action.toString());
                action.process();
            } catch (IOException ex) {
                log.error("Error while coping the file " + action.toString() + ": " + ex.getMessage(), ex);
            }
        }
        log.info("send notifications");
        for (AbstractNotification notificiation : notificiations) {
            notificiation.sendNotification(filesToCopy);
        }
        log.info("Done");
    }

}
