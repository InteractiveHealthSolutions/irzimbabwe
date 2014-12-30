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
import org.irdresearch.irzimbabwe.shared.model.Location;
import org.irdresearch.irzimbabwe.shared.model.Patient;
import org.irdresearch.irzimbabwe.shared.model.Referral;
import org.irdresearch.irzimbabwe.shared.model.User;
import org.irdresearch.irzimbabwe.shared.model.Visit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * Client's Referral form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class ReferralComposite extends Composite implements ClickHandler, ChangeHandler
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static final String formName = "REFERRAL";

    private UserRightsUtil rights = new UserRightsUtil();
    private Patient currentPatient;
    private String clientId = "";
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

    private Label lblClientsInitialDemographics = new Label("Client Referral Form");
    private Label lblClientsId = new Label("Client's ID:");
    private Label lblReasonForReferring = new Label("Reason for Referring:");
    private Label lblSiteReferredTo = new Label("Referral Site Type:");
    private Label lblDateReferred = new Label("Date Referred:");
    private Label lblReferralSiteName = new Label("Referral Site Name:");

    private TextBox clientIdTextBox = new TextBox();

    private DateBox dateReferredDateBox = new DateBox();

    private ListBox siteTypeComboBox = new ListBox();
    private ListBox siteNameComboBox = new ListBox();
    private ListBox reasonComboBox = new ListBox();

    public ReferralComposite()
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
	clientIdHorizontalPanel.add(checkIdButton);
	checkIdButton.setWidth("100%");
	lblReasonForReferring.setWordWrap(false);
	clientIdFlexTable.setWidget(1, 0, lblReasonForReferring);
	reasonComboBox.addItem("TB TREATMENT");
	reasonComboBox.addItem("TB INVESTIGATION");
	clientIdFlexTable.setWidget(1, 1, reasonComboBox);
	lblSiteReferredTo.setWordWrap(false);
	clientIdFlexTable.setWidget(2, 0, lblSiteReferredTo);
	siteTypeComboBox.setName("LOCATION_TYPE");
	clientIdFlexTable.setWidget(2, 1, siteTypeComboBox);
	lblReferralSiteName.setWordWrap(false);
	clientIdFlexTable.setWidget(3, 0, lblReferralSiteName);
	clientIdFlexTable.setWidget(3, 1, siteNameComboBox);
	lblDateReferred.setWordWrap(false);
	clientIdFlexTable.setWidget(4, 0, lblDateReferred);
	dateReferredDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("yyyy-MMM-dd")));
	clientIdFlexTable.setWidget(4, 1, dateReferredDateBox);
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
	ListBox[] listBoxes = { siteTypeComboBox };
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
	clientId = "";
	IRZClient.clearControls(topFlexTable);
	ListBox[] listBoxes = { reasonComboBox, siteTypeComboBox };
	for (int i = 0; i < listBoxes.length; i++)
	    listBoxes[i].setSelectedIndex(0);
	siteNameComboBox.clear();
	dateReferredDateBox.getTextBox().setText("");
	clientIdTextBox.setText("");
    }

    /**
     * To validate data in widgets before performing DML operations
     * 
     * @return
     */
    public boolean validate()
    {
	valid = true;
	final StringBuilder errorMessage = new StringBuilder();
	/* Validate mandatory fields */
	if (IRZClient.get(clientIdTextBox).equals(""))
	    errorMessage.append(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
	else if (dateReferredDateBox.getTextBox().getValue().equals(""))
	    errorMessage.append("Date Referred: " + CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
	else if (!(dateReferredDateBox.getTextBox().getValue().equals("")) && DateTimeUtil.isFutureDate(dateReferredDateBox.getValue()))
	    errorMessage.append("Date Referred is a future date: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	else if (siteNameComboBox.getSelectedIndex() == -1 && !IRZClient.get(siteTypeComboBox).equals("OTHER"))
	    errorMessage.append("Referral Site name cannot be empty: " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");

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
	final Date enteredDate = dateReferredDateBox.getValue();
	final int eId = 0;
	final String clientId = IRZClient.get(clientIdTextBox).toUpperCase();
	final String pid2 = IRZ.getCurrentUserName();
	try
	{
	    service.findVisit(clientId, new AsyncCallback<Visit>() {

		@Override
		public void onSuccess(Visit result)
		{
		    int compare = DateTimeUtil.compareDateOnly(dateReferredDateBox.getValue(), result.getVisitDate());
		    if (compare < 0)// trying to create referral before
				    // visitDate
		    {
			Window.alert("\n Error Details: Referral Date cannot be before the actual visit took place ! " + CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR));
			load(false);
			valid = false;
		    }
		    if (valid)
		    {
			EncounterId encounterId = new EncounterId(eId, clientId, pid2, formName);
			Encounter encounter = new Encounter(encounterId, IRZ.getCurrentLocation());
			encounter.setLocationId(IRZ.getCurrentLocation());
			encounter.setDateEntered(enteredDate);
			encounter.setDateStart(new Date());
			encounter.setDateEnd(new Date());
			ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults>();
			String reason = IRZClient.get(reasonComboBox);
			encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "REASON"), reason));
			String referredTo = IRZClient.get(siteNameComboBox);
			if (IRZClient.get(siteTypeComboBox).equals("OTHER"))
			    referredTo="OTHER SITE";
			encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "REFERRED_TO"), referredTo));
			Referral referral = new Referral(clientId);
			referral.setReason(reason);
			referral.setReferredTo(referredTo);
			referral.setReferredBy(IRZ.getCurrentUserName());
			referral.setDateReferred(enteredDate);

			service.saveReferral(referral, encounter, encounterResults.toArray(new EncounterResults[] {}), new AsyncCallback<String>() {
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

		@Override
		public void onFailure(Throwable caught)
		{
		    load(false);
		    caught.printStackTrace();

		}
	    });
	} catch(Exception e)
	{
	    e.printStackTrace();
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
		load(false);
		clientId = IRZClient.get(clientIdTextBox).toUpperCase();
		service.findPatient(clientId, new AsyncCallback<Patient>() {
		    public void onSuccess(Patient result)
		    {
			currentPatient = result;
			if (currentPatient != null && currentPatient.getDiseaseSuspected() != null && currentPatient.getDiseaseSuspected().equals("TB"))
			{
			    Window.alert(CustomMessage.getInfoMessage(InfoType.ID_VALID));
			}
			else
			    Window.alert(CustomMessage.getErrorMessage(ErrorType.ID_INVALID));
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
	    // If the ID has not been checked, then return
	    if (currentPatient == null || clientId == "")
	    {
		Window.alert("Please first check Client's ID.");
		load(false);
		return;
	    }
	    if (validate())// check the fields first prompt for incorrect
			   // input
	    {// See if the user is trying to refer client again referral
	     // encounter exists?
		try
		{
		    service.exists("encounter", "pid1='" + IRZClient.get(clientIdTextBox) + "' and encounter_type='REFERRAL'", new AsyncCallback<Boolean>() {
			public void onSuccess(Boolean result)
			{
			    if (result)
			    {
				boolean answer = Window.confirm("Client Information Already exists! Are you sure you want to update?");
				if (!answer)// no don't update
				{
				    load(false);
				}
				else
				// yes update create a second referral
				{
				    saveData();
				    load(false);
				}
			    }
			    else
			    // previous referral doesn't exist create referral
			    {
				saveData();
				load(false);
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
	load(true);
	if (sender == siteTypeComboBox)
	{
	    try
	    {
		service.findLocationsByType(IRZClient.get(siteTypeComboBox), new AsyncCallback<Location[]>() {
		    public void onSuccess(Location[] result)
		    {
			siteNameComboBox.clear();
			for (Location site : result)
			    siteNameComboBox.addItem(site.getLocationName(), site.getLocationId());
			load(false);
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
}
