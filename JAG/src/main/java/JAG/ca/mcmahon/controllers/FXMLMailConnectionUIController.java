package JAG.ca.mcmahon.controllers;

import java.io.IOException;

/**
 * Sample Skeleton for 'MailConnectionUI.fxml' Controller Class
 */

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import JAG.ca.mcmahon.beans.MailServerConfigurationBean;
import JAG.ca.mcmahon.beans.MySqlConigurationBean;
import JAG.ca.mcmahon.custom.nodes.NumberTextField;
import JAG.ca.mcmahon.data.Controllers.EmailDAO;
import JAG.ca.mcmahon.data.Controllers.MailServerIoManager;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

public class FXMLMailConnectionUIController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="editTextName"
    private TextField editTextName; // Value injected by FXMLLoader

    @FXML // fx:id="editTextImapAddress"
    private TextField editTextImapAddress; // Value injected by FXMLLoader

    @FXML // fx:id="editTextImapPort"
    private NumberTextField editTextImapPort; // Value injected by FXMLLoader

    @FXML // fx:id="editTextSmtpAddress"
    private TextField editTextSmtpAddress; // Value injected by FXMLLoader

    @FXML // fx:id="editTextSmtpPort"
    private NumberTextField editTextSmtpPort; // Value injected by FXMLLoader

    @FXML // fx:id="editTextEmail"
    private TextField editTextEmail; // Value injected by FXMLLoader

    @FXML // fx:id="editTextPassword"
    private PasswordField editTextPassword; // Value injected by FXMLLoader

    @FXML // fx:id="editTextMysqlUrl"
    private TextField editTextMysqlUrl; // Value injected by FXMLLoader

    @FXML // fx:id="editTextMysqlPort"
    private NumberTextField editTextMysqlPort; // Value injected by FXMLLoader

    @FXML // fx:id="editTextMysqlUsername"
    private TextField editTextMysqlUsername; // Value injected by FXMLLoader

    @FXML // fx:id="editTextMysqlPassword"
    private PasswordField editTextMysqlPassword; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader

    @FXML // fx:id="buttonConnect"
    private Button buttonConnect; // Value injected by FXMLLoader

	private Stage stage;
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
	private MailServerConfigurationBean imapConfig;
	private MailServerConfigurationBean smtpConfig;
	private MySqlConigurationBean mysql;
	private MailServerIoManager ioManager =  new MailServerIoManager(); 
	private EmailDAO emailDao;
	/**
	 * The event handler for the cancel button.
	 * allows to cancel all changes made.
	 * @param event -  the event data.
	 */
    @FXML
    void onButtonCancelClick(ActionEvent event) 
    {
		stage.close();
    }
    
    /**
     *  The event handler for the connect/save button.
     *  validates all field and displays appropriate error
     *  messages when fields are not valid.
     * @param event -  the event data.
     */
    @FXML
    void onButtonConnectClick(ActionEvent event) 
    {
    	StringBuffer errorMessage =  new StringBuffer();
    	boolean isAllfieldsVaild = true;
    	if(!isValidName(editTextName.getText()))
    	{
    		errorMessage.append("-The name must be between 6 and 15 charecters.\n");
    		isAllfieldsVaild = false;
    	}
    	if(!isValidString(editTextImapAddress.getText()))
    	{
    		errorMessage.append("-The Imap address cannot be empty.\n");
    		isAllfieldsVaild = false;
    	}
    	if(!isValidString(editTextSmtpAddress.getText()))
    	{
    		errorMessage.append("-The smtp address cannot be empty.\n");
    		isAllfieldsVaild = false;
    	}
    	if(!isValidString(editTextEmail.getText()))
    	{
    		errorMessage.append("-The email address cannot be empty.\n");
    		isAllfieldsVaild = false;
    	}
    	if(!isValidString(editTextPassword.getText()))
    	{
    		errorMessage.append("-The password cannot be empty.\n");
    		isAllfieldsVaild = false;
    	}
    	if(!isValidString(editTextMysqlUrl.getText()))
    	{
    		errorMessage.append("-The mysql address cannot be empty.\n");
    		isAllfieldsVaild = false;
    	}
    	if(!isValidString(editTextMysqlUsername.getText()))
    	{
    		errorMessage.append("-The mysql username cannot be empty.\n");
    		isAllfieldsVaild = false;
    	}
    	if(editTextMysqlPassword.getText() == null)
    	{
    		errorMessage.append("-The mysql password cannot be null.\n");
    		isAllfieldsVaild = false;
    	}
    	try
    	{
    		int smtpPort = Integer.parseInt(editTextSmtpPort.getText());
    		int imapPort = Integer.parseInt(editTextImapPort.getText());
    		int mysqlPort = Integer.parseInt(editTextMysqlPort.getText());
	    	if(!isValidPort(smtpPort))
	    	{
	    		errorMessage.append("the port must be in the range 1 to 65535");
	    		isAllfieldsVaild = false;
	    	}

	    	if(!isValidPort(imapPort))
	    	{
	    		errorMessage.append("the port must be in the range 1 to 65535");
	    		isAllfieldsVaild = false;
	    	}

	    	if(!isValidPort(mysqlPort))
	    	{
	    		errorMessage.append("the port must be in the range 1 to 65535");
	    		isAllfieldsVaild = false;
	    	}

    	}
    	catch(NumberFormatException nfe)
    	{
    		errorMessage.append("The port(s) you have entered are not a nuber.");
    		isAllfieldsVaild = false;
    	}
    	finally
    	{
        	if(isAllfieldsVaild)
        	{
        		editTextSmtpPort.getText();
        		editTextImapPort.getText();
        		       		
        		try 
        		{
        			// give empty string for the first parameter so it stores the properties in the 
        			// same location as the pom.xml and other non jared files.
					ioManager.saveTextProperties("", "imap", imapConfig, "imap properties");
					ioManager.saveTextProperties("", "smtp", smtpConfig, "imap properties");
					ioManager.saveTextProperties("", "mysql", mysql, "mysql properties");
	        		stage.close();
				} 
        		catch (IOException e) 
        		{
	    			Alert errorAlert =  new Alert(AlertType.ERROR);
	    			errorAlert.setContentText(e.getMessage());
	    			e.printStackTrace();
	    			errorAlert.setHeaderText("Could not save the connection properties");
	    			errorAlert.setResizable(false);
	    			errorAlert.initModality(Modality.WINDOW_MODAL);
	    			errorAlert.initOwner(stage);
	    			errorAlert.showAndWait();
				}

        	}
        	else
        	{
    			Alert errorAlert =  new Alert(AlertType.ERROR);
    			errorAlert.setContentText(errorMessage.toString());
    			errorAlert.setHeaderText("Error(s) for the follwing fields");
    			errorAlert.setResizable(false);
    			errorAlert.initModality(Modality.WINDOW_MODAL);
    			errorAlert.initOwner(stage);
    			errorAlert.showAndWait();
        	}
    	}    
    }
       

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() 
    {
        assert editTextName != null : "fx:id=\"editTextName\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextImapAddress != null : "fx:id=\"editTextImapAddress\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextImapPort != null : "fx:id=\"editTextImapPort\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextSmtpAddress != null : "fx:id=\"editTextSmtpAddress\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextSmtpPort != null : "fx:id=\"editTextSmtpPort\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextEmail != null : "fx:id=\"editTextEmail\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextPassword != null : "fx:id=\"editTextPassword\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextMysqlUrl != null : "fx:id=\"editTextMysqlUrl\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextMysqlPort != null : "fx:id=\"editTextMysqlPort\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextMysqlUsername != null : "fx:id=\"editTextMysqlUsername\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert editTextMysqlPassword != null : "fx:id=\"editTextMysqlPassword\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert buttonCancel != null : "fx:id=\"buttonCancel\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
        assert buttonConnect != null : "fx:id=\"buttonConnect\" was not injected: check your FXML file 'MailConnectionUI.fxml'.";
     
    }
    /**
     * Set the connection ui stage.
     * @param stage - the stage.
     */
    public void setStage(Stage stage)
    {
    	this.stage = stage;
    }
    
    /**
     * validate the name. make sure that it is
     * in a valid range of characters.
     * @param name
     * @return true if valid
     */
    private boolean isValidName(String name)
    {
    	if(name == null)
    		return false;
    	if(name.isEmpty())
    		return false;
    	int nameLenght = name.length();
    	if( nameLenght >= 6 && nameLenght <= 15)
    		return true;
    	else
    		return false;
    }
    /**
     * validate a string. makes sure that the string entered
     * is not null or empty
     * @param name
     * @return true if valid
     */
    private boolean isValidString(String name)
    {
    	if(name == null)
    		return false;
    	if(name.isEmpty())
    		return false;
    	else
    		return true;
    }
    /**
     * validate a port. makes sure that
     * the port is not 0 and less than 65535
     * @param name
     * @return true if valid
     */
    private boolean isValidPort(int port)
    {
    	if(port > 0 && port <= 65535)
    		return true;
    	else
    		return false;
    }
    
    // binds the text fields to the configuration beans.
    public void bindFields()
    {
        Bindings.bindBidirectional(editTextEmail.textProperty(),imapConfig.getGmailEmailProperty());
        Bindings.bindBidirectional(editTextPassword.textProperty(), imapConfig.getGmailPasswordProperty());
        Bindings.bindBidirectional(editTextImapAddress.textProperty(), imapConfig.getHostNameProperty());
        Bindings.bindBidirectional(editTextImapPort.textProperty(), imapConfig.getPortNumberProperty(),  new NumberStringConverter("##0"));
        Bindings.bindBidirectional(editTextName.textProperty(), imapConfig.getNameProperty());
        		
        Bindings.bindBidirectional(editTextEmail.textProperty(), smtpConfig.getGmailEmailProperty());
        Bindings.bindBidirectional(editTextPassword.textProperty(), smtpConfig.getGmailPasswordProperty());
        Bindings.bindBidirectional(editTextSmtpAddress.textProperty(), smtpConfig.getHostNameProperty());
        Bindings.bindBidirectional(editTextSmtpPort.textProperty(), smtpConfig.getPortNumberProperty(),  new NumberStringConverter("##0"));
        Bindings.bindBidirectional(editTextName.textProperty(), smtpConfig.getNameProperty());
						
        Bindings.bindBidirectional(editTextMysqlUrl.textProperty(), mysql.getHostNameProperty());
        Bindings.bindBidirectional(editTextMysqlUsername.textProperty(), mysql.getMySqlUserNameProperty());
        Bindings.bindBidirectional(editTextMysqlPassword.textProperty(), mysql.getMySqlPasswordProperty());
        Bindings.bindBidirectional(editTextMysqlPort.textProperty(), mysql.getPortProperty(),  new NumberStringConverter("##0"));
    }
    /**
     * Sets the imap configuration bean.
     * @param imap
     */
    public void setImapServer(MailServerConfigurationBean imap)
    {
    	this.imapConfig = imap;
    }
    /**
     * Sets the smtp configuration bean.
     * @param imap
     */
    public void setSmtpServer(MailServerConfigurationBean smtp)
    {
    	this.smtpConfig = smtp;
    }
    /**
     * Sets the mysql configuration bean.
     * @param imap
     */
    public void setMysqlServer(MySqlConigurationBean mysql)
    {
    	this.mysql = mysql;
    }



}

