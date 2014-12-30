package org.irdresearch.irzimbabwe.shared;

public class SmsRuleParam {
	public static final String ReferenceTableClientSms = "person";
	public static final String ReferenceColumnClientSms = "pid";
	public static final String ReferenceTableTreatmentSiteSms = "location";
	public static final String ReferenceColumnTreatmentSiteSms = "location_id";
	public static final String SiteResponseDateFormat = "ddMMMyyyy";

	public static final String SmsRule_Client_TB_Sputum_Results = "TB_RES_C";
	public static final String SmsRule_Site_TB_Sputum_Results = "TB_RES_S";
	public static final String SmsRule_Site_Referral = "TB_REF_S";
	public static final String SmsRule_Client_Referral_W1 = "TB_REF_C1";
	public static final String SmsRule_Client_Referral_W2 = "TB_REF_C2";
	public static final String SmsRule_Client_Referral_W3 = "TB_REF_C3";
	public static final String SmsRule_Client_Referral_W4 = "TB_REF_C4";
	public static final String SmsRule_Client_Referral_M2 = "TB_REF_C5";
	public static final String SmsRule_Client_Referral_M3 = "TB_REF_C6";
	public static final String SmsRule_Client_Referral_M4_plus = "TB_REF_C7";

	public static final String SmsRule_Client_MC_Day2 = "MC_DAY2";
	public static final String SmsRule_Client_MC_Day7 = "MC_DAY7";
	public static final String SmsRule_Client_MC_Day42 = "MC_DAY42";
	public static final String SmsRule_Client_MC_Custom = "MC_CUSTOM";

}
