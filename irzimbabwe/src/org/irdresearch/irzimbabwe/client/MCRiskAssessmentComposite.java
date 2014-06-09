
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

/**
 * MC Client Risk Assessment form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MCRiskAssessmentComposite extends Composite implements ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			formName						= "MC_INTAKE";

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

	private Label						lblClientsInitialDemographics	= new Label ("MC Risk Assessment Form");
	private Label						lblClientsId					= new Label ("Client's ID:");
	private Label						lblDidClientEver				= new Label ("Did Client ever had Sexual intercourse?");
	private Label						lblApproximateAgeOn				= new Label ("Approximate Age on First intercourse:");
	private Label						lblHasTheClient					= new Label ("Has the Client been Treated for STI in last 3 months?");
	private Label						lblHasTheClient_1				= new Label ("Has the Client had intercourse in last 3 months while Intoxicated?");
	private Label						lblHasTheClient_2				= new Label ("Has the Client had intercourse in exchange for Money/Goods in past 12 months?");
	private Label						lblDidTheClient					= new Label ("Did the Client use Condom in last intercourse?");
	private Label						lblDoesTheClient				= new Label ("Does the Client have more than 1 sex partner (including Spouse)?");
	private Label						lblHowManySex					= new Label ("How many sex partners did the Client have in past 12 months?");
	private Label						lblHasClientEver				= new Label ("Has Client ever talked about Circumcision with his partner?");
	private Label						lblDoesTheClient_1				= new Label ("Does the Client except Circumcision?");
	private Label						lblWhyNot						= new Label ("Why not?");
	private Label						lblSpecifyOther					= new Label ("Specify other:");

	private TextBox						clientIdTextBox					= new TextBox ();
	private TextBox						otherReasonForRefusalTextBox	= new TextBox ();

	private IntegerBox					ageOnFirstInterCourseIntegerBox	= new IntegerBox ();
	private IntegerBox					noOfPartnersIntegerBox			= new IntegerBox ();

	private ListBox						sexualIntercourseComboBox		= new ListBox ();
	private ListBox						testedForStiComboBox			= new ListBox ();
	private ListBox						intoxicatedIntercourseComboBox	= new ListBox ();
	private ListBox						paidSexComboBox					= new ListBox ();
	private ListBox						usedCondomComboBox				= new ListBox ();
	private ListBox						moreThanOnePartnerComboBox		= new ListBox ();
	private ListBox						talkedAboutCircumcisionComboBox	= new ListBox ();
	private ListBox						acceptedCircumcisionComboBox	= new ListBox ();
	private ListBox						reasonForRefusalComboBox		= new ListBox ();

	public MCRiskAssessmentComposite ()
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
		lblDidClientEver.setWordWrap (false);
		clientIdFlexTable.setWidget (1, 0, lblDidClientEver);
		sexualIntercourseComboBox.addItem ("NO");
		sexualIntercourseComboBox.addItem ("YES");
		sexualIntercourseComboBox.addItem ("REFUSED");
		clientIdFlexTable.setWidget (1, 1, sexualIntercourseComboBox);
		clientIdFlexTable.setWidget (2, 0, lblApproximateAgeOn);
		ageOnFirstInterCourseIntegerBox.setEnabled (false);
		ageOnFirstInterCourseIntegerBox.setVisibleLength (2);
		clientIdFlexTable.setWidget (2, 1, ageOnFirstInterCourseIntegerBox);
		lblHasTheClient.setWordWrap (false);
		clientIdFlexTable.setWidget (3, 0, lblHasTheClient);
		testedForStiComboBox.addItem ("NO");
		testedForStiComboBox.addItem ("YES");
		testedForStiComboBox.addItem ("REFUSED");
		testedForStiComboBox.addItem ("NOT SURE");
		testedForStiComboBox.addItem ("SKIPPED");
		clientIdFlexTable.setWidget (3, 1, testedForStiComboBox);
		clientIdFlexTable.setWidget (4, 0, lblHasTheClient_1);
		intoxicatedIntercourseComboBox.addItem ("NO");
		intoxicatedIntercourseComboBox.addItem ("YES");
		intoxicatedIntercourseComboBox.addItem ("REFUSED");
		intoxicatedIntercourseComboBox.addItem ("NOT SURE");
		intoxicatedIntercourseComboBox.addItem ("SKIPPED");
		intoxicatedIntercourseComboBox.setEnabled (false);
		clientIdFlexTable.setWidget (4, 1, intoxicatedIntercourseComboBox);
		clientIdFlexTable.setWidget (5, 0, lblHasTheClient_2);
		paidSexComboBox.addItem ("NO");
		paidSexComboBox.addItem ("YES");
		paidSexComboBox.addItem ("REFUSED");
		paidSexComboBox.addItem ("SKIPPED");
		paidSexComboBox.setEnabled (false);
		clientIdFlexTable.setWidget (5, 1, paidSexComboBox);
		lblDidTheClient.setWordWrap (false);
		clientIdFlexTable.setWidget (6, 0, lblDidTheClient);
		usedCondomComboBox.setEnabled (false);
		usedCondomComboBox.addItem ("NO");
		usedCondomComboBox.addItem ("YES");
		usedCondomComboBox.addItem ("REFUSED");
		usedCondomComboBox.addItem ("NOT SURE");
		usedCondomComboBox.addItem ("SKIPPED");
		clientIdFlexTable.setWidget (6, 1, usedCondomComboBox);
		clientIdFlexTable.setWidget (7, 0, lblDoesTheClient);
		moreThanOnePartnerComboBox.setEnabled (false);
		moreThanOnePartnerComboBox.addItem ("NO");
		moreThanOnePartnerComboBox.addItem ("YES");
		moreThanOnePartnerComboBox.addItem ("REFUSED");
		moreThanOnePartnerComboBox.addItem ("SKIPPED");
		clientIdFlexTable.setWidget (7, 1, moreThanOnePartnerComboBox);
		clientIdFlexTable.setWidget (8, 0, lblHowManySex);
		noOfPartnersIntegerBox.setEnabled (false);
		noOfPartnersIntegerBox.setVisibleLength (2);
		clientIdFlexTable.setWidget (8, 1, noOfPartnersIntegerBox);
		clientIdFlexTable.setWidget (9, 0, lblHasClientEver);
		talkedAboutCircumcisionComboBox.setEnabled (false);
		talkedAboutCircumcisionComboBox.addItem ("NO");
		talkedAboutCircumcisionComboBox.addItem ("YES");
		talkedAboutCircumcisionComboBox.addItem ("REFUSED");
		talkedAboutCircumcisionComboBox.addItem ("NOT SURE");
		clientIdFlexTable.setWidget (9, 1, talkedAboutCircumcisionComboBox);
		clientIdFlexTable.setWidget (10, 0, lblDoesTheClient_1);
		acceptedCircumcisionComboBox.addItem ("YES");
		acceptedCircumcisionComboBox.addItem ("NO");
		clientIdFlexTable.setWidget (10, 1, acceptedCircumcisionComboBox);
		clientIdFlexTable.setWidget (11, 0, lblWhyNot);
		reasonForRefusalComboBox.setEnabled (false);
		reasonForRefusalComboBox.addItem ("WANT TO DISCUSS WITH PARTNER");
		reasonForRefusalComboBox.addItem ("WANT TIME TO THINK");
		reasonForRefusalComboBox.addItem ("FEAR OF OPERATION");
		reasonForRefusalComboBox.addItem ("HIV POSITIVE");
		reasonForRefusalComboBox.addItem ("OTHER");
		clientIdFlexTable.setWidget (11, 1, reasonForRefusalComboBox);
		clientIdFlexTable.setWidget (12, 0, lblSpecifyOther);
		otherReasonForRefusalTextBox.setEnabled (false);
		otherReasonForRefusalTextBox.setMaxLength (255);
		clientIdFlexTable.setWidget (12, 1, otherReasonForRefusalTextBox);
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
							Window.alert (CustomMessage.getInfoMessage (InfoType.ID_VALID));
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
