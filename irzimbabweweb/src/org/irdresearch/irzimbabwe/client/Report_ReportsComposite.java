package org.irdresearch.irzimbabwe.client;

import java.util.Date;

import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.Parameter;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class Report_ReportsComposite extends Composite implements IReport, ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static final String menuName = "DATALOG";
    private String reportsDB = "";
    private String filter = "";
    private String startDate = "";
    private String endDate = "";
    private String patientId = "";
    private String facility = "";
    private UserRightsUtil rights = new UserRightsUtil();

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private FlexTable filterFlexTable = new FlexTable();

    private Grid grid = new Grid(1, 4);

    private HorizontalPanel dateRangeHorizontalPanel = new HorizontalPanel();
    private HorizontalPanel patientIdHorizontalPanel = new HorizontalPanel();

    private Button viewButton = new Button("Save");
    private Button closeButton = new Button("Close");
    private Button exportButton = new Button("Export");

    private Label lblSelectCategory = new Label("Select Category:");
    private Label lblSnapshot = new Label("Snapshot:");
    private Label snapshotLabel = new Label();
    private Label lblCaution = new Label("Some reports may take 5 to 10 minutes to generate. Please wait until report download window appears.");
    private Label lblTbReachLog = new Label(IRZ.getProjectTitle() + " Report");
    private Label lblSelectReport = new Label("Select Report:");
    private Label lblFilter = new Label("Filter (Check all that apply):");
    private TextBox patientIdTextBox = new TextBox();

    private ListBox categoryComboBox = new ListBox();
    private ListBox reportsListComboBox = new ListBox();
    private ListBox patientFilterTypeComboBox = new ListBox();
    private ListBox facilityComboBox = new ListBox();
    private ListBox referralComboBox = new ListBox();

    private DateBox fromDateBox = new DateBox();
    private DateBox toDateBox = new DateBox();

    private CheckBox dateRangeFilterCheckBox = new CheckBox("Date Range:");
    private CheckBox patientIdCheckBox = new CheckBox("Client ID:");
    private CheckBox facilityCheckBox = new CheckBox("Facility:");

    public Report_ReportsComposite()
    {
	initWidget(flexTable);
	flexTable.setSize("600px", "100%");
	flexTable.setWidget(0, 0, topFlexTable);
	topFlexTable.setSize("100%", "100%");
	lblTbReachLog.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblTbReachLog);
	topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
	topFlexTable.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
	topFlexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
	flexTable.setWidget(1, 0, rightFlexTable);
	rightFlexTable.setSize("100%", "100%");
	rightFlexTable.setWidget(0, 0, lblSnapshot);
	rightFlexTable.setWidget(0, 1, snapshotLabel);
	rightFlexTable.setWidget(1, 0, lblSelectCategory);
	categoryComboBox.addItem("-- Select Category --");
	categoryComboBox.addItem("Reports");
	categoryComboBox.addItem("Form Dumps");

	referralComboBox.addItem("TB INVESTIGATION");
	referralComboBox.addItem("TB TREATMENT");
	rightFlexTable.setWidget(1, 1, categoryComboBox);
	categoryComboBox.setWidth("100%");
	rightFlexTable.setWidget(2, 0, lblSelectReport);
	rightFlexTable.setWidget(2, 1, reportsListComboBox);
	reportsListComboBox.setWidth("100%");
	reportsListComboBox.addChangeHandler(this);
	lblCaution.setStyleName("gwt-MenuItem-selected");
	rightFlexTable.setWidget(3, 1, lblCaution);
	lblCaution.setWidth("100%");
	rightFlexTable.setWidget(4, 0, lblFilter);
	rightFlexTable.setWidget(4, 1, filterFlexTable);
	filterFlexTable.setWidth("100%");
	filterFlexTable.setWidget(0, 0, dateRangeFilterCheckBox);
	dateRangeFilterCheckBox.setVisible(true);
	dateRangeFilterCheckBox.setWidth("");
	filterFlexTable.setWidget(0, 1, dateRangeHorizontalPanel);
	dateRangeHorizontalPanel.setSize("200px", "");
	dateRangeHorizontalPanel.add(fromDateBox);
	fromDateBox.setEnabled(false);
	fromDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
	fromDateBox.setWidth("100px");
	dateRangeHorizontalPanel.add(toDateBox);
	toDateBox.setEnabled(false);
	toDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
	toDateBox.setWidth("100px");
	filterFlexTable.setWidget(1, 0, facilityCheckBox);
	facilityComboBox.setEnabled(false);
	filterFlexTable.setWidget(1, 1, facilityComboBox);
	facilityComboBox.setWidth("100%");
	filterFlexTable.setWidget(2, 0, patientIdCheckBox);
	filterFlexTable.setWidget(2, 1, patientIdHorizontalPanel);

	patientIdHorizontalPanel.setWidth("100%");
	patientIdHorizontalPanel.add(patientFilterTypeComboBox);
	patientIdHorizontalPanel.setCellVerticalAlignment(patientFilterTypeComboBox, HasVerticalAlignment.ALIGN_MIDDLE);
	patientFilterTypeComboBox.setEnabled(false);
	patientFilterTypeComboBox.setWidth("100%");
	patientIdHorizontalPanel.add(patientIdTextBox);
	patientIdTextBox.setVisibleLength(12);
	patientIdTextBox.setMaxLength(12);
	patientIdTextBox.setEnabled(false);
	patientIdTextBox.setWidth("100%");
	rightFlexTable.setWidget(5, 1, grid);
	grid.setSize("100%", "100%");
	viewButton.setEnabled(false);
	viewButton.setText("View");
	grid.setWidget(0, 0, viewButton);
	exportButton.setEnabled(false);
	grid.setWidget(0, 1, exportButton);
	grid.setWidget(0, 3, closeButton);
	rightFlexTable.getCellFormatter().setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
	rightFlexTable.getRowFormatter().setVerticalAlign(4, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

	dateRangeFilterCheckBox.addValueChangeHandler(this);
	facilityCheckBox.addValueChangeHandler(this);
	patientIdCheckBox.addValueChangeHandler(this);

	categoryComboBox.addChangeHandler(this);
	viewButton.addClickHandler(this);
	exportButton.addClickHandler(this);
	closeButton.addClickHandler(this);

	refreshList();
	setRights(menuName);
	reportsDB = IRZ.getReportingDatabase();
    }

    private void refreshList()
    {
	String[] filterOptions = { "IS EXACTLY", "STARTS WITH", "ENDS ON", "LOOKS LIKE", "NOT LIKE" };
	for (String s : filterOptions)
	{
	    patientFilterTypeComboBox.addItem(s);
	}
	try
	{
	    service.getSnapshotTime(new AsyncCallback<String>() {
		public void onSuccess(String result)
		{
		    snapshotLabel.setText(result);
		}

		public void onFailure(Throwable caught)
		{
		    caught.printStackTrace();
		}
	    });
	    service.getTableData("select location_id, location_name from location order by location_name", new AsyncCallback<String[][]>() {
		public void onSuccess(String[][] result)
		{
		    facilityComboBox.clear();
		    for (int i = 0; i < result.length; i++)
			facilityComboBox.addItem(result[i][1].toString(), result[i][0].toString());
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
     * Creates appropriate filter for given column names
     * 
     * @param patientColumnName
     * @param dateColumnName
     * @param facilityColumnName
     * @param fcColumnName
     * @param cmColumnName
     */
    @SuppressWarnings("deprecation")
    private String filterData(String dateColumnName, String districtColumnName, String facilityColumnName, String fcColumnName, String cmColumnName, String patientColumnName)
    {
	filter = "";
	startDate = "";
	endDate = "";
	facility = "";
	patientId = "";

	if (dateRangeFilterCheckBox.getValue())
	{
	    Date start = new Date(fromDateBox.getValue().getTime());
	    Date end = new Date(toDateBox.getValue().getTime());
	    StringBuilder startString = new StringBuilder();
	    StringBuilder endString = new StringBuilder();
	    startString.append((start.getYear() + 1900) + "-" + (start.getMonth() + 1) + "-" + start.getDate() + " " + start.getHours() + ":" + start.getMinutes() + ":00");
	    endString.append((end.getYear() + 1900) + "-" + (end.getMonth() + 1) + "-" + end.getDate() + " " + end.getHours() + ":" + end.getMinutes() + ":00");
	    startDate = startString.toString();
	    endDate = endString.toString();
	}
	if (facilityCheckBox.getValue())
	{
	    facility = " = '" + IRZClient.get(facilityComboBox) + "'";
	}
	if (patientIdCheckBox.getValue())
	{
	    switch (patientFilterTypeComboBox.getSelectedIndex())
	    {
	    case 0:
		patientId = " = '" + IRZClient.get(patientIdTextBox) + "'";
		break;
	    case 1:
		patientId = " LIKE '" + IRZClient.get(patientIdTextBox) + "%'";
		break;
	    case 2:
		patientId = " LIKE '%" + IRZClient.get(patientIdTextBox) + "'";
		break;
	    case 3:
		patientId = " LIKE '%" + IRZClient.get(patientIdTextBox) + "%'";
		break;
	    case 4:
		patientId = " NOT LIKE '%" + IRZClient.get(patientIdTextBox) + "%'";
		break;
	    }
	}
	/*
	 * if(yearCheckBox.getValue())
	 * filter += " AND"
	 * +" EXTRACT(YEAR FROM "+dateToExtract+")='"+IRZClient.
	 * get(yearComboBox)+"'";
	 * if(monthCheckBox.getValue())
	 * filter += " AND"
	 * +" EXTRACT(MONTH FROM "+dateToExtract+")='"+IRZClient
	 * .get(monthComboBox)+"'";
	 */
	if (dateRangeFilterCheckBox.getValue() && !dateColumnName.equals(""))
	    filter += " AND " + dateColumnName + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
	if (facilityCheckBox.getValue() && !facilityColumnName.equals(""))
	    filter += " AND " + facilityColumnName + facility;
	if (patientIdCheckBox.getValue() && !patientColumnName.equals(""))
	    filter += " AND " + patientColumnName + patientId;
	return filter;
    }

    public void clearUp()
    {
	// Not implemented
    }

    public boolean validate()
    {
	return true;
    }

    public void viewData(final boolean export)
    {
	String reportSelected = IRZClient.get(reportsListComboBox).replace(" ", "");
	String query = "";
	// Change the below to getDatabaseName () if live reports required
	reportsDB = IRZ.getReportingDatabase();
	// Case Detection Reports
	if (IRZClient.get(categoryComboBox).equals("Reports"))
	{
	    if (reportSelected.equals("Some Report"))
	    {
		query = "" + filterData("", "", "", "", "", "");
	    }
	    else if (reportSelected.equals("ClientTimeTakenInReferralReport"))
	    {

		query = "select s.patient_id as ClientId,max(s.date_smear_tested) AS LastSmearTested,r.date_referred as DateOfReferral,"
		+ "DATEDIFF(r.date_referred, (case coalesce(s.date_gxp_tested,'0') when '0' then max(s.date_smear_tested) else s.date_gxp_tested end)) as TimeTakenInReferral,"
		+ "r.reason as Reason from sputum_test as s inner join referral as r on s.patient_id = r.patient_id" + " where 1=1" + filterData("r.date_referred", "", "", "", "", "s.patient_id")
		+ " GROUP BY s.patient_id;";

	    }
	    else if (reportSelected.equals("TimeTakenInTBTreatmentReferral"))
	    {
		query = "SELECT s.patient_id AS ClientId,max(s.date_smear_tested) AS LastSmearTested,r.date_referred AS DateOfReferral,"
		+ "DATEDIFF(r.date_referred,(case coalesce(s.date_gxp_tested,'0') when '0' then max(s.date_smear_tested) else s.date_gxp_tested end)) AS TimeTakenInReferral"
		+ " FROM sputum_test s INNER JOIN referral r ON s.patient_id = r.patient_id where r.reason='TB TREATMENT' and 1=1 " + filterData("r.date_referred", "", "", "", "", "s.patient_id")
		+ " GROUP BY  s.patient_id;";

	    }
	    else if (reportSelected.equals("TimeTakenInTBInvestigationReferral"))
	    {
		query = "SELECT s.patient_id AS ClientId," + "max(s.date_smear_tested) AS LastSmearTested," + "r.date_referred AS DateOfReferral,"
		+ "DATEDIFF(r.date_referred,(case coalesce(s.date_gxp_tested,'0') when '0' then max(s.date_smear_tested) else s.date_gxp_tested end)) AS TimeTakenInReferral "
		+ "FROM sputum_test s INNER JOIN referral r ON s.patient_id = r.patient_id" + " where r.reason='TB INVESTIGATION' and 1=1 "
		+ filterData("r.date_referred", "", "", "", "", "s.patient_id") + " GROUP BY  s.patient_id;";
	    }
	    else if (reportSelected.equals("HIVStatusOfAllPatients"))
	    {
		disableFilters();
		query = "select count(*),hiv_status from patient where hiv_status is not null or hiv_status <> '' and 1=1 " + filterData("patient.date_screened", "", "", "", "", "")
		+ " group by hiv_status;";
	    }
	    else if (reportSelected.equals("TimeTakenInReferralOfAllTBSuspects"))
	    {
		disableFilters();
		query = "SELECT (case when (subSet.days_taken <= 5) then 'Within 5 days' else (case when (subSet.days_taken > 5) then 'More than 5 days' "
		+ "else (case when (subSet.days_taken is null) then 'Not yet referred' else 'N/A' end) end)end) as referralTypes, "
		+ "(case when (subSet.days_taken <= 5) then SUM(subSet.days_taken <= 5) else (case when (subSet.days_taken > 5) then SUM(subSet.days_taken > 5) else (case when (subSet.days_taken is null) then SUM(subSet.date_referred IS NULL) "
		+ "else 0 end)end)end) as countOfClients FROM" + " (select p.patient_id, "
		+ " (CASE WHEN st.smear_result <> 'NEGATIVE' OR st.gxp_result = 'MTB+' THEN (DATEDIFF(r.date_referred, MIN(st.date_smear_tested))) "
		+ " ELSE (CASE  WHEN st.smear_result = 'NEGATIVE' OR st.gxp_result = 'MTB-' THEN  (DATEDIFF(r.date_referred, MAX(st.date_smear_tested))) "
		+ "END) END) as days_taken,   r.date_referred as date_referred  from patient as p  left outer join sputum_test as st on p.patient_id = st.patient_id "
		+ " left outer join referral as r on r.patient_id = st.patient_id " + "  where p.suspected_by is not null  and (p.patient_status = 'SUSPECT' or" + " p.patient_status = 'CONFIRMED')"
		+ " group by p.patient_id)" + "  as subSet" + " group by referralTypes;";
	    }
	    else
		query = "";
	}
	else if (IRZClient.get(categoryComboBox).equals("Form Dumps"))
	{
	    query = "select * from Enc_" + IRZClient.get(reportsListComboBox) + " where 1 = 1" + filterData("date_entered", "", "location_id", "", "", "pid1");
	}

	load(true);
	if (query.equals(""))
	{
	    try
	    {
		service.generateReport(reportSelected, new Parameter[] {}, export, new AsyncCallback<String>() {
		    public void onSuccess(String result)
		    {
			Window.open(result, "_blank", "");
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
		load(false);
		e.printStackTrace();
	    }
	}
	else
	{
	    if (IRZClient.get(categoryComboBox).equals("Form Dumps"))
	    {
		try
		{
		    service.generateCSVfromQuery(reportsDB, query, new AsyncCallback<String>() {

			public void onSuccess(String result)
			{
			    Window.open(result, "_blank", "");
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
		    load(false);
		    e.printStackTrace();
		}
	    }
	    else
	    {
		try
		{
		    service.generateReportFromQuery(reportsDB, reportSelected, query, export, new AsyncCallback<String>() {

			public void onSuccess(String result)
			{
			    Window.open(result, "_blank", "");
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
		    load(false);
		    e.printStackTrace();
		}
	    }
	}
    }

    private void disableFilters()
    {
	dateRangeFilterCheckBox.setEnabled(false);
	fromDateBox.setEnabled(false);
	toDateBox.setEnabled(false);
	patientIdCheckBox.setEnabled(false);
	patientFilterTypeComboBox.setEnabled(false);
	patientIdTextBox.setEnabled(false);
	facilityCheckBox.setEnabled(false);
	facilityComboBox.setEnabled(false);

    }

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
				viewButton.setEnabled(rights.getAccess(AccessType.PRINT));
				exportButton.setEnabled(rights.getAccess(AccessType.PRINT));
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
	}
    }

    public void onClick(ClickEvent event)
    {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == viewButton)
	{
	    viewData(false);
	}
	else if (sender == exportButton)
	{
	    viewData(true);
	}
	else if (sender == closeButton)
	{
	    MainMenuComposite.clear();
	}
    }

    public void onChange(ChangeEvent event)
    {
	Widget sender = (Widget) event.getSource();
	// Fill report names
	if (sender == categoryComboBox)
	{
	    String text = IRZClient.get(sender);
	    if (text.equals("Reports"))
	    {
		reportsListComboBox.clear();
		reportsListComboBox.addItem("Client Treatment Progress Report");
		reportsListComboBox.addItem("Client Time Taken In Referral Report");
		reportsListComboBox.addItem("Time Taken In TB Investigation Referral");
		reportsListComboBox.addItem("Time Taken In TB Treatment Referral");
		reportsListComboBox.addItem("Time Taken In Referral Of All TB Suspects");
		reportsListComboBox.addItem("HIV Status Of All Patients");
	    }
	    else if (text.equals("Form Dumps"))
	    {
		load(true);
		reportsListComboBox.clear();
		try
		{
		    service.getColumnData("encounter", "encounter_type", "", new AsyncCallback<String[]>() {
			public void onSuccess(String[] result)
			{
			    for (String s : result)
				reportsListComboBox.addItem(s);
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

	}
	if (sender == reportsListComboBox)
	{
	    viewButton.setEnabled(!IRZClient.get(reportsListComboBox).equals("Form Dumps"));
	}
    }

    public void onValueChange(ValueChangeEvent<Boolean> event)
    {
	Widget sender = (Widget) event.getSource();
	String reportSelected = IRZClient.get(reportsListComboBox).replace(" ", "");
	if (sender == dateRangeFilterCheckBox)
	{
	    if (!reportSelected.equals("HIVStatusOfAllPatients") && !reportSelected.equals("TimeTakenInReferralOfAllTBSuspects"))
	    {
		fromDateBox.setEnabled(dateRangeFilterCheckBox.getValue());
		toDateBox.setEnabled(dateRangeFilterCheckBox.getValue());

	    }

	}
	else if (sender == patientIdCheckBox)
	{

	    if (!reportSelected.equals("HIVStatusOfAllPatients") && !reportSelected.equals("TimeTakenInReferralOfAllTBSuspects")
	    && !reportSelected.equals("ClientTimeTakenInReferralReport")
	    && !reportSelected.equals("TimeTakenInTBInvestigationReferral")
	    && !reportSelected.equals("TimeTakenInTBTreatmentReferral"))
	    {

		patientFilterTypeComboBox.setEnabled(patientIdCheckBox.getValue());
		patientIdTextBox.setEnabled(patientIdCheckBox.getValue());
	    }
	}
	else if (sender == facilityCheckBox)
	{

	    if (!reportSelected.equals("HIVStatusOfAllPatients") && !reportSelected.equals("TimeTakenInReferralOfAllTBSuspects")
	    && !reportSelected.equals("ClientTimeTakenInReferralReport")
	    && !reportSelected.equals("TimeTakenInTBInvestigationReferral")
	    && !reportSelected.equals("TimeTakenInTBTreatmentReferral"))
	    {
		facilityComboBox.setEnabled(facilityCheckBox.getValue());
	    }

	}
	/*
	 * else if(sender == yearCheckBox)
	 * {
	 * yearComboBox.setEnabled(yearCheckBox.getValue());
	 * }
	 * else if(sender == monthCheckBox)
	 * {
	 * monthComboBox.setEnabled(monthCheckBox.getValue());
	 * }
	 */
    }
}
