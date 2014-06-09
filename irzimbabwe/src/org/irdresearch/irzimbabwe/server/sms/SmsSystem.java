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
		System.out.println("Service Started: SmsPusherJob");

		timerSmsUpdater = new Timer("Sms Updater Job");// every 30 min
		timerSmsUpdater.schedule(new SmsUpdaterJob(), new Date(System.currentTimeMillis()+(1000*60*2L)), IRZ.getSmsUpdaterServiceRunupMins()*60*1000L);
		System.out.println("Service Started: SmsUpdaterJob");

		timerRespone = new Timer("Response Reader Job");// every 30 min
		timerRespone.schedule(new ResponseReaderJob(), new Date(System.currentTimeMillis()+(1000*60*2L)), IRZ.getResponseReaderServiceRunupMins()*60*1000L);
		System.out.println("Service Started: ResponseReaderJob");
	}
	
	public static boolean startSmsSystem()
	{
		if (_instance == null) {
			_instance = new SmsSystem();
		}
		return true;
	}
}
