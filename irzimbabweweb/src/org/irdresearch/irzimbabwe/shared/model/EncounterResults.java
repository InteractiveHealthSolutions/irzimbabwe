
package org.irdresearch.irzimbabwe.shared.model;

// Generated Jun 12, 2012 4:08:49 PM by Hibernate Tools 3.4.0.CR1

/**
 * EncounterResults generated by hbm2java
 */
public class EncounterResults implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5229720176007007640L;
	private EncounterResultsId	id;
	private String				value;

	public EncounterResults ()
	{
	}

	public EncounterResults (EncounterResultsId id)
	{
		this.id = id;
	}

	public EncounterResults (EncounterResultsId id, String value)
	{
		this.id = id;
		this.value = value;
	}

	public EncounterResultsId getId ()
	{
		return this.id;
	}

	public void setId (EncounterResultsId id)
	{
		this.id = id;
	}

	public String getValue ()
	{
		return this.value;
	}

	public void setValue (String value)
	{
		this.value = value;
	}

	@Override
	public String toString ()
	{
		return id + ", " + value;
	}

}