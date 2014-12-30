/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package org.irdresearch.irzimbabwe.server.sms;

import java.util.Date;
import java.util.Timer;

import org.irdresearch.irzimbabwe.shared.IRZ;

public class SmsSystem 
{
	private static SmsSystem _instance;
	private static Timer timerSmsPusher;
	private static Timer timerSmsUpdater;
	private static Timer timerRespone;
	
	private SmsSystem() {
		timerSmsPusher = new Timer("Sms Pusher Job");//every hour
		timerSmsPusher.schedule(new SmsPusherJob(), new Date(System.currentTimeMillis()+(1000*60*2L)), IRZ.getSmsPusherServiceRunupMins()*60*1000L);
		//System.out.println("Service Started: SmsPusherJob");

		timerSmsUpdater = new Timer("Sms Updater Job");// every 30 min
		timerSmsUpdater.schedule(new SmsUpdaterJob(), new Date(System.currentTimeMillis()+(1000*60*2L)), IRZ.getSmsUpdaterServiceRunupMins()*60*1000L);
		//System.out.println("Service Started: SmsUpdaterJob");

		timerRespone = new Timer("Response Reader Job");// every 30 min
		timerRespone.schedule(new ResponseReaderJob(), new Date(System.currentTimeMillis()+(1000*60*2L)), IRZ.getResponseReaderServiceRunupMins()*60*1000L);
		//System.out.println("Service Started: ResponseReaderJob");
	}
	
	public static boolean startSmsSystem()
	{
		if (_instance == null) {
			_instance = new SmsSystem();
		}
		return true;
	}
}
