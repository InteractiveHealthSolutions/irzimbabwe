
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
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Sputum Results form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class SmearResultsComposite extends Composite implements ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			formName						= "SMEAR_RES";

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

	private Button						checkIdButton					= new Button ("Check ID");
	private Button						saveButton						= new Button ("Save");
	private Button						closeButton						= new Button ("Close");

	private Label						lblClientsInitialDemographics	= new Label ("Smear Results Form");
	private Label						lblClientsId					= new Label ("Client's ID:");
	private Label						lblSampleIdcode					= new Label ("Sample ID/Code:");
	private Label						sampleCodeLabel					= new Label ("");
	private Label						lblSmearResult					= new Label ("Smear Result:");
	private Label						lblSmearTestDate				= new Label ("Smear Test Date:");

	private TextBox						clientIdTextBox					= new TextBox ();

	private DateBox						dateTestedDateBox				= new DateBox ();

	private ListBox						samplesListBox					= new ListBox ();
	private ListBox						smearResultComboBox				= new ListBox ();

	public SmearResultsComposite ()
	{
		samplesListBox.setVisibleItemCount (5);
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
		clientIdHorizontalPanel.add (clientIdTextBox);
		clientIdTextBox.setName ("patient;patient_id");
		clientIdHorizontalPanel.add (checkIdButton);
		checkIdButton.setWidth ("100%");
		clientIdFlexTable.setWidget (1, 0, samplesListBox);
		clientIdFlexTable.setWidget (1, 1, resultsFlexTable);
		lblSampleIdcode.setWordWrap (false);
		resultsFlexTable.setWidget (0, 0, lblSampleIdcode);
		resultsFlexTable.setWidget (0, 1, sampleCodeLabel);
		lblSmearResult.setWordWrap (false);
		resultsFlexTable.setWidget (1, 0, lblSmearResult);
		smearResultComboBox.addItem ("NEGATIVE");
		smearResultComboBox.addItem ("1+");
		smearResultComboBox.addItem ("2+");
		smearResultComboBox.addItem ("3+");
		smearResultComboBox.addItem ("1AFB");
		smearResultComboBox.addItem ("2AFB");
		smearResultComboBox.addItem ("3AFB");
		smearResultComboBox.addItem ("4AFB");
		smearResultComboBox.addItem ("5AFB");
		smearResultComboBox.addItem ("6AFB");
		smearResultComboBox.addItem ("7AFB");
		smearResultComboBox.addItem ("8AFB");
		smearResultComboBox.addItem ("9AFB");
		resultsFlexTable.setWidget (1, 1, smearResultComboBox);
		lblSmearTestDate.setWordWrap (false);
		resultsFlexTable.setWidget (2, 0, lblSmearTestDate);
		dateTestedDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yyyy-MMM-dd")));
		resultsFlexTable.setWidget (2, 1, dateTestedDateBox);
		clientIdFlexTable.getCellFormatter ().setVerticalAlignment (1, 1, HasVerticalAlignment.ALIGN_TOP);
		clientIdFlexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);
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
		ListBox[] listBoxes = {samplesListBox};
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
		IRZClient.clearControls (resultsFlexTable);
		ListBox[] listBoxes = {smearResultComboBox};
		for (int i = 0; i < listBoxes.length; i++)
			listBoxes[i].setSelectedIndex (-1);
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
		if (dateTestedDateBox.getTextBox ().getText ().equals (""))
			errorMessage.append ("Date Tested: " + CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
		if (IRZClient.get (smearResultComboBox).equals (""))
			errorMessage.append ("Smear Result: " + CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
		if (DateTimeUtil.isFutureDate (dateTestedDateBox.getValue ()))
			errorMessage.append ("Date Tested: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		if (DateTimeUtil.compareDateOnly (dateTestedDateBox.getValue (), currentTest.getDateCollected ()) < 0)
			errorMessage.append ("Date Tested cannot be before date of Collection." + "\n");
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
			Date enteredDate = dateTestedDateBox.getValue ();
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
			String result = IRZClient.get (smearResultComboBox);
			encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName, "SMEAR_RESULT"), result));
			currentTest.setSmearResult (result);
			currentTest.setDateSmearTested (enteredDate);
			currentTest.setSmearTestedBy (IRZ.getCurrentUserName ());

			service.saveSmearResult (currentTest, encounter, encounterResults.toArray (new EncounterResults[] {}), new AsyncCallback<String> ()
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
					service.getColumnData ("sputum_test", "sample_no", "patient_id='" + clientId + "' and sample_code is not null", new AsyncCallback<String[]> ()
					{
						public void onSuccess (String[] result)
						{
							samplesListBox.clear ();
							for (String s : result)
								samplesListBox.addItem ("Sample " + s, s);
							if (result.length == 0)
								Window.alert ("No registered Sputum Samples were found for this Client ID. Please make sure that the ID is entered correctly.");
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
				Window.alert ("Please first check Client's ID and then select a sample from the list of registered samples.");
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
						sampleCodeLabel.setText (String.valueOf (currentTest.getSampleCode ()));
						smearResultComboBox.setSelectedIndex (IRZClient.getIndex (smearResultComboBox, currentTest.getSmearResult ()));
						dateTestedDateBox.setValue (currentTest.getDateSmearTested ());
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
	}
}
