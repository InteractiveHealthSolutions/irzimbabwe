
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
import org.irdresearch.irzimbabwe.shared.model.SputumTest;
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

/**
 * Sputum Registration form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class SputumRegistrationComposite extends Composite implements ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			formName						= "SPUTUM_REG";

	private UserRightsUtil				rights							= new UserRightsUtil ();
	private SputumTest					currentTest;
	private String						clientId						= "";
	private boolean						valid;

	private FlexTable					flexTable						= new FlexTable ();
	private FlexTable					topFlexTable					= new FlexTable ();
	private FlexTable					clientIdFlexTable				= new FlexTable ();
	private FlexTable					resultsFlexTable				= new FlexTable ();

	private Grid						grid							= new Grid (1, 2);
	private VerticalPanel				middleVerticalPanel				= new VerticalPanel ();
	private HorizontalPanel				clientIdHorizontalPanel			= new HorizontalPanel ();

	private Button						checkIdButton					= new Button ("Check");
	private Button						saveButton						= new Button ("Save");
	private Button						closeButton						= new Button ("Close");

	private Label						lblClientsInitialDemographics	= new Label ("Sputum Registration Form");
	private Label						lblClientsId					= new Label ("Client's ID:");
	private Label						lblSampleIdcode					= new Label ("Sample ID/Code:");
	private Label						lblSampleReceived				= new Label ("Sample Received:");
	private Label						lblSputumQuality				= new Label ("Sputum Quality:");
	private Label						lblReasonForNot					= new Label ("Reason for not Registering:");
	private Label						lblSpecifyReason				= new Label ("Specify Reason:");

	private TextBox						clientIdTextBox					= new TextBox ();
	private TextBox						sampleCodeTextBox				= new TextBox ();
	private TextBox						otherReasonTextBox				= new TextBox ();

	private ListBox						samplesListBox					= new ListBox ();
	private ListBox						sampleRegisteredComboBox		= new ListBox ();
	private ListBox						sputumQualityComboBox			= new ListBox ();
	private ListBox						reasonComboBox					= new ListBox ();

	public SputumRegistrationComposite ()
	{
		samplesListBox.setVisibleItemCount (5);
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		lblClientsInitialDemographics.setWordWrap (false);
		lblClientsInitialDemographics.setStyleName ("title");
		topFlexTable.setWidget (0, 1, lblClientsInitialDemographics);
		topFlexTable.getCellFormatter ().setHorizontalAlignment (0, 1, HasHorizontalAlignment.ALIGN_CENTER);
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
		clientIdFlexTable.setWidget (1, 0, samplesListBox);
		clientIdFlexTable.setWidget (1, 1, resultsFlexTable);
		lblSampleReceived.setWordWrap (false);
		resultsFlexTable.setWidget (0, 0, lblSampleReceived);
		sampleRegisteredComboBox.addItem ("YES");
		sampleRegisteredComboBox.addItem ("NO");
		resultsFlexTable.setWidget (0, 1, sampleRegisteredComboBox);
		lblSampleIdcode.setWordWrap (false);
		resultsFlexTable.setWidget (1, 0, lblSampleIdcode);
		sampleCodeTextBox.setMaxLength (8);
		sampleCodeTextBox.setVisibleLength (8);
		resultsFlexTable.setWidget (1, 1, sampleCodeTextBox);
		resultsFlexTable.setWidget (2, 0, lblSputumQuality);
		sputumQualityComboBox.setName ("SPUTUM_QUALITY");
		resultsFlexTable.setWidget (2, 1, sputumQualityComboBox);
		lblReasonForNot.setWordWrap (false);
		resultsFlexTable.setWidget (3, 0, lblReasonForNot);
		reasonComboBox.setEnabled (false);
		reasonComboBox.addItem ("MISENTERED COLLECTION FORM");
		reasonComboBox.addItem ("SAMPLE CONTAINER BROKE");
		reasonComboBox.addItem ("SAMPLE CONTAINER LOST");
		reasonComboBox.addItem ("SPUTUM JAR WAS EMPTY");
		reasonComboBox.addItem ("OTHER");
		resultsFlexTable.setWidget (3, 1, reasonComboBox);
		resultsFlexTable.setWidget (4, 0, lblSpecifyReason);
		otherReasonTextBox.setVisibleLength (30);
		otherReasonTextBox.setEnabled (false);
		otherReasonTextBox.setMaxLength (50);
		resultsFlexTable.setWidget (4, 1, otherReasonTextBox);
		clientIdFlexTable.getCellFormatter ().setVerticalAlignment (1, 1, HasVerticalAlignment.ALIGN_TOP);
		clientIdFlexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);
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
		ListBox[] listBoxes = {samplesListBox, sampleRegisteredComboBox, reasonComboBox};
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
		currentTest = null;
		clientId = "";
		IRZClient.clearControls (resultsFlexTable);
		ListBox[] listBoxes = {sampleRegisteredComboBox, reasonComboBox};
		for (int i = 0; i < listBoxes.length; i++)
			listBoxes[i].setSelectedIndex (0);
		reasonComboBox.setEnabled (false);
		samplesListBox.clear ();
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
		if (samplesListBox.getItemCount () == 0)
			errorMessage.append ("Sample No.: " + CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
		if (IRZClient.get (sampleRegisteredComboBox).equals ("YES") && IRZClient.get (sampleCodeTextBox).equals (""))
			errorMessage.append ("Sample Code: " + CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
		if (IRZClient.get (sampleRegisteredComboBox).equals ("YES") && IRZClient.get (sputumQualityComboBox).equals (""))
			errorMessage.append ("Sputum Quality: " + CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
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

			EncounterId encounterId = new EncounterId (eId, clientId, pid2, formName);
			Encounter encounter = new Encounter (encounterId, IRZ.getCurrentLocation ());
			encounter.setLocationId (IRZ.getCurrentLocation ());
			encounter.setDateEntered (enteredDate);
			encounter.setDateStart (new Date ());
			encounter.setDateEnd (new Date ());
			ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults> ();
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SAMPLE_NO"), String.valueOf (currentTest.getId ().getSampleNo ())));
			String registered = IRZClient.get (sampleRegisteredComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SAMPLE_REGISTERED"), registered));
			if (registered.equals ("YES"))
			{
				String quality = IRZClient.get (sputumQualityComboBox);
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SPUTUM_QUALITY"), quality));
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SAMPLE_CODE"), IRZClient.get (sampleCodeTextBox)));
				currentTest.setSputumQuality (quality);
				currentTest.setSampleCode (IRZClient.get (sampleCodeTextBox));
			}
			else
			{
				String reason = IRZClient.get (reasonComboBox);
				String otherReason = "";
				encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "REASON"), reason));
				if (reason.equals ("OTHER"))
				{
					otherReason = IRZClient.get (otherReasonTextBox).toUpperCase ();
					encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "OTHER_REASON"), otherReason));
				}
				currentTest.setSmearRemarks (reason + ":" + otherReason);
			}
			currentTest.setRegisteredBy (IRZ.getCurrentUserName ());
			currentTest.setDateRegistered (enteredDate);

			service.saveSputumRegistration (currentTest, encounter, encounterResults.toArray (new EncounterResults[] {}), new AsyncCallback<String> ()
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
				try
				{
					service.getColumnData ("sputum_test", "sample_no", "patient_id='" + clientId + "' and date_registered is null", new AsyncCallback<String[]> ()
					{
						public void onSuccess (String[] result)
						{
							samplesListBox.clear ();
							for (String s : result)
								samplesListBox.addItem ("Sample " + s, s);
							if (result.length == 0)
								Window.alert ("No unregistered Sputum Samples were found for this Client ID. Please double check the ID.");
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
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		else if (sender == saveButton)
		{
			// If the ID has not been checked, then return
			if (currentTest == null || clientId == "")
			{
				Window.alert ("Please first check Client's ID and then select a sample from the list of unregistered samples.");
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
		if (sender == samplesListBox)
		{
			Integer sampleId = Integer.parseInt (IRZClient.get (samplesListBox));
			try
			{
				service.findSputumTest (clientId, sampleId, new AsyncCallback<SputumTest> ()
				{
					public void onSuccess (SputumTest result)
					{
						currentTest = result;
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
		else if (sender == sampleRegisteredComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("NO");
			reasonComboBox.setEnabled (choice);
			sampleCodeTextBox.setEnabled (!choice);
			sputumQualityComboBox.setEnabled (!choice);
		}
		else if (sender == reasonComboBox)
		{
			boolean choice = IRZClient.get (sender).equals ("OTHER");
			otherReasonTextBox.setEnabled (choice);
		}
	}
}
