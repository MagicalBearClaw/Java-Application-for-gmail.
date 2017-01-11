package JAG.ca.mcmahon.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import jodd.mail.EmailAttachment;


/**
 * @author Michael McMahon
 * @since 9/16/2015
 * @version 0.1
 * 
 *  Class that handles the data that encapsulates
 *  a e-mail message.
 * 
 */
public class MailBean 
{
		
	private ListProperty<String> tos;
	private ListProperty<String> bccs;
	private ListProperty<String> ccs;
	private ListProperty<String> replyTos;
	private ListProperty<EmailAttachment> attachments;
	private ListProperty<EmailAttachment> embedAttachment;
	
	private SimpleStringProperty subject; 
	private SimpleStringProperty from; 
	private SimpleStringProperty textMessage;
	private SimpleStringProperty htmlMessage;
	private int folder;
	private int mailId;
	
	private LocalDateTime sentDate;
	private LocalDateTime recievedDate;
	
	private int mailStatus;

	
	/**
	 * Default Constructor
	 * @author Michael McMahon
	 */
	public MailBean()
	{
		mailId = 0;
		folder = 1;
		tos =  new SimpleListProperty<String>(FXCollections.observableArrayList((new ArrayList<String>())));
		bccs =  new SimpleListProperty<String>(FXCollections.observableArrayList((new ArrayList<String>())));
		ccs =  new SimpleListProperty<String>(FXCollections.observableArrayList((new ArrayList<String>())));
		replyTos =  new SimpleListProperty<String>(FXCollections.observableArrayList((new ArrayList<String>())));
		attachments =  new SimpleListProperty<EmailAttachment>(FXCollections.observableArrayList((new ArrayList<EmailAttachment>())));
		embedAttachment =  new SimpleListProperty<EmailAttachment>(FXCollections.observableArrayList((new ArrayList<EmailAttachment>())));
		subject = new SimpleStringProperty();
		from = new SimpleStringProperty();
		textMessage = new SimpleStringProperty();
		htmlMessage = new SimpleStringProperty();

	}
	
	/**
	 * 
	 * Constructor that creates a basic e-mail bean.
	 * 
	 * @param from -  The person sending the e-mail
	 * @param subject - The subject of the e-mail.
	 * @param textMsg - The text version of the e-mail
	 * @param htmlMsg -  The html version of the e-mail
	 * @param folder -  The folder that the e-mail is associated with
	 * @param tos -  The addresses the e-mail is destined to.
	 */
	public MailBean(String from, String subject,String textMsg, String htmlMsg, int folder)
	{
		this();
		this.subject.set(subject);
		this.from.set(from);
		this.textMessage.set(textMsg);
		this.htmlMessage.set(htmlMsg);
		this.folder = folder; 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		MailBean other = (MailBean) obj;

		if (attachments == null) 
		{
			if (other.attachments != null)
				return false;
			if(attachments.size() != other.attachments.size())
				return false;
		} 
		if(this.folder != other.folder)
			return false;
		
		if(attachments.size() != 0 && other.attachments.size() != 0)
		{
			for(int  i = 0; i < attachments.size(); i++)
			{
				if (!Arrays.equals(embedAttachment.get(i).toByteArray(), other.attachments.get(i).toByteArray()))
	                return false;
			}
		}

		if (ccs == null) 
		{
			if (other.ccs != null)
				return false;
		} else if (!ccs.equals(other.ccs))
			return false;
		if (embedAttachment == null) 
		{
			if (other.embedAttachment != null)
				return false;
			if(embedAttachment.size() != other.embedAttachment.size())
				return false;
		} 
		if(embedAttachment.size() != 0 && other.embedAttachment.size() != 0)
		{
			for(int  i = 0; i < embedAttachment.size(); i++)
			{
				if (!Arrays.equals(embedAttachment.get(i).toByteArray(), other.embedAttachment.get(i).toByteArray()))
	                return false;
			}
		}

		
		if (from == null) 
		{
			if (other.from != null)
				return false;
		} 
		else if (!from.get().equals(other.from.get()))
			return false;
		
		if (htmlMessage == null) {
			if (other.htmlMessage != null)
				return false;
			
			htmlMessage.set(htmlMessage.get().trim());
			other.htmlMessage.set(other.htmlMessage.get().trim());
			
		} 
		else if (!htmlMessage.get().equals(other.htmlMessage.get()))
			return false;
				
		if (replyTos == null) 
		{
			if (other.replyTos != null)
				return false;
		} 
		else if (!replyTos.equals(other.replyTos))
			return false;

		if (subject == null) 
		{
			if (other.subject != null)
				return false;
			subject.set(subject.get().trim());
			other.subject.set(other.subject.get().trim());
		} 

		else if (!subject.get().equals(other.subject.get()))
			return false;
		
		if (textMessage == null) 
		{
			if (other.textMessage != null)
				return false;
			textMessage.set(textMessage.get().trim());
			other.textMessage.set(other.textMessage.get().trim());
		} 
		else if (!textMessage.get().equals(other.textMessage.get()))
			return false;
		if (tos == null) 
		{
			if (other.tos != null)
				return false;
		} 
		else if (!tos.equals(other.tos))
			return false;
		
		return true;
	}
	/**
	 * Returns a List property of EmailAttachments.
	 * @return ListProperty of EmailAttachment
	 */
	public ListProperty<EmailAttachment> getAttachmentsProperty() 
	{
		return attachments;
	}
	/**
	 * Returns a reference to attachment array list
	 * @return ArrayList of EmailAttachment
	 */
	public ArrayList<EmailAttachment> getAttachments() 
	{	
		return new ArrayList<>(attachments);
	}
	
