
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.Definition;
import org.irdresearch.irzimbabwe.shared.model.DefinitionId;
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

public class DefinitionComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service					= GWT.create (ServerService.class);
	private static final String			menuName				= "DEFINITION";
	private static final String			tableName				= "definition";

	private UserRightsUtil				rights					= new UserRightsUtil ();
	private boolean						valid;
	private Definition					current;

	private FlexTable					flexTable				= new FlexTable ();
	private FlexTable					topFlexTable			= new FlexTable ();
	private FlexTable					leftFlexTable			= new FlexTable ();
	private FlexTable					rightFlexTable			= new FlexTable ();
	private Grid						grid					= new Grid (1, 3);

	private ToggleButton				createButton			= new ToggleButton ("Create");
	private Button						saveButton				= new Button ("Save");
	private Button						deleteButton			= new Button ("Delete");
	private Button						closeButton				= new Button ("Close");

	private Label						label					= new Label (IRZ.getProjectTitle () + " Definitions");
	private Label						lblKey					= new Label ("Key:");
	private Label						lblValue				= new Label ("Value:");

	private TextBox						keyTextBox				= new TextBox ();
	private TextBox						valueTextBox			= new TextBox ();

	private ListBox						definitionKeysListBox	= new ListBox ();
	private ListBox						definitionTypeComboBox	= new ListBox ();

	public DefinitionComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 1, topFlexTable);
		label.setWordWrap (false);
		label.setStyleName ("title");
		topFlexTable.setWidget (0, 0, label);
		flexTable.setWidget (1, 0, leftFlexTable);
		definitionTypeComboBox.setEnabled (false);
		leftFlexTable.setWidget (0, 0, definitionTypeComboBox);
		definitionKeysListBox.setEnabled (false);
		leftFlexTable.setWidget (1, 0, definitionKeysListBox);
		definitionKeysListBox.setVisibleItemCount (5);
		flexTable.setWidget (1, 1, rightFlexTable);
		lblKey.setWordWrap (false);
		rightFlexTable.setWidget (0, 0, lblKey);
		keyTextBox.setEnabled (false);
		keyTextBox.setName ("definition;definition_key");
		rightFlexTable.setWidget (0, 1, keyTextBox);
		lblValue.setWordWrap (false);
		rightFlexTable.setWidget (1, 0, lblValue);
		valueTextBox.setName ("definition;definition_value");
		rightFlexTable.setWidget (1, 1, valueTextBox);
		createButton.setEnabled (false);
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
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);

		createButton.addClickHandler (this);
		saveButton.addClickHandler (this);
		deleteButton.addClickHandler (this);
		closeButton.addClickHandler (this);
		definitionTypeComboBox.addChangeHandler (this);
		definitionKeysListBox.addChangeHandler (this);

		IRZClient.refresh (flexTable);
		try
		{
			service.getColumnData ("definition_type", "definition_type", "", new AsyncCallback<String[]> ()
			{
				@Override
				public void onSuccess (String[] result)
				{
					definitionTypeComboBox.clear ();
					for (String s : result)
						definitionTypeComboBox.addItem (s);
					definitionTypeComboBox.setSelectedIndex (-1);
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
		current.setDefinitionValue (IRZClient.get (valueTextBox).toUpperCase ());
	}

	public void fillData ()
	{
		try
		{
			service.findDefinition (IRZClient.get (definitionTypeComboBox), IRZClient.get (definitionKeysListBox), new AsyncCallback<Definition> ()
			{
				public void onSuccess (Definition result)
				{
					current = result;
					keyTextBox.setValue (result.getId ().getDefinitionKey ().toUpperCase ());
					valueTextBox.setValue (result.getDefinitionValue ().toUpperCase ());
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

	public void fillList ()
	{
		try
		{
			service.getColumnData (tableName, "definition_key", "definition_type='" + IRZClient.get (definitionTypeComboBox) + "'", new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					definitionKeysListBox.clear ();
					for (String s : result)
						definitionKeysListBox.addItem (s);
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
		if (IRZClient.get (keyTextBox).equals ("") || IRZClient.get (valueTextBox).equals (""))
		{
			errorMessage.append (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
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
				service.exists (tableName, "definition_type='" + IRZClient.get (definitionTypeComboBox) + "' and definition_key='" + IRZClient.get (keyTextBox) + "'", new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (!result)
						{
							current = new Definition ();
							DefinitionId id = new DefinitionId (IRZClient.get (definitionTypeComboBox), IRZClient.get (keyTextBox).toUpperCase ());
							current.setId (id);
							setCurrent ();
							try
							{
								service.saveDefinition (current, new AsyncCallback<Boolean> ()
								{
									public void onSuccess (Boolean result)
									{
										if (result)
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
			}
			load (false);
		}
	}

	public void updateData ()
	{
		if (validate ())
		{
			setCurrent ();
			try
			{
				service.updateDefinition (current, new AsyncCallback<Boolean> ()
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
			}
		}
	}

	public void deleteData ()
	{
		if (validate ())
		{
			try
			{
				service.deleteDefinition (current, new AsyncCallback<Boolean> ()
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
					definitionTypeComboBox.setEnabled (rights.getAccess (AccessType.SELECT));
					definitionKeysListBox.setEnabled (rights.getAccess (AccessType.SELECT));
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

	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == createButton)
		{
			if (createButton.isDown ())
				clearUp ();
			definitionKeysListBox.setEnabled (!createButton.isDown ());
			keyTextBox.setEnabled (createButton.isDown ());
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
		if (sender == definitionKeysListBox)
		{
			fillData ();
		}
		else if (sender == definitionTypeComboBox)
		{
			fillList ();
		}
	}
}
