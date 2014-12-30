/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.irzimbabwe.shared.model;

// Generated Jun 12, 2012 4:08:49 PM by Hibernate Tools 3.4.0.CR1

/**
 * EncounterPrerequisiteId generated by hbm2java
 */
public class EncounterPrerequisiteId implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4648583768739321827L;
	private String				encounterType;
	private int					prerequisiteNo;

	public EncounterPrerequisiteId ()
	{
	}

	public EncounterPrerequisiteId (String encounterType, int prerequisiteNo)
	{
		this.encounterType = encounterType;
		this.prerequisiteNo = prerequisiteNo;
	}

	public String getEncounterType ()
	{
		return this.encounterType;
	}

	public void setEncounterType (String encounterType)
	{
		this.encounterType = encounterType;
	}

	public int getPrerequisiteNo ()
	{
		return this.prerequisiteNo;
	}

	public void setPrerequisiteNo (int prerequisiteNo)
	{
		this.prerequisiteNo = prerequisiteNo;
	}

	public boolean equals (Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof EncounterPrerequisiteId))
			return false;
		EncounterPrerequisiteId castOther = (EncounterPrerequisiteId) other;

		return ((this.getEncounterType () == castOther.getEncounterType ()) || (this.getEncounterType () != null && castOther.getEncounterType () != null && this.getEncounterType ().equals (
				castOther.getEncounterType ())))
				&& (this.getPrerequisiteNo () == castOther.getPrerequisiteNo ());
	}

	public int hashCode ()
	{
		int result = 17;

		result = 37 * result + (getEncounterType () == null ? 0 : this.getEncounterType ().hashCode ());
		result = 37 * result + this.getPrerequisiteNo ();
		return result;
	}

	@Override
	public String toString ()
	{
		return encounterType + ", " + prerequisiteNo;
	}

}
