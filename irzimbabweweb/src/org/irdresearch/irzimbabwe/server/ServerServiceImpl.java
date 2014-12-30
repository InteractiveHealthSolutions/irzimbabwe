package org.irdresearch.irzimbabwe.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.irdresearch.irzimbabwe.client.ServerService;
import org.irdresearch.irzimbabwe.server.sms.SmsSystem;
import org.irdresearch.irzimbabwe.server.sms.SmsUtil;
import org.irdresearch.irzimbabwe.server.util.DateTimeUtil;
import org.irdresearch.irzimbabwe.server.util.HibernateUtil;
import org.irdresearch.irzimbabwe.server.util.MDHashUtil;
import org.irdresearch.irzimbabwe.server.util.ReportUtil;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.Parameter;
import org.irdresearch.irzimbabwe.shared.SmsStatus;
import org.irdresearch.irzimbabwe.shared.model.*;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 * 
 * @author owais.hussain@irdresearch.org
 */
@SuppressWarnings("serial")
public class ServerServiceImpl extends RemoteServiceServlet implements ServerService
{
    private static String applicationPath = "";
    private static String propertiesFilePath = "";
    @SuppressWarnings("unused")
    private static Boolean serverStarted = SmsSystem.startSmsSystem();

    public ServerServiceImpl()
    {
	String currentDirectory = System.getProperty("user.dir");
	System.out.println("Current directory:" + currentDirectory);
	if (currentDirectory.startsWith("/"))
	    applicationPath = "/var/lib/tomcat6/webapps/irzimbabwe/";
	else
	    applicationPath = "C:\\workspace2\\irzimbabwe\\war\\";
	propertiesFilePath = applicationPath + "irzimbabwe.properties";
	setProperties();
    }

    /**
     * Initializes properties by reading from properties file
     * 
     * @return true
     */
    private Boolean setProperties()
    {
	ArrayList<String> text = new ArrayList<String>();
	try
	{
	    FileInputStream fis = new FileInputStream(propertiesFilePath);
	    DataInputStream dis = new DataInputStream(fis);
	    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
	    String strLine;
	    while ((strLine = br.readLine()) != null)
		text.add(strLine);
	    dis.close();

	    /* Initially set defaults */
	    IRZ.setProjectTitle("Interactive Reminders for Zimbabwe");
	    IRZ.setDatabaseName("irzimbabwe");
	    IRZ.setReportingDatabase(IRZ.getDatabaseName() + "_rpt");
	    IRZ.setSessionLimit(15 * 60 * 1000);
	    IRZ.setHashingAlgorithm("SHA");
	    IRZ.setCurrentVersion("1.0.0");

	    for (String s : text)
	    {
		if (s.startsWith("#"))
		    continue;
		String[] parts = s.split("=");
		if (parts.length < 2)
		    continue;
		if (parts[0].equals("resources_path"))
		    IRZ.setResourcesPath(applicationPath);
		else if (parts[0].equals("current_version"))
		    IRZ.setCurrentVersion(parts[1]);
		else if (parts[0].equals("project_title"))
		    IRZ.setProjectTitle(parts[1]);
		else if (parts[0].equals("database_name"))
		    IRZ.setDatabaseName(parts[1]);
		else if (parts[0].equals("data_warehouse_name"))
		    IRZ.setReportingDatabase(parts[1]);
		else if (parts[0].equals("reports_directory_name"))
		    IRZ.setReportsDirectoryName(parts[1]);
		else if (parts[0].equals("session_limit"))
		    IRZ.setSessionLimit(Integer.parseInt(parts[1]) * 1000);
		else if (parts[0].equals("hashing_algorithm"))
		    IRZ.setHashingAlgorithm(parts[1]);
		else if (parts[0].equals("service_sms_pusher_runup_min"))
		    IRZ.setSmsPusherServiceRunupMins(Integer.parseInt(parts[1]));
		else if (parts[0].equals("service_sms_updater_runup_min"))
		    IRZ.setSmsUpdaterServiceRunupMins(Integer.parseInt(parts[1]));
		else if (parts[0].equals("service_response_reader_runup_min"))
		    IRZ.setResponseReaderServiceRunupMins(Integer.parseInt(parts[1]));
		else if (parts[0].equals("smstarseel_project_id"))
		    IRZ.setSmsAppAssignedProjectID(Integer.parseInt(parts[1]));
	    }
	    return true;
	} catch(IOException e)
	{
	    e.printStackTrace();
	    System.out.print("Unable to read properties file. Make sure that <" + propertiesFilePath + "> exists in the Application root directory and is accessible.");
	    System.exit(-1);
	    return false;
	}
    }

    private String arrangeFilter(String filter)
    {
	if (filter.trim().equalsIgnoreCase(""))
	    return "";
	return (filter.toUpperCase().contains("WHERE") ? "":" where ") + filter;
    }

    /**
     * User authentication: Checks whether user exists, then match his password
     * and finally checks if he has the role defined. If user name is ADMIN and
     * no Users are defined, then calculates the password using a specific
     * formula and matches with the input. If role is not defined, then the
     * currentStatus property is set to 'D' - for deined
     * 
     * @return User
     */
    @SuppressWarnings("deprecation")
    public User authenticate(String userName, String password)
    {
	User user = findUser(userName);
	if (userName.equalsIgnoreCase("ADMIN"))
	{
	    Date dt = new Date();
	    int year = dt.getYear() + 1900;
	    int month = dt.getMonth() + 1;
	    int date = dt.getDate();
	    // Password = ASCII code of ADMIN in series * current year *
	    // current month * current date
	    long pass = year * month * date;
	    if (password.equals(String.valueOf(pass)))
		user = new User("-1", userName, 'A', "", "", "", "");
	}
	else
	{
	    if (user != null)
	    {
		// Authenticate password
		if (UserAuthentication.validatePassword(userName, password))
		{
		    recordLogin(userName);
		}
		else
		    user = null;
	    }
	}
	setCurrentUser(userName, user.getRole().toUpperCase());
	return user;
    }

    /**
     * Checks if a user exists in the database
     * 
     * @return Boolean
     */
    public Boolean authenticateUser(String userName)
    {
	if (!UserAuthentication.userExsists(userName))
	    return false;
	return true;
    }

    /**
     * Verifies secret answer against stored secret question
     * 
     * @return Boolean
     */
    public Boolean verifySecretAnswer(String userName, String secretAnswer)
    {
	if (!UserAuthentication.validateSecretAnswer(userName, secretAnswer))
	    return false;
	return true;
    }

