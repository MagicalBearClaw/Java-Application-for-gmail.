package JAG.ca.mcmahon.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Sample Skeleton for 'MainGmailUI.fxml' Controller Class
 */

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Filter;

import JAG.ca.mcmahon.beans.MailBean;
import JAG.ca.mcmahon.beans.MailServerConfigurationBean;
import JAG.ca.mcmahon.beans.MySqlConigurationBean;
import JAG.ca.mcmahon.data.Controllers.EmailDAO;
import JAG.ca.mcmahon.data.Controllers.MailController;
import JAG.ca.mcmahon.data.Controllers.MailServerIoManager;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import jodd.mail.EmailAttachment;
import jodd.mail.MailException;

public class FXMLGmailApplicationController 
{

	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="menuFike"
    private Menu menuFike; // Value injected by FXMLLoader

    @FXML // fx:id="menuItemNewMessage"
    private MenuItem menuItemNewMessage; // Value injected by FXMLLoader

    @FXML // fx:id="menuItemClose"
    private MenuItem menuItemClose; // Value injected by FXMLLoader

    @FXML // fx:id="menuEdit"
    private Menu menuEdit; // Value injected by FXMLLoader

    @FXML // fx:id="menuItemDelete"
    private MenuItem menuItemDelete; // Value injected by FXMLLoader

    @FXML // fx:id="menuItemConnectionProperties"
    private MenuItem menuItemConnectionProperties; // Value injected by FXMLLoader

    @FXML // fx:id="menuHelp"
    private Menu menuHelp; // Value injected by FXMLLoader

    @FXML // fx:id="menuItemAbout"
    private MenuItem menuItemAbout; // Value injected by FXMLLoader

    @FXML // fx:id="buttonConnectionProperties"
    private Button buttonConnectionProperties; // Value injected by FXMLLoader

    @FXML // fx:id="buttonNewMessage"
    private Button buttonNewMessage; // Value injected by FXMLLoader

    @FXML // fx:id="buttonDelete"
    private Button buttonDelete; // Value injected by FXMLLoader

    @FXML // fx:id="labelSearch"
    private Label labelSearch; // Value injected by FXMLLoader

    @FXML // fx:id="textFieldSearch"
    private TextField textFieldSearch; // Value injected by FXMLLoader

    @FXML // fx:id="combocBoxSearch"
    private ComboBox<String> combocBoxSearch; // Value injected by FXMLLoader

    @FXML // fx:id="comboBoxLanguage"
    private ComboBox<String> comboBoxLanguage; // Value injected by FXMLLoader

    @FXML // fx:id="treeViewFolders"
    private TreeView<String> treeViewFolders; // Value injected by FXMLLoader

    @FXML // fx:id="tableViewMessages"
    private TableView<MailBean> tableViewMessages; // Value injected by FXMLLoader

    @FXML // fx:id="tableColumnFrom"
    private TableColumn<MailBean, String> tableColumnFrom; // Value injected by FXMLLoader

    @FXML // fx:id="tableColumnSubject"
    private TableColumn<MailBean, String> tableColumnSubject; // Value injected by FXMLLoader

    @FXML // fx:id="tableColumnDate"
    private TableColumn<MailBean, String> tableColumnDate; // Value injected by FXMLLoader
    
    @FXML // fx:id="buttonForward"
    private Button buttonForward; // Value injected by FXMLLoader

    @FXML // fx:id="buttonReply"
    private Button buttonReply; // Value injected by FXMLLoader
    
    @FXML // fx:id="buttonNewFolder"
    private Button buttonNewFolder; // Value injected by FXMLLoader

    @FXML // fx:id="buttonDeleteFolder"
    private Button buttonDeleteFolder; // Value injected by FXMLLoader

    @FXML
    private ComboBox<String> comboBoxAttachments;
    
    @FXML
    private Button btnSave;
    
    @FXML
    private WebView webView;
    
    @FXML // fx:id="buttonSearch"
    private Button buttonSearch; // Value injected by FXMLLoader
    
    private ListProperty<MailBean> emails =  new SimpleListProperty<MailBean>(FXCollections.observableArrayList((new ArrayList<MailBean>())));
    
    private EmailDAO emailDao;
    
    private FXMLMailConnectionUIController properties;
    
    private FXMLMessageComposerController messageComposer;
    
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private Stage stage;
    private Stage connectionUiStage;
    private Stage messageComposerStage;

	private MailServerConfigurationBean imapConfig =  new MailServerConfigurationBean();

