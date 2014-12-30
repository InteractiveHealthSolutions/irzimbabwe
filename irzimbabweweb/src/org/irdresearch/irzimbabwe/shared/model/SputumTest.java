/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.irzimbabwe.shared.model;

// Generated Nov 19, 2012 3:38:13 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;

/**
 * SmearTest generated by hbm2java
 */
public class SputumTest implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1696775970495583505L;
	private SputumTestId		id;
	private String				sampleCode;
	private String				collectedBy;
	private Date				dateCollected;
	private Integer				month;
	private String				registeredBy;
	private Date				dateRegistered;
	private String				smearTestedBy;
	private Date				dateSmearTested;
	private String				sputumQuality;
	private String				smearResult;
	private String				smearRemarks;
	private String				gxpTestedBy;
	private Date				dateGxpTested;
	private String				gxpResult;
	private String				rifResistance;
	private String				gxpRemarks;

	public SputumTest ()
	{
	}

	public SputumTest (SputumTestId id)
	{
		this.id = id;
	}

	public SputumTest (SputumTestId id, String sampleCode, String collectedBy, Date dateCollected, Integer month, String registeredBy, Date dateRegistered, String sputumQuality, String smearTestedBy,
			Date dateSmearTested, String smearResult, String smearRemarks, String gxpTestedBy, Date dateGxpTested, String gxpResult, String rifResistance, String gxpRemarks)
	{
		this.id = id;
		this.sampleCode = sampleCode;
		this.collectedBy = collectedBy;
		this.dateCollected = dateCollected;
		this.month = month;
		this.registeredBy = registeredBy;
		this.dateRegistered = dateRegistered;
		this.smearTestedBy = smearTestedBy;
		this.dateSmearTested = dateSmearTested;
		this.sputumQuality = sputumQuality;
		this.smearResult = smearResult;
		this.smearRemarks = smearRemarks;
		this.gxpTestedBy = gxpTestedBy;
		this.dateGxpTested = dateGxpTested;
		this.gxpResult = gxpResult;
		this.rifResistance = rifResistance;
		this.gxpRemarks = gxpRemarks;
	}

	public SputumTestId getId ()
	{
		return this.id;
	}

	public void setId (SputumTestId id)
	{
		this.id = id;
	}

	public String getSampleCode ()
	{
		return this.sampleCode;
	}

	public void setSampleCode (String sampleCode)
	{
		this.sampleCode = sampleCode;
	}

	public String getCollectedBy ()
	{
		return this.collectedBy;
	}

	public void setCollectedBy (String collectedBy)
	{
		this.collectedBy = collectedBy;
	}

	public Date getDateCollected ()
	{
		return this.dateCollected;
	}

	public void setDateCollected (Date dateCollected)
	{
		this.dateCollected = dateCollected;
	}

	public Integer getMonth ()
	{
		return this.month;
	}

	public void setMonth (Integer month)
	{
		this.month = month;
	}

	public String getRegisteredBy ()
	{
		return this.registeredBy;
	}

	public void setRegisteredBy (String registeredBy)
	{
		this.registeredBy = registeredBy;
	}

	public Date getDateRegistered ()
	{
		return this.dateRegistered;
	}

	public void setDateRegistered (Date dateRegistered)
	{
		this.dateRegistered = dateRegistered;
	}

	public String getSputumQuality ()
	{
		return this.sputumQuality;
	}

	public void setSputumQuality (String sputumQuality)
	{
		this.sputumQuality = sputumQuality;
	}

	public String getSmearTestedBy ()
	{
		return this.smearTestedBy;
	}

	public void setSmearTestedBy (String smearTestedBy)
	{
		this.smearTestedBy = smearTestedBy;
	}

	public Date getDateSmearTested ()
	{
		return this.dateSmearTested;
	}

	public void setDateSmearTested (Date dateSmearTested)
	{
		this.dateSmearTested = dateSmearTested;
	}

	public String getSmearResult ()
	{
		return this.smearResult;
	}

	public void setSmearResult (String smearResult)
	{
		this.smearResult = smearResult;
	}

	public String getSmearRemarks ()
	{
		return this.smearRemarks;
	}

	public void setSmearRemarks (String smearRemarks)
	{
		this.smearRemarks = smearRemarks;
	}

	public String getGxpTestedBy ()
	{
		return this.gxpTestedBy;
	}

	public void setGxpTestedBy (String gxpTestedBy)
	{
		this.gxpTestedBy = gxpTestedBy;
	}

	public Date getDateGxpTested ()
	{
		return this.dateGxpTested;
	}

	public void setDateGxpTested (Date dateGxpTested)
	{
		this.dateGxpTested = dateGxpTested;
	}

	public String getGxpResult ()
	{
		return this.gxpResult;
	}

	public void setGxpResult (String gxpResult)
	{
		this.gxpResult = gxpResult;
	}

	public String getRifResistance ()
	{
		return this.rifResistance;
	}

	public void setRifResistance (String rifResistance)
	{
		this.rifResistance = rifResistance;
	}

	public String getGxpRemarks ()
	{
		return this.gxpRemarks;
	}

	public void setGxpRemarks (String gxpRemarks)
	{
		this.gxpRemarks = gxpRemarks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString ()
	{
		return id + ", " + sampleCode + ", " + collectedBy + ", " + dateCollected + ", " + month + ", " + registeredBy + ", " + dateRegistered + ", " + smearTestedBy + ", " + dateSmearTested + ", "
				+ sputumQuality + ", " + smearResult + ", " + smearRemarks + ", " + gxpTestedBy + ", " + dateGxpTested + ", " + gxpResult + ", " + rifResistance + ", " + gxpRemarks;
	}
}
