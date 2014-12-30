
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.EncounterElement;
import org.irdresearch.irzimbabwe.shared.model.EncounterElementId;
import org.irdresearch.irzimbabwe.shared.model.EncounterType;
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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class EncounterElementComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service					= GWT.create (ServerService.class);
	private static final String			menuName				= "ENCOUNTER";
	private static final String			tableName				= "encounter_element";

	private UserRightsUtil				rights					= new UserRightsUtil ();
	private boolean						valid;
	private EncounterType				currentType;
	private EncounterElement			currentElement;

	private FlexTable					flexTable				= new FlexTable ();
	private FlexTable					topFlexTable			= new FlexTable ();
	private FlexTable					leftFlexTable			= new FlexTable ();
	private FlexTable					rightFlexTable			= new FlexTable ();
	private Grid						grid					= new Grid (1, 3);

	private ToggleButton				createButton			= new ToggleButton ("Create");
	private Button						saveButton				= new Button ("Save");
	private Button						deleteButton			= new Button ("Delete");
	private Button						closeButton				= new Button ("Close");

	private Label						lblTbControlEncounter	= new Label (IRZ.getProjectTitle () + " Encounter Elements");
	private Label						lblType					= new Label ("Type:");
	private Label						lblElement				= new Label ("Element:");
	private Label						lblEncounterElement		= new Label ("Encounter Element:");
	private Label						lblValidator			= new Label ("Validation Regex:");
	private Label						lblDescription			= new Label ("Description:");

	private TextBox						encounterElementTextBox	= new TextBox ();
	private TextBox						validatorTextBox		= new TextBox ();
	private TextBox						descriptionTextBox		= new TextBox ();

	private ListBox						encounterElementListBox	= new ListBox ();
	private ListBox						encounterTypeComboBox	= new ListBox ();

	public EncounterElementComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 1, topFlexTable);
		lblTbControlEncounter.setWordWrap (false);
		lblTbControlEncounter.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblTbControlEncounter);
		flexTable.setWidget (1, 0, leftFlexTable);
		lblType.setWordWrap (false);
		leftFlexTable.setWidget (0, 0, lblType);
		leftFlexTable.setWidget (0, 1, encounterTypeComboBox);
		encounterTypeComboBox.setTitle ("List of Encounter Types. Selecting anyone fills existing Elements in the Elements List Box.");
		lblElement.setWordWrap (false);
		leftFlexTable.setWidget (1, 0, lblElement);
		encounterElementListBox.setEnabled (false);
		leftFlexTable.setWidget (1, 1, encounterElementListBox);
		encounterElementListBox.setVisibleItemCount (10);
		encounterElementListBox.setTitle ("This list contains existing Encounter Elements defined for seleted Type. Selecting anyone fills the details in right panel.");
		leftFlexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.setWidget (1, 1, rightFlexTable);
		lblEncounterElement.setWordWrap (false);
		rightFlexTable.setWidget (0, 0, lblEncounterElement);
		encounterElementTextBox.setEnabled (false);
		encounterElementTextBox.setName ("encounter_element;element");
		rightFlexTable.setWidget (0, 1, encounterElementTextBox);
		lblDescription.setWordWrap (false);
		rightFlexTable.setWidget (1, 0, lblDescription);
		descriptionTextBox.setTitle ("Details of the Encounter Element.");
		descriptionTextBox.setVisibleLength (35);
		descriptionTextBox.setName ("encounter_element;description");
		rightFlexTable.setWidget (1, 1, descriptionTextBox);
		lblValidator.setWordWrap (false);
		rightFlexTable.setWidget (2, 0, lblValidator);
		validatorTextBox
				.setTitle ("Validation Regular Expression contains an expression to validate \"Value\" entered for the Element. E.g:\r\n- \"[YN]\" will accept only 'Y' and 'N'\r\n- \"[0-255]\" will accept a number between 0 and 255\r\n- \"USER_ROLE\" will accept \"keys\" defined for Type USER_ROLE in Definition table");
		validatorTextBox.setName ("encounter_element;validator");
		rightFlexTable.setWidget (2, 1, validatorTextBox);
		createButton.setEnabled (false);
		rightFlexTable.setWidget (3, 0, createButton);
		rightFlexTable.setWidget (3, 1, grid);
		grid.setSize ("100%", "100%");
		saveButton.setEnabled (false);
		grid.setWidget (0, 0, saveButton);
		deleteButton.setEnabled (false);
		grid.setWidget (0, 1, deleteButton);
		grid.setWidget (0, 2, closeButton);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);

		createButton.addClickHandler (this);
		saveButton.addClickHandler (this);
		deleteButton.addClickHandler (this);
		closeButton.addClickHandler (this);
		encounterTypeComboBox.addChangeHandler (this);
		encounterElementListBox.addChangeHandler (this);

		IRZClient.refresh (flexTable);
		try
		{
			service.getColumnData ("encounter_type", "encounter_type", "", new AsyncCallback<String[]> ()
			{
				@Override
				public void onSuccess (String[] result)
				{
					encounterTypeComboBox.clear ();
					encounterTypeComboBox.addItem ("");
					for (String s : result)
						encounterTypeComboBox.addItem (s);
					setRights (menuName);
				}

				@Override
				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
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
		currentElement.setDescription (IRZClient.get (descriptionTextBox).toUpperCase ());
		currentElement.setValidator (IRZClient.get (validatorTextBox));
	}

	public void fillData ()
	{
		try
		{
			service.findEncounterElement (currentType.getEncounterType (), IRZClient.get (encounterElementListBox), new AsyncCallback<EncounterElement> ()
			{
				public void onSuccess (EncounterElement result)
				{
					currentElement = result;
					encounterElementTextBox.setValue (result.getId ().getElement ());
					descriptionTextBox.setValue (result.getDescription ());
					validatorTextBox.setValue (result.getValidator ());
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

	public void clearUp ()
	{
		IRZClient.clearControls (rightFlexTable);
	}

	public boolean validate ()
	{
		final StringBuilder errorMessage = new StringBuilder ();
		valid = true;
		/* Validate mandatory fields */
		if (IRZClient.get (encounterElementTextBox).equals ("") || IRZClient.get (descriptionTextBox).equals (""))
		{
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		/* Validate patter of Regular expression */
		try
		{
			"".matches (IRZClient.get (validatorTextBox));
		}
		catch (Exception e)
		{
			errorMessage.append ("Validation Expression failed to parse, please double check the expression format\n");
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
			/* Validate uniqueness */
			try
			{
				service.exists (tableName, "encounter_type='" + IRZClient.get (encounterElementListBox) + "'", new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (!result)
						{
							EncounterElementId encounterElementId = new EncounterElementId (IRZClient.get (encounterTypeComboBox), IRZClient.get (encounterElementTextBox).toUpperCase ());
							currentElement = new EncounterElement (encounterElementId);
							setCurrent ();
							try
							{
								service.saveEncounterElement (currentElement, new AsyncCallback<Boolean> ()
								{
									public void onSuccess (Boolean result)
									{
										if (result)
										{
											Window.alert (CustomMessage.getInfoMessage (InfoType.INSERTED));
											encounterElementListBox.addItem (currentElement.getId ().getElement ());
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
						else
							Window.alert (CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR));
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
			setCurrent ();
			try
			{
				service.updateEncounterElement (currentElement, new AsyncCallback<Boolean> ()
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
				service.deleteEncounterElement (currentElement, new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (result)
						{
							Window.alert (CustomMessage.getInfoMessage (InfoType.DELETED));
							encounterElementListBox.removeItem (IRZClient.getIndex (encounterElementListBox, IRZClient.get (encounterElementTextBox)));
							clearUp ();
						}
						else
							Window.alert (CustomMessage.getErrorMessage (ErrorType.DELETE_ERROR));
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
					encounterElementListBox.setEnabled (rights.getAccess (AccessType.SELECT));
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

	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == createButton)
		{
			if (createButton.isDown ())
				clearUp ();
			encounterTypeComboBox.setEnabled (!createButton.isDown ());
			encounterElementListBox.setEnabled (!createButton.isDown ());
			encounterElementTextBox.setEnabled (createButton.isDown ());
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
			if (menuName.equals ("DEFINITION"))
				Window.alert ("Please refresh your browser if you have made any changes to this form.");
			MainMenuComposite.clear ();
		}
	}

	public void onChange (ChangeEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == encounterTypeComboBox)
		{
			try
			{
				encounterElementListBox.clear ();
				service.findEncounterType (IRZClient.get (encounterTypeComboBox), new AsyncCallback<EncounterType> ()
				{
					@Override
					public void onSuccess (EncounterType result)
					{
						try
						{
							currentType = result;
							service.getColumnData (tableName, "element", "encounter_type='" + currentType.getEncounterType () + "'", new AsyncCallback<String[]> ()
							{
								@Override
								public void onSuccess (String[] result)
								{
									for (String s : result)
										encounterElementListBox.addItem (s);
									load (false);
								}

								@Override
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
				load (false);
			}
		}
		else if (sender == encounterElementListBox)
		{
			fillData ();
		}
	}
}