	private MailServerConfigurationBean smtpConfig =  new MailServerConfigurationBean();
	private String selectionMethod = "";
	private MySqlConigurationBean mysql =  new MySqlConigurationBean();
    private MailController mailController =  new MailController();
    /**
     * The event handler for the connection properties. 
     * It allows the opening of the connection properties
     * form.
     * @param event - the event data
     */
    @FXML
    void buttonConnectionPropertiesClick(ActionEvent event) 
    {
    	startConnectionProperties();
    }
    /**
	 * The event handler to delete a email
	 * from the table view
	 *
     * @param event - the event data
     */
    @FXML
    void buttonDeleteClick(ActionEvent event) 
    {
    	deleteMessage();
    }
    /**
     * deletes a message.
     */
    private void deleteMessage()
    {
    	TableSelectionModel<MailBean> selectedMail = tableViewMessages.getSelectionModel();
		try 
		{	
	    	if(selectedMail != null)
	    	{
	        	ObservableList<Integer> indicies =  selectedMail.getSelectedIndices();
	        	ObservableList<MailBean> mails = selectedMail.getSelectedItems();
	        	int size = mails.size();
	        	for(int i = 0; i < size; i++)
	        	{
					emailDao.deleteMail(mails.get(i).getMailId());
					emails.remove(indicies.get(i).intValue());
	
	        	}
	    	}
		}
		catch (SQLException se) 
		{
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText(se.getMessage());
			errorAlert.setHeaderText("Could not delete e-mail(s)");
			errorAlert.showAndWait();
		}
    }
    
    /**
     * The event handler for the connection properties. 
     * It allows the opening of the Message composer
     * form.
     * @param event - the event data
     */
    @FXML
    void buttonNewMessageClick(ActionEvent event) 
    {
    	startMessageComposer(null);
    }
    
    /**
     * Saves the attachment chosen by the user
     * to as user location.
     * @param event
     */
    @FXML
    void btnSaveClick(ActionEvent event) 
    {
    	if(comboBoxAttachments.getItems().size() > 0)
    	{
    		String fileName =  comboBoxAttachments.getSelectionModel().getSelectedItem();
			 FileChooser fileChooser = new FileChooser();
			 fileChooser.setTitle("Add a attachment");
			 fileChooser.getExtensionFilters().addAll(
			         new ExtensionFilter("Text Files", "*.txt"),
			 new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
			 new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
			 new ExtensionFilter("All Files", "*.*"));
			 File selectedFile = fileChooser.showSaveDialog(stage);
			 
	    	int selectedMsg = tableViewMessages.getSelectionModel().getFocusedIndex();
	    	log.debug("html bean index is" + selectedMsg);
	    	if(selectedMsg > 0)
	    	{
	        	MailBean bean = emails.get(selectedMsg);
	        	log.debug("the bean from is" + bean.getFrom());
	        	ListProperty<EmailAttachment> attach = bean.getAttachmentsProperty();
	        	for (EmailAttachment a : attach) 
	        	{
	        		if(a.getName() == fileName)
	        		{
	        			a.writeToFile(selectedFile);
	        		}
				}
	    	}
    	}
    }
    
    /**
	 * The event handler to delete a folder
	 * from the tree view
	 *
     * @param event - the event data
     */
    @FXML
    void buttonDeleteFolderClick(ActionEvent event) 
    {
    	TreeItem<String> selected = treeViewFolders.getSelectionModel().getSelectedItem();
    	String folderName =  selected.getValue();
    	log.debug("folder is " + folderName);
    	try 
    	{
        	if(!folderName.equals("Inbox") && !folderName.equals("Sent"))
    		{
    			log.debug("deleting");
    			MultipleSelectionModel<TreeItem<String>> model = treeViewFolders.getSelectionModel();
    			if(model != null)
    			{
        			treeViewFolders.getRoot().getChildren().remove(model.getSelectedIndex() -1);
        			emailDao.deleteFolder(folderName);
    			}
    		}
		} 
    	catch (Exception se) 
    	{
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText(se.getMessage());
			errorAlert.setHeaderText("Could not delete folder");
			errorAlert.showAndWait();
		}
    }
    
