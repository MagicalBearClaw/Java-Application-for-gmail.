package JAG.ca.mcmahon.beans;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MySqlConigurationBean 
{
	private SimpleStringProperty mySqlUserName;
	private SimpleStringProperty mySqlPassword;
	private SimpleStringProperty hostName;
	private SimpleIntegerProperty port;
	
	public MySqlConigurationBean()
	{
		super();
		mySqlUserName =  new SimpleStringProperty();
		mySqlPassword =  new SimpleStringProperty();
		hostName =  new SimpleStringProperty();
		port =  new SimpleIntegerProperty();
	}
	public MySqlConigurationBean( final String mySqlUserName, final String mySqlPassword, final String hostName, final int portNum) 
	{
		this();
		this.mySqlUserName.set(mySqlUserName);
		this.mySqlPassword.set(mySqlPassword);
		this.hostName.set(hostName);
		this.port.set(Integer.valueOf(portNum));
	}

	public String getMySqlPassword() 
	{
		return mySqlPassword.get();
	}
	
	public SimpleStringProperty getMySqlPasswordProperty() 
	{
		return mySqlPassword;
	}
	
	public SimpleStringProperty getMySqlUserNameProperty() 
	{
		return mySqlUserName;
	}
	public String getMySqlUserName() 
	{
		return mySqlUserName.get();
	}
	public void setMySqlPassword(final String mySqlPassword) 
	{
		this.mySqlPassword.set(mySqlPassword);
	}
	public void setMySqlUserName(final String mySqlUserName) 
	{
		this.mySqlUserName.set(mySqlUserName);
	}
	
	/**
	 * @return the hostName as a poperty
	 */
	public SimpleStringProperty getHostNameProperty() 
	{
		return hostName;
	}
	/**
	 * @return the hostName
	 */
	public String getHostName() 
	{
		return hostName.get();
	}
	/**
	 * @return the port as aproperty
	 */
	public SimpleIntegerProperty getPortProperty() 
	{
		return port;
	}
	/**
	 * @return the port
	 */
	public int getPort() 
	{
		return port.get();
	}
	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) 
	{
		this.hostName.set(hostName);
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) 
	{
		this.port.set(Integer.valueOf(port));
	}
}
