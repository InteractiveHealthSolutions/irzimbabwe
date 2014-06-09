
package org.irdresearch.irzimbabwe.client;

import java.util.ArrayList;
import java.util.Date;
import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.Encounter;
import org.irdresearch.irzimbabwe.shared.model.EncounterId;
import org.irdresearch.irzimbabwe.shared.model.EncounterResults;
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
import org.irdresearch.irzimbabwe.shared.model.Patient;
import org.irdresearch.irzimbabwe.shared.model.Person;
import org.irdresearch.irzimbabwe.shared.model.User;
import org.irdresearch.irzimbabwe.shared.model.Visit;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedStackPanel;

/**
 * TB/MC Registration form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class RegistrationComposite extends Composite implements ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			formName						= "TB_REG";

	private UserRightsUtil				rights							= new UserRightsUtil ();
	private Patient						currentPatient;
	private Person						currentPerson;
	private boolean						valid;

	private FlexTable					flexTable						= new FlexTable ();
	private FlexTable					topFlexTable					= new FlexTable ();
	private FlexTable					clientIdFlexTable				= new FlexTable ();
	private FlexTable					addressFlexTable				= new FlexTable ();
	private FlexTable					contactFlexTable				= new FlexTable ();
	private FlexTable					suspicionFlexTable				= new FlexTable ();
	private FlexTable					otherFlexTable					= new FlexTable ();

	private Grid						grid							= new Grid (1, 2);
	private VerticalPanel				middleVerticalPanel				= new VerticalPanel ();
	private DecoratedStackPanel			decoratedStackPanel				= new DecoratedStackPanel ();

	private Button						checkIdButton					= new Button ("Check");
	private Button						saveButton						= new Button ("Save");
	private Button						closeButton						= new Button ("Close");

	private Label						lblClientsInitialDemographics	= new Label ("Client Registration Form");
	private Label						lblClientsId					= new Label ("Client's ID:");
	private Label						lblSuspectType					= new Label ("Suspect Type:");
	private Label						lblSpecifyOther					= new Label ("Specify Other:");
	private Label						lblHivStatus					= new Label ("Client's HIV Status:");
	private Label						lblStreetNumber					= new Label ("Street Number:");
	private Label						lblStreetName					= new Label ("Street Name:");
	private Label						lblArea							= new Label ("Area:");
	private Label						lblAreaCode						= new Label ("Area Code:");
	private Label						lblEnterAddress					= new Label ("Has Client provided his Address?");
	private Label						lblCity							= new Label ("City:");
	private Label						lblOtherCity					= new Label ("Other City:");
	private Label						lblHasTheClient					= new Label ("Has the Client sent SMSs before?");
	private Label						lblHasTheClient_1				= new Label ("Has the Client received SMSs before?");
	private Label						lblDoesTheClient				= new Label ("Does the Client have Access to a Cell Phone?");
	private Label						lblNumber						= new Label ("Number:");
	private Label						lblOwnedBy						= new Label ("Owned by:");
	private Label						lblIsAHousehold					= new Label ("Is a Household Member:");
	private Label						label							= new Label ("Number:");
	private Label						label_1							= new Label ("Owned by:");
	private Label						label_2							= new Label ("Is a Household Member:");
	private Label						label_3							= new Label ("Number:");
	private Label						label_4							= new Label ("Owned by:");
	private Label						label_5							= new Label ("Is a Household Member:");
	private Label						lblOccupation					= new Label ("Client's Occupation:");
	private Label						lblSpecifyOther_1				= new Label ("Specify Other:");
	private Label						lblSpecifyOther_2				= new Label ("Specify Other:");
	private Label						lblClientsNationalIdentity		= new Label ("Client's National Identity Number:");
	private Label						lblClientsLevelOf				= new Label ("Client's level of Education");

	private TextBox						clientIdTextBox					= new TextBox ();
	private TextBox						streetNoTextBox					= new TextBox ();
	private TextBox						streetNameTextBox				= new TextBox ();
	private TextBox						areaTextBox						= new TextBox ();
	private TextBox						areaCodeTextBox					= new TextBox ();
	private TextBox						otherCityTextBox				= new TextBox ();
	private TextBox						primaryCellPhoneTextBox			= new TextBox ();
	private TextBox						secondaryCellPhoneTextBox		= new TextBox ();
	private TextBox						tertiaryCellPhoneTextBox		= new TextBox ();
	private TextBox						landlineNumberTextBox			= new TextBox ();
	private TextBox						otherSuspectTypeTextBox			= new TextBox ();
	private TextBox						otherOccupationTextBox			= new TextBox ();
	private TextBox						otherEducationLevelTextBox		= new TextBox ();
	private TextBox						nicTextBox						= new TextBox ();

	private CheckBox					primaryCellPhoneCheckBox		= new CheckBox ("Primary Cell Phone Number:");
	private CheckBox					secondaryCellPhoneCheckBox		= new CheckBox ("Secondary Cell Phone Number:");
	private CheckBox					tertiaryCellPhoneCheckBox		= new CheckBox ("Tertiary Cell Phone Number:");
	private CheckBox					landlineNumberCheckBox			= new CheckBox ("Landline Number:");
	private CheckBox					reachedViaSmsCheckBox			= new CheckBox ("Client can be reached via SMS");
	private CheckBox					reachedViaLandlineCheckBox		= new CheckBox ("Client can be reached via Landline");
	private CheckBox					hasWorkplaceCheckBox			= new CheckBox ("Client has workplace");
	private CheckBox					mobileVulnerableAreaCheckBox	= new CheckBox ("Client is part of Mobile vulnerable area");

	private ListBox						patientTypeComboBox				= new ListBox ();
	private ListBox						hivStatusComboBox				= new ListBox ();
	private ListBox						addressProvidedComboBox			= new ListBox ();
	private ListBox						cityComboBox					= new ListBox ();
	private ListBox						sentSmsBeforeComboBox			= new ListBox ();
	private ListBox						receivedSmsBeforeComboBox		= new ListBox ();
	private ListBox						hasCellPhoneComboBox			= new ListBox ();
	private ListBox						primaryCellPhoneOwnerComboBox	= new ListBox ();
	private ListBox						primaryOwnerHouseholdComboBox	= new ListBox ();
	private ListBox						secondaryCellPhoneOwnerComboBox	= new ListBox ();
	private ListBox						secondaryOwnerHouseholdComboBox	= new ListBox ();
	private ListBox						tertiaryCellPhoneOwnerComboBox	= new ListBox ();
	private ListBox						tertiaryOwnerHouseholdComboBox	= new ListBox ();
	private ListBox						occupationComboBox				= new ListBox ();
	private ListBox						educationLevelComboBox			= new ListBox ();

	public RegistrationComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		lblClientsInitialDemographics.setWordWrap (false);
		lblClientsInitialDemographics.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblClientsInitialDemographics);
		topFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget (1, 0, middleVerticalPanel);
		middleVerticalPanel.add (clientIdFlexTable);
		clientIdFlexTable.setWidth ("100%");
		clientIdFlexTable.setWidget (0, 0, lblClientsId);
		lblClientsId.setWordWrap (false);
		clientIdTextBox.setVisibleLength (12);
		clientIdTextBox.setMaxLength (12);
		clientIdFlexTable.setWidget (0, 1, clientIdTextBox);
		clientIdTextBox.setName ("patient;patient_id");
		clientIdFlexTable.setWidget (0, 2, checkIdButton);
		checkIdButton.setWidth ("100%");
		middleVerticalPanel.add (decoratedStackPanel);
		decoratedStackPanel.setWidth ("100%");
		decoratedStackPanel.add (suspicionFlexTable, "Suspicion Details", false);
		suspicionFlexTable.setSize ("100%", "100%");
		lblSuspectType.setWordWrap (false);
		suspicionFlexTable.setWidget (0, 0, lblSuspectType);
		patientTypeComboBox.setName ("PATIENT_TYPE");
		suspicionFlexTable.setWidget (0, 1, patientTypeComboBox);
		lblSpecifyOther.setWordWrap (false);
		suspicionFlexTable.setWidget (1, 0, lblSpecifyOther);
		otherSuspectTypeTextBox.setMaxLength (50);
		otherSuspectTypeTextBox.setEnabled (false);
		suspicionFlexTable.setWidget (1, 1, otherSuspectTypeTextBox);
		lblHivStatus.setWordWrap (false);
		suspicionFlexTable.setWidget (2, 0, lblHivStatus);
		hivStatusComboBox.setName ("HIV_STATUS");
		suspicionFlexTable.setWidget (2, 1, hivStatusComboBox);
		decoratedStackPanel.add (addressFlexTable, "Address Details", false);
		addressFlexTable.setSize ("100%", "100%");
		lblEnterAddress.setWordWrap (false);
		addressFlexTable.setWidget (0, 0, lblEnterAddress);
		addressProvidedComboBox.addItem ("NO");
		addressProvidedComboBox.addItem ("YES");
		addressProvidedComboBox.addItem ("REFUSED");
		addressFlexTable.setWidget (0, 1, addressProvidedComboBox);
		lblStreetNumber.setWordWrap (false);
		addressFlexTable.setWidget (1, 0, lblStreetNumber);
		streetNoTextBox.setMaxLength (50);
		streetNoTextBox.setEnabled (false);
		addressFlexTable.setWidget (1, 1, streetNoTextBox);
		addressFlexTable.setWidget (2, 0, lblStreetName);
		streetNameTextBox.setMaxLength (50);
		streetNameTextBox.setEnabled (false);
		addressFlexTable.setWidget (2, 1, streetNameTextBox);
		addressFlexTable.setWidget (3, 0, lblArea);
		areaTextBox.setMaxLength (50);
		areaTextBox.setEnabled (false);
		addressFlexTable.setWidget (3, 1, areaTextBox);
		addressFlexTable.setWidget (4, 0, lblAreaCode);
		areaCodeTextBox.setMaxLength (50);
		areaCodeTextBox.setEnabled (false);
		addressFlexTable.setWidget (4, 1, areaCodeTextBox);
		addressFlexTable.setWidget (5, 0, lblCity);
		cityComboBox.setEnabled (false);
		cityComboBox.setName ("CITY");
		addressFlexTable.setWidget (5, 1, cityComboBox);
		addressFlexTable.setWidget (6, 0, lblOtherCity);
		otherCityTextBox.setEnabled (false);
		otherCityTextBox.setMaxLength (50);
		addressFlexTable.setWidget (6, 1, otherCityTextBox);
		decoratedStackPanel.add (contactFlexTable, "Contact Details", false);
		contactFlexTable.setSize ("100%", "100%");
		lblHasTheClient.setWordWrap (false);
		contactFlexTable.setWidget (0, 0, lblHasTheClient);
		sentSmsBeforeComboBox.addItem ("NO");
		sentSmsBeforeComboBox.addItem ("YES");
		sentSmsBeforeComboBox.addItem ("NOT SURE");
		sentSmsBeforeComboBox.addItem ("REFUSED");
		contactFlexTable.setWidget (0, 1, sentSmsBeforeComboBox);
		contactFlexTable.setWidget (1, 0, lblHasTheClient_1);
		receivedSmsBeforeComboBox.addItem ("NO");
		receivedSmsBeforeComboBox.addItem ("YES");
		receivedSmsBeforeComboBox.addItem ("NOT SURE");
		receivedSmsBeforeComboBox.addItem ("REFUSED");
		contactFlexTable.setWidget (1, 1, receivedSmsBeforeComboBox);
		lblDoesTheClient.setWordWrap (false);
		contactFlexTable.setWidget (2, 0, lblDoesTheClient);
		hasCellPhoneComboBox.addItem ("NO");
		hasCellPhoneComboBox.addItem ("YES");
		hasCellPhoneComboBox.addItem ("NOT SURE");
		hasCellPhoneComboBox.addItem ("REFUSED");
		contactFlexTable.setWidget (2, 1, hasCellPhoneComboBox);
		primaryCellPhoneCheckBox.setEnabled (false);
		contactFlexTable.setWidget (3, 0, primaryCellPhoneCheckBox);
		lblNumber.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (4, 0, lblNumber);
		primaryCellPhoneTextBox.setMaxLength (10);
		primaryCellPhoneTextBox.setEnabled (false);
		contactFlexTable.setWidget (4, 1, primaryCellPhoneTextBox);
		lblOwnedBy.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (5, 0, lblOwnedBy);
		primaryCellPhoneOwnerComboBox.setEnabled (false);
		primaryCellPhoneOwnerComboBox.addItem ("SELF");
		primaryCellPhoneOwnerComboBox.addItem ("SPOUSE");
		primaryCellPhoneOwnerComboBox.addItem ("MOTHER");
		primaryCellPhoneOwnerComboBox.addItem ("FATHER");
		primaryCellPhoneOwnerComboBox.addItem ("BROTHER");
		primaryCellPhoneOwnerComboBox.addItem ("SISTER");
		primaryCellPhoneOwnerComboBox.addItem ("COUSING");
		primaryCellPhoneOwnerComboBox.addItem ("SON");
		primaryCellPhoneOwnerComboBox.addItem ("DAUGHTER");
		primaryCellPhoneOwnerComboBox.addItem ("NEPHEW");
		primaryCellPhoneOwnerComboBox.addItem ("NIECE");
		primaryCellPhoneOwnerComboBox.addItem ("GRANDFATER");
		primaryCellPhoneOwnerComboBox.addItem ("GRANDMOTHER");
		primaryCellPhoneOwnerComboBox.addItem ("GRANDSON");
		primaryCellPhoneOwnerComboBox.addItem ("GRANDDAUGHTER");
		primaryCellPhoneOwnerComboBox.addItem ("RELATIVE");
		primaryCellPhoneOwnerComboBox.addItem ("FRIEND");
		primaryCellPhoneOwnerComboBox.addItem ("REFUSED");
		contactFlexTable.setWidget (5, 1, primaryCellPhoneOwnerComboBox);
		lblIsAHousehold.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (6, 0, lblIsAHousehold);
		primaryOwnerHouseholdComboBox.setEnabled (false);
		primaryOwnerHouseholdComboBox.addItem ("YES");
		primaryOwnerHouseholdComboBox.addItem ("NO");
		primaryOwnerHouseholdComboBox.addItem ("REFUSED");
		contactFlexTable.setWidget (6, 1, primaryOwnerHouseholdComboBox);
		secondaryCellPhoneCheckBox.setEnabled (false);
		contactFlexTable.setWidget (7, 0, secondaryCellPhoneCheckBox);
		label.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (8, 0, label);
		secondaryCellPhoneTextBox.setMaxLength (10);
		secondaryCellPhoneTextBox.setEnabled (false);
		contactFlexTable.setWidget (8, 1, secondaryCellPhoneTextBox);
		label_1.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (9, 0, label_1);
		secondaryCellPhoneOwnerComboBox.setEnabled (false);
		secondaryCellPhoneOwnerComboBox.addItem ("SELF");
		secondaryCellPhoneOwnerComboBox.addItem ("SPOUSE");
		secondaryCellPhoneOwnerComboBox.addItem ("MOTHER");
		secondaryCellPhoneOwnerComboBox.addItem ("FATHER");
		secondaryCellPhoneOwnerComboBox.addItem ("BROTHER");
		secondaryCellPhoneOwnerComboBox.addItem ("SISTER");
		secondaryCellPhoneOwnerComboBox.addItem ("COUSING");
		secondaryCellPhoneOwnerComboBox.addItem ("SON");
		secondaryCellPhoneOwnerComboBox.addItem ("DAUGHTER");
		secondaryCellPhoneOwnerComboBox.addItem ("NEPHEW");
		secondaryCellPhoneOwnerComboBox.addItem ("NIECE");
		secondaryCellPhoneOwnerComboBox.addItem ("GRANDFATER");
		secondaryCellPhoneOwnerComboBox.addItem ("GRANDMOTHER");
		secondaryCellPhoneOwnerComboBox.addItem ("GRANDSON");
		secondaryCellPhoneOwnerComboBox.addItem ("GRANDDAUGHTER");
		secondaryCellPhoneOwnerComboBox.addItem ("RELATIVE");
		secondaryCellPhoneOwnerComboBox.addItem ("FRIEND");
		secondaryCellPhoneOwnerComboBox.addItem ("REFUSED");
		contactFlexTable.setWidget (9, 1, secondaryCellPhoneOwnerComboBox);
		label_2.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (10, 0, label_2);
		secondaryOwnerHouseholdComboBox.setEnabled (false);
		secondaryOwnerHouseholdComboBox.addItem ("YES");
		secondaryOwnerHouseholdComboBox.addItem ("NO");
		secondaryOwnerHouseholdComboBox.addItem ("REFUSED");
		contactFlexTable.setWidget (10, 1, secondaryOwnerHouseholdComboBox);
		tertiaryCellPhoneCheckBox.setEnabled (false);
		contactFlexTable.setWidget (11, 0, tertiaryCellPhoneCheckBox);
		label_3.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (12, 0, label_3);
		tertiaryCellPhoneTextBox.setMaxLength (10);
		tertiaryCellPhoneTextBox.setEnabled (false);
		contactFlexTable.setWidget (12, 1, tertiaryCellPhoneTextBox);
		label_4.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (13, 0, label_4);
		tertiaryCellPhoneOwnerComboBox.addItem ("SELF");
		tertiaryCellPhoneOwnerComboBox.addItem ("SPOUSE");
		tertiaryCellPhoneOwnerComboBox.addItem ("MOTHER");
		tertiaryCellPhoneOwnerComboBox.addItem ("FATHER");
		tertiaryCellPhoneOwnerComboBox.addItem ("BROTHER");
		tertiaryCellPhoneOwnerComboBox.addItem ("SISTER");
		tertiaryCellPhoneOwnerComboBox.addItem ("COUSING");
		tertiaryCellPhoneOwnerComboBox.addItem ("SON");
		tertiaryCellPhoneOwnerComboBox.addItem ("DAUGHTER");
		tertiaryCellPhoneOwnerComboBox.addItem ("NEPHEW");
		tertiaryCellPhoneOwnerComboBox.addItem ("NIECE");
		tertiaryCellPhoneOwnerComboBox.addItem ("GRANDFATER");
		tertiaryCellPhoneOwnerComboBox.addItem ("GRANDMOTHER");
		tertiaryCellPhoneOwnerComboBox.addItem ("GRANDSON");
		tertiaryCellPhoneOwnerComboBox.addItem ("GRANDDAUGHTER");
		tertiaryCellPhoneOwnerComboBox.addItem ("RELATIVE");
		tertiaryCellPhoneOwnerComboBox.addItem ("FRIEND");
		tertiaryCellPhoneOwnerComboBox.addItem ("REFUSED");
		tertiaryCellPhoneOwnerComboBox.setEnabled (false);
		contactFlexTable.setWidget (13, 1, tertiaryCellPhoneOwnerComboBox);
		label_5.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		contactFlexTable.setWidget (14, 0, label_5);
		tertiaryOwnerHouseholdComboBox.addItem ("YES");
		tertiaryOwnerHouseholdComboBox.addItem ("NO");
		tertiaryOwnerHouseholdComboBox.addItem ("REFUSED");
		tertiaryOwnerHouseholdComboBox.setEnabled (false);
		contactFlexTable.setWidget (14, 1, tertiaryOwnerHouseholdComboBox);
		contactFlexTable.setWidget (15, 0, landlineNumberCheckBox);
		landlineNumberTextBox.setMaxLength (9);
		landlineNumberTextBox.setEnabled (false);
		contactFlexTable.setWidget (15, 1, landlineNumberTextBox);
		contactFlexTable.setWidget (16, 0, reachedViaSmsCheckBox);
		contactFlexTable.setWidget (17, 0, reachedViaLandlineCheckBox);
		decoratedStackPanel.add (otherFlexTable, "Other Details", false);
		otherFlexTable.setSize ("100%", "100%");
		otherFlexTable.setWidget (0, 0, lblOccupation);
		occupationComboBox.addItem ("UNEMPLOYED");
		occupationComboBox.addItem ("STUDENT");
		occupationComboBox.addItem ("MINING INDUSTRY");
		occupationComboBox.addItem ("FARM WORKER");
		occupationComboBox.addItem ("PROFESSIONAL");
		occupationComboBox.addItem ("GENERAL WORKER");
		occupationComboBox.addItem ("DOMESTIC WORKER");
		occupationComboBox.addItem ("SELF-EMPLOYED");
		occupationComboBox.addItem ("VENDOR");
		occupationComboBox.addItem ("TRANSPORTER");
		occupationComboBox.addItem ("SECURITY GUARD");
		occupationComboBox.addItem ("POLICE OFFICER");
		occupationComboBox.addItem ("MILITARY OFFICER");
		occupationComboBox.addItem ("PRISON OFFICER");
		occupationComboBox.addItem ("HOUSEHOLD");
		occupationComboBox.addItem ("CHILD");
		occupationComboBox.addItem ("REFUSED");
		occupationComboBox.addItem ("OTHER");
		otherFlexTable.setWidget (0, 1, occupationComboBox);
		otherFlexTable.setWidget (1, 0, lblSpecifyOther_1);
		otherOccupationTextBox.setEnabled (false);
		otherFlexTable.setWidget (1, 1, otherOccupationTextBox);
		hasWorkplaceCheckBox.setWordWrap (false);
		otherFlexTable.setWidget (2, 0, hasWorkplaceCheckBox);
		mobileVulnerableAreaCheckBox.setWordWrap (false);
		otherFlexTable.setWidget (3, 0, mobileVulnerableAreaCheckBox);
		otherFlexTable.setWidget (4, 0, lblClientsLevelOf);
		educationLevelComboBox.addItem ("NOT STARTED");
		educationLevelComboBox.addItem ("ELEMENTARY");
		educationLevelComboBox.addItem ("PRIMARY");
		educationLevelComboBox.addItem ("SECONDARY");
		educationLevelComboBox.addItem ("TERTIARY");
		educationLevelComboBox.addItem ("REFUSED");
		educationLevelComboBox.addItem ("OTHER");
		otherFlexTable.setWidget (4, 1, educationLevelComboBox);
		otherFlexTable.setWidget (5, 0, lblSpecifyOther_2);
		otherEducationLevelTextBox.setEnabled (false);
		otherFlexTable.setWidget (5, 1, otherEducationLevelTextBox);
		otherFlexTable.setWidget (6, 0, lblClientsNationalIdentity);
		nicTextBox.setMaxLength (12);
		otherFlexTable.setWidget (6, 1, nicTextBox);
		middleVerticalPanel.add (grid);
		middleVerticalPanel.setCellHorizontalAlignment (grid, HasHorizontalAlignment.ALIGN_CENTER);
		grid.setWidth ("50%");
		grid.setWidget (0, 0, saveButton);
		saveButton.setWidth ("100%");
		saveButton.setEnabled (false);
		grid.setWidget (0, 1, closeButton);
		closeButton.setWidth ("100%");
		flexTable.getRowFormatter ().setVerticalAlign (2, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (0, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);

		createHandlers ();
		IRZClient.refresh (flexTable);
		setRights (formName);
	}

	/**
	 * Create event handlers for form widgets
	 */
	public void createHandlers ()
	{
		ListBox[] listBoxes = {patientTypeComboBox, addressProvidedComboBox, cityComboBox, hasCellPhoneComboBox, occupationComboBox, educationLevelComboBox};
		for (int i = 0; i < listBoxes.length; i++)
			listBoxes[i].addChangeHandler (this);
		CheckBox[] checkBoxes = {primaryCellPhoneCheckBox, secondaryCellPhoneCheckBox, tertiaryCellPhoneCheckBox, landlineNumberCheckBox};
		for (int i = 0; i < checkBoxes.length; i++)
			checkBoxes[i].addValueChangeHandler (this);
		checkIdButton.addClickHandler (this);
		closeButton.addClickHandler (this);
		saveButton.addClickHandler (this);
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load (boolean status)
	{
		if (status)
			MainMenuComposite.showLoading ();
		else
			MainMenuComposite.hideLoading ();
	}

	/**
	 * Does form-specific cleaning up of widgets. Used if the form has some
	 * specific default values to load on reset.
	 */
	public void clearUp ()
	{
		currentPatient = null;
		currentPerson = null;
		IRZClient.clearControls (flexTable);
		ListBox[] listBoxes = {patientTypeComboBox, hivStatusComboBox, addressProvidedComboBox, cityComboBox, sentSmsBeforeComboBox, receivedSmsBeforeComboBox, hasCellPhoneComboBox,
				primaryCellPhoneOwnerComboBox, primaryOwnerHouseholdComboBox, secondaryCellPhoneOwnerComboBox, secondaryOwnerHouseholdComboBox, tertiaryCellPhoneOwnerComboBox,
				tertiaryOwnerHouseholdComboBox, occupationComboBox, educationLevelComboBox};
		CheckBox[] checkBoxes = {primaryCellPhoneCheckBox, secondaryCellPhoneCheckBox, tertiaryCellPhoneCheckBox, landlineNumberCheckBox, reachedViaSmsCheckBox, reachedViaLandlineCheckBox,
				hasWorkplaceCheckBox, mobileVulnerableAreaCheckBox};
		for (int i = 0; i < listBoxes.length; i++)
			listBoxes[i].setSelectedIndex (0);
		for (int i = 0; i < checkBoxes.length; i++)
			checkBoxes[i].setValue (false);
		otherSuspectTypeTextBox.setEnabled (false);
		streetNoTextBox.setEnabled (false);
		streetNameTextBox.setEnabled (false);
		areaTextBox.setEnabled (false);
		areaCodeTextBox.setEnabled (false);
		cityComboBox.setEnabled (false);
		otherCityTextBox.setEnabled (false);
		primaryCellPhoneCheckBox.setEnabled (false);
		primaryCellPhoneTextBox.setEnabled (false);
		primaryCellPhoneOwnerComboBox.setEnabled (false);
		primaryOwnerHouseholdComboBox.setEnabled (false);
		secondaryCellPhoneCheckBox.setEnabled (false);
		secondaryCellPhoneTextBox.setEnabled (false);
		secondaryCellPhoneOwnerComboBox.setEnabled (false);
		secondaryOwnerHouseholdComboBox.setEnabled (false);
		tertiaryCellPhoneCheckBox.setEnabled (false);
		tertiaryCellPhoneTextBox.setEnabled (false);
		tertiaryCellPhoneOwnerComboBox.setEnabled (false);
		tertiaryOwnerHouseholdComboBox.setEnabled (false);
		landlineNumberTextBox.setEnabled (false);
		otherOccupationTextBox.setEnabled (false);
		otherEducationLevelTextBox.setEnabled (false);
		patientTypeComboBox.setSelectedIndex (-1);
		hivStatusComboBox.setSelectedIndex (-1);
		cityComboBox.setSelectedIndex (-1);
	}

	/**
	 * To validate data in widgets before performing DML operations
	 * 
	 * @return
	 */
	public boolean validate ()
	{
		valid = true;
		/* Validate mandatory fields */
		if (IRZClient.get (clientIdTextBox).equals ("") || (primaryCellPhoneCheckBox.getValue () && IRZClient.get (primaryCellPhoneTextBox).equals (""))
				|| (secondaryCellPhoneCheckBox.getValue () && IRZClient.get (secondaryCellPhoneTextBox).equals (""))
				|| (tertiaryCellPhoneCheckBox.getValue () && IRZClient.get (tertiaryCellPhoneTextBox).equals ("")))
		{
			valid = false;
		}
		if (!valid)
		{
			Window.alert (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR));
			load (false);
		}
		return valid;
	}

	public void saveData ()
	{
		if (validate ())
		{
			Date enteredDate = new Date ();
			int eId = 0;
			String clientId = IRZClient.get (clientIdTextBox).toUpperCase ();
			String pid2 = IRZ.getCurrentUserName ();

			EncounterId encounterId = new EncounterId (eId, clientId, pid2, formName);
			Encounter encounter = new Encounter (encounterId, IRZ.getCurrentLocation ());
			encounter.setLocationId (IRZ.getCurrentLocation ());
			encounter.setDateEntered (enteredDate);
			encounter.setDateStart (new Date ());
			encounter.setDateEnd (new Date ());
			ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults> ();
			// Suspicion Details
			String suspectType = IRZClient.get (patientTypeComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SUSPECT_TYPE"), suspectType));
			currentPatient.setPatientType (suspectType);
			if (suspectType.equals ("OTHER"))
			{
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OTHER_TYPE"), IRZClient.get (otherSuspectTypeTextBox)));
				currentPatient.setPatientType (suspectType + ":" + IRZClient.get (otherSuspectTypeTextBox));
			}
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "HIV_STATUS"), IRZClient.get (hivStatusComboBox)));
			currentPatient.setHivStatus (IRZClient.get (hivStatusComboBox));
			// Address Details
			String addressProvided = IRZClient.get (addressProvidedComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "ADDRESS_PROVIDED"), addressProvided));
			if (addressProvided.equals ("YES"))
			{
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "STREET_NO"), IRZClient.get (streetNoTextBox)));
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "STREET_NAME"), IRZClient.get (streetNameTextBox)));
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "AREA"), IRZClient.get (areaTextBox)));
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "AREA_CODE"), IRZClient.get (areaCodeTextBox)));
				String city = IRZClient.get (cityComboBox);
				if (city.equals ("OTHER") && otherCityTextBox.isEnabled ())
				{
					city = IRZClient.get (otherCityTextBox);
				}
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "CITY"), city));
				currentPerson.setAddress1 (IRZClient.get (streetNoTextBox).toUpperCase ());
				currentPerson.setAddress2 (IRZClient.get (streetNameTextBox).toUpperCase ());
				currentPerson.setAddress3 (IRZClient.get (areaTextBox).toUpperCase ());
				currentPerson.setAddress4 (IRZClient.get (areaCodeTextBox).toUpperCase ());
				currentPerson.setCity (city);
			}
			// Contact Details
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SENT_SMS_BEFORE"), IRZClient.get (sentSmsBeforeComboBox)));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "RECV_SMS_BEFORE"), IRZClient.get (receivedSmsBeforeComboBox)));
			String cellAccess = IRZClient.get (hasCellPhoneComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "CELL_ACCESS"), cellAccess));
			if (cellAccess.equals ("YES"))
			{
				if (primaryCellPhoneCheckBox.getValue ())
				{
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "PRIMARY_CELL"), IRZClient.get (primaryCellPhoneTextBox)));
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "PRIMARY_CELL_OWNER"), IRZClient.get (primaryCellPhoneOwnerComboBox)));
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "PRIMARY_OWNER_HH"), IRZClient.get (primaryOwnerHouseholdComboBox)));
					currentPerson.setMobile (IRZClient.get (primaryCellPhoneTextBox));
				}
				if (secondaryCellPhoneCheckBox.getValue ())
				{
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SECONDARY_CELL"), IRZClient.get (secondaryCellPhoneTextBox)));
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SECONDARY_CELL_OWNER"), IRZClient.get (secondaryCellPhoneOwnerComboBox)));
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SECONDARY_OWNER_HH"), IRZClient.get (secondaryOwnerHouseholdComboBox)));
					currentPerson.setAlternateMobile (IRZClient.get (secondaryCellPhoneTextBox));
				}
				if (tertiaryCellPhoneCheckBox.getValue ())
				{
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "TERTIARY_CELL"), IRZClient.get (tertiaryCellPhoneTextBox)));
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "TERTIARY_CELL_OWNER"), IRZClient.get (tertiaryCellPhoneOwnerComboBox)));
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "TERTIARY_OWNER_HH"), IRZClient.get (tertiaryOwnerHouseholdComboBox)));
					currentPerson.setTertiaryMobile (IRZClient.get (tertiaryCellPhoneTextBox));
				}
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SMS_REACH"), reachedViaSmsCheckBox.getValue () ? "Y" : "N"));
			}
			boolean landlineProvided = landlineNumberCheckBox.getValue ();
			if (landlineProvided)
			{
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "LANDLINE"), IRZClient.get (landlineNumberTextBox)));
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "LANDLINE_REACH"), reachedViaLandlineCheckBox.getValue () ? "Y" : "N"));
				currentPerson.setPhone (IRZClient.get (landlineNumberTextBox));
			}
			// Other Details
			String occupation = IRZClient.get (occupationComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OCCUPATION"), occupation));
			if (occupation.equals ("OTHER"))
			{
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OTHER_OCCUPATION"), IRZClient.get (otherOccupationTextBox)));
			}
			String education = IRZClient.get (educationLevelComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "EDUCATION"), ""));
			if (education.equals ("OTHER"))
			{
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OTHER_EDUCATION"), IRZClient.get (otherEducationLevelTextBox)));
			}
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "HAS_WORKPLACE"), hasWorkplaceCheckBox.getValue () ? "Y" : "N"));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "IS_VULNERABLE"), mobileVulnerableAreaCheckBox.getValue () ? "Y" : "N"));
			String nic = IRZClient.get (nicTextBox);
			if (!nic.equals (""))
			{
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "NIC"), nic));
			}
			currentPerson.setNationalId (IRZClient.get (nicTextBox));

			service.saveRegistration (currentPerson, currentPatient, encounter, encounterResults.toArray (new EncounterResults[] {}), new AsyncCallback<String> ()
			{
				public void onSuccess (String result)
				{
					if (result.equals ("SUCCESS"))
					{
						Window.alert (CustomMessage.getInfoMessage (InfoType.INSERTED));
						clearUp ();
						load (false);
					}
					else
					{
						Window.alert (CustomMessage.getErrorMessage (ErrorType.INSERT_ERROR) + "\nDetails: " + result);
						load (false);
					}
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
		}
	}

	/**
	 * Gets rights of current user role for given parameter (defined in
	 * metadata)
	 * 
	 * @param menuName
	 */
	public void setRights (String menuName)
	{
		try
		{
			load (true);
			service.getUserRgihts (IRZ.getCurrentUserName (), IRZ.getCurrentRole (), menuName, new AsyncCallback<Boolean[]> ()
			{
				public void onSuccess (Boolean[] result)
				{
					final Boolean[] userRights = result;
					try
					{
						service.findUser (IRZ.getCurrentUserName (), new AsyncCallback<User> ()
						{
							public void onSuccess (User result)
							{
								rights.setRoleRights (IRZ.getCurrentRole (), userRights);
								boolean hasAccess = rights.getAccess (AccessType.INSERT) | rights.getAccess (AccessType.UPDATE) | rights.getAccess (AccessType.DELETE)
										| rights.getAccess (AccessType.SELECT);
								if (!hasAccess)
								{
									Window.alert (CustomMessage.getErrorMessage (ErrorType.DATA_ACCESS_ERROR));
									MainMenuComposite.clear ();
								}
								saveButton.setEnabled (rights.getAccess (AccessType.INSERT));
								load (false);
							}

							public void onFailure (Throwable caught)
							{
								load (false);
							}
						});
					}
					catch (Exception e)
					{
						e.printStackTrace ();
						load (false);
					}
				}

				public void onFailure (Throwable caught)
				{
					load (false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			load (false);
		}
	}

	public void onClick (ClickEvent event)
	{
		final Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == checkIdButton)
		{
			try
			{
				load (false);
				final String clientId = IRZClient.get (clientIdTextBox);
				service.findPerson (clientId, new AsyncCallback<Person> ()
				{
					public void onSuccess (Person result)
					{
						currentPerson = result;
						if (currentPerson == null)
						{
							Window.alert (CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND));
							return;
						}
						try
						{
							service.findPatient (clientId, new AsyncCallback<Patient> ()
							{
								public void onSuccess (Patient result)
								{
									currentPatient = result;
									if (currentPatient == null)
									{
										Window.alert (CustomMessage.getErrorMessage (ErrorType.ID_INVALID));
										currentPerson = null;
										return;
									}
									else
									{
										try
										{
											service.findVisit (clientId, new AsyncCallback<Visit> ()
											{
												public void onSuccess (Visit result)
												{
													if (result.getVisitPurpose ().equals (currentPatient.getDiseaseSuspected ()))
														Window.alert (CustomMessage.getInfoMessage (InfoType.ID_VALID));
													else
													{
														Window.alert ("Client was not marked as a TB Suspect in TB Screening form.");
														currentPerson = null;
														currentPatient = null;
													}
												}

												public void onFailure (Throwable caught)
												{
												}
											});
										}
										catch (Exception e)
										{
											e.printStackTrace ();
										}
									}
								}

								public void onFailure (Throwable caught)
								{
									caught.printStackTrace ();
								}
							});
						}
						catch (Exception e)
						{
							e.printStackTrace ();
						}
					}

					public void onFailure (Throwable caught)
					{
						caught.printStackTrace ();
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		else if (sender == saveButton)
		{
			// If the ID has not been checked, then return
			if (currentPerson == null)
			{
				Window.alert ("Please check Client's ID first.");
				load (false);
				return;
			}
			saveData ();
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	@Override
	public void onValueChange (ValueChangeEvent<Boolean> event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender instanceof CheckBox)
		{
			CheckBox checkBox = (CheckBox) sender;
			boolean choice = checkBox.getValue ();
			if (sender == primaryCellPhoneCheckBox)
			{
				primaryCellPhoneTextBox.setEnabled (choice);
				primaryCellPhoneOwnerComboBox.setEnabled (choice);
				primaryOwnerHouseholdComboBox.setEnabled (choice);
			}
			else if (sender == secondaryCellPhoneCheckBox)
			{
				secondaryCellPhoneTextBox.setEnabled (choice);
				secondaryCellPhoneOwnerComboBox.setEnabled (choice);
				secondaryOwnerHouseholdComboBox.setEnabled (choice);
			}
			else if (sender == tertiaryCellPhoneCheckBox)
			{
				tertiaryCellPhoneTextBox.setEnabled (choice);
				tertiaryCellPhoneOwnerComboBox.setEnabled (choice);
				tertiaryOwnerHouseholdComboBox.setEnabled (choice);
			}
			else if (sender == landlineNumberCheckBox)
			{
				landlineNumberTextBox.setEnabled (choice);
				reachedViaLandlineCheckBox.setValue (choice);
			}
		}
	}

	@Override
	public void onChange (ChangeEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender == patientTypeComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("OTHER");
			otherSuspectTypeTextBox.setEnabled (choice);
		}
		else if (sender == addressProvidedComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("YES");
			streetNoTextBox.setEnabled (choice);
			streetNameTextBox.setEnabled (choice);
			areaTextBox.setEnabled (choice);
			areaCodeTextBox.setEnabled (choice);
			cityComboBox.setEnabled (choice);
		}
		else if (sender == cityComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("OTHER");
			otherCityTextBox.setEnabled (choice);
		}
		else if (sender == hasCellPhoneComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("YES");
			primaryCellPhoneCheckBox.setEnabled (choice);
			secondaryCellPhoneCheckBox.setEnabled (choice);
			tertiaryCellPhoneCheckBox.setEnabled (choice);
			reachedViaSmsCheckBox.setValue (choice);
		}
		else if (sender == occupationComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("OTHER");
			otherOccupationTextBox.setEnabled (choice);
		}
		else if (sender == educationLevelComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("OTHER");
			otherEducationLevelTextBox.setEnabled (choice);
		}
	}
}
