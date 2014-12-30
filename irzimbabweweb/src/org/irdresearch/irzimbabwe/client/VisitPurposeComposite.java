package org.irdresearch.irzimbabwe.client;

import java.util.ArrayList;
import java.util.Date;
import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.Encounter;
import org.irdresearch.irzimbabwe.shared.model.EncounterId;
import org.irdresearch.irzimbabwe.shared.model.EncounterResults;
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
import org.irdresearch.irzimbabwe.shared.model.Patient;
import org.irdresearch.irzimbabwe.shared.model.User;
import org.irdresearch.irzimbabwe.shared.model.Visit;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Visit Purpose form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class VisitPurposeComposite extends Composite implements ClickHandler, ValueChangeHandler<Boolean>
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static final String formName = "PURPOSE";
    private int purposesCount = 0;

    private UserRightsUtil rights = new UserRightsUtil();
    private boolean valid;

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private Grid grid = new Grid(1, 2);

    private HorizontalPanel visitorTypeHorizontalPanel = new HorizontalPanel();

    private Button saveButton = new Button("Save");
    private Button closeButton = new Button("Close");

    private Label lblVisitDate = new Label("Visit Date:");
    private Label lblVisitorType = new Label("Visitor Type:");
    private Label lblSite = new Label("External Site:");
    private Label lblClientsInitialDemographics = new Label("Client's Purpose of Visit Form");
    private Label lblPurposeOfVisit = new Label("Purpose of Visit:");
    private Label lblTeam = new Label("Team:");
    private Label lblClientsId = new Label("Client's ID:");
    private Label clientIdLabel = new Label("");

    private ListBox externalSiteComboBox = new ListBox();
    private ListBox siteComboBox = new ListBox();

    private RadioButton psiVisitorRadioButton = new RadioButton("visitorType", "PSI Visitor");
    private RadioButton externalVisitorRadioButton = new RadioButton("visitorType", "External");

    private DateBox dateVisitDateBox = new DateBox();
    private final Grid purposeGrid = new Grid(3, 2);

    private final CheckBox visitPurposeTB = new CheckBox("TB TEST");
    private final CheckBox visitPurposeHIV = new CheckBox("HIV COUNSELLING");
    private final CheckBox visitPurposeMC = new CheckBox("CIRCUMCISION");
    private final CheckBox visitPurposeART = new CheckBox("ANTIRETROVIRAL THERAPY");
    private final CheckBox visitPurposeCD4 = new CheckBox("CD4 CELL COUNT");
    private final CheckBox visitPurposeSRH = new CheckBox("SEXUAL AND REPRODUCTIVE HEALTH");

    public VisitPurposeComposite()
    {
	initWidget(flexTable);
	flexTable.setSize("85%", "100%");
	flexTable.setWidget(0, 0, topFlexTable);
	lblClientsInitialDemographics.setWordWrap(false);
	lblClientsInitialDemographics.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblClientsInitialDemographics);
	topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.setWidget(1, 0, rightFlexTable);
	rightFlexTable.setWidth("85%");
	rightFlexTable.setWidget(0, 0, lblVisitDate);
	rightFlexTable.setWidget(0, 1, dateVisitDateBox);
	rightFlexTable.setWidget(1, 0, lblVisitorType);
	rightFlexTable.setWidget(1, 1, visitorTypeHorizontalPanel);
	visitorTypeHorizontalPanel.setHeight("100%");
	psiVisitorRadioButton.setWordWrap(false);
	psiVisitorRadioButton.setValue(true);
	visitorTypeHorizontalPanel.add(psiVisitorRadioButton);
	externalVisitorRadioButton.setWordWrap(false);
	visitorTypeHorizontalPanel.add(externalVisitorRadioButton);
	externalVisitorRadioButton.setWidth("92px");
	rightFlexTable.setWidget(2, 0, lblSite);
	rightFlexTable.setWidget(2, 1, externalSiteComboBox);
	externalSiteComboBox.setEnabled(false);
	lblPurposeOfVisit.setWordWrap(false);
	rightFlexTable.setWidget(3, 0, lblPurposeOfVisit);

	rightFlexTable.setWidget(3, 1, purposeGrid);
	purposeGrid.setSize("80%", "100%");

	purposeGrid.setWidget(0, 0, visitPurposeTB);

	visitPurposeHIV.setHTML("HIV COUNSELLING");

	purposeGrid.setWidget(0, 1, visitPurposeHIV);
	visitPurposeMC.setWordWrap(false);
	visitPurposeMC.setHTML("CIRCUMCISION");

	purposeGrid.setWidget(1, 0, visitPurposeMC);
	visitPurposeCD4.setWordWrap(false);
	visitPurposeCD4.setHTML("CD4 CELL COUNT");

	purposeGrid.setWidget(1, 1, visitPurposeCD4);
	visitPurposeSRH.setHTML("SEXUAL AND REPRODUCTIVE HEALTH");
	purposeGrid.setWidget(2, 0, visitPurposeSRH);
	visitPurposeART.setWordWrap(false);
	visitPurposeART.setHTML("ANTIRETROVIRAL THERAPY");

	purposeGrid.setWidget(2, 1, visitPurposeART);

	purposeGrid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
	purposeGrid.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);
	purposeGrid.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
	purposeGrid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
	purposeGrid.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_LEFT);
	rightFlexTable.setWidget(4, 0, lblTeam);
	siteComboBox.setName("TEAM");
	rightFlexTable.setWidget(4, 1, siteComboBox);
	rightFlexTable.setWidget(5, 0, lblClientsId);
	rightFlexTable.setWidget(5, 1, clientIdLabel);
	rightFlexTable.setWidget(6, 1, grid);
	grid.setSize("100%", "100%");
	saveButton.setText("Generate");
	saveButton.setEnabled(false);
	grid.setWidget(0, 0, saveButton);
	grid.setWidget(0, 1, closeButton);
	flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
	flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
	flexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

	psiVisitorRadioButton.addValueChangeHandler(this);
	externalVisitorRadioButton.addValueChangeHandler(this);
	saveButton.addClickHandler(this);
	closeButton.addClickHandler(this);

	IRZClient.refresh(flexTable);
	dateVisitDateBox.setValue(new Date());
	setRights(formName);
	load(true);
	externalSiteComboBox.clear();
	rightFlexTable.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);
	rightFlexTable.getCellFormatter().setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_LEFT);
	try
	{
	    service.getTableData("location", new String[] { "location_id", "location_name" }, "location_type='GOVT'", new AsyncCallback<String[][]>() {
		public void onSuccess(String[][] result)
		{
		    for (int i = 0; i < result.length; i++)
			externalSiteComboBox.addItem(result[i][1], result[i][0]);
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

    /**
     * Does form-specific cleaning up of widgets. Used if the form has some
     * specific default values to load on reset.
     */
    @SuppressWarnings("deprecation")
    public void clearUp()
    {
	IRZClient.clearControls(flexTable);
	clientIdLabel.setText("Last Id generated: " + clientIdLabel.getText());
	// visitPurposeComboBox.setSelectedIndex(-1);
	siteComboBox.setSelectedIndex(0);
	externalSiteComboBox.setSelectedIndex(0);
	psiVisitorRadioButton.setValue(true);
	dateVisitDateBox.setValue(new Date());
	CheckBox[] visitPurposeCheckBoxes = { visitPurposeTB, visitPurposeHIV, visitPurposeMC, visitPurposeCD4, visitPurposeART, visitPurposeSRH };
	for (CheckBox checkBox : visitPurposeCheckBoxes)
	{
	    if (checkBox.isChecked())
		checkBox.setChecked(false);
	    if (!checkBox.isEnabled())
		checkBox.setEnabled(true);
	}
	externalSiteComboBox.setEnabled(false);

    }

    /**
     * To validate data in widgets before performing DML operations
     * 
     * @return
     */
    @SuppressWarnings("deprecation")
    public boolean validate()
    {
	valid = true;
	/* Validate mandatory fields */
	CheckBox[] visitPurposeCheckBoxes = { visitPurposeTB, visitPurposeHIV, visitPurposeMC, visitPurposeCD4, visitPurposeART, visitPurposeSRH };
	for (CheckBox checkBox : visitPurposeCheckBoxes)
	{
	    if (checkBox.isChecked())
	    {
		purposesCount++;
		valid = true;
	    }
	}
	if (IRZClient.get(dateVisitDateBox.getTextBox()).equals("") || purposesCount == 0)
	{
	    Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
	    valid = false;
	    load(false);
	}
	if (dateVisitDateBox.getValue().after(new Date()))
	{
	    Window.alert("Visit Date: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR));
	    valid = false;
	    load(false);
	}
	return valid;
    }

    @SuppressWarnings("deprecation")
    public void saveData()
    {
	if (validate())
	{
	    Date enteredDate = dateVisitDateBox.getValue();
	    int eId = 0;
	    final String clientId = clientIdLabel.getText();
	    String pid1 = clientId;
	    String pid2 = IRZ.getCurrentUserName();

	    /* get the multiple disease suspected */
	    String purpose = "";
	    String multiPurpose = "";
	    String team = IRZClient.get(siteComboBox);

	    EncounterId encounterId = new EncounterId(0, pid1, pid2, formName);
	    Encounter encounter = new Encounter(encounterId, IRZ.getCurrentLocation());
	    encounter.setLocationId(IRZ.getCurrentLocation());
	    encounter.setDateEntered(enteredDate);
	    encounter.setDateStart(new Date());
	    encounter.setDateEnd(new Date());

	    ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults>();
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, pid1, pid2, formName, "TEAM"), team));
	    ArrayList<Visit> visitPurposes = new ArrayList<Visit>();
	    CheckBox[] visitPurposeCheckBoxes = { visitPurposeTB, visitPurposeHIV, visitPurposeMC, visitPurposeCD4, visitPurposeART, visitPurposeSRH };
	    for (CheckBox checkBox : visitPurposeCheckBoxes)
	    {
		if (checkBox.isChecked())
		{
		    purpose = IRZ.getDefinitionKey("VISIT_PURPOSE", checkBox.getText());
		    Visit visit = new Visit(clientId, purpose, false, new Date());
		    visitPurposes.add(visit);
		    if (!multiPurpose.equals(""))
			multiPurpose = multiPurpose.concat("|").concat(IRZ.getDefinitionKey("VISIT_PURPOSE", checkBox.getText()));
		    else
			multiPurpose = IRZ.getDefinitionKey("VISIT_PURPOSE", checkBox.getText());
		}
	    }
	    // Visit purpose for
	    Patient patient = new Patient(clientId);
	    patient.setTeam(team);
	    patient.setDateScreened(new Date());
	    if (!purpose.equals("TB") && purposesCount > 1)
	    {// client has multiple visit purpose

		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, pid1, pid2, formName, "PURPOSE"), multiPurpose));
	    }
	    else if (!purpose.equals("TB") && purposesCount == 1)
	    {// client has single visit purpose other than tb
		patient.setDiseaseSuspected(purpose);
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, pid1, pid2, formName, "PURPOSE"), purpose));
	    }
	    else
		// is a TB patient
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, pid1, pid2, formName, "PURPOSE"), purpose));

	    if (externalVisitorRadioButton.getValue())
		patient.setTreatmentSite(IRZClient.get(externalSiteComboBox));
	    else
		patient.setTreatmentSite(IRZ.getCurrentLocation());

	    service.saveVisitPurpose(visitPurposes.toArray(new Visit[] {}), patient, encounter, encounterResults.toArray(new EncounterResults[] {}), new AsyncCallback<String>() {
		public void onSuccess(String result)
		{
		    if (result.equals("SUCCESS"))
		    {
			Window.alert(CustomMessage.getInfoMessage(InfoType.INSERTED) + "\nGenerated Client's ID: " + clientId);
			clearUp();
			load(false);
		    }
		    else
		    {
			Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR) + "\nDetails: " + result);
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
    }

    /**
     * Gets rights of current user role for given parameter (defined in
     * metadata)
     * 
     * @param menuName
     */
    public void setRights(String menuName)
    {
	try
	{
	    load(true);
	    service.getUserRgihts(IRZ.getCurrentUserName(), IRZ.getCurrentRole(), menuName, new AsyncCallback<Boolean[]>() {
		public void onSuccess(Boolean[] result)
		{
		    final Boolean[] userRights = result;
		    try
		    {
			service.findUser(IRZ.getCurrentUserName(), new AsyncCallback<User>() {
			    public void onSuccess(User result)
			    {
				rights.setRoleRights(IRZ.getCurrentRole(), userRights);
				boolean hasAccess = rights.getAccess(AccessType.INSERT) | rights.getAccess(AccessType.UPDATE) | rights.getAccess(AccessType.DELETE)
				| rights.getAccess(AccessType.SELECT);
				if (!hasAccess)
				{
				    Window.alert(CustomMessage.getErrorMessage(ErrorType.DATA_ACCESS_ERROR));
				    MainMenuComposite.clear();
				}
				saveButton.setEnabled(rights.getAccess(AccessType.INSERT));
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

    @SuppressWarnings("deprecation")
    public void onClick(ClickEvent event)
    {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == saveButton)
	{
	    try
	    {
		service.getObject("patient", "count(patient_id) + 1", "team='" + IRZClient.get(siteComboBox) + "'", new AsyncCallback<String>() {
		    public void onSuccess(String result)
		    {
			// Generate client ID
			StringBuilder clientId = new StringBuilder();
			clientId.append(IRZ.getCurrentLocation());
			clientId.append(String.valueOf(new Date().getYear()).substring(1));
			clientId.append(IRZClient.get(siteComboBox));
			String resString = String.valueOf(result);
			for (int i = 0; i < (5 - resString.length()); i++)
			    clientId.append("0");
			clientId.append(resString);
			clientIdLabel.setText(clientId.toString());
			saveData();
			load(false);
		    }

		    public void onFailure(Throwable caught)
		    {
			caught.printStackTrace();
			load(true);
		    }
		});
	    } catch(Exception e)
	    {
		e.printStackTrace();
		load(true);
	    }
	}
	else if (sender == closeButton)
	{
	    MainMenuComposite.clear();
	}
    }

    public void onValueChange(ValueChangeEvent<Boolean> event)
    {
	Widget sender = (Widget) event.getSource();
	if (sender == externalVisitorRadioButton)
	{
	    boolean choice = externalVisitorRadioButton.getValue();
	    externalSiteComboBox.setEnabled(choice);
	    CheckBox[] visitPurposeCheckBoxes = {visitPurposeMC,visitPurposeSRH };
	    for (CheckBox checkBox : visitPurposeCheckBoxes)
	    {
		    checkBox.setEnabled(false);
	    }
	}
	else if (sender == psiVisitorRadioButton)
	{
	    boolean choice = psiVisitorRadioButton.getValue();
	    externalSiteComboBox.setEnabled(!choice);
	    CheckBox[] visitPurposeCheckBoxes = { visitPurposeTB, visitPurposeHIV, visitPurposeMC, visitPurposeCD4, visitPurposeART, visitPurposeSRH };
	    for (CheckBox checkBox : visitPurposeCheckBoxes)
	    {
		checkBox.setEnabled(true);
	    }
	}
    }
}
