/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package org.irdresearch.irzimbabwe.server.sms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.irdresearch.irzimbabwe.server.ServerServiceImpl;
import org.irdresearch.irzimbabwe.server.util.HibernateUtil;
import org.irdresearch.irzimbabwe.shared.SmsRuleParam;
import org.irdresearch.irzimbabwe.shared.SmsStatus;
import org.irdresearch.irzimbabwe.shared.model.Person;
import org.irdresearch.irzimbabwe.shared.model.SmsLog;
import org.irdresearch.irzimbabwe.shared.model.SmsRule;

import com.mysql.jdbc.StringUtils;

public class SmsUtil {
	private static Random rnd=new Random();
	private static ServerServiceImpl ssl = new ServerServiceImpl();

	public static int getRandomNumber(int range) {
		int num=rnd.nextInt(range);
		return num+1;
	}
	
	//find sms on the basis of rule id and language id.Preferred Language of a patient exists in Person class Default is English 
	public static String findText (String smsRuleId,String langId){
		String text = "Please don`t forget to visit your clinic!";
		if( 0 == langId.compareTo("SHN"))
		{
		
			text = "Rukumbiro usa kanganwa ku shanyira kiriniki yako!";
		}
		else if( 0 ==langId.compareTo("NDB"))
		{
			text = "NDEBELE :Please don`t forget to visit your clinic!";
		}	
			
		List<Object> rt=new ArrayList<Object>();

		rt.addAll(Arrays.asList(HibernateUtil.util.findObjects("SELECT text FROM SmsText WHERE ruleId='" + smsRuleId+"' and language_id='"+langId+"'")));

		if (rt.size() > 0) {
			int num = 1;
			if (rt.size() > 1) {
				num = getRandomNumber(rt.size());
			}
			text = (String) rt.get(num - 1);
		}
		
		return text;
	}
	
