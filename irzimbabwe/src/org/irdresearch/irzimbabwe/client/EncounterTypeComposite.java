
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
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

public class EncounterTypeComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service					= GWT.create (ServerService.class);
	private static final String			menuName				= "ENCOUNTER";
	private static final String			tableName				= "encounter_type";

	private UserRightsUtil				rights					= new UserRightsUtil ();
	private boolean						valid;
	private EncounterType				current;

	private FlexTable					flexTable				= new FlexTable ();
	private FlexTable					topFlexTable			= new FlexTable ();
	private FlexTable					leftFlexTable			= new FlexTable ();
	private FlexTable					rightFlexTable			= new FlexTable ();
	private Grid						grid					= new Grid (1, 3);

	private ToggleButton				createButton			= new ToggleButton ("Create");
	private Button						saveButton				= new Button ("Save");
	private Button						deleteButton			= new Button ("Delete");
	private Button						closeButton				= new Button ("Close");

	private Label						lblType					= new Label ("Type:");
	private Label						label					= new Label (IRZ.getProjectTitle () + " Encounter Types");
	private Label						lblKey					= new Label ("Encounter Type:");
	private Label						lblValue				= new Label ("Description:");

	private TextBox						encounterTypeTextBox	= new TextBox ();
	private TextBox						descriptionTextBox		= new TextBox ();

	private ListBox						encounterTypeListBox	= new ListBox ();

	public EncounterTypeComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 1, topFlexTable);
		label.setWordWrap (false);
		label.setStyleName ("title");
		topFlexTable.setWidget (0, 0, label);
		flexTable.setWidget (1, 0, leftFlexTable);
		lblType.setWordWrap (false);
		encounterTypeListBox.setEnabled (false);
		leftFlexTable.setWidget (1, 0, encounterTypeListBox);
		encounterTypeListBox.setVisibleItemCount (5);
		encounterTypeListBox.setTitle ("This list contains existing Encounter Types. Selecting anyone fills the details in right panel.");
		flexTable.setWidget (1, 1, rightFlexTable);
		lblKey.setWordWrap (false);
		rightFlexTable.setWidget (0, 0, lblKey);
		encounterTypeTextBox.setVisibleLength (10);
		encounterTypeTextBox.setEnabled (false);
		encounterTypeTextBox.setName ("encounter_type;encounter_type");
		encounterTypeTextBox.setTitle ("Here goes the Form name, used in the program.");
		rightFlexTable.setWidget (0, 1, encounterTypeTextBox);
		lblValue.setWordWrap (false);
		rightFlexTable.setWidget (1, 0, lblValue);
		descriptionTextBox.setName ("encounter_type;description");
		rightFlexTable.setWidget (1, 1, descriptionTextBox);
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
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);

		createButton.addClickHandler (this);
		saveButton.addClickHandler (this);
		deleteButton.addClickHandler (this);
		closeButton.addClickHandler (this);
		encounterTypeListBox.addChangeHandler (this);

		IRZClient.refresh (flexTable);
		try
		{
			service.getColumnData ("encounter_type", "encounter_type", "", new AsyncCallback<String[]> ()
			{
				@Override
				public void onSuccess (String[] result)
				{
					encounterTypeListBox.clear ();
					for (String s : result)
						encounterTypeListBox.addItem (s);
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
		current.setDescription (IRZClient.get (descriptionTextBox).toUpperCase ());
	}

	public void fillData ()
	{
		try
		{
			service.findEncounterType (IRZClient.get (encounterTypeListBox), new AsyncCallback<EncounterType> ()
			{
				public void onSuccess (EncounterType result)
				{
					current = result;
					encounterTypeTextBox.setValue (result.getEncounterType ());
					descriptionTextBox.setValue (result.getDescription ());
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

	public void clearUp ()
	{
		IRZClient.clearControls (rightFlexTable);
	}

	public boolean validate ()
	{
		final StringBuilder errorMessage = new StringBuilder ();
		valid = true;
		/* Validate mandatory fields */
		if (IRZClient.get (encounterTypeTextBox).equals ("") || IRZClient.get (descriptionTextBox).equals (""))
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
				service.exists (tableName, "encounter_type='" + IRZClient.get (encounterTypeListBox) + "'", new AsyncCallback<Boolean> ()
				{
					public void onSuccess (Boolean result)
					{
						if (!result)
						{
							current = new EncounterType (IRZClient.get (encounterTypeTextBox).toUpperCase ());
							setCurrent ();
							try
							{
								service.saveEncounterType (current, new AsyncCallback<Boolean> ()
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
				service.updateEncounterType (current, new AsyncCallback<Boolean> ()
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
				service.deleteEncounterType (current, new AsyncCallback<Boolean> ()
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
					encounterTypeListBox.setEnabled (rights.getAccess (AccessType.SELECT));
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
			encounterTypeListBox.setEnabled (!createButton.isDown ());
			encounterTypeTextBox.setEnabled (createButton.isDown ());
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
		if (sender == encounterTypeListBox)
		{
			fillData ();
		}
	}
}
