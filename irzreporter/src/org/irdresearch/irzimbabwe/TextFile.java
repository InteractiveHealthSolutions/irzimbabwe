
package org.irdresearch.irzimbabwe;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.zip.*;

/**
 * @author Owais Ahmed
 * 
 */
public class TextFile
{
	static final int	BUFFER		= 1024;
	static final String	EXTENSION	= ".gzip";

	/**
	 * Reads all text from a text file
	 * 
	 * @param filePath
	 * @return String
	 */
	public static String readAllText (String filePath)
	{
		StringBuilder text = new StringBuilder ();
		try
		{
			FileInputStream fis = new FileInputStream (filePath);
			DataInputStream dis = new DataInputStream (fis);
			BufferedReader br = new BufferedReader (new InputStreamReader (dis));
			String strLine;
			while ((strLine = br.readLine ()) != null)
			{
				text.append (strLine);
			}
			dis.close ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
		return text.toString ();
	}

	/**
	 * Appends text to a text file
	 * 
	 * @param filePath
	 * @param text
	 */
	public static void appendText (String filePath, String text)
	{
		BufferedWriter out = null;
		try
		{
			out = new BufferedWriter (new FileWriter (filePath, true));
			out.write (text);
			if (out != null)
			{
				out.close ();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * Writes an array of strings to a file, appending line separator after each
	 * string
	 * 
	 * @param filePath
	 * @param lines
	 */
	public static void writeAllLines (String filePath, String[] lines)
	{
		StringBuilder text = new StringBuilder ();
		for (String s : lines)
			text.append (s + "\r\n");
		writeAllText (filePath, text.toString ());
	}

	/**
	 * Writes text to a file
	 * 
	 * @param filePath
	 * @param text
	 */
	public static void writeAllText (String filePath, String text)
	{
		Writer output = null;
		File file = new File (filePath);
		try
		{
			output = new BufferedWriter (new FileWriter (file));
			output.write (text);
			output.close ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * Creates a copy of a file
	 * 
	 * @param sourcePath
	 * @param destPath
	 */
	public static void createCopy (String sourcePath, String destPath)
	{
		FileChannel inChannel;
		FileChannel outChannel;
		try
		{
			inChannel = new FileInputStream (sourcePath).getChannel ();
			outChannel = new FileOutputStream (destPath).getChannel ();
			int maxCount = (64 * 1024 * 1024) - (32 * 1024);
			long size = inChannel.size ();
			long position = 0;
			while (position < size)
			{
				position += inChannel.transferTo (position, maxCount, outChannel);
			}
			if (inChannel != null)
				inChannel.close ();
			if (outChannel != null)
				outChannel.close ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * Compress a file using GZIP
	 * 
	 * @param source
	 * @param target
	 */
	public static void compressFile (String source, String target)
	{
		try
		{
			GZIPOutputStream out = new GZIPOutputStream (new FileOutputStream (target + EXTENSION));
			FileInputStream in = new FileInputStream (source);
			byte[] buf = new byte[BUFFER];
			int len;
			while ((len = in.read (buf)) > 0)
			{
				out.write (buf, 0, len);
			}
			in.close ();
			out.finish ();
			out.close ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * Compress a folder using ZIP
	 * 
	 * @param source
	 * @param target
	 */
	public static void compressDirectory (String source, String target, String separator)
	{
		try
		{
			File inFolder = new File (source);
			File outFolder = new File (target + ".zip");
			ZipOutputStream out = new ZipOutputStream (new BufferedOutputStream (new FileOutputStream (outFolder)));
			BufferedInputStream in = null;
			byte[] data = new byte[1000];
			String files[] = inFolder.list ();
			for (int i = 0; i < files.length; i++)
			{
				in = new BufferedInputStream (new FileInputStream (inFolder.getPath () + separator + files[i]), 1000);
				out.putNextEntry (new ZipEntry (files[i]));
				int count;
				while ((count = in.read (data, 0, 1000)) != -1)
				{
					out.write (data, 0, count);
				}
				out.closeEntry ();
			}
			out.flush ();
			out.close ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * Extract a GZIP file
	 * 
	 * @param source
	 * @param target
	 */
	public static void extractFile (String source, String target)
	{
		try
		{
			GZIPInputStream in = new GZIPInputStream (new FileInputStream (source));
			OutputStream out = new FileOutputStream (target.replaceAll (EXTENSION, ""));
			byte[] buf = new byte[BUFFER];
			int len;
			while ((len = in.read (buf)) > 0)
			{
				out.write (buf, 0, len);
			}
			in.close ();
			out.close ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}
}
