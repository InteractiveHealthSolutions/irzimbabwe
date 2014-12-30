/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.irzimbabwe.client;

import java.util.Date;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.model.Defaults;
import org.irdresearch.irzimbabwe.shared.model.Definition;
import org.irdresearch.irzimbabwe.shared.model.User;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author owais.hussain@irdresearch.org
 */
public class IRZimbabwe implements EntryPoint, ClickHandler
{
	private static ServerServiceAsync	service				= GWT.create (ServerService.class);
	private static LoadingWidget		loading				= new LoadingWidget ();
	private final MainMenuComposite		mainMenu			= new MainMenuComposite ();

	static RootPanel					rootPanel;
	private VerticalPanel				verticalPanel		= new VerticalPanel ();
	private FlexTable					loginFlexTable		= new FlexTable ();

	private Label						label				= new Label ("Login Name");
	private Label						label_1				= new Label ("Password");
	private TextBox						loginNameTextBox	= new TextBox ();
	private PasswordTextBox				passwordTextBox		= new PasswordTextBox ();

	private Button						loginButton			= new Button ("Login");

	/**
	 * This is the entry point method.
	 */

	public void onModuleLoad ()
	{
		Window.setTitle (IRZ.getProjectTitle ());
		rootPanel = RootPanel.get ();
		rootPanel.setSize ("100%", "50%");
		loginNameTextBox.setMaxLength (20);
		passwordTextBox.setMaxLength (50);
		rootPanel.add (verticalPanel);
		verticalPanel.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setSize ("100%", "");
		verticalPanel.add (loginFlexTable);
		verticalPanel.setCellHorizontalAlignment (loginFlexTable, HasHorizontalAlignment.ALIGN_CENTER);
		loginFlexTable.setSize ("100%", "");
		label.setWordWrap (false);
		loginFlexTable.setWidget (0, 0, label);
		loginFlexTable.setWidget (0, 1, loginNameTextBox);
		loginFlexTable.setWidget (1, 0, label_1);
		loginFlexTable.setWidget (1, 1, passwordTextBox);
		passwordTextBox.addKeyPressHandler (new KeyPressHandler ()
		{
			public void onKeyPress (KeyPressEvent event)
			{
				boolean enterPressed = event.getNativeEvent ().getKeyCode () == KeyCodes.KEY_ENTER;
				if (enterPressed)
				{
					loginButton.click ();
				}
			}
		});
		loginFlexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		loginFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		loginFlexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);
		loginFlexTable.getCellFormatter ().setHorizontalAlignment (0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		loginFlexTable.getCellFormatter ().setHorizontalAlignment (2, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		loginFlexTable.setWidget (2, 1, loginButton);
		loginFlexTable.getCellFormatter ().setHorizontalAlignment (2, 1, HasHorizontalAlignment.ALIGN_LEFT);
		loginButton.addClickHandler (this);
		Window.addWindowClosingHandler (new ClosingHandler ()
		{
			public void onWindowClosing (ClosingEvent event)
			{
				event.setMessage (CustomMessage.getInfoMessage (InfoType.CONFIRM_CLOSE));
			}
		});

		load (true);
		try
		{
			service.getSchema (new AsyncCallback<String[][]> ()
			{
				public void onSuccess (String[][] result)
				{
					IRZ.fillSchema (result);
					// Fill definition lists
					try
					{
						service.getDefaults (new AsyncCallback<Defaults[]> ()
						{
							public void onSuccess (Defaults[] result)
							{
								IRZ.fillDefaults (result);
								// Load default values
								try
								{
									service.getDefinitions (new AsyncCallback<Definition[]> ()
									{
										public void onSuccess (Definition[] result)
										{
											IRZ.fillDefinitions (result);
											loginNameTextBox.setFocus (true);
											login ();
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
								}
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
					}
				}

				@Override
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
		}
	}

	/**
	 * Handle User Login. If user is already logged in, main menu will display
	 * otherwise session renewal window will appear
	 */
	private void login ()
	{
		String userName;
		String userId;
		String role;
		String location;
		String passCode;
		String sessionLimit;
		try
		{
			// Try to get Cookies
			userName = Cookies.getCookie ("UserName");
			userId = Cookies.getCookie ("UserID");
			passCode = Cookies.getCookie ("Pass");
			role = Cookies.getCookie ("Role");
			location = Cookies.getCookie ("Location");
			if (location == null)
				location = "";
			sessionLimit = Cookies.getCookie ("SessionLimit");
			if (userName == null || role == null || passCode == null || sessionLimit == null)
				throw new Exception ();
			loginNameTextBox.setText (userName);

			// If session is expired then renew
			if (new Date ().getTime () > Long.parseLong (sessionLimit))
				if (!renewSession ())
					throw new Exception ();
			setCookies (userName, userId, role, location, passCode);
			service.setCurrentUser (userName, role, new AsyncCallback<Void> ()
			{
				public void onSuccess (Void result)
				{
					verticalPanel.clear ();
					verticalPanel.add (mainMenu);
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
		}
		catch (Exception e)
		{
			loginFlexTable.setVisible (true);
		}
	}

	/**
	 * Remove all widgets from application
	 */
	public static void flushAll ()
	{
		rootPanel.clear ();
		rootPanel.add (new HTML ("Application has been shut down. It is now safe to close the Browser window."));
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load (boolean status)
	{
		verticalPanel.setVisible (!status);
		if (status)
			loading.show ();
		else
			loading.hide ();
	}

	/**
	 * Log out the application
	 */
	public static void logout ()
	{
		try
		{
			flushAll ();
			String userName = IRZ.getCurrentUserName ();
			setCookies ("", "", "", "", "");
			service.recordLogout (userName, new AsyncCallback<Void> ()
			{
				public void onSuccess (Void result)
				{
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * Set browser cookies
	 */
	public static void setCookies (String userName, String userId, String role, String location, String passCode)
	{
		Cookies.removeCookie ("UserName");
		Cookies.removeCookie ("UserID");
		Cookies.removeCookie ("Role");
		Cookies.removeCookie ("Location");
		Cookies.removeCookie ("Pass");
		Cookies.removeCookie ("LoginTime");
		Cookies.removeCookie ("SessionLimit");

		IRZ.setCurrentUserName (userName);
		IRZ.setCurrentRole (role);
		IRZ.setCurrentLocation (location);
		IRZ.setPassCode (passCode);
		if (!userName.equals (""))
			Cookies.setCookie ("UserName", IRZ.getCurrentUserName ());
		if (!role.equals (""))
			Cookies.setCookie ("Role", IRZ.getCurrentRole ());
		if (!location.equals (""))
			Cookies.setCookie ("Location", IRZ.getCurrentLocation ());
		if (!passCode.equals (""))
		{
			Cookies.setCookie ("Pass", IRZ.getPassCode ());
			Cookies.setCookie ("LoginTime", String.valueOf (new Date ().getTime ()));
			Cookies.setCookie ("SessionLimit", String.valueOf (new Date ().getTime () + IRZ.getSessionLimit ()));
		}
	}

	/**
	 * To renew browsing session
	 * 
	 * @return true if renew was successful
	 */
	public static boolean renewSession ()
	{
		String passcode = Window.prompt (CustomMessage.getErrorMessage (ErrorType.SESSION_EXPIRED) + "\n" + "Please enter first 4 characters of your password to renew session.", "");
		if (IRZClient.verifyClientPasscode (passcode))
		{
			Window.alert (CustomMessage.getInfoMessage (InfoType.SESSION_RENEWED));
			return true;
		}
		Window.alert (CustomMessage.getErrorMessage (ErrorType.AUTHENTICATION_ERROR));
		return false;
	}

	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		final String userName = IRZClient.get (loginNameTextBox).toUpperCase ();
		load (true);
		if (sender == loginButton)
		{
			// Check for empty fields
			if (userName.equals ("") || IRZClient.get (passwordTextBox).equals (""))
			{
				Window.alert (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR));
				return;
			}
			try
			{
				service.authenticate (userName, IRZClient.get (passwordTextBox), new AsyncCallback<User> ()
				{
					public void onSuccess (User result)
					{
						final User user = result;
						if (user == null)
						{
							Window.alert (CustomMessage.getErrorMessage (ErrorType.AUTHENTICATION_ERROR));
						}
						else
						{
							if (IRZClient.get (passwordTextBox).equals (IRZ.getDefaultPassword ()))
							{
								String newPassword = Window.prompt (
										"You have logged in using default password. Please enter a new password.\nNote: the default password will be overrid with the new one.", "");
								if (newPassword.equals (Window.prompt ("Please enter the new password again to confirm.", "")))
								{
									try
									{
										service.updatePassword (user.getUserName (), newPassword, new AsyncCallback<Boolean> ()
										{
											public void onSuccess (Boolean result)
											{
												Window.alert (CustomMessage.getInfoMessage (InfoType.ACCESS_GRANTED) + "\nUser Role: " + user.getRole () + " Site: " + user.getLocation ());
												setCookies (user.getUserName (), user.getPid (), user.getRole (), user.getLocation (),
														String.valueOf (IRZClient.getSimpleCode (IRZClient.get (passwordTextBox).substring (0, 3))));
												login ();
											}

											public void onFailure (Throwable caught)
											{
												caught.printStackTrace ();
											}
										});
									}
									catch (Exception e)
									{
										e.printStackTrace ();
									}
								}
								else
								{
									Window.alert ("Password did not match!");
									load (false);
								}
							}
							else
							{
								Window.alert (CustomMessage.getInfoMessage (InfoType.ACCESS_GRANTED) + "\nUser Role: " + user.getRole () + " Site: " + user.getLocation ());
								setCookies (user.getUserName (), user.getPid (), user.getRole (), user.getLocation (),
										String.valueOf (IRZClient.getSimpleCode (IRZClient.get (passwordTextBox).substring (0, 3))));
								login ();
							}
						}
						load (false);
					}

					public void onFailure (Throwable caught)
					{
						Window.alert (CustomMessage.getErrorMessage (ErrorType.UNKNOWN_ERROR));
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
}
