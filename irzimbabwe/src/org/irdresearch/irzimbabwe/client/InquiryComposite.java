
package org.irdresearch.irzimbabwe.client;

import org.irdresearch.irzimbabwe.shared.DateTimeUtil;
import org.irdresearch.irzimbabwe.shared.model.Person;
import org.irdresearch.irzimbabwe.shared.model.Visit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IntegerBox;

public class InquiryComposite extends Composite implements IForm, ClickHandler
{
	private static ServerServiceAsync	service					= GWT.create (ServerService.class);
	private static final String			formName				= "PATIENT";
	private static final String			defaultImage			= "images/no_image.gif";

	private Person						current					= new Person ();

	private VerticalPanel				mainVerticalPanel		= new VerticalPanel ();

	private FlexTable					searchFlexTable			= new FlexTable ();

	private Button						searchButton			= new Button ("Search");
	private Button						waitingListButton		= new Button ("Waiting List");

	private Label						lblEnterIdOr			= new Label ("ID or Name:");
	private Label						lblAge					= new Label ("Age:");

	private TextBox						patientNameOrIdTextBox	= new TextBox ();
	private TextArea					patientInfoTextArea		= new TextArea ();
	private IntegerBox					ageIntegerBox			= new IntegerBox ();

	private Image						patientPictureImage;

	private ListBox						patientsListBox			= new ListBox ();

	public InquiryComposite ()
	{
		initWidget (mainVerticalPanel);
		mainVerticalPanel.setSize ("200px", "50%");
		mainVerticalPanel.add (searchFlexTable);
		lblEnterIdOr.setWordWrap (false);
		searchFlexTable.setWidget (0, 0, lblEnterIdOr);
		searchFlexTable.setWidget (0, 1, patientNameOrIdTextBox);
		patientNameOrIdTextBox.setVisibleLength (15);
		patientNameOrIdTextBox.setMaxLength (15);
		patientNameOrIdTextBox.setWidth ("100%");
		searchFlexTable.setWidget (1, 0, lblAge);
		ageIntegerBox.setMaxLength (2);
		ageIntegerBox.setVisibleLength (2);
		searchFlexTable.setWidget (1, 1, ageIntegerBox);
		mainVerticalPanel.add (searchButton);
		searchButton.setWidth ("100%");
		mainVerticalPanel.add (waitingListButton);
		waitingListButton.setWidth ("100%");
		mainVerticalPanel.add (patientsListBox);
		patientsListBox.setWidth ("100%");
		patientsListBox.setVisibleItemCount (5);
		patientInfoTextArea.setVisibleLines (6);
		patientInfoTextArea.setDirectionEstimator (false);
		patientInfoTextArea.setReadOnly (true);
		mainVerticalPanel.add (patientInfoTextArea);
		patientInfoTextArea.setSize ("190px", "");
		patientPictureImage = new Image (defaultImage);
		patientPictureImage.setVisible (false);
		mainVerticalPanel.add (patientPictureImage);
		mainVerticalPanel.setCellHorizontalAlignment (patientPictureImage, HasHorizontalAlignment.ALIGN_CENTER);
		mainVerticalPanel.setCellVerticalAlignment (patientPictureImage, HasVerticalAlignment.ALIGN_MIDDLE);
		patientPictureImage.setSize ("150px", "150px");

		patientNameOrIdTextBox.addClickHandler (this);
		searchButton.addClickHandler (this);
		waitingListButton.addClickHandler (this);
		patientsListBox.addClickHandler (this);
		setRights (formName);
	}

	public void clearUp ()
	{
		patientsListBox.clear ();
		patientPictureImage.setUrl (defaultImage);
		patientInfoTextArea.setText ("");
	}

	public boolean validate ()
	{
		// Not implemented
		return false;
	}

	public void saveData ()
	{
		// Not implemented
	}

	public void updateData ()
	{
		// Not implemented
	}

	public void deleteData ()
	{
		// Not implemented
	}

	public void fillData ()
	{
		final StringBuilder patientInfo = new StringBuilder ();
		patientInfo.append ("ID: " + current.getPid () + "\n");
		patientInfo.append ("Name: " + current.getFirstName () + " " + current.getLastName () + "\n");
		patientInfo.append ("Age: " + current.getApproximateAge () + "\n");
		try
		{
			service.findVisit (current.getPid (), new AsyncCallback<Visit> ()
			{
				public void onSuccess (Visit result)
				{
					patientInfo.append ("Visit Purpose: " + result.getVisitPurpose () + "\n");
					patientInfo.append ("Visit Date: " + DateTimeUtil.getFormattedDate (result.getVisitDate (), DateTimeUtil.SQL_DATE) + "\n");
					patientInfoTextArea.setText (patientInfo.toString ());
				}

				public void onFailure (Throwable caught)
				{
					patientInfoTextArea.setText (patientInfo.toString ());
				}
			});
		}
		catch (Exception e)
		{
		}
	}

	public void setCurrent ()
	{
		// Not implemented
	}

	public void setRights (String menuName)
	{
		searchButton.setEnabled (true);
	}

	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender == patientNameOrIdTextBox)
		{
			clearUp ();
		}
		else if (sender == searchButton)
		{
			String text = IRZClient.get (patientNameOrIdTextBox);
			if (text.length () < 3)
			{
				Window.alert ("Please first enter at-least 3-character value as search criteria in ID/Name box.");
				return;
			}
			try
			{
				String additionalCondition = "";
				if (!ageIntegerBox.getText ().equals (""))
					additionalCondition = " and approximate_age = " + ageIntegerBox.getText ();
				service.getColumnData ("person", "pid", "concat(pid, first_name, approximate_age) like '%" + text.toUpperCase () + "%'" + additionalCondition, new AsyncCallback<String[]> ()
				{
					public void onSuccess (String[] result)
					{
						patientsListBox.clear ();
						for (String s : result)
							patientsListBox.addItem (s);
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
		else if (sender == waitingListButton)
		{
			patientsListBox.clear ();
			try
			{
				service.getColumnData ("visit", "concat(patient_id, '-', visit_purpose)", "patient_id not in (select pid from person) and date(visit_date) = date(curdate())",
						new AsyncCallback<String[]> ()
						{
							public void onSuccess (String[] result)
							{
								patientsListBox.clear ();
								for (String s : result)
									patientsListBox.addItem (s);
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
		else if (sender == patientsListBox)
		{
			try
			{
				patientInfoTextArea.setText (IRZClient.get (patientsListBox));
				service.findPerson (IRZClient.get (patientsListBox), new AsyncCallback<Person> ()
				{
					public void onSuccess (Person result)
					{
						if (current != null)
						{
							current = result;
							fillData ();
						}
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
	}
}
