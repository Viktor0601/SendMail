package de.vkeimes.sendmail;

import java.io.File;
import java.util.Properties;

import de.vkeimes.sendmail.util.AsciiReader;
import de.vkeimes.sendmail.util.ByteArrayDataSource;
import de.vkeimes.sendmail.util.PasswordService;
import jakarta.activation.DataHandler;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class Mailer {
	
	private MailProperties props;
	private boolean testOnly = false;
	
	public Mailer(MailProperties props) {
		this.props = props;
	}

	public void setTestmode(boolean testmode) {
		this.testOnly = testmode;
	}
	
	public void send() {
        try {
            Properties myProps = new Properties();
            myProps.put("mail.smtp.host", props.getHost());
            myProps.put("mail.smtp.port", props.getPort());
            myProps.put("mail.smtp.connectiontimeout", props.getSslTimeout());
            myProps.put("mail.smtp.starttls.enable", props.getStarttls());
            myProps.put("mail.smtp.auth", props.getAuth());
            myProps.put("mail.smtp.ssl.trust", props.getSslTrust());
            myProps.put("mail.smtp.ssl.protocols", props.getSslProtocols());
            
            Session session = Session.getDefaultInstance(myProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(props.getMailUser(), PasswordService.decryptPasswort(props.getMailPass()));
                }
            });
            MimeMessage message = new MimeMessage(session);

            String[] recips = props.getRecipientsAsArray();
            InternetAddress[] recipsIA = new InternetAddress[recips.length];
            for (int i = 0; i < recips.length; i++) {
                recipsIA[i] = new InternetAddress(recips[i]);
            }
            message.setRecipients(Message.RecipientType.TO, recipsIA);

            String[] recipsCc = props.getRecipientsCcAsArray();
            InternetAddress[] recipsCcIA = new InternetAddress[recipsCc.length];
            for (int i = 0; i < recipsCc.length; i++) {
            	if (!recipsCc[i].isBlank()) {
            		recipsCcIA[i] = new InternetAddress(recipsCc[i]);
            	}
            }
            if (recipsCcIA != null) {
                message.setRecipients(Message.RecipientType.CC, recipsCcIA);
            }
            
            String[] recipsBcc = props.getRecipientsBccAsArray();
            InternetAddress[] recipsBccIA = new InternetAddress[recipsBcc.length];
            for (int i = 0; i < recipsBcc.length; i++) {
            	if (!recipsBcc[i].isBlank()) {
            		recipsBccIA[i] = new InternetAddress(recipsBcc[i]);
            	}
            }
            if (recipsBccIA != null) {
            	message.setRecipients(Message.RecipientType.BCC, recipsBccIA);
            }

            message.setFrom(new InternetAddress(props.getSender()));
            message.setSubject(props.getSubject());
            
            Multipart multipart = new MimeMultipart();

            // Mail-Body aus Text erstellen
            BodyPart messageBodyPart = new MimeBodyPart(); 
            messageBodyPart.setText(props.getBodyText());

            // Wenn eine Datei mit Inhalt existiert, wird der Body mit deren Inhalt überschrieben
            if (!props.getBodyFile().isBlank()) {
            	if (new File(props.getBodyFile()).exists()) {
            		StringBuffer sbFile_Data = new StringBuffer();
            		AsciiReader tmpReader = new AsciiReader(props.getBodyFile());
            		sbFile_Data = tmpReader.getDataAsStringBuffer();
            		messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(
            				sbFile_Data.toString(), props.getMimeType())));
            	} else {
            		fileNotFoundWarning(props.getBodyFile());
            	}
            }
            multipart.addBodyPart(messageBodyPart);

            // Anhänge erstellen
            String[] atts = props.getAttachmentsAsArray();
            MimeBodyPart[] attachmentParts = new MimeBodyPart[atts.length];
            for (int i = 0; i < atts.length; i++) {
            	if ( !atts[i].isBlank() ) {
	            	String attName = atts[i].trim();
	            	File att = new File(attName);
	            	if (att.exists()) {
		            	attachmentParts[i] = new MimeBodyPart();
		            	attachmentParts[i].attachFile(att);
		                multipart.addBodyPart(attachmentParts[i]);
	            	} else {
	            		fileNotFoundWarning(attName);
	            	}
            	}
            }

            // Alle Teile als Content setzen...
            message.setContent(multipart);

            // ... und senden
            if (!testOnly) {
                Transport.send(message);
            }

        } catch (MessagingException e) {
            handleException(e);
        } catch (Exception ex) {
            handleException(ex);
        }
	}

	private void fileNotFoundWarning(String filename) {
		System.out.println("WARNING! The specified file \"" + filename + "\" was not found.");
	}

    private void handleException(Throwable exception) {
        System.out.println("--- MAILER SEND EXCEPTION --------");
        exception.printStackTrace(System.out);
        System.out.println("--- END OF EXCEPTION -------------");
        System.exit(9);
    }
}
