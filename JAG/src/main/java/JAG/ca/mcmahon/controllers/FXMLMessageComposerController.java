package JAG.ca.mcmahon.controllers;

import static java.nio.file.Paths.get;

import java.io.File;
import java.io.FileWriter;

/**
 * Sample Skeleton for 'MessageComposerUI.fxml' Controller Class
 */

import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Observable;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JAG.ca.mcmahon.beans.MailBean;
import JAG.ca.mcmahon.beans.MailServerConfigurationBean;
import JAG.ca.mcmahon.data.Controllers.EmailDAO;
import JAG.ca.mcmahon.data.Controllers.MailController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jdk.jfr.events.FileWriteEvent;
import jodd.mail.EmailAttachment;
import sun.security.ec.ECKeyFactory;

public class FXMLMessageComposerController 
{

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="htmlEditor"
    private HTMLEditor htmlEditor; // Value injected by FXMLLoader

    @FXML // fx:id="textFiieldTo"
    private ComboBox<String> textFiieldTo; // Value injected by FXMLLoader

    @FXML // fx:id="textFieldBcc"
    private ComboBox<String> textFieldBcc; // Value injected by FXMLLoader

    @FXML // fx:id="textFieldCc"
    private ComboBox<String> textFieldCc; // Value injected by FXMLLoader
    
    @FXML // fx:id="comboBoxAttachments"
    private ComboBox<String> comboBoxAttachments; // Value injected by FXMLLoader

    @FXML // fx:id="comboBoxEmbededAttachments"
    private ComboBox<String> comboBoxEmbededAttachments; // Value injected by FXMLLoader

    @FXML // fx:id="textFieldSubject"
    private TextField textFieldSubject; // Value injected by FXMLLoader

    @FXML // fx:id="buttonToAdd"
    private Button buttonToAdd; // Value injected by FXMLLoader

    @FXML // fx:id="buttonToDelete"
    private Button buttonToDelete; // Value injected by FXMLLoader

    @FXML // fx:id="buttonBccAdd"
    private Button buttonBccAdd; // Value injected by FXMLLoader

    @FXML // fx:id="buttonBccDelete"
    private Button buttonBccDelete; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCcAdd"
    private Button buttonCcAdd; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCcDelete"
    private Button buttonCcDelete; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCreate"
    private Button buttonCreate; // Value injected by FXMLLoader

    @FXML // fx:id="buttonCancel"
    private Button buttonCancel; // Value injected by FXMLLoader
    
    @FXML // fx:id="buttonAddAtachments"
    private Button buttonAddAtachments; // Value injected by FXMLLoader

    @FXML // fx:id="buttonDeleteAttachment"
    private Button buttonDeleteAttachment; // Value injected by FXMLLoader

    @FXML // fx:id="buttonAddEmbededAttachment"
    private Button buttonAddEmbededAttachment; // Value injected by FXMLLoader

    @FXML // fx:id="buttonDeleteEmbededAtachment"
    private Button buttonDeleteEmbededAtachment; // Value injected by FXMLLoader
    

	private Stage stage;

	private EmailDAO emailDao;
	
	private MailBean newMessage =  new MailBean();
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

	private MailServerConfigurationBean mscb;
	/**
	 * The button to add a bcc to the message.
	 * @param event - the event data
	 */
    @FXML
    void buttonBccAddClick(ActionEvent event) 
    {
    	String email = textFieldBcc.getEditor().getText();
    	
    	if(!email.isEmpty() && email != null)
    	{
    		textFieldBcc.getItems().add(email);
    		textFieldBcc.getEditor().setText("");
    	}
    	
    }
    
    private void bindFields()
    {
    	  Bindings.bindBidirectional(textFieldBcc.itemsProperty(), newMessage.getBccsProperty());
    	  Bindings.bindBidirectional(textFieldCc.itemsProperty(), newMessage.getCcsProperty());
    	  Bindings.bindBidirectional(textFiieldTo.itemsProperty(), newMessage.getTosProperty());
    }
    
	/**
	 * The button to delete a bcc to the message.
	 * @param event - the event data
	 */
    @FXML
    void buttonBccDeleteClick(ActionEvent event) 
    {
    	int index = textFieldBcc.getSelectionModel().getSelectedIndex();
    	log.debug("the index to delete bcc is" + index);
    	if(index >= 0)
    	 textFieldBcc.getItems().remove(index);
    	
    }
	/**
	 * The button to cancel a message.
	 * closes the message composer.
	 * @param event - the event data
	 */
    @FXML
    void buttonCancelClick(ActionEvent event) 
    {
    	stage.close();
    }
    
