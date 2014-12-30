/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package org.irdresearch.irzimbabwe.server.sms;

import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import org.irdresearch.irzimbabwe.server.ServerServiceImpl;
import org.irdresearch.irzimbabwe.server.util.HibernateUtil;
import org.irdresearch.irzimbabwe.shared.SmsStatus;
import org.irdresearch.irzimbabwe.shared.model.SmsLog;
import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.OutboundMessage;
import org.irdresearch.smstarseel.data.OutboundMessage.OutboundStatus;

public class SmsUpdaterJob extends TimerTask{

	private ServerServiceImpl ssl = new ServerServiceImpl();
	
	@Override
	public void run()
	{
		try{
			//System.out.println("SmsUpdaterJob Running");
			List<Object> smsl = Arrays.asList(HibernateUtil.util.findObjects("FROM SmsLog WHERE status = '"+SmsStatus.WAITING+"' AND referenceNumber IS NOT NULL"));
			
			for (Object object : smsl) {
				SmsLog sms = (SmsLog) object;
				
				TarseelServices tsc = TarseelContext.getServices();
				try{
					OutboundMessage om = tsc.getSmsService().findOutboundMessageByReferenceNumber(sms.getReferenceNumber(), true);
					
					if(!om.getStatus().equals(OutboundStatus.PENDING)
							&& !om.getStatus().equals(OutboundStatus.UNKNOWN))
					{
						if(om.getStatus().equals(OutboundStatus.FAILED)){
							sms.setCancellationReason((sms.getCancellationReason()==null?"":sms.getCancellationReason())+om.getErrormessage());
							sms.setStatus(SmsStatus.FAILED.name());
						}
						else if(om.getStatus().equals(OutboundStatus.MISSED)){
							sms.setCancellationReason((sms.getCancellationReason()==null?"":sms.getCancellationReason())+om.getErrormessage());
							sms.setStatus(SmsStatus.MISSED.name());
						}
						else if(om.getStatus().equals(OutboundStatus.SENT)){
							sms.setDateSent(om.getSentdate());
							sms.setStatus(SmsStatus.SENT.name());
						}
						
						ssl.updateSmsLog(sms);
					}//end if
					
				}
				catch(Exception e){
					e.printStackTrace();
				}
				finally{
					tsc.closeSession();
				}
			}//end for
			//System.out.println("SmsUpdaterJob Ran Successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
