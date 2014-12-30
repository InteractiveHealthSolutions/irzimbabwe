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
import org.irdresearch.irzimbabwe.shared.DateTimeUtil;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.Encounter;
import org.irdresearch.irzimbabwe.shared.model.EncounterId;
import org.irdresearch.irzimbabwe.shared.model.EncounterResults;
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * MC Client Visit Notes form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MCVisitNotesComposite extends Composite implements ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
	private static ServerServiceAsync	service								= GWT.create (ServerService.class);
	private static final String			formName							= "MC_VISIT";

	private UserRightsUtil				rights								= new UserRightsUtil ();
	private String						clientId							= "";
	private boolean						valid;

	private FlexTable					flexTable							= new FlexTable ();
	private FlexTable					topFlexTable						= new FlexTable ();
	private FlexTable					clientIdFlexTable					= new FlexTable ();

	private Grid						grid								= new Grid (1, 2);
	private Grid						precautionsGrid						= new Grid (5, 1);
	private VerticalPanel				middleVerticalPanel					= new VerticalPanel ();
	private HorizontalPanel				clientIdHorizontalPanel				= new HorizontalPanel ();

	private Button						checkIdButton						= new Button ("Check");
	private Button						saveButton							= new Button ("Save");
	private Button						closeButton							= new Button ("Close");

	private Label						lblClientsInitialDemographics		= new Label ("MC Visit Notes Form");
	private Label						lblClientsId						= new Label ("Client's ID:");
	private Label						lblVisitDateFor						= new Label ("Follow-up Visit Date:");
	private Label						lblVisitType						= new Label ("Visit Type:");
	private Label						lblNursedoctorsName					= new Label ("Nurse/Doctor's Name:");
	private Label						lblAnyComplicationsFound			= new Label ("Were any Complications found?");
	private Label						lblHasWoundedHealed					= new Label ("Has wounded Healed?");
	private Label						lblHasClientStarted					= new Label ("Has Client started sexual activity?");
	private Label						lblIfYesWhen						= new Label ("If yes, when did he start?");
	private Label						lblStepsTakenTo						= new Label ("Precautions taken to reduce sexual risk:");

	private TextBox						clientIdTextBox						= new TextBox ();
	private TextBox						doctorTextBox						= new TextBox ();
	private TextBox						otherPrecautionsTextBox				= new TextBox ();

	private DateBox						dateVisitedDateBox					= new DateBox ();
	private DateBox						dateActivityStartedDateBox			= new DateBox ();

	private ListBox						visitTypeComboBox					= new ListBox ();
	private ListBox						complicationsComboBox				= new ListBox ();
	private ListBox						woundHealedComboBox					= new ListBox ();
	private ListBox						sexualActivityComboBox				= new ListBox ();

	private CheckBox					precautionDualProtectionCheckBox	= new CheckBox ("Dual Protection");
	private CheckBox					precautionUsedCondomsCheckBox		= new CheckBox ("Used Condoms");
	private CheckBox					precautionMonogamyCheckBox			= new CheckBox ("Monogamy");
	private CheckBox					precautionDecreasedPartnersCheckBox	= new CheckBox ("Decreased No. of Partners");
	private CheckBox					precautionAbstinenceCheckBox		= new CheckBox ("Abstinence");
	private CheckBox					otherPrecautionCheckBox				= new CheckBox ("Specify Other:");

	public MCVisitNotesComposite ()
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
		clientIdHorizontalPanel.setWidth ("100%");
		clientIdTextBox.setVisibleLength (12);
		clientIdTextBox.setMaxLength (12);
		clientIdHorizontalPanel.add (clientIdTextBox);
		checkIdButton.setText ("Check");
		clientIdHorizontalPanel.add (checkIdButton);
		checkIdButton.setWidth ("100%");
		lblVisitDateFor.setWordWrap (false);
		clientIdFlexTable.setWidget (1, 0, lblVisitDateFor);
		dateVisitedDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yyyy-MMM-dd")));
		clientIdFlexTable.setWidget (1, 1, dateVisitedDateBox);
		lblVisitType.setWordWrap (false);
		clientIdFlexTable.setWidget (2, 0, lblVisitType);
		visitTypeComboBox.addItem ("48 HOUR POST-OP", "2");
		visitTypeComboBox.addItem ("7TH DAY POST-OP", "7");
		visitTypeComboBox.addItem ("42ND DAY POST-OP", "42");
		visitTypeComboBox.addItem ("ADDITIONAL POST-OP", "CUSTOM");
		clientIdFlexTable.setWidget (2, 1, visitTypeComboBox);
		lblNursedoctorsName.setWordWrap (false);
		clientIdFlexTable.setWidget (3, 0, lblNursedoctorsName);
		clientIdFlexTable.setWidget (3, 1, doctorTextBox);
		lblAnyComplicationsFound.setWordWrap (false);
		clientIdFlexTable.setWidget (4, 0, lblAnyComplicationsFound);
		complicationsComboBox.addItem ("NONE");
		complicationsComboBox.addItem ("MILD");
		complicationsComboBox.addItem ("MODERATE");
		complicationsComboBox.addItem ("SEVERE");
		clientIdFlexTable.setWidget (4, 1, complicationsComboBox);
		clientIdFlexTable.setWidget (5, 0, lblHasWoundedHealed);
		woundHealedComboBox.addItem ("YES", "Y");
		woundHealedComboBox.addItem ("NO", "N");
		clientIdFlexTable.setWidget (5, 1, woundHealedComboBox);
		lblHasClientStarted.setWordWrap (false);
		clientIdFlexTable.setWidget (6, 0, lblHasClientStarted);
		sexualActivityComboBox.addItem ("NO", "N");
		sexualActivityComboBox.addItem ("YES", "Y");
		clientIdFlexTable.setWidget (6, 1, sexualActivityComboBox);
		lblIfYesWhen.setWordWrap (false);
		clientIdFlexTable.setWidget (7, 0, lblIfYesWhen);
		dateActivityStartedDateBox.setEnabled (false);
		dateActivityStartedDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yyyy-MMM-dd")));
		clientIdFlexTable.setWidget (7, 1, dateActivityStartedDateBox);
		lblStepsTakenTo.setWordWrap (false);
		clientIdFlexTable.setWidget (8, 0, lblStepsTakenTo);
		clientIdFlexTable.setWidget (8, 1, precautionsGrid);
		precautionDualProtectionCheckBox.setName ("DUAL_PROTECTION");
		precautionDualProtectionCheckBox.setWordWrap (false);
		precautionsGrid.setWidget (0, 0, precautionDualProtectionCheckBox);
		precautionUsedCondomsCheckBox.setName ("USED_CONDOMS");
		precautionUsedCondomsCheckBox.setWordWrap (false);
		precautionsGrid.setWidget (1, 0, precautionUsedCondomsCheckBox);
		precautionMonogamyCheckBox.setName ("MONOGAMY");
		precautionMonogamyCheckBox.setWordWrap (false);
		precautionsGrid.setWidget (2, 0, precautionMonogamyCheckBox);
		precautionDecreasedPartnersCheckBox.setName ("DECREASED_PARTNERS");
		precautionDecreasedPartnersCheckBox.setWordWrap (false);
		precautionsGrid.setWidget (3, 0, precautionDecreasedPartnersCheckBox);
		precautionAbstinenceCheckBox.setName ("ABSTINENCE");
		precautionAbstinenceCheckBox.setWordWrap (false);
		precautionsGrid.setWidget (4, 0, precautionAbstinenceCheckBox);
		clientIdFlexTable.setWidget (9, 0, otherPrecautionCheckBox);
		otherPrecautionsTextBox.setEnabled (false);
		otherPrecautionsTextBox.setMaxLength (50);
		clientIdFlexTable.setWidget (9, 1, otherPrecautionsTextBox);
		clientIdFlexTable.getCellFormatter ().setVerticalAlignment (8, 0, HasVerticalAlignment.ALIGN_TOP);
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
		checkIdButton.addClickHandler (this);
		closeButton.addClickHandler (this);
		saveButton.addClickHandler (this);
		sexualActivityComboBox.addChangeHandler (this);
		otherPrecautionCheckBox.addValueChangeHandler (this);
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
		clientId = "";
		IRZClient.clearControls (clientIdFlexTable);
		ListBox[] listBoxes = {visitTypeComboBox, complicationsComboBox};
		for (int i = 0; i < listBoxes.length; i++)
			listBoxes[i].setSelectedIndex (-1);
		CheckBox[] checkBoxes = {precautionAbstinenceCheckBox, precautionDecreasedPartnersCheckBox, precautionDualProtectionCheckBox, precautionMonogamyCheckBox, precautionUsedCondomsCheckBox,
				otherPrecautionCheckBox};
		for (int i = 0; i < checkBoxes.length; i++)
			checkBoxes[i].setValue (false);
		otherPrecautionsTextBox.setEnabled (false);
	}

	/**
	 * To validate data in widgets before performing DML operations
	 * 
	 * @return
	 */
	public boolean validate ()
	{
		valid = true;
		StringBuilder errorMessage = new StringBuilder ();
		/* Validate mandatory fields */
		if (IRZClient.get (clientIdTextBox).equals (""))
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
		if (DateTimeUtil.isFutureDate (dateVisitedDateBox.getValue ()))
			errorMessage.append ("Date Visited: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		if (IRZClient.get (sexualActivityComboBox).equals ("Y"))
			if (DateTimeUtil.isFutureDate (dateActivityStartedDateBox.getValue ()))
				errorMessage.append ("Date of Sexual Activity: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		valid = errorMessage.length () == 0;
		if (!valid)
		{
			Window.alert (errorMessage.toString ());
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
			enteredDate=dateVisitedDateBox.getValue();
			
			EncounterId encounterId = new EncounterId (eId, clientId, pid2, formName);
			Encounter encounter = new Encounter (encounterId, IRZ.getCurrentLocation ());
			encounter.setLocationId (IRZ.getCurrentLocation ());
			encounter.setDateEntered (enteredDate);
			encounter.setDateStart (new Date ());
			encounter.setDateEnd (new Date ());
			ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults> ();
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "VISIT_TYPE"), IRZClient.get (visitTypeComboBox)));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "DOCTOR_NAME"), IRZClient.get (doctorTextBox).toUpperCase ()));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "COMPLICATIONS"), IRZClient.get (complicationsComboBox)));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "WOUND_HEALED"), IRZClient.get (woundHealedComboBox)));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SEXUAL_ACTIVITY"), IRZClient.get (sexualActivityComboBox)));
			if (IRZClient.get (sexualActivityComboBox).equals ("Y"))
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "ACTIVITY_START_DATE"), DateTimeUtil.getFormattedDate (
						dateActivityStartedDateBox.getValue (), DateTimeUtil.SQL_DATE)));
			CheckBox[] checks = {precautionDualProtectionCheckBox, precautionUsedCondomsCheckBox, precautionMonogamyCheckBox, precautionDecreasedPartnersCheckBox, precautionAbstinenceCheckBox};
			StringBuilder precautions = new StringBuilder ();
			for (CheckBox checkBox : checks)
			{
				if (checkBox.getValue ())
					precautions.append (checkBox.getName ());
				precautions.append (";");
			}
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "PRECAUTION"), precautions.toString ()));
			if (otherPrecautionCheckBox.getValue ())
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OTHER_PRECAUTION"), IRZClient.get (otherPrecautionsTextBox).toUpperCase ()));
			service.saveVisitNotes (encounter, encounterResults.toArray (new EncounterResults[] {}), new AsyncCallback<String> ()
			{
				public void onSuccess (String result)
				{
					if (result.equals ("SUCCESS"))
					{
						Window.alert (CustomMessage.getInfoMessage (InfoType.INSERTED));
						load (false);
						clearUp ();
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
				service.exists ("encounter", "pid1='" + clientId + "' and encounter_type='MC_OPERAT'", new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (result)
							Window.alert (CustomMessage.getInfoMessage (InfoType.ID_VALID));
						else
							Window.alert (CustomMessage.getErrorMessage (ErrorType.ID_INVALID) + " Please make sure that the MC Operation form was filled for this Patient.");
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
			if (clientId == "")
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
		if (sender == sexualActivityComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("Y");
			dateActivityStartedDateBox.setEnabled (choice);
		}
	}

	@Override
	public void onValueChange (ValueChangeEvent<Boolean> event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender == otherPrecautionCheckBox)
		{
			otherPrecautionsTextBox.setEnabled (otherPrecautionCheckBox.getValue ());
		}
	}
}