    /**
     * Get number of records in a table, given appropriate filter
     * 
     * @return Long
     */
    public Long count(String tableName, String filter)
    {
	Object obj = HibernateUtil.util.selectObject("select count(*) from " + tableName + " " + arrangeFilter(filter));
	return Long.parseLong(obj.toString());
    }

    /**
     * Checks existence of data by counting number of records in a table, given
     * appropriate filter
     * 
     * @return Boolean
     */
    public Boolean exists(String tableName, String filter)
    {
	long count = count(tableName, filter);
	return count > 0;
    }

    /**
     * Generates CSV file from query passed along with the filters
     * 
     * @param query
     * @return
     */
    public String generateCSVfromQuery(String database, String query)
    {
	return ReportUtil.generateCSVfromQuery(database, query, ',');
    }

    /**
     * Generate report on server side and return the path it was created to
     * 
     * @param Path
     *            of report as String Report parameters as Parameter[] Report to
     *            be exported in csv format as Boolean
     * @return String
     */
    public String generateReport(String fileName, Parameter[] params, boolean export)
    {
	return ReportUtil.generateReport(fileName, params, export);
    }

    /**
     * Generate report on server side based on the query saved in the Database
     * against the reportName and return the path it was created to
     * 
     * @param reportName
     * @param params
     * @param export
     * @return
     */
    public String generateReportFromQuery(String database, String reportName, String query, Boolean export)
    {
	return ReportUtil.generateReportFromQuery(database, 
	reportName, 
	query,
	export);
    }

    public String[] getColumnData(String tableName, String columnName, String filter)
    {
	Object[] data = HibernateUtil.util.selectObjects("select distinct cast(" + columnName + " as char) from " + tableName + " " + arrangeFilter(filter));
	String[] columnData = new String[data.length];
	for (int i = 0; i < data.length; i++)
	    columnData[i] = data[i].toString();
	return columnData;
    }

    /**
     * Sets current user name, this is due to a strange GWT bug/feature that
     * shared variables, set from Client-side appear to be empty on Server-side
     * code
     * 
     * @return
     */
    public void setCurrentUser(String userName, String role)
    {
	IRZ.setCurrentUserName(userName);
	IRZ.setCurrentRole(role);
    }

    public String getCurrentUserName()
    {
	return IRZ.getCurrentUserName();
    }

    public String[][] getSchema()
    {
	Object[][] data = HibernateUtil.util.selectData("select TABLE_NAME, COLUMN_NAME, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH from information_schema.columns where TABLE_SCHEMA = '"
	+ IRZ.getDatabaseName() + "'");
	String[][] schema = new String[data.length][];
	for (int i = 0; i < data.length; i++)
	{
	    schema[i] = new String[data[i].length];
	    for (int j = 0; j < data[i].length; j++)
	    {
		if (data[i][j] == null)
		    data[i][j] = 0;
		schema[i][j] = data[i][j].toString();
	    }
	}
	return schema;
    }

    /**
     * Get default values to be used on front-ends
     */
    public Defaults[] getDefaults()
    {
	Object[] objs = HibernateUtil.util.findObjects("from Defaults");
	Defaults[] defaults = new Defaults[objs.length];
	for (int i = 0; i < objs.length; i++)
	{
	    Defaults def = (Defaults) objs[i];
	    defaults[i] = def;
	}
	return defaults;
    }

    /**
     * Get all definitions for static data
     */
    public Definition[] getDefinitions()
    {
	Object[] objs = HibernateUtil.util.findObjects("from Definition");
	Definition[] definitions = new Definition[objs.length];
	for (int i = 0; i < objs.length; i++)
	{
	    Definition def = (Definition) objs[i];
	    definitions[i] = def;
	}
	return definitions;
    }

    public String getObject(String tableName, String columnName, String filter)
    {
	return HibernateUtil.util.selectObject("select " + columnName + " from " + tableName + arrangeFilter(filter)).toString();
    }

    public String[] getQueriesResults(String[] queries)
    {
	String[] results = new String[queries.length];
	for (int i = 0; i < results.length; i++)
	{
	    try
	    {
		results[i] = HibernateUtil.util.selectObject(queries[i]).toString();
	    } catch(Exception e)
	    {
		results[i] = "";
		e.printStackTrace();
	    }
	}
	return results;
    }

    public String[] getDumpFiles()
    {
	ArrayList<String> files = new ArrayList<String>();
	File folder = new File(IRZ.getResourcesPath());
	for (File f : folder.listFiles())
	{
	    if (f.isFile())
	    {
		String file = f.getPath();
		if (file.endsWith(".zip") || file.endsWith(".ZIP"))
		    files.add(file);
	    }
	}
	Collections.sort(files);
	Collections.reverse(files);
	return files.toArray(new String[] {});
    }

    public String[][] getReportsList()
    {
	return ReportUtil.getReportList();
    }

    public String[] getRowRecord(String tableName, String[] columnNames, String filter)
    {
	return getTableData(tableName, columnNames, filter)[0];
    }

    public String getSecretQuestion(String userName)
    {
	User user = (User) HibernateUtil.util.findObject("from User where userName='" + userName + "'");
	return user.getSecretQuestion();
    }

    @SuppressWarnings("deprecation")
    public String getSnapshotTime()
    {
	Date dt = new Date();
	Object obj = HibernateUtil.util.selectObject("select max(date_end) from encounter where date(date_end) < '" + (dt.getYear() + 1900) + "-" + (dt.getMonth() + 1) + "-" + dt.getDate() + "'");
	return obj.toString();
    }

    public String[][] getTableData(String tableName, String[] columnNames, String filter)
    {
	StringBuilder columnList = new StringBuilder();
	for (String s : columnNames)
	{
	    columnList.append(s);
	    columnList.append(",");
	}
	columnList.deleteCharAt(columnList.length() - 1);
	String query = "select " + columnList.toString() + " from " + tableName + " " + arrangeFilter(filter);
	return getTableData(query);
    }

    public String[][] getTableData(String sqlQuery)
    {
	Object[][] data = HibernateUtil.util.selectData(sqlQuery);
	String[][] stringData = new String[data.length][];
	for (int i = 0; i < data.length; i++)
	{
	    stringData[i] = new String[data[i].length];
	    for (int j = 0; j < stringData[i].length; j++)
	    {
		if (data[i][j] == null)
		    data[i][j] = "";
		String str = data[i][j].toString();
		stringData[i][j] = str;
	    }
	}
	return stringData;
    }

