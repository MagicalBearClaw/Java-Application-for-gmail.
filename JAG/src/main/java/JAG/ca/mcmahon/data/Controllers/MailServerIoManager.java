package JAG.ca.mcmahon.data.Controllers;


import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Paths.get;
import JAG.ca.mcmahon.beans.MailServerConfigurationBean;
import JAG.ca.mcmahon.beans.MySqlConigurationBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @author Michael McMahon
 * @since 9/16/2015
 *@version 1.0
 */
public class MailServerIoManager 
{
	/**
	 * 
	 * Allows the retrieval of server information from a text file.
	 * 
	 * @param path the file path
	 * @param fileName the file name without extension
	 * @return MailServerConfigurationBean
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public final MailServerConfigurationBean LoadMailConfigFromTextProperties(final String path, final String fileName) throws IOException, NumberFormatException
	{
		Properties prop =  new Properties();
		Path textFile = get(path, fileName + ".properties");
		
		MailServerConfigurationBean config =  new MailServerConfigurationBean();
		
		try(InputStream stream = newInputStream(textFile))
		{
			prop.load(stream);
		}
		config.setHostName(prop.getProperty("HostName"));
		config.setName(prop.getProperty("Name"));
		config.setPortNumber(Integer.parseInt(prop.getProperty("PortNumber")));
		config.setGmailEmail(prop.getProperty("Email"));
		config.setGmailPassword(prop.getProperty("Password"));
		
		return config;
	}
	/**
	 * 
	 * Allows the retrieval of server information from a text file.
	 * 
	 * @param path the file path
	 * @param fileName the file name without extension
	 * @return MailServerConfigurationBean
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public final MySqlConigurationBean LoadMysqlConfigFromTextProperties(final String path, final String fileName) throws IOException, NumberFormatException
	{
		Properties prop =  new Properties();
		Path textFile = get(path, fileName + ".properties");
		
		MySqlConigurationBean config =  new MySqlConigurationBean();
		
		if(Files.exists(textFile))
		{
			try(InputStream stream = newInputStream(textFile))
			{
				prop.load(stream);
			}
			config.setHostName(prop.getProperty("HostName"));
			config.setPort(Integer.parseInt(prop.getProperty("PortNumber")));
			config.setMySqlUserName(prop.getProperty("userName"));
			config.setMySqlPassword(prop.getProperty("Password"));
		}
		
		return config;
	}
	
	/**
	 * 
	 * Allows the retrieval of server information from a xml file.
	 * 
	 * @param path the file path
	 * @param fileName the file name without extension
	 * @return MailServerConfigurationBean
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public final MailServerConfigurationBean LoadFromXmlProperties(final String path, final String fileName) throws IOException, NumberFormatException
	{
		Properties prop =  new Properties();
		Path textFile = get(path, fileName + ".properties");
		
		MailServerConfigurationBean config =  new MailServerConfigurationBean();
		
		if(Files.exists(textFile))
		{
			try(InputStream stream = newInputStream(textFile))
			{
				prop.loadFromXML(stream);
			}
			config.setHostName(prop.getProperty("HostName"));
			config.setName(prop.getProperty("Name"));
			config.setPortNumber(Integer.parseInt(prop.getProperty("PortNumber")));
			config.setGmailEmail(prop.getProperty("Email"));
			config.setGmailPassword(prop.getProperty("Password"));
		}
		
		return config;
	}
	/**
	 * 
	 * Allows the saving of server information to a text file
	 * 
	 * @param path the file path
	 * @param fileName the file name
	 * @param config the configuration bean
	 * @param comments the comments that you want to put in
	 * @throws IOException
	 */
	public void saveTextProperties(final String path, final String fileName, final MailServerConfigurationBean config, final String comments) throws IOException
	{
		Properties prop =  new Properties();
		Path textFile = get(path, fileName + ".properties");
		
		prop.setProperty("HostName", config.getHostName());
		prop.setProperty("Name", config.getName());
		prop.setProperty("PortNumber", Integer.toString(config.getPortNumber()));
		prop.setProperty("Email", config.getGmailEmail());
		prop.setProperty("Password", config.getGmailPassword());
		
		try(OutputStream stream = newOutputStream(textFile))
		{
			prop.store(stream, comments);
		}
		
	}
	/**
	 * 
	 * Allows the saving of server information to a text file
	 * 
	 * @param path the file path
	 * @param fileName the file name
	 * @param config the configuration bean
	 * @param comments the comments that you want to put in
	 * @throws IOException
	 */
	public void saveTextProperties(final String path, final String fileName, final MySqlConigurationBean config, final String comments) throws IOException
	{
		Properties prop =  new Properties();
		Path textFile = get(path, fileName + ".properties");
		
		prop.setProperty("HostName", config.getHostName());
		prop.setProperty("PortNumber", Integer.toString(config.getPort()));
		prop.setProperty("userName", config.getMySqlUserName());
		prop.setProperty("Password", config.getMySqlPassword());
		
		try(OutputStream stream = newOutputStream(textFile))
		{
			prop.store(stream, comments);
		}
		
	}
	/**
	 * 
	 * Allows the saving of server information to a xml file
	 * 
	 * @param path the file path
	 * @param fileName the file name
	 * @param config the configuration bean
	 * @param comments the comments that you want to put in
	 * @throws IOException
	 */
	public void savexmlroperties(final String path, final String fileName, final MailServerConfigurationBean config, final String comments) throws IOException
	{
		Properties prop =  new Properties();
		Path textFile = get(path, fileName + ".properties");
		prop.setProperty("HostName", config.getHostName());
		prop.setProperty("Name", config.getName());
		prop.setProperty("PortNumber", Integer.toString(config.getPortNumber()));
		prop.setProperty("Email", config.getGmailEmail());
		prop.setProperty("Password", config.getGmailPassword());
		
		try(OutputStream stream = newOutputStream(textFile))
		{
			prop.storeToXML(stream, comments);
		}
		
	}
	
	
}
