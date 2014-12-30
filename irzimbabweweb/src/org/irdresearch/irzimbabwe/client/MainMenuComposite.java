/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Main Menu Composite for TB CONTROL client
 */

package org.irdresearch.irzimbabwe.client;

import java.util.Date;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;

public class MainMenuComposite extends Composite
{
	private static VerticalPanel	mainVerticalPanel				= new VerticalPanel ();
	private static FlexTable		mainFlexTable					= new FlexTable ();
	private static VerticalPanel	middleVerticalPanel				= new VerticalPanel ();
	private static SimplePanel		loadingPanel					= new SimplePanel ();
	private SimplePanel				inquiryCompositePanel			= new SimplePanel ();
	private InquiryComposite		inquiryComposite				= new InquiryComposite ();
	private ClientProfilePopup		clientProfile					=new ClientProfilePopup();
	private Label					lblVisitorInquiryForm			= new Label ("Visitor Inquiry Form");

	private MenuBar					mainMenuBar						= new MenuBar (false);
	private MenuBar					setupMenuBar					= new MenuBar (true);
	private MenuBar					formsMenuBar					= new MenuBar (true);
	private MenuBar					reportingMenuBar				= new MenuBar (true);
	private MenuBar					toolsMenuBar					= new MenuBar (true);
	private MenuBar					helpMenuBar						= new MenuBar (true);

	private MenuItem				setupMenuItem					= new MenuItem ("Setup", false, setupMenuBar);
	private MenuItem				formsMenuItem					= new MenuItem ("Forms", false, formsMenuBar);
	private MenuItem				reportingMenuItem				= new MenuItem ("Reporting", false, reportingMenuBar);
	private MenuItem				helpMenuItem					= new MenuItem ("Help", false, helpMenuBar);

	private MenuItem				definitionsMenuItem				= new MenuItem ("Definitions", false, (Command) null);					;
	private MenuItem				defaultsMenuItem				= new MenuItem ("Defaults", false, (Command) null);						;
	private MenuItem				locationsMenuItem				= new MenuItem ("Site Registration", false, (Command) null);
	private MenuItem				smsRuleSetupMenuItem			= new MenuItem ("SMS Rule Setup", false, (Command) null);
	private MenuItem				smsTextSetupMenuItem			= new MenuItem ("SMS Text Setup", false, (Command) null);
	private MenuItem				usersMenuItem					= new MenuItem ("Users", false, (Command) null);
	private MenuItem				userRightsMenuItem				= new MenuItem ("User Rights", false, (Command) null);
	private MenuItem				userMappingMenuItem				= new MenuItem ("User Mapping", false, (Command) null);
	private MenuItem				encounterTypeMenuItem			= new MenuItem ("Encounter Types", false, (Command) null);
	private MenuItem				encounterElementMenuItem		= new MenuItem ("Encounter Elements", false, (Command) null);
	private MenuItem				encounterPrerequisiteMenuItem	= new MenuItem ("Encounter Pre-requisites", false, (Command) null);
	private MenuItem				encountersMenuItem				= new MenuItem ("Encounters", false, (Command) null);

	private MenuItem				visitPurposeMenuItem			= new MenuItem ("Client's Purpose of Visit", false, (Command) null);
	private MenuItem				clientDemographicsMenuItem		= new MenuItem ("Client's Demographics", false, (Command) null);
	private MenuItem				tbScreeningMenuItem				= new MenuItem ("TB Screening Results", false, (Command) null);
	private MenuItem				registrationMenuItem			= new MenuItem ("Client Registration Form", false, (Command) null);
	private MenuItem				sputumCollectionMenuItem		= new MenuItem ("Sputum Collection", false, (Command) null);
	private MenuItem				sputumRegistrationMenuItem		= new MenuItem ("Sputum Registration", false, (Command) null);
	private MenuItem				smearResultsMenuItem			= new MenuItem ("Smear Results", false, (Command) null);
	private MenuItem				gxpResultsMenuItem				= new MenuItem ("Gene Xpert Results", false, (Command) null);
	private MenuItem				referralMenuItem				= new MenuItem ("Client Referral", false, (Command) null);
	private MenuItem				mcReferralMenuItem				= new MenuItem ("MC Referral", false, (Command) null);
	private MenuItem				mcIntakeMenuItem				= new MenuItem ("MC Intake", false, (Command) null);
	private MenuItem				mcRiskAssessmentMenuItem		= new MenuItem ("MC Risk Assessment", false, (Command) null);
	private MenuItem				mcPreopAssessmentMenuItem		= new MenuItem ("MC Pre-Operation Assessment", false, (Command) null);
	private MenuItem				mcPhysicalExaminationMenuItem	= new MenuItem ("MC Physical Examination", false, (Command) null);
	private MenuItem				mcOperationNotesMenuItem		= new MenuItem ("MC Operation Notes", false, (Command) null);
	private MenuItem				mcVisitNotesMenuItem			= new MenuItem ("MC Visit Notes", false, (Command) null);
	private MenuItem				clientEditMenuItem				= new MenuItem ("Client Edit", false, (Command) null);

