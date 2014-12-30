/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.irzimbabwe.client;

import java.util.Date;
import java.util.Iterator;
import org.irdresearch.irzimbabwe.shared.DateTimeUtil;
import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.i18n.client.DateTimeFormat;

public class Report_LogComposite extends Composite implements IReport, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service						= GWT.create (ServerService.class);
	private static final String			menuName					= "DATALOG";
	private UserRightsUtil				rights						= new UserRightsUtil ();
	private boolean						valid;

	private FlexTable					flexTable					= new FlexTable ();
	private FlexTable					topFlexTable				= new FlexTable ();
	private FlexTable					leftFlexTable				= new FlexTable ();
	private FlexTable					rightFlexTable				= new FlexTable ();
	private Grid						grid						= new Grid (1, 2);

	private HorizontalPanel				dateFilterHorizontalPanel	= new HorizontalPanel ();
	private Button						closeButton					= new Button ("Close");
	private Button						exportButton				= new Button ("Export");

	private Label						lblTbReachLog				= new Label (IRZ.getProjectTitle () + " Log");
	private Label						lblUserId					= new Label ("User Name:");
	private Label						lblFrom						= new Label ("From:");
	private Label						lblTo						= new Label ("to:");

	private ListBox						logTypeListBox				= new ListBox ();
	private ListBox						userIdComboBox				= new ListBox ();

	private CheckBox					dateCheckBox				= new CheckBox ("Date");

	private DateBox						fromDateBox					= new DateBox ();
	private DateBox						toDateBox					= new DateBox ();

	public Report_LogComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 1, topFlexTable);
		lblTbReachLog.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblTbReachLog);
		flexTable.setWidget (1, 0, leftFlexTable);
		logTypeListBox.setEnabled (false);
		logTypeListBox.addItem ("LOGIN");
		logTypeListBox.addItem ("INSERT");
		logTypeListBox.addItem ("UPDATE");
		logTypeListBox.addItem ("DELETE");
		leftFlexTable.setWidget (0, 0, logTypeListBox);
		logTypeListBox.setVisibleItemCount (5);
		flexTable.setWidget (1, 1, rightFlexTable);
		rightFlexTable.setSize ("100%", "100%");
		rightFlexTable.setWidget (0, 0, lblUserId);
		rightFlexTable.setWidget (0, 1, userIdComboBox);
		dateCheckBox.setWordWrap (false);
		rightFlexTable.setWidget (1, 0, dateCheckBox);
		dateCheckBox.addValueChangeHandler (new ValueChangeHandler<Boolean> ()
		{
			public void onValueChange (ValueChangeEvent<Boolean> event)
			{
				fromDateBox.setEnabled (dateCheckBox.getValue ());
				toDateBox.setEnabled (dateCheckBox.getValue ());
			}
		});
		rightFlexTable.setWidget (1, 1, dateFilterHorizontalPanel);
		dateFilterHorizontalPanel.add (lblFrom);
		dateFilterHorizontalPanel.setCellVerticalAlignment (lblFrom, HasVerticalAlignment.ALIGN_MIDDLE);
		fromDateBox.setEnabled (false);
		fromDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("dd-MMM-yyyy")));
		dateFilterHorizontalPanel.add (fromDateBox);
		fromDateBox.setWidth ("75px");
		dateFilterHorizontalPanel.add (lblTo);
		dateFilterHorizontalPanel.setCellVerticalAlignment (lblTo, HasVerticalAlignment.ALIGN_MIDDLE);
		toDateBox.setEnabled (false);
		dateFilterHorizontalPanel.add (toDateBox);
		toDateBox.setWidth ("75px");
		rightFlexTable.setWidget (2, 1, grid);
		grid.setSize ("100%", "100%");
		exportButton.setEnabled (false);
		grid.setWidget (0, 0, exportButton);
		grid.setWidget (0, 1, closeButton);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);
		exportButton.addClickHandler (this);
		closeButton.addClickHandler (this);

		try
		{
			load (true);
			service.getColumnData ("user", "user_name", "", new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					userIdComboBox.clear ();
					for (int i = 0; i < result.length; i++)
						userIdComboBox.insertItem (result[i], i);
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					load (false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			load (false);
		}

		setRights (menuName);
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load (boolean status)
	{
		if (status)
			MainMenuComposite.showLoading ();
		else
			MainMenuComposite.hideLoading ();
	}

	public void clearControls (Widget w)
	{
		if (w instanceof Panel)
		{
			Iterator<Widget> iter = ((Panel) w).iterator ();
			while (iter.hasNext ())
				clearControls (iter.next ());
		}
		else if (w instanceof TextBoxBase)
		{
			((TextBoxBase) w).setText ("");
		}
		else if (w instanceof RichTextArea)
		{
			((RichTextArea) w).setText ("");
		}
		else if (w instanceof ListBox)
		{
			((ListBox) w).setSelectedIndex (0);
		}
		else if (w instanceof DatePicker)
		{
			((DatePicker) w).setValue (new Date ());
		}
	}

	public void clearUp ()
	{
		clearControls (flexTable);
	}

	public boolean validate ()
	{
		final StringBuilder errorMessage = new StringBuilder ();
		valid = true;
		/* Validate mandatory fields */
		if (dateCheckBox.getValue ())
		{
			if (IRZClient.get (fromDateBox.getTextBox ()).equals ("") || IRZClient.get (toDateBox.getTextBox ()).equals (""))
			{
				errorMessage.append ("Date: " + CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
				valid = false;
			}
		}
		if (!valid)
			Window.alert (errorMessage.toString ());
		return valid;
	}

	public void viewData (boolean export)
	{
		if (validate ())
		{
			try
			{
				String dateFilter = "";
				if (dateCheckBox.getValue ())
				{
					Date from = fromDateBox.getValue ();
					Date to = toDateBox.getValue ();
					dateFilter = " and date(date_logged) between " + DateTimeUtil.getFormattedDate (from, DateTimeUtil.SQL_DATE) + " and " + DateTimeUtil.getFormattedDate (to, DateTimeUtil.SQL_DATE);
				}
				service.generateCSVfromQuery (IRZ.getDatabaseName (), "select user_name, date_logged, log_type, entity, current_value from log_data where log_type='" + IRZClient.get (logTypeListBox) + "' and user_name='" + IRZClient.get (userIdComboBox)
						+ "'" + dateFilter, new AsyncCallback<String> ()
				{
					public void onSuccess (String result)
					{
						Window.open (result, "_blank", "");
						load (false);
					}

					public void onFailure (Throwable caught)
					{
						load (false);
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				load (false);
			}
		}
	}

	public void setRights (String menuName)
	{
		try
		{
			load (true);
			service.getUserRgihts (IRZ.getCurrentUserName (), IRZ.getCurrentRole (), menuName, new AsyncCallback<Boolean[]> ()
			{

				public void onSuccess (Boolean[] result)
				{
					final Boolean[] userRights = result;
					if (!IRZ.getCurrentRole ().equals ("GUEST"))
					{
						rights.setRoleRights (IRZ.getCurrentRole (), userRights);
						logTypeListBox.setEnabled (rights.getAccess (AccessType.SELECT));
						exportButton.setEnabled (rights.getAccess (AccessType.PRINT));
					}
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					load (false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == exportButton)
		{
			viewData (true);
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	public void onChange (ChangeEvent event)
	{
		// Not implemented
	}
}