    public Boolean[] getUserRgihts(String userName, String role, String menuName)
    {
	if (role.equalsIgnoreCase("ADMIN"))
	{
	    Boolean[] rights = { true, true, true, true, true };
	    return rights;
	}
	UserRights userRights = (UserRights) HibernateUtil.util.findObject("from UserRights where id.userRole='" + role + "' and id.menuName='" + menuName + "'");
	Boolean[] rights = { userRights.isSearchAccess(), userRights.isInsertAccess(), userRights.isUpdateAccess(), userRights.isDeleteAccess(), userRights.isPrintAccess() };
	return rights;
    }

    public void recordLogin(String userName)
    {
	User user = (User) HibernateUtil.util.findObject("from User where userName='" + userName + "'");
	HibernateUtil.util.recordLog(LogType.LOGIN, user);
	user.setLoggedIn(true);
	HibernateUtil.util.update(user);
    }

    /**
     * Search for value of a specific element (if present) in an array of
     * EncounterResults
     * 
     * @param encounterResults
     *            array
     * @param element
     * @return
     */
    public String searchValueInEncounterResults(EncounterResults[] encounterResults, String element)
    {
	for (EncounterResults er : encounterResults)
	    if (er.getId().getElement().equals(element))
		return er.getValue();
	return null;
    }

    /**
     * Update User's Login Status to false and save logout date to last login
     * record
     */
    public void recordLogout(String userName)
    {
	User user = (User) HibernateUtil.util.findObject("from User where userName='" + userName + "'");
	String selectQuery = "select max(login_no) from log_login where user_name='" + userName + "'";
	int num = Integer.parseInt(HibernateUtil.util.selectObject(selectQuery).toString());
	String updateQuery = "update log_login set date_logout = '" + DateTimeUtil.getSQLDate(new Date()) + "' where login_no = " + num + "";
	HibernateUtil.util.runCommand(updateQuery);
	user.setLoggedIn(false);
	HibernateUtil.util.update(user);
    }

    public int execute(String query)
    {
	return HibernateUtil.util.runCommand(query);
    }

    public Boolean execute(String[] queries)
    {
	for (String s : queries)
	{
	    boolean result = execute(s) >= 0;
	    if (!result)
		return false;
	}
	return true;
    }

    public Boolean executeProcedure(String procedure)
    {
	return HibernateUtil.util.runProcedure(procedure);
    }

    /* Delete methods */
    public Boolean deleteDefinition(Definition definition)
    {
	return HibernateUtil.util.delete(definition);
    }

    public Boolean deleteEncounter(Encounter encounter)
    {
	boolean result;
	// Delete Encounter Results
	HibernateUtil.util.findObjects("from EncounterResults where id.eId='" + encounter.getId().getEId() + "' and id.pid1='" + encounter.getId().getPid1() + "' and id.pid2='"
	+ encounter.getId().getPid2() + "'");
	// Delete Encounter
	result = HibernateUtil.util.delete(encounter);
	return result;
    }

    public Boolean deleteEncounterElement(EncounterElement encounterElement)
    {
	return HibernateUtil.util.delete(encounterElement);
    }

    public Boolean deleteEncounterPrerequisite(EncounterPrerequisite encounterPrerequisite)
    {
	return HibernateUtil.util.delete(encounterPrerequisite);
    }

    public Boolean deleteEncounterResults(EncounterResults encounterResults)
    {
	return HibernateUtil.util.delete(encounterResults);
    }

    public Boolean deleteEncounterType(EncounterType encounterType)
    {
	Boolean result = false;
	if (!exists("encounter_element", "encounter_type='" + encounterType.getEncounterType() + "'"))
	    result = HibernateUtil.util.delete(encounterType);
	return result;
    }

    public Boolean deleteFeedback(Feedback feedback)
    {
	return HibernateUtil.util.delete(feedback);
    }

    public Boolean deleteLocation(Location location)
    {
	return HibernateUtil.util.delete(location);
    }

    public Boolean deletePatient(Patient patient)
    {
	return HibernateUtil.util.delete(patient);
    }

    public Boolean deletePerson(Person person)
    {
	return HibernateUtil.util.delete(person);
    }

    public Boolean deletePersonRole(PersonRole personRole)
    {
	return HibernateUtil.util.delete(personRole);
    }

    public Boolean deleteReferral(Referral referral)
    {
	return HibernateUtil.util.delete(referral);
    }

    public Boolean deleteSputumTest(SputumTest sputumTest)
    {
	return HibernateUtil.util.update(sputumTest);
    }

    public Boolean deleteSmsText(SmsText smstext)
    {
	return HibernateUtil.util.delete(smstext);
    }

    public Boolean deleteUser(User user)
    {
	// Delete any roles
	PersonRole[] personRoles = findPersonRoles(user.getPid());
	for (PersonRole r : personRoles)
	    HibernateUtil.util.delete(r);
	// Delete any mappings
	UserMapping[] userMappings = findUserMappingsByUser(user.getPid());
	for (UserMapping u : userMappings)
	    HibernateUtil.util.delete(u);
	// Delet person object
	Person p = findPerson(user.getPid());
	if (p != null)
	    HibernateUtil.util.delete(p);
	return HibernateUtil.util.delete(user);
    }

    public Boolean deleteUserMapping(UserMapping userMapping)
    {
	return HibernateUtil.util.delete(userMapping);
    }

    public Boolean deleteUserRights(UserRights userRights)
    {
	return HibernateUtil.util.delete(userRights);
    }

    public Boolean deleteVisit(Visit visit)
    {
	return HibernateUtil.util.delete(visit);
    }

    public Boolean deleteVisits(Visit[] visit)// delete multiple visits in case
// of saveEncounterWithResults failure
    {
	Boolean deleteSuccess = false;
	for (Visit v : visit)
	{
	    deleteSuccess = deleteVisit(v);
	}
	return deleteSuccess;
    }

    /* Find methods */
    public Definition findDefinition(String definitionType, String definitionKey)
    {
	Definition definition = (Definition) HibernateUtil.util.findObject("from Definition as def where def.id.definitionType='" + definitionType + "' and def.id.definitionKey='" + definitionKey
	+ "'");
	return definition;
    }

    public Encounter findEncounter(EncounterId encounterID)
    {
	Encounter encounter = (Encounter) HibernateUtil.util.findObject("from Encounter where id.pid1='" + encounterID.getPid1() + "' and id.pid2='" + encounterID.getPid2() + "' and id.EId='"
	+ encounterID.getEId() + "'");
	return encounter;
    }

