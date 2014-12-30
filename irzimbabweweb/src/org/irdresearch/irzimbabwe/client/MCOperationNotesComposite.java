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
import org.irdresearch.irzimbabwe.shared.RegexUtil;
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

/**
 * MC Client Operation Notes form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MCOperationNotesComposite extends Composite implements ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			formName						= "MC_OPERAT";

	private UserRightsUtil				rights							= new UserRightsUtil ();
	private Patient						currentPatient;
	private String						clientId						= "";
	private boolean						valid;

	private FlexTable					flexTable						= new FlexTable ();
	private FlexTable					topFlexTable					= new FlexTable ();
	private FlexTable					clientIdFlexTable				= new FlexTable ();

	private Grid						grid							= new Grid (1, 2);
	private VerticalPanel				middleVerticalPanel				= new VerticalPanel ();
	private HorizontalPanel				clientIdHorizontalPanel			= new HorizontalPanel ();
	private HorizontalPanel				operationTimeHorizontalPanel	= new HorizontalPanel ();

	private Button						checkIdButton					= new Button ("Check");
	private Button						saveButton						= new Button ("Save");
	private Button						closeButton						= new Button ("Close");

	private Label						lblClientsInitialDemographics	= new Label ("MC Operation Notes Form");
	private Label						lblClientsId					= new Label ("Client's ID:");
	private Label						lblVisitDateFor					= new Label ("Visit Date for Circumcision:");
	private Label						lblWasThePreop					= new Label ("Was pre-op.. Assessment Conducted?");
	private Label						lblOperatorsName				= new Label ("Operator's Name:");
	private Label						lblAssistantsName				= new Label ("Assistant's Name:");
	private Label						lblOperationTime				= new Label ("Operation Time (hh:mm):");
	private Label						lblFrom							= new Label ("from");
	private Label						lblTo							= new Label ("to");

	private TextBox						clientIdTextBox					= new TextBox ();
	private TextBox						operatorTextBox					= new TextBox ();
	private TextBox						assistantTextBox				= new TextBox ();
	private TextBox						timeFromDateBox					= new TextBox ();
	private TextBox						timeToDateBox					= new TextBox ();

	private DateBox						dateVisitedDateBox				= new DateBox ();

	private ListBox						assessmentConductedComboBox		= new ListBox ();

	public MCOperationNotesComposite ()
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
		lblWasThePreop.setWordWrap (false);
		clientIdFlexTable.setWidget (2, 0, lblWasThePreop);
		assessmentConductedComboBox.addItem ("YES", "Y");
		assessmentConductedComboBox.addItem ("NO", "N");
		clientIdFlexTable.setWidget (2, 1, assessmentConductedComboBox);
		lblOperatorsName.setWordWrap (false);
		clientIdFlexTable.setWidget (3, 0, lblOperatorsName);
		operatorTextBox.setMaxLength (50);
		clientIdFlexTable.setWidget (3, 1, operatorTextBox);
		lblAssistantsName.setWordWrap (false);
		clientIdFlexTable.setWidget (4, 0, lblAssistantsName);
		assistantTextBox.setMaxLength (50);
		clientIdFlexTable.setWidget (4, 1, assistantTextBox);
		lblOperationTime.setWordWrap (false);
		clientIdFlexTable.setWidget (5, 0, lblOperationTime);
		clientIdFlexTable.setWidget (5, 1, operationTimeHorizontalPanel);
		operationTimeHorizontalPanel.add (lblFrom);
		operationTimeHorizontalPanel.setCellVerticalAlignment (lblFrom, HasVerticalAlignment.ALIGN_MIDDLE);
		timeFromDateBox.setText ("12:00");
		timeFromDateBox.setVisibleLength (5);
		timeFromDateBox.setMaxLength (5);
		operationTimeHorizontalPanel.add (timeFromDateBox);
		operationTimeHorizontalPanel.add (lblTo);
		operationTimeHorizontalPanel.setCellVerticalAlignment (lblTo, HasVerticalAlignment.ALIGN_MIDDLE);
		timeToDateBox.setText ("12:00");
		timeToDateBox.setVisibleLength (5);
		timeToDateBox.setMaxLength (5);
		operationTimeHorizontalPanel.add (timeToDateBox);
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
		IRZClient.clearControls (clientIdFlexTable);
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
		if (IRZClient.get (clientIdTextBox).equals ("") || IRZClient.get (dateVisitedDateBox.getTextBox ()).equals ("") || IRZClient.get (timeFromDateBox).equals ("")
				|| IRZClient.get (timeToDateBox).equals (""))
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
		if (DateTimeUtil.isFutureDate (dateVisitedDateBox.getValue ()))
			errorMessage.append ("Date Visited: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		if (!RegexUtil.isValidTime (IRZClient.get (timeFromDateBox), false) || !RegexUtil.isValidTime (IRZClient.get (timeToDateBox), false))
			errorMessage.append ("Time: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		valid = errorMessage.length () == 0;
		if (!valid)
		{
			Window.alert (errorMessage.toString ());
			load (false);
		}
		return valid;
	}

	@SuppressWarnings("deprecation")
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
			Date fromTime = enteredDate;
			Date toTime = enteredDate;
			String[] from = IRZClient.get (timeFromDateBox).split (":");
			String[] to = IRZClient.get (timeToDateBox).split (":");
			fromTime.setHours (Integer.parseInt (from[0]));
			fromTime.setMinutes (Integer.parseInt (from[1]));
			toTime.setHours (Integer.parseInt (to[0]));
			toTime.setMinutes (Integer.parseInt (to[1]));
			ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults> ();
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "ASSESSMENT_CONDUCTED"), IRZClient.get (assessmentConductedComboBox)));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OPERATOR_NAME"), IRZClient.get (operatorTextBox).toUpperCase ()));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "ASSISTANT_NAME"), IRZClient.get (assistantTextBox).toUpperCase ()));
			encounterResults
					.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OPERATION_START"), DateTimeUtil.getFormattedDate (fromTime, DateTimeUtil.SQL_DATETIME)));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OPERATION_END"), DateTimeUtil.getFormattedDate (toTime, DateTimeUtil.SQL_DATETIME)));

			service.saveOperationNotes (encounter, encounterResults.toArray (new EncounterResults[] {}), new AsyncCallback<String> ()
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
						if (currentPatient != null)
						{
							try
							{
								service.exists ("encounter", "pid1='" + currentPatient.getPatientId () + "' and encounter_type='" + formName + "'", new AsyncCallback<Boolean> ()
								{
									public void onSuccess (Boolean result)
									{
										if (result)
										{
											Window.alert (CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR));
											currentPatient = null;
										}
										else
											Window.alert (CustomMessage.getInfoMessage (InfoType.ID_VALID));
									}

									public void onFailure (Throwable caught)
									{
									}
								});
							}
							catch (Exception e)
							{
							}
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
