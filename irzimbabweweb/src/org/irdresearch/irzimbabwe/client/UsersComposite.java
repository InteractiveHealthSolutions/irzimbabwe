/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class UsersComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service				= GWT.create (ServerService.class);
	private static final String			menuName			= "SETUP";

	private UserRightsUtil				rights				= new UserRightsUtil ();
	private boolean						valid;
	private User						currentUser;

	private FlexTable					flexTable			= new FlexTable ();
	private FlexTable					leftFlexTable		= new FlexTable ();
	private FlexTable					rightFlexTable		= new FlexTable ();
	private FlexTable					topFlexTable		= new FlexTable ();
	private Grid						grid				= new Grid (1, 3);

	private ToggleButton				createButton		= new ToggleButton ("Create User");
	private Button						saveButton			= new Button ("Save");
	private Button						deleteButton		= new Button ("Delete");
	private Button						closeButton			= new Button ("Close");

	private Label						lblTbReachUsers		= new Label (IRZ.getProjectTitle () + " Users");
	private Label						lblUserName			= new Label ("User Name:");
	private Label						lblUserRole			= new Label ("User Role:");
	private Label						lblLocation			= new Label ("Location:");
	private Label						lblStatus			= new Label ("Status:");
	private Label						lblPassword			= new Label ("Password:");

	private TextBox						userNameTextBox		= new TextBox ();

	private PasswordTextBox				passwordTextBox		= new PasswordTextBox ();

	private ListBox						userRoleComboBox	= new ListBox ();
	private ListBox						userStatusComboBox	= new ListBox ();
	private ListBox						userRolesComboBox	= new ListBox ();
	private ListBox						locationComboBox	= new ListBox ();
	private ListBox						usersListBox		= new ListBox ();

	public UsersComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 1, topFlexTable);
		lblTbReachUsers.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblTbReachUsers);
		flexTable.setWidget (1, 0, leftFlexTable);
		userRolesComboBox.setName ("USER_ROLE");
		userRolesComboBox.setEnabled (false);
		leftFlexTable.setWidget (0, 0, userRolesComboBox);
		usersListBox.addClickHandler (this);
		leftFlexTable.setWidget (1, 0, usersListBox);
		usersListBox.setVisibleItemCount (5);
		leftFlexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		leftFlexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		leftFlexTable.getCellFormatter ().setVerticalAlignment (0, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.setWidget (1, 1, rightFlexTable);
		createButton.setEnabled (false);
		rightFlexTable.setWidget (0, 1, createButton);
		lblUserName.setWordWrap (false);
		rightFlexTable.setWidget (1, 0, lblUserName);
		userNameTextBox.setMaxLength (20);
		rightFlexTable.setWidget (1, 1, userNameTextBox);
		lblUserRole.setWordWrap (false);
		rightFlexTable.setWidget (2, 0, lblUserRole);
		userRoleComboBox.setName ("USER_ROLE");
		rightFlexTable.setWidget (2, 1, userRoleComboBox);
		userRoleComboBox.setWidth ("100%");
		userRoleComboBox.setVisibleItemCount (1);
		rightFlexTable.setWidget (3, 0, lblLocation);
		rightFlexTable.setWidget (3, 1, locationComboBox);
		lblStatus.setWordWrap (false);
		rightFlexTable.setWidget (4, 0, lblStatus);
		userStatusComboBox.setName ("USER_STATUS");
		rightFlexTable.setWidget (4, 1, userStatusComboBox);
		lblPassword.setWordWrap (false);
		rightFlexTable.setWidget (5, 0, lblPassword);
		passwordTextBox.setReadOnly (true);
		passwordTextBox.setMaxLength (20);
		rightFlexTable.setWidget (5, 1, passwordTextBox);
		rightFlexTable.setWidget (6, 1, grid);
		grid.setSize ("100%", "100%");
		saveButton.setEnabled (false);
		grid.setWidget (0, 0, saveButton);
		deleteButton.setEnabled (false);
		grid.setWidget (0, 1, deleteButton);
		grid.setWidget (0, 2, closeButton);
		rightFlexTable.getCellFormatter ().setVerticalAlignment (2, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (0, 1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 1, HasVerticalAlignment.ALIGN_TOP);

		createButton.addClickHandler (this);
		saveButton.addClickHandler (this);
		deleteButton.addClickHandler (this);
		closeButton.addClickHandler (this);
		userRolesComboBox.addChangeHandler (this);

		IRZClient.refresh (flexTable);
		try
		{
			service.getTableData ("location", new String[] {"location_id", "location_name"}, "", new AsyncCallback<String[][]> ()
			{
				public void onSuccess (String[][] result)
				{
					locationComboBox.clear ();
					locationComboBox.addItem ("");
					for (int i = 0; i < result.length; i++)
						locationComboBox.addItem (result[i][1], result[i][0]);
				}

				public void onFailure (Throwable caught)
				{
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		setRights (menuName.toUpperCase ());
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

	public void clearUp ()
	{
		currentUser = null;
		IRZClient.clearControls (rightFlexTable);
		usersListBox.clear ();
		userNameTextBox.setFocus (true);
		passwordTextBox.setText (IRZ.getDefaultPassword ());
	}

	public void setCurrent ()
	{
		currentUser.setCurrentStatus (IRZClient.get (userStatusComboBox).charAt (0));
		currentUser.setRole (IRZClient.get (userRoleComboBox));
		currentUser.setLocation (IRZClient.get (locationComboBox));
	}

	public void fillData ()
	{
		try
		{
			service.findUser (IRZClient.get (usersListBox), new AsyncCallback<User> ()
			{
				public void onSuccess (User result)
				{
					currentUser = result;
					userNameTextBox.setValue (result.getUserName ());
					userStatusComboBox.setSelectedIndex (IRZClient.getIndex (userStatusComboBox, String.valueOf (currentUser.getCurrentStatus ())));
					passwordTextBox.setValue (currentUser.getPassword ());
					userRoleComboBox.setSelectedIndex (IRZClient.getIndex (userRoleComboBox, currentUser.getRole ()));
					locationComboBox.setSelectedIndex (IRZClient.getIndex (locationComboBox, currentUser.getLocation ()));
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

	/**
	 * Validation Rules for Users form
	 * 
	 * 1. All fields are mandatory
	 * 
	 * 2. User name is unique and cannot contain spaces
	 * 
	 * 3. Password must be of at least 8 characters
	 */
	public boolean validate ()
	{
		final StringBuilder errorMessage = new StringBuilder ();
		valid = true;
		/* Validate mandatory fields */
		if (userNameTextBox.getText ().equals ("") || passwordTextBox.getText ().equals (""))
		{
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		/* Validate data-type rules */
		if (userNameTextBox.getText ().contains (" "))
		{
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
			valid = false;
		}
		/* Check if at least 1 role was selected */
		if (userRoleComboBox.getSelectedIndex () == -1)
		{
			errorMessage.append ("No role was selected. You must assign at least one role to the user\n");
			valid = false;
		}
		/* Validate password */
		if (passwordTextBox.getText ().length () < 8)
		{
			errorMessage.append ("Password is too short. Please enter a strong password of at least 8 characters\n");
			valid = false;
		}
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
			currentUser = new User ();
			try
			{
				currentUser.setPid (IRZClient.get (userNameTextBox).toUpperCase ());
				currentUser.setUserName (IRZClient.get (userNameTextBox).toUpperCase ());
				currentUser.setLoggedIn (false);
				currentUser.setPassword (IRZClient.get (passwordTextBox));
				currentUser.setSecretAnswer (IRZClient.get (passwordTextBox));
				setCurrent ();
				service.saveUser (currentUser, new String[] {IRZClient.get (userRoleComboBox)}, new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (result == null)
							Window.alert (CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR));
						else if (result)
						{
							Window.alert (CustomMessage.getInfoMessage (InfoType.INSERTED));
							clearUp ();
						}
						else
							Window.alert (CustomMessage.getErrorMessage (ErrorType.INSERT_ERROR));
						load (false);
					}

					public void onFailure (Throwable caught)
					{
						Window.alert (CustomMessage.getErrorMessage (ErrorType.INSERT_ERROR));
						load (false);
					}
				});
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
				service.updateUser (currentUser, new String[] {IRZClient.get (userRoleComboBox)}, new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (result)
						{
							Window.alert (CustomMessage.getInfoMessage (InfoType.UPDATED));
							clearUp ();
						}
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
				Window.alert (CustomMessage.getErrorMessage (ErrorType.UPDATE_ERROR));
			}
		}
	}

	public void deleteData ()
	{
		if (validate ())
		{
			try
			{
				service.deleteUser (currentUser, new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (result)
						{
							Window.alert (CustomMessage.getInfoMessage (InfoType.DELETED));
							clearUp ();
						}
						else
							Window.alert (CustomMessage.getErrorMessage (ErrorType.DELETE_ERROR));
						load (false);
					}

					public void onFailure (Throwable caught)
					{
						Window.alert (CustomMessage.getErrorMessage (ErrorType.DELETE_ERROR));
						load (false);
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				Window.alert (CustomMessage.getErrorMessage (ErrorType.DELETE_ERROR));
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
								userRolesComboBox.setEnabled (rights.getAccess (AccessType.SELECT));
								createButton.setEnabled (rights.getAccess (AccessType.INSERT));
								saveButton.setEnabled (rights.getAccess (AccessType.UPDATE));
								deleteButton.setEnabled (rights.getAccess (AccessType.DELETE));
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
		if (sender == usersListBox)
		{
			fillData ();
		}
		else if (sender == createButton)
		{
			if (createButton.isDown ())
				clearUp ();
			userRolesComboBox.setEnabled (!createButton.isDown ());
			usersListBox.setEnabled (!createButton.isDown ());
			load (false);
		}
		else if (sender == saveButton)
		{
			if (createButton.isDown ())
				saveData ();
			else
			{
				if (currentUser == null)
				{
					Window.alert ("Please first select a User from users' list box to update.");
					load (false);
				}
				updateData ();
			}
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
		load (true);
		String selectedValue = IRZClient.get (userRolesComboBox);
		try
		{
			service.getColumnData ("user", "user_name", "where pid in (select pid from person_role where role='" + selectedValue + "')", new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					usersListBox.clear ();
					for (int i = 0; i < result.length; i++)
						usersListBox.insertItem (result[i], i);
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					Window.alert (CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND));
					load (false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
}