    public EncounterElement findEncounterElement(String encounterType, String element)
    {
	EncounterElement eElement = (EncounterElement) HibernateUtil.util.findObject("from EncounterElement where id.encounterType='" + encounterType + "' and id.element='" + element + "'");
	return eElement;
    }

    public EncounterElement[] findEncounterElements(String encounterType)
    {
	Object[] objects = HibernateUtil.util.findObjects("from EncounterElement where id.encounterType='" + encounterType + "'");
	EncounterElement[] eElements = new EncounterElement[objects.length];
	for (int i = 0; i < eElements.length; i++)
	    eElements[i] = (EncounterElement) objects[i];
	return eElements;
    }

    public EncounterPrerequisite findEncounterPrerequisite(EncounterPrerequisiteId prerequisiteId)
    {
	EncounterPrerequisite prereq = (EncounterPrerequisite) HibernateUtil.util.findObject("from EncounterPrerequisite where id.encounterType='" + prerequisiteId.getEncounterType()
	+ "' and id.prerequisiteNo=" + prerequisiteId.getPrerequisiteNo() + "");
	return prereq;
    }

    public EncounterPrerequisite[] findEncounterPrerequisites(EncounterType encounterType)
    {
	Object[] objects = HibernateUtil.util.findObjects("from EncounterPrerequisite where id.encounterType='" + encounterType.getEncounterType() + "'");
	EncounterPrerequisite[] prereqs = new EncounterPrerequisite[objects.length];
	for (int i = 0; i < objects.length; i++)
	    prereqs[i] = (EncounterPrerequisite) objects[i];
	return prereqs;
    }

    public EncounterResults[] findEncounterResults(EncounterId encounterId)
    {
	Object[] objects = HibernateUtil.util.findObjects("from EncounterResults where id.pid1='" + encounterId.getPid1() + "' and id.pid2='" + encounterId.getPid2() + "' and id.encounterType='"
	+ encounterId.getEncounterType() + "' and id.EId='" + encounterId.getEId() + "'");
	EncounterResults[] eResults = new EncounterResults[objects.length];
	for (int i = 0; i < eResults.length; i++)
	    eResults[i] = (EncounterResults) objects[i];
	return eResults;
    }

    public EncounterResults findEncounterResultsByElement(EncounterResultsId encounterResultsID)
    {
	EncounterResults eResults = (EncounterResults) HibernateUtil.util.findObject("from EncounterResults where pid1='" + encounterResultsID.getPid1() + "' and pid2='"
	+ encounterResultsID.getPid2() + "' and eid='" + encounterResultsID.getEId() + "' and element='" + encounterResultsID.getElement() + "'");
	return eResults;
    }

    public EncounterType findEncounterType(String encounterType)
    {
	EncounterType eType = (EncounterType) HibernateUtil.util.findObject("from EncounterType where encounterType='" + encounterType + "'");
	return eType;
    }

    public Location findLocation(String locationID)
    {
	Location location = (Location) HibernateUtil.util.findObject("from Location where locationId='" + locationID + "'");
	return location;
    }

    public Location[] findLocationsByType(String locationType)
    {
	Object[] list = HibernateUtil.util.findObjects("from Location where locationType='" + locationType + "'");
	Location[] locations = new Location[list.length];
	for (int i = 0; i < list.length; i++)
	    locations[i] = (Location) list[i];
	return locations;
    }

    public Patient findPatient(String patientID)
    {
	Patient patient = (Patient) HibernateUtil.util.findObject("from Patient where patientId='" + patientID + "'");
	return patient;
    }

    public Patient findPatientByMR(String Mrno)
    {
	Patient patient = (Patient) HibernateUtil.util.findObject("from Patient where mrNo='" + Mrno + "'");
	return patient;
    }

    public Person findPerson(String PID)
    {
	Person person = (Person) HibernateUtil.util.findObject("from Person where pid='" + PID + "'");
	return person;
    }

    public PersonRole[] findPersonRoles(String pid)
    {
	Object[] objs = HibernateUtil.util.findObjects("from PersonRole where pid='" + pid + "'");
	PersonRole[] roles = new PersonRole[objs.length];
	for (int i = 0; i < objs.length; i++)
	    roles[i] = (PersonRole) objs[i];
	return roles;
    }

    public Person[] findPersonsByName(String firstName, String lastName)
    {
	Object[] objs = HibernateUtil.util.findObjects("from Person where firstName like '" + firstName + "%' and lastName like '" + lastName + "%'");
	Person[] persons = new Person[objs.length];
	for (int i = 0; i < objs.length; i++)
	    persons[i] = (Person) objs[i];
	return persons;
    }

    public Person findPersonsByNIC(String NIC)
    {
	Person person = (Person) HibernateUtil.util.findObject("from Person where NIC='" + NIC + "'");
	return person;
    }

    public Referral findReferral(String patientId)
    {
	Referral referral = (Referral) HibernateUtil.util.findObject("from Referral where patientId='" + patientId + "'");
	return referral;
    }

    public SputumTest findSputumTest(String patientId, Integer sampleNo)
    {
	SputumTest sputumTest = (SputumTest) HibernateUtil.util.findObject("from SputumTest where id.patientId='" + patientId + "' and id.sampleNo='" + sampleNo + "'");
	return sputumTest;
    }

    public SmsRule findSmsRule(String smsRuleId)
    {
	return (SmsRule) HibernateUtil.util.findObject("from SmsRule where ruleId='" + smsRuleId + "'");
    }

    public SmsRule[] findSmsRules()
    {
	Object[] objs = HibernateUtil.util.findObjects("from SmsRule");
	SmsRule[] smsRules = new SmsRule[objs.length];
	for (int i = 0; i < objs.length; i++)
	    smsRules[i] = (SmsRule) objs[i];
	return smsRules;
    }

    public SmsText findSmsText(String ruleId, String textId)
    {
	return (SmsText) HibernateUtil.util.findObject("from SmsText where ruleId='" + ruleId + "' and textId='" + textId + "'");
    }

    public SmsText[] findSmsTextByRule(String ruleId)
    {
	Object[] objs = HibernateUtil.util.findObjects("from SmsText where ruleId='" + ruleId + "'");
	SmsText[] smsText = new SmsText[objs.length];
	for (int i = 0; i < objs.length; i++)
	    smsText[i] = (SmsText) objs[i];
	return smsText;
    }

