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
import org.irdresearch.irzimbabwe.shared.RegexUtil;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.Location;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LocationComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			menuName						= "SETUP";

	private UserRightsUtil				rights							= new UserRightsUtil ();
	private Location					currentLocation;

	private FlexTable					flexTable						= new FlexTable ();
	private FlexTable					topFlexTable					= new FlexTable ();
	private FlexTable					leftFlexTable					= new FlexTable ();
	private FlexTable					rightFlexTable					= new FlexTable ();
	private Grid						grid							= new Grid (1, 3);

	private VerticalPanel				locationTypeHorizontalPanel		= new VerticalPanel ();
	private VerticalPanel				cityHorizontalPanel				= new VerticalPanel ();
	private VerticalPanel				phoneVerticalPanel				= new VerticalPanel ();
	private VerticalPanel				mobileVerticalPanel				= new VerticalPanel ();
	private VerticalPanel				secondaryPhoneVerticalPanel		= new VerticalPanel ();
	private VerticalPanel				secondaryMobileVerticalPanel	= new VerticalPanel ();

	private ToggleButton				createButton					= new ToggleButton ("Create Site");
	private Button						saveButton						= new Button ("Save");
	private Button						deleteButton					= new Button ("Delete");
	private Button						closeButton						= new Button ("Close");

	private Label						lblSiteRegistration				= new Label ("Site Registration");
	private Label						lblLocationId					= new Label ("Site ID:");
	private Label						lblLocationName					= new Label ("Site Name:");
	private Label						lblLocationType					= new Label ("Site Type:");
	private Label						lblAddress						= new Label ("Address 1:");
	private Label						lblAddress_1					= new Label ("Address 2:");
	private Label						lblCity							= new Label ("City:");
	private Label						lblStateprovince				= new Label ("State/Province:");
	private Label						lblCountry						= new Label ("Country:");
	private Label						lblPhone						= new Label ("Primary Number:");
	private Label						lblMobile						= new Label ("Secondary Number:");
	private Label						lblQuaternaryNumber				= new Label ("Quaternary Number:");
	private Label						lblFax							= new Label ("Tertiary Number:");
	private Label						lblEmail						= new Label ("Other Contact:");

	private TextBox						locationIdTextBox				= new TextBox ();
	private TextBox						locationNameTextBox				= new TextBox ();
	private TextBox						address1TextBox					= new TextBox ();
	private TextBox						address2TextBox					= new TextBox ();
	private TextBox						cityTextBox						= new TextBox ();
	private TextBox						locationTypeTextBox				= new TextBox ();
	private TextBox						stateTextBox					= new TextBox ();
	private TextBox						phoneTextBox					= new TextBox ();
	private TextBox						confirmPhoneTextBox				= new TextBox ();
	private TextBox						mobileTextBox					= new TextBox ();
	private TextBox						confirmMobileTextBox			= new TextBox ();
	private TextBox						secondaryPhoneTextBox			= new TextBox ();
	private TextBox						confirmSecondaryPhoneTextBox	= new TextBox ();
	private TextBox						secondaryMobileTextBox			= new TextBox ();
	private TextBox						confirmSecondaryMobileTextBox	= new TextBox ();
	private TextBox						emailTextBox					= new TextBox ();

	private ListBox						locationNamesListBox			= new ListBox ();
	private ListBox						locationTypesComboBox			= new ListBox ();
	private ListBox						locationTypeComboBox			= new ListBox ();
	private ListBox						cityComboBox					= new ListBox ();
	private ListBox						countryComboBox					= new ListBox ();

	public LocationComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 1, topFlexTable);
		lblSiteRegistration.setWordWrap (false);
		lblSiteRegistration.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblSiteRegistration);
		topFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget (1, 0, leftFlexTable);
		locationTypesComboBox.setName ("LOCATION_TYPE");
		locationTypesComboBox.setTitle ("This box contains Location Types from Definition. Selecting anyone fills the list box below.");
		leftFlexTable.setWidget (0, 0, locationTypesComboBox);
		locationNamesListBox.setTitle ("This list box contains Location names of selected type. Clicking anyone fills details in right panel.");
		leftFlexTable.setWidget (1, 0, locationNamesListBox);
		locationNamesListBox.setVisibleItemCount (10);
		flexTable.setWidget (1, 1, rightFlexTable);
		rightFlexTable.setSize ("100%", "100%");
		createButton.setEnabled (false);
		rightFlexTable.setWidget (0, 1, createButton);
		createButton.addClickHandler (this);
		rightFlexTable.setWidget (1, 0, lblLocationId);
		locationIdTextBox.setMaxLength (2);
		locationIdTextBox.setVisibleLength (2);
		locationIdTextBox.setEnabled (false);
		rightFlexTable.setWidget (1, 1, locationIdTextBox);
		rightFlexTable.setWidget (2, 0, lblLocationName);
		locationNameTextBox.setVisibleLength (35);
		locationNameTextBox.setName ("location;location_name");
		rightFlexTable.setWidget (2, 1, locationNameTextBox);
		rightFlexTable.setWidget (3, 0, lblLocationType);
		rightFlexTable.setWidget (3, 1, locationTypeHorizontalPanel);
		locationTypeHorizontalPanel.add (locationTypeComboBox);
		locationTypeComboBox.setName ("LOCATION_TYPE");
		locationTypeTextBox.setEnabled (false);
		locationTypeTextBox.setMaxLength (20);
		locationTypeHorizontalPanel.add (locationTypeTextBox);
		lblAddress.setVisible (false);
		rightFlexTable.setWidget (4, 0, lblAddress);
		address1TextBox.setVisible (false);
		address1TextBox.setVisibleLength (35);
		address1TextBox.setName ("location;address1");
		rightFlexTable.setWidget (4, 1, address1TextBox);
		lblAddress_1.setVisible (false);
		rightFlexTable.setWidget (5, 0, lblAddress_1);
		address2TextBox.setVisible (false);
		address2TextBox.setVisibleLength (35);
		address2TextBox.setName ("location;address2");
		rightFlexTable.setWidget (5, 1, address2TextBox);
		rightFlexTable.setWidget (6, 0, lblCity);
		rightFlexTable.setWidget (6, 1, cityHorizontalPanel);
		cityComboBox.setName ("CITY");
		cityHorizontalPanel.add (cityComboBox);
		cityTextBox.setEnabled (false);
		cityTextBox.setMaxLength (20);
		cityHorizontalPanel.add (cityTextBox);
		cityTextBox.setName ("location;city");
		lblStateprovince.setVisible (false);
		rightFlexTable.setWidget (7, 0, lblStateprovince);
		stateTextBox.setVisible (false);
		stateTextBox.setName ("20");
		rightFlexTable.setWidget (7, 1, stateTextBox);
		lblCountry.setVisible (false);
		rightFlexTable.setWidget (8, 0, lblCountry);
		countryComboBox.setVisible (false);
		countryComboBox.setName ("COUNTRY");
		rightFlexTable.setWidget (8, 1, countryComboBox);
		lblPhone.setWordWrap (false);
		rightFlexTable.setWidget (9, 0, lblPhone);
		rightFlexTable.setWidget (9, 1, mobileVerticalPanel);
		mobileVerticalPanel.add (mobileTextBox);
		mobileTextBox.setVisibleLength (10);
		mobileTextBox.setMaxLength (10);
		confirmMobileTextBox.setVisibleLength (10);
		confirmMobileTextBox.setMaxLength (10);
		mobileVerticalPanel.add (confirmMobileTextBox);
		lblMobile.setWordWrap (false);
		rightFlexTable.setWidget (10, 0, lblMobile);
		rightFlexTable.setWidget (10, 1, phoneVerticalPanel);
		phoneVerticalPanel.add (phoneTextBox);
		phoneTextBox.setVisibleLength (10);
		phoneTextBox.setMaxLength (10);
		confirmPhoneTextBox.setVisibleLength (10);
		confirmPhoneTextBox.setMaxLength (10);
		phoneVerticalPanel.add (confirmPhoneTextBox);
		lblFax.setWordWrap (false);
		rightFlexTable.setWidget (11, 0, lblFax);
		rightFlexTable.setWidget (11, 1, secondaryMobileVerticalPanel);
		secondaryMobileVerticalPanel.add (secondaryMobileTextBox);
		secondaryMobileTextBox.setVisibleLength (10);
		secondaryMobileTextBox.setMaxLength (10);
		confirmSecondaryMobileTextBox.setVisibleLength (10);
		confirmSecondaryMobileTextBox.setMaxLength (10);
		secondaryMobileVerticalPanel.add (confirmSecondaryMobileTextBox);
		lblQuaternaryNumber.setWordWrap (false);
		rightFlexTable.setWidget (12, 0, lblQuaternaryNumber);
		rightFlexTable.setWidget (12, 1, secondaryPhoneVerticalPanel);
		secondaryPhoneVerticalPanel.add (secondaryPhoneTextBox);
		secondaryPhoneTextBox.setMaxLength (10);
		secondaryPhoneTextBox.setVisibleLength (10);
		confirmSecondaryPhoneTextBox.setVisibleLength (10);
		confirmSecondaryPhoneTextBox.setMaxLength (10);
		secondaryPhoneVerticalPanel.add (confirmSecondaryPhoneTextBox);
		rightFlexTable.setWidget (13, 0, lblEmail);
		emailTextBox.setName ("location;email");
		rightFlexTable.setWidget (13, 1, emailTextBox);
		rightFlexTable.setWidget (14, 1, grid);
		grid.setSize ("100%", "100%");
		saveButton.setEnabled (false);
		grid.setWidget (0, 0, saveButton);
		deleteButton.setEnabled (false);
		grid.setWidget (0, 1, deleteButton);
		grid.setWidget (0, 2, closeButton);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);

		locationTypesComboBox.addChangeHandler (this);
		locationTypeComboBox.addChangeHandler (this);
		locationNamesListBox.addChangeHandler (this);
		cityComboBox.addChangeHandler (this);
		saveButton.addClickHandler (this);
		deleteButton.addClickHandler (this);
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

	public void setCurrent ()
	{
		currentLocation.setLocationType (locationTypeTextBox.isEnabled () ? IRZClient.get (locationTypeTextBox).toUpperCase () : IRZClient.get (locationTypeComboBox));
		currentLocation.setAddress1 (IRZClient.get (address1TextBox).toUpperCase ());
		currentLocation.setAddress2 (IRZClient.get (address2TextBox).toUpperCase ());
		currentLocation.setAddress3 ("");
		currentLocation.setAddress4 ("");
		currentLocation.setCity (cityTextBox.isEnabled () ? IRZClient.get (cityTextBox).toUpperCase () : IRZClient.get (cityComboBox).toUpperCase ());
		currentLocation.setState (IRZClient.get (stateTextBox).toUpperCase ());
		currentLocation.setCountry (IRZClient.get (countryComboBox));
		currentLocation.setPhone (IRZClient.get (phoneTextBox));
		currentLocation.setOtherPhone (IRZClient.get (secondaryPhoneTextBox));
		currentLocation.setMobile (IRZClient.get (mobileTextBox));
		currentLocation.setOtherMobile (IRZClient.get (secondaryMobileTextBox));
		currentLocation.setEmail (IRZClient.get (emailTextBox).toUpperCase ());
	}

	public void fillData ()
	{
		try
		{
			service.findLocation (IRZClient.get (locationNamesListBox), new AsyncCallback<Location> ()
			{
				public void onSuccess (Location result)
				{
					if (result == null)
						Window.alert (CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND));
					else
					{
						currentLocation = result;
						locationIdTextBox.setValue (currentLocation.getLocationId ());
						locationNameTextBox.setValue (currentLocation.getLocationName ());
						String locationType = currentLocation.getLocationType ();
						int locationIndex = IRZClient.getIndex (locationTypeComboBox, locationType);
						if (locationIndex == -1)
						{
							locationTypeComboBox.setSelectedIndex (IRZClient.getIndex (locationTypeComboBox, "OTHER"));
							locationTypeTextBox.setEnabled (true);
							locationTypeTextBox.setValue (currentLocation.getLocationType ());
						}
						else
							locationTypeComboBox.setSelectedIndex (locationIndex);
						address1TextBox.setValue (currentLocation.getAddress1 ());
						address2TextBox.setValue (currentLocation.getAddress2 ());
						String city = currentLocation.getCity ();
						int cityIndex = IRZClient.getIndex (cityComboBox, city);
						if (cityIndex == -1)
						{
							cityComboBox.setSelectedIndex (IRZClient.getIndex (cityComboBox, "OTHER"));
							cityTextBox.setEnabled (true);
							cityTextBox.setValue (currentLocation.getCity ());
						}
						else
							cityComboBox.setSelectedIndex (cityIndex);
						stateTextBox.setValue (currentLocation.getState ());
						countryComboBox.setSelectedIndex (IRZClient.getIndex (countryComboBox, currentLocation.getCountry ()));
						phoneTextBox.setValue (currentLocation.getPhone ());
						confirmPhoneTextBox.setValue (currentLocation.getPhone ());
						mobileTextBox.setValue (currentLocation.getMobile ());
						confirmMobileTextBox.setValue (currentLocation.getMobile ());
						secondaryPhoneTextBox.setValue (currentLocation.getOtherPhone ());
						confirmSecondaryPhoneTextBox.setValue (currentLocation.getOtherPhone ());
						secondaryMobileTextBox.setValue (currentLocation.getOtherMobile ());
						confirmSecondaryMobileTextBox.setValue (currentLocation.getOtherMobile ());
						emailTextBox.setValue (currentLocation.getEmail ());
					}
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

	public void clearUp ()
	{
		IRZClient.clearControls (flexTable);
	}

	public boolean validate ()
	{
		final StringBuilder errorMessage = new StringBuilder ();
		/* Validate mandatory fields */
		Widget[] mandatory = {locationIdTextBox, locationNameTextBox};
		for (int i = 0; i < mandatory.length; i++)
		{
			if (IRZClient.get (mandatory[i]).equals (""))
			{
				errorMessage.append (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
				break;
			}
		}
		/* Validate allowed data types */
		String str = IRZClient.get (locationIdTextBox);
		if (!str.equals ("") & !RegexUtil.isAlphaNumeric (str))
			errorMessage.append ("Site ID: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		str = IRZClient.get (locationNameTextBox);
		if (!str.equals ("") & !RegexUtil.isWord (str))
			errorMessage.append ("Site Name: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		str = IRZClient.get (locationTypeTextBox);
		if (!str.equals ("") & !RegexUtil.isWord (str))
			errorMessage.append ("Site Type: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		str = IRZClient.get (phoneTextBox);
		if (!str.equals ("") & !RegexUtil.isContactNumber (str))
			errorMessage.append ("Primary Number: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		str = IRZClient.get (mobileTextBox);
		if (!str.equals ("") & !RegexUtil.isContactNumber (str))
			errorMessage.append ("Secondary Number: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		str = IRZClient.get (secondaryPhoneTextBox);
		if (!str.equals ("") & !RegexUtil.isContactNumber (str))
			errorMessage.append ("Tertiary Number: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		str = IRZClient.get (secondaryMobileTextBox);
		if (!str.equals ("") & !RegexUtil.isContactNumber (str))
			errorMessage.append ("Quartenary Number: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		str = IRZClient.get (cityTextBox);
		if (!str.equals ("") & !RegexUtil.isWord (str))
			errorMessage.append ("Other City: " + CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n");
		if (!IRZClient.get (mobileTextBox).equals (IRZClient.get (confirmMobileTextBox)))
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.DATA_MISMATCH_ERROR) + " (Primary Number)\n");
		if (!IRZClient.get (phoneTextBox).equals (IRZClient.get (confirmPhoneTextBox)))
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.DATA_MISMATCH_ERROR) + " (Secondary Number)\n");
		if (!IRZClient.get (secondaryMobileTextBox).equals (IRZClient.get (confirmSecondaryMobileTextBox)))
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.DATA_MISMATCH_ERROR) + " (Tertiary Number)\n");
		if (!IRZClient.get (secondaryPhoneTextBox).equals (IRZClient.get (confirmSecondaryPhoneTextBox)))
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.DATA_MISMATCH_ERROR) + " (Quatenary Number)\n");
		if (errorMessage.length () > 0)
		{
			Window.alert (errorMessage.toString ());
			load (false);
			return false;
		}
		return true;
	}

	public void saveData ()
	{
		if (validate ())
		{
			try
			{
				String locationId = IRZClient.get (locationIdTextBox).toUpperCase ();
				String locationName = IRZClient.get (locationNameTextBox).toUpperCase ();
				currentLocation = new Location (locationId, locationName, "");
				setCurrent ();
				service.saveLocation (currentLocation, new AsyncCallback<Boolean> ()
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
				load (false);
			}
		}
	}

	public void updateData ()
	{
		if (validate ())
		{
			try
			{
				setCurrent ();
				service.updateLocation (currentLocation, new AsyncCallback<Boolean> ()
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
				service.deleteLocation (currentLocation, new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (result)
						{
							Window.alert (CustomMessage.getInfoMessage (InfoType.DELETED));
							clearUp ();
							locationNamesListBox.removeItem (IRZClient.getIndex (locationNamesListBox, currentLocation.getLocationId ()));
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
								locationTypesComboBox.setEnabled (rights.getAccess (AccessType.SELECT));
								locationNamesListBox.setEnabled (rights.getAccess (AccessType.SELECT));
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
						load (false);
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
			load (false);
		}
	}

	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == createButton)
		{
			if (createButton.isDown ())
				clearUp ();
			locationIdTextBox.setEnabled (createButton.isDown ());
			locationTypesComboBox.setEnabled (!createButton.isDown ());
			locationNamesListBox.setEnabled (!createButton.isDown ());
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
		if (sender == locationTypesComboBox)
		{
			try
			{
				String locationType = IRZClient.get (locationTypesComboBox);
				service.findLocationsByType (locationType, new AsyncCallback<Location[]> ()
				{
					public void onSuccess (Location[] result)
					{
						locationNamesListBox.clear ();
						for (Location s : result)
							locationNamesListBox.addItem (s.getLocationName (), s.getLocationId ());
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
		else if (sender == locationNamesListBox)
		{
			fillData ();
		}
		else if (sender == locationTypeComboBox)
		{
			locationTypeTextBox.setEnabled (IRZClient.get (locationTypeComboBox).equals ("OTHER"));
			load (false);
		}
		else if (sender == cityComboBox)
		{
			cityTextBox.setEnabled (IRZClient.get (cityComboBox).equals ("OTHER"));
			load (false);
		}
	}
}
