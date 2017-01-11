package JAG.ca.mcmahon.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import JAG.ca.mcmahon.beans.MailBean;
import JAG.ca.mcmahon.beans.MySqlConigurationBean;
import JAG.ca.mcmahon.data.Controllers.EmailDAO;
import JAG.ca.mcmahon.data.Controllers.Interfaces.MailDataAccessProvider;
import jodd.mail.EmailAttachment;

public class EmailDAOTest 
{
	
	private MailBean bean1;
	private MailBean bean2;
	private MailBean bean3;
	private MailBean bean4;
	// TODO: CHANGE YOUR USER CREDINTIALS IN BOTH THIS FILE AND IN THE EMAILDAO OBJECT (initializeDatanase) method.
	private MySqlConigurationBean config =  new MySqlConigurationBean("root", "", "localhost", 3306 );
	private MailDataAccessProvider provider =  new EmailDAO(config);
	private  final String url = "jdbc:mysql://" + config.getHostName() +":" + config.getPort() + "/gmaildatabase";
	@Rule
	public MethodLogger methodLogger = new MethodLogger();

	// Real programmers use loggings, not System.out.println
	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	@Before
	public void setupBeans()
	{
		bean1 = new MailBean("testfrom1@hotmail.com", "heloo subject 1", "testing the message 1", "<p>html message 1</p>", 1);
		bean1.getAttachmentsProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean1.getEmbedAttachmentProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());
		bean1.setRecievedDate(LocalDateTime.now());
		bean1.setSentDate(LocalDateTime.now());
		bean1.getTosProperty().add("Test1TO@hotmail.com");
		bean1.getTosProperty().add("Test1TO2@hotmail.com");
		bean1.getCcsProperty().add("Test1CC@hotmail.com");
		bean1.getCcsProperty().add("Test1CC2@hotmail.com");
		bean1.getBccsProperty().add("Test1BCC@hotmail.com");
		bean1.getBccsProperty().add("Test1BCC2@hotmail.com");
		bean1.setFolder(1);
		//test
		bean2 = new MailBean("testfrom2@hotmail.com", "heloo subject 2", "testing the message 2", "<p>html message 2</p>", 1);
		bean2.getAttachmentsProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean2.getEmbedAttachmentProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());
		bean2.setRecievedDate(LocalDateTime.of(2015, 10, 7, 10, 45));
		bean2.setSentDate(LocalDateTime.of(2015, 10, 7, 10, 45));
		bean2.getTosProperty().add("Test1TO@hotmail.com");
		bean2.getTosProperty().add("Test1TO2@hotmail.com");
		bean2.getCcsProperty().add("Test1CC@hotmail.com");
		bean2.getCcsProperty().add("Test1CC2@hotmail.com");
		bean2.getBccsProperty().add("Test1BCC@hotmail.com");
		bean2.getBccsProperty().add("Test1BCC2@hotmail.com");
		bean2.setFolder(1);
		
		bean3 = new MailBean("testfrom3@hotmail.com", "heloo subject 3", "testing the message 3", "<p>html message 3</p>", 1);
		bean3.getAttachmentsProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean3.getEmbedAttachmentProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());
		bean3.setRecievedDate(LocalDateTime.of(2015, 10, 6, 10, 45));
		bean3.setSentDate(LocalDateTime.of(2015, 10, 6, 10, 45));
		bean3.getTosProperty().add("Test3TO@hotmail.com");
		bean3.getTosProperty().add("Test1TO2@hotmail.com");
		bean3.getCcsProperty().add("Test3CC@hotmail.com");
		bean3.getCcsProperty().add("Test1CC2@hotmail.com");
		bean3.getBccsProperty().add("Test3BCC@hotmail.com");
		bean3.getBccsProperty().add("Test1BCC2@hotmail.com");
		bean3.setFolder(2);
		
		bean4 = new MailBean("testfrom4@hotmail.com", "heloo subject 4", "testing the message 4", "<p>html message 4</p>", 1);
		bean4.setRecievedDate(LocalDateTime.of(2015, 10, 5, 10, 45));
		bean4.setSentDate(LocalDateTime.of(2015, 10, 5, 10, 45));
		bean4.getTosProperty().add("Test1TO@hotmail.com");
		bean4.getTosProperty().add("Test1TO2@hotmail.com");		
	}
	/*
	 * Test the creation of a email.
	 */
	@Test
	public void TestCreateEmail()
	{
		try
		{
			provider.InitializeDataBase();
			int id = provider.createMail(bean1);
			boolean result = true;
			try(Connection con = DriverManager.getConnection(url, config.getMySqlUserName(), config.getMySqlPassword());)
			{
				PreparedStatement stmt =  con.prepareStatement("SELECT id FROM emails WHERE id = ?;");
				stmt.setInt(1,id);
				stmt.execute();
				if(!stmt.getResultSet().next())
				{
					result = false;
				}
				
				stmt =  con.prepareStatement("SELECT id FROM addresses WHERE mailId = ?;");
				stmt.setInt(1,id);
				stmt.execute();
				if(!stmt.getResultSet().next())
				{
					result = false;
				}
				stmt =  con.prepareStatement("SELECT id FROM messages WHERE mailId = ?;");
				stmt.setInt(1,id);
				stmt.execute();
				if(!stmt.getResultSet().next())
				{
					result = false;
				}
				
				stmt =  con.prepareStatement("SELECT id FROM attachments WHERE mailId = ?;");
				stmt.setInt(1,id);
				stmt.execute();
				if(!stmt.getResultSet().next())
				{
					result = false;
				}		
			}
			result = result && id == 1;
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestCreateEmail has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the creation of a email with bad data.
	 */
	@Test
	public void TestCreateEmailWithBadData()
	{
		try
		{
			provider.InitializeDataBase();
			int id = provider.createMail(null);
			log.debug("number of rows s: " + id);
			boolean result = id == 0;
			if(result)
			{
				log.debug("TestCreateEmailWithBadData has PASSED");
			}
			else
				log.debug("TestCreateEmailWithBadData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestCreateEmailWithBadData has FAILED");
			fail("sqlException occured");
		}
		
	}
	/*
	 * Test the creation of a folder.
	 */
	@Test
	public void TestCreateFolder()
	{
		try
		{
			provider.InitializeDataBase();
			boolean result = provider.createFolder("test") == 1;
			if(result)
			{
				log.debug("TestCreateFolderWithBadData has PASSED");
			}
			else
				log.debug("TestCreateFolderWithBadData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestCreateFolderWithBadData has FAILED");
			fail("sqlException occured");
		}
		
	}
	/*
	 * Test the creation of a folder with bad data.
	 */
	@Test
	public void TestCreateFolderWithBadData()
	{
		try
		{
			provider.InitializeDataBase();
			boolean result = provider.createFolder("") == 0;
			if(result)
			{
				log.debug("TestCreateFolderWithBadData has PASSED");
			}
			else
				log.debug("TestCreateFolderWithBadData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestCreateFolderWithBadData has FAILED");
			fail("sqlException occured");
		}
		
	}
	/*
	 * This test, test to see if a the database will create properly.
	 */
	@Test
	public void TestDatabaseCreation()
	{
		try
		{
			boolean result = provider.InitializeDataBase();
			if(result)
			{
				log.debug("TestDatabaseCreation has PASSED");
			}
			else
				log.debug("TestDatabaseCreation has FAILED");
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestDatabaseCreation has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the deletion of a folder.
	 */
	@Test
	public void TestDeleteFolder()
	{
		try
		{
			boolean result = true;
			provider.InitializeDataBase();
			provider.createFolder("test");
			provider.deleteFolder("test");
			try(Connection con = DriverManager.getConnection(url, config.getMySqlUserName(), config.getMySqlPassword());)
			{
				PreparedStatement stmt =  con.prepareStatement("SELECT id FROM folders WHERE folderName = ?;");
				stmt.setString(1,"test");
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}
			}		
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the deletion of a folder with invalid data
	 */
	@Test
	public void TestDeleteFolderWithInvalidData()
	{
		try
		{
			boolean result = true;
			provider.InitializeDataBase();
			provider.createFolder("test");
			provider.deleteFolder("phill");
			try(Connection con = DriverManager.getConnection(url, config.getMySqlUserName(), config.getMySqlPassword());)
			{
				PreparedStatement stmt =  con.prepareStatement("SELECT id FROM folders WHERE folderName = ?;");
				stmt.setString(1,"phill");
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}
			}		
			log.debug(result + "");
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	
	/*
	 * Test the deletion of a email.
	 */
	@Test
	public void TestDeleteMail()
	{
		try
		{
			boolean result = true;
			provider.InitializeDataBase();
			int id = provider.createMail(bean1);
			provider.deleteMail(id);
			try(Connection con = DriverManager.getConnection(url, config.getMySqlUserName(), config.getMySqlPassword());)
			{
				PreparedStatement stmt =  con.prepareStatement("SELECT id FROM emails WHERE id = ?;");
				stmt.setInt(1,id);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}
				stmt =  con.prepareStatement("SELECT id FROM addresses WHERE mailId = ?;");
				stmt.setInt(1,id);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}
				stmt =  con.prepareStatement("SELECT id FROM messages WHERE mailId = ?;");
				stmt.setInt(1,id);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}
				
				stmt =  con.prepareStatement("SELECT id FROM attachments WHERE mailId = ?;");
				stmt.setInt(1,id);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}

				
				
			}		
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the deletion of a email with invalid data
	 */
	@Test
	public void TestDeleteMailWithInvalidData()
	{
		try
		{
			boolean result = true;
			provider.InitializeDataBase();
			provider.createMail(bean1);
			int rows = provider.deleteMail(2);
			try(Connection con = DriverManager.getConnection(url, config.getMySqlUserName(), config.getMySqlPassword());)
			{
				PreparedStatement stmt =  con.prepareStatement("SELECT id FROM emails WHERE id = ?;");
				stmt.setInt(1,2);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}
				
				stmt =  con.prepareStatement("SELECT id FROM addresses WHERE mailId = ?;");
				stmt.setInt(1,2);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}
				stmt =  con.prepareStatement("SELECT id FROM messages WHERE mailId = ?;");
				stmt.setInt(1,2);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}
				
				stmt =  con.prepareStatement("SELECT id FROM attachments WHERE mailId = ?;");
				stmt.setInt(1,2);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = false;
				}		
			}		
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	
	/*
	 * Test the retrieval of a MailBean with only the email portion by id.
	 */
	@Test
	public void TestGetEmailByID()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			MailBean test =  provider.getMailById(1);
			
			boolean result = false;
			
			if(test.equals(bean1) && test.getBccsProperty().equals(bean1.getBccsProperty()))
			{
				result = true;
			}
			
			if(result)
			{
				log.debug("TestGetEmailByID has PASSED");
			}
			else
				log.debug("TestGetEmailByID has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailByID has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBean with only the email portion by id, with invalid id.
	 */
	@Test
	public void TestGetEmailByIDWithInvalidID()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			MailBean test =  provider.getMailById(-1);
			
			boolean result = false;
			
			if(test == null)
			{
				result = true;
			}
			
			if(result)
			{
				log.debug("TestGetEmailByIDWithInvalidID has PASSED");
			}
			else
				log.debug("TestGetEmailByIDWithInvalidID has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailByIDWithInvalidID has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of mail beans based on if their received date is after another date, with invalid data
	 */
	@Test
	public void TestGetEmailsThatAreAfterReceivedDateWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean2);
			provider.createMail(bean3);
			provider.createMail(bean4);
			
			ArrayList<MailBean> beans =  provider.getEmailsThatAreAfterRecievedDate(LocalDateTime.of(2015, 12, 11, 10, 45));
			boolean result = beans == null;
			if(result)
			{
				log.debug("TestGetEmailsThatAreAfterSentDate has PASSED");
			}
			else
				log.debug("TestGetEmailsThatAreAfterSentDate has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	
	/*
	 * Test the retrieval of mail beans based on if their received date is after another date.
	 */
	@Test
	public void TestGetEmailsThatAreAfterRecievedDate()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean2);
			provider.createMail(bean3);
			provider.createMail(bean4);
			
			ArrayList<MailBean> beans =  provider.getEmailsThatAreAfterRecievedDate(LocalDateTime.of(2015, 10, 6, 10, 45));
			boolean result = beans.size() == 2;
			
			if(result)
			{
				log.debug("TestGetEmailsThatAreAfterSentDate has PASSED");
			}
			else
				log.debug("TestGetEmailsThatAreAfterSentDate has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of mail beans based on if their sent date is after another date.
	 */
	@Test
	public void TestGetEmailsThatAreAfterSentDate()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean2);
			provider.createMail(bean3);
			provider.createMail(bean4);
			
			ArrayList<MailBean> beans =  provider.getEmailsThatAreAfterSentDate(LocalDateTime.of(2015, 10, 6, 10, 45));
			boolean result = beans.size() == 2;
			
			if(result)
			{
				log.debug("TestGetEmailsThatAreAfterSentDate has PASSED");
			}
			else
				log.debug("TestGetEmailsThatAreAfterSentDate has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of mail beans based on if their sent date is after another date, with invalid data
	 */
	@Test
	public void TestGetEmailsThatAreAfterSentDateWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean2);
			provider.createMail(bean3);
			provider.createMail(bean4);
			
			ArrayList<MailBean> beans =  provider.getEmailsThatAreAfterSentDate(LocalDateTime.of(2015, 12, 11, 10, 45));
			boolean result = beans == null;
			if(result)
			{
				log.debug("TestGetEmailsThatAreAfterSentDate has PASSED");
			}
			else
				log.debug("TestGetEmailsThatAreAfterSentDate has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified bcc address
	 */
	@Test
	public void TestGetEmailThatMatcBCC()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean2);
			ArrayList<MailBean> tests =  provider.getEmailsThatMatchBcc("test1BCC@hotmail.com");
			
			boolean result = tests.size() == 2;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatcBCC has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatcBCC has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatcBCC has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified bcc address with invalid data.
	 */
	@Test
	public void TestGetEmailThatMatcBCCWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean2);
			ArrayList<MailBean> tests =  provider.getEmailsThatMatchBcc("test5BCC@hotmail.com");
			
			boolean result = tests == null;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatcBCCWithInvalidData has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatcBCCWithInvalidData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatcBCCWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified bcc address
	 */
	@Test
	public void TestGetEmailThatMatcCC()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean2);
			ArrayList<MailBean> tests =  provider.getEmailsThatMathcCc("test1CC@hotmail.com");
			
			boolean result = tests.size() == 2;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatcCC has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatcCC has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatcCC has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified bcc address with invalid data.
	 */
	@Test
	public void TestGetEmailThatMatcCCWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean2);
			ArrayList<MailBean> tests =  provider.getEmailsThatMathcCc("test5BCC@hotmail.com");
			
			boolean result = tests == null;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatcCCWithInvalidData has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatcCCWithInvalidData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatchTo has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified from address.
	 */
	@Test
	public void TestGetEmailThatMatchFrom()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean1);
			ArrayList<MailBean> tests =  provider.getEmailsThatMatchFrom("testfrom1@hotmail.com");
			boolean same = false;
			for(MailBean test : tests)
			{
				if(bean1.equals(test))
					same = true;
				else
					same = false;
			}
			boolean result = tests.size() == 2 && same;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatchFrom has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatchFrom has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatchFrom has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified from address, with Invalid Data.
	 */
	@Test
	public void TestGetEmailThatMatchFromWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean1);
			ArrayList<MailBean> tests =  provider.getEmailsThatMatchFrom("test3@hotmail.com");
			
			boolean result = tests == null;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatchFromWithInvalidData has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatchFromWithInvalidData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatchFromWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified subject.
	 */
	@Test
	public void TestGetEmailThatMatchSubject()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			ArrayList<MailBean> tests =  provider.getEmailsThatMatchSubject("heloo subject 1");
			boolean result = tests.size() == 1;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatchSubject has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatchSubject has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatchSubject has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified subject, with Invalid Data.
	 */
	@Test
	public void TestGetEmailThatMatchSubjectithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean1);
			ArrayList<MailBean> tests =  provider.getEmailsThatMatchSubject("heloo subject 6");
			
			boolean result = tests == null;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatchFromWithInvalidData has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatchFromWithInvalidData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatchFromWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified to address
	 */
	@Test
	public void TestGetEmailThatMatchTo()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean4);
			ArrayList<MailBean> tests =  provider.getEmailsThatMatchTo("Test1TO@hotmail.com");
			log.debug("tests size is :" + tests.size());
			boolean result = tests.size() == 2;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatchTo has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatchTo has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatchTo has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified to address with invalid data.
	 */
	@Test
	public void TestGetEmailThatMatchToWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean3);
			ArrayList<MailBean> tests =  provider.getEmailsThatMatchTo("test5TO@hotmail.com");
			
			boolean result = tests == null;
			
			if(result)
			{
				log.debug("TestGetEmailThatMatchToWithInvalidData has PASSED");
			}
			else
				log.debug("TestGetEmailThatMatchToWithInvalidData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailThatMatchToWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified folder name.
	 */
	@Test
	public void TestGetEmailWithFolderName()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean3);
			ArrayList<MailBean> tests =  provider.getEmailsWithFolderName("inbox");
			
			boolean result = tests.size() == 1;
			
			if(result)
			{
				log.debug("TestGetEmailWithFolderName has PASSED");
			}
			else
				log.debug("TestGetEmailWithFolderName has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailWithFolderName has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBeans with the specified folder name, with a invalid folder name.
	 */
	@Test
	public void TestGetEmailWithFolderNameWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			provider.createMail(bean3);
			ArrayList<MailBean> tests =  provider.getEmailsWithFolderName("test");
			
			boolean result = tests == null;
			
			if(result)
			{
				log.debug("TestGetEmailWithFolderNameWithInvalidData has PASSED");
			}
			else
				log.debug("TestGetEmailWithFolderNameWithInvalidData has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetEmailWithFolderNameWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of a MailBean by id.
	 */
	@Test
	public void TestGetMailByID()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			MailBean test =  provider.getMailById(1);
			boolean result = false;
			
			if(test.equals(bean1) && test.getBccsProperty().equals(bean1.getBccsProperty()))
			{
				result = true;
			}
			
			if(result)
			{
				log.debug("TestGetMailByID has PASSED");
			}
			else
				log.debug("TestGetMailByID has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestGetMailByID has FAILED");
			fail("sqlException occured");
		}
		
	}
	/*
	 * Test the retrieval of a MailBean by id with invalid id.
	 */
	@Test
	public void TestGetMailByIDWithInvalidID()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			MailBean test =  provider.getMailById(-1);
			
			boolean result = false;
			
			if(test == null)
			{
				result = true;
			}
			
			if(result)
			{
				log.debug("TestGetMailByIDWithInvalidID has PASSED");
			}
			else
				log.debug("TestGetMailByIDWithInvalidID has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestRetrieveAllFolders has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the retrieval of all folders.
	 */
	@Test
	public void TestRetrieveAllFolders()
	{
		try
		{
			provider.InitializeDataBase();
			boolean result = provider.getFolders().size() == 2;
			if(result)
			{
				log.debug("TestRetrieveAllFolders has PASSED");
			}
			else
				log.debug("TestRetrieveAllFolders has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestRetrieveAllFolders has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the update of a folder with the renaming of the folder.
	 */
	@Test
	public void TestUpdateFolder()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			int rows =  provider.updateFolder("inbox", "test");
			ArrayList<String> folders = provider.getFolders();
			boolean result = false;
			for (String string : folders) 
			{
				if(string.equals("test"))
					result = true;
			}
			
			result = result && rows == 1;
			
			if(result)
			{
				log.debug("TestUpdateFolder has PASSED");
			}
			else
				log.debug("TestUpdateFolder has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolder has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the update of a folder with the renaming of the folder, with invalid data.
	 */
	@Test
	public void TestUpdateFolderWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			int rows =  provider.updateFolder("inbox", "test");
			ArrayList<String> folders = provider.getFolders();
			boolean result = false;
			for (String string : folders) 
			{
				if(string.equals("test"))
					result = true;
			}
			
			result = result && rows == 0;
			
			if(result)
			{
				log.debug("TestUpdateFolderWithInvalidData has PASSED");
			}
			else
				log.debug("TestUpdateFolderWithInvalidData has FAILED");
			
			assertFalse(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateFolderWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
	/*
	 * Test the update of an email with the specified folder.
	 */
	@Test
	public void TestUpdateMail()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			int rows =  provider.updateMailFolder(1, "sent");
			boolean result = false;
			try(Connection con = DriverManager.getConnection(url, config.getMySqlUserName(), config.getMySqlPassword());)
			{
				PreparedStatement stmt =  con.prepareStatement("Select id FROM emails where folderID = ?");
				stmt.setInt(1, 2);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = rows == 1;
				}
			}

			
			if(result)
			{
				log.debug("TestUpdateMail has PASSED");
			}
			else
				log.debug("TestUpdateMail has FAILED");
			
			assertTrue(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateMail has FAILED");
			fail("sqlException occured");
		}
		
	}
	/*
	 * Test the update of an email with the specified folder. with invalid data
	 */
	@Test
	public void TestUpdateMailWithInvalidData()
	{
		try
		{
			provider.InitializeDataBase();
			provider.createMail(bean1);
			int rows =  provider.updateMailFolder(1, "sssssssss");
			boolean result = false;
			try(Connection con = DriverManager.getConnection(url, config.getMySqlUserName(), config.getMySqlPassword());)
			{
				PreparedStatement stmt =  con.prepareStatement("Select id FROM emails where folderID = ?");
				stmt.setInt(1, 2);
				stmt.execute();
				if(stmt.getResultSet().next())
				{
					result = rows == 1;
				}
			}

			
			if(result)
			{
				log.debug("TestUpdateMailWithInvalidData has PASSED");
			}
			else
				log.debug("TestUpdateMailWithInvalidData has FAILED");
			
			assertFalse(result);
		}
		catch(SQLException e)
		{
			log.debug(e.getMessage());
			log.debug("TestUpdateMailWithInvalidData has FAILED");
			fail("sqlException occured");
		}
		
	}
	
}
