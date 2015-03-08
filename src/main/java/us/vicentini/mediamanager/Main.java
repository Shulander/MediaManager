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
        log.info("*******************************************************");
        log.info("*******************************************************");
        log.info(config.getString("main.version"));
        
        for (String mediaSections : config.getStringArray("main.mediasections")) {
            log.info("******** "+mediaSections+" ********");
            for (String fileExtension : config.getStringArray(mediaSections+".fileextensions")) {
                log.info(fileExtension);    
            }
            for (String fileExtension : config.getStringArray(mediaSections+".fileFilters")) {
                log.info(fileExtension);    
            }
            log.info(config.getString(mediaSections+".destinationPath"));
        }
        
        log.info("Hello this is an info message");
        log.info("hello world!");
        log.debug("Debug message");
        log.error("Error message");
        
        log.info("*******************************************************");
        log.info("*******************************************************");
        
    }
}
