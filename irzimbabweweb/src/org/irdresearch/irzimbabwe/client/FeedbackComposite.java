/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.irzimbabwe.client;

import java.util.Date;
import org.irdresearch.irzimbabwe.shared.*;
import org.irdresearch.irzimbabwe.shared.model.Feedback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class FeedbackComposite extends Composite implements IForm, ClickHandler
{
	// RPC Service
	private static ServerServiceAsync	service					= GWT.create (ServerService.class);
	private static LoadingWidget		loading					= new LoadingWidget ();

	ListBox								feedbackTypeComboBox	= new ListBox ();
	RichTextArea						richTextArea			= new RichTextArea ();
	Button								submitButton			= new Button ("Submit");

	public FeedbackComposite ()
	{
		FlexTable flexTable = new FlexTable ();
		initWidget (flexTable);
		flexTable.setSize ("100%", "100%");
		FlexTable topFlexTable = new FlexTable ();
		flexTable.setWidget (0, 0, topFlexTable);
		Label feedbackTypeLabel = new Label ("Feedback Type:");
		topFlexTable.setWidget (0, 0, feedbackTypeLabel);
		feedbackTypeComboBox.addItem ("New Requirement");
		feedbackTypeComboBox.addItem ("Suggestion");
		feedbackTypeComboBox.addItem ("Error/Bug");
		feedbackTypeComboBox.addItem ("Other");
		topFlexTable.setWidget (0, 1, feedbackTypeComboBox);
		FlexTable middleFlexTable = new FlexTable ();
		flexTable.setWidget (1, 0, middleFlexTable);
		Label detailLabel = new Label ("Detail:");
		middleFlexTable.setWidget (0, 0, detailLabel);
		middleFlexTable.setWidget (1, 0, richTextArea);
		FlexTable bottomFlexTable = new FlexTable ();
		flexTable.setWidget (2, 0, bottomFlexTable);
		submitButton.setText ("Submit");
		bottomFlexTable.setWidget (0, 0, submitButton);
		flexTable.getCellFormatter ().setVerticalAlignment (1, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setHorizontalAlignment (2, 0, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter ().setVerticalAlignment (2, 0, HasVerticalAlignment.ALIGN_TOP);
		setStyleName ("gwt-DialogBox");

		submitButton.addClickHandler (this);
		// Set user rights according to the role
		setRights ("PATIENT");
	}

	@Override
	public void clearUp ()
	{
		richTextArea.setText ("");
	}

	@Override
	public void setCurrent ()
	{
	}

	@Override
	public boolean validate ()
	{
		return true;
	}

	@Override
	public void setRights (String menuName)
	{
		submitButton.setEnabled (true);
	}

	@Override
	public void saveData ()
	{
		final String type = IRZClient.get (feedbackTypeComboBox).toUpperCase ();
		final String text = richTextArea.getText ().toUpperCase ();
		try
		{
			Feedback feedback = new Feedback (IRZ.getCurrentUserName (), type, text, new Date (), "PENDING");
			service.saveFeedback (feedback, new AsyncCallback<Boolean> ()
			{
				@Override
				public void onSuccess (Boolean result)
				{
					if (result)
					{
						Window.alert (CustomMessage.getInfoMessage (InfoType.INSERTED));
						clearUp ();
					}
					else
						Window.alert (CustomMessage.getErrorMessage (ErrorType.INSERT_ERROR));
				}

				@Override
				public void onFailure (Throwable caught)
				{
					Window.alert (CustomMessage.getErrorMessage (ErrorType.INSERT_ERROR));
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	@Override
	public void updateData ()
	{
		// Not applicable
	}

	@Override
	public void deleteData ()
	{
		// Not applicable
	}

	@Override
	public void fillData ()
	{
		// Not applicable
	}

	@Override
	public void onClick (ClickEvent event)
	{
		loading.show ();
		setVisible (false);
		if (validate ())
			saveData ();
		setVisible (true);
		loading.hide ();
	}
}
