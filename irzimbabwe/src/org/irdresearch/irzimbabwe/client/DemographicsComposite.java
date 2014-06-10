
package org.irdresearch.irzimbabwe.client;

import java.util.ArrayList;
import java.util.Date;
import org.irdresearch.irzimbabwe.shared.AccessType;
import org.irdresearch.irzimbabwe.shared.CustomMessage;
import org.irdresearch.irzimbabwe.shared.DateTimeUtil;
import org.irdresearch.irzimbabwe.shared.ErrorType;
import org.irdresearch.irzimbabwe.shared.InfoType;
import org.irdresearch.irzimbabwe.shared.IRZ;
import org.irdresearch.irzimbabwe.shared.UserRightsUtil;
import org.irdresearch.irzimbabwe.shared.model.Encounter;
import org.irdresearch.irzimbabwe.shared.model.EncounterId;
import org.irdresearch.irzimbabwe.shared.model.EncounterResults;
import org.irdresearch.irzimbabwe.shared.model.EncounterResultsId;
import org.irdresearch.irzimbabwe.shared.model.Person;
import org.irdresearch.irzimbabwe.shared.model.User;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Client's Initial Demographics form
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class DemographicsComposite extends Composite implements ClickHandler, ValueChangeHandler<Integer>
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static final String			formName						= "DEMOG";

	private UserRightsUtil				rights							= new UserRightsUtil ();
	private boolean						valid;

	private FlexTable					flexTable						= new FlexTable ();
	private FlexTable					topFlexTable					= new FlexTable ();
	private FlexTable					rightFlexTable					= new FlexTable ();

	private Grid						grid							= new Grid (1, 2);
	private HorizontalPanel				genderHorizontalPanel			= new HorizontalPanel ();

	private Button						saveButton						= new Button ("Save");
	private Button						closeButton						= new Button ("Close");

	private Label						lblClientsInitialDemographics	= new Label (
																				"Client's Initial Demographics Form");
	private Label						lblClientsId					= new Label ("Client's ID:");
	private Label						lblFirstName					= new Label ("First Name:");
	private Label						lblLastName						= new Label ("Last Name:");
	private Label						lblGender						= new Label ("Gender:");
	private Label						lblAge							= new Label ("Age:");
	private Label						lblDateOfBirth					= new Label ("Date of Birth:");

	private TextBox						clientIdTextBox					= new TextBox ();
	private TextBox						firstNameTextBox				= new TextBox ();
	private TextBox						lastNameTextBox					= new TextBox ();

	private IntegerBox					ageIntegerBox					= new IntegerBox ();

	private RadioButton					maleRadioButton					= new RadioButton ("Gender", "Male");
	private RadioButton					femaleRadioButton				= new RadioButton ("Gender", "Female");

	private DateBox						dobDateBox						= new DateBox ();

	public DemographicsComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		lblClientsInitialDemographics.setWordWrap (false);
		lblClientsInitialDemographics.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblClientsInitialDemographics);
		topFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget (1, 0, rightFlexTable);
		rightFlexTable.setWidget (0, 0, lblClientsId);
		clientIdTextBox.setMaxLength (12);
		clientIdTextBox.setVisibleLength (12);
		rightFlexTable.setWidget (0, 1, clientIdTextBox);
		rightFlexTable.setWidget (1, 0, lblFirstName);
		firstNameTextBox.setMaxLength (50);
		firstNameTextBox.setName ("person;first_name");
		rightFlexTable.setWidget (1, 1, firstNameTextBox);
		rightFlexTable.setWidget (2, 0, lblLastName);
		lastNameTextBox.setMaxLength (50);
		lastNameTextBox.setName ("person;last_name");
		rightFlexTable.setWidget (2, 1, lastNameTextBox);
		rightFlexTable.setWidget (3, 0, lblGender);
		rightFlexTable.setWidget (3, 1, genderHorizontalPanel);
		maleRadioButton.setValue (true);
		genderHorizontalPanel.add (maleRadioButton);
		genderHorizontalPanel.add (femaleRadioButton);
		rightFlexTable.setWidget (4, 0, lblAge);
		ageIntegerBox.setVisibleLength (3);
		ageIntegerBox.setMaxLength (3);
		rightFlexTable.setWidget (4, 1, ageIntegerBox);
		rightFlexTable.setWidget (5, 0, lblDateOfBirth);
		dobDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yyyy-MM-dd")));
		rightFlexTable.setWidget (5, 1, dobDateBox);
		rightFlexTable.setWidget (6, 1, grid);
		grid.setSize ("100%", "100%");
		saveButton.setEnabled (false);
		grid.setWidget (0, 0, saveButton);
		grid.setWidget (0, 1, closeButton);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (0, 0, HasVerticalAlignment.ALIGN_TOP);

		ageIntegerBox.addValueChangeHandler (this);
		saveButton.addClickHandler (this);
		closeButton.addClickHandler (this);

		IRZClient.refresh (flexTable);
		setRights (formName);
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

	/**
	 * Does form-specific cleaning up of widgets. Used if the form has some
	 * specific default values to load on reset.
	 */
	public void clearUp ()
	{
		IRZClient.clearControls (flexTable);
		ageIntegerBox.setText ("");
		dobDateBox.getTextBox ().setText ("");
	}

	/**
	 * To validate data in widgets before performing DML operations
	 * 
	 * @return
	 */
	public boolean validate ()
	{
		valid = true;
		StringBuilder errorMessage = new StringBuilder ();
		/* Validate mandatory fields */
		Widget[] mandatory = {clientIdTextBox, firstNameTextBox, lastNameTextBox, ageIntegerBox,
				dobDateBox.getTextBox ()};
		int i;
		for (i = 0; i < mandatory.length; i++)
		{
			if (IRZClient.get (mandatory[i]).equals (""))
			{
				errorMessage.append (CustomMessage.getErrorMessage (ErrorType.EMPTY_DATA_ERROR) + "\n");
				break;
			}
		}
		if (errorMessage.length () == 0)
		{
			if (ageIntegerBox.getValue () < 0 || ageIntegerBox.getValue () > 127
					|| DateTimeUtil.isFutureDate (dobDateBox.getValue ()))
			{
				errorMessage.append ("Age/Date of Birth: " + CustomMessage.getErrorMessage

				(ErrorType.INVALID_DATA_ERROR) + "\n");
			}
		}
		valid = (errorMessage.length () == 0);
		if (!valid)
		{
			Window.alert (errorMessage.toString ());
			load (false);
		}
		return valid;
	}

	public void saveData ()
	{
		Date enteredDate = new Date ();
		int eId = 0;
		String clientId = IRZClient.get (clientIdTextBox).toUpperCase ();
		String pid2 = IRZ.getCurrentUserName ();
		String firstName = IRZClient.get (firstNameTextBox).toUpperCase ();
		String lastName = IRZClient.get (lastNameTextBox).toUpperCase ();
		String age = IRZClient.get (ageIntegerBox);
		String gender = maleRadioButton.getValue () ? "M" : "F";
		String dob = DateTimeUtil.getFormattedDate (dobDateBox.getValue (),

		DateTimeUtil.SQL_DATETIME);

		EncounterId encounterId = new EncounterId (0, clientId, pid2, formName);
		Encounter encounter = new Encounter (encounterId, "");
		encounter.setLocationId (IRZ.getCurrentLocation ());
		encounter.setDateEntered (enteredDate);
		encounter.setDateStart (new Date ());
		encounter.setDateEnd (new Date ());
		ArrayList<EncounterResults> encounterResults = new ArrayList<EncounterResults>

		();
		encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,

		"F_NAME"), firstName));
		encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,

		"L_NAME"), lastName));
		encounterResults.add (new EncounterResults (new EncounterResultsId (eId,

		clientId, pid2, formName, "AGE"), age));
		encounterResults.add (new EncounterResults (new EncounterResultsId (eId, clientId, pid2, formName,

		"GENDER"), gender));
		encounterResults.add (new EncounterResults (new EncounterResultsId (eId,

		clientId, pid2, formName, "DOB"), dob));
		Person person = new Person (clientId, firstName);
		person.setLastName (lastName);
		person.setApproximateAge (ageIntegerBox.getValue ());
		person.setDob (dobDateBox.getValue ());
		person.setGender (gender);
		person.setPreferredLanguage ("ENG");
		service.saveClientDemographics (person, encounter, encounterResults.toArray (new

		EncounterResults[] {}), new AsyncCallback<String> ()
		{
			public void onSuccess (String result)
			{
				if (result.equals ("SUCCESS"))
				{
					Window.alert(CustomMessage.getInfoMessage (InfoType.INSERTED));
					clearUp ();
					load (false);
				}
				else
				{
					Window.alert

					(CustomMessage.getErrorMessage (ErrorType.INSERT_ERROR) + "\nDetails: " + result);
					load (false);
				}
			}

			public void onFailure (Throwable caught)
			{
				caught.printStackTrace ();
				load (false);
			}
		});
	}

	/**
	 * Gets rights of current user role for given parameter (defined in
	 * metadata)
	 * 
	 * @param menuName
	 */
	public void setRights (String menuName)
	{
		try
		{
			load (true);
			service.getUserRgihts (IRZ.getCurrentUserName (), IRZ.getCurrentRole (),

			menuName, new AsyncCallback<Boolean[]> ()
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
								rights.setRoleRights(IRZ.getCurrentRole (), userRights);
								boolean hasAccess =

								rights.getAccess (AccessType.INSERT) |

								rights.getAccess (AccessType.UPDATE) |

								rights.getAccess (AccessType.DELETE) |

								rights.getAccess (AccessType.SELECT);
								if (!hasAccess)
								{
									Window.alert

									(CustomMessage.getErrorMessage (ErrorType.DATA_ACCESS_ERROR));

									MainMenuComposite.clear ();
								}
								saveButton.setEnabled

								(rights.getAccess (AccessType.INSERT));
								load (false);
							}

							public void onFailure (Throwable

							caught)
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

		if (sender == saveButton)
		{
			try
			{
				if (validate ())
				{
					service.exists ("patient", "patient_id='" + IRZClient.get (clientIdTextBox) + "'",
							new AsyncCallback<Boolean> ()
							{
								public void onSuccess (Boolean result)
								{
									if (result)
									{
										try
										{
											service.exists ("person", "pid='" + IRZClient.get (clientIdTextBox) + "'",
													new AsyncCallback<Boolean> ()
													{
														public void onSuccess (Boolean result)
														{
															if (result)
															{
																boolean answer = Window.confirm ("Client Information Already exists! Are you sure you want to update?");
																if (!answer)
																{
																	load (false);
																	return;
																}
															}
															saveData ();
															clearUp ();
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
										Window.alert (CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND)
												+ "\nPlease make sure you have entered correct Client ID.");
										load (false);
									}
								}

								public void onFailure (Throwable caught)
								{
									load (false);
								}
							});

				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				load (false);
			}
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	@SuppressWarnings("deprecation")
	public void onValueChange (ValueChangeEvent<Integer> event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender == ageIntegerBox)
		{
			try
			{
				Integer age = ageIntegerBox.getValueOrThrow ();
				Date approxDob = new Date ();
				approxDob.setDate (1);
				approxDob.setMonth (0);
				approxDob.setYear (approxDob.getYear () - age);
				dobDateBox.setValue (approxDob);
			}
			catch (Exception e)
			{
				ageIntegerBox.setText ("0");
				ageIntegerBox.setFocus (true);
			}
		}
	}
}
