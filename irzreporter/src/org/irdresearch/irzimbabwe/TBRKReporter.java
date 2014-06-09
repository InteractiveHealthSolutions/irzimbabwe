/**
 * De-Encounterizer fetches form data from Encounter tables and generates tables for each encounter type in the database.
 * In Addition, it also creates csv's of Forms and compresses into a zipped file. 
 * 
 */

package org.irdresearch.irzimbabwe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author owais.hussain@irdinformatics.org
 * 
 */
public final class TBRKReporter
{
	public static final String	directoryPath	= "c:\\workspace\\irzimbabwe\\DataDump";
//	public static final String	directoryPath	= "/var/lib/tomcat6/webapps/irzimbabwe/DataDump";
	public static final String	filePath		= directoryPath + new Date ().getTime () + ".sql";
	public static final String	DB				= "irzimbabwe";
	public static final String	DW				= "irzimbabwe_rpt";
	public static HibernateUtil	util			= new HibernateUtil ();
	public static Connection	conn;

	/**
	 * Main executable. Arguments need to be provided as: 1xx to execute
	 * deencounterizer x1x to generate reports xx1 to dump forms
	 * 
	 * @param args
	 */
	public static void main (String[] args)
	{
		if (args.length == 0 || args[0] == null || args[0].length () != 3)
		{
			System.out.println ("Arguments are invalid. Arguments need to be provided as: 1xx to execute deencounterizer, x1x to generate reports, xx1 to dump forms");
			return;
		}
		// Deencounterize
		if (args[0].charAt (0) == '1')
		{
			Object[] encounterTypes = util.selectObjects ("select encounter_type from " + DB + ".encounter_type order by encounter_type");
			System.out.println ("Deencounterizer starting on " + new Date ().toString ());
			for (Object o : encounterTypes)
				deencounterizeUsingQuery (o.toString ());
			System.out.println ("Deencounterizer completed on " + new Date ().toString ());
		}
		// Generate Reports
		if (args[0].charAt (1) == '1')
		{
			System.out.println ("Reports generation starting on " + new Date ().toString ());
			generateReports ();
			System.out.println ("Reports generation completed on " + new Date ().toString ());
		}
		// Dump Forms
		if (args[0].charAt (2) == '1')
		{
			System.out.println ("Form Dumps starting on " + new Date ().toString ());
			dumpForms ();
			System.out.println ("Form Dumps completed on " + new Date ().toString ());
		}
	}

	public static void dumpForms ()
	{
		String pathSeparator = "/";
		String formName = "";
		String query = "";

		formName = "Patients";
		query = "select patient_id from patient";
		generateCSVfromQuery (directoryPath + pathSeparator + formName, query);
		System.out.println (formName + " completed.");

		String dateString = new SimpleDateFormat ("yyyyMMdd").format (new Date ());
		// Compress files
		TextFile.compressDirectory (directoryPath, directoryPath + dateString, pathSeparator);
	}

	public static void generateReports ()
	{
		System.out.println ("Executing ImportData");
		util.runProcedure ("call ImportData");
		System.out.println ("ImportData Complete");
	}

	public static boolean deencounterizeUsingQuery (String encounterType)
	{
		Object[] elements = util.selectObjects ("select element from " + DB + ".encounter_element where encounter_type = '" + encounterType + "'");
		StringBuilder groupConcat = new StringBuilder ();
		for (Object o : elements)
		{
			String str = o.toString ().replace ("'", "''");
			groupConcat.append ("group_concat(if(er.element = '" + str + "', er.value, null)) as '" + str + "',");
		}
		String baseQuery = "select e.e_id, e.pid1, e.pid2, e.encounter_type, e.location_id, e.date_start, e.date_end, e.date_entered, " + groupConcat.toString () + "'' as BLANK "
				+ "from " + DB + ".encounter as e inner join " + DB + ".encounter_results as er using (e_id, pid1, pid2, encounter_type)" + "where e.encounter_type = '" + encounterType + "'"
				+ "group by e.e_id, e.pid1, e.pid2, e.encounter_type";
		// Drop previous table
		util.runCommand ("drop table if exists Enc_" + encounterType);
		System.out.println ("Generating table for " + encounterType);
		// Insert new data
		int result = util.runCommand ("create table Enc_" + encounterType + " " + baseQuery);
		// Creating Primary key
		util.runCommand ("alter table Enc_" + encounterType + " add primary key (e_id, pid1, pid2, encounter_type)");
		return result >= 0;
	}

	@SuppressWarnings("deprecation")
	public static void insertBulkData (String[] insertCommands)
	{
		// Bulk insert
		try
		{
			DatabaseMetaData metaData = util.getSession ().connection ().getMetaData ();
			conn = metaData.getConnection ();
			conn.setAutoCommit (true);
			Statement st = conn.createStatement ();
			st.clearBatch ();
			for (String s : insertCommands)
				if (s.startsWith ("insert"))
					st.addBatch (s);
			st.executeBatch ();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace ();
		}
	}

	@SuppressWarnings("deprecation")
	public static String generateCSVfromQuery (String targetPath, String query)
	{
		try
		{
			Connection con = HibernateUtil.util.getSession ().connection ();
			// con.setCatalog(database);
			Statement statement = con.createStatement ();
			ResultSet result = statement.executeQuery (query);
			ArrayList<String> list = new ArrayList<String> ();
			int range = result.getMetaData ().getColumnCount ();
			String record = "";
			for (int i = 0; i < range; i++)
				record += result.getMetaData ().getColumnName (i + 1) + ",";
			list.add (record.substring (0, record.length () - 1));
			while (result.next ())
			{
				record = "\"";
				for (int i = 0; i < range; i++)
					record += result.getString (i + 1) + "\",\"";
				if (record.length () > 0)
					list.add (record.substring (0, record.length () - 1).replace ("null", "").replace ("NULL", ""));
			}
			String dest = targetPath + ".csv";
			StringBuilder text = new StringBuilder ();
			for (int i = 0; i < list.size (); i++)
				text.append (list.get (i) + "\r\n");
			// Delete file if existing
			try
			{
				File file = new File (dest);
				file.delete ();
				Writer output = null;
				output = new BufferedWriter (new FileWriter (file));
				output.write (text.toString ());
				output.close ();
				output.flush ();
			}
			catch (Exception e)
			{
				// Not implemented
			}
			return dest.substring (dest.lastIndexOf ('/') + 1);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return "";
		}
	}
}
