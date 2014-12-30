/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.DateTimeUtil;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.model.Patient;
import org.irdresearch.irzimbabwe.shared.model.Person;
import org.irdresearch.irzimbabwe.shared.model.Visit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ClientProfilePopup extends PopupPanel implements IForm
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);

    private TabPanel profileTabPanel = new TabPanel();
    private FlowPanel flowPanel = new FlowPanel();
    private FlowPanel flowPanel2 = new FlowPanel();
    private FlowPanel flowPanel3 = new FlowPanel();

    private Patient currentClient = new Patient();
    private Person currentPerson = new Person();

    private int rowCountTestResults = 1;
    private String[][] testResultsArray = new String[rowCountTestResults][3];
    private String[] headerTestResultArray = new String[] { "DATE", "TESTED BY", "RESULT" };

    private final VerticalPanel verticalPanel = new VerticalPanel();
    private final TextArea patientDemogTextArea = new TextArea();
    private final Label lblNoTestResults = new Label("No test results have been conducted !");
    private final Label lblNoReferrals = new Label("Not referred yet !");

    /**
     * @wbp.parser.constructor
     */
    public ClientProfilePopup()
    {
	for (int i = 0; i < testResultsArray.length; i++)
	{
	    for (int j = 0; j < 3; j++)
	    {
		testResultsArray[i][j] = headerTestResultArray[j];
	    }
	}
	// setSize("520px", "510px");
	setSize("520px", "510px");
	setAnimationEnabled(true);
	profileTabPanel.setSize("520px", "510px");
	flowPanel.setSize("100%", "510px");
	flowPanel2.setSize("100%", "510px");
	flowPanel3.setSize("100%", "510px");

	// Tab 1 : Profile and Encounter list
	profileTabPanel.add(flowPanel, "Profile", false);
	verticalPanel.setSpacing(1);
	verticalPanel.setBorderWidth(1);

	flowPanel.add(verticalPanel);
	verticalPanel.setSize("100%", "100%");
	patientDemogTextArea.setDirectionEstimator(true);
	patientDemogTextArea.setReadOnly(true);

	verticalPanel.add(patientDemogTextArea);
	patientDemogTextArea.setSize("80%", "30%");

	// Tab 2 : Test Results
	profileTabPanel.add(flowPanel2, "Test Results", false);

	flowPanel2.add(lblNoTestResults);
	lblNoTestResults.setVisible(false);

	// Tab 3 : Referrals
	profileTabPanel.add(flowPanel3, "Referrals", false);

	flowPanel3.add(lblNoReferrals);

	profileTabPanel.selectTab(0);
	lblNoReferrals.setVisible(false);
	this.lblNoTestResults.setVisible(false);
	this.setAutoHideEnabled(true);
	this.setPopupPosition(525, 200);

    }

    public ClientProfilePopup(Person personObj)
    {
	setSize("520px", "510px");
	setAnimationEnabled(true);
	profileTabPanel.setSize("520px", "510px");
	profileTabPanel.setVisible(true);
	setCurrent(personObj);

	flowPanel.setSize("550px", "510px");
	flowPanel2.setSize("550px", "510px");
	flowPanel3.setSize("550px", "510px");

	// Tab 1 : Profile and Encounter list
	profileTabPanel.add(flowPanel, "Profile", false);
	verticalPanel.setSpacing(1);
	verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	verticalPanel.setBorderWidth(1);

	flowPanel.add(verticalPanel);
	verticalPanel.setSize("550px", "510px");
	patientDemogTextArea.setDirectionEstimator(true);

	verticalPanel.add(patientDemogTextArea);
	patientDemogTextArea.setSize("545px", "200px");
	patientDemogTextArea.setReadOnly(true);

	// Tab 2 : Test Results
	profileTabPanel.add(flowPanel2, "Test Results", false);
	flowPanel2.add(lblNoTestResults);
	lblNoTestResults.setVisible(false);

	// Tab 3 : Referrals
	profileTabPanel.add(flowPanel3, "Referrals", false);
	flowPanel3.add(lblNoReferrals);

	// When a user selects patient from the list of search panel pops up and
	// shows the current patient
	profileTabPanel.selectTab(0);
	lblNoReferrals.setVisible(false);
	this.lblNoTestResults.setVisible(false);
	this.setAutoHideEnabled(true);
	// this.setPopupPosition(525, 200);
	this.setPopupPosition(550, 200);
	this.add(profileTabPanel.asWidget());
	this.fillData();
	this.show();
    }

    @Override
    public void clearUp()
    {
	// TODO Auto-generated method stub
	this.currentClient = null;
	this.currentPerson = null;
	// gxp_resultsGrid.clearCell(i+1, j);
	// referralsGrid.clearCell(i+1, j);

    }

    @Override
    public boolean validate()
    {
	// Needs no validations as read only client profile
	return false;
    }

    @Override
    public void saveData()
    {// Client profile is read only
    }

    @Override
    public void updateData()
    { // Client profile is read only

    }

    @Override
    public void deleteData()
    {
	// Client profile is read only
    }

    @Override
    public void fillData()
    {

	/*
	 * TAB 1 Patient demographics and his encounter along with encounter
	 * elements
	 */
	final StringBuilder patientInfo = new StringBuilder();
	patientInfo.append("CLIENT ID: " + currentClient.getPatientId());
	patientInfo.append("\n"+"NAME: " + currentPerson.getFirstName() + " " + currentPerson.getLastName());
	patientInfo.append("\n"+"AGE: " + currentPerson.getApproximateAge());
	if (currentPerson.getMobile() == null || currentPerson.getMobile() == null)
	    patientInfo.append("\n"+"MOBILE: " + "Not provided");
	else
	    patientInfo.append("\n"+"MOBILE: " + currentPerson.getMobile());
	try
	{
	    service.findAllVisits(currentClient.getPatientId(), new AsyncCallback<Visit[]>() {
		public void onSuccess(Visit[] result)
		{
		    if(result.length!=0)
		    {
			String visitPurpose="";
			for(int i=0;i<result.length;i++)
			{
			    if(!visitPurpose.equals(""))
				visitPurpose=visitPurpose.concat(", ").concat(IRZ.getDefinitionValue("VISIT_PURPOSE", result[i].getVisitPurpose()));
			    else
				visitPurpose=IRZ.getDefinitionValue("VISIT_PURPOSE", result[i].getVisitPurpose());
			}    
			patientInfo.append("\n"+"VISIT PURPOSE: " + visitPurpose);    
			patientInfo.append("\n"+"VISIT DATE: " + DateTimeUtil.getFormattedDate(result[0].getVisitDate(), DateTimeUtil.SQL_DATE) + "\n");
		    }
		    
		    patientDemogTextArea.setText(patientInfo.toString());
		}

		public void onFailure(Throwable caught)
		{
		    patientDemogTextArea.setText(patientInfo.toString());
		}
	    });
	    String getEncounters = "SELECT e.date_entered,et.description,l.location_name "
	    + "FROM irzimbabwe.encounter_type as et, irzimbabwe.encounter as e ,location as l where e.encounter_type=et.encounter_type " + "and e.pid1='" + currentClient.getPatientId()
	    + "' and e.location_id=l.location_id " + "group by e.encounter_type order by e.date_end desc;";
	    service.getTableData(getEncounters, new AsyncCallback<String[][]>() {

		@Override
		public void onFailure(Throwable caught)
		{
		    System.out.println("Fill Data problem: Get Encounters getTableData failed");
		    caught.printStackTrace();
		}

		@Override
		public void onSuccess(String[][] result)
		{
		    if (result.length != 0)
		    {
			Grid encounterGrid = new Grid(result.length + 1, 3);
			encounterGrid.setBorderWidth(1);
			encounterGrid.setSize("100%", "50%");
			encounterGrid.setCellSpacing(2);
			encounterGrid.setBorderWidth(2);
			encounterGrid.setTitle("PATIENT ENCOUNTERS");
			encounterGrid.setText(0, 0, "DATE");
			encounterGrid.setText(0, 1, "FORMS");
			encounterGrid.setText(0, 2, "LOCATION");
			encounterGrid.getRowFormatter().addStyleName(0, "clientProfileGrids");
			int i, j;
			for (i = 0; i < result.length; i++)
			{
			    for (j = 0; j < 3; j++)
			    {
				if (result[i][j] == null || result[i][j].equals(""))
				    encounterGrid.setText(i + 1, j, "not available");
				encounterGrid.setText(i + 1, j, result[i][j]);
				verticalPanel.add(encounterGrid);
			    }
			}
		    }
		    else
			Window.alert(CustomMessage.getErrorMessage(ErrorType.ITEM_NOT_FOUND) + ": No encounters found for this client !");

		}

	    });
	    /*
	     * TAB 2: TEST RESULTS TAB GET SMEAR RESULTS AND GENXPERT RESULTS
	     * FOR THE RESPECTIVE CLIENT
	     */

	    String getSmearResults = "SELECT date_smear_tested,smear_tested_by,smear_result from sputum_test where patient_id='" + currentClient.getPatientId()
	    + "' and date_smear_tested is not null and smear_result is not null;";
	    service.getTableData(getSmearResults, new AsyncCallback<String[][]>() {

		@Override
		public void onFailure(Throwable caught)
		{
		    System.out.println("Fill Data problem: Get SmearResults getTableData failed");
		    caught.printStackTrace();
		}

		@Override
		public void onSuccess(String[][] result)
		{
		    if (result.length != 0)
		    {
			Grid test_resultsGrid = new Grid(result.length + 1, 3);
			test_resultsGrid.setCellPadding(1);
			test_resultsGrid.setBorderWidth(1);
			// test_resultsGrid.addStyleName("clientProfileGrids");
			test_resultsGrid.setSize("100%", "50%");
			test_resultsGrid.setTitle("SMEAR RESULTS");
			test_resultsGrid.setText(0, 0, "DATE");
			test_resultsGrid.setText(0, 1, "TESTED BY");
			test_resultsGrid.setText(0, 2, "RESULTS");
			test_resultsGrid.getRowFormatter().addStyleName(0, "clientProfileGrids");
			int i, j;
			for (i = 0; i < result.length; i++)
			{
			    for (j = 0; j < 3; j++)
			    {
				test_resultsGrid.getColumnFormatter().setWidth(j, "25%");
				if (result[i][j] == null || result[i][j].equals(""))
				{
				    test_resultsGrid.setText(i + 1, j, "NOT AVAILABLE");
				    flowPanel2.add(test_resultsGrid);
				}

				else if (j == 0)
				{
				    test_resultsGrid.setText(i + 1, j, result[i][j].substring(0, 10));
				    flowPanel2.add(test_resultsGrid);
				}
				else if (j == 2 && (result[i][j] == null || result[i][j].equals("")))
				{
				    test_resultsGrid.setText(i + 1, j, "PENDING");
				    flowPanel2.add(test_resultsGrid);
				}
				else
				{
				    test_resultsGrid.setText(i + 1, j, result[i][j]);
				    flowPanel2.add(test_resultsGrid);
				}

			    }
			}

		    }
		    else
		    {
			lblNoTestResults.setText("No Smear Test Results have been entered !\n");
			lblNoTestResults.setVisible(true);
		    }

		}

	    });

	} catch(Exception e)
	{
	    e.printStackTrace();
	    load(false);
	}
	final String notApplicable = "Not Applicable on result";
	String zero = "0";
	String getGenXprtResults = "select date_gxp_tested,gxp_tested_by," + "case coalesce(rif_resistance,'" + zero + "')" + "when '0' then concat(gxp_result," + "';','" + notApplicable + "') "
	+ "else  concat(gxp_result,'" + ";" + "',rif_resistance) end" + " as results" + " from sputum_test where smear_result='NEGATIVE' and patient_id='" + currentClient.getPatientId()
	+ "'and gxp_result is not null;";
	try
	{
	    service.getTableData(getGenXprtResults, new AsyncCallback<String[][]>() {

		@Override
		public void onFailure(Throwable caught)
		{
		    System.out.println("Caught some thing in getting GeneXpert results");
		    caught.printStackTrace();
		}

		@Override
		public void onSuccess(String[][] result)
		{

		    if (result.length != 0)
		    {
			Grid gxp_resultsGrid = new Grid(result.length + 1, 3);
			gxp_resultsGrid.setCellPadding(1);
			gxp_resultsGrid.setBorderWidth(1);
			gxp_resultsGrid.setSize("100%", "50%");
			gxp_resultsGrid.setTitle("GENE XPERT RESULTS");
			gxp_resultsGrid.setText(0, 0, "DATE");
			gxp_resultsGrid.setText(0, 1, "TESTED BY");
			gxp_resultsGrid.setText(0, 2, "RESULTS");
			gxp_resultsGrid.getRowFormatter().addStyleName(0, "clientProfileGrids");

			for (int i = 0; i < result.length; i++)
			{
			    for (int j = 0; j < 3; j++)
			    {
				gxp_resultsGrid.getColumnFormatter().setWidth(j, "25%");
				if ((result[i][j] == null || result[i][j].equals("")) && j != 2)
				    gxp_resultsGrid.setText(i + 1, j, "NOT AVAILABLE");
				else if (j == 2 && result[i][j].contains(";"))
				{
				    String[] resultParts = result[i][j].split(";");
				    String gxpResult = IRZ.getDefinitionValue("GXP_RESULT", resultParts[0]);
				    String rifResist = "";
				    if (!resultParts[1].equals(notApplicable))
					rifResist = IRZ.getDefinitionValue("RIF_RESISTANCE", resultParts[1]);
				    else
					rifResist = notApplicable;
				    String displayCellContent = "Gene Xpert Result:" + gxpResult + "\n" + " \nRIF Resistance:" + rifResist;
				    gxp_resultsGrid.setText(i + 1, j, displayCellContent);// i+1

				    flowPanel2.add(gxp_resultsGrid);
				}
				else if (j == 2 && (result[i][j] == null || result[i][j].equals("")))
				{
				    gxp_resultsGrid.setText(i + 1, j, "PENDING");
				    flowPanel2.add(gxp_resultsGrid);
				}
				else if (j == 0 && (!result[i][j].equals("") || result[i][j] != null))
				{
				    gxp_resultsGrid.setText(i + 1, j, result[i][j].substring(0, 10));
				    flowPanel2.add(gxp_resultsGrid);

				}
				else
				{
				    gxp_resultsGrid.setText(i + 1, j, result[i][j]);
				    flowPanel2.add(gxp_resultsGrid);
				}

			    }
			}
		    }
		    else
		    {

			lblNoTestResults.setText(lblNoTestResults.getText() + "\n No Gene Xpert Test Results have been entered !\n");
			lblNoTestResults.setVisible(true);
		    }

		}

	    });
	    String referralQuery = "select e.date_entered,(select location_name from location as l where e.location_id=l.location_id),"
	    + " MAX(IF(er.element = 'REFERRED_TO', (SELECT location_name from location where location_id=er.value),null)) AS REFERRED_TO,"
	    + "  MAX(IF(er.element = 'REASON', er.value, null)) AS REASON " + "from encounter_results er inner join encounter e on  e.e_id=er.e_id and e.pid1=er.pid1 and"
	    + " e.encounter_type=er.encounter_type and e.pid2=e.pid2 where e.pid1='" + currentClient.getPatientId() + "' and er.encounter_type='REFERRAL' group by e.e_id ORDER BY e.date_end desc;";
	    service.getTableData(referralQuery, new AsyncCallback<String[][]>() {
/*
 * Referral table overwrites data and updates the latest referrals
 */
		@Override
		public void onFailure(Throwable caught)
		{
		    System.out.println("Caught some thing in getting Referrals");
		    caught.printStackTrace();
		}

		@Override
		public void onSuccess(String[][] result)
		{
		    // TODO Auto-generated method stub

		    if (result.length != 0)
		    {
			Grid referralsGrid = new Grid(result.length + 1, 4);
			referralsGrid.setCellPadding(1);
			referralsGrid.setBorderWidth(1);
			referralsGrid.setSize("100%", "50%");
			referralsGrid.setTitle("REFERRALS");
			referralsGrid.setText(0, 0, "DATE");
			referralsGrid.setText(0, 1, "REFERRED FROM");
			referralsGrid.setText(0, 2, "REFERRED TO");
			referralsGrid.setText(0, 3, "REASON");
			referralsGrid.getRowFormatter().addStyleName(0, "clientProfileGrids");

			for (int i = 0; i < result.length; i++)
			{
			    for (int j = 0; j < 4; j++)
			    {
				referralsGrid.getColumnFormatter().setWidth(j, "25%");
				if ((result[i][j] == null || result[i][j].equals("")) && (j == 2 || j==3))
				{
				    referralsGrid.setText(i + 1, j, "NOT AVAILABLE");
				    flowPanel3.add(referralsGrid);
				    // referralsGrid.setVisible(false);
				}
				else if (result[i][j] == null || result[i][j].equals(""))
				{
				    referralsGrid.setText(i + 1, j, "NOT AVAILABLE");
				    flowPanel3.add(referralsGrid);
				}
				else
				{
				    referralsGrid.setText(i + 1, j, result[i][j]);
				    flowPanel3.add(referralsGrid);
				    referralsGrid.setVisible(true);
				}
			    }
			}
		    }
		    else
		    {
			lblNoReferrals.setText("CLIENT HAS NOT BEEN REFERRED");
			lblNoReferrals.setVisible(true);
		    }

		}

	    });
	} catch(Exception e)
	{
	    e.printStackTrace();
	}

    }

    @Override
    public void setRights(String menuName)
    {
	// not needed
    }

    @Override
    public void onClick(ClickEvent event)
    {// client uses for read only purpose

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

    @Override
    public void setCurrent()
    {
	// is already implemented
    }

    public void setCurrent(Person personObj)
    {
	/*
	 * get the current person whose profile the user is trying to view
	 */
	this.currentClient.setPatientId(personObj.getPid());
	this.currentPerson = personObj;

    }

}
