
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.User;
import org.irdresearch.irzimbabwe.shared.model.UserRights;
import org.irdresearch.irzimbabwe.shared.model.UserRightsId;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class UserRightsComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service			= GWT.create (ServerService.class);
	private static final String			menuName		= "SETUP";
	private static final String			tableName		= "user_rights";

	private UserRightsUtil				rights			= new UserRightsUtil ();
	private UserRights					current;

	private FlexTable					flexTable		= new FlexTable ();
	private FlexTable					topFlexTable	= new FlexTable ();
	private FlexTable					leftFlexTable	= new FlexTable ();
	private FlexTable					rightFlexTable	= new FlexTable ();
	private FlexTable					accessFlexTable	= new FlexTable ();
	private Grid						grid			= new Grid (1, 3);

	private ToggleButton				createButton	= new ToggleButton ("Create");
	private Button						saveButton		= new Button ("Save");
	private Button						deleteButton	= new Button ("Delete");
	private Button						closeButton		= new Button ("Close");
	private Button						toggleAllButton	= new Button ("Toggle All");

	private Label						lblTbReachUser	= new Label (IRZ.getProjectTitle () + " User Rights");
	private Label						lblDataItem		= new Label ("Data Item:");
	private Label						lblAccess		= new Label ("Access:");

	private ListBox						rolesListBox	= new ListBox ();
	private ListBox						menuComboBox	= new ListBox ();

	private CheckBox					searchCheckBox	= new CheckBox ("Search");
	private CheckBox					insertCheckBox	= new CheckBox ("Insert");
	private CheckBox					updateCheckBox	= new CheckBox ("Update");
	private CheckBox					deleteCheckBox	= new CheckBox ("Delete");
	private CheckBox					printCheckBox	= new CheckBox ("Print");

	public UserRightsComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 1, topFlexTable);
		lblTbReachUser.setWordWrap (false);
		lblTbReachUser.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblTbReachUser);
		flexTable.setWidget (1, 0, leftFlexTable);
		rolesListBox.setName ("USER_ROLE");
		rolesListBox.setEnabled (false);
		rolesListBox.clear ();
		leftFlexTable.setWidget (0, 0, rolesListBox);
		rolesListBox.setVisibleItemCount (5);
		flexTable.setWidget (1, 1, rightFlexTable);
		lblDataItem.setWordWrap (false);
		rightFlexTable.setWidget (0, 0, lblDataItem);
		menuComboBox.setName ("MENU_NAME");
		rightFlexTable.setWidget (0, 1, menuComboBox);
		menuComboBox.setWidth ("");
		lblAccess.setWordWrap (false);
		rightFlexTable.setWidget (1, 0, lblAccess);
		rightFlexTable.setWidget (1, 1, accessFlexTable);
		accessFlexTable.setWidth ("100%");
		accessFlexTable.setWidget (0, 0, insertCheckBox);
		accessFlexTable.setWidget (0, 1, searchCheckBox);
		accessFlexTable.setWidget (1, 0, updateCheckBox);
		accessFlexTable.setWidget (1, 1, printCheckBox);
		accessFlexTable.setWidget (2, 0, deleteCheckBox);
		accessFlexTable.setWidget (2, 1, toggleAllButton);
		toggleAllButton.setWidth ("100%");
		createButton.setEnabled (false);
		createButton.setVisible (false);
		rightFlexTable.setWidget (2, 0, createButton);
		rightFlexTable.setWidget (2, 1, grid);
		grid.setSize ("100%", "100%");
		saveButton.setEnabled (false);
		grid.setWidget (0, 0, saveButton);
		deleteButton.setEnabled (false);
		grid.setWidget (0, 1, deleteButton);
		grid.setWidget (0, 2, closeButton);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (0, 1, HasVerticalAlignment.ALIGN_TOP);

		toggleAllButton.addClickHandler (this);
		createButton.addClickHandler (this);
		saveButton.addClickHandler (this);
		deleteButton.addClickHandler (this);
		closeButton.addClickHandler (this);
		rolesListBox.addChangeHandler (this);
		menuComboBox.addChangeHandler (this);

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

	public void setCurrent ()
	{
		current.setSearchAccess (searchCheckBox.getValue ());
		current.setInsertAccess (insertCheckBox.getValue ());
		current.setUpdateAccess (updateCheckBox.getValue ());
		current.setDeleteAccess (deleteCheckBox.getValue ());
		current.setPrintAccess (printCheckBox.getValue ());
	}

	public void clearUp ()
	{
		IRZClient.clearControls (leftFlexTable);
	}

	public boolean validate ()
	{
		return true;
	}

	public void saveData ()
	{
		if (validate ())
		{
			try
			{
				// Not implemented
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
	}

	public void updateData ()
	{
		if (validate ())
		{
			setCurrent ();
			try
			{
				service.updateUserRights (current, new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (result)
							Window.alert (CustomMessage.getInfoMessage (InfoType.UPDATED));
						else
							Window.alert (CustomMessage.getErrorMessage (ErrorType.UPDATE_ERROR));
						load (false);
					}

					public void onFailure (Throwable caught)
					{
						Window.alert (CustomMessage.getErrorMessage (ErrorType.UPDATE_ERROR));
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

	public void deleteData ()
	{
		if (validate ())
		{
			try
			{
				// Not implemented
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
	}

	public void fillData ()
	{
		try
		{
			service.getRowRecord (tableName, new String[] {"search_access", "insert_access", "update_access", "delete_access", "print_access"}, "user_role='" + IRZClient.get (rolesListBox)
					+ "' and menu_name='" + IRZClient.get (menuComboBox) + "'", new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					Boolean[] rights = new Boolean[result.length];
					for (int i = 0; i < result.length; i++)
						rights[i] = Boolean.parseBoolean (result[i]);
					current = new UserRights (new UserRightsId (IRZClient.get (rolesListBox), IRZClient.get (menuComboBox)), rights[0], rights[1], rights[2], rights[3], rights[4]);
					searchCheckBox.setValue (current.isSearchAccess ());
					insertCheckBox.setValue (current.isInsertAccess ());
					updateCheckBox.setValue (current.isUpdateAccess ());
					deleteCheckBox.setValue (current.isDeleteAccess ());
					printCheckBox.setValue (current.isPrintAccess ());
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					// If User Rights are not available, try to Insert
					try
					{
						UserRightsId userRightsId = new UserRightsId (IRZClient.get (rolesListBox), IRZClient.get (menuComboBox));
						UserRights userRights = new UserRights (userRightsId, false, false, false, false, false);
						service.saveUserRights (userRights, new AsyncCallback<Boolean> ()
						{
							public void onSuccess (Boolean result)
							{
								// Inserted
								fillData ();
							}

							public void onFailure (Throwable caught)
							{
								Window.alert (CustomMessage.getErrorMessage (ErrorType.DATA_ACCESS_ERROR));
							}
						});
					}
					catch (Exception e)
					{
						e.printStackTrace ();
					}
					load (false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
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
					try
					{
						service.findUser (IRZ.getCurrentUserName (), new AsyncCallback<User> ()
						{
							public void onSuccess (User result)
							{
								rights.setRoleRights (IRZ.getCurrentRole (), userRights);
								boolean hasAccess = rights.getAccess (AccessType.INSERT) | rights.getAccess (AccessType.UPDATE) | rights.getAccess (AccessType.DELETE)
										| rights.getAccess (AccessType.SELECT);
								if (!hasAccess)
								{
									Window.alert (CustomMessage.getErrorMessage (ErrorType.DATA_ACCESS_ERROR));
									MainMenuComposite.clear ();
								}
								rolesListBox.setEnabled (rights.getAccess (AccessType.SELECT));
								createButton.setEnabled (false);
								saveButton.setEnabled (rights.getAccess (AccessType.UPDATE));
								deleteButton.setEnabled (false);
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
		if (sender == toggleAllButton)
		{
			searchCheckBox.setValue (!searchCheckBox.getValue ());
			insertCheckBox.setValue (!insertCheckBox.getValue ());
			updateCheckBox.setValue (!updateCheckBox.getValue ());
			deleteCheckBox.setValue (!deleteCheckBox.getValue ());
			printCheckBox.setValue (!printCheckBox.getValue ());
			load (false);
		}
		else if (sender == createButton)
		{
			if (createButton.isDown ())
				clearUp ();
			rolesListBox.setEnabled (!createButton.isDown ());
			load (false);
		}
		else if (sender == saveButton)
		{
			if (createButton.isDown ())
				saveData ();
			else
				updateData ();
		}
		else if (sender == deleteButton)
		{
			deleteData ();
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	public void onChange (ChangeEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == rolesListBox)
		{
			load (false);
			menuComboBox.setSelectedIndex (-1);
		}
		else if (sender == menuComboBox)
		{
			fillData ();
		}
	}
}
