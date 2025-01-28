package de.vkeimes.sendmail;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import de.vkeimes.sendmail.util.PasswordService;

public class SendMail {

	static String HOST = "host";
	static String PORT = "port";
	static String AUTH = "auth";
	static String STARTTLS = "starttls";
	static String SSLTRUST = "ssltrust";
	static String SSLPROTOCOLS = "sslprotocols";
	static String SSLTIMEOUT = "ssltimeout";
	static String MAILUSER = "mailuser";
	static String MAILPASS = "mailpass";
	static String SENDER = "sender";
	static String SUBJECT = "subject";
	static String BODYTEXT = "bodytext";
	static String BODYFILE = "bodyfile";
	static String MIMETYPE = "mimetype";
	static String RECIPIENTS = "recipients";
	static String RECIPIENTSCC = "recipientscc";
	static String RECIPIENTSBCC = "recipientsbcc";
	static String ATTACHEMENTS = "attachments";

	// Defaultwerte
	static String TRUE = "true";
	static String EMPTY = "";
	static String PORTDEF = "587";
	static String SSLPROTOCOLSDEF = "TLSv1.2";
	static String SSLTIMEOUTDEF = "10000";
	static String MIMETYPETEXT = "text/plain";
	static String MIMETYPEHTML = "text/html";

	static MailProperties props = new MailProperties();
	static boolean verbose = false;
	static boolean testmode = false;
	static String iniFilename = SendMail.class.getSimpleName() + ".ini";;
	
	/**
	 * Startbare Programmklasse
	 * @param args Aufrufparameter:
	 * -r Empfänger
	 * -s Betreff
	 * -b Mailtext
	 * -f Datei mit Mailtext (überschreibt -b)
	 * -a Datei(en) als Anhang
	 * -i ini-Dateiname
	 * -v
	 * -t
	 * -h
	 * -enc encrypt password
	 */
	public static void main(String[] args) {
		
		// Konfigurationsfile bestimmen
		readIniFilename(args);
		
		// Konfiguration einlesen
		readConfig();
		
		// Umgebungsvariablen einlesen
		readEnvironment();
		
		// Komandozeilenparameter einlesen
		readCmdLinePars(args);
		
		// Parameter validieren und protokollieren
		verifyParameters();
		
		// Mail versenden
		sendMail();
	}

