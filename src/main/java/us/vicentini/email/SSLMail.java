package us.vicentini.email;

import java.util.Properties;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author Shulander
 */
public class SSLMail extends AbstractMail {
    
    @Override
    protected Properties loadImpl(Configuration config, String section) {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getString(section+".host"));
        props.put("mail.smtp.socketFactory.port", config.getString(section+".port"));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", config.getString(section+".port"));

        return props;
    }
    
}
