/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Provides various Client-side methods used in the Application
 */

package org.irdresearch.irzimbabwe.client;

import java.util.Date;
import java.util.Iterator;
import org.irdresearch.irzimbabwe.shared.DateTimeUtil;
import org.irdresearch.irzimbabwe.shared.IRZ;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class IRZClient
{
	/**
	 * Creates a 'long' code for a given string using some mathematics
	 * 
	 * @param string
	 * @return
	 */
	public static long getSimpleCode (String string)
	{
		long code = 1;
		for (int i = 0; i < string.length (); i++)
			code *= string.charAt (i);
		return code;
	}

	/**
	 * Verifies whether client has entered a valid pass code (required for some
	 * sensitive operations)
	 * 
	 * @return
	 */
	public static boolean verifyClientPasscode (String passcode)
	{
		try
		{
			String storedPasscode = Cookies.getCookie ("Pass");
			long passedCode = getSimpleCode (passcode.substring (0, 3));
			long existing = Long.parseLong (storedPasscode);
			return (passedCode == existing);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return false;
		}
	}

	/**
	 * Get usually desired value from a widget 1. Text fields return their
	 * respective text 2. List boxes return selected value
	 * 
	 * @param widget
	 * @return
	 */
	public static String get (Widget widget)
	{
		try
		{
			if (widget instanceof TextBoxBase)
				return ((TextBoxBase) widget).getText ();
			if (widget instanceof ListBox)
				return ((ListBox) widget).getValue (((ListBox) widget).getSelectedIndex ());
			if (widget instanceof ValueBoxBase<?>)
				return ((ValueBoxBase<?>) widget).getText ();
			if (widget instanceof DateBox)
				return DateTimeUtil.getFormattedDate (((DateBox) widget).getValue (), DateTimeUtil.SQL_DATETIME);
		}
		catch (Exception e)
		{
		}
		return "";
	}

	/**
	 * Get usually desired value from a widget 1. Text fields return their
	 * respective text 2. List boxes return selected value
	 * 
	 * @param listBox
	 * @return
	 */
	public static String getKey (ListBox listBox)
	{
		try
		{
			return IRZ.getDefinitionKey (listBox.getName (), get (listBox));
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * Get index of a given value from a widget (probably ListBox). Returns -1
	 * if value not found
	 * 
	 * @param widget
	 * 
	 * @param value
	 * @return
	 */
	public static int getIndex (Widget widget, String value)
	{
		if (widget instanceof ListBox)
		{
			ListBox listBox = (ListBox) widget;
			for (int i = 0; i < listBox.getItemCount (); i++)
				if (listBox.getValue (i).equalsIgnoreCase (value))
					return i;
		}
		return -1;
	}

	/**
	 * Fill a widget with values from definition based on its "name" property.
	 * This method also sets current index of a widget to default (if exists in
	 * defaults)
	 * 
	 * @param widget
	 * @return
	 */
	public static Widget fillList (Widget widget)
	{
		if (widget instanceof ListBox)
		{
			ListBox listBox = (ListBox) widget;
			String name = listBox.getName ();
			listBox.clear ();
			String[] values = IRZ.getDefinitionValues (name);
			for (String s : values)
				listBox.addItem (s, IRZ.getDefinitionKey (name, s));
			String defaultValue = IRZ.getDefinitionValue (name, IRZ.getDefaultValue (name));
			listBox.setSelectedIndex (IRZClient.getIndex (listBox, defaultValue));
			return listBox;
		}
		return widget;
	}

	/**
	 * This method refreshes data inside a widget recursively. If the widget is
	 * a List box and the "name" property of the list box is set, then the
	 * method searches the value in "name" (e.g. MARITAL_STATUS) in definitions
	 * and loads into the list. Otherwise if the "name" property is not set, the
	 * list box is left untouched. If the widget is text-type, and the "name"
	 * property is set, then the method sets max length to the allowed length in
	 * table meta data. (The format of name property has to be in the format:
	 * table_name;column_name)
	 * 
	 * @param widget
	 */
	public static void refresh (Widget widget)
	{
		if (widget instanceof FlexTable)
		{
			Iterator<Widget> iter = ((FlexTable) widget).iterator ();
			while (iter.hasNext ())
				refresh (iter.next ());
		}
		else if (widget instanceof Panel)
		{
			Iterator<Widget> iter = ((Panel) widget).iterator ();
			while (iter.hasNext ())
				refresh (iter.next ());
		}
		else if (widget instanceof TextBox)
		{
			TextBox text = (TextBox) widget;
			String name = text.getName ();
			if (!name.equals (""))
			{
				String[] parts = name.split (";");
				if (parts.length == 2)
					text.setMaxLength (IRZ.getMaxLength (parts[0], parts[1]));
			}
		}
		else if (widget instanceof ListBox)
		{
			if (!((ListBox) widget).getName ().equals (""))
				widget = IRZClient.fillList (widget);
		}
	}

	/**
	 * Clears/Resets values in child widgets of the widget passed, depending
	 * upon the widget type
	 * 
	 * @param widget
	 */
	public static void clearControls (Widget widget)
	{
		if (widget instanceof Panel)
		{
			Iterator<Widget> iter = ((Panel) widget).iterator ();
			while (iter.hasNext ())
				clearControls (iter.next ());
		}
		else if (widget instanceof TextBoxBase)
		{
			((TextBoxBase) widget).setText ("");
		}
		else if (widget instanceof RichTextArea)
		{
			((RichTextArea) widget).setText ("");
		}
		else if (widget instanceof ListBox)
		{
			((ListBox) widget).setSelectedIndex (0);
		}
		else if (widget instanceof DatePicker)
		{
			((DatePicker) widget).setValue (new Date ());
		}
	}

}
