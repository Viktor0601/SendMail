package de.vkeimes.sendmail;

public class MailProperties {

	private String host;
	private String port;
	private String auth;
	private String starttls;
	private String sslTrust;
	private String sslProtocols;
	private String sslTimeout;
	private String mailUser;
	private String mailPass;
	private String sender;

	private String subject;
	private String bodyText;
	private String bodyFile;
	private String mimeType;
	private String recipients;
	private String recipientsCc;
	private String recipientsBcc;
	private String attachments;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}

	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getStarttls() {
		return starttls;
	}
	public void setStarttls(String starttls) {
		this.starttls = starttls;
	}

	public String getSslTrust() {
		return sslTrust;
	}
	public void setSslTrust(String sslTrust) {
		this.sslTrust = sslTrust;
	}

	public String getSslProtocols() {
		return sslProtocols;
	}
	public void setSslProtocols(String sslProtocols) {
		this.sslProtocols = sslProtocols;
	}

	public String getSslTimeout() {
		return sslTimeout;
	}
	public void setSslTimeout(String sslTimeout) {
		this.sslTimeout = sslTimeout;
	}

	public String getMailUser() {
		return mailUser;
	}
	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public String getMailPass() {
		return mailPass;
	}
	public void setMailPass(String mailPass) {
		this.mailPass = mailPass;
	}
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBodyText() {
		return bodyText;
	}
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

	public String getBodyFile() {
		return bodyFile;
	}
	public void setBodyFile(String bodyFile) {
		this.bodyFile = bodyFile;
	}

	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getRecipients() {
		return recipients;
	}
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	public String[] getRecipientsAsArray() {
		return splitNoBlanks(recipients);
	}

	public String getRecipientsCc() {
		return recipientsCc;
	}
	public void setRecipientsCc(String recipientsCc) {
		this.recipientsCc = recipientsCc;
	}
	public String[] getRecipientsCcAsArray() {
		return splitNoBlanks(recipientsCc);
	}

	public String getRecipientsBcc() {
		return recipientsBcc;
	}
	public void setRecipientsBcc(String recipientsBcc) {
		this.recipientsBcc = recipientsBcc;
	}
	public String[] getRecipientsBccAsArray() {
		return splitNoBlanks(recipientsBcc);
	}
	
	public String getAttachments() {
		return attachments;
	}
	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
	public String[] getAttachmentsAsArray() {
		return attachments.split(",");
	}

	private static String[] splitNoBlanks(String str) {
		String[] all = str.split(",");
		int anz = 0;
		for (int i = 0; i < all.length; i++) {
			if (!all[i].trim().isBlank()) {
				anz++;
			}
		}
		String[] ret = new String[anz];
		anz = 0;
		for (int i = 0; i < all.length; i++) {
			if (!all[i].trim().isBlank()) {
				ret[anz] = all[i].trim();
				anz++;
			}
		}
		return ret;
	}
}
