/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.irzimbabwe.shared.model;

// Generated Nov 17, 2012 3:02:56 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;

/**
 * Visit generated by hbm2java
 */
public class Visit implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8308799265129812458L;
	private Integer	visitNo;
	private String	patientId;
	private String	visitPurpose;
	private Boolean	diseaseConfirmed;
	private Date	visitDate;

	public Visit ()
	{
		// Not implemented
	}

	public Visit (String patientId, String visitPurpose)
	{
		this.patientId = patientId;
		this.visitPurpose = visitPurpose;
	}

	public Visit (String patientId, String visitPurpose, Boolean disease_confirmed, Date visitDate)
	{
		this.patientId = patientId;
		this.visitPurpose = visitPurpose;
		this.diseaseConfirmed = disease_confirmed;
		this.visitDate = visitDate;
	}

	public Integer getVisitNo ()
	{
		return this.visitNo;
	}

	public void setVisitNo (Integer visitNo)
	{
		this.visitNo = visitNo;
	}

	public String getPatientId ()
	{
		return this.patientId;
	}

	public void setPatientId (String patientId)
	{
		this.patientId = patientId;
	}

	public String getVisitPurpose ()
	{
		return this.visitPurpose;
	}

	public void setVisitPurpose (String visitPurpose)
	{
		this.visitPurpose = visitPurpose;
	}

	public Boolean getDiseaseConfirmed ()
	{
		return this.diseaseConfirmed;
	}

	public void setDiseaseConfirmed (Boolean disease_confirmed)
	{
		this.diseaseConfirmed = disease_confirmed;
	}

	public Date getVisitDate ()
	{
		return this.visitDate;
	}

	public void setVisitDate (Date visitDate)
	{
		this.visitDate = visitDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString ()
	{
		return visitNo + ", " + patientId + ", " + visitPurpose + ", " + diseaseConfirmed + ", " + visitDate;
	}
}
