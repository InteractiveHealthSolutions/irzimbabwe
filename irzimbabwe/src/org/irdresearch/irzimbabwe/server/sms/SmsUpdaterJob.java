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
			System.out.println("SmsUpdaterJob Running");
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
			System.out.println("SmsUpdaterJob Ran Successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