	/**
	 * The button to add a cc to the message.
	 * @param event - the event data
	 */
    @FXML
    void buttonCcAddClick(ActionEvent event) 
    {
    	String email = textFieldCc.getEditor().getText();
    	
    	if(!email.isEmpty() && email != null)
    	{
    		textFieldCc.getItems().add(email);
    		textFieldCc.getEditor().setText("");
    	}
    }
	/**
	 * The button to delete a cc to the message.
	 * @param event - the event data
	 */
    @FXML
    void buttonCcDeleteClick(ActionEvent event) 
    {
    	int index = textFieldCc.getSelectionModel().getSelectedIndex();
    	log.debug("the index to delete cc is" + index);
    	if(index >= 0)
    		textFieldCc.getItems().remove(index);
    }
	/**
	 * The button to send the message.
	 * @param event - the event data
	 */
    @FXML
    void buttonCreateClick(ActionEvent event) 
    {
    	StringBuilder builder =  new StringBuilder();
    	if(textFieldSubject.getText() != null && textFieldSubject.getText().equals(""))
    	{
    		builder.append("-The subject field cannot be empty.\n");
    	}
    	
    	if(textFiieldTo.getItems().size() == 0)
    	{
    		builder.append("-There has to be a recipeient to this message. \n");
    	}
    	
    	MailBean bean =  new MailBean(mscb.getGmailEmail(), textFieldSubject.getText(), "", htmlEditor.getHtmlText(), 2);
    	
    	bean.getTosProperty().addAll(textFiieldTo.getItems());
    	bean.getBccsProperty().addAll(textFieldBcc.getItems());
    	bean.getCcsProperty().addAll(textFieldCc.getItems());
    	bean.setSentDate(LocalDateTime.now());
    	bean.setRecievedDate(LocalDateTime.now());
    	
    	ObservableList<String> attachments = comboBoxAttachments.getItems();
    	
    	for(String attach : attachments)
    	{
    		bean.getAttachmentsProperty().add( EmailAttachment.attachment().bytes(new File(attach)).create() );
    	}
    	
    	if(!builder.toString().equals(""))
    	{
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText(builder.toString());
			errorAlert.setHeaderText("Error(s) for the follwing fields");
			errorAlert.setResizable(false);
			errorAlert.initModality(Modality.WINDOW_MODAL);
			errorAlert.initOwner(stage);
			errorAlert.showAndWait();
    	}
    	else
    	{
    		log.debug("cc size is" + textFieldCc.getItems().size());
    		MailController c =  new MailController();
    		c.sendMail(bean, mscb);
    		try 
    		{
				emailDao.createMail(bean);
			} catch (SQLException e) 
    		{
				Alert errorAlert =  new Alert(AlertType.ERROR);
				errorAlert.setContentText("there was an error inserting this email into the database");
				errorAlert.setHeaderText("Error(s) inserting msg into database");
				errorAlert.setResizable(false);
				errorAlert.initModality(Modality.WINDOW_MODAL);
				errorAlert.initOwner(stage);
				errorAlert.showAndWait();
			}
    		finally
    		{
        		stage.close();
    		}
    	}
    	
    }
	/**
	 * The button to add a to to the message.
	 * @param event - the event data
	 */
    @FXML
    void buttonToAddClick(ActionEvent event) 
    {
    	String email = textFiieldTo.getEditor().getText();
    	
    	if(!email.isEmpty() && email != null)
    	{
    		textFiieldTo.getItems().add(email);
    		textFiieldTo.getEditor().setText("");
    	}
    }
	/**
	 * The button to delete a to to the message.
	 * @param event - the event data
	 */
    @FXML
    void buttonToDeleteClick(ActionEvent event) 
    {
    	int index = textFiieldTo.getSelectionModel().getSelectedIndex();
    	log.debug("the index to delete is" + index);
    	if(index >= 0)
    		textFiieldTo.getItems().remove(index);
    }


    @FXML
    void buttonAddAtachmentsClick(ActionEvent event) 
    {
    	 FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Add a attachment");
    	 fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Text Files", "*.txt"),
    	         new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
    	         new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
    	         new ExtensionFilter("All Files", "*.*"));
    	 File selectedFile = fileChooser.showOpenDialog(stage);
    	 
