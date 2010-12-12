package org.molgenis.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
	private String smtpPassword = null;
	private String smtpProtocol = "smtps";
	
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#sendEmail(java.lang.String, java.lang.String, java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.molgenis.util.email.EmailService#email(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean email(String subject, String body, String toEmail) throws EmailException
	{
		//Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		//put in config
		Properties props = new Properties();
		props.put("mail.transport.protocol", smtpProtocol);	
		Session session = Session.getDefaultInstance(props, null);
		//session.setDebug(true);

		Message message = new MimeMessage(session);

		try
		{
			message.setFrom(new InternetAddress(this.smtpFromAddress, "MOLGENIS user activation"));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject(subject);
			message.setText(body);
			message.setSentDate(new Date());
			message.saveChanges();

			Transport transport = session.getTransport();
			transport.connect(smtpHostname, smtpPort, smtpUser, smtpPassword);
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
	public String getSmtpAuthPassword()
	{
		return smtpPassword;
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
	public void setSmtpPassword(String smtpPassword)
	{
		this.smtpPassword = smtpPassword;
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
