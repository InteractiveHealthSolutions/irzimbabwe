/**
 * Implements hibernate features. This class must be initialized before performing any hibernate operation 
 */

package org.irdresearch.irzimbabwe;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class HibernateUtil implements Serializable
{
	private static final long		serialVersionUID	= -2333198879612643152L;
	public static HibernateUtil		util				= new HibernateUtil();
	private static SessionFactory	factory;

	/**
	 * Default Constructor to initialize Session
	 */
	public HibernateUtil()
	{
		factory = new Configuration().configure().buildSessionFactory();
	}

	/**
	 * Create a query object from query string
	 * 
	 * @param queryString
	 * @return
	 */
	public Query create(String query)
	{
		return getSession().createQuery(query);
	}

	/**
	 * Get record count for given query
	 * 
	 * @return
	 */
	public long count(String query)
	{
		return count(create(query));
	}

	/**
	 * Get record count for given query
	 * 
	 * @return
	 */
	public long count(Query query)
	{
		return Long.parseLong(query.uniqueResult().toString());
	}

	/**
	 * Overloaded method for findObject(String)
	 * 
	 * @param query
	 * @return
	 */
	public Object findObject(String query)
	{
		return findObjects(query)[0];
	}

	/**
	 * Get a list of objects from given query.
	 * 
	 * @param query
	 * @return
	 */
	public Object[] findObjects(String query)
	{
		try
		{
			Session session = getSession();
			Query q = session.createQuery(query);
			List<?> list = q.list();
			return list.toArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get a list of objects from given SQL query
	 * 
	 * @param SQLQuery
	 * @return
	 */
	public Object selectObject(String SqlQuery)
	{
		return selectObjects(SqlQuery)[0];
	}

	/**
	 * Get a list of objects from given SQL query
	 * 
	 * @param SQLQuery
	 * @return
	 */
	public Object[] selectObjects(String SqlQuery)
	{
		Session session = getSession();
		// session.beginTransaction();
		SQLQuery q = session.createSQLQuery(SqlQuery);
		List<?> list = q.list();
		return list.toArray();
	}

	/**
	 * Get a list of objects from given SQL query
	 * 
	 * @param SQLQuery
	 * @return
	 */
	public Object[][] selectData(String SqlQuery)
	{
		Session session = getSession();
		SQLQuery q = session.createSQLQuery(SqlQuery);
		List<?> list = q.list();
		Object[][] tableData = new Object[list.size()][];
		try
		{
			int cnt = 0;
			for (ListIterator<?> iter = list.listIterator(); iter.hasNext();)
			{
				Object[] array = (Object[]) iter.next();
				tableData[cnt++] = array;
			}
			return tableData;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update an object present in the database
	 * 
	 * @param obj
	 * @return
	 */
	public boolean update(Object obj)
	{
		try
		{
			Session session = getSession();
			session.update(obj);
			session.flush();
			session.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves an object into database
	 * 
	 * @param obj
	 * @return
	 */
	public boolean bulkSave(Object[] objects)
	{
		try
		{
			Session session = getSession();
			for(Object o : objects)
				session.save(o);
			session.flush();
			session.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean save(Object obj)
	{
		try
		{
			Session session = getSession();
			session.save(obj);
			session.flush();
			session.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Deletes an Object from database
	 * 
	 * @param obj
	 * @return
	 */
	public boolean delete(Object obj)
	{
		try
		{
			Session session = getSession();
			session.delete(obj);
			session.flush();
			session.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Executes a DML command
	 * 
	 * @param command
	 * 			SQL statement as command
	 * @return
	 * 			number of records affected 
	 */
	public int runCommand(String command)
	{
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		SQLQuery q = session.createSQLQuery(command);
		int results = q.executeUpdate();
		transaction.commit();
		return results;
	}

	/**
	 * Executes a Stored Procedure
	 * 
	 * @param procedure
	 * 			SQL statement as procedure
	 * @return
	 * 			number of records affected 
	 */
	@SuppressWarnings("deprecation")
	public boolean runProcedure(String procedure)
	{
		Session session = getSession();
		session.beginTransaction();
		try
		{
			CallableStatement callableStatement = session.connection().prepareCall(procedure);
			System.out.println("Executing stored procedure: " + procedure);
			callableStatement.execute();
			session.getTransaction().commit();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return the session
	 */
	public Session getSession()
	{
		return factory.openSession();
	}
}
