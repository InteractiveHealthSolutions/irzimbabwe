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
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * MC Client Intake form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MCIntakeComposite extends Composite implements ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service								= GWT.create (ServerService.class);
	private static final String			formName							= "MC_INTAKE";

	private UserRightsUtil				rights								= new UserRightsUtil ();
	private Patient						currentPatient;
	private String						clientId							= "";
	private boolean						valid;

	private FlexTable					flexTable							= new FlexTable ();
	private FlexTable					topFlexTable						= new FlexTable ();
	private FlexTable					clientIdFlexTable					= new FlexTable ();

	private Grid						grid								= new Grid (1, 2);
	private VerticalPanel				middleVerticalPanel					= new VerticalPanel ();
	private VerticalPanel				hivTestLocationsVerticalPanel		= new VerticalPanel ();
	private HorizontalPanel				clientIdHorizontalPanel				= new HorizontalPanel ();
	private HorizontalPanel				recentVisitHorizontalPanel			= new HorizontalPanel ();

	private Button						checkIdButton						= new Button ("Check");
	private Button						saveButton							= new Button ("Save");
	private Button						closeButton							= new Button ("Close");

	private Label						lblClientsInitialDemographics		= new Label ("MC Client Intake Form");
	private Label						lblClientsId						= new Label ("Client's ID:");
	private Label						lblHasTheClient						= new Label ("Has the Client attended Group Education Session?");
	private Label						lblHasTheClient_1					= new Label ("Has the Client has been tested for HIV (including at current Site)?");
	private Label						lblWhereWasThe						= new Label ("Where was the Client tested for HIV?");
	private Label						lblWhenWasThe						= new Label ("When was the Client recently tested prior to visiting MC Site?");
	private Label						lblAgo								= new Label ("ago");
	private Label						lblWhatWasThe						= new Label ("HIV result of the last Visit:");
	private Label						lblWhatIsClients					= new Label ("HIV result on MC Site:");
	private Label						label								= new Label ("Client's Occupation:");
	private Label						label_1								= new Label ("Specify Other:");
	private Label						lblRelationshipStatus				= new Label ("Client's Relationship Status:");
	private Label						lblServicesClientVisited			= new Label ("Services Client visited for:");

	private TextBox						clientIdTextBox						= new TextBox ();
	private TextBox						otherOccupationTextBox				= new TextBox ();

	private IntegerBox					durationIntegerBox					= new IntegerBox ();

	private CheckBox					hivLocationMCCheckbox				= new CheckBox ("At Current MC Site");
	private CheckBox					hivLocationNewStartCheckbox			= new CheckBox ("New Start Treatment Site");
	private CheckBox					hivLocationNGOCheckbox				= new CheckBox ("Other NGO's Treatment Site");
	private CheckBox					hivLocationPublicSectorCheckbox		= new CheckBox ("Public Sector Healthcare");
	private CheckBox					hivLocationPrivateSectorCheckbox	= new CheckBox ("Private Sector Healthcare");

	private ListBox						attenderGroupEducationComboBox		= new ListBox ();
	private ListBox						testedForHIVComboBox				= new ListBox ();
	private ListBox						durationComboBox					= new ListBox ();
	private ListBox						hivResultComboBox					= new ListBox ();
	private ListBox						hivResultOnSiteComboBox				= new ListBox ();
	private ListBox						occupationComboBox					= new ListBox ();
	private ListBox						relationshipStatusComboBox			= new ListBox ();
	private ListBox						servicesComboBox					= new ListBox ();

	public MCIntakeComposite ()
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
		checkIdButton.setText ("Check");
		clientIdHorizontalPanel.add (checkIdButton);
		checkIdButton.setWidth ("100%");
		lblHasTheClient.setWordWrap (false);
		clientIdFlexTable.setWidget (1, 0, lblHasTheClient);
		attenderGroupEducationComboBox.addItem ("NO");
		attenderGroupEducationComboBox.addItem ("YES");
		clientIdFlexTable.setWidget (1, 1, attenderGroupEducationComboBox);
		clientIdFlexTable.setWidget (2, 0, lblHasTheClient_1);
		testedForHIVComboBox.addItem ("NO");
		testedForHIVComboBox.addItem ("YES");
		clientIdFlexTable.setWidget (2, 1, testedForHIVComboBox);
		clientIdFlexTable.getCellFormatter ().setVerticalAlignment (3, 0, HasVerticalAlignment.ALIGN_TOP);
		clientIdFlexTable.setWidget (3, 0, lblWhereWasThe);

		clientIdFlexTable.setWidget (3, 1, hivTestLocationsVerticalPanel);
		hivLocationMCCheckbox.setName ("hivLocation");

		hivTestLocationsVerticalPanel.add (hivLocationMCCheckbox);
		hivLocationNewStartCheckbox.setName ("hivLocation");

		hivTestLocationsVerticalPanel.add (hivLocationNewStartCheckbox);
		hivLocationNGOCheckbox.setName ("hivLocation");

		hivTestLocationsVerticalPanel.add (hivLocationNGOCheckbox);
		hivLocationPublicSectorCheckbox.setName ("hivLocation");

		hivTestLocationsVerticalPanel.add (hivLocationPublicSectorCheckbox);
		hivLocationPrivateSectorCheckbox.setName ("hivLocation");

		hivTestLocationsVerticalPanel.add (hivLocationPrivateSectorCheckbox);
		clientIdFlexTable.setWidget (4, 0, lblWhenWasThe);
		clientIdFlexTable.setWidget (4, 1, recentVisitHorizontalPanel);
		durationIntegerBox.setEnabled (false);
		durationIntegerBox.setVisibleLength (2);
		recentVisitHorizontalPanel.add (durationIntegerBox);
		durationComboBox.setEnabled (false);
		durationComboBox.addItem ("DAYS");
		durationComboBox.addItem ("WEEKS");
		durationComboBox.addItem ("MONTHS");
		durationComboBox.addItem ("YEARS");
		recentVisitHorizontalPanel.add (durationComboBox);
		recentVisitHorizontalPanel.add (lblAgo);
		recentVisitHorizontalPanel.setCellVerticalAlignment (lblAgo, HasVerticalAlignment.ALIGN_MIDDLE);
		clientIdFlexTable.setWidget (5, 0, lblWhatWasThe);
		hivResultComboBox.setEnabled (false);
		hivResultComboBox.addItem ("NEGATIVE");
		hivResultComboBox.addItem ("POSITIVE");
		hivResultComboBox.addItem ("REFUSED");
		hivResultComboBox.addItem ("NOT SURE");
		clientIdFlexTable.setWidget (5, 1, hivResultComboBox);
		clientIdFlexTable.setWidget (6, 0, lblWhatIsClients);
		hivResultOnSiteComboBox.setEnabled (false);
		hivResultOnSiteComboBox.addItem ("NEGATIVE");
		hivResultOnSiteComboBox.addItem ("POSITIVE");
		clientIdFlexTable.setWidget (6, 1, hivResultOnSiteComboBox);
		clientIdFlexTable.setWidget (7, 0, label);
		occupationComboBox.setEnabled (false);
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
		occupationComboBox.addItem ("REFUSED");
		occupationComboBox.addItem ("OTHER");
		clientIdFlexTable.setWidget (7, 1, occupationComboBox);
		clientIdFlexTable.setWidget (8, 0, label_1);
		otherOccupationTextBox.setEnabled (false);
		clientIdFlexTable.setWidget (8, 1, otherOccupationTextBox);
		clientIdFlexTable.setWidget (9, 0, lblRelationshipStatus);
		relationshipStatusComboBox.setEnabled (false);
		relationshipStatusComboBox.addItem ("SINGLE, NEVER HAD PARTNER");
		relationshipStatusComboBox.addItem ("SINGLE, NON-REGULAR PARTNER");
		relationshipStatusComboBox.addItem ("SINGLE, REGULAR NON-COHABITATING PARTNER");
		relationshipStatusComboBox.addItem ("MARRIED");
		relationshipStatusComboBox.addItem ("SEPARATED");
		relationshipStatusComboBox.addItem ("DIVORCED");
		relationshipStatusComboBox.addItem ("WIDOW(ER)");
		relationshipStatusComboBox.addItem ("COMMITTED");
		relationshipStatusComboBox.addItem ("POLYGAMOUS");
		clientIdFlexTable.setWidget (9, 1, relationshipStatusComboBox);
		clientIdFlexTable.setWidget (10, 0, lblServicesClientVisited);
		servicesComboBox.setEnabled (false);
		servicesComboBox.addItem ("MC COUNSELLING");
		servicesComboBox.addItem ("MC");
		servicesComboBox.addItem ("MC AND HTC");
		clientIdFlexTable.setWidget (10, 1, servicesComboBox);
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
