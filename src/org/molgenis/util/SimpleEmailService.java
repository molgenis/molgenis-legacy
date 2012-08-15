package org.molgenis.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class SimpleEmailService implements EmailService
{
	private String smtpFromAddress = null;
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpFromAddress()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpFromAddress()
	 */
	public String getSmtpFromAddress()
	{
		return smtpFromAddress;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpFromAddress(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpFromAddress(java.lang.String)
	 */
	public void setSmtpFromAddress(String smtpFromAddress)
	{
		this.smtpFromAddress = smtpFromAddress;
	}

	private String smtpHostname = "localhost";
	private Integer smtpPort = 25;
	private String smtpUser = null;
	private String smtpAu = null;
	private String smtpProtocol = "smtps";
	
	public boolean email(String subject, String body, String toEmail, boolean deObf) throws EmailException
	{
		return email(subject, body, toEmail, deObf, "MOLGENIS user activation");
	}
	
	/**
	 * Send an email.
	 * @param subject
	 * @param body
	 * @param toEmail
	 * @param deObf
	 * @return
	 * @throws EmailException
	 */
	public boolean email(String subject, String body, String toEmail, boolean deObf, String sender) throws EmailException
	{
		//put in config
		Properties props = new Properties();
		props.put("mail.transport.protocol", smtpProtocol);	
		Session session = Session.getDefaultInstance(props, null);
		//session.setDebug(true);

		Message message = new MimeMessage(session);

		try
		{
			message.setFrom(new InternetAddress(this.smtpFromAddress, sender));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject(subject);
			message.setText(body);
			message.setSentDate(new Date());
			message.saveChanges();


			Transport transport = session.getTransport();
			transport.connect(smtpHostname, smtpPort, smtpUser, (deObf ? HtmlTools.fromSafeUrlStringO_b_f(smtpAu) : smtpAu));
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EmailException(e);
		}
	}
	
	public boolean email(String subject, String body, String toEmail, String fileAttachment, ByteArrayOutputStream outputStream, boolean deObf) throws EmailException
	{
		String sender = "MOLGENIS";
		return email(subject, body, toEmail, fileAttachment, outputStream, deObf, sender);
	}
	
	public boolean email(String subject, String body, String toEmail, String fileAttachment, ByteArrayOutputStream outputStream, boolean deObf, String sender) throws EmailException
	{
		Properties props = new Properties();
		props.put("mail.transport.protocol", smtpProtocol);	
		Session session = Session.getDefaultInstance(props, null);
		//session.setDebug(true);
		Message message = new MimeMessage(session);

		try
		{
			message.setFrom(new InternetAddress(this.smtpFromAddress, sender));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject(subject);
				
			message.setSentDate(new Date());
			//message.saveChanges();

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);
		    
		    Multipart multipart = new MimeMultipart();
	        MimeBodyPart htmlPart = new MimeBodyPart();        

	        htmlPart.setContent(body, "text/html");
	        multipart.addBodyPart(htmlPart);

	        MimeBodyPart attachment = new MimeBodyPart();
	        attachment.setFileName("attachment.xls");
	        //attachment.setContent(outputStream.toByteArray(), "application/vnd.ms-excel");
	        attachment.setContent(fileAttachment, "application/vnd.ms-excel");
	        multipart.addBodyPart(attachment);

	        message.setContent(multipart);
	        
	        message.setContent(multipart);
	        Transport transport = session.getTransport();
			transport.connect(smtpHostname, smtpPort, smtpUser, (deObf ? HtmlTools.fromSafeUrlStringO_b_f(smtpAu) : smtpAu));
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EmailException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#sendEmail(java.lang.String, java.lang.String, java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#email(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean email(String subject, String body, String toEmail) throws EmailException
	{
		return email(subject, body, toEmail, false);
	}
	
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpHostName()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpHostName()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpHostName()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpHostName()
	 */
	public String getSmtpHostname()
	{
		return smtpHostname;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpHostName(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpHostName(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpHostName(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpHostName(java.lang.String)
	 */
	public void setSmtpHostname(String smtpHostName)
	{
		this.smtpHostname = smtpHostName;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpHostPort()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpHostPort()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpHostPort()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpHostPort()
	 */
	public Integer getSmtpPort()
	{
		return smtpPort;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpHostPort(java.lang.Integer)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpHostPort(java.lang.Integer)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpHostPort(java.lang.Integer)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpHostPort(java.lang.Integer)
	 */
	public void setSmtpPort(Integer smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpAuthUser()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpAuthUser()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpAuthUser()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpAuthUser()
	 */
	public String getSmtpUser()
	{
		return smtpUser;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpAuthUser(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpAuthUser(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpAuthUser(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpAuthUser(java.lang.String)
	 */
	public void setSmtpUser(String smtpUser)
	{
		this.smtpUser = smtpUser;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpAuthPassword()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpAuthPassword()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpAuthPassword()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpAuthPassword()
	 */
	public String getSmtpAu()
	{
		return smtpAu;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpAuthPassword(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpAuthPassword(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpAuthPassword(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpAuthPassword(java.lang.String)
	 */
	public void setSmtpAu(String smtpAu)
	{
		this.smtpAu = smtpAu;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpProtocol()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#getSmtpProtocol()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpProtocol()
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#getSmtpProtocol()
	 */
	public String getSmtpProtocol()
	{
		return smtpProtocol;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpProtocol(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailServer#setSmtpProtocol(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpProtocol(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#setSmtpProtocol(java.lang.String)
	 */
	public void setSmtpProtocol(String smtpProtocol)
	{
		this.smtpProtocol = smtpProtocol;
	}
	
	public static class EmailException extends Exception
	{
		private static final long serialVersionUID = -7543170033863810367L;

		public EmailException(String message)
		{
			super(message);
		}
		
		public EmailException(Exception e)
		{
			super(e);
		}
	}
}
