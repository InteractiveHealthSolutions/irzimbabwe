
package org.irdresearch.irzimbabwe.shared.model;

// Generated Jun 12, 2012 4:08:49 PM by Hibernate Tools 3.4.0.CR1

/**
 * Defaults generated by hbm2java
 */
public class Defaults implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2502012352997723572L;
	private DefaultsId			id;

	public Defaults ()
	{
	}

	public Defaults (DefaultsId id)
	{
		this.id = id;
	}

	public DefaultsId getId ()
	{
		return this.id;
	}

	public void setId (DefaultsId id)
	{
		this.id = id;
	}

	@Override
	public String toString ()
	{
		return String.valueOf (id);
	}

}
