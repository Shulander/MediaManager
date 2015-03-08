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
        mediaManager.load(config, "main.mediasections");
        
        for (String arg : args) {
            File newFile = new File(arg);
            if(newFile.exists()) {
                mediaManager.process(newFile);
            }
        }
    }
}
