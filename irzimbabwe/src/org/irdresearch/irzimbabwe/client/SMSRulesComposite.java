
package org.irdresearch.irzimbabwe.client;

import java.util.ArrayList;
import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.SmsType;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.SmsRule;
import org.irdresearch.irzimbabwe.shared.model.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TB Screening form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class SMSRulesComposite extends Composite implements ClickHandler, ValueChangeHandler<Boolean>
{
	private static ServerServiceAsync	service									= GWT.create (ServerService.class);
	private static final String			formName								= "SETUP";

	private UserRightsUtil				rights									= new UserRightsUtil ();
	private SmsRule[]					currentRules;
	private boolean						valid;

	private VerticalPanel				middleVerticalPanel						= new VerticalPanel ();

	private FlexTable					flexTable								= new FlexTable ();
	private FlexTable					topFlexTable							= new FlexTable ();
	private FlexTable					rulesFlexTable							= new FlexTable ();

	private Grid						grid									= new Grid (1, 2);
	private Grid						tbRemindersGrid							= new Grid (7, 2);

	private Button						saveButton								= new Button ("Save");
	private Button						closeButton								= new Button ("Close");

	private Label						lblClientsInitialDemographics			= new Label ("SMS Rules Setup");
	private Label						lblFirstWeek							= new Label ("First Week:");
	private Label						lblSecondWeek							= new Label ("Second Week:");
	private Label						lblThirdWeek							= new Label ("Third Week:");
	private Label						lblFourthWeek							= new Label ("Fourth Week:");
	private Label						lblSecondMonth							= new Label ("Second Month:");
	private Label						lblThirdMonth							= new Label ("Third Month:");
	private Label						lblFourthMonth							= new Label ("Onwards:");
	private Label						lblTheSectionBelow						= new Label (
																						"All controls of this section must be redefined everytime there is a change in Clients' Referral Reminders.");

	private CheckBox					day2MCReminderToClientCheckBox			= new CheckBox ("Enable 2nd day reminder on MC to Client");
	private CheckBox					day7MCReminderToClientCheckBox			= new CheckBox ("Enable 7th day reminder on MC to Client");
	private CheckBox					day42MCReminderToClientCheckBox			= new CheckBox ("Enable 42nd day reminder on MC to Client");
	private CheckBox					customRemindersCheckBox					= new CheckBox ("Enable custom reminders on MC to Client");
	private CheckBox					tbSputumResultsAlertToClientCheckBox	= new CheckBox ("Enable alert on TB Sputum Results to Client");
	private CheckBox					siteReferralAlertCheckBox				= new CheckBox ("Enable alert on Client Referral to TB Treatment Site");
	private CheckBox					tbSputumResultsAlertToSiteCheckBox		= new CheckBox ("Enable alert on TB Sputum Results to Treatment Site");
	private CheckBox					tbReferralRemindersCheckBox				= new CheckBox ("Enable reminders on TB Referral to Client");

	private ListBox						tbSputumResultsAlertToClientComboBox	= new ListBox ();
	private ListBox						siteReferralAlertComboBox				= new ListBox ();
	private ListBox						tbSputumResultsAlertToSiteComboBox		= new ListBox ();
	private ListBox						tbWeek1RemindersComboBox				= new ListBox ();
	private ListBox						tbWeek2RemindersComboBox				= new ListBox ();
	private ListBox						tbWeek3RemindersComboBox				= new ListBox ();
	private ListBox						tbWeek4RemindersComboBox				= new ListBox ();
	private ListBox						tbMonth2RemindersComboBox				= new ListBox ();
	private ListBox						tbMonth3RemindersComboBox				= new ListBox ();
	private ListBox						tbOnwardsRemindersComboBox				= new ListBox ();

	public SMSRulesComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		lblClientsInitialDemographics.setWordWrap (false);
		lblClientsInitialDemographics.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblClientsInitialDemographics);
		topFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget (1, 0, middleVerticalPanel);
		middleVerticalPanel.add (rulesFlexTable);
		rulesFlexTable.setWidth ("100%");
		day2MCReminderToClientCheckBox.setWordWrap (false);
		rulesFlexTable.setWidget (0, 0, day2MCReminderToClientCheckBox);
		day7MCReminderToClientCheckBox.setWordWrap (false);
		rulesFlexTable.setWidget (1, 0, day7MCReminderToClientCheckBox);
		day42MCReminderToClientCheckBox.setWordWrap (false);
		rulesFlexTable.setWidget (2, 0, day42MCReminderToClientCheckBox);
		rulesFlexTable.setWidget (3, 0, customRemindersCheckBox);
		tbSputumResultsAlertToClientCheckBox.setWordWrap (false);
		rulesFlexTable.setWidget (4, 0, tbSputumResultsAlertToClientCheckBox);
		tbSputumResultsAlertToClientComboBox.addItem ("SEND INSTANTLY", "0");
		tbSputumResultsAlertToClientComboBox.addItem ("SEND NEXT DAY", "1");
		tbSputumResultsAlertToClientComboBox.setEnabled (false);
		rulesFlexTable.setWidget (4, 1, tbSputumResultsAlertToClientComboBox);
		tbSputumResultsAlertToSiteCheckBox.setWordWrap (false);
		rulesFlexTable.setWidget (5, 0, tbSputumResultsAlertToSiteCheckBox);
		tbSputumResultsAlertToSiteComboBox.addItem ("SEND INSTANTLY", "0");
		tbSputumResultsAlertToSiteComboBox.addItem ("SEND NEXT DAY", "1");
		tbSputumResultsAlertToSiteComboBox.setEnabled (false);
		rulesFlexTable.setWidget (5, 1, tbSputumResultsAlertToSiteComboBox);
		rulesFlexTable.setWidget (6, 0, siteReferralAlertCheckBox);
		siteReferralAlertComboBox.setEnabled (false);
		siteReferralAlertComboBox.addItem ("SEND INSTANTLY", "0");
		siteReferralAlertComboBox.addItem ("SEND NEXT DAY", "1");
		rulesFlexTable.setWidget (6, 1, siteReferralAlertComboBox);
		rulesFlexTable.setWidget (7, 0, tbReferralRemindersCheckBox);
		rulesFlexTable.setWidget (8, 0, lblTheSectionBelow);
		rulesFlexTable.setWidget (9, 0, tbRemindersGrid);
		tbRemindersGrid.setWidget (0, 0, lblFirstWeek);
		lblFirstWeek.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		tbWeek1RemindersComboBox.setEnabled (false);
		tbRemindersGrid.setWidget (0, 1, tbWeek1RemindersComboBox);
		tbWeek1RemindersComboBox.addItem ("NONE", "0");
		tbWeek1RemindersComboBox.addItem ("ONCE", "1");
		tbWeek1RemindersComboBox.addItem ("DAILY", "1");
		tbWeek1RemindersComboBox.addItem ("ALTERNATE DAYS", "2");
		tbRemindersGrid.setWidget (1, 0, lblSecondWeek);
		lblSecondWeek.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		tbWeek2RemindersComboBox.setEnabled (false);
		tbRemindersGrid.setWidget (1, 1, tbWeek2RemindersComboBox);
		tbWeek2RemindersComboBox.addItem ("NONE", "0");
		tbWeek2RemindersComboBox.addItem ("ONCE", "1");
		tbWeek2RemindersComboBox.addItem ("DAILY", "1");
		tbWeek2RemindersComboBox.addItem ("ALTERNATE DAYS", "2");
		tbRemindersGrid.setWidget (2, 0, lblThirdWeek);
		lblThirdWeek.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		tbWeek3RemindersComboBox.setEnabled (false);
		tbRemindersGrid.setWidget (2, 1, tbWeek3RemindersComboBox);
		tbWeek3RemindersComboBox.addItem ("NONE", "0");
		tbWeek3RemindersComboBox.addItem ("ONCE", "1");
		tbWeek3RemindersComboBox.addItem ("DAILY", "1");
		tbWeek3RemindersComboBox.addItem ("ALTERNATE DAYS", "2");
		tbRemindersGrid.setWidget (3, 0, lblFourthWeek);
		lblFourthWeek.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		tbWeek4RemindersComboBox.setEnabled (false);
		tbRemindersGrid.setWidget (3, 1, tbWeek4RemindersComboBox);
		tbWeek4RemindersComboBox.addItem ("NONE", "0");
		tbWeek4RemindersComboBox.addItem ("ONCE", "1");
		tbWeek4RemindersComboBox.addItem ("DAILY", "1");
		tbWeek4RemindersComboBox.addItem ("ALTERNATE DAYS", "2");
		tbRemindersGrid.setWidget (4, 0, lblSecondMonth);
		lblSecondMonth.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		tbMonth2RemindersComboBox.setEnabled (false);
		tbRemindersGrid.setWidget (4, 1, tbMonth2RemindersComboBox);
		tbMonth2RemindersComboBox.addItem ("NONE", "0");
		tbMonth2RemindersComboBox.addItem ("ONCE", "1");
		tbMonth2RemindersComboBox.addItem ("DAILY", "1");
		tbMonth2RemindersComboBox.addItem ("ALTERNATE DAYS", "2");
		tbMonth2RemindersComboBox.addItem ("WEEKLY", "7");
		tbMonth2RemindersComboBox.addItem ("BI-WEEKLY", "14");
		tbRemindersGrid.setWidget (5, 0, lblThirdMonth);
		lblThirdMonth.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		tbMonth3RemindersComboBox.setEnabled (false);
		tbRemindersGrid.setWidget (5, 1, tbMonth3RemindersComboBox);
		tbMonth3RemindersComboBox.addItem ("NONE", "0");
		tbMonth3RemindersComboBox.addItem ("ONCE", "1");
		tbMonth3RemindersComboBox.addItem ("DAILY", "1");
		tbMonth3RemindersComboBox.addItem ("ALTERNATE DAYS", "2");
		tbMonth3RemindersComboBox.addItem ("WEEKLY", "7");
		tbMonth3RemindersComboBox.addItem ("BI-WEEKLY", "14");
		tbRemindersGrid.setWidget (6, 0, lblFourthMonth);
		lblFourthMonth.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_RIGHT);
		tbOnwardsRemindersComboBox.setEnabled (false);
		tbRemindersGrid.setWidget (6, 1, tbOnwardsRemindersComboBox);
		tbOnwardsRemindersComboBox.addItem ("NONE", "0");
		tbOnwardsRemindersComboBox.addItem ("ONCE", "1");
		tbOnwardsRemindersComboBox.addItem ("DAILY", "1");
		tbOnwardsRemindersComboBox.addItem ("ALTERNATE DAYS", "2");
		tbOnwardsRemindersComboBox.addItem ("WEEKLY", "7");
		tbOnwardsRemindersComboBox.addItem ("BI-WEEKLY", "14");
		tbOnwardsRemindersComboBox.addItem ("MONTHLY", "30");
		rulesFlexTable.getCellFormatter ().setHorizontalAlignment (9, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		rulesFlexTable.getCellFormatter ().setHorizontalAlignment (8, 0, HasHorizontalAlignment.ALIGN_CENTER);
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

		day2MCReminderToClientCheckBox.addValueChangeHandler (this);
		day7MCReminderToClientCheckBox.addValueChangeHandler (this);
		day42MCReminderToClientCheckBox.addValueChangeHandler (this);
		customRemindersCheckBox.addValueChangeHandler (this);
		tbSputumResultsAlertToClientCheckBox.addValueChangeHandler (this);
		siteReferralAlertCheckBox.addValueChangeHandler (this);
		tbSputumResultsAlertToSiteCheckBox.addValueChangeHandler (this);
		tbReferralRemindersCheckBox.addValueChangeHandler (this);

		closeButton.addClickHandler (this);
		saveButton.addClickHandler (this);

		IRZClient.refresh (flexTable);
		setRights (formName);
		try
		{
			service.findSmsRules (new AsyncCallback<SmsRule[]> ()
			{
				public void onSuccess (SmsRule[] result)
				{
					currentRules = result;
					if (currentRules != null)
					{
						fillData ();
					}
				}

				public void onFailure (Throwable caught)
				{
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
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
		Widget[] mandatory = {};
		for (int i = 0; i < mandatory.length; i++)
		{
			if (IRZClient.get (mandatory[i]).equals (""))
			{
				valid = false;
				break;
			}
		}
		if (!valid)
		{
			Window.alert (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR));
			load (false);
		}
		return valid;
	}

	protected void fillData ()
	{
		for (SmsRule rule : currentRules)
		{
			if (rule.getRuleId ().equals ("MC_CUSTOM"))
				customRemindersCheckBox.setValue (rule.getIsEnabled ());
			else if (rule.getRuleId ().equals ("MC_DAY2"))
				day2MCReminderToClientCheckBox.setValue (rule.getIsEnabled ());
			else if (rule.getRuleId ().equals ("MC_DAY7"))
				day7MCReminderToClientCheckBox.setValue (rule.getIsEnabled ());
			else if (rule.getRuleId ().equals ("MC_DAY42"))
				day42MCReminderToClientCheckBox.setValue (rule.getIsEnabled ());
			else if (rule.getRuleId ().equals ("TB_RES_C"))
			{
				tbSputumResultsAlertToClientCheckBox.setValue (rule.getIsEnabled ());
				tbSputumResultsAlertToClientComboBox.setEnabled (rule.getIsEnabled ());
				if (rule.getIsEnabled ())
					tbSputumResultsAlertToClientComboBox.setSelectedIndex (IRZClient.getIndex (tbSputumResultsAlertToClientComboBox, rule.getStartDay ().toString ()));
			}
			else if (rule.getRuleId ().equals ("TB_RES_S"))
			{
				tbSputumResultsAlertToSiteCheckBox.setValue (rule.getIsEnabled ());
				tbSputumResultsAlertToSiteComboBox.setEnabled (rule.getIsEnabled ());
				if (rule.getIsEnabled ())
					tbSputumResultsAlertToSiteComboBox.setSelectedIndex (IRZClient.getIndex (tbSputumResultsAlertToSiteComboBox, rule.getStartDay ().toString ()));
			}
			else if (rule.getRuleId ().equals ("TB_REF_S"))
			{
				siteReferralAlertCheckBox.setValue (rule.getIsEnabled ());
				siteReferralAlertComboBox.setEnabled (rule.getIsEnabled ());
				if (rule.getIsEnabled ())
					siteReferralAlertComboBox.setSelectedIndex (IRZClient.getIndex (siteReferralAlertComboBox, rule.getStartDay ().toString ()));
			}
			else
			{
				if (rule.getIsEnabled ())
				{
					tbReferralRemindersCheckBox.setValue (true);
					tbWeek1RemindersComboBox.setEnabled (true);
					tbWeek2RemindersComboBox.setEnabled (true);
					tbWeek3RemindersComboBox.setEnabled (true);
					tbWeek4RemindersComboBox.setEnabled (true);
					tbMonth2RemindersComboBox.setEnabled (true);
					tbMonth3RemindersComboBox.setEnabled (true);
					tbOnwardsRemindersComboBox.setEnabled (true);
				}
			}
		}
	}

	public void saveData ()
	{
		if (validate ())
		{
			try
			{
				ArrayList<SmsRule> rules = new ArrayList<SmsRule> ();
				String ruleId;
				String ruleName;
				SmsType type;
				String rule;
				Boolean isEnabled;
				Integer startDay;
				Integer iterations;
				Integer smsInterval;
				Integer endDay = 0;
				String description;
				Integer serviceRunupMinutes = 60;

				// Add rule for Day 2 MC Reminder
				ruleId = "MC_DAY2";
				ruleName = "Reminder for Day 2 Follow-up for Circumcision";
				type = SmsType.REMINDER;
				rule = "";
				isEnabled = day2MCReminderToClientCheckBox.getValue ();
				startDay = 2;
				iterations = 2;
				smsInterval = 1;
				description = "Day 2 MC Reminder to Client";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Add rule for Day 7 MC Reminder to Client
				ruleId = "MC_DAY7";
				ruleName = "Reminder for Day 7 Follow-up for Circumcision";
				type = SmsType.REMINDER;
				rule = "";
				isEnabled = day7MCReminderToClientCheckBox.getValue ();
				startDay = 6;
				iterations = 3;
				smsInterval = 1;
				description = "Day 7 MC Reminder to Client";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Add rule for Day 42 MC Reminder to Client
				ruleId = "MC_DAY42";
				ruleName = "Reminder for Day 42 Follow-up for Circumcision";
				type = SmsType.REMINDER;
				rule = "";
				isEnabled = day42MCReminderToClientCheckBox.getValue ();
				startDay = 41;
				iterations = 3;
				smsInterval = 1;
				description = "Day 42 MC Reminder to Client";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Add rule for Custom MC Reminder to Client
				ruleId = "MC_CUSTOM";
				ruleName = "Reminder for Custom Follow-up for Circumcision";
				type = SmsType.REMINDER;
				rule = "";
				isEnabled = customRemindersCheckBox.getValue ();
				startDay = 0;
				iterations = 1;
				smsInterval = 0;
				description = "Custom MC Reminder";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Add rule for Sputum Results Reminders to Client
				ruleId = "TB_RES_C";
				ruleName = "Client TB Sputum Results Reminders";
				type = SmsType.REMINDER;
				rule = "";
				isEnabled = tbSputumResultsAlertToClientCheckBox.getValue ();
				startDay = Integer.parseInt (IRZClient.get (tbSputumResultsAlertToClientComboBox));
				iterations = 5;
				smsInterval = 1;
				description = "Alert to Client on completion of all Lab tests";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Add rule for Sputum Results alert to Site
				ruleId = "TB_RES_S";
				ruleName = "Site TB Sputum Results Alert";
				type = SmsType.ALERT;
				rule = "";
				isEnabled = tbSputumResultsAlertToSiteCheckBox.getValue ();
				startDay = Integer.parseInt (IRZClient.get (tbSputumResultsAlertToSiteComboBox));
				iterations = 1;
				smsInterval = 0;
				description = "Alert to Client's Treatment Site on completion of all Lab tests";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Add rule for Referral alert to Site
				ruleId = "TB_REF_S";
				ruleName = "Site Referral Alert";
				type = SmsType.ALERT;
				rule = "";
				isEnabled = siteReferralAlertCheckBox.getValue ();
				startDay = Integer.parseInt (IRZClient.get (siteReferralAlertComboBox));
				iterations = 1;
				smsInterval = 0;
				description = "Alert to Client's Treatment Site on Referral";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Add rule for Referral Reminders to Clients
				boolean enable = tbReferralRemindersCheckBox.getValue ();
				// Week 1
				ruleId = "TB_REF_C1";
				ruleName = "Client Referral Reminders Week 1";
				type = SmsType.REMINDER;
				rule = "";
				int interval = Integer.parseInt (IRZClient.get (tbWeek1RemindersComboBox));
				isEnabled = (interval != 0) & enable;
				startDay = 1;
				smsInterval = (interval == 0) ? 1 : interval;
				iterations = 7 / smsInterval;
				description = "Reminders to Client's Treatment Site on Referral - Week 1";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Week 2
				ruleId = "TB_REF_C2";
				ruleName = "Client Referral Reminders Week 2";
				type = SmsType.REMINDER;
				rule = "";
				interval = Integer.parseInt (IRZClient.get (tbWeek2RemindersComboBox));
				isEnabled = (interval != 0) & enable;
				startDay = 8;
				smsInterval = (interval == 0) ? 1 : interval;
				iterations = 7 / smsInterval;
				description = "Reminders to Client's Treatment Site on Referral - Week 2";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Week 3
				ruleId = "TB_REF_C3";
				ruleName = "Client Referral Reminders Week 3";
				type = SmsType.REMINDER;
				rule = "";
				interval = Integer.parseInt (IRZClient.get (tbWeek3RemindersComboBox));
				isEnabled = (interval != 0) & enable;
				startDay = 15;
				smsInterval = (interval == 0) ? 1 : interval;
				iterations = 7 / smsInterval;
				description = "Reminders to Client's Treatment Site on Referral - Week 3";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Week 4
				ruleId = "TB_REF_C4";
				ruleName = "Client Referral Reminders Week 4";
				type = SmsType.REMINDER;
				rule = "";
				interval = Integer.parseInt (IRZClient.get (tbWeek4RemindersComboBox));
				isEnabled = (interval != 0) & enable;
				startDay = 22;
				smsInterval = (interval == 0) ? 1 : interval;
				iterations = 7 / smsInterval;
				description = "Reminders to Client's Treatment Site on Referral - Week 4";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Month 2
				ruleId = "TB_REF_C5";
				ruleName = "Client Referral Reminders Month 2";
				type = SmsType.REMINDER;
				rule = "";
				interval = Integer.parseInt (IRZClient.get (tbMonth2RemindersComboBox));
				isEnabled = (interval != 0) & enable;
				startDay = 30;
				smsInterval = (interval == 0) ? 1 : interval;
				iterations = 30 / smsInterval;
				description = "Reminders to Client's Treatment Site on Referral - Month 2";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Month 3
				ruleId = "TB_REF_C6";
				ruleName = "Client Referral Reminders Month 3";
				type = SmsType.REMINDER;
				rule = "";
				interval = Integer.parseInt (IRZClient.get (tbMonth3RemindersComboBox));
				isEnabled = (interval != 0) & enable;
				startDay = 60;
				smsInterval = (interval == 0) ? 1 : interval;
				iterations = 30 / smsInterval;
				description = "Reminders to Client's Treatment Site on Referral - Month 3";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));

				// Month 4+
				ruleId = "TB_REF_C7";
				ruleName = "Client Referral Reminders Month 4 and on";
				type = SmsType.REMINDER;
				rule = "";
				interval = Integer.parseInt (IRZClient.get (tbOnwardsRemindersComboBox));
				isEnabled = (interval != 0) & enable;
				startDay = 90;
				smsInterval = (interval == 0) ? 1 : interval;
				iterations = 270 / smsInterval;
				description = "Reminders to Client's Treatment Site on Referral - Month 4 and onwards";
				rules.add (new SmsRule (ruleId, ruleName, type.toString (), rule, isEnabled, "", "", startDay, iterations, smsInterval, endDay, description, serviceRunupMinutes));
				currentRules = rules.toArray (new SmsRule[] {});
				service.updateSmsRules (currentRules, new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (!result)
							Window.alert (CustomMessage.getInfoMessage (InfoType.INSERTED));
						else
							Window.alert (CustomMessage.getInfoMessage (InfoType.UPDATED));
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

	@Override
	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == saveButton)
		{
			saveData ();
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	@Override
	public void onValueChange (ValueChangeEvent<Boolean> event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender instanceof CheckBox)
		{
			CheckBox checkBox = (CheckBox) sender;
			boolean choice = checkBox.getValue ();
			if (sender == day2MCReminderToClientCheckBox)
			{

			}
			else if (sender == day7MCReminderToClientCheckBox)
			{

			}
			else if (sender == day42MCReminderToClientCheckBox)
			{

			}
			else if (sender == customRemindersCheckBox)
			{

			}
			else if (sender == tbSputumResultsAlertToClientCheckBox)
			{
				tbSputumResultsAlertToClientComboBox.setEnabled (choice);
			}
			else if (sender == siteReferralAlertCheckBox)
			{
				siteReferralAlertComboBox.setEnabled (choice);
			}
			else if (sender == tbSputumResultsAlertToSiteCheckBox)
			{
				tbSputumResultsAlertToSiteComboBox.setEnabled (choice);
			}
			else if (sender == tbReferralRemindersCheckBox)
			{
				tbWeek1RemindersComboBox.setEnabled (choice);
				tbWeek2RemindersComboBox.setEnabled (choice);
				tbWeek3RemindersComboBox.setEnabled (choice);
				tbWeek4RemindersComboBox.setEnabled (choice);
				tbMonth2RemindersComboBox.setEnabled (choice);
				tbMonth3RemindersComboBox.setEnabled (choice);
				tbOnwardsRemindersComboBox.setEnabled (choice);
			}
		}
	}
}
