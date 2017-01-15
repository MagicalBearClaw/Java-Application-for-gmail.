package JAG.ca.mcmahon.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import JAG.ca.mcmahon.beans.MailBean;
import JAG.ca.mcmahon.beans.MailServerConfigurationBean;
import JAG.ca.mcmahon.data.Controllers.MailController;
import jodd.mail.EmailAttachment;
import jodd.mail.MailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailControllerTest 
{


	private MailBean bean1;
	private MailBean bean2;

	@Rule
	public MethodLogger methodLogger = new MethodLogger();

	// Real programmers use loggings, not System.out.println
	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/*
	 * The purpose of this test was to send an email with everything 
	 * but attachments(embedded or not).The test succeeds if it finds
	 * the same email when retrieving email.
	 */
	@Test
	public void mailControlerSendAllButAttachmentsValidEmailTest()
	{
		MailController controller =  new MailController();
		MailServerConfigurationBean imap =  new MailServerConfigurationBean(993, "imap.gmail.com", "", "", "mike");
		MailServerConfigurationBean smtp =  new MailServerConfigurationBean(465, "smtp.gmail.com", "", "", "mike");
		bean1.getBccsProperty().add("");
		bean1.getBccsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.setSubject("Hello from test 16");

		try
		{
			controller.sendMail(bean1, smtp);

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				log.error("Threaded sleep failed", e);
			}
			ArrayList<MailBean> beans = controller.getMail(imap);	
			MailBean recieved = null;

			for(MailBean temp : beans)
			{
				if(bean1.getSubject().equals(temp.getSubject()))
				{
					recieved = temp;
					break;
				}
			}

			bean1.setRecievedDate(recieved.getRecievedDate());
			bean1.setSentDate(recieved.getSentDate());

			boolean test = false;
			if(beans != null && beans.size() != 0)
			{
				test =  bean1.equals(recieved);
			}
			if(!test)
			{
				LogFailurePoint(bean1, recieved, "mailControlerSendAllButAttachmentsValidEmailTest");
			}
			else
				log.info("mailControlerSendAllButAttachmentsValidEmailTest PASSED");
			assertTrue(test);
		}
		catch(MailException e)
		{
			log.error(e.getMessage());
			fail("failed the test");
		}		
	}

	/*
	 * The point of this test case is to test for the MailException to be thrown
	 * This method can be thrown when the user puts invalid information such as
	 * username and password. This test is for imap.
	 */
	@Test(expected=MailException.class)
	public void mailControlerSendAllExceptionThrownImap()
	{
		MailController controller =  new MailController();
		MailServerConfigurationBean imap =  new MailServerConfigurationBean(993, "imap.gmail.com", "", "", "mike");
		MailServerConfigurationBean smtp =  new MailServerConfigurationBean(465, "smtp.gmail.com", "", "", "mike");
		bean1.getBccsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.setSubject("Hello from test 5");
		bean1.getAttachmentsProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean1.getEmbedAttachmentProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());

		ArrayList<MailBean> beans = controller.getMail(imap);
		log.info("mailControlerSendAllExceptionThrownImap FAILED");
	}

	/*
	 * The point of this test case is to test for the MailException to be thrown
	 * This method can be thrown when the user puts invalid information such as
	 * username and password. This test is for smtp.
	 */
	@Test(expected=MailException.class)
	public void mailControlerSendAllExceptionThrownSmtp()
	{
		MailController controller =  new MailController();
		MailServerConfigurationBean imap =  new MailServerConfigurationBean(993, "imap.gmail.com", "", "", "mike");
		MailServerConfigurationBean smtp =  new MailServerConfigurationBean(465, "smtp.gmail.com", "", "", "mike");
		bean1.getBccsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.setSubject("Hello from test 5");
		bean1.getAttachmentsProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean1.getEmbedAttachmentProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());
		controller.sendMail(bean1, smtp);
		log.info("mailControlerSendAllExceptionThrownSmtp FAILED");
	}
	/*
	 * The purpose of this test was to send an email with everything 
	 * including both types of attachments.The test succeeds if it finds
	 * the same email when retrieving email.
	 */

	@Test
	public void mailControlerSendAllmailTest()
	{
		MailController controller =  new MailController();
		MailServerConfigurationBean imap =  new MailServerConfigurationBean(993, "imap.gmail.com", "", "", "mike");
		MailServerConfigurationBean smtp =  new MailServerConfigurationBean(465, "smtp.gmail.com", "", "", "mike");
		bean1.getBccsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.setSubject("Hello from test 5");
		bean1.getAttachmentsProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean1.getEmbedAttachmentProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());

		try
		{
			controller.sendMail(bean1, smtp);

			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				log.error("Threaded sleep failed", e);
			}
			ArrayList<MailBean> beans = controller.getMail(imap);	
			MailBean recieved = null;

			for(MailBean temp : beans)
			{
				if(bean1.getSubject().equals(temp.getSubject()))
				{
					recieved = temp;
					break;
				}
			}

			bean1.setRecievedDate(recieved.getRecievedDate());
			bean1.setSentDate(recieved.getSentDate());

			boolean test = false;
			if(beans != null && beans.size() != 0)
			{
				test =  recieved.getAttachments().size() >= 1 && recieved.getEmbedAttachment().size() >= 1;
			}
			if(!test)
			{
				LogFailurePoint(bean1, recieved, "mailControlerSendAllmailTest");
			}
			else
				log.info("mailControlerSendAllmailTest PASSED");
			assertTrue(test);
		}
		catch(MailException e)
		{
			log.error(e.getMessage());
			fail("failed the test");
		}		
	}
	/*
	 * The purpose of this test was to send an email with everything 
	 * but normal attachments(the email includes an embedded attachment).The test succeeds if it finds
	 * the same email when retrieving email.
	 */
	@Test
	public void mailControlerSendAttachmentEmbededValidEmailTest()
	{
		MailController controller =  new MailController();
		MailServerConfigurationBean imap =  new MailServerConfigurationBean(993, "imap.gmail.com", "", "", "mike");
		MailServerConfigurationBean smtp =  new MailServerConfigurationBean(465, "smtp.gmail.com", "", "", "mike");
		bean1.getBccsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.setSubject("Hello from test 3");
		bean1.getEmbedAttachmentProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());

		try
		{
			controller.sendMail(bean1, smtp);

			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				log.error("Threaded sleep failed", e);
			}
			ArrayList<MailBean> beans = controller.getMail(imap);	
			MailBean recieved = null;

			for(MailBean temp : beans)
			{
				if(bean1.getSubject().equals(temp.getSubject()))
				{
					recieved = temp;
					break;
				}
			}

			bean1.setRecievedDate(recieved.getRecievedDate());
			bean1.setSentDate(recieved.getSentDate());

			boolean test = false;
			if(beans != null && beans.size() != 0)
			{
				test =  recieved.getEmbedAttachment().size() >= 1;
			}
			if(!test)
			{
				LogFailurePoint(bean1, recieved, "mailControlerSendAttachmentEmbededValidEmailTest");
			}
			else
				log.info("mailControlerSendAttachmentEmbededValidEmailTest PASSED");
			assertTrue(test);
		}
		catch(MailException e)
		{
			log.error(e.getMessage());
			fail("failed the test");
		}		
	}
	/*
	 * The purpose of this test was to send an email with everything 
	 * but attachments(except embedded attachments).The test succeeds if it finds
	 * the same email when retrieving email.
	 */
	@Test
	public void mailControlerSendAttachmentValidEmailTest()
	{
		MailController controller =  new MailController();
		MailServerConfigurationBean imap =  new MailServerConfigurationBean(993, "imap.gmail.com", "", "", "mike");
		MailServerConfigurationBean smtp =  new MailServerConfigurationBean(465, "smtp.gmail.com", "", "", "mike");
		bean1.getBccsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.setSubject("Hello from test 4");
		bean1.getAttachmentsProperty().add( EmailAttachment.attachment().bytes(new File("cat.jpg")).create());

		try
		{
			controller.sendMail(bean1, smtp);

			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				log.error("Threaded sleep failed", e);
			}
			ArrayList<MailBean> beans = controller.getMail(imap);	
			MailBean recieved = null;

			for(MailBean temp : beans)
			{
				if(bean1.getSubject().equals(temp.getSubject()))
				{
					recieved = temp;
					break;
				}
			}

			bean1.setRecievedDate(recieved.getRecievedDate());
			bean1.setSentDate(recieved.getSentDate());

			boolean test = false;
			if(beans != null && beans.size() != 0)
			{
				test =  recieved.getAttachments().size() >= 1;
			}
			if(!test)
			{
				LogFailurePoint(bean1, recieved, "mailControlerSendAttachmentValidEmailTest");
			}
			else
				log.info("mailControlerSendAttachmentValidEmailTest PASSED");
			assertTrue(test);
		}
		catch(MailException e)
		{
			log.error(e.getMessage());
			fail("failed the test");
		}		
	}
	/*
	 * The purpose of this test was to send an email with the minimum required information.
	 * so (1 from, 1 to, 1 subject and message).The test succeeds if it finds
	 * the same email when retrieving email.
	 */
	@Test
	public void mailControlerSendMinimumValidEmailTest()
	{
		MailController controller =  new MailController();
		MailServerConfigurationBean imap =  new MailServerConfigurationBean(993, "imap.gmail.com", "", "", "mike");
		MailServerConfigurationBean smtp =  new MailServerConfigurationBean(465, "smtp.gmail.com", "", "", "mike");

		try
		{
			controller.sendMail(bean1, smtp);

			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				log.error("Threaded sleep failed", e);
			}
			ArrayList<MailBean> beans = controller.getMail(imap);	
			MailBean recieved = null;

			for(MailBean temp : beans)
			{
				if(bean1.getSubject().equals(temp.getSubject()))
				{
					recieved = temp;
					break;
				}
			}

			bean1.setRecievedDate(recieved.getRecievedDate());
			bean1.setSentDate(recieved.getSentDate());

			boolean test = false;
			if(beans != null && beans.size() != 0)
			{
				test =  bean1.equals(recieved);
			}
			if(!test)
			{
				LogFailurePoint(bean1, recieved,"mailControlerSendMinimumValidEmailTest");
			}
			else
				log.info("mailControlerSendMinimumValidEmailTest PASSED");
			assertTrue(test);
		}
		catch(MailException e)
		{
			log.error(e.getMessage());
			fail("failed the test");
		}		
	}
	/*
	 * The purpose of this test was to send an email with everything 
	 * but attachments(embedded or not) however adds the sender e-mail address 
	 * as a cc and bcc .The test succeeds if it finds
	 * the same email when retrieving email.
	 */
	@Test
	public void mailControlerSendWithSenderEmailAsCCAndBccValidEmailTest()
	{
		MailController controller =  new MailController();
		MailServerConfigurationBean imap =  new MailServerConfigurationBean(993, "imap.gmail.com", "", "", "mike");
		MailServerConfigurationBean smtp =  new MailServerConfigurationBean(465, "smtp.gmail.com", "", "", "mike");
		bean1.getBccsProperty().add("");
		bean1.getBccsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.getBccsProperty().add("");
		bean1.getCcsProperty().add("");
		bean1.setSubject("Hello from test 7");

		try
		{
			controller.sendMail(bean1, smtp);

			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				log.error("Threaded sleep failed", e);
			}
			ArrayList<MailBean> beans = controller.getMail(imap);	
			MailBean recieved = null;

			for(MailBean temp : beans)
			{
				if(bean1.getSubject().equals(temp.getSubject()))
				{
					recieved = temp;
					break;
				}
			}

			bean1.setRecievedDate(recieved.getRecievedDate());
			bean1.setSentDate(recieved.getSentDate());

			boolean test = false;
			if(beans != null && beans.size() != 0)
			{
				test =  bean1.equals(recieved);
			}
			if(!test)
			{
				LogFailurePoint(bean1, recieved, "mailControlerSendWithSenderEmailAsCCAndBccValidEmailTest");
			}
			else
				log.info("mailControlerSendWithSenderEmailAsCCAndBccValidEmailTest PASSED");
			assertTrue(test);
		}
		catch(MailException e)
		{
			log.error(e.getMessage());
			fail("failed the test");
		}		
	}
	
	@Before
	public void setUpBeans()
	{
		ArrayList<String> tosValid =  new ArrayList<String>();
		tosValid.add("");
		tosValid.add("");
		tosValid.add("");
		tosValid.add("");
		ArrayList<String> tosInvalid =  new ArrayList<String>();
		tosInvalid.add("");
		bean1 =  new MailBean("", "Hello", "hello person", "<b>hello<b>",1);
		bean1.getTosProperty().addAll(tosValid);
		bean2 =  new MailBean("", "Hello", "hello person22", "<b>hello<b>",1);
		bean2.getTosProperty().addAll(tosValid);
	}
	//tests
	private void LogFailurePoint(MailBean bean1, MailBean bean2, String testName)
	{
		log.info(testName + " FAILED");
		log.info(testName +": mailbean 1 was not equal to mailbean 2");
		if( ! (bean1.getAttachments().equals(bean2.getAttachments())))
		{
			log.info(testName +": Attachments were not equal");
		}
		
		if( ! (bean1.getEmbedAttachment().equals(bean2.getEmbedAttachment())))
		{
			log.info(testName +": Embedded Attachments were not equal");
		}
				
		if( ! (bean1.getCcs().equals(bean2.getCcs())))
		{
			log.info(testName +": ccs were not equal");
		}
		
		if( ! (bean1.getFrom().equals(bean2.getFrom())))
		{
			log.info(testName +": from were not equal");
		}
		if( ! (bean1.getHtmlMessage().equals(bean2.getHtmlMessage())))
		{
			log.info(testName +": html messages were not equal");
		}
		
		if( ! (bean1.getTextMessage().equals(bean2.getTextMessage())))
		{
			log.info(testName +": text messages were not equal");
		}
		if( ! (bean1.getReplyTos().equals(bean2.getReplyTos())))
		{
			log.info(testName +": replyTos were not equal");
		}
		if( ! (bean1.getSubject().equals(bean2.getSubject())))
		{
			log.info(testName +": subject were not equal");
		}
		if( ! (bean1.getTos().equals(bean2.getTos())))
		{
			log.info(testName +": tos were not equal");
		}
	}

}
