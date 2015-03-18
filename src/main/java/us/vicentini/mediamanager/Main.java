package us.vicentini.mediamanager;

import java.io.File;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author Shulander
 */
public class Main {
    /* Get actual class name to be printed on */
    private final static Log log = LogFactory.getLog(Main.class);
    
    static {
        if((new File("config/log4j.xml")).exists()){
            DOMConfigurator.configure("config/log4j.xml");
        }else if((new File("config/log4j.properties")).exists()) {
            PropertyConfigurator.configure("config/log4j.properties");
        }
    }
    
    public static void main(String[] args) throws InterruptedException, ConfigurationException {
        Configuration config = new PropertiesConfiguration("config/config.properties");
        MediaManager mediaManager = new MediaManager();
        mediaManager.load(config, "main");
        
        log.info("************************************************************");
        log.info("******************** Media Manager Started *****************");
        log.info("************************************************************");
        
        log.info("args: "+args.length);
        if(args.length == 0 || args.length > 2) {
            log.info("This program supporsts 1 or 2 arguments:");
            log.info("1 argument: it uses the full for the media");
            log.info("2 argument: the first argument as a base path and the second as specific path");
        }
        
        File mediaPath = new File(args[0].replace("\"", ""));
        if(args.length >1 ){
            File subDir = new File(mediaPath, args[1].replace("\"", ""));
            if(mediaPath.exists() && mediaPath.isDirectory() && subDir.exists()) {
                log.info("concatening the subdirectory");
                mediaPath = subDir;
            }
        }
        
        log.info("processing media: "+mediaPath.getAbsolutePath());
        if(mediaPath.exists()) {
            mediaManager.process(mediaPath);
        }
        log.info("********************* Media Manager Ended ******************");
        
    }
}
