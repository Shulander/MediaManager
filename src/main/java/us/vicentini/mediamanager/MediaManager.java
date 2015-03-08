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

/**
 *
 * @author Shulander
 */
public class MediaManager {
    private final static Log log = LogFactory.getLog(MediaManager.class);

    private List<AbstractFileFilter> fileFilters;
    
    void load(Configuration config, String mainmediasections) {
        fileFilters = new LinkedList<>();
        
        for (String mediaSections : config.getStringArray("main.mediasections")) {
            try {
                AbstractFileFilter newFileFilter = (AbstractFileFilter) Class.forName(config.getString(mediaSections)).newInstance();
                newFileFilter.load(config, mediaSections);
                fileFilters.add(newFileFilter);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                log.error("Error instantiating "+mediaSections+": "+ex.getMessage(), ex);
            }
        }
    }
    
    public void process(File mediaPath) {
        List<CopyFileAction> filesToCopy = new LinkedList<>();
        
        for (AbstractFileFilter fileFilter : fileFilters) {
            filesToCopy.addAll(fileFilter.process(mediaPath));
            if(!filesToCopy.isEmpty()) {
                break;
            }
        };
        
        for (CopyFileAction action : filesToCopy) {
            try {
                action.process();
            } catch (IOException ex) {
                log.error("Error while coping the file "+action.toString()+" ", ex);
            }
        }
        
    }
    
}
