package us.vicentini.email;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;

import java.util.Properties;

@Slf4j
public class NoMail extends AbstractMail {
    @Override
    protected Properties loadImpl(Configuration config, String section) {
        return new Properties();
    }


    @Override
    public boolean sendMessage(String to, String subject, String message) {
        log.info("Sending message");
        log.info("to: {}", to);
        log.info("subject: {}", subject);
        log.info("message: {}", message);
        return true;
    }
}
