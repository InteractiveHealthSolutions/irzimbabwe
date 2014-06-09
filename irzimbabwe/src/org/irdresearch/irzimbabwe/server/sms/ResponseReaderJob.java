package org.irdresearch.irzimbabwe.server.sms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.irdresearch.irzimbabwe.server.ServerServiceImpl;
import org.irdresearch.irzimbabwe.server.util.HibernateUtil;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.RegexUtil;
import org.irdresearch.irzimbabwe.shared.ResponseType;
import org.irdresearch.irzimbabwe.shared.SmsRuleParam;
import org.irdresearch.irzimbabwe.shared.SmsStatus;
import org.irdresearch.irzimbabwe.shared.model.Encounter;
import org.irdresearch.irzimbabwe.shared.model.EncounterId;
import org.irdresearch.irzimbabwe.shared.model.EncounterResults;
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
import org.irdresearch.irzimbabwe.shared.model.Patient;
import org.irdresearch.irzimbabwe.shared.model.Referral;
import org.irdresearch.irzimbabwe.shared.model.Response;
import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.DataException;
import org.irdresearch.smstarseel.data.InboundMessage;
import org.irdresearch.smstarseel.data.InboundMessage.InboundStatus;
import org.irdresearch.smstarseel.data.OutboundMessage.PeriodType;
import org.irdresearch.smstarseel.data.OutboundMessage.Priority;

import com.mysql.jdbc.StringUtils;

public class ResponseReaderJob extends TimerTask{

	private static int MAX_CELL_NUMBER_MATCH_LENGTH = 9;
	private ServerServiceImpl ssl = new ServerServiceImpl();
	