    /**
	 * The event handler to create a folder
	 * from the tree view
	 *
     * @param event - the event data
     */
    @FXML
    void buttonNewFolderClick(ActionEvent event) 
    {
    	TextInputDialog diag =  new TextInputDialog();
    	diag.setTitle("Create new folder");
    	diag.setHeaderText("Enter folder name(max 25 charecters)");
    	diag.setContentText("Folder name:");
    	Node n = diag.getDialogPane().lookupButton(ButtonType.OK);
    	n.setDisable(true);
    	diag.getEditor().textProperty().addListener((observable, oldVal, newVal) -> 
    	{
    		if(newVal.length() > 25)
    		{
    			diag.getEditor().setText(oldVal);
    		}
    		if(newVal.isEmpty())
    		{
    			n.setDisable(true);
    		}
    		else
    		{
    			n.setDisable(false);
    		}
    	});

    	Optional<String> result =  diag.showAndWait();
    	if(result.isPresent())
    	{
    		try 
    		{
				emailDao.createFolder(result.get());
	            TreeItem<String> item = new TreeItem<String> (result.get(), new ImageView(new Image(this.getClass().getResourceAsStream("/images/folder.png"))));     
				treeViewFolders.getRoot().getChildren().add(item);
			} catch (SQLException se) 
    		{
				Alert errorAlert =  new Alert(AlertType.ERROR);
				errorAlert.setContentText(se.getMessage());
				errorAlert.setHeaderText("Could not create folder");
				errorAlert.showAndWait();
			}
    	}
     }
    