	/**
	 *  Konfigurationsdatei einlesen
	 */
	private static void readConfig() {
		try {
			Ini ini = new Ini(new File(iniFilename));

			Preferences prefs = new IniPreferences(ini).node("smtp");
			props.setHost(prefs.get(HOST, null));
			props.setPort(prefs.get(PORT, PORTDEF));
			props.setAuth(prefs.get(AUTH, TRUE));
			props.setStarttls(prefs.get(STARTTLS, TRUE));
			props.setSslTrust(prefs.get(SSLTRUST, prefs.get(HOST, null)));
			props.setSslProtocols(prefs.get(SSLPROTOCOLS, SSLPROTOCOLSDEF));
			props.setSslTimeout(prefs.get(SSLTIMEOUT, SSLTIMEOUTDEF));
			props.setMailUser(prefs.get(MAILUSER, EMPTY));
			props.setMailPass(prefs.get(MAILPASS, EMPTY));
			props.setSender(prefs.get(SENDER, null));
			
			prefs = new IniPreferences(ini).node("mail");
			props.setSubject(prefs.get(SUBJECT, EMPTY));
			props.setBodyText(prefs.get(BODYTEXT, EMPTY));
			props.setBodyFile(prefs.get(BODYFILE, EMPTY));
			props.setMimeType(prefs.get(MIMETYPE, EMPTY));
			props.setRecipients(prefs.get(RECIPIENTS, null));
			props.setRecipientsCc(prefs.get(RECIPIENTSCC, EMPTY));
			props.setRecipientsBcc(prefs.get(RECIPIENTSBCC, EMPTY));
			props.setAttachments(prefs.get(ATTACHEMENTS, EMPTY));

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Umgebungsvariablen einlesen
	 */
	private static void readEnvironment() {
		if (System.getenv(HOST) != null) { props.setHost(System.getenv(HOST)); }
		if (System.getenv(PORT) != null) { props.setPort(System.getenv(PORT)); }
		if (System.getenv(AUTH) != null) { props.setAuth(System.getenv(AUTH)); }
		if (System.getenv(STARTTLS) != null) { props.setStarttls(System.getenv(STARTTLS)); }
		if (System.getenv(SSLTRUST) != null) { props.setSslTrust(System.getenv(SSLTRUST)); }
		if (System.getenv(SSLPROTOCOLS) != null) { props.setSslProtocols(System.getenv(SSLPROTOCOLS)); }
		if (System.getenv(SSLTIMEOUT) != null) { props.setSslTimeout(System.getenv(SSLTIMEOUT)); }
		if (System.getenv(MAILUSER) != null) { props.setMailUser(System.getenv(MAILUSER)); }
		if (System.getenv(MAILPASS) != null) { props.setMailPass(System.getenv(MAILPASS)); }
		if (System.getenv(SENDER) != null) { props.setSender(System.getenv(SENDER)); }
		if (System.getenv(SUBJECT) != null) { props.setSubject(System.getenv(SUBJECT)); }
		if (System.getenv(BODYTEXT) != null) { props.setBodyText(System.getenv(BODYTEXT)); }
		if (System.getenv(BODYFILE) != null) { props.setBodyFile(System.getenv(BODYFILE)); }
		if (System.getenv(MIMETYPE) != null) { props.setMimeType(System.getenv(MIMETYPE)); }
		if (System.getenv(RECIPIENTS) != null) { props.setRecipients(System.getenv(RECIPIENTS)); }
		if (System.getenv(RECIPIENTSCC) != null) { props.setRecipientsCc(System.getenv(RECIPIENTSCC)); }
		if (System.getenv(RECIPIENTSBCC) != null) { props.setRecipientsBcc(System.getenv(RECIPIENTSBCC)); }
		if (System.getenv(ATTACHEMENTS) != null) { props.setAttachments(System.getenv(ATTACHEMENTS)); }
	}

	/**
	 * Ini-Filename aus Aufrufparametern einlesen
	 */
	private static void readIniFilename(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i")) {
				iniFilename = args[i++ + 1];
			}
		}
	}

