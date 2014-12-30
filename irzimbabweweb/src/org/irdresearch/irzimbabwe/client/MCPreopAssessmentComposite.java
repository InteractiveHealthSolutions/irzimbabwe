/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

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

/**
 * MC Client Pre-operation Assessment form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MCPreopAssessmentComposite extends Composite implements ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service										= GWT.create (ServerService.class);
	private static final String			formName									= "MC_PREOP";

	private UserRightsUtil				rights										= new UserRightsUtil ();
	private Patient						currentPatient;
	private String						clientId									= "";
	private boolean						valid;

	private FlexTable					flexTable									= new FlexTable ();
	private FlexTable					topFlexTable								= new FlexTable ();
	private FlexTable					clientIdFlexTable							= new FlexTable ();

	private Grid						grid										= new Grid (1, 2);
	private Grid						diseaseHistoryGrid							= new Grid (11, 1);
	private VerticalPanel				middleVerticalPanel							= new VerticalPanel ();
	private HorizontalPanel				clientIdHorizontalPanel						= new HorizontalPanel ();

	private Button						checkIdButton								= new Button ("Check");
	private Button						saveButton									= new Button ("Save");
	private Button						closeButton									= new Button ("Close");

	private Label						lblClientsInitialDemographics				= new Label ("MC Pre-operation Assessment Form");
	private Label						lblClientsId								= new Label ("Client's ID:");

	private Label						lblClientsHistoryOf							= new Label ("Client's History of Diseases:");
	private Label						lblIsClientTaking							= new Label ("Is Client Taking Medication?");
	private Label						lblSpecifyOther_1							= new Label ("Specify Other:");
	private Label						lblDidClientHave							= new Label ("Did Client have any Surgical Operation?");

	private TextBox						clientIdTextBox								= new TextBox ();
	private TextBox						otherDiseaseTextBox							= new TextBox ();
	private TextBox						otherMedicationTextBox						= new TextBox ();

	private CheckBox					diseaseHistoryHemophiliaCheckBox			= new CheckBox ("Hemophilia or Other Bleeding Disorder");
	private CheckBox					diseaseHistoryAnemiaCheckBox				= new CheckBox ("Anemia");
	private CheckBox					diseaseHistoryDiabetesMellitusCheckBox		= new CheckBox ("Diabetes Mellitus");
	private CheckBox					diseaseHistoryArterialHypertensionCheckBox	= new CheckBox ("Arterial Hypertension");
	private CheckBox					diseaseHistoryHeartConditionCheckBox		= new CheckBox ("Heart Condition");
	private CheckBox					diseaseHistoryLungConditionsCheckBox		= new CheckBox ("Lung Conditions (Asthema/Emphysema/Pneumonia etc.)");
	private CheckBox					diseaseHistoryAllergiesCheckBox				= new CheckBox ("Allergies");
	private CheckBox					diseaseHistoryKidneyProblemsCheckBox		= new CheckBox ("Kidney Problems");
	private CheckBox					diseaseHistoryNeurologicalProblemsCheckBox	= new CheckBox ("Neurological Problems");
	private CheckBox					diseaseHistoryCancerCheckBox				= new CheckBox ("Cancer");
	private CheckBox					diseaseHistoryThyroidProblemsCheckBox		= new CheckBox ("Thyroid Problems");
	private CheckBox					chckbxSpecifyOther							= new CheckBox ("Specify Other:");

	private ListBox						medicationComboBox							= new ListBox ();
	private ListBox						surgicalOperationComboBox					= new ListBox ();

	public MCPreopAssessmentComposite ()
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
		clientIdTextBox.setMaxLength (12);
		clientIdTextBox.setVisibleLength (12);
		clientIdHorizontalPanel.add (clientIdTextBox);
		clientIdTextBox.setName ("patient;patient_id");
		clientIdHorizontalPanel.add (checkIdButton);
		checkIdButton.setWidth ("100%");
		clientIdFlexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		lblClientsHistoryOf.setWordWrap (false);
		clientIdFlexTable.setWidget (1, 0, lblClientsHistoryOf);
		clientIdFlexTable.setWidget (1, 1, diseaseHistoryGrid);
		diseaseHistoryHemophiliaCheckBox.setWordWrap (false);
		diseaseHistoryHemophiliaCheckBox.setHTML ("Hemophilia or other Bleeding Disorder");
		diseaseHistoryGrid.setWidget (0, 0, diseaseHistoryHemophiliaCheckBox);
		diseaseHistoryAnemiaCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (1, 0, diseaseHistoryAnemiaCheckBox);
		diseaseHistoryDiabetesMellitusCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (2, 0, diseaseHistoryDiabetesMellitusCheckBox);
		diseaseHistoryArterialHypertensionCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (3, 0, diseaseHistoryArterialHypertensionCheckBox);
		diseaseHistoryHeartConditionCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (4, 0, diseaseHistoryHeartConditionCheckBox);
		diseaseHistoryLungConditionsCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (5, 0, diseaseHistoryLungConditionsCheckBox);
		diseaseHistoryAllergiesCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (6, 0, diseaseHistoryAllergiesCheckBox);
		diseaseHistoryKidneyProblemsCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (7, 0, diseaseHistoryKidneyProblemsCheckBox);
		diseaseHistoryNeurologicalProblemsCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (8, 0, diseaseHistoryNeurologicalProblemsCheckBox);
		diseaseHistoryCancerCheckBox.setWordWrap (false);
		diseaseHistoryGrid.setWidget (9, 0, diseaseHistoryCancerCheckBox);
		diseaseHistoryGrid.setWidget (10, 0, diseaseHistoryThyroidProblemsCheckBox);
		clientIdFlexTable.setWidget (2, 0, chckbxSpecifyOther);
		otherDiseaseTextBox.setMaxLength (50);
		otherDiseaseTextBox.setEnabled (false);
		clientIdFlexTable.setWidget (2, 1, otherDiseaseTextBox);
		clientIdFlexTable.setWidget (3, 0, lblIsClientTaking);
		medicationComboBox.addItem ("NO");
		medicationComboBox.addItem ("YES");
		medicationComboBox.addItem ("REFUSED");
		clientIdFlexTable.setWidget (3, 1, medicationComboBox);
		clientIdFlexTable.setWidget (4, 0, lblSpecifyOther_1);
		otherMedicationTextBox.setMaxLength (50);
		otherMedicationTextBox.setEnabled (false);
		clientIdFlexTable.setWidget (4, 1, otherMedicationTextBox);
		lblDidClientHave.setWordWrap (false);
		clientIdFlexTable.setWidget (5, 0, lblDidClientHave);
		surgicalOperationComboBox.addItem ("NO");
		surgicalOperationComboBox.addItem ("YES");
		surgicalOperationComboBox.addItem ("REFUSED");
		clientIdFlexTable.setWidget (5, 1, surgicalOperationComboBox);
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