	@Override
	public void run() 
	{
		System.out.println("ResponseReaderJob Running");

		TarseelServices tsc = TarseelContext.getServices();

		try{
			List<InboundMessage> list = new ArrayList<InboundMessage>();
			try {
				list = tsc.getSmsService().findInbound(null, null, InboundStatus.UNREAD, null, null, null, IRZ.getSmsAppAssignedProjectID(), false);
			} catch (DataException e1) {
				e1.printStackTrace();
			}
			
			for (InboundMessage ib : list) {
				try{
					String sender = ib.getOriginator();
					if(sender.length() > MAX_CELL_NUMBER_MATCH_LENGTH){
						sender = sender.substring(sender.length() - MAX_CELL_NUMBER_MATCH_LENGTH);
					}
					
					String responderTable = SmsRuleParam.ReferenceTableTreatmentSiteSms;
					String responderColumn = SmsRuleParam.ReferenceColumnTreatmentSiteSms;
					Object responderId = null;
					try{
						responderId = HibernateUtil.util.selectObject("SELECT "+responderColumn+" FROM "+responderTable+" WHERE mobile like '%"+sender+"'");
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
					try{
						if(responderId == null || StringUtils.isEmptyOrWhitespaceOnly(responderId.toString())){
							responderTable = SmsRuleParam.ReferenceTableClientSms;
							responderColumn = SmsRuleParam.ReferenceColumnClientSms;
							responderId = HibernateUtil.util.selectObject("SELECT "+responderColumn+" FROM "+responderTable+" WHERE mobile like '%"+sender+"'");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					if(responderId != null)
					{
						if(responderTable.equalsIgnoreCase(SmsRuleParam.ReferenceTableClientSms) 
								&& ib.getText().trim().equalsIgnoreCase("stop")) {
							stopCommandReceived(responderTable, responderColumn, responderId.toString(), ib.getOriginator());
						}
						
						if(responderTable.equalsIgnoreCase(SmsRuleParam.ReferenceTableClientSms)) {
							visitedCommandReceived(responderTable, responderColumn, responderId.toString(),ib.getText(), ib.getOriginator());
						}
					
						Response resp = new Response();
						
						resp.setDateResponseProcessed(new Date());
						resp.setRecievedDate(ib.getRecieveDate());
						resp.setReferenceNumber(ib.getReferenceNumber());
						resp.setResponderCellNumber(ib.getOriginator());
						resp.setResponderId(responderId.toString());
						resp.setResponderReferenceColumn(responderColumn);
						resp.setResponderReferenceTable(responderTable);
						resp.setText(ib.getText());
						resp.setType(ResponseType.SMS.name());
						
						HibernateUtil.util.save(resp);
					}
					
					tsc.getSmsService().markInboundAsRead(ib.getReferenceNumber());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			tsc.commitTransaction();
			System.out.println("ResponseReaderJob Ran Successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			tsc.closeSession();
		}
	}

	public static void stopCommandReceived(String responderTable, String responderColumn, String responderId, String responderCell){
		try{
			String query = 
				" UPDATE sms_log SET " +
				" status = '"+SmsStatus.CANCELLED+"' , " +
				" cancellation_reason = CONCAT(IFNULL(cancellation_reason,''),CURDATE(),' STOP command received;') " +
				" WHERE recipient_reference_table='"+responderTable+"' " +
						" AND recipient_reference_column='"+responderColumn+"' " +
						" AND recipient_id='"+responderId+"' " +
						" AND status='"+SmsStatus.PENDING+"'";
		
			HibernateUtil.util.runCommand(query);
		}
		catch (Exception e) {
			e.printStackTrace();
			TarseelServices tsc = TarseelContext.getServices();
			try{
				String errorReply = "Error while processing your request. Contact Program team.";

				int projectId = IRZ.getSmsAppAssignedProjectID();
				int validityPeriod = 1;
				PeriodType periodType = PeriodType.DAY;
				
				Priority priority = Priority.HIGH;
				
				Date duedate = new Date();
				
				tsc.getSmsService().createNewOutboundSms(responderCell, errorReply, duedate, priority, validityPeriod, periodType, projectId, null);
					
				tsc.commitTransaction();
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
			finally{
				tsc.closeSession();
			}
		}
	}
	
	private static void patientsVisited(ArrayList<String> clientIDs){
		String clientList = "";
		for (String string : clientIDs) {
			clientList += "'"+string+"',";
		}
		
		if(clientList.endsWith(",")){
			clientList = clientList.substring(0, clientList.lastIndexOf(","));
		}
		String query = 
			" UPDATE sms_log SET " +
			" status = '"+SmsStatus.CANCELLED+"' , " +
			" cancellation_reason = CONCAT(IFNULL(cancellation_reason,''),CURDATE(),' VISITED command received;') " +
			" WHERE recipient_reference_table='"+SmsRuleParam.ReferenceTableClientSms+"' " +
					" AND recipient_reference_column='"+SmsRuleParam.ReferenceColumnClientSms+"' " +
					" AND recipient_id IN ("+clientList+") " +
					" AND status='"+SmsStatus.PENDING+"'";
	
		HibernateUtil.util.runCommand(query);
	}
	
	public void visitedCommandReceived(String responderTable, String responderColumn, String senderSiteId, String responderCell, String text) {
		try{
			text = text.trim().replaceAll("\\s+", " ");
			while(text.endsWith(";")){
				text = text.substring(0, text.lastIndexOf(";"));
			}
			String[] splittedText = text.split(";");
			SimpleDateFormat df = new SimpleDateFormat(SmsRuleParam.SiteResponseDateFormat);
			df.setLenient(false);
			
			String errorLines = "";
			
			int chunkNum = 1;
			for (String string : splittedText) {
				if(!RegexUtil.isValidSMS(string.trim().toUpperCase())){
					errorLines += chunkNum+",";
				}
				chunkNum++;
			}
			
			if(!errorLines.equals("")){
				TarseelServices tsc = TarseelContext.getServices();
				try{
					String errorReply = "Invalid message format near record numbers (" + errorLines + "). Correct format is (ClientId Date Y/N;....):(A0201221210 26NOV2012 N; A0201223460 26NOV2012 Y)";
	
					int projectId = IRZ.getSmsAppAssignedProjectID();
					int validityPeriod = 1;
					PeriodType periodType = PeriodType.DAY;
					
					Priority priority = Priority.HIGH;
					
					Date duedate = new Date();
					
					tsc.getSmsService().createNewOutboundSms(responderCell, errorReply, duedate, priority, validityPeriod, periodType, projectId, null);
						
					tsc.commitTransaction();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					tsc.closeSession();
				}
				
				return;
			}
			
			String errorChunk = "";
			for (String string : splittedText) 
			{
				string = string.trim();
				String clientID = string.substring(0, string.indexOf(" ")).trim();
				String date = string.substring(string.indexOf(" "), string.lastIndexOf(" ")).trim();
				try{
					df.parse(date);
				}
				catch (Exception e) {
					e.printStackTrace();
					errorChunk += "Invalid date:"+date;
				}
				
				Referral ref = ssl.findReferral(clientID);
				if(ref == null){
					errorChunk += " "+ clientID+" Not referred;";
				}
			}
			
			if(!errorChunk.equals("")){
				TarseelServices tsc = TarseelContext.getServices();
				try{
					String errorReply = errorChunk;
	
					int projectId = IRZ.getSmsAppAssignedProjectID();
					int validityPeriod = 1;
					PeriodType periodType = PeriodType.DAY;
					
					Priority priority = Priority.HIGH;
					
					Date duedate = new Date();
					
					tsc.getSmsService().createNewOutboundSms(responderCell, errorReply, duedate, priority, validityPeriod, periodType, projectId, null);
						
					tsc.commitTransaction();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					tsc.closeSession();
				}
				
				return;
			}
			
			ArrayList<String> clientIDs = new ArrayList<String>();
			for (String string : splittedText) {
				string = string.trim();
				String clientID = string.substring(0, string.indexOf(" ")).trim();
				String date = string.substring(string.indexOf(" "), string.lastIndexOf(" ")).trim();
				Date visitDate = df.parse(date);
				String treatmentStatus = string.substring(string.lastIndexOf(" ")).trim();
				
				clientIDs.add(clientID);
				
				Patient patient = ssl.findPatient(clientID);
				
				if(treatmentStatus.trim().equalsIgnoreCase("Y")){
					patient.setPatientStatus("ON_TREAT");
					patient.setTreatmentSite(senderSiteId);
					ssl.updatePatient(patient);
				}
				Encounter encounter = new Encounter(new EncounterId(0, clientID, senderSiteId, "VISIT_IND"), senderSiteId);
				encounter.setDateStart(new Date());
				encounter.setDateEnd(new Date());
				encounter.setDateEntered(visitDate);
				EncounterId id = encounter.getId();
				ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults>();
				encounterResults.add (new EncounterResults(new EncounterResultsId(id.getEId(), id.getPid1(), id.getPid2(), id.getEncounterType(), "TREATMENT_STARTED"), treatmentStatus));
				ssl.saveVisitIndication(encounter, encounterResults.toArray(new EncounterResults[] {}));
			}
			
			patientsVisited(clientIDs);
		}
		catch (Exception e) {
			e.printStackTrace();
			TarseelServices tsc = TarseelContext.getServices();
			try{
				String errorReply = "Error while processing your request. Contact Program team.";

				int projectId = IRZ.getSmsAppAssignedProjectID();
				int validityPeriod = 1;
				PeriodType periodType = PeriodType.DAY;
				
				Priority priority = Priority.HIGH;
				
				Date duedate = new Date();
				
				tsc.getSmsService().createNewOutboundSms(responderCell, errorReply, duedate, priority, validityPeriod, periodType, projectId, null);
					
				tsc.commitTransaction();
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
			finally{
				tsc.closeSession();
			}
		}
	}
}
