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
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
import org.irdresearch.irzimbabwe.shared.model.Patient;
import org.irdresearch.irzimbabwe.shared.model.Referral;
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
import com.google.gwt.user.client.ui.CheckBox;

/**
 * MC Client Referral form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MCReferralComposite extends Composite implements ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
	private static ServerServiceAsync	service										= GWT.create (ServerService.class);
	private static final String			formName									= "MC_REFER";

	private UserRightsUtil				rights										= new UserRightsUtil ();
	private Patient						currentPatient;
	private String						clientId									= "";
	private boolean						valid;

	private FlexTable					flexTable									= new FlexTable ();
	private FlexTable					topFlexTable								= new FlexTable ();
	private FlexTable					clientIdFlexTable							= new FlexTable ();

	private Grid						grid										= new Grid (1, 2);
	private Grid						sourceGrid									= new Grid (6, 2);
	private VerticalPanel				middleVerticalPanel							= new VerticalPanel ();
	private HorizontalPanel				clientIdHorizontalPanel						= new HorizontalPanel ();

	private Button						checkIdButton								= new Button ("Check");
	private Button						saveButton									= new Button ("Save");
	private Button						closeButton									= new Button ("Close");

	private Label						lblClientsInitialDemographics				= new Label ("MC Referral Form");
	private Label						lblClientsId								= new Label ("Client's ID:");
	private Label						lblReasonForReferring						= new Label ("Client's reason for MC:");
	private Label						lblReferralSiteName							= new Label ("Referring Site Name:");
	private Label						lblHowDidClient								= new Label ("How did Client hear about MC?");
	private Label						lblDidCommunityMobilizer					= new Label ("Did Community Mobilizer Team speak to the Client('s family member)?");
	private Label						lblWhichAreaWas								= new Label ("Which Area was the Client in when the Team spoke to him or his family member?");

	private TextBox						clientIdTextBox								= new TextBox ();
	private TextBox						otherReasonTextBox							= new TextBox ();
	private TextBox						otherSourceTextBox							= new TextBox ();

	private CheckBox					hasReferralFormCheckBox						= new CheckBox ("Client has a Referral Form");
	private CheckBox					otherReasonCheckBox							= new CheckBox ("Other Reason:");
	private CheckBox					sourceFriendOrFamilyCheckBox				= new CheckBox ("Friend/Relative/Family");
	private CheckBox					sourceOtherMcClientCheckBox					= new CheckBox ("Other MC Client");
	private CheckBox					sourceSpouseCheckBox						= new CheckBox ("Spouse");
	private CheckBox					sourceHealthWorkerCheckBox					= new CheckBox ("Health Worker");
	private CheckBox					sourcePosterCheckBox						= new CheckBox ("Poster/Leaflet/Brochure");
	private CheckBox					sourceCommunityMobilizerCheckBox			= new CheckBox ("Community Mobilizer");
	private CheckBox					sourceVolunteerCheckBox						= new CheckBox ("Volunteer/Volunteer Group");
	private CheckBox					sourceTvCheckBox							= new CheckBox ("TV");
	private CheckBox					sourceInternetCheckBox						= new CheckBox ("Internet");
	private CheckBox					sourceRadioCheckBox							= new CheckBox ("Radio");
	private CheckBox					sourceNewStartTreatmentCheckBox				= new CheckBox ("New Start Treatment Site");
	private CheckBox					sourceOtherTreatmentProviderCheckBox		= new CheckBox ("Other Treatment Provider");
	private CheckBox					otherSourceCheckBox							= new CheckBox ("Other Source:");

	private ListBox						siteNameComboBox							= new ListBox ();
	private ListBox						reasonListBox								= new ListBox ();
	private ListBox						communityMobilizerSpokeComboBox				= new ListBox ();
	private ListBox						locationWhenCommunityMobilizerSpokeComboBox	= new ListBox ();

	@SuppressWarnings("deprecation")
	public MCReferralComposite ()
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
		clientIdHorizontalPanel.add (checkIdButton);
		checkIdButton.setWidth ("100%");
		lblReasonForReferring.setWordWrap (false);
		clientIdFlexTable.setWidget (1, 0, lblReasonForReferring);
		reasonListBox.setMultipleSelect (true);
		reasonListBox.setName ("MC_REASON");
		clientIdFlexTable.setWidget (1, 1, reasonListBox);
		clientIdFlexTable.setWidget (2, 0, otherReasonCheckBox);
		otherReasonTextBox.setEnabled (false);
		otherReasonTextBox.setMaxLength (20);
		clientIdFlexTable.setWidget (2, 1, otherReasonTextBox);
		lblHowDidClient.setWordWrap (false);
		clientIdFlexTable.setWidget (3, 0, lblHowDidClient);
		clientIdFlexTable.setWidget (3, 1, sourceGrid);
		sourceFriendOrFamilyCheckBox.setName ("FRIEND_FAMILY");
		sourceFriendOrFamilyCheckBox.setWordWrap (false);
		sourceGrid.setWidget (0, 0, sourceFriendOrFamilyCheckBox);
		sourceOtherMcClientCheckBox.setName ("MC_CLIENT");
		sourceOtherMcClientCheckBox.setWordWrap (false);
		sourceGrid.setWidget (0, 1, sourceOtherMcClientCheckBox);
		sourceSpouseCheckBox.setName ("SPOUSE");
		sourceSpouseCheckBox.setWordWrap (false);
		sourceGrid.setWidget (1, 0, sourceSpouseCheckBox);
		sourceHealthWorkerCheckBox.setName ("HEALTH_WORKER");
		sourceHealthWorkerCheckBox.setWordWrap (false);
		sourceGrid.setWidget (1, 1, sourceHealthWorkerCheckBox);
		sourcePosterCheckBox.setName ("POSTER");
		sourcePosterCheckBox.setWordWrap (false);
		sourceGrid.setWidget (2, 0, sourcePosterCheckBox);
		sourceCommunityMobilizerCheckBox.setName ("MOBILIZER");
		sourceCommunityMobilizerCheckBox.setWordWrap (false);
		sourceGrid.setWidget (2, 1, sourceCommunityMobilizerCheckBox);
		sourceVolunteerCheckBox.setName ("VOLUNTEER");
		sourceVolunteerCheckBox.setWordWrap (false);
		sourceGrid.setWidget (3, 0, sourceVolunteerCheckBox);
		sourceNewStartTreatmentCheckBox.setName ("NEW_START_SITE");
		sourceNewStartTreatmentCheckBox.setWordWrap (false);
		sourceGrid.setWidget (3, 1, sourceNewStartTreatmentCheckBox);
		sourceTvCheckBox.setName ("TV");
		sourceTvCheckBox.setWordWrap (false);
		sourceGrid.setWidget (4, 0, sourceTvCheckBox);
		sourceInternetCheckBox.setName ("INTERNET");
		sourceInternetCheckBox.setWordWrap (false);
		sourceGrid.setWidget (4, 1, sourceInternetCheckBox);
		sourceRadioCheckBox.setName ("RADIO");
		sourceRadioCheckBox.setWordWrap (false);
		sourceGrid.setWidget (5, 0, sourceRadioCheckBox);
		sourceOtherTreatmentProviderCheckBox.setName ("OTHER_TX_PROVIDER");
		sourceOtherTreatmentProviderCheckBox.setWordWrap (false);
		sourceGrid.setWidget (5, 1, sourceOtherTreatmentProviderCheckBox);
		clientIdFlexTable.setWidget (4, 0, otherSourceCheckBox);
		otherSourceTextBox.setMaxLength (20);
		otherSourceTextBox.setEnabled (false);
		clientIdFlexTable.setWidget (4, 1, otherSourceTextBox);
		clientIdFlexTable.setWidget (5, 0, lblDidCommunityMobilizer);
		communityMobilizerSpokeComboBox.addItem ("YES", "YES");
		communityMobilizerSpokeComboBox.addItem ("NO", "NO");
		communityMobilizerSpokeComboBox.addItem ("DON'T KNOW", "DONT KNOW");
		clientIdFlexTable.setWidget (5, 1, communityMobilizerSpokeComboBox);
		clientIdFlexTable.setWidget (6, 0, lblWhichAreaWas);
		clientIdFlexTable.setWidget (6, 1, locationWhenCommunityMobilizerSpokeComboBox);
		lblReferralSiteName.setWordWrap (false);
		clientIdFlexTable.setWidget (7, 0, lblReferralSiteName);
		clientIdFlexTable.setWidget (7, 1, siteNameComboBox);
		clientIdFlexTable.setWidget (8, 1, hasReferralFormCheckBox);
		clientIdFlexTable.getCellFormatter ().setVerticalAlignment (3, 0, HasVerticalAlignment.ALIGN_TOP);
		clientIdFlexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
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
		otherReasonCheckBox.addValueChangeHandler (this);
		otherSourceCheckBox.addValueChangeHandler (this);
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
		if(validate())
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
			StringBuilder reasons = new StringBuilder ();
			for (int i = 0; i < reasonListBox.getItemCount (); i++)
				if (reasonListBox.isItemSelected (i))
					reasons.append (reasonListBox.getValue (i) + ";");
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "REASON"),reasons.toString ()));
			if (otherReasonTextBox.isEnabled ())
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,"OTHER_REASON"), IRZClient.get (otherReasonTextBox).toUpperCase ()));
			StringBuilder sources = new StringBuilder ();
			CheckBox[] checks = {sourceFriendOrFamilyCheckBox, sourceSpouseCheckBox, sourceOtherMcClientCheckBox,
					sourceCommunityMobilizerCheckBox, sourceHealthWorkerCheckBox, sourceNewStartTreatmentCheckBox,
					sourceOtherTreatmentProviderCheckBox, sourceVolunteerCheckBox, sourcePosterCheckBox, sourceTvCheckBox,
					sourceRadioCheckBox, sourceInternetCheckBox};
			for (CheckBox checkBox : checks)
			{
				if (checkBox.getValue ())
					sources.append (checkBox.getName ());
				sources.append (";");
			}
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SOURCE"),
					sources.toString ()));
			if (otherSourceTextBox.isEnabled ())
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,
						"OTHER_SOURCE"), IRZClient.get (otherSourceTextBox).toUpperCase ()));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,
					"SPOKE_TO_TEAM"), IRZClient.get (communityMobilizerSpokeComboBox)));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,
					"AREA_WHEN_SPOKE"), IRZClient.get (locationWhenCommunityMobilizerSpokeComboBox)));
			String referredTo = IRZClient.get (siteNameComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,
					"REFERRED_TO"), referredTo));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,
					"HAD_REFERRAL"), (hasReferralFormCheckBox.getValue () ? "Y" : "N")));

			Referral referral = new Referral (clientId);
			referral.setReason (reasons.toString ());
			referral.setReferredTo (referredTo);
			referral.setReferredBy (IRZ.getCurrentUserName ());
			referral.setDateReferred (new Date ());

			service.saveMCReferral (referral, encounter, encounterResults.toArray (new EncounterResults[] {}),
					new AsyncCallback<String> ()
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
	public void setRights (String menuName)
	{
		try
		{
			load (true);
			service.getUserRgihts (IRZ.getCurrentUserName (), IRZ.getCurrentRole (), menuName,
					new AsyncCallback<Boolean[]> ()
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
										boolean hasAccess = rights.getAccess (AccessType.INSERT)
												| rights.getAccess (AccessType.UPDATE)
												| rights.getAccess (AccessType.DELETE)
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
										caught.printStackTrace();
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
							caught.printStackTrace ();
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
						if (currentPatient != null || currentPatient.getDiseaseSuspected () != null || !currentPatient.getDiseaseSuspected ().equals ("TB"))
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
				Window.alert ("Please first check Client's ID and then select a sample from the list of registered samples.");
				load (false);
				return;
			}
			try
			{
				service.findReferral (clientId, new AsyncCallback<Referral> ()
				{
					public void onSuccess (Referral result)
					{
						if (result != null)
						{
						    Window.alert (CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR));
						    load(false);
						}
						else
							saveData ();
					}

					public void onFailure (Throwable caught)
					{
						caught.printStackTrace ();
						load(false);
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				load(false);
			}
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

	public void onValueChange (ValueChangeEvent<Boolean> event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender == otherReasonCheckBox)
		{
			boolean choice = otherReasonCheckBox.getValue ();
			otherReasonTextBox.setEnabled (choice);
		}
		else if (sender == otherSourceCheckBox)
		{
			boolean choice = otherSourceCheckBox.getValue ();
			otherSourceTextBox.setEnabled (choice);
		}
	}
}
