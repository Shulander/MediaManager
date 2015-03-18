package us.vicentini.email;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author Shulander
 */
public abstract class AbstractMail {

    private Properties props;
    private String user;
    private String password;

    public void load(Configuration config, String section) {
        user = config.getString(section + ".user");
        password = config.getString(section + ".password");
        props = loadImpl(config, section);
    }

    protected abstract Properties loadImpl(Configuration config, String section);

    public boolean sendMessage(String to, String subject, String strMessage) {
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(strMessage);

            Transport.send(message);

            System.out.println("Done");
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
