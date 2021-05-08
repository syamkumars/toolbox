package com.syberhub.toolbox.email;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.syberhub.toolbox.ToolBoxConfig;

public class EmailService {

	// must enable less secure connections in gmail settings
	public static void sendGMailNotification(String title, String content, String contentType)
			throws AddressException, MessagingException, KeyManagementException, NoSuchAlgorithmException {

		sendMailSMTP("smtp.gmail.com", "587", "smtp.gmail.com", ToolBoxConfig.getProperty("GoogleUserId"),
				ToolBoxConfig.getProperty("GooglePassword"), ToolBoxConfig.getProperty("email.to.addresses"), title,
				content, contentType);
	}

	public static void sendMailSMTP(String host, String port, String sslTrust, String emailId, String password,
			String toAddresses, String subject, Object content, String contentType)
			throws AddressException, MessagingException {

		Properties prop = new Properties();
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", host);
		prop.put("mail.smtp.port", port);
		prop.put("mail.smtp.ssl.trust", sslTrust); // solves PKIX error
		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(emailId, password);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(emailId));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddresses));
		message.setSubject(subject);

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(content, contentType);

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		message.setContent(multipart);

		Transport.send(message);

	}

}
