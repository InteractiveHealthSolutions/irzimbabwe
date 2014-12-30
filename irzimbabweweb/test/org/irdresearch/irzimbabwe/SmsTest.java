package org.irdresearch.irzimbabwe;

import org.irdresearch.irzimbabwe.server.sms.ResponseReaderJob;
import org.junit.Test;

public class SmsTest {

/*	@Test
	public void smsUtilTest(){
		try {
			//SmsUtil.createSputumResultsClientSms("P9122100011", new Date());
			//SmsUtil.createSputumResultsTreatmentSiteSms("P9122100011", "G1", new Date(), 0);
			//SmsUtil.createReferralClientSms("G11210-00004", "G1", new Date());
			//SmsUtil.createReferralTreatmentSiteSms("P9121000016", "P1", new Date(), 0);
			SmsUtil.createMCDay2ClientSms("P9121000016", new Date());
			SmsUtil.createMCDay7ClientSms("P9121000016", new Date());
			SmsUtil.createMCDay42ClientSms("P9121000016", new Date());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	@Test
	public void responseTest(){
		String text = "C1121000013     26NOV2012  Y   ;  P9121000016    26NOV2012 Y   ;";

		//new ResponseReaderJob().visitedCommandReceived(SmsRuleParam.ReferenceTableTreatmentSiteSms, SmsRuleParam.ReferenceColumnTreatmentSiteSms, "G1", "0334", text);
		new ResponseReaderJob().run();
	}
}
