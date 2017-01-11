package JAG.ca.mcmahon.data.Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JAG.ca.mcmahon.data.Controllers.Interfaces.MailDataAccessProvider;
import JAG.ca.mcmahon.beans.MailBean;
import JAG.ca.mcmahon.beans.MySqlConigurationBean;
import jodd.mail.EmailAttachment;


/*
 * This class is used to take care of the persistence of the 
 * mail beans. It stores them into a mysql database.
 * 
 * @author Michael McMahon
 * @since 9/16/2015
 * @version 0.1
 *
 */

public class EmailDAO implements MailDataAccessProvider
{
	private String url;
	private final String dbName = "gmaildatabase";
	private MySqlConigurationBean config;
	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/**
	 * Creates a new MySqlDatabaseDAO 
	 * @param config the configuration file for the connection.
	 */
	public EmailDAO(MySqlConigurationBean config) 
	{
		this.config = config;
		url = "jdbc:mysql://" + config.getHostName() +":" + config.getPort() + "/";
	}
	/**
	 * 
	 * Creates a new folder in the database.
	 * @param String - the name of the folder.
	 * @return int - the number of rows created.
	 * @throws SQLException
	 */
	@Override
	public int createFolder(final String folder) throws SQLException
	{
		if(folder == "" || folder == null)
			return 0;
		String folderInsert = "INSERT INTO folders(`folderName`) VALUES (?);";
		int rowsAffected = 0;
		
		if(findFolder(folder))
			throw new SQLException("There Cannot be duplicate folders");
		
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(folderInsert, PreparedStatement.RETURN_GENERATED_KEYS);
			stmt.setString(1, folder);
			rowsAffected += stmt.executeUpdate();
		}
			return rowsAffected;
	}
	/**
	 * Creates a entry in the database using the passed in
	 * Mailbean. Creates all entries in the needed tables.
	 * 
	 * @param Mailbean -  The mailbean to be used.
	 * @return int - returns the number of rows that have been created.
	 * @throws SQLException
	 */
	@Override
	public int createMail(final MailBean mail)throws SQLException 
	{
		if(mail == null)
			return 0;
		String emailInsert = "INSERT INTO EMAILS (`from`, `subject`, `FolderId`, `DateRecieved`, `DateSent`) VALUES (?,?,?,?,?)";
		String addressInsert = "INSERT INTO ADDRESSES (`address`, `mailType`, `MailId`) VALUES (?,?,?);";
		String AttachmentInsert = "INSERT INTO ATTACHMENTS (`attachment`, `isEmbeded`, `contentId`,`fileName`,`MailId`) VALUES (?,?,?,?,?);";
		String messageInsert = "INSERT INTO MESSAGES (`Content`,`MimeType`, `MailId`) VALUES (?,?,?);";
		int mailId = -1;
		
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(emailInsert, PreparedStatement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, mail.getFrom());
			stmt.setString(2, mail.getSubject());
			stmt.setInt(3, mail.getFolder());
			stmt.setTimestamp(4, Timestamp.valueOf(mail.getRecievedDate()));
			stmt.setTimestamp(5, Timestamp.valueOf(mail.getSentDate()));
			stmt.executeUpdate();
			
			ResultSet set = stmt.getGeneratedKeys();

			if(set.next())
			{
				mailId =  set.getInt(1);
			}
			
			if(mailId == -1)
			{
				throw new SQLException("There was an error inserting a email into the database because the mailId was -1");
			}
			
			mail.setMailId(mailId);
			stmt =  connection.prepareStatement(addressInsert, PreparedStatement.RETURN_GENERATED_KEYS);
			
			for(String to : mail.getTos())
			{
				stmt =  connection.prepareStatement(addressInsert);
				stmt.setString(1, to);
				stmt.setString(2, "T");
				stmt.setInt(3, mailId);
				stmt.executeUpdate();
			}
			for(String cc : mail.getCcs())
			{
				stmt =  connection.prepareStatement(addressInsert);
				stmt.setString(1, cc);
				stmt.setString(2, "C");
				stmt.setInt(3, mailId);
				stmt.executeUpdate();
			}
			for(String bcc : mail.getBccs())
			{
				stmt =  connection.prepareStatement(addressInsert);
				stmt.setString(1, bcc);
				stmt.setString(2, "B");
				stmt.setInt(3, mailId);
				stmt.executeUpdate();
			}
			for(EmailAttachment attachment : mail.getAttachments())
			{
				stmt =  connection.prepareStatement(AttachmentInsert);
				stmt.setBytes(1, attachment.toByteArray());
				stmt.setInt(2, 0);
				stmt.setString(3, attachment.getContentId());
				stmt.setString(4, attachment.getName());
				stmt.setInt(5, mailId);

				stmt.executeUpdate();
			}
			for(EmailAttachment embeded : mail.getEmbedAttachment())
			{
				stmt =  connection.prepareStatement(AttachmentInsert);
				stmt.setBytes(1, embeded.toByteArray());
				stmt.setInt(2, 1);
				stmt.setString(3, embeded.getContentId());
				stmt.setString(4, embeded.getName());
				stmt.setInt(5, mailId);

				stmt.executeUpdate();
			}
			
			stmt =  connection.prepareStatement(messageInsert);
			stmt.setString(1, mail.getTextMessage());
			stmt.setString(2, "text");
			stmt.setInt(3, mailId);
			stmt.executeUpdate();
			
			stmt =  connection.prepareStatement(messageInsert);
			stmt.setString(1, mail.getHtmlMessage());
			stmt.setString(2, "html");
			stmt.setInt(3, mailId);
			stmt.executeUpdate();
			
		}
		return mailId;
	}

	/**
	 * 
	 * Deletes a folder from the folder table.
	 * 
	 * @param String -  the folder name.
	 * @return int -  the number of rows deleted.
	 * @throws SQLException
	 */
	@Override
	public int deleteFolder(final String folder) throws SQLException
	{
		String deleteFolder = "Delete From folders where folderName = ?;";
		int deletes = 0;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(deleteFolder);
			stmt.setString(1, folder);
			deletes += stmt.executeUpdate();
		}
		
		return deletes;
	}
	/**
	 * 
	 * Deletes a email and all of its associated rows from other tables.
	 * 
	 * @param int - The id of the email to delete.
	 * @return int -  The number of rows deleted.
	 * @throws SQLException
	 */
	@Override
	public int deleteMail(final int id) throws SQLException
	{
		if(id < 1)
			return 0;
		String emailQueryId = "DELETE FROM emails WHERE id = ?";
		int deletes = 0;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(emailQueryId);
			idStmt.setInt(1, id);
			deletes += idStmt.executeUpdate();
		}
		return deletes;
	}
	/**
	 * Tries to find a folder in the database.
	 * 
	 * @param String - the name of the folder.
	 * @return bool -  true if it did find the folder, false otherwise.
	 * @throws SQLException
	 */
	private boolean findFolder(final String folderName) throws SQLException
	{
		String folderQuery = "SELECT folderName FROM Folders where folderName = ?;";
		boolean result = false;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(folderQuery);
			stmt.setString(1, folderName);
			stmt.execute();
			
			ResultSet results = stmt.getResultSet();
			
			if(results.next())
			{
				result = true;
			}
			result = false;
		}
		return result;
	}
	public int findFolderIdByName(final String folderName) throws SQLException
	{
		String folderQuery = "SELECT id FROM Folders where folderName = ?;";
		int result = 0;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(folderQuery);
			stmt.setString(1, folderName);
			stmt.execute();
			
			ResultSet results = stmt.getResultSet();
			
			if(results.next())
			{
				result = results.getInt(0);
			}
		}
		return result;
	}


	/**
	 * 
	 * Similar to getMailById but only retrieves the email portion.
	 * 
	 * Gets the email portion that has the specified mail id.
	 * @param int -  the mail id
	 * @return MailBean - the MailBean found from the id, null if not found.
	 * @throws SQLException
	 */
	@Override
	public MailBean getEmailById(final int id) throws SQLException 
	{
		String emailQuery = "SELECT id, from, subject, folderId, dateRecieved, dateSent FROM emails WHERE id = ?;";
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(emailQuery);
			stmt.setInt(1, id);
			stmt.execute();
			
			ResultSet set =  stmt.getResultSet();
			if(set.next())
			{
				MailBean bean  =  new MailBean();
				bean.setMailId(set.getInt(1));
				bean.setFrom(set.getString(2));
				bean.setSubject(set.getString(3));
				bean.setFolder(set.getInt(4));
				bean.setRecievedDate(set.getTimestamp(5).toLocalDateTime());
				bean.setSentDate(set.getTimestamp(6).toLocalDateTime());
				
				return bean;
			}
			else
			{
				return null;
			}

		}
	}
	/**
	 * Gets all the mail beans that are after the received date.
	 * @param LocalDateTime -  the date 
	 * @return ArrayList<MailBean> - the beans found, null if none.
	 * @throws SQLException
	 */
	public ArrayList<MailBean> getEmailsThatAreAfterRecievedDate(final LocalDateTime recieved)throws SQLException
	{
		if(recieved == null)
			return null;
		
		String emailQuery = "SELECT id FROM emails WHERE dateSent > ?;";
		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(emailQuery);
			idStmt.setTimestamp(1, Timestamp.valueOf(recieved));
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}

		}
		
		return beans;
	}
	/**
	 * Gets all the mail beans that are after the sent date.
	 * @param LocalDateTime -  the date 
	 * @return ArrayList<MailBean> - the beans found, null if none.
	 * @throws SQLException
	 */
	public ArrayList<MailBean> getEmailsThatAreAfterSentDate(final LocalDateTime sent)throws SQLException
	{
		if(sent == null)
			return null;
		
		String emailQuery = "SELECT id FROM emails WHERE dateRecieved > ?;";
		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(emailQuery);
			idStmt.setTimestamp(1, Timestamp.valueOf(sent));
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}

		}
		
		return beans;
	}
	/**
	 * Gets all the emails that have the specified BCC: address.
	 * @param String -  the BCC address.
	 * @return ArrayList<MailBean> - all the mailbeans that have that BCC.
	 * null if not found
	 * @throws SQLException
	 */
	@Override
	public ArrayList<MailBean> getEmailsThatMatchBcc(final String bcc) throws SQLException 
	{
		String addressQueryId = "SELECT mailId FROM addresses WHERE address = ? AND mailType = ?;";

		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(addressQueryId);
			idStmt.setString(1, bcc);
			idStmt.setString(2, "B");
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}
		}
		
		return beans;
	}
	
	/**
	 * Gets all the emails that have the specified from address.
	 * @param String -  the from address
	 * @return ArrayList<MailBean> - all the mailbeans that have that from address.
	 * null if not found
	 * @throws SQLException
	 */
	@Override
	public ArrayList<MailBean> getEmailsThatMatchFrom(final String from) throws SQLException 
	{
		String emailQuery = "SELECT id FROM emails WHERE `from` = ?;";

		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(emailQuery);
			idStmt.setString(1, from);;
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}
		}
		
		return beans;
	}
	/**
	 * Gets all the emails that have the specified received date.
	 * @param LocalDateTime -  the date to query against
	 * @return ArrayList<MailBean> - all the mailbeans that have that received date.
	 * null if not found
	 * @throws SQLException
	 */
	@Override
	public ArrayList<MailBean> getEmailsThatMatchRecievedDate(final LocalDateTime recieved) throws SQLException
	{
		String emailQuery = "SELECT Id FROM emails WHERE dateRecieved = ?;";

		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(emailQuery);
			idStmt.setTimestamp(1, Timestamp.valueOf(recieved));
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}
		}
		
		return beans;
	}

	/**
	 * Gets all the emails that have the specified sent date.
	 * @param LocalDateTime -  the date to query against
	 * @return ArrayList<MailBean> - all the mailbeans that have that sent date.
	 * null if not found
	 * @throws SQLException
	 */
	@Override
	public ArrayList<MailBean> getEmailsThatMatchSentDate(final LocalDateTime sent) throws SQLException 
	{
		String emailQuery = "SELECT id FROM emails WHERE dateSent = ?;";
		
		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(emailQuery);
			idStmt.setTimestamp(1, Timestamp.valueOf(sent));
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}
		}
		
		return beans;
	}
	/**
	 * Gets all the emails that have the specified subject: address.
	 * @param String -  the subject.
	 * @return ArrayList<MailBean> - all the mailbeans that have that subject.
	 * null if not found
	 * @throws SQLException
	 */
	@Override
	public ArrayList<MailBean> getEmailsThatMatchSubject(final String subject) throws SQLException 
	{
		String emailQuery = "SELECT Id FROM emails WHERE subject = ?;";

		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(emailQuery);
			idStmt.setString(1, subject);
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}
		}
		
		return beans;
	}
	/**
	 * Gets all the emails that have the specified TO: address.
	 * @param String -  the to address.
	 * @return ArrayList<MailBean> - all the mailbeans that have that to.
	 * null if not found
	 * @throws SQLException
	 */
	@Override
	public ArrayList<MailBean> getEmailsThatMatchTo(final String to) throws SQLException 
	{
		String addressQueryId = "SELECT mailId FROM addresses WHERE address = ? AND mailType = ?;";

		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(addressQueryId);
			idStmt.setString(1, to);
			idStmt.setString(2, "T");
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}
		}
		
		return beans;
	}
	/**
	 * Gets all the emails that have the specified CC: address.
	 * @param String -  the CC address.
	 * @return ArrayList<MailBean> - all the mailbeans that have that CC.
	 * null if not found
	 * @throws SQLException
	 */
	@Override
	public ArrayList<MailBean> getEmailsThatMathcCc(final String cc) throws SQLException 
	{
		String addressQueryId = "SELECT mailId FROM addresses WHERE address = ? AND mailType = ?;";

		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(addressQueryId);
			idStmt.setString(1, cc);
			idStmt.setString(2, "C");
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}
		}
		
		return beans;
	}

	/**
	 * Gets the email that has the specified folder name.
	 * @param String -  the fodler name.
	 * @return ArrayList<MailBean> - the MailBeans found from the folder name, null if not found.
	 * @throws SQLException
	 */
	@Override
	public ArrayList<MailBean> getEmailsWithFolderName(final String name) throws SQLException 
	{
		String emailQuery = "SELECT Id FROM emails WHERE folderId = ?;";
		String folderIdQuery = "SELECT id FROM folders WHERE foldername = ?";
		ArrayList<MailBean> beans = null;
		int folderId = -1;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement folderIdStmt =  connection.prepareStatement(folderIdQuery);
			folderIdStmt.setString(1, name);
			folderIdStmt.execute();
			// Get all the email ids
			ResultSet folderIdSet =  folderIdStmt.getResultSet();
			
			if(folderIdSet.next())
			{
				folderId = folderIdSet.getInt(1);
				PreparedStatement emailIdStmt =  connection.prepareStatement(emailQuery);
				emailIdStmt.setInt(1, folderId);
				emailIdStmt.execute();
				ResultSet emailSet =  emailIdStmt.getResultSet();
				beans = getMailBeansFromResultInformation(emailSet, connection);
			}
		}
		return beans;
	}
	/**
	 * 
	 * Retrieves all the folder name from the folder table
	 * 
	 * @return ArrayList<String> - the folder names.
	 * @throws SQLException
	 */
	@Override
	public ArrayList<String> getFolders() throws SQLException
	{
		
		String folderQuery = "SELECT folderName FROM Folders Order by id;";
		
		ArrayList<String> folders = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(folderQuery);
			stmt.execute();
			
			ResultSet results = stmt.getResultSet();

			
			while(results.next())
			{
				folders.add(results.getString(1));
			}
		}
		return folders;
	}
	
	private ArrayList<MailBean> getMailBeansFromResultInformation(final ResultSet set, final Connection connection) throws SQLException
	{
		int mailId = -1;
		String addressQuery = "SELECT mailId, address, mailType FROM addresses WHERE mailId = ? ORDER BY mailType";
		String emailQuery = "SELECT id,`from`, subject, folderID, dateRecieved, dateSent FROM emails WHERE id = ? ";
		String attachmentQuery = "SELECT attachment, isEmbeded, contentId, fileName FROM attachments WHERE mailId = ?";
		String messageQuery = "SELECT content, MimeType FROM messages WHERE mailId = ?";
		ArrayList<MailBean> beans =  new ArrayList<>();
		
		// while there is still mail ids to process.
		while(set.next())
		{
			
			mailId = set.getInt(1);
			
			MailBean bean =  new MailBean();
			
			PreparedStatement beanStmt =  connection.prepareStatement(addressQuery);
			beanStmt.setInt(1, mailId);
			beanStmt.execute();
			ResultSet beanSet =  beanStmt.getResultSet();
			
			// Get all the addresses.
			while(beanSet.next())
			{
				switch(beanSet.getString(3))
				{
					case"B":
						bean.getBccsProperty().add(beanSet.getString(2));
						break;
					case"C":
						bean.getCcsProperty().add(beanSet.getString(2));
						break;
					case"T":
						bean.getTosProperty().add(beanSet.getString(2));
						break;
				}
			}
			
			beanStmt =  connection.prepareStatement(emailQuery);
			beanStmt.setInt(1, mailId);
			beanStmt.execute();	
			beanSet =  beanStmt.getResultSet();
			// get the emal portion
			if(beanSet.next())
			{	
				bean.setMailId(beanSet.getInt(1));
				bean.setFrom(beanSet.getString(2));
				bean.setSubject(beanSet.getString(3));
				bean.setFolder(beanSet.getInt(4));

				bean.setRecievedDate(beanSet.getTimestamp(5).toLocalDateTime());
				bean.setSentDate(beanSet.getTimestamp(6).toLocalDateTime());
			}
			else
				throw new SQLException("Something went really wrong incomplete email found.");
			// get the message portion
			beanStmt =  connection.prepareStatement(messageQuery);
			beanStmt.setInt(1, mailId);
			beanStmt.execute();	
			beanSet =  beanStmt.getResultSet();
			
			while(beanSet.next())
			{
				switch(beanSet.getString(2))
				{
					case "text":
						bean.setTextMessage(beanSet.getString(1));
						break;
					case "html":
						bean.setHtmlMessage(beanSet.getString(1));
						break;
				}
			}
			// get the attachment portion.
			beanStmt =  connection.prepareStatement(attachmentQuery);
			beanStmt.setInt(1, mailId);
			beanStmt.execute();	
			beanSet =  beanStmt.getResultSet();
			
			while(beanSet.next())
			{
				if(beanSet.getInt(2) >=  1)
				{
					EmailAttachment embeded =  EmailAttachment.attachment().bytes(beanSet.getBytes(1)).setName(beanSet.getString(4)).setInline(beanSet.getString(3)).create();
					bean.getEmbedAttachmentProperty().add(embeded);
				}
				else
				{
					EmailAttachment attachment =  EmailAttachment.attachment().bytes(beanSet.getBytes(1)).setName(beanSet.getString(4)).create();
					bean.getAttachmentsProperty().add(attachment);
				}
			}
			
			beans.add(bean);
		}
		
		return beans;
	}

	/**
	 * Gets the email that has the specified mail id.
	 * @param int -  the mail id
	 * @return MailBean - the MailBean found from the id, null if not found.
	 * @throws SQLException
	 */
	@Override
	public MailBean getMailById(final int id) throws SQLException
	{
		if(id < 1)
			return null;
		String emailQuery = "SELECT id FROM emails WHERE id = ?;";

		ArrayList<MailBean> beans = null;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement idStmt =  connection.prepareStatement(emailQuery);
			idStmt.setInt(1, id);;
			idStmt.execute();
			// Get all the email ids
			ResultSet idSet =  idStmt.getResultSet();
			if(idSet.next())
			{
				idSet.beforeFirst();
				beans = getMailBeansFromResultInformation(idSet, connection);
			}

		}
		
		return beans.get(0);
	}
	/**
	 * Takes care of creating the database as well as creating the tables
	 * also grants access to the database to the Config user that was passed
	 * in the constructor.
	 */
	@Override
	public boolean InitializeDataBase() throws SQLException 
	{
		String grant1 = "GRANT ALL PRIVILEGES ON gmaildatabase.* TO ?@\"%\" IDENTIFIED BY ?;";
		String grant2 = "GRANT ALL PRIVILEGES ON gmaildatabase.* TO ?@\"localhost\" IDENTIFIED BY ?;";
		
		
		final String seedDataScript = loadAsString("gmaildatabase.sql");
		// TODO: CHANGE YOUR USER CREDINTIALS HERE (initializeDatanase) method.
		try (Connection connection = DriverManager.getConnection(url, config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt = connection.prepareStatement(grant1);
			stmt.setString(1, config.getMySqlUserName());
			stmt.setString(2,config.getMySqlPassword());
			stmt.execute();
			stmt = connection.prepareStatement(grant2);
			stmt.setString(1, config.getMySqlUserName());
			stmt.setString(2,config.getMySqlPassword());
			stmt.execute();
			
			for (String statement : splitStatements(new StringReader(seedDataScript), ";")) 
			{
				connection.prepareStatement(statement).execute();
			}
			return true;
		}
	}
	private boolean isComment(final String line) {
		return line.startsWith("--") || line.startsWith("//") || line.startsWith("/*");
	}
	private String loadAsString(final String path) {
		try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			Scanner scanner = new Scanner(inputStream)) {
			return scanner.useDelimiter("\\A").next();
		} catch (IOException e) {
			throw new RuntimeException("Unable to close input stream.", e);
		}
	}
	private List<String> splitStatements(Reader reader, String statementDelimiter) {
		final BufferedReader bufferedReader = new BufferedReader(reader);
		final StringBuilder sqlStatement = new StringBuilder();
		final List<String> statements = new LinkedList<String>();
		try {
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || isComment(line)) {
					continue;
				}
				sqlStatement.append(line);
				if (line.endsWith(statementDelimiter)) {
					statements.add(sqlStatement.toString());
					sqlStatement.setLength(0);
				}
			}
			return statements;
		} catch (IOException e) {
			throw new RuntimeException("Failed parsing sql", e);
		}
	}
	/**
	 * Renames a folder name with another.
	 * @param String -  the old folder name.
	 * @param String -  the new folder name.
	 * @return int - the number of rows updated.
	 * @throws SQLException
	 */
	@Override
	public int updateFolder(final String old, final String newName) throws SQLException 
	{
		String updateFolder = "UPDATE folders SET folderName = ? WHERE id = ?;";
		String findFolder = "SELECT id FROM folders WHERE folderName = ?;";
		int updateded = 0;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(findFolder);
			stmt.setString(1, old);
			stmt.execute();
			
			ResultSet results = stmt.getResultSet();
			int folderId = 0;
			
			if(results.next())
			{
				folderId = results.getInt(1);
				stmt = connection.prepareStatement(updateFolder);
				stmt.setString(1, newName);
				stmt.setInt(2, folderId);
				updateded += stmt.executeUpdate();
			}
			
		}
		return updateded;
	}
	/**
	 * 
	 * Updates the folder id which a email is using to another
	 * @param String - the folder name to change to.
	 * @return int - the number of rows updated.
	 * @throws SQLException
	 */
	@Override
	public int updateMailFolder(final int mailId, final String folder)throws SQLException
	{		
		String updateFolder = "UPDATE emails SET folderId = ? WHERE id = ?;";
		String findFolder = "Select id From folders where foldername = ?;";
		int updateded = 0;
		try (Connection connection = DriverManager.getConnection(url + dbName , config.getMySqlUserName(), config.getMySqlPassword());) 
		{
			PreparedStatement stmt =  connection.prepareStatement(findFolder);
			stmt.setString(1, folder);
			stmt.execute();
			
			ResultSet results = stmt.getResultSet();
			int folderId = 0;
			if(results.next())
			{
				folderId = results.getInt(1);
				stmt = connection.prepareStatement(updateFolder);
				stmt.setInt(1, folderId);
				stmt.setInt(2, mailId);
				updateded += stmt.executeUpdate();
			}
		}
		return updateded;
	}
	
}
