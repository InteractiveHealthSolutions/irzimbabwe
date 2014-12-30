/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
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
