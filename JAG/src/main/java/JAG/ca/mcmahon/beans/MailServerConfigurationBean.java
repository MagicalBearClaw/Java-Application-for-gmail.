package JAG.ca.mcmahon.beans;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author Michael McMahon
 * The server configuration data class. 
 * used for emaple to configure a imap or smtp server
 * connection.
 * @since 9/16/15
 * @version 1.0
 */
public class MailServerConfigurationBean 
{

	private SimpleIntegerProperty portNumber;
	private SimpleStringProperty hostName;
	private SimpleStringProperty gmailEmail;
	private SimpleStringProperty gmailPassword;
	private SimpleStringProperty name;

	
	/**
	 * Default constructor
	 */
	public MailServerConfigurationBean()
	{
		super();
		portNumber =  new SimpleIntegerProperty();
		hostName =  new SimpleStringProperty();
		gmailEmail =  new SimpleStringProperty();
		gmailPassword =  new SimpleStringProperty();
		name =  new SimpleStringProperty();
	}

	/**
	 * Constructor to create a new server configuration
	 * @param portNumber 
	 * @param hostName
	 * @param gmailEmail
	 * @param gmailPassword
	 * @param name
	 */
	public MailServerConfigurationBean(final int portNumber, final String hostName, final String gmailEmail, final String gmailPassword,
			String name) 
	{
		this();
		this.portNumber.set(portNumber);
		this.hostName.set(hostName);
		this.gmailEmail.set(gmailEmail);
		this.gmailPassword.set(gmailPassword);
		this.name.set(name);
	}

	/**
	 * Gets the gmail account
	 * @return String
	 */
	public String getGmailEmail() 
	{
		return gmailEmail.get();
	}
	/**
	 * Gets the gmail account as a property
	 * @return String
	 */
	public SimpleStringProperty getGmailEmailProperty() 
	{
		return gmailEmail;
	}

	/**
	 * Gets the gmail password
	 * @return String
	 */
	public String getGmailPassword() 
	{
		return gmailPassword.get();
	}

	/**
	 * Gets the gmail password as a property
	 * @return String
	 */
	public SimpleStringProperty getGmailPasswordProperty() 
	{
		return gmailPassword;
	}

	/**
	 * Gets the host name
	 * @return String
	 */
	public String getHostName() 
	{
		return hostName.get();
	}

	/**
	 * Gets the host name as a property
	 * @return String
	 */
	public SimpleStringProperty getHostNameProperty() 
	{
		return hostName;
	}
	
	/**
	 * Gets the name of the person
	 * @return String
	 */
	public String getName()
{
		return name.get();
	}

	/**
	 * Gets the name of the person as a property
	 * @return String
	 */
	public SimpleStringProperty getNameProperty()
{
		return name;
	}

	/**
	 * Gets the server port number
	 * @return int
	 */
	public int getPortNumber() 
	{
		return portNumber.get();
	}

	/**
	 * Gets the server port number as a property
	 * @return int
	 */
	public SimpleIntegerProperty getPortNumberProperty() 
	{
		return portNumber;
	}
	
	/**
	 * Sets the Gmail e-mail
	 * @param gmailEmail
	 */
	public void setGmailEmail(final String gmailEmail) 
	{
		this.gmailEmail.set(gmailEmail);
	}


	/**
	 * Sets the gmail password
	 * @param gmailPassword
	 */
	public void setGmailPassword(final String gmailPassword) 
	{
		this.gmailPassword.set(gmailPassword);
	}


	/**
	 * Sets the host name
	 * @param hostName
	 */
	public void setHostName(final String hostName) 
	{
		this.hostName.set(hostName);
	}


	/**
	 * Sets the name of the person
	 * @param name
	 */
	public void setName(final String name) 
	{
		this.name.set(name);
	}


	/**
	 * Sets the port number
	 * @param portNumber
	 */
	public void setPortNumber(final int portNumber) 
	{
		this.portNumber.set(portNumber);
	}
	

	
}
	