	public static void createSputumResultsTreatmentSiteSms(String clientId, String siteId, Date triggerDate, int treatmentMonth) throws ParseException{
		SmsRule rule = ssl.findSmsRule(SmsRuleParam.SmsRule_Site_TB_Sputum_Results);
		Person pers=ssl.findPerson(clientId);
		String langId=pers.getPreferredLanguage ();
		if(rule.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule.getRuleId(),langId), clientId, treatmentMonth );
			createSms(rule, siteId, SmsRuleParam.ReferenceTableTreatmentSiteSms, SmsRuleParam.ReferenceColumnTreatmentSiteSms, triggerDate, text);
		}
	}
	
	public static void createSputumResultsClientSms(String clientId, Date triggerDate) throws ParseException{
		SmsRule rule = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_TB_Sputum_Results);
		Person pers=ssl.findPerson(clientId);
		String langId=pers.getPreferredLanguage();
		if(rule.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule.getRuleId(),langId), clientId, null );
			createSms(rule, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
	}
	
	public static void createReferralTreatmentSiteSms(String clientId, String siteId, Date triggerDate, Integer month) throws ParseException{
		SmsRule rule = ssl.findSmsRule(SmsRuleParam.SmsRule_Site_Referral);
		Person pers=ssl.findPerson(clientId);
		String langId=pers.getPreferredLanguage();
		if(rule.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule.getRuleId(),langId), clientId, month );
			createSms(rule, siteId, SmsRuleParam.ReferenceTableTreatmentSiteSms, SmsRuleParam.ReferenceColumnTreatmentSiteSms, triggerDate, text);
		}
	}
	
	public static void createReferralClientSms(String clientId, String siteId, Date triggerDate) throws ParseException
	{
		SmsRule rule1 = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_Referral_W1);
		SmsRule rule2 = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_Referral_W2);
		SmsRule rule3 = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_Referral_W3);
		SmsRule rule4 = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_Referral_W4);
		SmsRule rule5 = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_Referral_M2);
		SmsRule rule6 = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_Referral_M3);
		SmsRule rule7 = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_Referral_M4_plus);
		
		Person pers=ssl.findPerson(clientId);
		String langId=pers.getPreferredLanguage();
		
		if(rule1.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule1.getRuleId(),langId), clientId, null );
			createSms(rule1, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
		if(rule2.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule2.getRuleId(),langId), clientId, null );
			createSms(rule2, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
		if(rule3.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule3.getRuleId(),langId), clientId, null );
			createSms(rule3, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
		if(rule4.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule4.getRuleId(),langId), clientId, null );
			createSms(rule4, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
		if(rule5.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule5.getRuleId(),langId), clientId, null );
			createSms(rule5, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
		if(rule6.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule6.getRuleId(),langId), clientId, null );
			createSms(rule6, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
		if(rule7.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule7.getRuleId(),langId), clientId, null );
			createSms(rule7, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
	}
	
	public static void createMCDay2ClientSms(String clientId, Date triggerDate) throws ParseException{
		SmsRule rule = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_MC_Day2);
		Person pers=ssl.findPerson(clientId);
		String langId=pers.getPreferredLanguage();
		
		if(rule.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule.getRuleId(),langId), clientId, null );
			createSms(rule, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
	}
	
	public static void createMCDay7ClientSms(String clientId, Date triggerDate) throws ParseException{
		SmsRule rule = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_MC_Day7);
		Person pers=ssl.findPerson(clientId);
		String langId=pers.getPreferredLanguage();
		
		if(rule.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule.getRuleId(),langId), clientId, null );
			createSms(rule, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
	}
	
	public static void createMCDay42ClientSms(String clientId, Date triggerDate) throws ParseException{
		SmsRule rule = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_MC_Day42);
		Person pers=ssl.findPerson(clientId);
		String langId=pers.getPreferredLanguage();
		
		if(rule.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule.getRuleId(),langId), clientId, null );
			createSms(rule, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
	}
	
	public static void createMCCustomClientSms(String clientId, Date triggerDate) throws ParseException{
		SmsRule rule = ssl.findSmsRule(SmsRuleParam.SmsRule_Client_MC_Custom);
		Person pers=ssl.findPerson(clientId);
		String langId=pers.getPreferredLanguage();
		
		if(rule.getIsEnabled()){
			String text = insertClientInfoInSmsText( findText(rule.getRuleId(),langId), clientId, null );
			createSms(rule, clientId, SmsRuleParam.ReferenceTableClientSms, SmsRuleParam.ReferenceColumnClientSms, triggerDate, text);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static void createSms(SmsRule rule, String recipientId, String recipientReferenceTable, String recipientReferenceColumn, Date triggerDate,String text) throws ParseException{
		
		Object phone = HibernateUtil.util.selectObject("SELECT mobile FROM "+recipientReferenceTable+" WHERE "+recipientReferenceColumn+"='"+recipientId+"'");
		if(phone != null && !StringUtils.isEmptyOrWhitespaceOnly(phone.toString())){
			String time = "09:00:00";
			try{
				time = HibernateUtil.util.selectObject("SELECT default_definition_key FROM defaults WHERE definition_type = 'SMS_TIME'").toString();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			Date date = sdf.parse(time);
			
			Calendar startDate = Calendar.getInstance();
			startDate.setTime(triggerDate);
			startDate.set(Calendar.HOUR_OF_DAY, date.getHours());
			startDate.set(Calendar.MINUTE, date.getMinutes());
			startDate.set(Calendar.SECOND, date.getSeconds());
			startDate.add(Calendar.DATE, rule.getStartDay());
	
			// looping would add interval repeatedly to make all reminders with respective gaps
			Calendar duedate = Calendar.getInstance();
			duedate.setTime(startDate.getTime());
			for (int i = 0; i < rule.getIterations(); i++) {
				
				if(i != 0)//If first iteration duedate should be start-day without any interval gap
					duedate.add(Calendar.DATE, rule.getSmsInterval());
	
				SmsLog log = new SmsLog();
				log.setDateCreated(new Date());
				log.setDescription(null);
				log.setDateDue(duedate.getTime());
				log.setRecipientId(recipientId);
				log.setRecipientReferenceColumn(recipientReferenceColumn);
				log.setRecipientReferenceTable(recipientReferenceTable);
				log.setRuleId(rule.getRuleId());
				log.setStatus(SmsStatus.PENDING.name());
				log.setText(text);
				
				ssl.saveSmsLog(log);
			}
		}
		else{
			System.out.println("No Cell Number Found for "+recipientId);			
		}
	}
	
/*	public static String insertOperationInfoInSmsText(String text, String clientId, int dayNum){
		if(text.contains("[[MCNextAppointmentDate]]")){
			String qryMc = "SELECT date_entered FROM encounter WHERE encounter_type='MC_OPERAT' AND pid1='"+clientId+"'";
			Date NextAppDate = HibernateUtil.util.selectObject(qryMc);
		}
	}*/
	
	public static String insertClientInfoInSmsText(String text, String clientId, Integer treatmentMonth){
		String qry = "SELECT " +
				" r.referred_to , " + 
				" p.first_name , " +
				" p.last_name , " +
				" p.approximate_age, " +
				" (SELECT CAST(GROUP_CONCAT(month, ' ', date_smear_tested, ' ', smear_result, '; ') AS CHAR(300)) FROM sputum_test WHERE patient_id=p.pid AND month="+treatmentMonth+")," +
				" CONCAT(CASE WHEN p.phone IS NULL THEN '' ELSE CONCAT(p.phone,'; ') END,CASE WHEN p.mobile IS NULL THEN '' ELSE CONCAT(p.mobile,'; ') END)," +
				" CONCAT(IFNULL(p.address1,''),' ',IFNULL(p.address2,''),' ',IFNULL(p.address3,''),' ',IFNULL(p.address4,''),' ',IFNULL(p.city,'')) " + 
					" FROM person p LEFT JOIN referral r ON r.patient_id=p.pid " +
					" WHERE p.pid='"+clientId+"'";
		System.out.println(qry);
		Object[] infoArr = (Object[]) HibernateUtil.util.selectObject(qry);
		
		if(text.contains("[[ClientID]]")){
			text = text.replace("[[ClientID]]", clientId);
		}
		if(text.contains("[[TBReferralSiteID]]")){
			text = text.replace("[[TBReferralSiteID]]", (infoArr[0]==null?"":infoArr[0].toString()));
		}
		if(text.contains("[[ClientFirstName]]")){
			text = text.replace("[[ClientFirstName]]", (infoArr[1]==null?"":infoArr[1].toString()));
		}
		if(text.contains("[[ClientLastName]]")){
			text = text.replace("[[ClientLastName]]", (infoArr[2]==null?"":infoArr[2].toString()));
		}
		if(text.contains("[[ClientAge]]")){
			text = text.replace("[[ClientAge]]", (infoArr[3]==null?"":infoArr[3].toString()));
		}
		if(text.contains("[[SmearTestSiteID]]")){
			Object smearTestSiteID = null;
			try{
				smearTestSiteID = HibernateUtil.util.selectObject("SELECT location_id FROM encounter WHERE pid1='"+clientId+"' AND encounter_type = 'SMEAR_RES' ORDER BY date_end DESC");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			text = text.replace("[[SmearTestSiteID]]", (smearTestSiteID==null?"*not found*":smearTestSiteID.toString()));
		}
		if(text.contains("[[SmearResults]]")){
			text = text.replace("[[SmearResults]]", (infoArr[4]==null?"":infoArr[4].toString()));
		}
		if(text.contains("[[ClientContactNumbers]]")){
			text = text.replace("[[ClientContactNumbers]]", (infoArr[5]==null?"":infoArr[5].toString()));
		}
		if(text.contains("[[ClientAddress]]")){
			text = text.replace("[[ClientAddress]]", (infoArr[6]==null?"":infoArr[6].toString()));
		}
		
		return text;
	}
}