    public SmsText findSmsTextByLanguage(String ruleId, String preferredLanguageId)
    {
	return (SmsText) HibernateUtil.util.findObject("from SmsText where ruleId='" + ruleId + "' AND languageId='" + preferredLanguageId + "'");
    }

    public User findUser(String userName)
    {
	User user = (User) HibernateUtil.util.findObject("from User where userName='" + userName + "'");
	return user;
    }

    public UserMapping findUserMapping(UserMappingId userMappingId)
    {
	UserMapping mapping = (UserMapping) HibernateUtil.util.findObject("from UserMapping where id.userId='" + userMappingId.getUserId() + "' and id.locationId='" + userMappingId.getLocationId()
	+ "'");
	return mapping;
    }

    public UserMapping[] findUserMappingsByUser(String userId)
    {
	Object[] objs = HibernateUtil.util.findObjects("from UserMapping where id.userId='" + userId + "'");
	UserMapping[] userMappings = new UserMapping[objs.length];
	for (int i = 0; i < objs.length; i++)
	    userMappings[i] = (UserMapping) objs[i];
	return userMappings;
    }

    public Visit findParticularVisit(String patientId, String visitPurpose)
    {
	Visit visit = (Visit) HibernateUtil.util.findObject("from Visit where patientId='" + patientId + "' and visit_purpose='" + visitPurpose + "'");
	return visit;
    }

    public Visit findVisit(String patientId)
    {
	Visit visit = (Visit) HibernateUtil.util.findObject("from Visit where patientId='" + patientId + "'");
	return visit;
    }
    
    public Visit[] findAllVisits(String patient_id)
    {
	Object[] objs = HibernateUtil.util.findObjects("from Visit where patientId='"+patient_id+"'");
	Visit[] visits = new Visit[objs.length];
	for (int i=0;i<objs.length;i++) 
	{
		visits[i]=(Visit) objs[i];
	}
	return visits;
    }

    public UserRights findUserRights(String roleName, String tableName)
    {
	return UserAuthentication.getRights(roleName, tableName);
    }

    /* Save methods */
    public Boolean saveDefinition(Definition definition)
    {
	return HibernateUtil.util.save(definition);
    }

    private Boolean saveEncounter(Encounter encounter)
    {
	// Get the max encounter ID and add 1
	EncounterId currentID = encounter.getId();
	Object[] max = HibernateUtil.util.selectObjects("select max(e_id) from encounter where pid1='" + currentID.getPid1() + "' and pid2='" + currentID.getPid2() + "' and encounter_type='"
	+ currentID.getEncounterType() + "'");

	Integer maxInt = (Integer) max[0];
	if (maxInt == null)
	    currentID.setEId(1);
	else
	    currentID.setEId((maxInt.intValue() + 1));
	encounter.setId(currentID);
	return HibernateUtil.util.save(encounter);
    }

    public Boolean saveEncounterElement(EncounterElement element)
    {
	return HibernateUtil.util.save(element);
    }

    public Boolean saveEncounterPrerequisite(EncounterPrerequisite encounterPrerequisite)
    {
	// Get next Pre-requisite no. for person
	Long prerequisiteNo = Long.parseLong(getObject("encounter_prerequisite", "ifnull(max(prerequisite_no), 0)", "encounter_type='" + encounterPrerequisite.getId().getEncounterType() + "'")
	.toString());
	prerequisiteNo++;
	encounterPrerequisite.getId().setPrerequisiteNo(prerequisiteNo.intValue());
	return HibernateUtil.util.save(encounterPrerequisite);
    }

    private Boolean saveEncounterResults(EncounterResults encounterResults)
    {
	// Validate value if bounded by a validation
	/*
	 * when encounter type is CLIENT EDIT
	 * it gets 2 things ATTRIBUTE AND VALUE
	 * ATTRIBUTE contains "person;nic"
	 * and VALUE contains "123456/*" the edited value
	 * PROBLEM: We cannot fix the regex in db for the ATTRIBUTE or the VALUE
	 * because it will vary for different fields
	 * SO,in order to validate the CLIENT EDIT I access the
	 * Encounter_elements() in the savePatientEdit
	 */
	EncounterElement elem = findEncounterElement(encounterResults.getId().getEncounterType(), encounterResults.getId().getElement());
	String regex = elem.getValidator();
	if (!regex.equals("") && !encounterResults.getValue().matches(regex))
	    return null;
	return HibernateUtil.util.save(encounterResults);
    }

    public Boolean saveEncounterType(EncounterType encounterType)
    {
	return HibernateUtil.util.save(encounterType);
    }

    /**
     * Saves Encounter with Results. If the Pre-requisites are not satisfied or
     * encounter values are not valid then nothing will be saved.
     */
    public String saveEncounterWithResults(Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	// Check for Pre-Requisites
	EncounterType encounterType = findEncounterType(encounter.getId().getEncounterType());
	EncounterPrerequisite[] prereqs = findEncounterPrerequisites(encounterType);
	for (EncounterPrerequisite pr : prereqs)
	{
	    EncounterResults prereqResult = (EncounterResults) HibernateUtil.util.findObject("from EncounterResults where id.pid1='" + encounter.getId().getPid1() + "' and id.encounterType='"
	    + pr.getPrerequisiteEncounter() + "' and id.element='" + pr.getConditionElement() + "'");
	    if (prereqResult == null)
	    {
		result = "Pre-requisite form " + pr.getPrerequisiteEncounter() + " was not found.";
		return result;
	    }
	    else if (!prereqResult.getValue().matches(pr.getPossibleValueRegex()))
	    {
		result = "Pre-requisite was not satisfied. To submit this form, there must be a(n) " + pr.getPrerequisiteEncounter() + " form filled with value of question "
		+ pr.getConditionElement() + " like " + pr.getPossibleValueRegex() + "\n";
		return result;
	    }
	}
	// Get the max encounter ID and add 1
	EncounterId currentID = encounter.getId();
	Object[] max = HibernateUtil.util.selectObjects("select max(e_id) from encounter where pid1='" + currentID.getPid1() + "' and pid2='" + currentID.getPid2() + "' and encounter_type='"
	+ currentID.getEncounterType() + "'");
	Integer maxInt = (Integer) max[0];
	if (maxInt == null)
	    currentID.setEId(1);
	else
	    currentID.setEId((maxInt.intValue() + 1));
	encounter.setId(currentID);
	// Validate values of results if bounded by a validation
	for (EncounterResults er : encounterResults)
	{
	    er.getId().setEId(encounter.getId().getEId());
	    EncounterElement elem = findEncounterElement(er.getId().getEncounterType(), er.getId().getElement());
	    String regex = elem.getValidator();
	    if (regex == null)
		continue;
	    if (!regex.equals("") && !er.getValue().matches(regex))
	    {
		result = "Invalid value provided for question: " + elem.getId().getElement() + " (" + elem.getDescription() + ")\n";
		return result;
	    }
	}
	// Save Encounter
	if (saveEncounter(encounter))
	{
	    // Save Encounter Results
	    for (EncounterResults er : encounterResults)
		saveEncounterResults(er);
	    result = "SUCCESS";
	}
	else
	    result = "Could not save Form.";
	return result;
    }

