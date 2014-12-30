
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.Parameter;
import org.irdresearch.irzimbabwe.shared.model.*;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author owais.hussain@irdresearch.org
 */
@RemoteServiceRelativePath("greet")
public interface ServerService extends RemoteService
{
	/* Authentication methods */
	User authenticate (String userName, String password) throws Exception;

	Boolean authenticateUser (String text) throws Exception;

	/* Delete methods */
	Boolean deleteDefinition (Definition definition) throws Exception;

	Boolean deleteEncounter (Encounter encounter) throws Exception;

	Boolean deleteEncounterElement (EncounterElement encounterElement) throws Exception;

	Boolean deleteEncounterPrerequisite (EncounterPrerequisite encounterPrerequisite) throws Exception;

	Boolean deleteEncounterResults (EncounterResults encounterResults) throws Exception;

	Boolean deleteEncounterType (EncounterType encounterType) throws Exception;

	Boolean deleteFeedback (Feedback feedback) throws Exception;

	Boolean deleteLocation (Location location) throws Exception;

	Boolean deletePatient (Patient patient) throws Exception;

	Boolean deletePerson (Person person) throws Exception;

	Boolean deletePersonRole (PersonRole personRole) throws Exception;

	Boolean deleteUser (User user) throws Exception;

	Boolean deleteReferral (Referral referral) throws Exception;

	Boolean deleteSmsText (SmsText smstext) throws Exception;

	Boolean deleteSputumTest (SputumTest sputumTest) throws Exception;

	Boolean deleteUserMapping (UserMapping userMapping) throws Exception;

	Boolean deleteUserRights (UserRights userRights) throws Exception;

	Boolean deleteVisit (Visit visit) throws Exception;

	/* Find methods */
	Definition findDefinition (String definitionType, String definitionKey) throws Exception;

	Encounter findEncounter (EncounterId encounterID) throws Exception;

	EncounterElement findEncounterElement (String encounterType, String element) throws Exception;

	EncounterElement[] findEncounterElements (String encounterType) throws Exception;

	EncounterResults[] findEncounterResults (EncounterId encounterId) throws Exception;

	EncounterResults findEncounterResultsByElement (EncounterResultsId encounterResultsID) throws Exception;

	EncounterPrerequisite findEncounterPrerequisite (EncounterPrerequisiteId encounterPrerequisiteId) throws Exception;

	EncounterPrerequisite[] findEncounterPrerequisites (EncounterType encounterType) throws Exception;

	EncounterType findEncounterType (String encounterType) throws Exception;

	Location findLocation (String locationID) throws Exception;

	Location[] findLocationsByType (String locationType) throws Exception;

	Patient findPatient (String patientID) throws Exception;

	Patient findPatientByMR (String Mrno) throws Exception;

	Person findPerson (String pid) throws Exception;

	PersonRole[] findPersonRoles (String pid) throws Exception;

	Person[] findPersonsByName (String firstName, String lastName) throws Exception;

	Person findPersonsByNIC (String NIC) throws Exception;

	Referral findReferral (String patientId) throws Exception;

	SmsRule findSmsRule (String smsRuleId) throws Exception;

	SmsRule[] findSmsRules () throws Exception;

	SmsText findSmsText (String ruleId, String textId) throws Exception;

	SmsText[] findSmsTextByRule (String ruleId) throws Exception;

	SmsText findSmsTextByLanguage (String ruleId, String preferredLanguageId) throws Exception;

	SputumTest findSputumTest (String patientId, Integer sampleNo) throws Exception;

	User findUser (String currentUserName) throws Exception;

	UserMapping findUserMapping (UserMappingId userMappingId) throws Exception;

	UserMapping[] findUserMappingsByUser (String userId) throws Exception;

	UserRights findUserRights (String roleName, String menuName) throws Exception;

	Visit findVisit (String patientId) throws Exception;

	/* Save methods */
	Boolean saveDefinition (Definition definition) throws Exception;

	// Boolean saveEncounter (Encounter encounter) throws Exception;

	Boolean saveEncounterElement (EncounterElement encounterElement) throws Exception;

	Boolean saveEncounterPrerequisite (EncounterPrerequisite encounterPrerequisite) throws Exception;

	// Boolean saveEncounterResults (EncounterResults encounterResults) throws
	// Exception;

	Boolean saveEncounterType (EncounterType encounterType) throws Exception;

	String saveEncounterWithResults (Encounter encounter, EncounterResults[] encounterResults);

	Boolean saveFeedback (Feedback feedback) throws Exception;

	Boolean saveLocation (Location location) throws Exception;

	Boolean savePatient (Patient patient) throws Exception;

	Boolean savePerson (Person person) throws Exception;

	Boolean savePersonRole (PersonRole personRole) throws Exception;

	Boolean saveReferral (Referral referral) throws Exception;

	Boolean saveSmsRule (SmsRule sms) throws Exception;

	Boolean saveSmsText (SmsText smstext) throws Exception;

