
package org.irdresearch.irzimbabwe.shared.model;

// Generated Jun 12, 2012 4:08:49 PM by Hibernate Tools 3.4.0.CR1

/**
 * PersonRole generated by hbm2java
 */
public class PersonRole implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1044966718950828828L;
	private PersonRoleId		id;

	public PersonRole ()
	{
	}

	public PersonRole (PersonRoleId id)
	{
		this.id = id;
	}

	public PersonRoleId getId ()
	{
		return this.id;
	}

	public void setId (PersonRoleId id)
	{
		this.id = id;
	}

	@Override
	public String toString ()
	{
		return String.valueOf (id);
	}

}