    /**
     * Sets up the folder tree view listners.
     */
    private void setTreefolderChangeListner()
    {
    	
    	treeViewFolders.setOnDragOver((event) -> {
    		
            if (event.getGestureSource() != treeViewFolders &&
                    event.getDragboard().hasString()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            
            event.consume();
    		
    	});
    	
    	treeViewFolders.setOnDragEntered((event) -> {
 	         if (event.getGestureSource() != treeViewFolders &&
	                 event.getDragboard().hasString()) {
 	        	log.debug("over target");
	         }
	                
	         event.consume();
    	});
    	
    	treeViewFolders.setOnDragDropped((event) -> {
	        /* data dropped */
	        /* if there is a string data on dragboard, read it and use it */
	        Dragboard db = event.getDragboard();
	        boolean success = false;
	        if (db.hasString()) 
	        {
	 
	        	log.debug("dropped");

	        	Node foundNode = event.getPickResult().getIntersectedNode();
	        	if(foundNode != null)
	        	{	
	        		String name = "";
	        		if(foundNode instanceof Text)
	        		{
	        			name = ((Text)foundNode).getText();
	        		}
	        		else if(foundNode instanceof TreeCell)
	        		{
	        			name = ((TreeCell<String>)foundNode).getText();
	        		}

		        	int index =  Integer.valueOf(db.getString());
		        	log.debug("folder name is" + name);
	        		MailBean bean = emails.get(index);
		        	log.debug("findished drag");
		        	try {
						emailDao.updateMailFolder(bean.getMailId(), name);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	log.debug("findished drag");
	        	}
	              	
	           success = true;
	        }
	        /* let the source know whether the string was successfully 
	         * transferred and used */
	        event.setDropCompleted(success);
	        
	        event.consume();
    	});
    	
    	treeViewFolders.getSelectionModel().selectedItemProperty().addListener((observerable, oldVal, newVal) -> {
        
    		
    		TreeItem<String> current =  newVal;
    		try 
    		{
    			if(current.getValue().equals("Inbox"))
		    	{
    				getInboxMessages(current.getValue());
		    	}
    			else
    			{
    				log.debug("foldername is " + current.getValue());
    				getOtherFolderMessages(current.getValue());
    			}

			} 
    		catch (SQLException se) 
    		{
				Alert errorAlert =  new Alert(AlertType.ERROR);
				errorAlert.setContentText(se.getMessage());
				errorAlert.setHeaderText("Could not get email in this folder");
				errorAlert.showAndWait();
			}
    		
    	});
    }
    /**
     * since the inbox is unique we process
     * the other folders differently.
     * 
     * displays the messages with that folder inbox.
     * @param folderName
     * @throws SQLException
     */
    private void getInboxMessages(String folderNaem) throws SQLException
    {
		tableViewMessages.getItems().clear();
		emails.addAll(emailDao.getEmailsWithFolderName(folderNaem));
		addNewInboxMessages();
    }
    /**
     * since the inbox is unique we process
     * the other folders differently.
     * 
     * displays the messages with that folder id.
     * @param folderName
     * @throws SQLException
     */
    private void getOtherFolderMessages(String folderName) throws SQLException
    {
		tableViewMessages.getItems().clear();
		log.debug("view message for folder" + folderName);
		emails.addAll(emailDao.getEmailsWithFolderName(folderName));
		tableViewMessages.setItems(emails);	
    }
    
    /**
     * adds the emails to the databse.
     * @param beans
     * @throws SQLException
     */
    private void addAllEmailsInDatabase(ArrayList<MailBean> beans) throws SQLException
    {
    	for(MailBean current : beans)
    	{
    		emailDao.createMail(current);
    	}
    }
    
    /**
	 * The event handler for the menu item
	 * to open the about form.
	 *
     * @param event - the event data
     */
    @FXML
    void menuItemAboutClick(ActionEvent event) 
    {
    	log.debug("about was clicked");
    }
    
    /**
     * The event handler for the search 
     * button. allows to search a specific
     * criteria from the crieria combobox.
	 *
     * @param event - the event data
     */
    @FXML
    void buttonSearchClick(ActionEvent event) 
    {
    	int selection =  combocBoxSearch.getSelectionModel().getSelectedIndex();
    	log.debug("selection is:" + selection);
    	log.debug("search criteria is:" + textFieldSearch.getText());
    	if(textFieldSearch.getText().equals(""))
    	{
    		TreeItem<String> treeItem = treeViewFolders.getSelectionModel().getSelectedItem();
    		String selectedName = "Inbox";
    		
    		if(treeItem != null)
    			selectedName = treeItem.getValue();
 
    		log.debug("foldername is " + selectedName);
    		if(selectedName.equalsIgnoreCase("Inbox"))
    		{
    			try 
    			{
					getInboxMessages("inbox");
					
				} catch (SQLException e) 
    			{
					Alert errorAlert =  new Alert(AlertType.ERROR);
					errorAlert.setContentText("there was a proble retrieving the messages for the inbox folder.");
					errorAlert.setHeaderText("Database Connection error");
					errorAlert.showAndWait();
				}
    		}
    		else
    		{
    			try 
    			{
					getInboxMessages(selectedName);
					
				} catch (SQLException e) 
    			{
					Alert errorAlert =  new Alert(AlertType.ERROR);
					errorAlert.setContentText("there was a proble retrieving the messages for the inbox folder.");
					errorAlert.setHeaderText("Database Connection error");
					errorAlert.showAndWait();
				}
    		}
    	}
    	else
    	{
    		displayResultsOfSearch(selection);
    	}
    }
    /**
     * 
     * Displays the results of the search criteria
     * 
     * 
     * @param selection -the combo box selection criteria.
     */
    private void displayResultsOfSearch(int selection)
    {
		ObservableList<MailBean> beans = tableViewMessages.getItems();
		log.debug("email count in view" + beans.size());
		ArrayList<MailBean> foundBeans =  new ArrayList<>();
    	switch(selection)
    	{
    	case 0:
    	{
    		//from
    		for(MailBean bean : beans)
    		{
    			log.debug("actiual from:" + bean.getFromProperty().get());
    			if(bean.getFromProperty().get().equals(textFieldSearch.getText()))
    			{
    				log.debug("found a email");

					foundBeans.add(bean.getHardCopy());
    			}
    		}
    		addNewResultsToTable(foundBeans);
    	}
    	case 1:
    	{
    		// to
    		for(MailBean bean : beans)
    		{
    			log.debug("actiual from:" + bean.getFromProperty().get());
    			if(bean.getTosProperty().get().equals(textFieldSearch.getText()))
    			{
    				log.debug("found a email");

					foundBeans.add(bean.getHardCopy());
    			}
    		}
    		addNewResultsToTable(foundBeans);
    	}
    	case 2:
    	{
    		// bcc
    		for(MailBean bean : beans)
    		{
    			log.debug("actiual from:" + bean.getFromProperty().get());
    			if(bean.getBccsProperty().get().equals(textFieldSearch.getText()))
    			{
    				log.debug("found a email");

					foundBeans.add(bean.getHardCopy());
    			}
    		}
    		addNewResultsToTable(foundBeans);
    		
    	}
    	case 3:
    	{
    		//cc
    		for(MailBean bean : beans)
    		{
    			log.debug("actiual from:" + bean.getFromProperty().get());
    			if(bean.getCcsProperty().get().equals(textFieldSearch.getText()))
    			{
    				log.debug("found a email");

					foundBeans.add(bean.getHardCopy());
    			}
    		}
    		addNewResultsToTable(foundBeans);
    		
    	}
    	case 4:
    	{
    		// date
    		String dateString = textFieldSearch.getText().replaceAll("/", "-");
    		for(MailBean bean : beans)
    		{
    			log.debug("actiual from:" + bean.getFromProperty().get());
    			if(bean.getRecievedDate().toString().contains(dateString))
    			{
    				log.debug("found a email");

					foundBeans.add(bean.getHardCopy());
    			}
    		}
    		addNewResultsToTable(foundBeans);
    		
    	}
    	}
    }
    
    
    /**
     * Adds the new emails to the table.
     * @param foundBeans -  the new emails.
     */
    private void addNewResultsToTable(ArrayList<MailBean> foundBeans)
    {
		ObservableList<MailBean> beans = tableViewMessages.getItems();
		log.debug("email count in view" + beans.size());
		tableViewMessages.getItems().clear();
		emails.addAll(foundBeans);
		log.debug("emails count:" + emails.size());
		log.debug("new table couint: " + tableViewMessages.getItems().size());
    }
    
    /**
	 * The event handler for the menu item
	 * to close the application.
	 *
     * @param event - the event data
     */
    @FXML
    void menuItemCloseClick(ActionEvent event) 
    {
    	stage.close();
    }

    /**
	 * The event handler for the menu item
	 * to open the connection properties form.
	 *
     * @param event - the event data
     */
    @FXML
    void menuItemConnectionPropertiesClick(ActionEvent event) 
    {
    	startConnectionProperties();
    }
    /**
	 * The event handler for the reply button.
	 *
     * @param event - the event data
     */
    @FXML
    void buttonReplyClick(ActionEvent event) 
    {
    	int selectedMsg = tableViewMessages.getSelectionModel().getFocusedIndex();
    	log.debug("html bean index is" + selectedMsg);
    	if(selectedMsg > 0)
    	{
        	MailBean bean = emails.get(selectedMsg);
        	log.debug("the bean from is" + bean.getFrom());
        	startMessageComposer(bean);
    	}
    }
    /**
	 * The event handler for the forward button,
	 * opens the Message Composer form.
	 *
     * @param event - the event data
     */
    @FXML
    void buttonForwardClick(ActionEvent event) 
    {
    	int selectedMsg = tableViewMessages.getSelectionModel().getFocusedIndex();
    	log.debug("html bean index is" + selectedMsg);
    	if(selectedMsg > 0)
    	{
        	MailBean bean = emails.get(selectedMsg);
        	log.debug("the bean from is" + bean.getFrom());
        	startMessageComposer(bean);
    	}
    }
    /**
	 * The event handler for the menu item
	 * to delete a message
	 *
     * @param event - the event data
     */
    @FXML
    void menuItemDeleteClick(ActionEvent event) 
    {
    	deleteMessage();
    }
    /**
	 * The event handler for the menu item
	 * to create a new message.opens the
	 * message composer form.
	 *
     * @param event - the event data
     */
    @FXML
    void menuItemNewMessageClick(ActionEvent event) 
    {
    	
    	startMessageComposer(null);
    }

    /**
	 * The event handler for the language combo
	 * box. It allows the ability to change the
	 * applications language.
	 *
     * @param event - the event data
     */
    @FXML
    void comboBoxLanguageSelectionChanged(ActionEvent event) 
    {
    	String newValue =  comboBoxLanguage.getSelectionModel().getSelectedItem();
    	if(newValue.equalsIgnoreCase("English") || newValue.equalsIgnoreCase("Anglais"))
    	{
    		resources = ResourceBundle.getBundle("MessagesBundle_en_CA");
    	}
    	else if(newValue.equalsIgnoreCase("French") || newValue.equalsIgnoreCase("français"))
    	{
    		resources = ResourceBundle.getBundle("MessagesBundle_fr_CA");
    	}
    	changeSceneNodeText();
    }

    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() 
    {
        assert menuItemNewMessage != null : "fx:id=\"menuItemNewMessage\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert menuItemClose != null : "fx:id=\"menuItemClose\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert menuItemDelete != null : "fx:id=\"menuItemDelete\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert menuItemConnectionProperties != null : "fx:id=\"menuItemConnectionProperties\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert menuItemAbout != null : "fx:id=\"menuItemAbout\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert buttonConnectionProperties != null : "fx:id=\"buttonConnectionProperties\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert buttonNewMessage != null : "fx:id=\"buttonNewMessage\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert buttonDelete != null : "fx:id=\"buttonDelete\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert textFieldSearch != null : "fx:id=\"textFieldSearch\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert combocBoxSearch != null : "fx:id=\"combocBoxSearch\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert comboBoxLanguage != null : "fx:id=\"comboBoxLanguage\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert treeViewFolders != null : "fx:id=\"treeViewFolders\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
        assert tableViewMessages != null : "fx:id=\"tableViewMessages\" was not injected: check your FXML file 'MainGmailUI.fxml'.";
                
        comboBoxLanguage.getItems().clear();
        comboBoxLanguage.getItems().add(resources.getString("english"));
        comboBoxLanguage.getItems().add(resources.getString("french"));
        
        Locale local =  Locale.CANADA;
        
        comboBoxLanguage.setValue(local.getDisplayLanguage());        
        combocBoxSearch.getItems().clear();
        combocBoxSearch.getItems().add(resources.getString("from"));
        combocBoxSearch.getItems().add(resources.getString("to"));
        combocBoxSearch.getItems().add("Bcc");
        combocBoxSearch.getItems().add("Cc");
        combocBoxSearch.getItems().add(resources.getString("date"));
        combocBoxSearch.setValue(resources.getString("from"));
        tableViewMessages.setItems(emails);
    	tableColumnFrom.setCellValueFactory(cellData -> cellData.getValue().getFromProperty());
    	tableColumnSubject.setCellValueFactory(cellData -> cellData.getValue().getSubjectProperty());
    	tableColumnDate.setCellValueFactory(cellData -> cellData.getValue().getRecievedDateFormatedStringProperty());
    	selectionMethod = combocBoxSearch.getSelectionModel().getSelectedItem();
    	log.debug("starting cbx text id" + selectionMethod);
    	setTreefolderChangeListner();
    	        
    }
    /**
     * displayes the folders of the tree view
     * @param folders -  the names of the folders.
     */
    private void DisplayFolderTree(ArrayList<String> folders)
    {
    	
        TreeItem<String> rootItem = new TreeItem<String> ("Folders");
        rootItem.setExpanded(true);
        for (int i = 0; i < folders.size(); i++) 
        {
            TreeItem<String> item = new TreeItem<String> (folders.get(i), new ImageView(new Image(this.getClass().getResourceAsStream("/images/folder.png"))));
            rootItem.getChildren().add(item);
        }        
       treeViewFolders.setRoot(rootItem);
    }
        
    /**
     *  Sets the main stage.
     * @param stage - the stage.
     */
    public void setStage(Stage stage)
    {
    	this.stage = stage;
    }
    /**
     * Starts the connection properties form.
     */
    public void startConnectionProperties()
    {    	
    	
        FXMLLoader loader = new FXMLLoader();
        connectionUiStage =  new Stage();
        loader.setLocation(this.getClass().getResource("/fxml/MailConnectionUI.fxml"));
        // Localize the loader with its bundle
        // Uses the default locale and if a matching bundle is not found
        // will then use MessagesBundle.properties
		loader.setResources(resources);
        
        GridPane parent;
		try 
		{
			parent = (GridPane) loader.load();

			properties = (FXMLMailConnectionUIController)loader.getController();
			properties.setStage(connectionUiStage);
			loadPreviousConnectionProperties();
			properties.setImapServer(imapConfig);
			properties.setSmtpServer(smtpConfig);
			properties.setMysqlServer(mysql);
			properties.bindFields();
	        // Load the parent into a Scene
	        Scene scene = new Scene(parent);
	        // Put the Scene on Stage
	        connectionUiStage.setScene(scene);
	        connectionUiStage.setResizable(false);
	        connectionUiStage.setAlwaysOnTop(true);
	        connectionUiStage.initModality(Modality.WINDOW_MODAL);
	        connectionUiStage.initOwner(stage);
	        connectionUiStage.setTitle(resources.getString("connectionproperties"));
	        connectionUiStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/envelope.png")));
	        connectionUiStage.showAndWait();

        	emailDao =  new EmailDAO(mysql);
        	emailDao.InitializeDataBase();
	        DisplayFolderTree(emailDao.getFolders());
	        
		} 
		catch (IOException e) 
		{
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText("Could not open the connection properties.");
			errorAlert.setHeaderText("Error opening connection properties");
			errorAlert.showAndWait();
		}
        catch(SQLException se)
        {
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText(se.getMessage());
			errorAlert.setHeaderText("Database Connection error");
			errorAlert.showAndWait();
        }
    }
    /**
     * initializes the tablview messages listners
     * both for drag and drop and on clik row.
     */
    public void initializeTableViewwListners()
    {
    	tableViewMessages.setOnDragDetected((event) -> {
    		
    		Dragboard board = tableViewMessages.startDragAndDrop(TransferMode.ANY);
    		ClipboardContent clip =  new ClipboardContent();
    		clip.putString(tableViewMessages.getSelectionModel().getSelectedIndex()+ "");
    		board.setContent(clip);
    		event.consume();
    	});
    	
    		tableViewMessages.setOnDragDone((event) -> {
	        if (event.getTransferMode() == TransferMode.MOVE) 
	        {
	        	int index = tableViewMessages.getSelectionModel().getSelectedIndex();
	        	emails.remove(index);
	        	log.debug("draged to folder");
	        }
	        event.consume();
    	});
    	
    	
    	tableViewMessages.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> 
    	{
    		comboBoxAttachments.getItems().clear();
    		int indexSelected = tableViewMessages.getSelectionModel().getSelectedIndex();

    		log.debug("the index selected id" + indexSelected);
    		if(indexSelected > 0)
    		{
    			MailBean retrieved = emails.get(indexSelected);
    		String html = retrieved.getHtmlMessage();
    		
    			ListProperty<EmailAttachment> att = retrieved.getEmbedAttachmentProperty();
    			ListProperty<EmailAttachment> attachments = retrieved.getAttachmentsProperty();
    			log.debug("attachements size" + attachments.size());
    			for(EmailAttachment a : attachments)
    			{
    				comboBoxAttachments.getItems().add(a.getName());
    			}
    			log.debug("combobox size" + comboBoxAttachments.getItems().size());
 				String newHtml = html;
     			for(EmailAttachment a : att)
     			{
     				File f = new File(a.getName());	     				
     				a.writeToFile(f);

     				if(!html.equals("") && html != null)
     				{
     					newHtml = replcaeImageTagsCid(a.getContentId(), f, html);
     					log.debug("for loop new html:" + newHtml);
     				}
     			}
    		if(!html.equals("") && html != null)
    		{
    			File f =  new File("embeded.html");
    			try 
    			{
    				FileWriter writer = new FileWriter(f, false);
    				log.debug("the new html before writting is: " +  newHtml);
    				writer.write("<div>"+newHtml+"</div>");
    				writer.flush();
    				writer.close();
				} 
    			catch (IOException e) 
    			{
    				Alert errorAlert =  new Alert(AlertType.ERROR);
    				errorAlert.setContentText("Could not display the embeded attachment.");
    				errorAlert.setHeaderText("Error opening the embeded attachment");
    				errorAlert.showAndWait();
				}
    			webView.getEngine().load(f.toURI().toString());
    		}
    		else
    			webView.getEngine().loadContent(retrieved.getTextMessage(), "text");
    		log.debug("selection change end");
    	}
    	}); 
    }
    
    /**
     * 
     * parses a html string to replace the cid's with the file names.
     * 
     * @param cid - the cid of the image
     * @param file the- file beign created
     * @param html - the html source to aprse
     * @return the new html
     */
    private String replcaeImageTagsCid(String cid, File file, String html)
    {
    	String newCid = cid.substring(1, cid.length()-1);
    	log.debug("new cid: is " + newCid);
    	log.debug("old path is" + file.getAbsolutePath());
    	String newPath = file.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\");
    	log.debug("nw path is:" + newPath);
    	String newHtml = html.replaceFirst("cid:" + newCid, file.getName());
    	log.debug("url to picture" +  file.toURI().toString());
    	log.debug("new html:" + newHtml);
    	return newHtml;
    }
    
    /**
     * Starts the message composer form.
     */
    private void startMessageComposer(MailBean bean)
    {    	
        FXMLLoader loader = new FXMLLoader();
        messageComposerStage =  new Stage();
        loader.setLocation(this.getClass().getResource("/fxml/MessageComposerUI.fxml"));

        // Localize the loader with its bundle
        // Uses the default locale and if a matching bundle is not found
        // will then use MessagesBundle.properties
		loader.setResources(resources);
        
        GridPane parent;
		try 
		{
			parent = (GridPane) loader.load();

			
			messageComposer = (FXMLMessageComposerController)loader.getController();
			messageComposer.setStage(messageComposerStage);
			messageComposer.setMailServerConfigBean(smtpConfig);
	        // Load the parent into a Scene
	        Scene scene = new Scene(parent);
	        // Put the Scene on Stage
	        messageComposerStage.setScene(scene);
	        messageComposerStage.setResizable(false);
	        messageComposer.setEmailDao(emailDao);
	        messageComposerStage.setAlwaysOnTop(true);
	        messageComposerStage.initModality(Modality.WINDOW_MODAL);
	        messageComposerStage.initOwner(stage);
	        messageComposerStage.setTitle(resources.getString("messagecomposertitle"));
	        messageComposerStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/envelope.png")));
			if(bean != null)
			{
	        	log.debug("the bean from is" + bean.getFrom());
				messageComposer.loadBeanInForm(bean);
				messageComposer.loadReplyMessage();
			}
	        messageComposerStage.showAndWait();	        
		} 
		catch (IOException e) 
		{
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText("Could not open the message composer.");
			errorAlert.setHeaderText("Error opening  message composer");
			errorAlert.showAndWait();
		}
    }
    
    // changes the language of relevant nodes in the scene.
    private void changeSceneNodeText()
    {
		menuFike.setText(resources.getString("file")); 
		menuItemNewMessage.setText(resources.getString("newnessage")); 
		menuItemClose.setText(resources.getString("close"));
		menuEdit.setText(resources.getString("edit")); 
		menuItemDelete.setText(resources.getString("delete")); 
		menuItemConnectionProperties.setText(resources.getString("connectionproperties"));
		menuHelp.setText(resources.getString("help")); 
		menuItemAbout.setText(resources.getString("about"));
		buttonConnectionProperties.setText(resources.getString("connectionproperties"));
		buttonNewMessage.setText(resources.getString("newnessage")); 
		buttonDelete.setText(resources.getString("delete"));
		labelSearch.setText(resources.getString("search")); 
		tableColumnFrom.setText(resources.getString("from")); 
		tableColumnSubject.setText(resources.getString("subject")); 
		tableColumnDate.setText(resources.getString("date")); 
		
		comboBoxLanguage.getItems().set(0, resources.getString("english"));
		comboBoxLanguage.getItems().set(1, resources.getString("french"));
        combocBoxSearch.getItems().clear();
        combocBoxSearch.getItems().add(resources.getString("from"));
        combocBoxSearch.getItems().add(resources.getString("to"));
        combocBoxSearch.getItems().add("Bcc");
        combocBoxSearch.getItems().add("Cc");
        combocBoxSearch.getItems().add(resources.getString("date"));
        combocBoxSearch.getSelectionModel().clearAndSelect(0);  
    } 
    /**
     * loads the the properties from disk.
     * need to move this in the main ui. so we can make
     * it so that if there is no property files than we need to
     * popup the connection form.
     */
    public boolean loadPreviousConnectionProperties()
    {
    	MailServerIoManager ioManager =  new MailServerIoManager();
    	boolean isLoaded = false;
    	try 
    	{
			imapConfig =  ioManager.LoadMailConfigFromTextProperties("", "imap");
	    	smtpConfig =  ioManager.LoadMailConfigFromTextProperties("", "smtp");
	    	mysql = ioManager.LoadMysqlConfigFromTextProperties("", "mysql");
        	emailDao =  new EmailDAO(mysql);
	        DisplayFolderTree(emailDao.getFolders());
	        	        
	    	isLoaded = true;

		} 
    	catch (NumberFormatException nfe) 
    	{
    		log.debug("number format exception");
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText("cound not load the properties file because of a coversion error");
			errorAlert.setHeaderText("there was a problem reading the properties files");
			errorAlert.setResizable(false);
			errorAlert.show();
		}
    	catch(IOException e)
    	{
    		log.warn("ioexception in loadPreviousConnectionProperties ");

    	} 
    	catch (SQLException se) 
    	{
    		log.error("could not connect to database.");
		}
    	return isLoaded;
    }
    
    /**
     * Displays all the inbox messages received
     * by the databse and web.
     */
    public void diplayInboxMessages()
    {
        try 
        {
        	ArrayList<MailBean> newEmails = mailController.getMail(imapConfig);
        	if(newEmails != null)
        	{
            	addAllEmailsInDatabase(newEmails);
        	}
        	ArrayList<MailBean> previous = emailDao.getEmailsWithFolderName("Inbox");
        	if(previous != null)
        		emails.addAll(previous);
		} 
        catch (SQLException se) 
        {
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText(se.getMessage());
			errorAlert.setHeaderText("Database Connection error");
			errorAlert.showAndWait();
		}
    }
    /**
     * retrieves the new messages from the web.
     */
    private void addNewInboxMessages()
    {
        try 
        {
        	ArrayList<MailBean> newEmails = mailController.getMail(imapConfig);
        	if(newEmails != null)
        	{
            	emails.addAll(newEmails);
            	addAllEmailsInDatabase(newEmails);
        	}
		} 
        catch (SQLException se) 
        {
			Alert errorAlert =  new Alert(AlertType.ERROR);
			errorAlert.setContentText(se.getMessage());
			errorAlert.setHeaderText("Database Connection error");
			errorAlert.showAndWait();
		}
    }

}