	Boolean saveSputumTest (SputumTest sputumTest) throws Exception;

	Boolean saveUser (User user, String[] currentRoles) throws Exception;

	Boolean saveUserMapping (UserMapping userMapping) throws Exception;

	Boolean saveUserRights (UserRights userRights) throws Exception;

	Boolean saveVisit (Visit visit) throws Exception;

	/* Update methods */
	Boolean updateDefaults (Defaults[] defaults) throws Exception;

	Boolean updateDefinition (Definition definition) throws Exception;

	Boolean updateEncounter (Encounter encounter) throws Exception;

	Boolean updateEncounterElement (EncounterElement element) throws Exception;

	Boolean updateEncounterPrerequisite (EncounterPrerequisite prerequisite) throws Exception;

	Boolean updateEncounterResults (EncounterResults encounterResults) throws Exception;

	Boolean updateEncounterType (EncounterType encounterType) throws Exception;

	Boolean updateEncounterWithResults (Encounter encounter, EncounterResults[] encounterResults) throws Exception;

	Boolean updateFeedback (Feedback feedback) throws Exception;

	Boolean updateLocation (Location location) throws Exception;

	Boolean updatePassword (String userName, String newPassword) throws Exception;

	Boolean updatePatient (Patient patient) throws Exception;

	Boolean updatePerson (Person person) throws Exception;

	Boolean updatePersonRole (PersonRole personRole) throws Exception;

	Boolean updateReferral (Referral referral) throws Exception;

	Boolean updateSmsRule (SmsRule rule) throws Exception;

	Boolean updateSmsRules (SmsRule[] rules) throws Exception;

	Boolean updateSmsText (SmsText smstext) throws Exception;

	Boolean updateSputumTest (SputumTest sputumTest) throws Exception;

	Boolean updateUser (User user, String[] currentRoles) throws Exception;

	Boolean updateUserMapping (UserMapping userMapping) throws Exception;

	Boolean updateUserRights (UserRights userRights) throws Exception;

	Boolean updateVisit (Visit visit) throws Exception;

	/* Other methods */
	Long count (String tableName, String filter) throws Exception;

	Boolean verifySecretAnswer (String userName, String secretAnswer) throws Exception;

	String generateCSVfromQuery (String database, String query) throws Exception;

	String generateReport (String fileName, Parameter[] params, boolean export) throws Exception;

	String generateReportFromQuery (String database, String reportName, String query, Boolean export) throws Exception;

	String[] getColumnData (String tableName, String columnName, String filter) throws Exception;

	String getCurrentUserName () throws Exception;

	String[][] getSchema () throws Exception;

	Definition[] getDefinitions () throws Exception;

	Defaults[] getDefaults () throws Exception;

	String[] getDumpFiles () throws Exception;

	String[][] getReportsList () throws Exception;

	String[] getRowRecord (String tableName, String[] columnNames, String filter) throws Exception;

	String getObject (String tableName, String columnName, String filter) throws Exception;

	String[] getQueriesResults (String[] queries) throws Exception;

	String getSecretQuestion (String userName) throws Exception;

	String getSnapshotTime () throws Exception;

	String[][] getTableData (String tableName, String[] columnNames, String filter) throws Exception;

	String[][] getTableData (String sqlQuery) throws Exception;

	Boolean[] getUserRgihts (String userName, String role, String menuName) throws Exception;

	Boolean exists (String tableName, String filter) throws Exception;

	int execute (String query) throws Exception;

	Boolean execute (String[] queries) throws Exception;

	Boolean executeProcedure (String procedure) throws Exception;

	void recordLogin (String userName) throws Exception;

	void recordLogout (String userName) throws Exception;

	void setCurrentUser (String userName, String role) throws Exception;

	/* Form submission methods */
	String saveVisitPurpose (Visit visit, Patient patient, Encounter encounter, EncounterResults[] array);

	String saveClientDemographics (Person person, Encounter encounter, EncounterResults[] encounterResults);

	String saveTBScreening (Patient patient, Encounter encounter, EncounterResults[] encounterResults);

	String saveRegistration (Person person, Patient patient, Encounter encounter, EncounterResults[] encounterResults);

	String saveSputumCollection (SputumTest[] smearTests, Patient patient, Encounter encounter, EncounterResults[] encounterResults);

	String saveSputumRegistration (SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults);

	String saveSmearResult (SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults, boolean enableClientSms);

	String checkForGeneXpertTest (String patientId);

	String saveGxpResult (SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults);

	String saveReferral (Referral referral, Encounter encounter, EncounterResults[] encounterResults);

	String saveMCReferral (Referral referral, Encounter encounter, EncounterResults[] encounterResults);

	String saveOperationNotes (Encounter encounter, EncounterResults[] encounterResults);

	String saveVisitNotes (Encounter encounter, EncounterResults[] encounterResults);

	String saveVisitIndication (Encounter encounter, EncounterResults[] encounterResults);

	String savePatientEdit (String query, Encounter encounter, EncounterResults[] encounterResults);
}