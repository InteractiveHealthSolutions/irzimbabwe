
package org.irdresearch.irzimbabwe.client;

import java.util.ArrayList;
import java.util.Date;
import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.Encounter;
import org.irdresearch.irzimbabwe.shared.model.EncounterId;
import org.irdresearch.irzimbabwe.shared.model.EncounterResults;
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
import org.irdresearch.irzimbabwe.shared.model.Patient;
import org.irdresearch.irzimbabwe.shared.model.User;
import org.irdresearch.irzimbabwe.shared.model.Visit;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Visit Purpose form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class VisitPurposeComposite extends Composite implements ClickHandler, ValueChangeHandler<Boolean>
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			formName						= "PURPOSE";

	private UserRightsUtil				rights							= new UserRightsUtil ();
	private boolean						valid;

	private FlexTable					flexTable						= new FlexTable ();
	private FlexTable					topFlexTable					= new FlexTable ();
	private FlexTable					rightFlexTable					= new FlexTable ();
	private Grid						grid							= new Grid (1, 2);

	private HorizontalPanel				visitorTypeHorizontalPanel		= new HorizontalPanel ();

	private Button						saveButton						= new Button ("Save");
	private Button						closeButton						= new Button ("Close");

	private Label						lblVisitDate					= new Label ("Visit Date:");
	private Label						lblVisitorType					= new Label ("Visitor Type:");
	private Label						lblSite							= new Label ("External Site:");
	private Label						lblClientsInitialDemographics	= new Label ("Client's Purpose of Visit Form");
	private Label						lblPurposeOfVisit				= new Label ("Purpose of Visit:");
	private Label						lblTeam							= new Label ("Team:");
	private Label						lblClientsId					= new Label ("Client's ID:");
	private Label						clientIdLabel					= new Label ("");

	private ListBox						externalSiteComboBox			= new ListBox ();
	private ListBox						visitPurposeComboBox			= new ListBox ();
	private ListBox						siteComboBox					= new ListBox ();

	private RadioButton					psiVisitorRadioButton			= new RadioButton ("visitorType", "PSI Visitor");
	private RadioButton					externalVisitorRadioButton		= new RadioButton ("visitorType", "External");

	private DateBox						dateVisitDateBox				= new DateBox ();

	public VisitPurposeComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		lblClientsInitialDemographics.setWordWrap (false);
		lblClientsInitialDemographics.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblClientsInitialDemographics);
		topFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget (1, 0, rightFlexTable);
		rightFlexTable.setWidget (0, 0, lblVisitDate);
		rightFlexTable.setWidget (0, 1, dateVisitDateBox);
		rightFlexTable.setWidget (1, 0, lblVisitorType);
		rightFlexTable.setWidget (1, 1, visitorTypeHorizontalPanel);
		psiVisitorRadioButton.setWordWrap (false);
		psiVisitorRadioButton.setValue (true);
		visitorTypeHorizontalPanel.add (psiVisitorRadioButton);
		externalVisitorRadioButton.setWordWrap (false);
		visitorTypeHorizontalPanel.add (externalVisitorRadioButton);
		rightFlexTable.setWidget (2, 0, lblSite);
		rightFlexTable.setWidget (2, 1, externalSiteComboBox);
		externalSiteComboBox.setEnabled (false);
		lblPurposeOfVisit.setWordWrap (false);
		rightFlexTable.setWidget (3, 0, lblPurposeOfVisit);
		visitPurposeComboBox.setName ("VISIT_PURPOSE");
		rightFlexTable.setWidget (3, 1, visitPurposeComboBox);
		rightFlexTable.setWidget (4, 0, lblTeam);
		siteComboBox.setName ("TEAM");
		rightFlexTable.setWidget (4, 1, siteComboBox);
		rightFlexTable.setWidget (5, 0, lblClientsId);
		rightFlexTable.setWidget (5, 1, clientIdLabel);
		rightFlexTable.setWidget (6, 1, grid);
		grid.setSize ("100%", "100%");
		saveButton.setText ("Generate");
		saveButton.setEnabled (false);
		grid.setWidget (0, 0, saveButton);
		grid.setWidget (0, 1, closeButton);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (0, 0, HasVerticalAlignment.ALIGN_TOP);

		psiVisitorRadioButton.addValueChangeHandler (this);
		externalVisitorRadioButton.addValueChangeHandler (this);
		saveButton.addClickHandler (this);
		closeButton.addClickHandler (this);

		IRZClient.refresh (flexTable);
		dateVisitDateBox.setValue (new Date ());
		setRights (formName);
		load (true);
		externalSiteComboBox.clear ();
		try
		{
			service.getTableData ("location", new String[] {"location_id", "location_name"}, "location_type='GOVT'", new AsyncCallback<String[][]> ()
			{
				public void onSuccess (String[][] result)
				{
					for (int i = 0; i < result.length; i++)
						externalSiteComboBox.addItem (result[i][1], result[i][0]);
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
		IRZClient.clearControls (flexTable);
		clientIdLabel.setText ("Last Id generated: " + clientIdLabel.getText ());
		visitPurposeComboBox.setSelectedIndex (-1);
		siteComboBox.setSelectedIndex (-1);
		externalSiteComboBox.setSelectedIndex (-1);
		psiVisitorRadioButton.setValue (true);
		dateVisitDateBox.setValue (new Date ());
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
		if (IRZClient.get (visitPurposeComboBox).equals ("") || IRZClient.get (dateVisitDateBox.getTextBox ()).equals (""))
		{
			Window.alert (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR));
			valid = false;
			load (false);
		}
		if (dateVisitDateBox.getValue ().after (new Date ()))
		{
			Window.alert ("Visit Date: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR));
			valid = false;
			load (false);
		}
		return valid;
	}

	public void saveData ()
	{
		if (validate ())
		{
			Date enteredDate = dateVisitDateBox.getValue ();
			int eId = 0;
			final String clientId = clientIdLabel.getText ();
			String pid1 = clientId;
			String pid2 = IRZ.getCurrentUserName ();
			String purpose = IRZClient.get (visitPurposeComboBox);
			String team = IRZClient.get (siteComboBox);

			EncounterId encounterId = new EncounterId (0, pid1, pid2, formName);
			Encounter encounter = new Encounter (encounterId, IRZ.getCurrentLocation ());
			encounter.setLocationId (IRZ.getCurrentLocation ());
			encounter.setDateEntered (enteredDate);
			encounter.setDateStart (new Date ());
			encounter.setDateEnd (new Date ());
			ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults> ();
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, pid1, pid2, formName, "TEAM"), team));
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, pid1, pid2, formName, "PURPOSE"), purpose));
			Visit visit = new Visit (clientId, purpose, "", new Date ());
			Patient patient = new Patient (clientId);
			patient.setTeam (team);
			patient.setDateScreened (new Date ());
			if (externalVisitorRadioButton.getValue ())
				patient.setTreatmentSite (IRZClient.get (externalSiteComboBox));
			else
				patient.setTreatmentSite (IRZ.getCurrentLocation ());

			service.saveVisitPurpose (visit, patient, encounter, encounterResults.toArray (new EncounterResults[] {}), new AsyncCallback<String> ()
			{
				public void onSuccess (String result)
				{
					if (result.equals ("SUCCESS"))
					{
						Window.alert (CustomMessage.getInfoMessage (InfoType.INSERTED) + "\nGenerated Client's ID: " + clientId);
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
					load (false);
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

	@SuppressWarnings("deprecation")
	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == saveButton)
		{
			try
			{
				service.getObject ("patient", "count(patient_id) + 1", "team='" + IRZClient.get (siteComboBox) + "'", new AsyncCallback<String> ()
				{
					public void onSuccess (String result)
					{
						// Generate client ID
						StringBuilder clientId = new StringBuilder ();
						clientId.append (IRZ.getCurrentLocation ());
						clientId.append (String.valueOf (new Date ().getYear ()).substring (1));
						clientId.append (IRZClient.get (siteComboBox));
						String resString = String.valueOf (result);
						for (int i = 0; i < (5 - resString.length ()); i++)
							clientId.append ("0");
						clientId.append (resString);
						clientIdLabel.setText (clientId.toString ());
						saveData ();
						load (false);
					}

					public void onFailure (Throwable caught)
					{
						caught.printStackTrace ();
						load (true);
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				load (true);
			}
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	public void onValueChange (ValueChangeEvent<Boolean> event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender == externalVisitorRadioButton)
		{
			boolean choice = externalVisitorRadioButton.getValue ();
			externalSiteComboBox.setEnabled (choice);
		}
		else if (sender == psiVisitorRadioButton)
		{
			boolean choice = psiVisitorRadioButton.getValue ();
			externalSiteComboBox.setEnabled (!choice);
		}
	}
}