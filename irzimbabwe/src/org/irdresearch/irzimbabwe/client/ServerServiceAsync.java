
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.Parameter;
import org.irdresearch.irzimbabwe.shared.model.*;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Async counterpart of <code>Service</code>.
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public interface ServerServiceAsync
{
	/* Authentication methods */
	void authenticate (String userName, String password, AsyncCallback<User> callback) throws Exception;

	void authenticateUser (String text, AsyncCallback<Boolean> callback) throws Exception;

	void verifySecretAnswer (String userName, String secretAnswer, AsyncCallback<Boolean> callback) throws Exception;

	/* Delete methods */
	void deleteDefinition (Definition definition, AsyncCallback<Boolean> callback) throws Exception;

	void deleteEncounter (Encounter encounter, AsyncCallback<Boolean> callback) throws Exception;

	void deleteEncounterElement (EncounterElement currentElement, AsyncCallback<Boolean> callback) throws Exception;

	void deleteEncounterPrerequisite (EncounterPrerequisite encounterPrerequisite, AsyncCallback<Boolean> callback) throws Exception;

	void deleteEncounterResults (EncounterResults encounterResults, AsyncCallback<Boolean> callback) throws Exception;

	void deleteEncounterType (EncounterType encounterType, AsyncCallback<Boolean> callback) throws Exception;

	void deleteFeedback (Feedback feedback, AsyncCallback<Boolean> callback) throws Exception;

	void deleteLocation (Location location, AsyncCallback<Boolean> callback) throws Exception;

	void deletePatient (Patient patient, AsyncCallback<Boolean> callback) throws Exception;

	void deletePerson (Person person, AsyncCallback<Boolean> callback) throws Exception;

	void deletePersonRole (PersonRole personRole, AsyncCallback<Boolean> callback) throws Exception;

	void deleteReferral (Referral referral, AsyncCallback<Boolean> callback) throws Exception;

	void deleteSmsText (SmsText smstext, AsyncCallback<Boolean> callback) throws Exception;

	void deleteSputumTest (SputumTest sputumTest, AsyncCallback<Boolean> callback) throws Exception;

	void deleteUser (User user, AsyncCallback<Boolean> callback) throws Exception;

	void deleteUserMapping (UserMapping userMapping, AsyncCallback<Boolean> callback) throws Exception;

	void deleteUserRights (UserRights userRights, AsyncCallback<Boolean> callback) throws Exception;

	void deleteVisit (Visit visit, AsyncCallback<Boolean> callback) throws Exception;
	void deleteVisits(Visit[] visit, AsyncCallback<Boolean> callback) throws Exception;

	/* Find methods */
	void findDefinition (String definitionType, String definitionKey, AsyncCallback<Definition> callback) throws Exception;

	void findEncounter (EncounterId encounterID, AsyncCallback<Encounter> callback) throws Exception;

	void findEncounterElement (String encounterType, String element, AsyncCallback<EncounterElement> callback) throws Exception;

	void findEncounterElements (String encounterType, AsyncCallback<EncounterElement[]> asyncCallback) throws Exception;

	void findEncounterPrerequisite (EncounterPrerequisiteId encounterPrerequisiteId, AsyncCallback<EncounterPrerequisite> asyncCallback) throws Exception;

	void findEncounterPrerequisites (EncounterType encounterType, AsyncCallback<EncounterPrerequisite[]> callback) throws Exception;

	void findEncounterResults (EncounterId encounterId, AsyncCallback<EncounterResults[]> callback) throws Exception;

	void findEncounterResultsByElement (EncounterResultsId encounterResultsID, AsyncCallback<EncounterResults> callback) throws Exception;

	void findEncounterType (String encounterType, AsyncCallback<EncounterType> callback) throws Exception;

	void findLocation (String locationID, AsyncCallback<Location> callback) throws Exception;

	void findLocationsByType (String locationType, AsyncCallback<Location[]> asyncCallback) throws Exception;

	void findPatient (String patientID, AsyncCallback<Patient> callback) throws Exception;

	void findPatientByMR (String Mrno, AsyncCallback<Patient> callback) throws Exception;

	void findPerson (String pid, AsyncCallback<Person> callback) throws Exception;

	void findPersonRoles (String pid, AsyncCallback<PersonRole[]> callback) throws Exception;

	void findPersonsByName (String firstName, String lastName, AsyncCallback<Person[]> callback) throws Exception;

	void findPersonsByNIC (String NIC, AsyncCallback<Person> callback) throws Exception;
	
	void findDiseaseConfirmed(String patientId, AsyncCallback<String> callback) throws Exception;

	void findReferral (String patientId, AsyncCallback<Referral> callback) throws Exception;

	void findSmsRule (String smsRuleId, AsyncCallback<SmsRule> callback) throws Exception;

	void findSmsRules (AsyncCallback<SmsRule[]> callback) throws Exception;
	
	void findSmsText (String ruleId, String textId, AsyncCallback<SmsText> callback) throws Exception;

	void findSmsTextByRule (String ruleId, AsyncCallback<SmsText[]> callback);

	void findSmsTextByLanguage (String ruleId, String preferredLanguageId, AsyncCallback<SmsText> callback) throws Exception;

	void findSputumTest (String patientId, Integer sampleNo, AsyncCallback<SputumTest> callback) throws Exception;

	void findUser (String currentUserName, AsyncCallback<User> callback) throws Exception;

	void findUserMapping (UserMappingId userMappingId, AsyncCallback<UserMapping> asyncCallback) throws Exception;

	void findUserMappingsByUser (String userId, AsyncCallback<UserMapping[]> callback) throws Exception;

	void findUserRights (String roleName, String menuName, AsyncCallback<UserRights> callback) throws Exception;

	void findParticularVisit (String patientId, String visitPurpose ,AsyncCallback<Visit> callback) throws Exception;
	
	void findVisit(String patientId, AsyncCallback<Visit> callback);
	
	void findAllVisits(String patient_id,AsyncCallback<Visit[]> callback) throws Exception;

	/* Save methods */
	void saveDefinition (Definition definition, AsyncCallback<Boolean> callback) throws Exception;

	// void saveEncounter (Encounter encounter, AsyncCallback<Boolean> callback)
	// throws Exception;

	void saveEncounterElement (EncounterElement currentElement, AsyncCallback<Boolean> callback) throws Exception;

	void saveEncounterPrerequisite (EncounterPrerequisite encounterPrerequisite, AsyncCallback<Boolean> callback) throws Exception;

	// void saveEncounterResults (EncounterResults encounterResults,
	// AsyncCallback<Boolean> callback) throws Exception;

	void saveEncounterType (EncounterType encounterType, AsyncCallback<Boolean> callback) throws Exception;

	void saveEncounterWithResults (Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> asyncCallback);

	void saveFeedback (Feedback feedback, AsyncCallback<Boolean> callback) throws Exception;

	void saveLocation (Location location, AsyncCallback<Boolean> callback) throws Exception;

	void savePatient (Patient patient, AsyncCallback<Boolean> callback) throws Exception;

	void savePerson (Person person, AsyncCallback<Boolean> callback) throws Exception;

	void savePersonRole (PersonRole personRole, AsyncCallback<Boolean> callback) throws Exception;

	void saveReferral (Referral referral, AsyncCallback<Boolean> callback) throws Exception;

	void saveSmsRule (SmsRule sms, AsyncCallback<Boolean> callback) throws Exception;

	void saveSmsText (SmsText smstext, AsyncCallback<Boolean> callback) throws Exception;

	void saveSputumTest (SputumTest sputumTest, AsyncCallback<Boolean> callback) throws Exception;

	void saveUser (User user, String[] currentRoles, AsyncCallback<Boolean> callback) throws Exception;

	void saveUserMapping (UserMapping userMapping, AsyncCallback<Boolean> callback) throws Exception;

	void saveUserRights (UserRights userRights, AsyncCallback<Boolean> callback) throws Exception;

	void saveVisit (Visit visit, AsyncCallback<Boolean> callback) throws Exception;

	/* Update methods */
	void updateDefaults (Defaults[] defaults, AsyncCallback<Boolean> callback) throws Exception;

	void updateDefinition (Definition definition, AsyncCallback<Boolean> callback) throws Exception;

	void updateEncounter (Encounter encounter, AsyncCallback<Boolean> callback) throws Exception;

	void updateEncounterElement (EncounterElement element, AsyncCallback<Boolean> callback) throws Exception;

	void updateEncounterPrerequisite (EncounterPrerequisite prerequisite, AsyncCallback<Boolean> callback) throws Exception;

	void updateEncounterResults (EncounterResults encounterResults, AsyncCallback<Boolean> callback) throws Exception;

	void updateEncounterType (EncounterType encounterType, AsyncCallback<Boolean> callback) throws Exception;

	void updateEncounterWithResults (Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateFeedback (Feedback feedback, AsyncCallback<Boolean> callback) throws Exception;

	void updateLocation (Location location, AsyncCallback<Boolean> callback) throws Exception;

	void updatePassword (String userName, String newPassword, AsyncCallback<Boolean> callback) throws Exception;

	void updatePatient (Patient patient, AsyncCallback<Boolean> callback) throws Exception;

	void updatePerson (Person person, AsyncCallback<Boolean> callback) throws Exception;

	void updatePersonRole (PersonRole personRole, AsyncCallback<Boolean> callback) throws Exception;

	void updateReferral (Referral referral, AsyncCallback<Boolean> callback) throws Exception;

	void updateSmsText (SmsText smstext, AsyncCallback<Boolean> callback) throws Exception;

	void updateSmsRule (SmsRule sms, AsyncCallback<Boolean> callback) throws Exception;

	void updateSmsRules (SmsRule[] rules, AsyncCallback<Boolean> callback) throws Exception;

	void updateSputumTest (SputumTest sputumTest, AsyncCallback<Boolean> callback) throws Exception;

	void updateUser (User user, String[] currentRoles, AsyncCallback<Boolean> callback) throws Exception;

	void updateUserMapping (UserMapping userMapping, AsyncCallback<Boolean> callback) throws Exception;

	void updateUserRights (UserRights userRights, AsyncCallback<Boolean> callback) throws Exception;

	void updateVisit (Visit visit, AsyncCallback<Boolean> callback) throws Exception;

	/* Other methods */
	void count (String tableName, String filter, AsyncCallback<Long> callback) throws Exception;

	void exists (String tableName, String filer, AsyncCallback<Boolean> callback) throws Exception;

	void generateCSVfromQuery (String database, String query, AsyncCallback<String> callback) throws Exception;

	void generateReport (String fileName, Parameter[] params, boolean export, AsyncCallback<String> callback) throws Exception;

	void generateReportFromQuery (String database, String reportName, String query, Boolean export, AsyncCallback<String> callback) throws Exception;

	void getColumnData (String tableName, String columnName, String filter, AsyncCallback<String[]> callback) throws Exception;

	void getCurrentUserName (AsyncCallback<String> callback) throws Exception;

	void getSchema (AsyncCallback<String[][]> callback) throws Exception;

	void getDefaults (AsyncCallback<Defaults[]> callback) throws Exception;

	void getDefinitions (AsyncCallback<Definition[]> callback) throws Exception;

	void getDumpFiles (AsyncCallback<String[]> callback) throws Exception;

	void getReportsList (AsyncCallback<String[][]> callback) throws Exception;

	void getRowRecord (String tableName, String[] columnNames, String filter, AsyncCallback<String[]> callback) throws Exception;

	void getObject (String tableName, String columnName, String filter, AsyncCallback<String> callback) throws Exception;

	void getQueriesResults (String[] queries, AsyncCallback<String[]> callback) throws Exception;

	void getSecretQuestion (String userName, AsyncCallback<String> callback) throws Exception;

	void getSnapshotTime (AsyncCallback<String> callback) throws Exception;

	void getTableData (String tableName, String[] columnNames, String filter, AsyncCallback<String[][]> callback) throws Exception;

	void getTableData (String sqlQuery, AsyncCallback<String[][]> callback) throws Exception;

	void getUserRgihts (String userName, String role, String menuName, AsyncCallback<Boolean[]> callback) throws Exception;

	void execute (String query, AsyncCallback<Integer> callback) throws Exception;

	void execute (String[] queries, AsyncCallback<Boolean> callback) throws Exception;

	void executeProcedure (String procedure, AsyncCallback<Boolean> callback) throws Exception;

	void recordLogin (String userName, AsyncCallback<Void> callback) throws Exception;

	void recordLogout (String userName, AsyncCallback<Void> callback) throws Exception;

	void setCurrentUser (String userName, String role, AsyncCallback<Void> callback) throws Exception;

	/* Form submission methods */
	void saveVisitPurpose (Visit[] visits, Patient patient, Encounter encounter, EncounterResults[] array, AsyncCallback<String> asyncCallback);

	void saveClientDemographics (Person person, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveTBScreening (Patient patient, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveRegistration (Person person, Patient patient, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveSputumCollection (SputumTest[] smearTests, Patient patient, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveSputumRegistration (SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveSmearResult (SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults,boolean enableClientSms, AsyncCallback<String> callback);

	void checkForGeneXpertTest (String patientId, AsyncCallback<String> callback);

	void saveGxpResult (SputumTest sputumTest, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveReferral (Referral referral, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveMCReferral (Referral referral, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveOperationNotes (Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveVisitNotes (Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void saveVisitIndication (Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);

	void savePatientEdit (String query, Encounter encounter, EncounterResults[] encounterResults, AsyncCallback<String> callback);
}