    public Boolean saveFeedback(Feedback feedback)
    {
	return HibernateUtil.util.save(feedback);
    }

    public Boolean saveLocation(Location location)
    {
	// if (exists ("location", "location_name='" + location.getLocationName
	// () + "'"))
	// return null;
	// Get next location id for person
	// String locationId = getObject ("location",
	// "ifnull(max(location_id), 0) + 1", "").toString ();
	// location.setLocationId (locationId);
	return HibernateUtil.util.save(location);
    }

    public Boolean savePatient(Patient patient)
    {
	return HibernateUtil.util.save(patient);
    }

    public Boolean savePerson(Person person)
    {
	return HibernateUtil.util.save(person);
    }

    public Boolean savePersonRole(PersonRole personRole)
    {
	return HibernateUtil.util.save(personRole);
    }

    public Boolean saveReferral(Referral referral)
    {
	return HibernateUtil.util.save(referral);
    }

    public Boolean saveSputumTest(SputumTest sputumTest)
    {
	return HibernateUtil.util.save(sputumTest);
    }

    public Boolean saveSmsRule(SmsRule sms)
    {
	return HibernateUtil.util.save(sms);
    }

    public Boolean saveSmsLog(SmsLog smslog)
    {
	return HibernateUtil.util.save(smslog);
    }

    public Boolean saveSmsText(SmsText smstext)
    {
	return HibernateUtil.util.save(smstext);
    }

    public Boolean saveUser(User user, String[] roles)
    {
	Boolean result;
	long total = count("user", "");
	String shortName = user.getUserName();
	if (shortName.length() > 8)
	    shortName = shortName.replace(" ", "").replace("-", "").substring(0, 8);
	user.setPid(shortName + total);
	user.setPassword(MDHashUtil.getHashString(user.getPassword()));
	user.setSecretAnswer(MDHashUtil.getHashString(user.getSecretAnswer()));
	if (exists("user", "user_name='" + user.getUserName() + "'"))
	    result = null;
	else
	{
	    // Create a person first
	    Person person = new Person(user.getPid(), user.getUserName());
	    result = HibernateUtil.util.save(person);
	    if (result)
	    {
		result = HibernateUtil.util.save(user);
		// Create roles
		for (String s : roles)
		{
		    PersonRole role = new PersonRole(new PersonRoleId(user.getPid(), s));
		    result = HibernateUtil.util.save(role);
		}
	    }
	    else
	    {
		HibernateUtil.util.delete(person);
		HibernateUtil.util.delete(user);
	    }
	}
	return result;
    }

    public Boolean saveUserMapping(UserMapping userMapping)
    {
	return HibernateUtil.util.save(userMapping);
    }

    public Boolean saveUserRights(UserRights userRights)
    {
	return HibernateUtil.util.save(userRights);
    }

    public Boolean saveVisit(Visit visit)
    {
	return HibernateUtil.util.save(visit);
    }

    /* Update methods */
    public Boolean updateDefaults(Defaults[] defaults)
    {
	Boolean result = true;
	for (Defaults def : defaults)
	{
	    if (def != null)
	    {
		result = HibernateUtil.util.delete(def);
		result = HibernateUtil.util.save(def);
	    }
	}
	return result;
    }

    public Boolean updateDefinition(Definition definition)
    {
	return HibernateUtil.util.update(definition);
    }

    public Boolean updateEncounter(Encounter encounter)
    {
	return HibernateUtil.util.update(encounter);
    }

    public Boolean updateEncounterElement(EncounterElement element)
    {
	return HibernateUtil.util.update(element);
    }

    public Boolean updateEncounterPrerequisite(EncounterPrerequisite prerequisite)
    {
	return HibernateUtil.util.update(prerequisite);
    }

    public Boolean updateEncounterResults(EncounterResults encounterResults)
    {
	return HibernateUtil.util.update(encounterResults);
    }

    public Boolean updateEncounterType(EncounterType encounterType)
    {
	return HibernateUtil.util.update(encounterType);
    }

    public Boolean updateEncounterWithResults(Encounter encounter, EncounterResults[] encounterResults) throws Exception
    {
	Boolean result = null;
	// Validate values of results if bounded by a validation
	for (EncounterResults er : encounterResults)
	{
	    er.getId().setEId(encounter.getId().getEId());
	    EncounterElement elem = findEncounterElement(er.getId().getEncounterType(), er.getId().getElement());
	    String regex = elem.getValidator();
	    if (regex == null)
		continue;
	    if (!regex.equals("") && !er.getValue().matches(regex))
	    {
		return null;
	    }
	}
	// Save Encounter
	updateEncounter(encounter);
	// Save Encounter Results
	for (EncounterResults er : encounterResults)
	{
	    deleteEncounterResults(er);
	    result = saveEncounterResults(er);
	}
	return result;
    }

    public Boolean updateFeedback(Feedback feedback)
    {
	return HibernateUtil.util.update(feedback);
    }

    public Boolean updateLocation(Location location)
    {
	return HibernateUtil.util.update(location);
    }

    public Boolean updatePassword(String userName, String newPassword)
    {
	User user = (User) HibernateUtil.util.findObject("from User where userName = '" + userName + "'");
	user.setPassword(MDHashUtil.getHashString(newPassword));
	return HibernateUtil.util.update(user);
    }

    public Boolean updatePatient(Patient patient)
    {
	return HibernateUtil.util.update(patient);
    }

    public Boolean updatePerson(Person person)
    {
	return HibernateUtil.util.update(person);
    }

    public Boolean updatePersonRole(PersonRole personRole)
    {
	return HibernateUtil.util.update(personRole);
    }

    @Override
    public Boolean updateSmsRule(SmsRule rule)
    {
	return HibernateUtil.util.update(rule);
    }

    public Boolean updateSmsLog(SmsLog smslog)
    {
	return HibernateUtil.util.update(smslog);
    }

