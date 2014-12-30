
package org.irdresearch.irzimbabwe.client;

import java.util.Date;
import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.Encounter;
import org.irdresearch.irzimbabwe.shared.model.EncounterElement;
import org.irdresearch.irzimbabwe.shared.model.EncounterId;
import org.irdresearch.irzimbabwe.shared.model.EncounterResults;
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

public class EncounterComposite extends Composite implements IForm, ClickHandler, ChangeHandler, ValueChangeHandler<String>
{
	private static ServerServiceAsync	service					= GWT.create (ServerService.class);
	private static final String			menuName				= "ENCOUNTER";

	private UserRightsUtil				rights					= new UserRightsUtil ();
	private boolean						valid;
	private Encounter					currentEncounter;
	private EncounterResults[]			currentResults;
	private Date						startedTimestamp;

	private FlexTable					flexTable				= new FlexTable ();
	private FlexTable					topFlexTable			= new FlexTable ();
	private FlexTable					rightFlexTable			= new FlexTable ();
	private FlexTable					bottomFlexTable			= new FlexTable ();
	private SimplePanel					resultsPanel			= new SimplePanel ();
	private VerticalPanel				searchVerticalPanel		= new VerticalPanel ();

	private Grid						grid					= new Grid (1, 3);
	private Grid						resultsGrid				= new Grid (1, 1);

	private ToggleButton				createButton			= new ToggleButton ("Create");
	private Button						saveButton				= new Button ("Save");
	private Button						deleteButton			= new Button ("Delete");
	private Button						closeButton				= new Button ("Close");

	private Label						label					= new Label (IRZ.getProjectTitle () + " Encounters");
	private Label						lblKey					= new Label ("Encounter Type:");
	private Label						lblPid					= new Label ("Patient ID:");
	private Label						lblPid_1				= new Label ("PID2:");
	private Label						lblEncounterId			= new Label ("Encounter ID:");
	private Label						lblLocationId			= new Label ("Location ID:");
	private Label						lblDateEntered			= new Label ("Date Entered:");
	private Label						pid1Label				= new Label ("");
	private Label						pid2Label				= new Label ("");

	private TextBox						pid1TextBox				= new TextBox ();
	private TextBox						pid2TextBox				= new TextBox ();

	private DateBox						dateEnteredDateBox		= new DateBox ();

	private ListBox						encounterTypeComboBox	= new ListBox ();
	private ListBox						encounterTypesComboBox	= new ListBox ();
	private ListBox						patientsListBox			= new ListBox ();
	private ListBox						pid2ListBox				= new ListBox ();
	private ListBox						encounterIdComboBox		= new ListBox ();
	private ListBox						locationIdComboBox		= new ListBox ();

