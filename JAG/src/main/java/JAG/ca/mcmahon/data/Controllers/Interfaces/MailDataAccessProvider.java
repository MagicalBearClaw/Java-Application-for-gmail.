package JAG.ca.mcmahon.data.Controllers.Interfaces;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import JAG.ca.mcmahon.beans.MailBean;

public interface MailDataAccessProvider 
{
	// Create DB and Tables
	public boolean InitializeDataBase() throws SQLException;
	
	// Create
	public int createMail(final MailBean mail)throws SQLException;
	public int createFolder(final String folder)throws SQLException;
	
	//Read
	public ArrayList<String> getFolders()throws SQLException;
	public MailBean getMailById(final int id) throws SQLException;
	public MailBean getEmailById(final int id) throws SQLException;
	public ArrayList<MailBean> getEmailsWithFolderName(final String Name)throws SQLException;
	public ArrayList<MailBean> getEmailsThatMatchFrom(final String from)throws SQLException;
	public ArrayList<MailBean> getEmailsThatMatchTo(final String to)throws SQLException;
	public ArrayList<MailBean> getEmailsThatMatchBcc(final String bcc)throws SQLException;
	public ArrayList<MailBean> getEmailsThatMathcCc(final String cc)throws SQLException;
	public ArrayList<MailBean> getEmailsThatMatchSubject(final String subject)throws SQLException;
	public ArrayList<MailBean> getEmailsThatMatchRecievedDate(final LocalDateTime recieved)throws SQLException;
	public ArrayList<MailBean> getEmailsThatAreAfterRecievedDate(final LocalDateTime recieved)throws SQLException;
	public ArrayList<MailBean> getEmailsThatAreAfterSentDate(final LocalDateTime sent)throws SQLException;
	public ArrayList<MailBean> getEmailsThatMatchSentDate(final LocalDateTime sent)throws SQLException;
	// Update
	public int updateMailFolder(final int mailid, final String folder)throws SQLException;
	public int updateFolder(final String old, final String newName) throws SQLException;

	// Delete
	public int deleteFolder(final String folder)throws SQLException;
	public int deleteMail(final int id)throws SQLException;
}
