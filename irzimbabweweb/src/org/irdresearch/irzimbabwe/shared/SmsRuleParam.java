/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
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
