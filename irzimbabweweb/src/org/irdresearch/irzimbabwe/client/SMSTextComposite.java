
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.SmsRule;
import org.irdresearch.irzimbabwe.shared.model.SmsText;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * TB Screening form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class SMSTextComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static final String menuName = "SETUP";

    private UserRightsUtil rights = new UserRightsUtil();
    private boolean valid;
    private SmsText current;

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private Grid grid = new Grid(1, 4);

    private ToggleButton createButton = new ToggleButton("Create");
    private Button saveButton = new Button("Save");
    private Button deleteButton = new Button("Delete");
    private Button closeButton = new Button("Close");

    private Label lblType = new Label("Type:");
    private Label lblSmsTextSetup = new Label("SMS Text Setup");
    private Label lblMessage = new Label("Message:");

    private RichTextArea smsTextRichTextBox = new RichTextArea();

    private ListBox smsNoListBox = new ListBox();
    private ListBox smsTypeComboBox = new ListBox();
    private final Label lblLanguage = new Label("Language:");
    private final ListBox langcomboBox = new ListBox();
    private final Label lblTextId = new Label("Text ID:");
    private final Label lblReferralMessage = new Label("Referral Message:");

    public SMSTextComposite()
    {
	initWidget(flexTable);
	flexTable.setSize("80%", "100%");
	flexTable.setWidget(0, 0, topFlexTable);
	lblSmsTextSetup.setWordWrap(false);
	lblSmsTextSetup.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblSmsTextSetup);
	lblType.setWordWrap(false);
	flexTable.setWidget(1, 0, rightFlexTable);

	rightFlexTable.setWidget(0, 0, lblReferralMessage);
	lblReferralMessage.setSize("100%", "30%");
	rightFlexTable.setWidget(0, 1, smsTypeComboBox);

	smsTypeComboBox.addChangeHandler(this);

	rightFlexTable.setWidget(1, 0, lblTextId);
	lblTextId.setSize("80%", "30%");
	rightFlexTable.setWidget(1, 1, smsNoListBox);
	smsNoListBox.setEnabled(false);
	smsNoListBox.addChangeHandler(this);
	rightFlexTable.setWidget(2, 0, lblLanguage);
	lblLanguage.setSize("80%", "30%");
	lblLanguage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

	langcomboBox.setName("PREFERRED_LANGUAGE");
	langcomboBox.setEnabled(false);
	rightFlexTable.setWidget(2, 1, langcomboBox);
	rightFlexTable.setWidget(3, 0, lblMessage);
	lblMessage.setWidth("106px");
	rightFlexTable.setWidget(3, 1, smsTextRichTextBox);
	rightFlexTable.getRowFormatter().setVerticalAlign(3, HasVerticalAlignment.ALIGN_TOP);
	rightFlexTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_LEFT);
	rightFlexTable.setWidget(4, 1, grid);
	grid.setSize("100%", "100%");
	grid.setWidget(0, 0, createButton);
	createButton.setEnabled(false);
	createButton.addClickHandler(this);
	saveButton.setEnabled(false);
	grid.setWidget(0, 1, saveButton);
	deleteButton.setEnabled(false);
	grid.setWidget(0, 2, deleteButton);
	grid.setWidget(0, 3, closeButton);
	saveButton.addClickHandler(this);
	deleteButton.addClickHandler(this);
	closeButton.addClickHandler(this);
	flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);

	IRZClient.refresh(flexTable);
	try
	{
	    service.findSmsRules(new AsyncCallback<SmsRule[]>() {
		public void onSuccess(SmsRule[] result)
		{
		    smsTypeComboBox.clear();
		    if (result != null)
		    {
			for (SmsRule rule : result)
			    smsTypeComboBox.addItem(rule.getRuleName(), rule.getRuleId());
			setRights(menuName);
		    }
		    else
			Window.alert("Pre-configured SMS tyeps cannot be fetched. Please contact your Administrator.");
		}

		public void onFailure(Throwable caught)
		{
		    caught.printStackTrace();
		}
	    });
	} catch(Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     * Display/Hide main panel and loading widget
     * 
     * @param status
     */
    public void load(boolean status)
    {
	if (status)
	    MainMenuComposite.showLoading();
	else
	    MainMenuComposite.hideLoading();
    }

    public void setCurrent()
    {
	current.setText(smsTextRichTextBox.getText());
	// String
	// defaultKey=IRZ.getDefinitionKey("LANGUAGE",langcomboBox.getValue(langcomboBox));
	// when explicitly values were not set
	current.setLanguageId(langcomboBox.getValue(langcomboBox.getSelectedIndex()));
	// Window.alert(current.getLanguageId());

    }

    public void fillData()
    {
	try
	{
	    service.findSmsText(IRZClient.get(smsTypeComboBox), IRZClient.get(smsNoListBox), new AsyncCallback<SmsText>() {
		public void onSuccess(SmsText result)
		{
		    current = result;
		    smsTextRichTextBox.setText(current.getText());
		    int requiredIndex = -1;
		    for (int i = 0; i < langcomboBox.getItemCount(); i++)
		    {
			if (langcomboBox.getValue(i).equalsIgnoreCase(current.getLanguageId()))
			{
			    requiredIndex = i;
			    break;
			}
		    }
		    langcomboBox.setSelectedIndex(requiredIndex);
		    // Window.alert("Required index:"+requiredIndex+"  "+langcomboBox.getItemText(requiredIndex)+"  "+current.getLanguageId());
		    load(false);
		}

		public void onFailure(Throwable caught)
		{
		    load(false);
		}
	    });

	} catch(Exception e)
	{
	    e.printStackTrace();
	    load(false);
	}
    }

    public void clearUp()
    {
	IRZClient.clearControls(rightFlexTable);
	smsNoListBox.clear();
    }

    public boolean validate()
    {
	final StringBuilder errorMessage = new StringBuilder();
	valid = true;
	if (!valid)
	{
	    Window.alert(errorMessage.toString());
	    load(false);
	}
	return valid;
    }

    public void saveData()
    {
	if (validate())
	{
	    current = new SmsText();
	    current.setRuleId(IRZClient.get(smsTypeComboBox));
	    setCurrent();
	    try
	    {
		service.saveSmsText(current, new AsyncCallback<Boolean>() {
		    public void onSuccess(Boolean result)
		    {
			if (result)
			{
			    Window.alert(CustomMessage.getInfoMessage(InfoType.INSERTED));
			    clearUp();
			}
			else
			{
			    Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR));
			}
			load(false);
		    }

		    public void onFailure(Throwable caught)
		    {
			Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR));
			load(false);
		    }
		});
	    } catch(Exception e)
	    {
		e.printStackTrace();
	    }
	    load(false);
	}
    }

    public void updateData()
    {
	if (validate())
	{
	    setCurrent();
	    try
	    {
		service.updateSmsText(current, new AsyncCallback<Boolean>() {
		    public void onSuccess(Boolean result)
		    {
			if (result)
			    Window.alert(CustomMessage.getInfoMessage(InfoType.UPDATED));
			else
			    Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
			load(false);
		    }

		    public void onFailure(Throwable caught)
		    {
			Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
			load(false);
		    }
		});
	    } catch(Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public void deleteData()
    {
	if (validate())
	{
	    try
	    {
		service.deleteSmsText(current, new AsyncCallback<Boolean>() {
		    public void onSuccess(Boolean result)
		    {
			if (result)
			{
			    Window.alert(CustomMessage.getInfoMessage(InfoType.DELETED));
			    clearUp();
			}
			else
			    Window.alert(CustomMessage.getErrorMessage(ErrorType.DELETE_ERROR));
			load(false);
		    }

		    public void onFailure(Throwable caught)
		    {
			caught.printStackTrace();
			load(false);
		    }
		});
	    } catch(Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public void setRights(String menuName)
    {
	load(true);
	try
	{
	    service.getUserRgihts(IRZ.getCurrentUserName(), IRZ.getCurrentRole(), menuName, new AsyncCallback<Boolean[]>() {
		public void onSuccess(Boolean[] result)
		{
		    final Boolean[] userRights = result;
		    rights.setRoleRights(IRZ.getCurrentRole(), userRights);
		    boolean hasAccess = rights.getAccess(AccessType.INSERT) | rights.getAccess(AccessType.UPDATE) | rights.getAccess(AccessType.DELETE) | rights.getAccess(AccessType.SELECT);
		    if (!hasAccess)
		    {
			Window.alert(CustomMessage.getErrorMessage(ErrorType.DATA_ACCESS_ERROR));
			MainMenuComposite.clear();
		    }
		    smsNoListBox.setEnabled(rights.getAccess(AccessType.SELECT));
		    createButton.setEnabled(rights.getAccess(AccessType.INSERT));
		    saveButton.setEnabled(rights.getAccess(AccessType.UPDATE));
		    deleteButton.setEnabled(rights.getAccess(AccessType.DELETE));
		    load(false);
		}

		public void onFailure(Throwable caught)
		{
		    load(false);
		}
	    });
	} catch(Exception e)
	{
	    e.printStackTrace();
	}
    }

    public void onClick(ClickEvent event)
    {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == createButton)
	{
	    smsTextRichTextBox.setVisible(true);
	    smsTextRichTextBox.setText("");
	    smsTypeComboBox.setEnabled(true);
	    smsNoListBox.clear();
	    langcomboBox.setEnabled(true);
	    // Window.alert(langcomboBox.getItemText(langcomboBox.getSelectedIndex())+";"+langcomboBox.getValue(langcomboBox.getSelectedIndex()));
	    load(false);
	}
	else if (sender == saveButton)
	{
	    if (createButton.isDown())
	    {
		saveData();
		createButton.setDown(false);
		smsTextRichTextBox.setVisible(false);

	    }
	    else
	    {
		smsTextRichTextBox.setVisible(true);
		updateData();
	    }

	}
	else if (sender == deleteButton)
	{
	    smsTextRichTextBox.setVisible(true);
	    deleteData();
	}
	else if (sender == closeButton)
	{
	    if (menuName.equals("DEFINITION"))
		Window.alert("Please refresh your browser if you have made any changes to this form.");
	    MainMenuComposite.clear();
	}
    }

    public void onChange(ChangeEvent event)
    {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == smsTypeComboBox)
	{
	    smsTextRichTextBox.setText("");
	    langcomboBox.setEnabled(isVisible());
	    smsTextRichTextBox.setVisible(true);
	    service.findSmsTextByRule(IRZClient.get(smsTypeComboBox), new AsyncCallback<SmsText[]>() {
		public void onSuccess(SmsText[] result)
		{
		    smsNoListBox.clear();
		    if (result != null)
		    {
			for (SmsText text : result)
			{
			    smsNoListBox.addItem(String.valueOf(text.getTextId()));
			}

			fillData();
			load(false);
		    }
		}

		public void onFailure(Throwable caught)
		{
		    caught.printStackTrace();
		    load(false);
		}
	    });
	}
	else if (sender == smsNoListBox)
	{
	    fillData();
	}
    }
}
