package us.vicentini.email;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author Shulander
 */
@Slf4j
public abstract class AbstractMail {

    private Properties props;
    private String user;
    private String password;


    public void load(Configuration config, String section) {
        this.user = config.getString(section + ".user");
        this.password = config.getString(section + ".password");
        this.props = loadImpl(config, section);
    }


    protected abstract Properties loadImpl(Configuration config, String section);


    @SneakyThrows
    public boolean sendMessage(String to, String subject, String strMessage) {
        Session session = Session.getDefaultInstance(props, getAuthenticator());

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(strMessage, "text/html; charset=utf-8");

        Transport.send(message);

        log.info("Done");
        return true;
    }


    private Authenticator getAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        };
    }
}
