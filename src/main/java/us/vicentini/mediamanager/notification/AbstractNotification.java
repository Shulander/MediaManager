package us.vicentini.mediamanager.notification;

import org.apache.commons.configuration.Configuration;
import us.vicentini.mediamanager.actions.CopyFileAction;

import java.util.List;

/**
 * @author Shulander
 */
public abstract class AbstractNotification {

    public void load(Configuration config, String section) {
        loadImpl(config, section);
    }


    public abstract boolean sendNotification(List<CopyFileAction> actions);

    protected abstract void loadImpl(Configuration config, String section);
}
