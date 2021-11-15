package us.vicentini.mediamanager.notification;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import us.vicentini.email.AbstractMail;
import us.vicentini.mediamanager.actions.CopyFileAction;

/**
 *
 * @author Shulander
 */
public class EmailNotification extends AbstractNotification {
    private String to;
    private String subject;
    private String message;
    private AbstractMail mailService = null;

    @Override
    protected void loadImpl(Configuration config, String section) {
        to = config.getString(section+".to");
        subject = config.getString(section+".subject");
        message = config.getString(section + ".message");
        try {
            mailService = (AbstractMail)Class.forName(config.getString(section+".impl")).newInstance();
            mailService.load(config, section+".impl");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(EmailNotification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean sendNotification(List<CopyFileAction> actions) {
        StringBuilder sb = new StringBuilder();
        if(actions == null || actions.isEmpty()) {
            return true;
        }
        actions.forEach(action -> {
            sb.append(action.getDestinationPath().getAbsolutePath()).append("\\");
            sb.append("<b>").append(action.getFromFile().getName()).append("</b>");
            sb.append("<br />");
        });
        
       return mailService.sendMessage(to, subject, message.replace("[FILES]", sb.toString()));
    }
}
