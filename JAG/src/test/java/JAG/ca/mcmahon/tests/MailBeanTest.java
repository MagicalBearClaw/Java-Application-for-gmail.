package JAG.ca.mcmahon.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JAG.ca.mcmahon.beans.MailBean;
import jodd.mail.EmailAttachment;

public class MailBeanTest {

	private MailBean bean1;
	private MailBean bean2;
	@Rule
	public MethodLogger methodLogger = new MethodLogger();

	// Real programmers use logging, not System.out.println
	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	@Test
	public void mailBeanEqualAttachmentTest()
	{
		bean1.getEmbedAttachmentProperty().add(EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());
		bean1.getAttachmentsProperty().add(EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean1.getBccsProperty().add("test3@hotmail.com");
		bean1.getBccsProperty().add("test4@hotmail.com");
		bean1.getCcsProperty().add("test5@hotmail.com");
		bean1.getCcsProperty().add("test6@hotmail.com");
		bean1.getReplyTosProperty().add("test7@hotail.com");
		bean1.getReplyTosProperty().add("test8@hotail.com");
		bean2.getEmbedAttachmentProperty().add(EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());
		bean2.getAttachmentsProperty().add(EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean2.getBccsProperty().add("test3@hotmail.com");
		bean2.getBccsProperty().add("test4@hotmail.com");
		bean2.getCcsProperty().add("test5@hotmail.com");
		bean2.getCcsProperty().add("test6@hotmail.com");
		bean2.getReplyTosProperty().add("test7@hotail.com");
		bean2.getReplyTosProperty().add("test8@hotail.com");
		boolean test =  bean1.equals(bean2);
		if(test)
			log.info("mailBeanEqualAttachmentTest PASSED");
		else
			LogFailurePoint(bean1, bean2, "mailBeanEqualAttachmentTest");
		assertTrue(test);
	}
	
	@Test
	public void mailBeanEqualMaximumTest()
	{
		bean1.getEmbedAttachmentProperty().add(EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());
		bean1.getAttachmentsProperty().add(EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		bean2.getEmbedAttachmentProperty().add(EmailAttachment.attachment().bytes(new File("cat.jpg")).setInline("cat.jpg").create());
		bean2.getAttachmentsProperty().add(EmailAttachment.attachment().bytes(new File("cat.jpg")).create());
		
		boolean test =  bean1.equals(bean2);
		if(test)
			log.info("mailBeanEqualMaximumTest PASSED");
		else
			LogFailurePoint(bean1, bean2, "mailBeanEqualMaximumTest");
		assertTrue(test);
	}

	@Test
	public void mailBeanEqualMinimumTest()
	{
		boolean test =  bean1.equals(bean2);
		if(test)
			log.info("mailBeanEqualMinimumTest PASSED");
		else
			LogFailurePoint(bean1, bean2, "mailBeanEqualMinimumTest");
		assertTrue(test);
	}
	@Before
	public void setUpBeans()
	{
		bean1 =  new MailBean("test@hotmail.com", "Hello", "hello person", "<b>hello<b>", 1);
		bean1.getTosProperty().add("test@hotmail.com");
		bean2 =  new MailBean("test@hotmail.com", "Hello", "hello person", "<b>hello<b>", 1);
		bean2.getTosProperty().add("test@hotmail.com");

	}
	private void LogFailurePoint(MailBean bean1, MailBean bean2, String testName)
	{
		log.info(testName + " FAILED");
		log.info(testName +": mailbean 1 was not equal to mailbean 2");
		log.debug("failure size is  " + bean1.getAttachmentsProperty().size() + ":" +bean2.getAttachmentsProperty().size());
		if( ! (bean1.getAttachmentsProperty().equals(bean2.getAttachmentsProperty())))
		{
			log.info(testName +": Attachments were not equal");
		}
		
		if( ! (bean1.getEmbedAttachmentProperty().equals(bean2.getEmbedAttachmentProperty())))
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
