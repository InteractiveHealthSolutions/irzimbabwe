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
import org.irdresearch.irzimbabwe.shared.model.EncounterElement;
import org.irdresearch.irzimbabwe.shared.model.EncounterId;
import org.irdresearch.irzimbabwe.shared.model.EncounterResults;
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
import org.irdresearch.irzimbabwe.shared.model.Person;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * Client's information editting form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class PatientComposite extends Composite implements ClickHandler, ChangeHandler
{
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static final String formName = "CLIENT_EDT";

    private UserRightsUtil rights = new UserRightsUtil();
    private String clientId = "";
    private boolean valid;

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable clientIdFlexTable = new FlexTable();

    private Grid grid = new Grid(1, 2);
    private VerticalPanel middleVerticalPanel = new VerticalPanel();
    private HorizontalPanel clientIdHorizontalPanel = new HorizontalPanel();

    private SimplePanel valueSimplePanel = new SimplePanel();

    private Button checkIdButton = new Button("Check");
    private Button saveButton = new Button("Save");
    private Button closeButton = new Button("Close");

    private Label lblClientsInitialDemographics = new Label("Client's Information Form");
    private Label lblClientsId = new Label("Client's ID:");
    private Label lblEditAttribute = new Label("Edit Attribute:");
    private Label lblNewValue = new Label("New Value:");
    private Label lblNICformat = new Label("Format: 19-1234567-A-19 or 12-123456-Z-12");

    private TextBox clientIdTextBox = new TextBox();
    private TextArea clientInfoTextArea = new TextArea();

    private ListBox attributeComboBox = new ListBox();

    public PatientComposite()
    {
	initWidget(flexTable);
	flexTable.setSize("373px", "447px");
	flexTable.setWidget(0, 0, topFlexTable);
	lblClientsInitialDemographics.setWordWrap(false);
	lblClientsInitialDemographics.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblClientsInitialDemographics);
	topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.setWidget(1, 0, middleVerticalPanel);
	clientIdTextBox.setVisibleLength(12);
	clientIdTextBox.setMaxLength(12);
	middleVerticalPanel.add(clientIdHorizontalPanel);
	clientIdHorizontalPanel.add(lblClientsId);
	clientIdHorizontalPanel.setCellVerticalAlignment(lblClientsId, HasVerticalAlignment.ALIGN_MIDDLE);
	lblClientsId.setWordWrap(false);
	clientIdHorizontalPanel.add(clientIdTextBox);
	clientIdHorizontalPanel.add(checkIdButton);
	checkIdButton.setWidth("100%");
	clientInfoTextArea.setVisibleLines(8);
	lblNICformat.setVisible(false);

	middleVerticalPanel.add(clientInfoTextArea);
	clientInfoTextArea.setWidth("267px");
	middleVerticalPanel.add(clientIdFlexTable);
	clientIdFlexTable.setWidth("100%");
	lblEditAttribute.setWordWrap(false);
	clientIdFlexTable.setWidget(0, 0, lblEditAttribute);
	attributeComboBox.addItem("HIV STATUS", "patient:hiv_status");
	attributeComboBox.addItem("PATIENT TYPE", "patient:patient_type");
	attributeComboBox.addItem("FIRST NAME", "person:first_name;DEMOG:F_NAME");
	attributeComboBox.addItem("LAST NAME", "person:last_name;DEMOG:L_NAME");
	attributeComboBox.addItem("GENDER", "person:gender");
	attributeComboBox.addItem("DATE OF BIRTH", "person:dob");
	attributeComboBox.addItem("NATIONAL ID", "person:national_id;TB_REG:NIC");// person:national_id;TB_REG:NIC
	attributeComboBox.addItem("PRIMARY CONTACT", "person:mobile;TB_REG:PRIMARY_CELL");
	attributeComboBox.addItem("SECONDARY CONTACT", "person:alternate_mobile;TB_REG:SECONDARY_CELL");
	attributeComboBox.addItem("TERTIARY CONTACT", "person:tertiary_mobile;TB_REG:TERTIARY_CELL");
	clientIdFlexTable.setWidget(0, 1, attributeComboBox);
	lblNewValue.setWordWrap(false);
	clientIdFlexTable.setWidget(1, 0, lblNewValue);
	clientIdFlexTable.setWidget(1, 1, valueSimplePanel);
	flexTable.getRowFormatter().setVerticalAlign(5, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
	flexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
	flexTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
	flexTable.setWidget(3, 0, lblNICformat);

	middleVerticalPanel.setCellHorizontalAlignment(grid, HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.setWidget(4, 0, grid);
	grid.setWidth("50%");
	grid.setWidget(0, 0, saveButton);
	saveButton.setWidth("100%");
	saveButton.setEnabled(false);
	grid.setWidget(0, 1, closeButton);
	closeButton.setWidth("100%");
	flexTable.getCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.getFlexCellFormatter().setRowSpan(1, 0, 2);

	createHandlers();
	IRZClient.refresh(flexTable);
	setRights(formName);
    }

    public void createHandlers()
    {
	attributeComboBox.addChangeHandler(this);
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
	clientId = "";
	IRZClient.clearControls(middleVerticalPanel);
    }

    /**
     * To validate data in widgets before performing DML operations
     * 
     * @return
     */
    public boolean validate()
    {
	valid = true;
	/* Validate mandatory fields */
	if (IRZClient.get(clientIdTextBox).equals(""))
	{
	    valid = false;
	}
	if (!valid)
	{
	    Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
	    load(false);
	}
	return valid;
    }

    public void saveData()
    {
	if (validate())
	{
	    Date enteredDate = new Date();
	    final int eId = 0;
	    final String clientId = IRZClient.get(clientIdTextBox).toUpperCase();
	    final String pid2 = IRZ.getCurrentUserName();

	    EncounterId encounterId = new EncounterId(eId, clientId, pid2, formName);
	    final Encounter encounter = new Encounter(encounterId, IRZ.getCurrentLocation());
	    encounter.setLocationId(IRZ.getCurrentLocation());
	    encounter.setDateEntered(enteredDate);
	    encounter.setDateStart(new Date());
	    encounter.setDateEnd(new Date());

	    final ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults>();
	    String[] parts = IRZClient.get(attributeComboBox).split(";");

	    final String[] attribute = parts[0].split(":");
	    final String value = IRZClient.get(valueSimplePanel.getWidget()).toUpperCase();

	    /*
	     * Checks added on NIC ..If attribute is NIC check its regular
	     * expression which is stored in database through Encounter Elements
	     * form Client Edit form has Attribute and Value no regular
	     * expression is saved against them as they are variable TB
	     * registration saves encounter elements like NIC MOBILE etc
	     * NIC,PRIMARY MOBILE,SECONDARY MOBILE,TERTIARY MOBILE had no checks
	     * on the type of input so person:national_id;TB_REG:NIC is split in
	     * order to get the Encounter type(TB_REG) Encounter Element(NIC)
	     * and regular expression against the encounter element then the
	     * table which needs to be updated and the attribute which is to be
	     * edited person table national_id attribute
	     */

	    if (parts.length != 1)// part[0]=person:national_id and
				  // part[1]=TB_REG:NIC parts.length=2
	    {
		String[] encounterElement = parts[1].split(":");
		try
		{
		    service.findEncounterElement(encounterElement[0], encounterElement[1], new AsyncCallback<EncounterElement>() {
			public void onSuccess(EncounterElement result)
			{
			    String regex = result.getValidator();
			    if (!value.matches(regex))
			    {
				Window.alert(CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR));
				load(false);
			    }
			    else
			    {
				encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "ATTRIBUTE"), attribute[1]));
				encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "VALUE"), value));
				try
				{
				    String[] tableInfo = IRZClient.get(attributeComboBox).split(";");
				    String getTableName[] = tableInfo[0].split(":");
				    String tableName = getTableName[0];
				    String identifierName = tableName.equals("person") ? "pid":"patient_id";
				    String columnName = getTableName[1];
				    String query = "update " + tableName + " set " + columnName + " = '" + value + "' where " + identifierName + " = '" + clientId + "'";
				    service.savePatientEdit(query, encounter, encounterResults.toArray(new EncounterResults[] {}), new AsyncCallback<String>() {
					public void onSuccess(String result)
					{
					    if (result.equals("SUCCESS"))
					    {
						Window.alert(CustomMessage.getInfoMessage(InfoType.UPDATED));
						clearUp();
					    }
					    else
						Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
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

			public void onFailure(Throwable caught)
			{
			}
		    });
		} catch(Exception e1)
		{
		    e1.printStackTrace();
		}
	    }
	    else if (parts.length == 1)// "patient:hiv_status"
	    {
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "ATTRIBUTE"), attribute[1]));
		encounterResults.add(new EncounterResults(new EncounterResultsId(eId, clientId, pid2, formName, "VALUE"), value));
		try
		{
		    String[] tableInfo = IRZClient.get(attributeComboBox).split(":");
		    String tableName = tableInfo[0];
		    String identifierName = tableName.equals("person") ? "pid":"patient_id";
		    String columnName = tableInfo[1];
		    if (columnName.equals("hiv_status"))
		    {
			if (value.equals("POSITIVE"))
			{
			    try
			    {
				service.findParticularVisit(clientId, "HIV", new AsyncCallback<Visit>() {

				    @Override
				    public void onFailure(Throwable caught)
				    {
					caught.printStackTrace();

				    }

				    @Override
				    public void onSuccess(Visit result)
				    {
					if (result != null)
					{
					    result.setDiseaseConfirmed(true);
					    try
					    {
						service.updateVisit(result, new AsyncCallback<Boolean>() {

						    @Override
						    public void onFailure(Throwable caught)
						    {
							caught.printStackTrace();
							load(false);
						    }

						    @Override
						    public void onSuccess(Boolean result)
						    {
							if (result)
							{
							    System.out.println("Visit Purpose HIV Updated");
							}
							else
							    System.out.println("Visit Purpose HIV NOT FOUND");

						    }

						});
					    } catch(Exception e)
					    {
						System.out.println("Visit Purpose HIV Update CAUGHT IN CLIENT EDIT");
						e.printStackTrace();
					    }
					}
				    }

				});
			    } catch(Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else
			{
			    try
			    {
				service.findParticularVisit(clientId, "HIV", new AsyncCallback<Visit>() {

				    @Override
				    public void onFailure(Throwable caught)
				    {
					caught.printStackTrace();
				    }

				    @Override
				    public void onSuccess(Visit result)
				    {
					if (result != null)
					{
					    result.setDiseaseConfirmed(false);
					    try
					    {
						service.updateVisit(result, new AsyncCallback<Boolean>() {

						    @Override
						    public void onFailure(Throwable caught)
						    {
							caught.printStackTrace();
							load(false);
						    }

						    @Override
						    public void onSuccess(Boolean result)
						    {
							if (result)
							{
							    System.out.println("Visit Purpose HIV Updated");
							}
							else
							    System.out.println("Visit Purpose HIV NOT FOUND");

						    }

						});
					    } catch(Exception e)
					    {
						System.out.println("Visit Purpose HIV Update CAUGHT IN CLIENT EDIT");
						e.printStackTrace();
					    }
					}
				    }

				});
			    } catch(Exception e)
			    {
				e.printStackTrace();
			    }

			}
		    }
		    String query = "update " + tableName + " set " + columnName + " = '" + value + "' where " + identifierName + " = '" + clientId + "'";
		    service.savePatientEdit(query, encounter, encounterResults.toArray(new EncounterResults[] {}), new AsyncCallback<String>() {
			public void onSuccess(String result)
			{
			    if (result.equals("SUCCESS"))
			    {
				Window.alert(CustomMessage.getInfoMessage(InfoType.UPDATED));
				clearUp();
			    }
			    else
				Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
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
		clientId = IRZClient.get(clientIdTextBox);
		clientInfoTextArea.setText("");
		service.findPerson(clientId, new AsyncCallback<Person>() {
		    public void onSuccess(Person result)
		    {
			try
			{
			    if (result == null)
			    {
				Window.alert(CustomMessage.getErrorMessage(ErrorType.ID_INVALID));
				load(false);
				return;
			    }
			    service
			    .getTableData(
			    "select concat(first_name, ' ', last_name) as name, DATE_FORMAT(dob, '%D %b %Y') as dob, DATE_FORMAT(visit_date, '%d-%b-%y') as visit_date, visit_purpose, patient_type, national_id, mobile, alternate_mobile, tertiary_mobile, hiv_status from person "
			    + "inner join patient on patient.patient_id = person.pid " + "inner join visit using (patient_id) where patient.patient_id='" + clientId + "'",
			    new AsyncCallback<String[][]>() {
				public void onSuccess(String[][] result)
				{
				    load(false);
				    if (result != null)
				    {
					String[] record = result[0];
					for (int i = 0; i < record.length; i++)
					    if (record[i] == null)
						record[i] = "";
					StringBuilder info = new StringBuilder();
					info.append("Name: " + record[0] + "\n");
					info.append("Date of Birth: " + record[1] + "\n");
					info.append("Date of Visit: " + record[2] + "\n");
					info.append("Purpose of Visit: " + record[3] + "\n");
					info.append("Patient Type: " + record[4] + "\n");
					info.append("National ID: " + record[5] + "\n");
					info.append("Primary Contact: " + record[6] + "\n");
					info.append("Secondary Contact: " + record[7] + "\n");
					info.append("Tertiary Contact: " + record[8] + "\n");
					info.append("HIV Status: " + record[9] + "\n");
					clientInfoTextArea.setText(info.toString());
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

		    public void onFailure(Throwable caught)
		    {
			caught.printStackTrace();
			load(false);
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
	    if (clientId == "")
	    {
		Window.alert("Please first check Client's ID.");
		load(false);
		return;
	    }
	    saveData();
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
	if (sender == attributeComboBox)
	{
	    int selection = attributeComboBox.getSelectedIndex();
	    Widget widget = null;
	    ListBox listBox = new ListBox();
	    TextBox textBox = new TextBox();
	    DateBox dateBox = new DateBox();

	    switch (selection)
	    {
	    // HIV STATUS
	    case 0:
		listBox.setName("HIV_STATUS");
		IRZClient.refresh(listBox);
		lblNICformat.setVisible(false);
		widget = listBox;
		break;
	    // PATIENT TYPE
	    case 1:
		listBox.setName("PATIENT_TYPE");
		IRZClient.refresh(listBox);
		lblNICformat.setVisible(false);
		widget = listBox;
		break;
	    // FIRST NAME
	    case 2:
		// LAST NAME
	    case 3:
		textBox.setMaxLength(20);
		lblNICformat.setVisible(false);
		widget = textBox;
		break;
	    // GENDER
	    case 4:
		listBox.clear();
		listBox.addItem("MALE", "M");
		listBox.addItem("FEMALE", "F");
		lblNICformat.setVisible(false);
		widget = listBox;
		break;
	    // DATE OF BIRTH
	    case 5:
		dateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd")));
		lblNICformat.setVisible(false);
		widget = dateBox;
		break;
	    // NATIONAL ID
	    case 6:
		textBox.setMaxLength(15);
		lblNICformat.setVisible(true);
		widget = textBox;
		break;
	    // CONTACTS
	    case 7:
	    case 8:
	    case 9:

		textBox.setMaxLength(10);
		widget = textBox;
		break;
	    }
	    valueSimplePanel.setWidget(widget);
	}
    }
}
