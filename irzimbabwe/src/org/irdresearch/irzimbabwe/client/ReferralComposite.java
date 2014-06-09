
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
import org.irdresearch.irzimbabwe.shared.model.Location;
import org.irdresearch.irzimbabwe.shared.model.Patient;
import org.irdresearch.irzimbabwe.shared.model.Referral;
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
 * Client's Referral form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class ReferralComposite extends Composite implements ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			formName						= "REFERRAL";

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

	private Button						checkIdButton					= new Button ("Check");
	private Button						saveButton						= new Button ("Save");
	private Button						closeButton						= new Button ("Close");

	private Label						lblClientsInitialDemographics	= new Label ("Client Referral Form");
	private Label						lblClientsId					= new Label ("Client's ID:");
	private Label						lblReasonForReferring			= new Label ("Reason for Referring:");
	private Label						lblSiteReferredTo				= new Label ("Referral Site Type:");
	private Label						lblDateReferred					= new Label ("Date Referred:");
	private Label						lblReferralSiteName				= new Label ("Referral Site Name:");

	private TextBox						clientIdTextBox					= new TextBox ();

	private DateBox						dateReferredDateBox				= new DateBox ();

	private ListBox						siteTypeComboBox				= new ListBox ();
	private ListBox						siteNameComboBox				= new ListBox ();
	private ListBox						reasonComboBox					= new ListBox ();

	public ReferralComposite ()
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
		clientIdHorizontalPanel.add (checkIdButton);
		checkIdButton.setWidth ("100%");
		lblReasonForReferring.setWordWrap (false);
		clientIdFlexTable.setWidget (1, 0, lblReasonForReferring);
		reasonComboBox.addItem ("TB TREATMENT");
		reasonComboBox.addItem ("TB INVESTIGATION");
		clientIdFlexTable.setWidget (1, 1, reasonComboBox);
		lblSiteReferredTo.setWordWrap (false);
		clientIdFlexTable.setWidget (2, 0, lblSiteReferredTo);
		siteTypeComboBox.setName ("LOCATION_TYPE");
		clientIdFlexTable.setWidget (2, 1, siteTypeComboBox);
		lblReferralSiteName.setWordWrap (false);
		clientIdFlexTable.setWidget (3, 0, lblReferralSiteName);
		clientIdFlexTable.setWidget (3, 1, siteNameComboBox);
		lblDateReferred.setWordWrap (false);
		clientIdFlexTable.setWidget (4, 0, lblDateReferred);
		dateReferredDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yyyy-MMM-dd")));
		clientIdFlexTable.setWidget (4, 1, dateReferredDateBox);
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
		ListBox[] listBoxes = {siteTypeComboBox};
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
		clientId = "";
		IRZClient.clearControls (topFlexTable);
		ListBox[] listBoxes = {reasonComboBox, siteTypeComboBox};
		for (int i = 0; i < listBoxes.length; i++)
			listBoxes[i].setSelectedIndex (-1);
		siteNameComboBox.clear ();
		dateReferredDateBox.getTextBox ().setText ("");
		clientIdTextBox.setText ("");
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
		if (DateTimeUtil.isFutureDate (dateReferredDateBox.getValue ()))
			errorMessage.append ("Date Referred: " + CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
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
			Date enteredDate = dateReferredDateBox.getValue ();
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
			String reason = IRZClient.get (reasonComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "REASON"), reason));
			String referredTo = IRZClient.get (siteNameComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "REFERRED_TO"), referredTo));
			Referral referral = new Referral (clientId);
			referral.setReason (reason);
			referral.setReferredTo (referredTo);
			referral.setReferredBy (IRZ.getCurrentUserName ());
			referral.setDateReferred (new Date ());

			service.saveReferral (referral, encounter, encounterResults.toArray (new EncounterResults[] {}), new AsyncCallback<String> ()
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
		if (sender == siteTypeComboBox)
		{
			try
			{
				service.findLocationsByType (IRZClient.get (siteTypeComboBox), new AsyncCallback<Location[]> ()
				{
					public void onSuccess (Location[] result)
					{
						siteNameComboBox.clear ();
						for (Location site : result)
							siteNameComboBox.addItem (site.getLocationName (), site.getLocationId ());
						load (false);
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
	}
}
