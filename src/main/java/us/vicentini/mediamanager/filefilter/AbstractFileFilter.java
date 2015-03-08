package us.vicentini.mediamanager.filefilter;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import us.vicentini.mediamanager.Main;
import us.vicentini.mediamanager.actions.CopyFileAction;

/**
 *
 * @author Shulander
 */
public abstract class AbstractFileFilter {

    private final static Log log = LogFactory.getLog(Main.class);

    protected String fileFilterName;
    protected List<String> fileFilterList;
    protected List<String> fileExtensions;
    protected File destinationFolder;

    public void load(Configuration config, String section) {
        fileFilterName = section;
        this.fileFilterList = new LinkedList<>();
        config.getList(section + ".fileFilter").stream().forEach((fileFilter) -> {
            fileFilterList.add(fileFilter.toString());
        });

        this.fileExtensions = new LinkedList<>();
        config.getList(section + ".fileextensions").stream().forEach((fileFilter) -> {
            fileExtensions.add(fileFilter.toString());
        });

        this.destinationFolder = new File(config.getString(section + ".destinationPath"));
    }
    
    public boolean hasMedia(File mediaPath) {
        if(mediaPath.isDirectory()) {
            for (File subdir : mediaPath.listFiles()) {
                if(hasMedia(subdir))
                    return true;
            }
        }
        return fileFilterList.stream().anyMatch((fileFilter) -> (mediaPath.getName().contains(fileFilter)));
    }

    public boolean includeFile(File mediaPath) {
        
        if(mediaPath.isDirectory()) {
            for (File subdir : mediaPath.listFiles()) {
                if(includeFile(subdir))
                    return true;
            }
        }
        return fileExtensions.stream().anyMatch((fileFilter) -> (mediaPath.getName().endsWith(fileFilter)));
    }
    
    public List<CopyFileAction> process(File mediaPath) {
        List<CopyFileAction> returnValue = new LinkedList<>();
        if(hasMedia(mediaPath)) {
            returnValue.addAll(reviewFiles(mediaPath, destinationFolder));
        }
        return returnValue;
    }

    protected Collection<? extends CopyFileAction> reviewFiles(File mediaPath, File basePath) {
        List<CopyFileAction> returnValue = new LinkedList<>();
        if(mediaPath == null || !mediaPath.exists()) {
            return returnValue;
        }
        if(mediaPath.isDirectory()) {
            for (File subdir : mediaPath.listFiles()) {
                returnValue.addAll(reviewFiles(subdir, new File(basePath, mediaPath.getName())));
            }
        } else {
            if(includeFile(mediaPath)) {
                returnValue.add(createFileAction(mediaPath, basePath));
            }
        }
        
        return returnValue;
    }
    
    public abstract CopyFileAction createFileAction(File mediaPath, File basePath);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("******** ").append(fileFilterName).append(" ********\n");
        sb.append("******** fileFilterList ********\n");
        fileFilterList.stream().forEach((fileExtension) -> {
            sb.append(fileExtension).append(", ");
        });
        sb.append("******** fileExtensions ********\n");
        fileExtensions.stream().forEach((fileExtension) -> {
            sb.append(fileExtension).append(", ");
        });
        sb.append("\ndestinationFolder: ").append(destinationFolder.getAbsolutePath());
        return sb.toString();
    }

}
