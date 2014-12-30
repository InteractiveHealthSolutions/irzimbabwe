
package org.irdresearch.irzimbabwe.client;

import java.util.ArrayList;
import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class DataCleaningComposite extends Composite implements ClickHandler
{
	private static ServerServiceAsync	service				= GWT.create (ServerService.class);
	private static final String			menuName			= "SETUP";

	private UserRightsUtil				rights				= new UserRightsUtil ();
	private boolean						valid;

	private FlexTable					flexTable			= new FlexTable ();
	private FlexTable					topFlexTable		= new FlexTable ();
	private FlexTable					rightFlexTable		= new FlexTable ();
	private Grid						grid				= new Grid (1, 2);

	private Button						saveButton			= new Button ("Clean");
	private Button						closeButton			= new Button ("Close");

	private Label						lblDataCleaning		= new Label ("Data Cleaning");
	private Label						lblTaskType			= new Label ("Task Type:");

	private ListBox						taskTypeComboBox	= new ListBox ();

	public DataCleaningComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		lblDataCleaning.setWordWrap (false);
		lblDataCleaning.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblDataCleaning);
		flexTable.setWidget (1, 0, rightFlexTable);
		lblTaskType.setWordWrap (false);
		rightFlexTable.setWidget (0, 0, lblTaskType);
		taskTypeComboBox.addItem ("CLEAN UNUSED GENERATED IDs", "CLEAN_ID");
		taskTypeComboBox.setSelectedIndex (0);
		rightFlexTable.setWidget (0, 1, taskTypeComboBox);
		rightFlexTable.setWidget (1, 1, grid);
		grid.setSize ("100%", "100%");
		saveButton.setText ("Clean");
		saveButton.setEnabled (false);
		grid.setWidget (0, 0, saveButton);
		grid.setWidget (0, 1, closeButton);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		saveButton.addClickHandler (this);
		closeButton.addClickHandler (this);

		IRZClient.refresh (flexTable);
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

	public boolean validate ()
	{
		final StringBuilder errorMessage = new StringBuilder ();
		valid = true;
		/* Validate mandatory fields */
		if (!valid)
		{
			Window.alert (errorMessage.toString ());
			load (false);
		}
		return valid;
	}

	public void saveData ()
	{
		if (validate ())
		{
			String task = IRZClient.get (taskTypeComboBox);
			ArrayList<String> queries = new ArrayList<String> ();
			if (task.equals ("CLEAN_ID"))
			{
				queries.add ("delete from visit where datediff(curdate(), visit_date) > 7 and patient_id not in (select pid from person)");
			}
			try
			{
				service.execute (queries.toArray (new String[] {}), new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						Window.alert (CustomMessage.getInfoMessage (InfoType.OPERATION_SUCCESSFUL));
						load (false);
					}

					public void onFailure (Throwable caught)
					{
						caught.printStackTrace ();
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
		load (true);
		try
		{
			service.getUserRgihts (IRZ.getCurrentUserName (), IRZ.getCurrentRole (), menuName, new AsyncCallback<Boolean[]> ()
			{
				public void onSuccess (Boolean[] result)
				{
					final Boolean[] userRights = result;
					rights.setRoleRights (IRZ.getCurrentRole (), userRights);
					boolean hasAccess = rights.getAccess (AccessType.INSERT) | rights.getAccess (AccessType.UPDATE) | rights.getAccess (AccessType.DELETE) | rights.getAccess (AccessType.SELECT);
					if (!hasAccess)
					{
						Window.alert (CustomMessage.getErrorMessage (ErrorType.DATA_ACCESS_ERROR));
						MainMenuComposite.clear ();
					}
					saveButton.setEnabled (rights.getAccess (AccessType.UPDATE));
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
		if (sender == saveButton)
		{
			saveData ();
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}
}