	private MenuItem				dashboardMenuItem				= new MenuItem ("Dashboard", false, (Command) null);
	private MenuItem				reportsMenuItem					= new MenuItem ("Reports", false, (Command) null);
	private MenuItem				patientSearchMenuItem			= new MenuItem ("Patient Search", false, (Command) null);
	private MenuItem				logsMenuItem					= new MenuItem ("Logs", false, (Command) null);
	private MenuItem				smsMenuItem						= new MenuItem ("SMS", false, (Command) null);

	private MenuItem				toolsMenuItem					= new MenuItem ("Tools", false, toolsMenuBar);
	private MenuItem				dataCleaningMenuItem			= new MenuItem ("Data Cleaning", false, (Command) null);

	private MenuItem				aboutMenuItem					= new MenuItem ("About Us", false, (Command) null);
	private MenuItem				aboutMeMenuItem					= new MenuItem ("About Me", false, (Command) null);
	private MenuItem				helpContentsMenuItem			= new MenuItem ("Help Contents", false, (Command) null);
	private MenuItem				feedbackMenuItem				= new MenuItem ("Feedback", false, (Command) null);
	private MenuItem				logoutMenuItem					= new MenuItem ("Logout", false, (Command) null);

	@SuppressWarnings("deprecation")
	public MainMenuComposite ()
	{
		initWidget (mainVerticalPanel);
		mainVerticalPanel.add (loadingPanel);
		mainVerticalPanel.setCellVerticalAlignment (loadingPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		mainVerticalPanel.setCellHorizontalAlignment (loadingPanel, HasHorizontalAlignment.ALIGN_CENTER);
		loadingPanel.setSize ("100%", "50%");
		mainVerticalPanel.add (mainFlexTable);
		lblVisitorInquiryForm.setWordWrap (false);
		mainFlexTable.setWidget (0, 0, lblVisitorInquiryForm);
		lblVisitorInquiryForm.setWidth ("50%");
		mainMenuBar.setFocusOnHoverEnabled (false);
		mainFlexTable.setWidget (0, 1, mainMenuBar);
		mainFlexTable.setWidget (1, 0, inquiryCompositePanel);
		inquiryCompositePanel.setWidth ("25%");
		middleVerticalPanel = new VerticalPanel ();
		mainFlexTable.setWidget (1, 1, middleVerticalPanel);
		middleVerticalPanel.setStyleName ("mainVerticalPanel");
		middleVerticalPanel.setSize ("100%", "100%");
		mainFlexTable.getCellFormatter ().setHorizontalAlignment (0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		mainFlexTable.setSize ("800px", "50%");
		mainFlexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		mainMenuBar.setHeight ("100%");
		setupMenuBar.setAutoOpen (true);
		setupMenuBar.setAnimationEnabled (true);
		formsMenuBar.setAutoOpen (true);
		formsMenuBar.setAnimationEnabled (true);
		reportingMenuBar.setAutoOpen (true);
		reportingMenuBar.setAnimationEnabled (true);
		helpMenuBar.setAutoOpen (true);
		helpMenuBar.setAnimationEnabled (true);
		inquiryComposite.setWidth ("100%");
		inquiryCompositePanel.setWidget (inquiryComposite.asWidget ());
		clientProfile.setWidth("100%");
		
		
		loadingPanel.add (new LoadingWidget ().asWidget ());
		mainFlexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		mainFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_LEFT);

		userMappingMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SETUP");
				middleVerticalPanel.add (new UserMappingComposite ().asWidget ());
			}
		});
		patientSearchMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DATALOG");
				middleVerticalPanel.add (new Report_PatientComposite ().asWidget ());
			}
		});
		defaultsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DEFINITION");
				middleVerticalPanel.add (new DefaultsComposite ().asWidget ());
			}
		});
		smsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SMS");
			}
		});
		definitionsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DEFINITION");
				middleVerticalPanel.add (new DefinitionComposite ().asWidget ());
			}
		});
		locationsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SETUP");
				middleVerticalPanel.add (new LocationComposite ().asWidget ());
			}
		});
		smsRuleSetupMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SETUP");
				middleVerticalPanel.add (new SMSRulesComposite ().asWidget ());
			}
		});
		smsTextSetupMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SETUP");
				middleVerticalPanel.add (new SMSTextComposite ().asWidget ());
			}
		});
		usersMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SETUP");
				middleVerticalPanel.add (new UsersComposite ().asWidget ());
			}
		});
		userRightsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SETUP");
				middleVerticalPanel.add (new UserRightsComposite ().asWidget ());
			}
		});
		visitPurposeMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "PURPOSE");
				middleVerticalPanel.add (new VisitPurposeComposite ().asWidget ());
			}
		});
		clientDemographicsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DEMOG");
				middleVerticalPanel.add (new DemographicsComposite ().asWidget ());
			}
		});
		tbScreeningMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "TB_SCREEN");
				middleVerticalPanel.add (new TBScreeningComposite ().asWidget ());
			}
		});
		registrationMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "TB_REG");
				middleVerticalPanel.add (new RegistrationComposite ().asWidget ());
			}
		});
		sputumCollectionMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SPUTUM_COL");
				middleVerticalPanel.add (new SputumCollectionComposite ().asWidget ());
			}
		});
		sputumRegistrationMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SPUTUM_REG");
				middleVerticalPanel.add (new SputumRegistrationComposite ().asWidget ());
			}
		});
		smearResultsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SMEAR_RES");
				middleVerticalPanel.add (new SmearResultsComposite ().asWidget ());
			}
		});
		gxpResultsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "GXP_RES");
				middleVerticalPanel.add (new GeneXpertResultsComposite ().asWidget ());
			}
		});
		referralMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "REFERRAL");
				middleVerticalPanel.add (new ReferralComposite ().asWidget ());
			}
		});
		mcReferralMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "MC_REFER");
				middleVerticalPanel.add (new MCReferralComposite ().asWidget ());
			}
		});
		mcIntakeMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "MC_INTAKE");
				middleVerticalPanel.add (new MCIntakeComposite ().asWidget ());
			}
		});
		mcRiskAssessmentMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "MC_ASSESS");
				middleVerticalPanel.add (new MCRiskAssessmentComposite ().asWidget ());
			}
		});
		mcPreopAssessmentMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "MC_PREOP");
				middleVerticalPanel.add (new MCPreopAssessmentComposite ().asWidget ());
			}
		});
		mcPhysicalExaminationMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "MC_EXAM");
				middleVerticalPanel.add (new MCPhysicalExaminationComposite ().asWidget ());
			}
		});
		mcOperationNotesMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "MC_OPERAT");
				middleVerticalPanel.add (new MCOperationNotesComposite ().asWidget ());
			}
		});
		mcVisitNotesMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "MC_VISIT");
				middleVerticalPanel.add (new MCVisitNotesComposite ().asWidget ());
			}
		});
		clientEditMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "CLIENT_EDT");
				middleVerticalPanel.add (new PatientComposite ().asWidget ());
			}
		});
		encounterTypeMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "ENCOUNTER");
				middleVerticalPanel.add (new EncounterTypeComposite ().asWidget ());
			}
		});
		encounterElementMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "ENCOUNTER");
				middleVerticalPanel.add (new EncounterElementComposite ().asWidget ());
			}
		});
		encounterPrerequisiteMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "ENCOUNTER");
				middleVerticalPanel.add (new EncounterPrerequisiteComposite ().asWidget ());
			}
		});
		encountersMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "ENCOUNTER");
				middleVerticalPanel.add (new EncounterComposite ().asWidget ());
			}
		});
		dashboardMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DATALOG");
				middleVerticalPanel.add (new Report_DashboardComposite ().asWidget ());
			}
		});
		reportsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DATALOG");
				middleVerticalPanel.add (new Report_ReportsComposite ().asWidget ());
			}
		});
		logsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DATALOG");
				middleVerticalPanel.add (new Report_LogComposite ().asWidget ());
			}
		});
		feedbackMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "FEEDBACK");
				middleVerticalPanel.add (new FeedbackComposite ().asWidget ());
			}
		});
		dataCleaningMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SETUP");
				middleVerticalPanel.add (new DataCleaningComposite ().asWidget ());
			}
		});
		aboutMeMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				try
				{
					String user = Cookies.getCookie ("UserName");
					Date loginDate = new Date (Long.parseLong (Cookies.getCookie ("LoginTime")));
					int mins = new Date (new Date ().getTime () - loginDate.getTime ()).getMinutes ();
					String str = "CURRENT USER: " + user + "\n" + "LOGIN TIME: " + loginDate.toGMTString ().replace ("GMT", "") + "\n" + "CURRENT SESSION: " + mins + " mins";
					Window.alert (str);
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
		});
		aboutMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "About Us");
				middleVerticalPanel.add (new AboutUsComposite ().asWidget ());
			}
		});
		helpContentsMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				Window.alert ("Help file(s) not found.");
			}
		});
		logoutMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				IRZimbabwe.logout ();
			}
		});
		
		mainMenuBar.addItem (setupMenuItem);
		mainMenuBar.addItem (formsMenuItem);
		mainMenuBar.addItem (reportingMenuItem);
		mainMenuBar.addItem (toolsMenuItem);
		mainMenuBar.addItem (helpMenuItem);
		mainMenuBar.addItem (logoutMenuItem);

		setupMenuBar.addItem (definitionsMenuItem);
		// setupMenuBar.addItem (defaultsMenuItem);
		setupMenuBar.addItem (locationsMenuItem);
		setupMenuBar.addItem (smsRuleSetupMenuItem);
		setupMenuBar.addItem (smsTextSetupMenuItem);
		setupMenuBar.addItem (usersMenuItem);
		setupMenuBar.addItem (userRightsMenuItem);
		// setupMenuBar.addItem (userMappingMenuItem);
		setupMenuBar.addItem (encounterTypeMenuItem);
		setupMenuBar.addItem (encounterElementMenuItem);
		setupMenuBar.addItem (encounterPrerequisiteMenuItem);
		setupMenuBar.addItem (encountersMenuItem);

		formsMenuBar.addItem (visitPurposeMenuItem);
		formsMenuBar.addItem (clientDemographicsMenuItem);
		formsMenuBar.addItem (tbScreeningMenuItem);
		formsMenuBar.addItem (registrationMenuItem);
		formsMenuBar.addItem (sputumCollectionMenuItem);
		formsMenuBar.addItem (sputumRegistrationMenuItem);
		formsMenuBar.addItem (smearResultsMenuItem);
		formsMenuBar.addItem (gxpResultsMenuItem);
		formsMenuBar.addItem (referralMenuItem);
		formsMenuBar.addItem (mcReferralMenuItem);
		formsMenuBar.addItem (mcIntakeMenuItem);
		formsMenuBar.addItem (mcRiskAssessmentMenuItem);
		formsMenuBar.addItem (mcPreopAssessmentMenuItem);
		formsMenuBar.addItem (mcPhysicalExaminationMenuItem);
		formsMenuBar.addItem (mcOperationNotesMenuItem);
		formsMenuBar.addItem (mcVisitNotesMenuItem);
		formsMenuBar.addItem (clientEditMenuItem);

		reportingMenuBar.addItem (dashboardMenuItem);
		reportingMenuBar.addItem (reportsMenuItem);
		// reportingMenuBar.addItem (patientSearchMenuItem);
		reportingMenuBar.addItem (logsMenuItem);

		toolsMenuBar.addItem (dataCleaningMenuItem);

		helpMenuBar.addItem (aboutMeMenuItem);
		helpMenuBar.addItem (aboutMenuItem);
		helpMenuBar.addItem (feedbackMenuItem);
		helpMenuBar.addItem (helpContentsMenuItem);
		
		hideLoading ();
	}

	public static void showLoading ()
	{
		loadingPanel.setVisible (true);
		mainFlexTable.setVisible (false);
	}

	public static void hideLoading ()
	{
		loadingPanel.setVisible (false);
		mainFlexTable.setVisible (true);
	}

	public static void clear ()
	{
		Cookies.setCookie ("CurrentMenu", "");
		middleVerticalPanel.clear ();
		hideLoading ();
	}
}
