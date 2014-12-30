package org.irdresearch.irzimbabwe.client;

import java.util.Date;

import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.DateTimeUtil;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.model.Person;

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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InquiryComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static final String formName = "PATIENT";
    private static final String defaultImage = "images/no_image.gif";

    private Person current = new Person();
    private ClientProfilePopup popUpProfile;
    String filterSearch;

    private VerticalPanel mainVerticalPanel = new VerticalPanel();

    private FlexTable searchFlexTable = new FlexTable();

    private Button searchButton = new Button("Search");
    private Label lblAge = new Label("Age:");

    private TextBox patientNameOrIdTextBox = new TextBox();
    private IntegerBox ageIntegerBox = new IntegerBox();

    private Image patientPictureImage;

    private ListBox patientsListBox = new ListBox();
    private final ListBox searchComboBox = new ListBox();
    private final ListBox pendingComboBox = new ListBox();

    public InquiryComposite()
    {
	filterSearch = "";
	initWidget(mainVerticalPanel);
	mainVerticalPanel.setSize("30%", "50%");
	mainVerticalPanel.add(searchFlexTable);
	searchFlexTable.setSize("255px", "71px");

	searchFlexTable.setWidget(0, 0, searchComboBox);
	searchComboBox.setName("SEARCH");
	searchComboBox.addItem("ID");
	searchComboBox.addItem("NAME");
	searchComboBox.addItem("MOBILE");
	searchComboBox.addItem("NATIONAL ID");

	searchFlexTable.setWidget(0, 1, patientNameOrIdTextBox);
	searchFlexTable.setWidget(1, 0, lblAge);
	searchFlexTable.setWidget(1, 1, ageIntegerBox);

	patientNameOrIdTextBox.setVisibleLength(11);
	patientNameOrIdTextBox.setMaxLength(11);
	patientNameOrIdTextBox.setWidth("100%");

	ageIntegerBox.setMaxLength(3);
	ageIntegerBox.setVisibleLength(3);

	mainVerticalPanel.add(searchButton);
	searchButton.setWidth("100%");
	pendingComboBox.addItem("Waiting for registration");
	pendingComboBox.addItem("Pending sputum sample results");
	pendingComboBox.addItem("Pending referrals");

	mainVerticalPanel.add(pendingComboBox);
	pendingComboBox.setWidth("256px");
	mainVerticalPanel.add(patientsListBox);
	patientsListBox.setSize("255px", "101px");
	patientsListBox.setVisibleItemCount(5);
	patientsListBox.addClickHandler(this);

	patientPictureImage = new Image(defaultImage);
	patientPictureImage.setVisible(false);

	mainVerticalPanel.add(patientPictureImage);
	mainVerticalPanel.setCellHorizontalAlignment(patientPictureImage, HasHorizontalAlignment.ALIGN_CENTER);
	mainVerticalPanel.setCellVerticalAlignment(patientPictureImage, HasVerticalAlignment.ALIGN_MIDDLE);
	patientPictureImage.setSize("150px", "150px");

	patientNameOrIdTextBox.addClickHandler(this);
	searchButton.addClickHandler(this);
	searchComboBox.addClickHandler(this);
	searchComboBox.addChangeHandler(this);

	pendingComboBox.addChangeHandler(this);
	//pendingComboBox.addClickHandler(this);
	setRights(formName);
    }

    public void clearUp()
    {
	patientsListBox.clear();
	patientPictureImage.setUrl(defaultImage);
    }

    public boolean validate()
    {
	boolean isValid = true;
	/*
	 * the corresponding list item should match the pattern of input in text
	 * box of inquiry validate id starts with P and upto 10 chars must be
	 * numeric,mobile all 10 chars are numbers
	 */
	if (IRZClient.get(patientNameOrIdTextBox).equals(""))
	{ // the patientIdNameorMobile text box should not be empty
	    isValid = false;
	    Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
	}
	else if ((IRZClient.get(searchComboBox)).equals("ID") 
	&& !(IRZClient.get(patientNameOrIdTextBox).matches("^[A-Za-z](\\d{10})")))    
	{
	    isValid = false;
	    Window.alert(CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "Client id starts with P or G example:P1131000101,G1234567899");

	}
	else if (IRZClient.get(searchComboBox).equals("NATIONAL ID") && !IRZClient.get(patientNameOrIdTextBox).matches("^(\\d{2})-(\\d{6,7})-([a-zA-Z]{1})-(\\d{2})"))
	{
	    isValid = false;
	    Window.alert(CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "Use format 12-123456-A-12 or 12-1234567-Z-12");

	}
	else if ((IRZClient.get(searchComboBox)).equals("MOBILE") && !IRZClient.get(patientNameOrIdTextBox).matches("[0-9]{10,10}"))
	{
	    isValid = false;
	    Window.alert(CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "Mobile must be 10 digits !");

	}
	return isValid;
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

    public void saveData()
    {
	// Not implemented
    }

    public void updateData()
    {
	// Not implemented
    }

    public void deleteData()
    {
	// Not implemented
    }

    public void fillData()
    {
	setPopUpProfile(new ClientProfilePopup(current));
	
    }

    public void setCurrent()
    {
	// Not implemented
    }

    public void setRights(String menuName)
    {
	searchButton.setEnabled(true);
    }

    public void onChange(ChangeEvent event)
    {
	Widget sender = (Widget) event.getSource();

	if (sender == searchComboBox)
	{
	    String selectedIndex = searchComboBox.getValue(searchComboBox.getSelectedIndex());
	    if (selectedIndex.equals("ID"))
	    {
		patientNameOrIdTextBox.setMaxLength(11);
		patientNameOrIdTextBox.setVisibleLength(11);
		patientNameOrIdTextBox.setText("");
	    }
	    else if (selectedIndex.equals("NAME"))
	    {
		patientNameOrIdTextBox.setMaxLength(80);
		patientNameOrIdTextBox.setVisibleLength(80);
		patientNameOrIdTextBox.setText("");
	    }
	    else if (selectedIndex.equals("MOBILE"))
	    {
		patientNameOrIdTextBox.setMaxLength(10);
		patientNameOrIdTextBox.setVisibleLength(10);
		patientNameOrIdTextBox.setText("");
	    }
	    else if (selectedIndex.equals("NATIONAL ID"))
	    {
		patientNameOrIdTextBox.setMaxLength(15);
		patientNameOrIdTextBox.setVisibleLength(15);
		patientNameOrIdTextBox.setText("");
	    }

	}
	else if (sender == pendingComboBox)
	{
	    clearUp();
	    String selectedIndex = IRZClient.get(pendingComboBox);
	    String tableName = null;
	    String colName = null;
	    /*
	     * Waiting list filters
	     * might be added to concept in the definition, definition type and default
	     * */
	    if (selectedIndex.equals("Waiting for registration"))// waiting for registration
	    {
		tableName = "encounter";
		colName = "pid1";
		String formatDate = DateTimeUtil.getFormattedDate(new Date(), DateTimeUtil.SQL_DATE);
		this.filterSearch = "where encounter_type = 'DEMOG' and date_entered='" + formatDate + "' and pid1 not in (select pid1 from encounter where encounter_type='TB_REG') ";
	    }
	    else if (selectedIndex.equals("Pending sputum sample results"))// sputum test results pending
	    {
		tableName = "sputum_test";
		colName = "patient_id";
		this.filterSearch = "where sample_code is not null and smear_result is null;";
	    }
	    else if (selectedIndex.equals("Pending referrals"))// tests done referrals pending
	    {
		tableName = "sputum_test";
		colName = "patient_id";
		this.filterSearch = "where smear_result is not null and patient_id not in (select pid1 from encounter where encounter_type='REFERRAL');";
	    }

	    try
	    {
		service.getColumnData(tableName, colName, filterSearch, new AsyncCallback<String[]>() {
		    public void onSuccess(String[] result)
		    {
			clearUp();
			load(false);
			for (String s : result)
			    patientsListBox.addItem(s);
			if (patientsListBox.getItemCount() == 0)
			    Window.alert(CustomMessage.getErrorMessage(ErrorType.ITEM_NOT_FOUND) + "No patients found with " + IRZClient.get(pendingComboBox));
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

    }

    public void onClick(ClickEvent event)
    {
	Widget sender = (Widget) event.getSource();
	if (sender == patientNameOrIdTextBox)
	{
	    if (IRZClient.get(patientNameOrIdTextBox).equals(""))
		clearUp();
	}
	else if (sender == searchButton)
	{

	    String searchField = IRZClient.get(patientNameOrIdTextBox);
	    load(true);
	    if (searchField.length() < 3)
	    {
		Window.alert("Please first enter at-least 3-character value as search criteria in ID/Name box or mobile number.");
		load(false);
		return;
	    }
	    if (validate())
	    {
		String selectedIndex = IRZClient.get(searchComboBox);
		if (selectedIndex.equals("ID"))
		{
		    this.filterSearch = "pid='" + searchField.toUpperCase() + "'";
		}
		else if (selectedIndex.equals("NAME"))
		{
		    this.filterSearch = "concat(first_name,' ',last_name) like '%" + searchField.toUpperCase() + "%'";
		}
		else if (selectedIndex.equals("MOBILE"))
		{
		    this.filterSearch = "mobile='" + searchField + "'";
		}
		else if (selectedIndex.equals("NATIONAL ID"))
		{
		    this.filterSearch = "national_id='" + searchField.toUpperCase() + "'";
		}
		try
		{
		    String additionalCondition = "";
		    if (!ageIntegerBox.getText().equals(""))
		    {
			additionalCondition = " and approximate_age = " + ageIntegerBox.getText();
		    }
		    service.getColumnData("person", "pid", filterSearch + additionalCondition, new AsyncCallback<String[]>() {
			public void onSuccess(String[] result)
			{
			    clearUp();
			    load(false);
			    for (String s : result)
				patientsListBox.addItem(s);
			    if (patientsListBox.getItemCount() == 0)
				Window.alert(CustomMessage.getErrorMessage(ErrorType.ITEM_NOT_FOUND));

			}

			public void onFailure(Throwable caught)
			{
			    load(false);
			    caught.printStackTrace();
			}
		    });
		} catch(Exception e)
		{
		    e.printStackTrace();
		    load(false);
		}
	    }
	    load(false);
	    clearUp();
	}
	else if (sender == patientsListBox)
	{
	    try
	    {
		service.findPerson(IRZClient.get(patientsListBox), new AsyncCallback<Person>() {
		    public void onSuccess(Person result)
		    {
			if (current != null)
			{
			    current = result;
			    fillData();			    
			}
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
    }

    public ClientProfilePopup getPopUpProfile()
    {
	return popUpProfile;
    }

    public void setPopUpProfile(ClientProfilePopup popUpProfile)
    {
	this.popUpProfile = popUpProfile;
    }
}