	/**
	 * Returns a A list property of string for bccs
	 * @return ArrayList of String
	 */
	public ListProperty<String> getBccsProperty()
	{
		return bccs;
	}
	/**
	 * Returns a copy to the blind carbon copy email addresses array list
	 * @return ArrayList of String
	 */
	public ArrayList<String> getBccs()
	{
		return new ArrayList<>(bccs);
	}
	
	/**
	 * Returns a copy to the carbon copy email addresses array list
	 * @return ArrayList of String
	 */
	public ArrayList<String> getCcs() 
	{
		return new ArrayList<>(ccs);
	}
	/**
	 * Returns a copy to the carbon copy email addresses array list
	 * @return ArrayList of String
	 */
	public ListProperty<String> getCcsProperty() 
	{
		return ccs;
	}
	/**
	 * Returns a copy to the embeded attachment array list
	 * @return ArrayList of String
	 */
	public ArrayList<EmailAttachment> getEmbedAttachment() 
	{
		return new ArrayList<>(embedAttachment);
	}
	/**
	 * Returns a copy to the embeded attachment array list
	 * @return ArrayList of String
	 */
	public ListProperty<EmailAttachment> getEmbedAttachmentProperty() 
	{
		return embedAttachment;
	}
	/**
	 * Gets the folder of the e-mail
	 * @return int
	 */
	public int getFolder() 
	{
		return folder;
	}
	
	/**
	 * Returns the from of the e-mail
	 * @return String
	 */
	public String getFrom() 
	{
		return from.get();
	}
	/**
	 * Returns the from as A string property
	 * @return String
	 */
	public SimpleStringProperty getFromProperty() 
	{
		return from;
	}
	/**
	 * Returns the html message of the e-mail
	 * @return String
	 */
	public String getHtmlMessage() 
	{
		return htmlMessage.get();
	}
	/**
	 * Returns the html message of the e-mail as a property
	 * @return String
	 */
	public SimpleStringProperty getHtmlMessageProperty() 
	{
		return htmlMessage;
	}

