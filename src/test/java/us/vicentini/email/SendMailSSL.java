package us.vicentini.email;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
public class SendMailSSL {

    @SneakyThrows
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                                                     getSuperSecureCredentials());

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("email@from.com"));
        message.setRecipients(Message.RecipientType.TO,
                              InternetAddress.parse("email@to.com"));
        message.setSubject("Testing Subject");
        message.setText("Dear Mail Crawler,"
                        + "\n\n No spam to my email, please!");

        Transport.send(message);

        log.info("Done");

    }


    private static Authenticator getSuperSecureCredentials() {
        return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("email@account.com",
                                                  "supersecurecredentials");
            }
        };
    }
}
