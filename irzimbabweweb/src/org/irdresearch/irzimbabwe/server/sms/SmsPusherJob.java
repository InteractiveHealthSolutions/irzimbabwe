/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package org.irdresearch.irzimbabwe.server.sms;

import java.util.TimerTask;

import org.irdresearch.irzimbabwe.server.ServerServiceImpl;
import org.irdresearch.irzimbabwe.server.util.HibernateUtil;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.SmsRuleParam;
import org.irdresearch.irzimbabwe.shared.SmsStatus;
import org.irdresearch.irzimbabwe.shared.model.Person;
import org.irdresearch.irzimbabwe.shared.model.SmsLog;
import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.OutboundMessage.PeriodType;
import org.irdresearch.smstarseel.data.OutboundMessage.Priority;

import com.mysql.jdbc.StringUtils;

public class SmsPusherJob extends TimerTask{
	private static ServerServiceImpl ssl = new ServerServiceImpl();

	public SmsPusherJob() {
	}
	
	@Override
	public void run() {
		try{
		//	System.out.println("SmsPusherJob Running");
			Object[] objs = HibernateUtil.util.findObjects("FROM SmsLog WHERE status='"+SmsStatus.PENDING+"' AND DATE(dateDue) <= CURDATE()");
	
			for (Object object : objs) {
				SmsLog sms = (SmsLog) object;
				
				if(StringUtils.isEmptyOrWhitespaceOnly(sms.getRecipientNumber())){
					String query = "SELECT mobile FROM "+sms.getRecipientReferenceTable()+" WHERE "+sms.getRecipientReferenceColumn()+"='"+sms.getRecipientId()+"'";
					Object rc = HibernateUtil.util.selectObject(query);
					
					if(rc != null){
						String recipientCell = rc.toString();
						sms.setRecipientNumber(recipientCell);
					}
					else{
						sms.setRecipientNumber("");
					}
				}
				
				if(sms.getText() == null && sms.getRecipientReferenceTable().equalsIgnoreCase(SmsRuleParam.ReferenceTableClientSms)){
					Person person=ssl.findPerson(sms.getRecipientId());
					String langId=person.getPreferredLanguage();
					String text = SmsUtil.insertClientInfoInSmsText(SmsUtil.findText(sms.getRuleId(),langId), sms.getRecipientId(), null);
					sms.setText(text);
				}
				
				sms.setStatus(SmsStatus.WAITING.name());
	
				int projectId = IRZ.getSmsAppAssignedProjectID();
				int validityPeriod = 1;//TODO
				PeriodType periodType = PeriodType.DAY;
				
				Priority priority = Priority.HIGH;
				
				TarseelServices tsc = TarseelContext.getServices();
				try{
					String referenceNumber = tsc.getSmsService().createNewOutboundSms(sms.getRecipientNumber(), sms.getText(), sms.getDateDue(), priority, validityPeriod, periodType, projectId, null);
					
					sms.setReferenceNumber(referenceNumber);
	
					tsc.commitTransaction();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					tsc.closeSession();
				}
			
				HibernateUtil.util.update(sms);
			}
		//	System.out.println("SmsPusherJob Ran Successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