	/**
	 * Gets the mail status of the e-mail
	 * @return int
	 */
	public int getMailStatus()
	{
		return mailStatus;
	}
	/**
	 * Gets the received a formated string Date of the e-mail as a property
	 * @return Date
	 */
	public SimpleStringProperty getRecievedDateFormatedStringProperty() 
	{
		return new SimpleStringProperty(recievedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy:hh:mm:ss")));
	}
	/**
	 * Gets the received Date of the e-mail
	 * @return Date
	 */
	public LocalDateTime getRecievedDate() 
	{
		return recievedDate;
	}
	/**
	 * Returns a reference to the reply to's email addresses array list
	 * @return ArrayList of String
	 */
	public ArrayList<String> getReplyTos() 
	{
		return new ArrayList<>(replyTos);
	}
	/**
	 * Returns a List property of strings for replytos
	 * @return ArrayList of String
	 */
	public ListProperty<String> getReplyTosProperty() 
	{
		return replyTos;
	}
	/**
	 * Gets the sent Date of the e-mail
	 * @return Date
	 */
	public LocalDateTime getSentDate() 
	{
		return sentDate;
	}
	/**
	 * Gets the sent formated string Date of the e-mail as a property
	 * @return Date
	 */
	public SimpleStringProperty getSentDateFormatedStringProperty() 
	{
		return new SimpleStringProperty(sentDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy:hh:mm:ss")));
	}
	/**
	 * Returns the subject of the e-mail
	 * @return String
	 */
	public String getSubject() 
	{
		return subject.get();
	}
	/**
	 * Returns the subject of the e-mail as a property
	 * @return String
	 */
	public SimpleStringProperty getSubjectProperty() 
	{
		return subject;
	}
	/**
	 * Returns the text message of the e-mail as aproperty
	 * @return String
	 */
	public SimpleStringProperty getTextMessageProperty() 
	{
		return textMessage;
	}
	/**
	 * Returns the text message of the e-mail
	 * @return String
	 */
	public String getTextMessage() 
	{
		return textMessage.get();
	}
	/**
	 * Returns a reference to the destination email address array list
	 * @return ArrayList of String
	 */
	public ArrayList<String> getTos() 
	{
		return new ArrayList<>(tos);
	}
	/**
	 * Returns a reference to the destination email address array list
	 * @return ArrayList of String
	 */
	public ListProperty<String> getTosProperty() 
	{
		return tos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attachments == null) ? 0 : attachments.hashCode());
		result = prime * result + ((bccs == null) ? 0 : bccs.hashCode());
		result = prime * result + ((ccs == null) ? 0 : ccs.hashCode());
		result = prime * result + ((embedAttachment == null) ? 0 : embedAttachment.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((htmlMessage == null) ? 0 : htmlMessage.hashCode());
		result = prime * result + ((recievedDate == null) ? 0 : recievedDate.hashCode());
		result = prime * result + ((replyTos == null) ? 0 : replyTos.hashCode());
		result = prime * result + ((sentDate == null) ? 0 : sentDate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((textMessage == null) ? 0 : textMessage.hashCode());
		result = prime * result + ((tos == null) ? 0 : tos.hashCode());
		return result;
	}

	/**
	 * Sets the folder of the e-mail
	 * @param folderid - the folder
	 */
	public void setFolder(int folderid) 
	{
		this.folder = folderid;
	}
	/**
	 * Sets the from of the e-mail
	 * @param from - the from
	 */
	public void setFrom(String from) 
	{
		this.from.set(from);
	}
	/**
	 * Sets the html message of the e-mail
	 * @param htmlMessage - the html message
	 */
	public void setHtmlMessage(String htmlMessage) 
	{
		this.htmlMessage.set(htmlMessage);
	}
	/**
	 * Sets the mail status of the e-mail
	 * @param mailStatus - the mail status
	 */
	public void setMailStatus(int mailStatus) 
	{
		this.mailStatus = mailStatus;
	}
	/**
	 * Sets the received Date of the e-mail
	 * @param recievedDate - the received date
	 */
	public void setRecievedDate(LocalDateTime recievedDate) 
	{
		this.recievedDate = recievedDate;
	}
	/**
	 * Sets the sent Date of the e-mail
	 * @param sentDate - the sentDate
	 */
	public void setSentDate(LocalDateTime sentDate) 
	{
		this.sentDate = sentDate;
	}
	/**
	 * Sets the subject of the e-mail
	 * @param subject - the subject
	 */
	public void setSubject(String subject) 
	{
		this.subject.set(subject);
	}

	/**
	 * Sets the text message of the e-mail
	 * @param textMessage - the text message
	 */
	public void setTextMessage(String textMessage) 
	{
		this.textMessage.set(textMessage);
	}
	/**
	 * @return the mailId
	 */
	public int getMailId() 
	{
		return mailId;
	}

	/**
	 * @param mailId the mailId to set
	 */
	public void setMailId(int mailId) {
		this.mailId = mailId;
	}
	
	public MailBean getHardCopy()
	{
		MailBean bean =  new MailBean();
		bean.setFrom(this.from.get());
		bean.setFolder(this.folder);
		bean.setHtmlMessage(this.htmlMessage.get());
		bean.setTextMessage(this.textMessage.get());
		bean.setMailId(this.mailId);
		bean.setMailStatus(this.mailStatus);
		bean.setRecievedDate(this.recievedDate);
		bean.setSentDate(this.sentDate);
		bean.setSubject(this.subject.get());
		bean.getAttachments().addAll(this.attachments);
		bean.getEmbedAttachment().addAll(this.embedAttachment);
		bean.getBccsProperty().addAll(this.bccs);
		bean.getCcs().addAll(this.ccs);
		bean.getTos().addAll(this.tos);
		bean.getReplyTos().addAll(this.replyTos);
		return bean;
	}
	
}