    	 if(selectedFile != null)
    	 {
    		 comboBoxAttachments.getItems().add(selectedFile.getAbsolutePath());
    	 }
    }

    @FXML
    void buttonDeleteAttachmentClick(ActionEvent event) 
    {
    	int index = comboBoxAttachments.getSelectionModel().getSelectedIndex();
    	log.debug("the index to delete is" + index);
    	if(index >= 0)
    		comboBoxAttachments.getItems().remove(index);
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() 
    {
	    assert htmlEditor != null : "fx:id=\"htmlEditor\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	    assert textFiieldTo != null : "fx:id=\"textFiieldTo\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert textFieldBcc != null : "fx:id=\"textFieldBcc\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert textFieldCc != null : "fx:id=\"textFieldCc\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert textFieldSubject != null : "fx:id=\"textFieldSubject\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonToAdd != null : "fx:id=\"buttonToAdd\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonToDelete != null : "fx:id=\"buttonToDelete\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonBccAdd != null : "fx:id=\"buttonBccAdd\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonBccDelete != null : "fx:id=\"buttonBccDelete\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonCcAdd != null : "fx:id=\"buttonCcAdd\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonCcDelete != null : "fx:id=\"buttonCcDelete\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert comboBoxAttachments != null : "fx:id=\"comboBoxAttachments\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert comboBoxEmbededAttachments != null : "fx:id=\"comboBoxEmbededAttachments\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonAddAtachments != null : "fx:id=\"buttonAddAtachments\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonDeleteAttachment != null : "fx:id=\"buttonDeleteAttachment\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonAddEmbededAttachment != null : "fx:id=\"buttonAddEmbededAttachment\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonDeleteEmbededAtachment != null : "fx:id=\"buttonDeleteEmbededAtachment\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	  	assert buttonCreate != null : "fx:id=\"buttonCreate\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
	    assert buttonCancel != null : "fx:id=\"buttonCancel\" was not injected: check your FXML file 'MessageComposerUI.fxml'.";
        bindFields();
        
        
    }
    
    public void loadReplyMessage()
    {
    	log.debug("new message is" + newMessage.getFrom());
    	textFieldBcc.getItems().addAll(newMessage.getBccsProperty());
    	if(textFieldBcc.getItems().size() > 0)
    		textFieldBcc.getSelectionModel().select(0);
		textFieldCc.getItems().addAll(newMessage.getCcsProperty());
    	if(textFieldCc.getItems().size() > 0)
    		textFieldCc.getSelectionModel().select(0);
		textFiieldTo.getItems().add(newMessage.getFromProperty().get());    		
    	if(textFiieldTo.getItems().size() > 0)
    		textFiieldTo.getSelectionModel().select(0);

    		textFieldSubject.setText(newMessage.getSubject());
    	
    	log.debug("html text is" + newMessage.getHtmlMessageProperty().get());
    	log.debug("text is" + newMessage.getTextMessageProperty().get());
    	if(newMessage.getHtmlMessage().equals(""))
    	{
    		htmlEditor.setHtmlText(newMessage.getTextMessage() +"</br>-----------------------------------------------------------");
    	}
    	else
    		htmlEditor.setHtmlText(newMessage.getHtmlMessage() +"</br>-------------------------------------------------------------");
    	
    	saveAttachments(newMessage.getAttachmentsProperty());
    	ListProperty<EmailAttachment> e =  newMessage.getAttachmentsProperty();
    	for(EmailAttachment a: e)
    	{
    		comboBoxAttachments.getItems().add(a.getName());
    	}
    	if(comboBoxAttachments.getItems().size() > 0)
    		comboBoxAttachments.getSelectionModel().select(0);
    }
    
    public void saveAttachments(ListProperty<EmailAttachment> attach)
    {
    	for(EmailAttachment a : attach)
    	{
    		a.writeToFile(new File(a.getName()));
    	}
    }
    
    /**
     * ets the stage for the message composer.
     * @param stage
     */
    public void setStage(Stage stage)
    {
    	this.stage = stage;
    }
    
    public void setEmailDao(EmailDAO dao)
    {
    	this.emailDao = dao;
    }
    
    public void loadBeanInForm(MailBean bean)
    {
    	this.newMessage = bean;
    }
    
    public void setMailServerConfigBean(MailServerConfigurationBean mscb)
    {
    	this.mscb = mscb;
    }
}
