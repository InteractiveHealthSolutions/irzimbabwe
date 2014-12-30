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
import org.irdresearch.irzimbabwe.shared.model.SputumTest;
import org.irdresearch.irzimbabwe.shared.model.SputumTestId;
import org.irdresearch.irzimbabwe.shared.model.User;

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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Sputum Collection form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class SputumCollectionComposite extends Composite implements ClickHandler, ChangeHandler
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static final String formName = "SPUTUM_COL";

    private UserRightsUtil rights = new UserRightsUtil();
    private Patient currentPatient;
    private int currentSampleCount = 0;
    private boolean valid;

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable clientIdFlexTable = new FlexTable();

    private Grid grid = new Grid(1, 2);
    private VerticalPanel middleVerticalPanel = new VerticalPanel();
    private HorizontalPanel clientIdHorizontalPanel = new HorizontalPanel();

    private Button checkIdButton = new Button("Check");
    private Button saveButton = new Button("Save");
    private Button closeButton = new Button("Close");

    private Label lblClientsInitialDemographics = new Label("Sputum Collection Form");
    private Label lblClientsId = new Label("Client's ID:");
    private Label lblSputumCollectionSite = new Label("Sputum Collection Site:");
    private Label lblSiteName = new Label("Site Name:");
    private Label lblReasonForCollection = new Label("Reason for Collection:");
    private Label lblWasSampleCollected = new Label("Was Sample Collected?");
    private Label lblSpecifyOther = new Label("Specify Other:");
    private Label lblNumberOfSamples = new Label("Number of Samples:");

    private TextBox clientIdTextBox = new TextBox();
    private TextBox otherReasonTextBox = new TextBox();

    private ListBox siteTypeComboBox = new ListBox();
    private ListBox monthComboBox = new ListBox();
    private ListBox sampleCollectedComboBox = new ListBox();
    private ListBox totalSamplesCollectedComboBox = new ListBox();
    private ListBox siteNameComboBox = new ListBox();
    protected Person currentPerson;

    public SputumCollectionComposite()
    {
	initWidget(flexTable);
	flexTable.setSize("80%", "100%");
	flexTable.setWidget(0, 0, topFlexTable);
	lblClientsInitialDemographics.setWordWrap(false);
	lblClientsInitialDemographics.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblClientsInitialDemographics);
	topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.setWidget(1, 0, middleVerticalPanel);
	middleVerticalPanel.add(clientIdFlexTable);
	clientIdFlexTable.setWidth("100%");
	clientIdFlexTable.setWidget(0, 0, lblClientsId);
	lblClientsId.setWordWrap(false);
	clientIdFlexTable.setWidget(0, 1, clientIdHorizontalPanel);
	clientIdTextBox.setVisibleLength(12);
	clientIdTextBox.setMaxLength(12);
	clientIdHorizontalPanel.add(clientIdTextBox);
	clientIdTextBox.setName("patient;patient_id");
	checkIdButton.setText("Check");
	clientIdHorizontalPanel.add(checkIdButton);
	checkIdButton.setWidth("100%");
	lblSputumCollectionSite.setWordWrap(false);
	clientIdFlexTable.setWidget(1, 0, lblSputumCollectionSite);
	siteTypeComboBox.setName("LOCATION_TYPE");
	clientIdFlexTable.setWidget(1, 1, siteTypeComboBox);
	clientIdFlexTable.setWidget(2, 0, lblSiteName);
	siteNameComboBox.setEnabled(false);
	clientIdFlexTable.setWidget(2, 1, siteNameComboBox);
	clientIdFlexTable.setWidget(3, 0, lblReasonForCollection);
	monthComboBox.addItem("BASELINE", "0");
	monthComboBox.addItem("2ND MONTH FOLLOW-UP", "2");
	monthComboBox.addItem("5TH MONTH FOLLOW-UP", "5");
	monthComboBox.addItem("6TH MONTH FOLLOW-UP", "6");
	clientIdFlexTable.setWidget(3, 1, monthComboBox);
	lblWasSampleCollected.setWordWrap(false);
	clientIdFlexTable.setWidget(4, 0, lblWasSampleCollected);
	sampleCollectedComboBox.addItem("YES");
	sampleCollectedComboBox.addItem("CLIENT COULD NOT PRODUCE SAMPLE");
	sampleCollectedComboBox.addItem("CLIENT REFUSED TO PROVIDE SAMPLE");
	sampleCollectedComboBox.addItem("CLIENT PROVIDED WITH CONTAINER");
	sampleCollectedComboBox.addItem("OTHER");
	clientIdFlexTable.setWidget(4, 1, sampleCollectedComboBox);
	clientIdFlexTable.setWidget(5, 0, lblSpecifyOther);
	otherReasonTextBox.setEnabled(false);
	clientIdFlexTable.setWidget(5, 1, otherReasonTextBox);
	clientIdFlexTable.setWidget(6, 0, lblNumberOfSamples);
	totalSamplesCollectedComboBox.addItem("1");
	totalSamplesCollectedComboBox.addItem("2");
	totalSamplesCollectedComboBox.addItem("3");
	totalSamplesCollectedComboBox.addItem("4");
	clientIdFlexTable.setWidget(6, 1, totalSamplesCollectedComboBox);
	middleVerticalPanel.add(grid);
	middleVerticalPanel.setCellHorizontalAlignment(grid, HasHorizontalAlignment.ALIGN_CENTER);
	grid.setWidth("50%");
	grid.setWidget(0, 0, saveButton);
	saveButton.setWidth("100%");
	saveButton.setEnabled(false);
	grid.setWidget(0, 1, closeButton);
	closeButton.setWidth("100%");
	flexTable.getRowFormatter().setVerticalAlign(2, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
	flexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
	flexTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);

	createHandlers();
	IRZClient.refresh(flexTable);
	setRights(formName);
    }

    public void createHandlers()
    {
	ListBox[] listBoxes = { siteTypeComboBox, siteNameComboBox, sampleCollectedComboBox };
	for (int i = 0; i < listBoxes.length; i++)
	    listBoxes[i].addChangeHandler(this);
	checkIdButton.addClickHandler(this);
	closeButton.addClickHandler(this);
	saveButton.addClickHandler(this);
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
    public void clearUp()
    {
	currentPatient = null;
	IRZClient.clearControls(flexTable);
	ListBox[] listBoxes = { siteTypeComboBox, siteNameComboBox, sampleCollectedComboBox };
	for (int i = 0; i < listBoxes.length; i++)
	    listBoxes[i].setSelectedIndex(0);
	otherReasonTextBox.setEnabled(false);
    }

    /**
     * To validate data in widgets before performing DML operations
     * 
     * @return
     */
    public boolean validate()
    {
	valid = true;
	/* Validate mandatory fields */
	if (IRZClient.get(clientIdTextBox).equals(""))
	{
	    valid = false;
	}
	if (!valid)
	{
	    Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
	    load(false);
	}
	return valid;
    }

    public void saveData()
    {
	if (validate())
	{
	    Date enteredDate = new Date();
	    int eId = 0;
	    String clientId = IRZClient.get(clientIdTextBox).toUpperCase();
	    String pid2 = IRZ.getCurrentUserName();

	    EncounterId encounterId = new EncounterId(eId, clientId, pid2, formName);
	    Encounter encounter = new Encounter(encounterId, IRZ.getCurrentLocation());
	    encounter.setLocationId(IRZ.getCurrentLocation());
	    encounter.setDateEntered(enteredDate);
	    encounter.setDateStart(new Date());
	    encounter.setDateEnd(new Date());
	    ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults>();
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "SITE_TYPE"), IRZClient.get(siteTypeComboBox)));
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "SITE_NAME"), IRZClient.get(siteNameComboBox)));
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "MONTH"), IRZClient.get(monthComboBox)));
	    String collected = IRZClient.get(sampleCollectedComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "SAMPLE_COLLECTED"), collected));
	    if (collected.equals("YES"))
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "SAMPLES"), IRZClient.get(totalSamplesCollectedComboBox)));
	    else
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "OTHER_REASON"), IRZClient.get(otherReasonTextBox).toUpperCase()));
	    // Lab results
	    int samples = totalSamplesCollectedComboBox.isEnabled() ? Integer.parseInt(IRZClient.get(totalSamplesCollectedComboBox)):0;
	    SputumTest[] smearTests = new SputumTest[samples];
	    for (int i = 0; i < smearTests.length; i++)
	    {
		SputumTestId id = new SputumTestId(clientId, currentSampleCount + i + 1);
		smearTests[i] = new SputumTest(id);
		smearTests[i].setCollectedBy(IRZ.getCurrentUserName());
		smearTests[i].setDateCollected(enteredDate);
		smearTests[i].setMonth(Integer.parseInt(IRZClient.get(monthComboBox)));
	    }
	    service.saveSputumCollection(smearTests, currentPatient, encounter, encounterResults.toArray(new EncounterResults[] {}), new AsyncCallback<String>() {
		public void onSuccess(String result)
		{
		    if (result.equals("SUCCESS"))
		    {
			Window.alert(CustomMessage.getInfoMessage(InfoType.INSERTED));
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

    public void onClick(ClickEvent event)
    {
	final Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == checkIdButton)
	{
	    try
	    {
		load(true);
		final String clientId = IRZClient.get(clientIdTextBox);
		// /////////////////////////////////////////////////////////////////////////////////////////////
		service.exists("person", "pid='" + clientId + "'", new AsyncCallback<Boolean>() {
		    public void onSuccess(Boolean result)
		    {
			if (result)
			{
			    try
			    {
				service.findPatient(clientId, new AsyncCallback<Patient>() {
				    public void onSuccess(Patient result)
				    {
					currentPatient = result;
					load(false);
					if (currentPatient == null)
					{
					    Window.alert("Enter Client Demographics:" + CustomMessage.getErrorMessage(ErrorType.ID_INVALID));
					    currentPerson = null;
					}
					else
					{

					    Window.alert(CustomMessage.getInfoMessage(InfoType.ID_VALID));
					    try
					    {
						service.getObject("sputum_test", "ifnull(max(sample_no), 0)", "patient_id='" + IRZClient.get(clientIdTextBox) + "'", new AsyncCallback<String>() {
						    public void onSuccess(String result)
						    {
							currentSampleCount = Integer.parseInt(result);
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
						load(false);
					    }
					}
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
				load(false);
			    }

			}
			else
			{
			    load(false);
			    Window.alert("Enter Client Demographics:" + CustomMessage.getErrorMessage(ErrorType.ID_INVALID));

			}
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
		load(false);
	    }

	}
	else if (sender == saveButton)
	{
	    // If the ID has not been checked, then return
	    if (currentPatient == null)
	    {
		Window.alert("Please check Client's ID first.");
		load(false);
		return;
	    }
	    saveData();
	}
	else if (sender == closeButton)
	{
	    MainMenuComposite.clear();
	}

    }

    @Override
    public void onChange(ChangeEvent event)
    {
	Widget sender = (Widget) event.getSource();
	if (sender == siteTypeComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("OTHER");
	    siteNameComboBox.setEnabled(!choice);
	    if (!choice)
	    {
		try
		{
		    service.getColumnData("location", "location_name", "location_type='" + IRZClient.get(siteTypeComboBox) + "'", new AsyncCallback<String[]>() {
			public void onSuccess(String[] result)
			{
			    siteNameComboBox.clear();
			    for (String s : result)
				siteNameComboBox.addItem(s);
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
	else if (sender == sampleCollectedComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("OTHER");
	    otherReasonTextBox.setEnabled(choice);
	    choice = IRZClient.get(sender).equals("YES");
	    totalSamplesCollectedComboBox.setEnabled(choice);
	}
    }
}
