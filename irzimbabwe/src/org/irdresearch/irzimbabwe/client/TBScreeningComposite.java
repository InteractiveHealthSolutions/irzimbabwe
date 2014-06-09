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
import org.irdresearch.irzimbabwe.shared.model.User;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedStackPanel;

/**
 * TB Screening form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class TBScreeningComposite extends Composite implements ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static final String formName = "TB_SCREEN";

    private UserRightsUtil rights = new UserRightsUtil();
    private Patient currentPatient;
    private boolean valid;

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();

    private VerticalPanel middleVerticalPanel = new VerticalPanel();

    private DecoratedStackPanel decoratedStackPanel = new DecoratedStackPanel();
    private FlexTable coughFlexTable = new FlexTable();
    private FlexTable symptomsFlexTable = new FlexTable();
    private FlexTable tbHistoryFlexTable = new FlexTable();

    private HorizontalPanel idHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel chestPainDurationHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel breathingShortnessDurationHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel nightSweatsDurationHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel weightLossDurationHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel feverDurationHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel lymphNodeDurationHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel treatmentHistoryDurationHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel treatmentDurationHorizontalPanel = new HorizontalPanel();

    private Grid grid = new Grid(1, 2);

    private Button checkIdButton = new Button("Check");
    private Button saveButton = new Button("Save");
    private Button closeButton = new Button("Close");

    private Label lblClientsInitialDemographics = new Label("TB Screening Results Form");
    private Label lblClientsId = new Label("Client's ID:");
    private Label lblDoesTheClient = new Label("Does the Client have cough?");
    private Label lblDurationOfCough = new Label("Duration of Cough:");
    private Label lblIsTheClient = new Label("Is the Client producing Sputum?");
    private Label lblIsTheBlood = new Label("Is the Blood in Sputum (Haemptysis)?");
    private Label lblDoesTheClient_1 = new Label("Does the Client have Chest Pain?");
    private Label lblHasClientExperienced = new Label("Does Client have Shortness of Breath or difficulty in Breathing?");
    private Label lblDoesTheClient_2 = new Label("Does the Client Sweat in Night?");
    private Label lblIsClientExperiencing = new Label("Is Client experiencing Weight Loss?");
    private Label lblDoesClientHave = new Label("Does Client have Fever?");
    private Label lblDoesTheClient_3 = new Label("Does the Client experience Lymph Node Swelling?");
    private Label lblHasTheClient = new Label("Does the Client have TB History?");
    private Label lblHasTheClient_1 = new Label("Has the Client been on TB Treatment before?");
    private Label lblWhereWasThe = new Label("Where was the Client Treated?");
    private Label lblDidTheClient = new Label("Did the Client complete the Treatment?");
    private Label lblDoesAnyoneIn = new Label("Does anyone in Client's household have TB?");

    private TextBox clientIdTextBox = new TextBox();
    private TextBox treatmentCentreTextBox = new TextBox();

    private IntegerBox chestPainDurationIntegerBox = new IntegerBox();
    private IntegerBox breathingShortnessDurationIntegerBox = new IntegerBox();
    private IntegerBox nightSweatsDurationIntegerBox = new IntegerBox();
    private IntegerBox weightLossDurationIntegerBox = new IntegerBox();
    private IntegerBox feverDurationIntegerBox = new IntegerBox();
    private IntegerBox lymphNodeDurationIntegerBox = new IntegerBox();
    private IntegerBox treatmentHistoryDurationIntegerBox = new IntegerBox();
    private IntegerBox treatmentDurationIntegerBox = new IntegerBox();
    private CheckBox durationFormTreatmentToDateCheckBox = new CheckBox("Duration form Treatment to date");
    private CheckBox durationOfTreatmentCheckBox = new CheckBox("Duration of Treatment");
    private CheckBox conclusionCheckBox = new CheckBox("The Client is a TB Suspect");

    private ListBox coughComboBox = new ListBox();
    private ListBox coughDurationComboBox = new ListBox();
    private ListBox productiveCoughComboBox = new ListBox();
    private ListBox haemoptysisComboBox = new ListBox();
    private ListBox chestPainComboBox = new ListBox();
    private ListBox chestPainDurationUnitComboBox = new ListBox();
    private ListBox breathingShortnessComboBox = new ListBox();
    private ListBox breathingShortnessDurationUnitComboBox = new ListBox();
    private ListBox nightSweatsComboBox = new ListBox();
    private ListBox nightSweatsDurationUnitComboBox = new ListBox();
    private ListBox weightLossComboBox = new ListBox();
    private ListBox weightLossDurationUnitComboBox = new ListBox();
    private ListBox feverComboBox = new ListBox();
    private ListBox feverDurationUnitComboBox = new ListBox();
    private ListBox lymphNodeDurationUnitComboBox = new ListBox();
    private ListBox tbHistoryComboBox = new ListBox();
    private ListBox treatmentHistoryComboBox = new ListBox();
    private ListBox treatmentHistoryDurationUnitComboBox = new ListBox();
    private ListBox treatmentDurationUnitComboBox = new ListBox();
    private ListBox treatmentCompletedComboBox = new ListBox();
    private ListBox familyTBHistoryComboBox = new ListBox();
    private ListBox lymphNodeComboBox = new ListBox();

    public TBScreeningComposite()
    {
	initWidget(flexTable);
	flexTable.setSize("80%", "100%");
	flexTable.setWidget(0, 0, topFlexTable);
	lblClientsInitialDemographics.setWordWrap(false);
	lblClientsInitialDemographics.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblClientsInitialDemographics);
	topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.setWidget(1, 0, middleVerticalPanel);
	middleVerticalPanel.add(idHorizontalPanel);
	idHorizontalPanel.add(lblClientsId);
	idHorizontalPanel.setCellVerticalAlignment(lblClientsId, HasVerticalAlignment.ALIGN_MIDDLE);
	lblClientsId.setWordWrap(false);
	idHorizontalPanel.add(clientIdTextBox);
	clientIdTextBox.setVisibleLength(12);
	clientIdTextBox.setMaxLength(12);
	clientIdTextBox.setName("patient;patient_id");
	checkIdButton.setText("Check");
	idHorizontalPanel.add(checkIdButton);
	middleVerticalPanel.add(decoratedStackPanel);
	decoratedStackPanel.setWidth("100%");
	lblDoesTheClient.setWordWrap(false);
	decoratedStackPanel.add(coughFlexTable, "Cough Details", false);
	coughFlexTable.setSize("100%", "100%");
	coughFlexTable.setWidget(0, 0, lblDoesTheClient);
	coughComboBox.addItem("NO");
	coughComboBox.addItem("YES");
	coughComboBox.addItem("NOT SURE");
	coughFlexTable.setWidget(0, 1, coughComboBox);
	coughFlexTable.setWidget(1, 0, lblDurationOfCough);
	coughDurationComboBox.addItem("LESS THAN 2 WEEKS");
	coughDurationComboBox.addItem("2 WEEKS OR MORE");
	coughDurationComboBox.addItem("NOT SURE");
	coughDurationComboBox.addItem("SKIP");
	coughDurationComboBox.setEnabled(false);
	coughFlexTable.setWidget(1, 1, coughDurationComboBox);
	lblIsTheClient.setWordWrap(false);
	coughFlexTable.setWidget(2, 0, lblIsTheClient);
	productiveCoughComboBox.addItem("NO");
	productiveCoughComboBox.addItem("YES");
	productiveCoughComboBox.addItem("NOT SURE");
	productiveCoughComboBox.addItem("SKIP");
	productiveCoughComboBox.setEnabled(false);
	coughFlexTable.setWidget(2, 1, productiveCoughComboBox);
	lblIsTheBlood.setWordWrap(false);
	coughFlexTable.setWidget(3, 0, lblIsTheBlood);
	haemoptysisComboBox.addItem("NO");
	haemoptysisComboBox.addItem("YES");
	haemoptysisComboBox.addItem("NOT SURE");
	haemoptysisComboBox.addItem("SKIP");
	haemoptysisComboBox.setEnabled(false);
	coughFlexTable.setWidget(3, 1, haemoptysisComboBox);
	decoratedStackPanel.add(symptomsFlexTable, "TB Symptoms", false);
	symptomsFlexTable.setSize("100%", "100%");
	lblDoesTheClient_1.setWordWrap(false);
	symptomsFlexTable.setWidget(0, 0, lblDoesTheClient_1);
	chestPainComboBox.addItem("NO");
	chestPainComboBox.addItem("YES");
	chestPainComboBox.addItem("NOT SURE");
	chestPainComboBox.addItem("SKIP");
	symptomsFlexTable.setWidget(0, 1, chestPainComboBox);
	symptomsFlexTable.setWidget(1, 1, chestPainDurationHorizontalPanel);
	chestPainDurationIntegerBox.setMaxLength(2);
	chestPainDurationIntegerBox.setText("0");
	chestPainDurationIntegerBox.setEnabled(false);
	chestPainDurationIntegerBox.setVisibleLength(2);
	chestPainDurationHorizontalPanel.add(chestPainDurationIntegerBox);
	chestPainDurationUnitComboBox.addItem("DAYS", "D");
	chestPainDurationUnitComboBox.addItem("WEEKS", "W");
	chestPainDurationUnitComboBox.addItem("MONTHS", "M");
	chestPainDurationUnitComboBox.addItem("YEARS", "Y");
	chestPainDurationUnitComboBox.setEnabled(false);
	chestPainDurationHorizontalPanel.add(chestPainDurationUnitComboBox);
	lblHasClientExperienced.setWordWrap(false);
	symptomsFlexTable.setWidget(2, 0, lblHasClientExperienced);
	breathingShortnessComboBox.addItem("NO");
	breathingShortnessComboBox.addItem("YES");
	breathingShortnessComboBox.addItem("NOT SURE");
	breathingShortnessComboBox.addItem("SKIP");
	symptomsFlexTable.setWidget(2, 1, breathingShortnessComboBox);
	symptomsFlexTable.setWidget(3, 1, breathingShortnessDurationHorizontalPanel);
	breathingShortnessDurationIntegerBox.setMaxLength(2);
	breathingShortnessDurationIntegerBox.setText("0");
	breathingShortnessDurationIntegerBox.setEnabled(false);
	breathingShortnessDurationIntegerBox.setVisibleLength(2);
	breathingShortnessDurationHorizontalPanel.add(breathingShortnessDurationIntegerBox);
	breathingShortnessDurationUnitComboBox.addItem("DAYS", "D");
	breathingShortnessDurationUnitComboBox.addItem("WEEKS", "W");
	breathingShortnessDurationUnitComboBox.addItem("MONTHS", "M");
	breathingShortnessDurationUnitComboBox.addItem("YEARS", "Y");
	breathingShortnessDurationUnitComboBox.setEnabled(false);
	breathingShortnessDurationHorizontalPanel.add(breathingShortnessDurationUnitComboBox);
	lblDoesTheClient_2.setWordWrap(false);
	symptomsFlexTable.setWidget(4, 0, lblDoesTheClient_2);
	nightSweatsComboBox.addItem("NO");
	nightSweatsComboBox.addItem("YES");
	nightSweatsComboBox.addItem("NOT SURE");
	nightSweatsComboBox.addItem("SKIP");
	symptomsFlexTable.setWidget(4, 1, nightSweatsComboBox);
	symptomsFlexTable.setWidget(5, 1, nightSweatsDurationHorizontalPanel);
	nightSweatsDurationIntegerBox.setMaxLength(2);
	nightSweatsDurationIntegerBox.setText("0");
	nightSweatsDurationIntegerBox.setEnabled(false);
	nightSweatsDurationIntegerBox.setVisibleLength(2);
	nightSweatsDurationHorizontalPanel.add(nightSweatsDurationIntegerBox);
	nightSweatsDurationUnitComboBox.addItem("DAYS", "D");
	nightSweatsDurationUnitComboBox.addItem("WEEKS", "W");
	nightSweatsDurationUnitComboBox.addItem("MONTHS", "M");
	nightSweatsDurationUnitComboBox.addItem("YEARS", "Y");
	nightSweatsDurationUnitComboBox.setEnabled(false);
	nightSweatsDurationHorizontalPanel.add(nightSweatsDurationUnitComboBox);
	lblIsClientExperiencing.setWordWrap(false);
	symptomsFlexTable.setWidget(6, 0, lblIsClientExperiencing);
	weightLossComboBox.addItem("NO");
	weightLossComboBox.addItem("YES");
	weightLossComboBox.addItem("NOT SURE");
	weightLossComboBox.addItem("SKIP");
	symptomsFlexTable.setWidget(6, 1, weightLossComboBox);
	symptomsFlexTable.setWidget(7, 1, weightLossDurationHorizontalPanel);
	weightLossDurationIntegerBox.setMaxLength(2);
	weightLossDurationIntegerBox.setText("0");
	weightLossDurationIntegerBox.setEnabled(false);
	weightLossDurationIntegerBox.setVisibleLength(2);
	weightLossDurationHorizontalPanel.add(weightLossDurationIntegerBox);
	weightLossDurationUnitComboBox.addItem("DAYS", "D");
	weightLossDurationUnitComboBox.addItem("WEEKS", "W");
	weightLossDurationUnitComboBox.addItem("MONTHS", "M");
	weightLossDurationUnitComboBox.addItem("YEARS", "Y");
	weightLossDurationUnitComboBox.setEnabled(false);
	weightLossDurationHorizontalPanel.add(weightLossDurationUnitComboBox);
	lblDoesClientHave.setWordWrap(false);
	symptomsFlexTable.setWidget(8, 0, lblDoesClientHave);
	feverComboBox.addItem("NO");
	feverComboBox.addItem("YES");
	feverComboBox.addItem("NOT SURE");
	feverComboBox.addItem("SKIP");
	symptomsFlexTable.setWidget(8, 1, feverComboBox);
	symptomsFlexTable.setWidget(9, 1, feverDurationHorizontalPanel);
	feverDurationIntegerBox.setMaxLength(2);
	feverDurationIntegerBox.setText("0");
	feverDurationIntegerBox.setEnabled(false);
	feverDurationIntegerBox.setVisibleLength(2);
	feverDurationHorizontalPanel.add(feverDurationIntegerBox);
	feverDurationUnitComboBox.addItem("DAYS", "D");
	feverDurationUnitComboBox.addItem("WEEKS", "W");
	feverDurationUnitComboBox.addItem("MONTHS", "M");
	feverDurationUnitComboBox.addItem("YEARS", "Y");
	feverDurationUnitComboBox.setEnabled(false);
	feverDurationHorizontalPanel.add(feverDurationUnitComboBox);
	symptomsFlexTable.setWidget(10, 0, lblDoesTheClient_3);
	lymphNodeComboBox.addItem("NO");
	lymphNodeComboBox.addItem("YES");
	lymphNodeComboBox.addItem("NOT SURE");
	lymphNodeComboBox.addItem("SKIP");
	symptomsFlexTable.setWidget(10, 1, lymphNodeComboBox);
	symptomsFlexTable.setWidget(11, 1, lymphNodeDurationHorizontalPanel);
	lymphNodeDurationIntegerBox.setMaxLength(2);
	lymphNodeDurationIntegerBox.setText("0");
	lymphNodeDurationIntegerBox.setVisibleLength(2);
	lymphNodeDurationHorizontalPanel.add(lymphNodeDurationIntegerBox);
	lymphNodeDurationUnitComboBox.setEnabled(false);
	lymphNodeDurationUnitComboBox.addItem("DAYS", "D");
	lymphNodeDurationUnitComboBox.addItem("WEEKS", "W");
	lymphNodeDurationUnitComboBox.addItem("MONTHS", "M");
	lymphNodeDurationUnitComboBox.addItem("YEARS", "Y");
	lymphNodeDurationHorizontalPanel.add(lymphNodeDurationUnitComboBox);
	decoratedStackPanel.add(tbHistoryFlexTable, "TB History", false);
	tbHistoryFlexTable.setSize("100%", "100%");
	lblHasTheClient.setWordWrap(false);
	tbHistoryFlexTable.setWidget(0, 0, lblHasTheClient);
	tbHistoryComboBox.addItem("NO");
	tbHistoryComboBox.addItem("YES");
	tbHistoryComboBox.addItem("NOT SURE");
	tbHistoryComboBox.addItem("REFUSED");
	tbHistoryFlexTable.setWidget(0, 1, tbHistoryComboBox);
	lblHasTheClient_1.setWordWrap(false);
	tbHistoryFlexTable.setWidget(1, 0, lblHasTheClient_1);
	treatmentHistoryComboBox.setEnabled(false);
	treatmentHistoryComboBox.addItem("NO");
	treatmentHistoryComboBox.addItem("YES");
	treatmentHistoryComboBox.addItem("NOT SURE");
	treatmentHistoryComboBox.addItem("REFUSED");
	treatmentHistoryComboBox.addItem("SKIP");
	tbHistoryFlexTable.setWidget(1, 1, treatmentHistoryComboBox);
	lblWhereWasThe.setWordWrap(false);
	tbHistoryFlexTable.setWidget(2, 0, lblWhereWasThe);
	treatmentCentreTextBox.setMaxLength(50);
	treatmentCentreTextBox.setName("patient;treatment_centre");
	treatmentCentreTextBox.setEnabled(false);
	tbHistoryFlexTable.setWidget(2, 1, treatmentCentreTextBox);
	durationFormTreatmentToDateCheckBox.setEnabled(false);
	durationFormTreatmentToDateCheckBox.setWordWrap(false);
	tbHistoryFlexTable.setWidget(3, 0, durationFormTreatmentToDateCheckBox);
	tbHistoryFlexTable.setWidget(3, 1, treatmentHistoryDurationHorizontalPanel);
	treatmentHistoryDurationIntegerBox.setMaxLength(2);
	treatmentHistoryDurationIntegerBox.setText("0");
	treatmentHistoryDurationIntegerBox.setEnabled(false);
	treatmentHistoryDurationIntegerBox.setVisibleLength(2);
	treatmentHistoryDurationHorizontalPanel.add(treatmentHistoryDurationIntegerBox);
	treatmentHistoryDurationUnitComboBox.setEnabled(false);
	treatmentHistoryDurationUnitComboBox.addItem("DAYS", "D");
	treatmentHistoryDurationUnitComboBox.addItem("WEEKS", "W");
	treatmentHistoryDurationUnitComboBox.addItem("MONTHS", "M");
	treatmentHistoryDurationUnitComboBox.addItem("YEARS", "Y");
	treatmentHistoryDurationHorizontalPanel.add(treatmentHistoryDurationUnitComboBox);
	durationOfTreatmentCheckBox.setEnabled(false);
	tbHistoryFlexTable.setWidget(4, 0, durationOfTreatmentCheckBox);
	tbHistoryFlexTable.setWidget(4, 1, treatmentDurationHorizontalPanel);
	treatmentDurationIntegerBox.setMaxLength(2);
	treatmentDurationIntegerBox.setText("0");
	treatmentDurationIntegerBox.setEnabled(false);
	treatmentDurationIntegerBox.setVisibleLength(2);
	treatmentDurationHorizontalPanel.add(treatmentDurationIntegerBox);
	treatmentDurationUnitComboBox.setEnabled(false);
	treatmentDurationUnitComboBox.addItem("DAYS", "D");
	treatmentDurationUnitComboBox.addItem("WEEKS", "W");
	treatmentDurationUnitComboBox.addItem("MONTHS", "M");
	treatmentDurationHorizontalPanel.add(treatmentDurationUnitComboBox);
	tbHistoryFlexTable.setWidget(5, 0, lblDidTheClient);
	treatmentCompletedComboBox.addItem("NO");
	treatmentCompletedComboBox.addItem("YES");
	treatmentCompletedComboBox.addItem("NOT SURE");
	treatmentCompletedComboBox.addItem("REFUSED");
	treatmentCompletedComboBox.addItem("SKIP");
	treatmentCompletedComboBox.setEnabled(false);
	tbHistoryFlexTable.setWidget(5, 1, treatmentCompletedComboBox);
	tbHistoryFlexTable.setWidget(6, 0, lblDoesAnyoneIn);
	familyTBHistoryComboBox.addItem("NO");
	familyTBHistoryComboBox.addItem("YES");
	familyTBHistoryComboBox.addItem("NOT SURE");
	familyTBHistoryComboBox.addItem("REFUSED");
	familyTBHistoryComboBox.addItem("SKIP");
	tbHistoryFlexTable.setWidget(6, 1, familyTBHistoryComboBox);
	middleVerticalPanel.add(conclusionCheckBox);
	middleVerticalPanel.add(grid);
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

    /**
     * Create event handlers for form widgets
     */
    public void createHandlers()
    {
	ListBox[] listBoxes = { coughComboBox, productiveCoughComboBox, chestPainComboBox, breathingShortnessComboBox, nightSweatsComboBox, weightLossComboBox, feverComboBox, lymphNodeComboBox,
	tbHistoryComboBox, treatmentHistoryComboBox };
	for (int i = 0; i < listBoxes.length; i++)
	    listBoxes[i].addChangeHandler(this);
	CheckBox[] checkBoxes = { durationFormTreatmentToDateCheckBox, durationOfTreatmentCheckBox, conclusionCheckBox };
	for (int i = 0; i < checkBoxes.length; i++)
	    checkBoxes[i].addValueChangeHandler(this);
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
	IntegerBox[] integerBoxes = { chestPainDurationIntegerBox, breathingShortnessDurationIntegerBox, nightSweatsDurationIntegerBox, weightLossDurationIntegerBox, feverDurationIntegerBox,
	lymphNodeDurationIntegerBox, treatmentHistoryDurationIntegerBox, treatmentDurationIntegerBox };
	ListBox[] listBoxes = { coughComboBox, coughDurationComboBox, productiveCoughComboBox, haemoptysisComboBox, chestPainComboBox, chestPainDurationUnitComboBox, breathingShortnessComboBox,
	breathingShortnessDurationUnitComboBox, nightSweatsComboBox, nightSweatsDurationUnitComboBox, weightLossComboBox, weightLossDurationUnitComboBox, feverComboBox, feverDurationUnitComboBox,
	lymphNodeComboBox, lymphNodeDurationUnitComboBox, tbHistoryComboBox, treatmentHistoryComboBox, treatmentHistoryDurationUnitComboBox, treatmentDurationUnitComboBox, treatmentCompletedComboBox,
	familyTBHistoryComboBox };
	CheckBox[] checkBoxes = { durationFormTreatmentToDateCheckBox, durationOfTreatmentCheckBox, conclusionCheckBox };
	for (int i = 0; i < integerBoxes.length; i++)
	    integerBoxes[i].setText("0");
	for (int i = 0; i < listBoxes.length; i++)
	    listBoxes[i].setSelectedIndex(0);
	for (int i = 0; i < checkBoxes.length; i++)
	    checkBoxes[i].setValue(false);
	coughDurationComboBox.setEnabled(false);
	productiveCoughComboBox.setEnabled(false);
	haemoptysisComboBox.setEnabled(false);
	chestPainDurationIntegerBox.setEnabled(false);
	chestPainDurationUnitComboBox.setEnabled(false);
	breathingShortnessDurationIntegerBox.setEnabled(false);
	breathingShortnessDurationUnitComboBox.setEnabled(false);
	nightSweatsDurationIntegerBox.setEnabled(false);
	nightSweatsDurationUnitComboBox.setEnabled(false);
	weightLossDurationIntegerBox.setEnabled(false);
	weightLossDurationUnitComboBox.setEnabled(false);
	feverDurationIntegerBox.setEnabled(false);
	feverDurationUnitComboBox.setEnabled(false);
	lymphNodeDurationIntegerBox.setEnabled(false);
	lymphNodeDurationUnitComboBox.setEnabled(false);
	treatmentHistoryComboBox.setEnabled(false);
	treatmentCentreTextBox.setEnabled(false);
	durationFormTreatmentToDateCheckBox.setEnabled(false);
	treatmentHistoryDurationIntegerBox.setEnabled(false);
	treatmentHistoryDurationUnitComboBox.setEnabled(false);
	durationOfTreatmentCheckBox.setEnabled(false);
	treatmentDurationIntegerBox.setEnabled(false);
	treatmentDurationUnitComboBox.setEnabled(false);
	treatmentCompletedComboBox.setEnabled(false);
    }

    /**
     * To validate data in widgets before performing DML operations
     * 
     * @return
     */
    public boolean validate()
    {
	valid = true;
	StringBuilder errorMessage = new StringBuilder();
	/* Validate mandatory fields */
	if (IRZClient.get(clientIdTextBox).equals(""))
	    errorMessage.append("Client ID: " + CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
	if (IRZClient.get(chestPainComboBox).equals("YES") && chestPainDurationIntegerBox.getText().equals(""))
	    errorMessage.append("Duration of Chest Pain: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	if (IRZClient.get(breathingShortnessComboBox).equals("YES") && breathingShortnessDurationIntegerBox.getText().equals(""))
	    errorMessage.append("Duration of Breathing Shortness: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	if (IRZClient.get(nightSweatsComboBox).equals("YES") && nightSweatsDurationIntegerBox.getText().equals(""))
	    errorMessage.append("Duration of Night Sweats: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	if (IRZClient.get(weightLossComboBox).equals("YES") && weightLossDurationIntegerBox.getText().equals(""))
	    errorMessage.append("Duration of Weight Loss: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	if (IRZClient.get(feverComboBox).equals("YES") && feverDurationIntegerBox.getText().equals(""))
	    errorMessage.append("Duration of Fever: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	if (IRZClient.get(lymphNodeComboBox).equals("YES") && lymphNodeDurationIntegerBox.getText().equals(""))
	    errorMessage.append("Duration of Lymph Node: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	valid = errorMessage.length() == 0;
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
	    Date enteredDate = new Date();
	    int eId = 0;
	    String clientId = IRZClient.get(clientIdTextBox).toUpperCase();
	    String pid2 = IRZ.getCurrentUserName();

	    EncounterId encounterId = new EncounterId(0, clientId, pid2, formName);
	    Encounter encounter = new Encounter(encounterId, IRZ.getCurrentLocation());
	    encounter.setLocationId(IRZ.getCurrentLocation());
	    encounter.setDateEntered(enteredDate);
	    encounter.setDateStart(new Date());
	    encounter.setDateEnd(new Date());
	    ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults>();
	    // Cough Details
	    String cough = IRZClient.get(coughComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "COUGH"), cough));
	    if (cough.equals("YES"))
	    {
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "COUGH_DURATION"), IRZClient.get(coughDurationComboBox)));
		String productive = IRZClient.get(productiveCoughComboBox);
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "PROD_COUGH"), productive));
		if (productive.equals("YES"))
		{
		    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "HAEMOPTYSIS"), IRZClient.get(haemoptysisComboBox)));
		}
	    }
	    // TB Symptoms
	    String chestPain = IRZClient.get(chestPainComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "CHEST_PAIN"), chestPain));
	    if (chestPain.equals("YES"))
	    {
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "CHEST_PAIN_DUR"), IRZClient.get(chestPainDurationIntegerBox)));
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "CHEST_PAIN_UNIT"), IRZClient.get(chestPainDurationUnitComboBox)));
	    }
	    String breathingShortness = IRZClient.get(breathingShortnessComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "BREATH_SHORT"), breathingShortness));
	    if (breathingShortness.equals("YES"))
	    {
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "BREATH_SHORT_DUR"), IRZClient.get(breathingShortnessDurationIntegerBox)));
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "BREATH_SHORT_UNIT"), IRZClient.get(breathingShortnessDurationUnitComboBox)));
	    }
	    String nightSweat = IRZClient.get(nightSweatsComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "NIGHT_SWEAT"), nightSweat));
	    if (nightSweat.equals("YES"))
	    {
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "NIGHT_SWEAT_DUR"), IRZClient.get(nightSweatsDurationIntegerBox)));
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "NIGHT_SWEAT_UNIT"), IRZClient.get(nightSweatsDurationUnitComboBox)));
	    }
	    String weightLoss = IRZClient.get(weightLossComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "WEIGHT_LOSS"), weightLoss));
	    if (weightLoss.equals("YES"))
	    {
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "WEIGHT_LOSS_DUR"), IRZClient.get(weightLossDurationIntegerBox)));
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "WEIGHT_LOSS_UNIT"), IRZClient.get(weightLossDurationUnitComboBox)));
	    }
	    String fever = IRZClient.get(feverComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "FEVER"), fever));
	    if (fever.equals("YES"))
	    {
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "FEVER_DUR"), IRZClient.get(feverDurationIntegerBox)));
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "FEVER_UNIT"), IRZClient.get(feverDurationUnitComboBox)));
	    }
	    String lymphNode = IRZClient.get(lymphNodeComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "LYMPH_NODE"), lymphNode));
	    if (lymphNode.equals("YES"))
	    {
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "LYMPH_NODE_DUR"), IRZClient.get(lymphNodeDurationIntegerBox)));
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "LYMPH_NODE_UNIT"), IRZClient.get(lymphNodeDurationUnitComboBox)));
	    }
	    // TB History
	    String tbHistory = IRZClient.get(tbHistoryComboBox);
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TB_HISTORY"), tbHistory));
	    if (tbHistory.equals("YES"))
	    {
		String treatmentHistory = IRZClient.get(treatmentHistoryComboBox);
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TREAT_HISTORY"), treatmentHistory));
		if (treatmentHistory.equals("YES"))
		{
		    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TREAT_HISTORY_DUR"), IRZClient.get(treatmentHistoryDurationIntegerBox)));
		    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TREAT_HISTORY_UNIT"), IRZClient.get(treatmentHistoryDurationUnitComboBox)));
		    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TREAT_DUR"), IRZClient.get(treatmentDurationIntegerBox)));
		    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TREAT_UNIT"), IRZClient.get(treatmentDurationUnitComboBox)));
		    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TREATMENT_CENTRE"), IRZClient.get(treatmentCentreTextBox).toUpperCase()));
		    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TREATMENT_COMP"), IRZClient.get(treatmentCompletedComboBox)));
		}
	    }
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "TB_FAMILY_HISTORY"), IRZClient.get(familyTBHistoryComboBox)));
	    encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "IS_SUSPECT"), conclusionCheckBox.getValue() ? "Y":"N"));
	    // Set Patient attributes
	    currentPatient.setDateScreened(enteredDate);
	    if (conclusionCheckBox.getValue())
	    {
		currentPatient.setDateSuspected(enteredDate);
		if (currentPatient.getDateSuspected() != null)
		    currentPatient.setDiseaseSuspected("TB");
		/*if(currentPatient.getDiseaseSuspected()!=null)
		{
		    String diseaseSuspected = currentPatient.getDiseaseSuspected() + ";TB";
		    currentPatient.setDiseaseSuspected(diseaseSuspected);
		}*/
		else if(currentPatient.getDiseaseSuspected()==null)
		{
		    currentPatient.setDiseaseSuspected("TB");
		}
		/*else
		    currentPatient.setDiseaseSuspected("TB");*/

		currentPatient.setPatientStatus("SUSPECT");
		currentPatient.setSuspectedBy(IRZ.getCurrentUserName());
	    }
	    service.saveTBScreening(currentPatient, encounter, encounterResults.toArray(new EncounterResults[] {}), new AsyncCallback<String>() {
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
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == checkIdButton)
	{
	    load(false);
	    final String clientId = IRZClient.get(clientIdTextBox);
	    try
	    {//have the demographics been entered for this client
		service.exists("person", "pid='" + IRZClient.get(clientIdTextBox) + "'", new AsyncCallback<Boolean>() {
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
				    if (currentPatient == null)
				    {
					Window.alert(CustomMessage.getErrorMessage(ErrorType.ID_INVALID));
					return;
				    }
				    else if (currentPatient.getDiseaseSuspected() !=null && currentPatient.getDiseaseSuspected().equals("TB"))
				    {
					Window.alert(CustomMessage.getErrorMessage(ErrorType.DUPLICATION_ERROR));
					return;
				    }
				    else
					Window.alert(CustomMessage.getInfoMessage(InfoType.ID_VALID));
				}
				@Override
				public void onFailure(Throwable caught)
				{

				    load(false);
				    caught.printStackTrace();

				}
				});
			    } catch(Exception e)
			    {
				load(false);
				e.printStackTrace();
			    }
			}
			else
			{
			    Window.alert("Enter Client Demographics:"+CustomMessage.getErrorMessage(ErrorType.ID_INVALID));
			    return;
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
	else if (sender == saveButton)
	{
	    try
	    {
		service.findPatient(IRZClient.get(clientIdTextBox), new AsyncCallback<Patient>() {
		    public void onSuccess(Patient result)
		    {
			currentPatient = result;
			if (result != null)
			{
			    boolean doIt = false;
			    // Recommend User to mark as suspect if sever
			    // symptoms are found
			    if (!conclusionCheckBox.getValue())
			    {
				boolean hasCough = IRZClient.get(coughComboBox).equals("YES");
				boolean productive = IRZClient.get(productiveCoughComboBox).equals("YES");
				boolean haemoptysis = IRZClient.get(haemoptysisComboBox).equals("YES");
				boolean breathingShortness = IRZClient.get(breathingShortnessComboBox).equals("YES");
				boolean nightSweats = IRZClient.get(nightSweatsComboBox).equals("YES");
				boolean weightLoss = IRZClient.get(weightLossComboBox).equals("YES");
				boolean fever = IRZClient.get(feverComboBox).equals("YES");
				boolean lymphNode = IRZClient.get(lymphNodeComboBox).equals("YES");
				boolean tbHistory = IRZClient.get(tbHistoryComboBox).equals("YES");
				boolean familyHistory = IRZClient.get(familyTBHistoryComboBox).equals("YES");
				boolean recommended = hasCough & productive;
				recommended = recommended | haemoptysis;
				recommended = recommended | (hasCough & (breathingShortness | nightSweats | weightLoss | fever | lymphNode));
				recommended = recommended | (tbHistory | familyHistory);
				if (recommended)
				    doIt = Window.confirm("There are several indicators of TB, it is strongly recommended that you mark the Client as a TB Suspect.");
			    }
			    if (!doIt)
				saveData();
			}
			else
			    Window.alert(CustomMessage.getErrorMessage(ErrorType.ITEM_NOT_FOUND) + "\nPlease make sure you have entered correct Client ID.");
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
	else if (sender == closeButton)
	{
	    MainMenuComposite.clear();
	}
    }

    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event)
    {
	Widget sender = (Widget) event.getSource();
	if (sender instanceof CheckBox)
	{
	    CheckBox checkBox = (CheckBox) sender;
	    boolean choice = checkBox.getValue();
	    if (sender == durationFormTreatmentToDateCheckBox)
	    {
		treatmentHistoryDurationIntegerBox.setEnabled(choice);
		treatmentHistoryDurationUnitComboBox.setEnabled(choice);
	    }
	    else if (sender == durationOfTreatmentCheckBox)
	    {
		treatmentDurationIntegerBox.setEnabled(choice);
		treatmentDurationUnitComboBox.setEnabled(choice);
	    }
	}
    }

    @Override
    public void onChange(ChangeEvent event)
    {
	Widget sender = (Widget) event.getSource();
	if (sender == coughComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    coughDurationComboBox.setEnabled(choice);
	    productiveCoughComboBox.setEnabled(choice);
	}
	else if (sender == productiveCoughComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    haemoptysisComboBox.setEnabled(choice);
	}
	else if (sender == chestPainComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    chestPainDurationIntegerBox.setEnabled(choice);
	    chestPainDurationUnitComboBox.setEnabled(choice);
	}
	else if (sender == breathingShortnessComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    breathingShortnessDurationIntegerBox.setEnabled(choice);
	    breathingShortnessDurationUnitComboBox.setEnabled(choice);
	}
	else if (sender == nightSweatsComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    nightSweatsDurationIntegerBox.setEnabled(choice);
	    nightSweatsDurationUnitComboBox.setEnabled(choice);
	}
	else if (sender == weightLossComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    weightLossDurationIntegerBox.setEnabled(choice);
	    weightLossDurationUnitComboBox.setEnabled(choice);
	}
	else if (sender == feverComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    feverDurationIntegerBox.setEnabled(choice);
	    feverDurationUnitComboBox.setEnabled(choice);
	}
	else if (sender == lymphNodeComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    lymphNodeDurationIntegerBox.setEnabled(choice);
	    lymphNodeDurationUnitComboBox.setEnabled(choice);
	}
	else if (sender == tbHistoryComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    treatmentHistoryComboBox.setEnabled(choice);
	}
	else if (sender == treatmentHistoryComboBox)
	{
	    boolean choice = IRZClient.get(sender).equals("YES");
	    treatmentCentreTextBox.setEnabled(choice);
	    durationFormTreatmentToDateCheckBox.setEnabled(choice);
	    durationOfTreatmentCheckBox.setEnabled(choice);
	    treatmentCompletedComboBox.setEnabled(choice);
	    if (!choice)
	    {
		durationFormTreatmentToDateCheckBox.setValue(choice);
		durationOfTreatmentCheckBox.setValue(choice);
		treatmentHistoryDurationIntegerBox.setEnabled(choice);
		treatmentHistoryDurationUnitComboBox.setEnabled(choice);
		treatmentDurationIntegerBox.setEnabled(choice);
		treatmentDurationUnitComboBox.setEnabled(choice);
	    }
	}
    }
}