    public Boolean updateSmsRules(SmsRule[] rules)
    {
	for (int i = 0; i < rules.length; i++)
	{
	    try
	    {
		HibernateUtil.util.delete(rules[i]);
	    } catch(Exception e)
	    {
	    }
	    finally
	    {
		HibernateUtil.util.save(rules[i]);
	    }
	}
	return true;
    }

    public Boolean updateSmsText(SmsText smstext)
    {
	return HibernateUtil.util.update(smstext);
    }

    public Boolean updateSputumTest(SputumTest sputumTest)
    {
	return HibernateUtil.util.update(sputumTest);
    }

    public Boolean updateReferral(Referral referral)
    {
	return HibernateUtil.util.update(referral);
    }

    public Boolean updateUser(User user, String[] roles)
    {
	// Delete existing roles
	PersonRole[] existingRoles = findPersonRoles(user.getPid());
	for (PersonRole r : existingRoles)
	    HibernateUtil.util.delete(r);
	// Create new roles
	existingRoles = new PersonRole[roles.length];
	for (int i = 0; i < roles.length; i++)
	{
	    PersonRole personRole = new PersonRole(new PersonRoleId(user.getPid(), roles[i]));
	    HibernateUtil.util.save(personRole);
	}
	// Update User
	return HibernateUtil.util.update(user);
    }

    public Boolean updateUserMapping(UserMapping userMapping)
    {
	return HibernateUtil.util.update(userMapping);
    }

    public Boolean updateUserRights(UserRights userRights)
    {
	return HibernateUtil.util.update(userRights);
    }

    public Boolean updateVisit(Visit visit)
    {
	return HibernateUtil.util.update(visit);
    }

    public Boolean undoFormRequest(String userName, String encounterType)
    {
	String maxDate = HibernateUtil.util.selectObject(
	"select max(date_end) from encounter where encounter_type = '" + encounterType + "' and pid2 = (select pid from user where user_name = '" + userName + "')").toString();
	if (maxDate == null)
	    return false;
	String updateQuery = "update encounter set description = 'Marked to Undo on " + DateTimeUtil.getSQLDate(new Date()) + " by " + userName + "' where encounter_type = '" + encounterType
	+ "' and pid2 = (select pid from user where user_name = '" + userName + "') and date_end = '" + maxDate + "'";
	return HibernateUtil.util.runCommand(updateQuery) == 1;
    }

