package JAG.ca.mcmahon.data.Controllers;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Flags;
import javax.mail.search.SentDateTerm;

import JAG.ca.mcmahon.beans.MailBean;
import JAG.ca.mcmahon.beans.MailServerConfigurationBean;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailFilter;
import jodd.mail.EmailMessage;
import jodd.mail.ImapSslServer;
import jodd.mail.MailAddress;
import jodd.mail.MailException;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpSslServer;
import jodd.util.MimeTypes;

/**
 * @author Michael Mcmahon 
 *@since 9/16/15
 *@version 1.0
 *
 *	MailController allows the transmission and receiving of email.
 *	
 */
public class MailController 
{
	/**
	 * Default constructor 
	 */
	public MailController()
	{		
	}
	
	/**
	 * Allows the retrieval of all e-mails that have not been yet 
	 * seen from the gmail account that was passed by the configuration 
	 * bean.
	 *  May throw a mail exception if 
	 *  authentication fails.
	 * 
	 * @param configBean the server configuration bean(imap or pop3)
	 * @return all the e-mails that have not been yet seen
	 * @throws MailException
	 */
	@SuppressWarnings("deprecation")
	public final ArrayList<MailBean> getMail(final MailServerConfigurationBean configBean) throws MailException
	{	
		ImapSslServer imapServer = new ImapSslServer(configBean.getHostName(), configBean.getPortNumber(), configBean.getGmailEmail() , configBean.getGmailPassword());
		
		ArrayList<MailBean> emailBeans = null;
		
		ReceiveMailSession recievedMailailSession;
		recievedMailailSession =  imapServer.createSession();
		recievedMailailSession.open();
		ReceivedEmail[] emails = recievedMailailSession.receiveEmailAndMarkSeen(EmailFilter.filter().flag(Flags.Flag.SEEN, false));
		if(emails != null)
		{
			emailBeans =  new ArrayList<>();
			for(ReceivedEmail email : emails)
			{
				MailBean mail =  new MailBean();
				mail.setFrom(email.getFrom().getEmail());
				mail.setSubject(email.getSubject());
				Date sent =  email.getSentDate();
				Date received = email.getReceiveDate();
				mail.setSentDate(sent.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				mail.setRecievedDate(received.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				
				for(MailAddress address : email.getTo())
				{
					mail.getTosProperty().add(address.getEmail());
				}
				
				for(MailAddress address : email.getBcc())
				{
					mail.getBccsProperty().add(address.getEmail());
				}
				
				for(MailAddress address : email.getCc())
				{
					mail.getCcsProperty().add(address.getEmail());
				}
				for(MailAddress address : email.getReplyTo())
				{
					mail.getReplyTosProperty().add(address.getEmail());
				}
				// for each e-mail go through all the messages(text and html)
				List<EmailMessage> messages =  email.getAllMessages();

				List<EmailAttachment> attachments =  email.getAttachments();

				if(attachments != null)
				{
					for(EmailAttachment attachment : attachments)
					{
						
						if (attachment.getContentId() != null)
						{
	                        mail.getEmbedAttachmentProperty().add(attachment);
						}

	                    else
	                    {
							mail.getAttachmentsProperty().add(attachment);
	                    }
					}
				}
				
				for(EmailMessage msg : messages)
				{
					if(msg.getMimeType().equalsIgnoreCase("text/plain"))
					{
						mail.setTextMessage(msg.getContent());
					}
					else if(msg.getMimeType().equalsIgnoreCase("text/html"))
					{
						mail.setHtmlMessage(msg.getContent());
					}
				}

				mail.setMailStatus(1);
				emailBeans.add(mail);
			}
		}
			recievedMailailSession.close();
			return emailBeans;
	}

	/**
	 *  Allows the transmission of an e-mail using a email data bean
	 *  and a server configuration bean. May throw a mail exception if 
	 *  authentication fails.
	 * @param msg  the e-mail bean
	 * @param configBean the server configuration bean for transmission
	 * @return the message id from the email sent
	 * @throws MailException
	 */
	public final String sendMail(final MailBean msg, final MailServerConfigurationBean configBean) throws MailException
	{	
		SmtpSslServer smtpServer = SmtpSslServer.create(configBean.getHostName(), configBean.getPortNumber());
		smtpServer.authenticateWith(configBean.getGmailEmail(), configBean.getGmailPassword());
		
		SendMailSession sendMailSession =  smtpServer.createSession();
		sendMailSession.open();
		msg.setMailStatus(1);
		
		Email  email =  new Email();
		
		for(String to :msg.getTos())
		{
			email.addTo(new MailAddress(to));
		}
		for(String bcc :msg.getBccs())
		{
			email.addBcc(new MailAddress(bcc));
		}
		for(String cc :msg.getCcs())
		{
			email.addCc(new MailAddress(cc));
		}
		for(String reply :msg.getReplyTos())
		{
			email.addReplyTo(new MailAddress(reply));
		}
		email.setFrom(new MailAddress(msg.getFrom()));
		email.setSubject(msg.getSubject());
		
		EmailMessage textMessage = new EmailMessage(msg.getTextMessage(), MimeTypes.MIME_TEXT_PLAIN);
		email.addMessage(textMessage);

		EmailMessage htmlMessage = new EmailMessage(msg.getHtmlMessage(),
		    MimeTypes.MIME_TEXT_HTML);
		    email.addMessage(htmlMessage);
		

		for(EmailAttachment attachment : msg.getAttachments())
		{
			email.attach(attachment);
		}
		
		for(EmailAttachment attachment : msg.getEmbedAttachment())
		{
			if(msg.getHtmlMessage() != null && msg.getHtmlMessage() != "")
				attachment.setEmbeddedMessage(htmlMessage);
			
			email.embed(attachment);
		}
		
		String messageid = sendMailSession.sendMail(email);
		sendMailSession.close();
	
		return messageid;
	}

}
