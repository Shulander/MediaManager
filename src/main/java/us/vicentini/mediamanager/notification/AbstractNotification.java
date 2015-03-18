package us.vicentini.mediamanager.notification;

import java.util.List;
import org.apache.commons.configuration.Configuration;
import us.vicentini.mediamanager.actions.CopyFileAction;

/**
 *
 * @author Shulander
 */
public abstract class AbstractNotification {
    
    public void load(Configuration config, String section) {
        loadImpl(config, section);
    }
    
    public abstract boolean sendNotification(List<CopyFileAction> actions);

    protected abstract void loadImpl(Configuration config, String section);
}