    /* Form submission methods */
    public String saveVisitPurpose(Visit[] visit, Patient patient, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    if (savePatient(patient))
	    {
		for (int i = 0; i < visit.length; i++)
		{
		    saveVisit(visit[i]);//save all visits
		}
		result = saveEncounterWithResults(encounter, encounterResults);
		if (!result.equals("SUCCESS"))
		{
		    deletePatient(patient);
		    if(deleteVisits(visit))
			result="FAIL";
		}
	    }

	} catch(Exception e)
	{
	    e.printStackTrace();
	}
	return result;
    }

    public String saveClientDemographics(Person person, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    boolean res = false;
	    ;
	    // If the person exists, then update, otherwise save
	    Person p = findPerson(person.getPid());
	    if (p == null)
		res = savePerson(person);
	    else
		res = updatePerson(person);
	    if (res)
	    {
		result = saveEncounterWithResults(encounter, encounterResults);
		if (p == null && !result.equals("SUCCESS"))
		    deletePerson(person);
	    }
	    else
		return CustomMessage.getErrorMessage(ErrorType.DUPLICATION_ERROR);
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveTBScreening(Patient patient, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (result.equals("SUCCESS"))
		if (!updatePatient(patient))
		    return CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR);
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveRegistration(Person person, Patient patient, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    // Check if the Client came is a suspect (in case of TB visit)
	    Visit visit = findVisit(patient.getPatientId());
	    if (visit == null)
		return "ERROR! Visit information not found.";
	    else if (visit.getVisitPurpose().equals("TB"))
	    {
		if (!patient.getDiseaseSuspected().equals("TB"))
		    return "ERROR! Client was not marked as TB suspect.";
	    }
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (result.equals("SUCCESS"))
	    {
		if (!updatePerson(person) || !updatePatient(patient))
		    return CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR);
	    }
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveSputumCollection(SputumTest[] smearTests, Patient patient, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    if (!patient.getDiseaseSuspected().equals("TB"))
		return "ERROR! Client was not marked as TB suspect.";
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (result.equals("SUCCESS"))
	    {
		for (SputumTest sputumTest : smearTests)
		    saveSputumTest(sputumTest);
	    }
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveSputumRegistration(SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (result.equals("SUCCESS"))
		updateSputumTest(sputumTest);
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveSmearResult(SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults, boolean enableClientSms)
    {
	String result = "";
	try
	{
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (result.equals("SUCCESS"))
	    {
		updateSputumTest(sputumTest);
		Patient patient = findPatient(sputumTest.getId().getPatientId());
		Visit updateDiseaseConfirm = findParticularVisit(patient.getPatientId(), patient.getDiseaseSuspected());
		// If result is positive, then confirm disease
		if (!sputumTest.getSmearResult().equals("NEGATIVE"))
		{
		    patient.setDiseaseConfirmed(true);
		    patient.setDateConfirmed(encounter.getDateEntered());
		    patient.setConfirmationSource("MICROSCOPY");
		    patient.setConfirmationRemarks(sputumTest.getSmearResult());
		    patient.setPatientStatus("CONFIRMED");
		    updateDiseaseConfirm.setDiseaseConfirmed(true);
		    updatePatient(patient);
		    updateVisit(updateDiseaseConfirm);
		}
		// Send alert to Site
		// If no results are pending or any of the results is Positive,
		// then send alerts
		boolean pending = exists("sputum_test", "patient_id='" + patient.getPatientId() + "' and month = " + sputumTest.getMonth() + " and sample_code is not null and smear_result is null");
		// Disabled: Right now, we wait for all of the tests to complete
		// boolean positive = exists ("sputum_test", "patient_id='" +
		// patient.getPatientId () + "' and month = " +
		// sputumTest.getMonth () +
		// " and smear_result is not null and smear_result <> 'NEGATIVE'");
		if (!pending)
		{
		    try
		    {
			boolean referralTookPlace = exists("encounter", "pid1='" + patient.getPatientId() + "' and encounter_type='REFERRAL'");
			if (!referralTookPlace || enableClientSms)
			{
			    SmsUtil.createSputumResultsClientSms(patient.getPatientId(), encounter.getDateEntered());
			}

			// If Client is of a Govt. site, auto-create a referral
			// form
			Location site = findLocation(patient.getTreatmentSite());
			if (site != null)
			{
			    if (site.getLocationType().equals("GOVT"))
				SmsUtil.createSputumResultsTreatmentSiteSms(patient.getPatientId(), site.getLocationId(), encounter.getDateEntered(), sputumTest.getMonth());
			}
		    } catch(Exception e)
		    {
			e.printStackTrace();
		    }
		}
	    }
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String checkForGeneXpertTest(String patientId)
    {
	String result = "SUCCESS";
	try
	{
	    Patient patient = findPatient(patientId);
	    if (patient == null)
		return "ERROR! Client ID does not exist.";
	    if (patient.getHivStatus() == null)
		return "Client's HIV status is not defined.";
	    if (!patient.getHivStatus().equals("POSITIVE"))
		return "Client's HIV status must be POSITIVE";
	    int negatives = count("sputum_test", "patient_id='" + patientId + "' and sample_code is not null and smear_result = 'NEGATIVE'").intValue();
	    int positives = count("sputum_test", "patient_id='" + patientId + "' and sample_code is not null and smear_result is not null and smear_result <> 'NEGATIVE'").intValue();
	    int pending = count("sputum_test", "patient_id='" + patientId + "' and sample_code is not null and smear_result is null").intValue();
	    int gxps = count("sputum_test", "patient_id='" + patientId + "' and sample_code is not null and gxp_result is not null").intValue();
	    if (pending != 0)
		return "There are " + pending + " smear results pending. Please first enter pending results.";
	    if (positives != 0)
		return "Gene Xpert Test cannot be done on Smear Positive cases.";
	    if (negatives < 2)
		return "There should be at-least 2 NEGATIVE Smear Results for Gene Xpert Test.";
	    if (gxps != 0)
		return "Gene Xpert Test results were already entered for this Client.";
	} catch(Exception e)
	{
	    e.printStackTrace();
	    return CustomMessage.getErrorMessage(ErrorType.UNKNOWN_ERROR);
	}
	return result;
    }

    public String saveGxpResult(SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (result.equals("SUCCESS"))
	    {
		updateSputumTest(sputumTest);
		Patient patient = findPatient(sputumTest.getId().getPatientId());
		Visit visitPurpose = findParticularVisit(patient.getPatientId(), patient.getDiseaseSuspected());
		// If result is positive, then confirm disease
		if (!sputumTest.getGxpResult().equals("MTB+"))
		{
		    patient.setDiseaseConfirmed(true);
		    patient.setDateConfirmed(encounter.getDateEntered());
		    patient.setConfirmationSource("GXP");
		    patient.setConfirmationRemarks(sputumTest.getGxpResult() + ". " + sputumTest.getRifResistance());
		    patient.setPatientStatus("CONFIRMED");
		    visitPurpose.setDiseaseConfirmed(true);
		    updatePatient(patient);
		    updateVisit(visitPurpose);
		}
	    }
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveReferral(Referral referral, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    boolean res = false;
	    // If the referral record exists, then update, otherwise save
	    Referral p = findReferral(referral.getPatientId());
	    if (p == null)
		res = saveReferral(referral);
	    else
		res = updateReferral(referral);
	    if (res)
	    {
		result = saveEncounterWithResults(encounter, encounterResults);
		if (p == null && !result.equals("SUCCESS"))
		    deleteReferral(referral);
		// Create SMS alerts after Referrals
		try
		{
		    SmsUtil.createReferralClientSms(referral.getPatientId(), referral.getReferredTo(), referral.getDateReferred());
		    SmsUtil.createReferralTreatmentSiteSms(referral.getPatientId(), referral.getReferredTo(), referral.getDateReferred(), 0);
		} catch(Exception e)
		{
		    e.printStackTrace();
		}
		return result;
	    }
	    return CustomMessage.getErrorMessage(ErrorType.DUPLICATION_ERROR);
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveMCReferral(Referral referral, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    boolean res = false;
	    // If the person exists, then update, otherwise save
	    Referral p = findReferral(referral.getPatientId());
	    if (p == null)
		res = saveReferral(referral);
	    else
		res = updateReferral(referral);
	    if (res)
	    {
		result = saveEncounterWithResults(encounter, encounterResults);
		if (p == null && !result.equals("SUCCESS"))
		    deleteReferral(referral);
		return result;
	    }
	    return CustomMessage.getErrorMessage(ErrorType.DUPLICATION_ERROR);
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveOperationNotes(Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (!result.equals("SUCCESS"))
		return CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR);
	    // Create SMS reminders for 2nd day
	    try
	    {
		SmsUtil.createMCDay2ClientSms(encounter.getId().getPid1(), encounter.getDateEntered());
	    } catch(Exception e)
	    {
		e.printStackTrace();
	    }
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveVisitNotes(Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (!result.equals("SUCCESS"))
		return CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR);
	    // Create SMS reminders for 7th or 42nd day
	    try
	    {
		// Get visit type, i.e. day of visit after MC
		String day = searchValueInEncounterResults(encounterResults, "VISIT_TYPE");
		if (day.equals("2"))
		    SmsUtil.createMCDay7ClientSms(encounter.getId().getPid1(), encounter.getDateEntered());
		else if (day.equals("7"))
		    SmsUtil.createMCDay42ClientSms(encounter.getId().getPid1(), encounter.getDateEntered());
	    } catch(Exception e)
	    {
		e.printStackTrace();
	    }
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String saveVisitIndication(Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (!result.equals("SUCCESS"))
		return CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR);
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }

    public String savePatientEdit(String query, Encounter encounter, EncounterResults[] encounterResults)
    {
	String result = "";
	try
	{
	    result = saveEncounterWithResults(encounter, encounterResults);
	    if (!result.equals("SUCCESS"))
		return CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR);
	    execute(query);
	} catch(Exception e)
	{
	    e.printStackTrace();
	    try
	    {
		deleteEncounter(encounter);
	    } catch(Exception e1)
	    {
	    }
	}
	return result;
    }


    public String findDiseaseConfirmed(String patientId)
    {
	String[] columns={"visit_purpose","disease_confirmed"};
	String[][] getDiseaseConfirmed=getTableData("visit",columns,"patient_id='"+patientId+"'");
	String result="";
	for(int i=0;i<getDiseaseConfirmed.length;i++)
	    result=result+getDiseaseConfirmed[i][0]+":"+getDiseaseConfirmed[i][1];
	
	if(result.equals(""))
	    result="Pending";
	return result;
    }

}