	public EncounterComposite ()
	{
		pid2ListBox.setVisibleItemCount (15);
		patientsListBox.setVisibleItemCount (18);
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		label.setWordWrap (false);
		label.setStyleName ("title");
		topFlexTable.setWidget (0, 0, label);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.setWidget (1, 0, bottomFlexTable);
		bottomFlexTable.setWidget (0, 0, searchVerticalPanel);
		searchVerticalPanel.add (encounterTypesComboBox);
		encounterTypesComboBox.setWidth ("120px");
		searchVerticalPanel.add (pid2ListBox);
		pid2ListBox.setWidth ("120px");
		bottomFlexTable.setWidget (0, 1, patientsListBox);
		patientsListBox.setWidth ("120px");
		bottomFlexTable.setWidget (0, 2, rightFlexTable);
		rightFlexTable.setWidth ("100%");
		rightFlexTable.setWidget (0, 0, lblEncounterId);
		rightFlexTable.setWidget (0, 1, encounterIdComboBox);
		lblKey.setWordWrap (false);
		rightFlexTable.setWidget (1, 0, lblKey);
		encounterTypeComboBox.setEnabled (false);
		rightFlexTable.setWidget (1, 1, encounterTypeComboBox);
		rightFlexTable.setWidget (2, 0, lblPid);
		pid1TextBox.setEnabled (false);
		pid1TextBox.setVisibleLength (12);
		pid1TextBox.setName ("encounter;pid1");
		rightFlexTable.setWidget (2, 1, pid1TextBox);
		rightFlexTable.setWidget (3, 1, pid1Label);
		rightFlexTable.setWidget (4, 0, lblPid_1);
		pid2TextBox.setEnabled (false);
		pid2TextBox.setVisibleLength (12);
		pid2TextBox.setName ("encounter;pid2");
		rightFlexTable.setWidget (4, 1, pid2TextBox);
		rightFlexTable.setWidget (5, 1, pid2Label);
		rightFlexTable.setWidget (6, 0, lblLocationId);
		rightFlexTable.setWidget (6, 1, locationIdComboBox);
		rightFlexTable.setWidget (7, 0, lblDateEntered);
		dateEnteredDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("dd-MMM-yyyy")));
		rightFlexTable.setWidget (7, 1, dateEnteredDateBox);
		dateEnteredDateBox.setWidth ("75px");
		rightFlexTable.setWidget (8, 1, resultsPanel);
		resultsPanel.setWidget (resultsGrid);
		resultsGrid.setSize ("100%", "100%");
		createButton.setEnabled (false);
		rightFlexTable.setWidget (9, 0, createButton);
		rightFlexTable.setWidget (9, 1, grid);
		grid.setSize ("100%", "100%");
		saveButton.setEnabled (false);
		grid.setWidget (0, 0, saveButton);
		deleteButton.setEnabled (false);
		grid.setWidget (0, 1, deleteButton);
		grid.setWidget (0, 2, closeButton);
		bottomFlexTable.getCellFormatter ().setVerticalAlignment (0, 2, HasVerticalAlignment.ALIGN_TOP);
		bottomFlexTable.getCellFormatter ().setVerticalAlignment (0, 0, HasVerticalAlignment.ALIGN_TOP);
		bottomFlexTable.getCellFormatter ().setVerticalAlignment (0, 1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);

		createButton.addClickHandler (this);
		saveButton.addClickHandler (this);
		deleteButton.addClickHandler (this);
		closeButton.addClickHandler (this);
		encounterTypesComboBox.addChangeHandler (this);
		patientsListBox.addChangeHandler (this);
		pid2ListBox.addChangeHandler (this);
		encounterTypeComboBox.addChangeHandler (this);
		pid1TextBox.addValueChangeHandler (this);
		pid2TextBox.addValueChangeHandler (this);
		encounterIdComboBox.addChangeHandler (this);

		IRZClient.refresh (flexTable);
		try
		{
			service.getTableData ("location", new String[] {"location_name", "location_id"}, "", new AsyncCallback<String[][]> ()
			{
				public void onSuccess (String[][] result)
				{
					locationIdComboBox.clear ();
					for (int i = 0; i < result.length; i++)
						locationIdComboBox.addItem (result[i][0], result[i][1]);
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
			service.getColumnData ("encounter_type", "encounter_type", "", new AsyncCallback<String[]> ()
			{
				@Override
				public void onSuccess (String[] result)
				{
					encounterTypesComboBox.addItem ("");
					encounterTypeComboBox.addItem ("");
					for (String s : result)
					{
						encounterTypesComboBox.addItem (s, s);
						encounterTypeComboBox.addItem (s, s);
					}
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
		currentEncounter.setDateEntered (dateEnteredDateBox.getValue ());
		currentEncounter.setLocationId (IRZClient.get (locationIdComboBox));
		currentResults = new EncounterResults[resultsGrid.getRowCount ()];
		for (int i = 0; i < currentResults.length; i++)
		{
			EncounterResultsId id = new EncounterResultsId ();
			TextBox textBox = (TextBox) resultsGrid.getWidget (i, 1);
			id.setEId (currentEncounter.getId ().getEId ());
			id.setPid1 (currentEncounter.getId ().getPid1 ());
			id.setPid2 (currentEncounter.getId ().getPid2 ());
			id.setEncounterType (currentEncounter.getId ().getEncounterType ());
			id.setElement (textBox.getName ());
			currentResults[i] = new EncounterResults (id);
			currentResults[i].setValue (textBox.getText ().toUpperCase ());
		}
		currentEncounter.setDateEnd (new Date ());
	}

	public void fillData ()
	{
		try
		{
			int eId = Integer.parseInt (IRZClient.get (encounterIdComboBox));
			String pid1 = IRZClient.get (patientsListBox);
			String pid2 = IRZClient.get (pid2ListBox);
			String encounterType = IRZClient.get (encounterTypesComboBox);
			EncounterId encounterId = new EncounterId (eId, pid1, pid2, encounterType);
			service.findEncounter (encounterId, new AsyncCallback<Encounter> ()
			{
				public void onSuccess (Encounter result)
				{
					if (result == null)
					{
						Window.alert (CustomMessage.getErrorMessage (ErrorType.UNKNOWN_ERROR));
						load (false);
						return;
					}
					currentEncounter = result;
					encounterTypeComboBox.setSelectedIndex (IRZClient.getIndex (encounterTypeComboBox, currentEncounter.getId ().getEncounterType ()));
					pid1TextBox.setText (currentEncounter.getId ().getPid1 ());
					pid2TextBox.setText (currentEncounter.getId ().getPid2 ());
					locationIdComboBox.setSelectedIndex (IRZClient.getIndex (locationIdComboBox, currentEncounter.getLocationId ()));
					dateEnteredDateBox.setValue (currentEncounter.getDateEntered ());
					try
					{
						service.findEncounterResults (currentEncounter.getId (), new AsyncCallback<EncounterResults[]> ()
						{
							public void onSuccess (EncounterResults[] result)
							{
								resultsGrid = new Grid (result.length, 2);
								final Label[] labels = new Label[result.length];
								final TextBox[] textBoxes = new TextBox[result.length];
								for (int i = 0; i < result.length; i++)
								{
									try
									{
										final int cnt = i;
										final String element = result[i].getId ().getElement ();
										final String value = result[i].getValue ();
										textBoxes[cnt] = new TextBox ();
										textBoxes[cnt].setValue (element);
										textBoxes[cnt].setText (value);
										resultsGrid.setWidget (cnt, 1, textBoxes[cnt]);
										service.findEncounterElement (currentEncounter.getId ().getEncounterType (), result[i].getId ().getElement (), new AsyncCallback<EncounterElement> ()
										{
											public void onSuccess (EncounterElement result)
											{
												labels[cnt] = new Label (result.getDescription ());
												labels[cnt].setWordWrap (false);
												resultsGrid.setWidget (cnt, 0, labels[cnt]);
											}

											public void onFailure (Throwable caught)
											{
												// Not implemented
											}
										});
										resultsPanel.setWidget (resultsGrid);
										load (false);
									}
									catch (Exception e)
									{
										e.printStackTrace ();
										load (false);
									}
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
		startedTimestamp = new Date ();
	}

	public boolean validate ()
	{
		final StringBuilder errorMessage = new StringBuilder ();
		valid = true;
		/* Validate mandatory fields */
		if (IRZClient.get (pid1TextBox).equals ("") || IRZClient.get (pid2TextBox).equals ("") || IRZClient.get (locationIdComboBox).equals ("")
				|| IRZClient.get (dateEnteredDateBox.getTextBox ()).equals (""))
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
			try
			{
				EncounterId encounterId = new EncounterId ();
				encounterId.setEId (0);
				encounterId.setPid1 (IRZClient.get (pid1TextBox));
				encounterId.setPid2 (IRZClient.get (pid2TextBox));
				encounterId.setEncounterType (IRZClient.get (encounterTypeComboBox));
				currentEncounter = new Encounter (encounterId, IRZClient.get (locationIdComboBox));
				currentEncounter.setDateStart (startedTimestamp);
				setCurrent ();
				service.saveEncounterWithResults (currentEncounter, currentResults, new AsyncCallback<String> ()
				{
					public void onSuccess (String result)
					{
						if (result.equals ("SUCCESS"))
							Window.alert (CustomMessage.getInfoMessage (InfoType.INSERTED));
						else
							Window.alert (CustomMessage.getErrorMessage (ErrorType.INSERT_ERROR) + "\n" + result);
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

	public void updateData ()
	{
		if (validate ())
		{
			setCurrent ();
			try
			{
				service.updateEncounterWithResults (currentEncounter, currentResults, new AsyncCallback<Boolean> ()
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
				service.deleteEncounter (currentEncounter, new AsyncCallback<Boolean> ()
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
					encounterTypesComboBox.setEnabled (rights.getAccess (AccessType.SELECT));
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
			encounterTypesComboBox.setEnabled (!createButton.isDown ());
			patientsListBox.setEnabled (createButton.isDown ());
			pid2ListBox.setEnabled (createButton.isDown ());
			encounterTypeComboBox.setEnabled (createButton.isDown ());
			pid1TextBox.setEnabled (createButton.isDown ());
			pid2TextBox.setEnabled (createButton.isDown ());
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
				String encounterType = IRZClient.get (encounterTypeComboBox);
				service.findEncounterElements (encounterType, new AsyncCallback<EncounterElement[]> ()
				{
					public void onSuccess (EncounterElement[] result)
					{
						resultsGrid = new Grid (result.length, 2);
						Label[] labels = new Label[result.length];
						TextBox[] textBoxes = new TextBox[result.length];
						for (int i = 0; i < result.length; i++)
						{
							String element = result[i].getId ().getElement ();
							textBoxes[i] = new TextBox ();
							labels[i] = new Label (result[i].getDescription ());
							labels[i].setWordWrap (false);
							textBoxes[i].setName (element);
							textBoxes[i].setText ("");
							textBoxes[i].setTitle (result[i].getValidator ());
							resultsGrid.setWidget (i, 0, labels[i]);
							resultsGrid.setWidget (i, 1, textBoxes[i]);
						}
						resultsPanel.setWidget (resultsGrid);
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
		else if (sender == encounterTypesComboBox)
		{
			try
			{
				String query = "select distinct u.user_name, e.pid2 from encounter as e inner join user as u on e.pid2 = u.pid where e.encounter_type='" + IRZClient.get (encounterTypesComboBox) + "'";
				service.getTableData (query, new AsyncCallback<String[][]> ()
				{
					public void onSuccess (String[][] result)
					{
						pid2ListBox.clear ();
						for (int i = 0; i < result.length; i++)
							pid2ListBox.addItem (result[i][0], result[i][1]);
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
		else if (sender == pid2ListBox)
		{
			try
			{
				service.getColumnData ("encounter", "pid1", "encounter_type='" + IRZClient.get (encounterTypesComboBox) + "' and pid2='" + IRZClient.get (pid2ListBox) + "'",
						new AsyncCallback<String[]> ()
						{
							public void onSuccess (String[] result)
							{
								patientsListBox.clear ();
								for (int i = 0; i < result.length; i++)
									patientsListBox.addItem (result[i]);
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
		else if (sender == patientsListBox)
		{
			try
			{
				service.getColumnData ("encounter", "e_id",
						"encounter_type='" + IRZClient.get (encounterTypesComboBox) + "' and pid1='" + IRZClient.get (patientsListBox) + "' and pid2='" + IRZClient.get (pid2ListBox) + "'",
						new AsyncCallback<String[]> ()
						{
							public void onSuccess (String[] result)
							{
								encounterIdComboBox.clear ();
								encounterIdComboBox.addItem ("");
								for (String s : result)
									encounterIdComboBox.addItem (s);
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
		else if (sender == encounterIdComboBox)
		{
			fillData ();
		}
	}

	public void onValueChange (ValueChangeEvent<String> event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == pid1TextBox)
		{
			try
			{
				service.getObject ("person", "concat(first_name, ifnull(last_name, ''))", "pid='" + IRZClient.get (pid1TextBox) + "'", new AsyncCallback<String> ()
				{
					public void onSuccess (String result)
					{
						pid1Label.setText (result);
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
		else if (sender == pid2TextBox)
		{
			try
			{
				service.getObject ("person", "concat(first_name, ifnull(last_name, ''))", "pid='" + IRZClient.get (pid2TextBox) + "'", new AsyncCallback<String> ()
				{
					public void onSuccess (String result)
					{
						pid2Label.setText (result);
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
}
