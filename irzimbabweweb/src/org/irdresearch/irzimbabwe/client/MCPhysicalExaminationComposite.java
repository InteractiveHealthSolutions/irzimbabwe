
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
import org.irdresearch.irzimbabwe.shared.model.Patient;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.IntegerBox;

/**
 * MC Client Physical Examination form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MCPhysicalExaminationComposite extends Composite implements ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service											= GWT.create (ServerService.class);
	private static final String			formName										= "MC_EXAM";

	private UserRightsUtil				rights											= new UserRightsUtil ();
	private Patient						currentPatient;
	private String						clientId										= "";
	private boolean						valid;

	private FlexTable					flexTable										= new FlexTable ();
	private FlexTable					topFlexTable									= new FlexTable ();
	private FlexTable					clientIdFlexTable								= new FlexTable ();
	private FlexTable					clinicalAbnormalitiesFlexTable					= new FlexTable ();
	private FlexTable					genitalExaminationFlexTable						= new FlexTable ();

	private DecoratedStackPanel			decoratedStackPanel								= new DecoratedStackPanel ();
	private Grid						grid											= new Grid (1, 2);
	private Grid						clinicalAbnormalitiesGrid						= new Grid (5, 2);
	private Grid						genitalAbnormalitiesGrid						= new Grid (4, 1);
	private VerticalPanel				middleVerticalPanel								= new VerticalPanel ();
	private HorizontalPanel				clientIdHorizontalPanel							= new HorizontalPanel ();
	private HorizontalPanel				bloodPressureHorizontalPanel					= new HorizontalPanel ();

	private Button						checkIdButton									= new Button ("Check");
	private Button						saveButton										= new Button ("Save");
	private Button						closeButton										= new Button ("Close");

	private Label						lblClientsInitialDemographics					= new Label ("MC Physical Examination Form");
	private Label						lblClientsId									= new Label ("Client's ID:");
	private Label						lblClinicalAbnormalities						= new Label ("Check all applicable:");
	private Label						lblBloodPressure								= new Label ("Blood Pressure:");
	private Label						lblOver											= new Label ("over");
	private Label						lblSelectAllApplicable							= new Label ("Select all applicable:");
	private Label						lblIndicateWhoHiv								= new Label ("WHO HIV Clinical Stage:");
	private Label						lblCdCellCount									= new Label ("CD4 Cell Count:");

	private TextBox						clientIdTextBox									= new TextBox ();
	private TextBox						abnormalityTextBox								= new TextBox ();
	private TextBox						otherGenitalAbnormalityTextBox					= new TextBox ();

	private IntegerBox					lowerBloodPressureIntegerBox					= new IntegerBox ();
	private IntegerBox					higherBloodPressureIntegerBox					= new IntegerBox ();
	private IntegerBox					cd4CellCountIntegerBox							= new IntegerBox ();

	private CheckBox					clientIsSuitableCheckBox						= new CheckBox ("Client is suitable for MC");
	private CheckBox					clinicalAbnormalityHeartCheckBox				= new CheckBox ("Heart");
	private CheckBox					clinicalAbnormalityLungsCheckBox				= new CheckBox ("Lungs");
	private CheckBox					clinicalAbnormalityAbdomenCheckBox				= new CheckBox ("Abdomen");
	private CheckBox					clinicalAbnormalityHeadCheckBox					= new CheckBox ("Head");
	private CheckBox					clinicalAbnormalityNeckCheckBox					= new CheckBox ("Neck");
	private CheckBox					clinicalAbnormalityMouthCheckBox				= new CheckBox ("Mouth");
	private CheckBox					clinicalAbnormalityThroatCheckBox				= new CheckBox ("Throat");
	private CheckBox					clinicalAbnormalityEarsCheckBox					= new CheckBox ("Ears");
	private CheckBox					clinicalAbnormalityLimbsCheckBox				= new CheckBox ("Limbs");
	private CheckBox					otherClinicalAbnormalityCheckBox				= new CheckBox ("Specify Other:");
	private CheckBox					genitalAbnormalityUrethralDischargeCheckBox		= new CheckBox ("Urethral Discharge");
	private CheckBox					genitalAbnormalityGenitalUlcerCheckBox			= new CheckBox ("Genital Ulcer");
	private CheckBox					genitalAbnormalitySwellingOfScrotumCheckBox		= new CheckBox ("Swelling of Scrotum");
	private CheckBox					genitalAbnormalityIrretractibleForeskinCheckBox	= new CheckBox ("Irretractible Foreskin");
	private CheckBox					otherGenitalAbnormalityCheckBox					= new CheckBox ("Specify Other:");

	private ListBox						hivStageComboBox								= new ListBox ();

	public MCPhysicalExaminationComposite ()
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
		clientIdFlexTable.setWidget (0, 1, clientIdHorizontalPanel);
		clientIdTextBox.setVisibleLength (12);
		clientIdTextBox.setMaxLength (12);
		clientIdHorizontalPanel.add (clientIdTextBox);
		clientIdTextBox.setName ("patient;patient_id");
		checkIdButton.setText ("Check");
		clientIdHorizontalPanel.add (checkIdButton);
		checkIdButton.setWidth ("100%");
		middleVerticalPanel.add (decoratedStackPanel);
		decoratedStackPanel.add (clinicalAbnormalitiesFlexTable, "Clinical Abnormalities", false);
		clinicalAbnormalitiesFlexTable.setSize ("100%", "100%");
		clinicalAbnormalitiesFlexTable.setWidget (0, 0, lblClinicalAbnormalities);
		lblClinicalAbnormalities.setWordWrap (false);
		clinicalAbnormalitiesFlexTable.setWidget (0, 1, clinicalAbnormalitiesGrid);
		clinicalAbnormalitiesGrid.setWidget (0, 0, clinicalAbnormalityHeartCheckBox);
		clinicalAbnormalitiesGrid.setWidget (0, 1, clinicalAbnormalityLungsCheckBox);
		clinicalAbnormalitiesGrid.setWidget (1, 0, clinicalAbnormalityAbdomenCheckBox);
		clinicalAbnormalitiesGrid.setWidget (1, 1, clinicalAbnormalityHeadCheckBox);
		clinicalAbnormalitiesGrid.setWidget (2, 0, clinicalAbnormalityNeckCheckBox);
		clinicalAbnormalitiesGrid.setWidget (2, 1, clinicalAbnormalityMouthCheckBox);
		clinicalAbnormalitiesGrid.setWidget (3, 0, clinicalAbnormalityThroatCheckBox);
		clinicalAbnormalitiesGrid.setWidget (3, 1, clinicalAbnormalityEarsCheckBox);
		clinicalAbnormalitiesGrid.setWidget (4, 0, clinicalAbnormalityLimbsCheckBox);
		clinicalAbnormalitiesFlexTable.setWidget (1, 0, otherClinicalAbnormalityCheckBox);
		abnormalityTextBox.setEnabled (false);
		abnormalityTextBox.setMaxLength (50);
		clinicalAbnormalitiesFlexTable.setWidget (1, 1, abnormalityTextBox);
		lblBloodPressure.setWordWrap (false);
		clinicalAbnormalitiesFlexTable.setWidget (2, 0, lblBloodPressure);
		clinicalAbnormalitiesFlexTable.setWidget (2, 1, bloodPressureHorizontalPanel);
		lowerBloodPressureIntegerBox.setVisibleLength (3);
		bloodPressureHorizontalPanel.add (lowerBloodPressureIntegerBox);
		bloodPressureHorizontalPanel.add (lblOver);
		bloodPressureHorizontalPanel.setCellVerticalAlignment (lblOver, HasVerticalAlignment.ALIGN_MIDDLE);
		higherBloodPressureIntegerBox.setVisibleLength (3);
		bloodPressureHorizontalPanel.add (higherBloodPressureIntegerBox);
		clinicalAbnormalitiesFlexTable.getCellFormatter ().setVerticalAlignment (0, 0, HasVerticalAlignment.ALIGN_TOP);
		decoratedStackPanel.add (genitalExaminationFlexTable, "Genital Examination", false);
		genitalExaminationFlexTable.setSize ("100%", "100%");
		lblSelectAllApplicable.setWordWrap (false);
		genitalExaminationFlexTable.setWidget (0, 0, lblSelectAllApplicable);
		genitalExaminationFlexTable.setWidget (0, 1, genitalAbnormalitiesGrid);
		genitalAbnormalitiesGrid.setWidget (0, 0, genitalAbnormalityUrethralDischargeCheckBox);
		genitalAbnormalitiesGrid.setWidget (1, 0, genitalAbnormalityGenitalUlcerCheckBox);
		genitalAbnormalitiesGrid.setWidget (2, 0, genitalAbnormalitySwellingOfScrotumCheckBox);
		genitalAbnormalitiesGrid.setWidget (3, 0, genitalAbnormalityIrretractibleForeskinCheckBox);
		genitalExaminationFlexTable.setWidget (1, 0, otherGenitalAbnormalityCheckBox);
		otherGenitalAbnormalityTextBox.setEnabled (false);
		otherGenitalAbnormalityTextBox.setMaxLength (50);
		genitalExaminationFlexTable.setWidget (1, 1, otherGenitalAbnormalityTextBox);
		lblIndicateWhoHiv.setWordWrap (false);
		genitalExaminationFlexTable.setWidget (2, 0, lblIndicateWhoHiv);
		hivStageComboBox.setEnabled (false);
		hivStageComboBox.addItem ("I");
		hivStageComboBox.addItem ("II");
		hivStageComboBox.addItem ("III");
		hivStageComboBox.addItem ("IV");
		hivStageComboBox.addItem ("SKIPPED");
		genitalExaminationFlexTable.setWidget (2, 1, hivStageComboBox);
		genitalExaminationFlexTable.setWidget (3, 0, lblCdCellCount);
		cd4CellCountIntegerBox.setEnabled (false);
		cd4CellCountIntegerBox.setVisibleLength (3);
		genitalExaminationFlexTable.setWidget (3, 1, cd4CellCountIntegerBox);
		genitalExaminationFlexTable.setWidget (4, 1, clientIsSuitableCheckBox);
		genitalExaminationFlexTable.getCellFormatter ().setVerticalAlignment (0, 0, HasVerticalAlignment.ALIGN_TOP);
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

	public void createHandlers ()
	{
		ListBox[] listBoxes = {};
		for (int i = 0; i < listBoxes.length; i++)
			listBoxes[i].addChangeHandler (this);
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
		IRZClient.clearControls (topFlexTable);
		ListBox[] listBoxes = {};
		for (int i = 0; i < listBoxes.length; i++)
			listBoxes[i].setSelectedIndex (0);
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
		if (IRZClient.get (clientIdTextBox).equals (""))
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

			service.saveReferral (null, encounter, encounterResults.toArray (new EncounterResults[] {}), new AsyncCallback<String> ()
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
				clientId = IRZClient.get (clientIdTextBox);
				service.findPatient (clientId, new AsyncCallback<Patient> ()
				{
					public void onSuccess (Patient result)
					{
						currentPatient = result;
						if (currentPatient != null && currentPatient.getDiseaseSuspected ().equals ("TB"))
						{
							Window.alert (CustomMessage.getInfoMessage (InfoType.ID_VALID));
						}
						else
							Window.alert (CustomMessage.getErrorMessage (ErrorType.ID_INVALID));
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
			if (currentPatient == null || clientId == "")
			{
				Window.alert ("Please first check Client's ID.");
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
	public void onChange (ChangeEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == null)
		{
			try
			{
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				load (false);
			}
		}
	}
}