	/**
	 * Aufrufparameter einlesen
	 */
	private static void readCmdLinePars(String[] args) {
		try {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-enc")) {
					encryptPassword(args[i++ + 1]);
				} else if (args[i].equals("-r")) {
					props.setRecipients(args[i++ + 1]);
				} else if (args[i].equals("-rc")) {
					props.setRecipientsCc(args[i++ + 1]);
				} else if (args[i].equals("-rb")) {
					props.setRecipientsBcc(args[i++ + 1]);
				} else if (args[i].equals("-s")) {
					props.setSubject(args[i++ + 1]);
				} else if (args[i].equals("-b")) {
					props.setBodyText(args[i++ + 1]);
				} else if (args[i].equals("-f")) {
					props.setBodyFile(args[i++ + 1]);
					setFinalMimetype();
				} else if (args[i].equals("-a")) {
					props.setAttachments(args[i++ + 1]);
				} else if (args[i].equals("-v")) {
					verbose = true;
				} else if (args[i].equals("-t")) {
					verbose = true;
					testmode = true;
				} else if (args[i].equals("-h")) {
					usageAndEnd(0);
				} else {
					usageAndEnd(1);
				}
			} 
		} catch(ArrayIndexOutOfBoundsException e) {
			usageAndEnd(1);
		}
	}

	/**
	 * Verifizieren, ob alle notwendigen Werte (host, sender, recipients) vorhanden sind:
	 */
	private static void verifyParameters() {

		if ((props.getHost() == null) || props.getHost().isBlank()) {
			System.err.println("PROGRAM TERMINATION! Missing value for \"host\" (SMTP servername)!");
			System.exit(3);
		}
		if ((props.getSender() == null) || props.getSender().isBlank()) {
			System.err.println("PROGRAM TERMINATION! Missing value for \"sender\" (sender email address(es))!");
			System.exit(4);
		}
		if ((props.getRecipients() == null) || props.getRecipients().isBlank()) {
			System.err.println("PROGRAM TERMINATION! Missing value for \"recipients\" (recipient email address(es))!");
			System.exit(5);
		}

		// Protokollierung
		log("Configuration file:  " + iniFilename);
		log("Mail host:           " + props.getHost());
		log("SMPT port:           " + props.getPort());
		log("Authentication:      " + props.getAuth());
		log("StartTLS enabled:    " + props.getStarttls());
		log("SSL trust host:      " + props.getSslTrust());
		log("SSL protocol(s):     " + props.getSslProtocols());
		log("SSL timeout [ms]:    " + props.getSslTimeout());
		log("Mail user name:      " + props.getMailUser());
		log("Mail user password:  " + props.getMailPass());
		log("Sender:              " + props.getSender());
		log("Subject:             " + props.getSubject());
		log("Mail body text:      " + props.getBodyText());
		log("Mail body textfile : " + props.getBodyFile());
		log("Mime type:           " + props.getMimeType());
		log("Attachment(s):       " + "[" + props.getAttachments() + "]");
		log("Recipient(s):        " + "[" + props.getRecipients() + "]");
		log("Recipient(s) CC:     " + "[" + props.getRecipientsCc() + "]");
		log("Recipient(s) BCC:    " + "[" + props.getRecipientsBcc() + "]");
		log("Test run only:       " + testmode + "\n");
	}

	private static void setFinalMimetype() {
		if (props.getMimeType().isBlank()) {
			if (props.getBodyFile().toLowerCase().contains(".htm")) { 
				props.setMimeType(MIMETYPEHTML);
			} else {
				props.setMimeType(MIMETYPETEXT);
			}
		}
	}

	private static void sendMail() {
		Mailer mailer = new Mailer(props);
		mailer.setTestmode(testmode);
		mailer.send();
	}

	private static void encryptPassword(String pwd) {
		System.out.println("Encrypted password for configuration file:\n" + PasswordService.encryptPasswort(pwd));
		System.exit(0);
	}

    private static void log(String msg) {
    	if (verbose) {
    		System.out.println(msg);
    	}
    }

	private static void usageAndEnd(int exitcode) {
 		System.err.println("Usage: java SendMail\n\n"
 				+ "Program arguments:\n"
 				+ "-r   Recipient(s)          [At least one recipient, separate with commas if more than one]\n"
 				+ "-rc  Recipient(s) CC       [Recipient(s) CC, separate with commas if more than one]\n"
 				+ "-rb  Recipient(s) BCC      [Recipient(s) BCC, separate with commas if more than one]\n"
 				+ "-s   Subject               [Subject text]\n"
 				+ "-b   Mail body             [Mail body text]\n"
 				+ "-f   Mail body text file   [File used as mail body (overrides -b)]\n"
 				+ "-i   Configuration file    [Default is \"SendMail.ini\"]\n"
 				+ "-a   File(s)               [Attachment(s), separate with commas if more than one]\n"
 				+ "-v                         [Log output on console]\n"
 				+ "-t                         [Test run without sending mail, forces -v]\n"
 				+ "-h                         [Show this text, ignores all other arguments]\n"
 				+ "-enc Password              [Encrypt password, ignores all other arguments]\n\n"
 				+ "All parameters are optional.\n\n"
 				+ "All parameters (except -v and -h) ​​can be defined in the configuration file, "
 				+ "in environment variables or as program arguments.\n"
 				+ "If defined multiple times, values from the configuration file are "
 				+ "overwritten by values from environment variables.\n"
 				+ "The environment variables must be named exactly like the key names "
 				+ "in the configuration file (i.e. lowercase).\n"
 				+ "The program arguments have the highest priority, i.e. they also overwrite environment variables.\n");
		System.exit(exitcode);
	}
}
