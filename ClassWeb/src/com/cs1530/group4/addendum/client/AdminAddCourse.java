package com.cs1530.group4.addendum.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdminAddCourse extends Composite
{
	private TextBox descriptionTextBox;
	private TextBox nameTextBox;
	private IntegerBox numberIntegerBox;
	private TextBox subjectTextBox;

	public AdminAddCourse(final MainView main)
	{

		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);

		FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);

		Label lblSubjectCode = new Label("Subject Code");
		flexTable.setWidget(0, 0, lblSubjectCode);

		subjectTextBox = new TextBox();
		flexTable.setWidget(0, 1, subjectTextBox);

		Label lblNumber = new Label("Number");
		flexTable.setWidget(1, 0, lblNumber);

		numberIntegerBox = new IntegerBox();
		flexTable.setWidget(1, 1, numberIntegerBox);

		Label lblName = new Label("Name");
		flexTable.setWidget(2, 0, lblName);

		nameTextBox = new TextBox();
		flexTable.setWidget(2, 1, nameTextBox);

		Label lblDescription = new Label("Description");
		flexTable.setWidget(3, 0, lblDescription);

		descriptionTextBox = new TextBox();
		flexTable.setWidget(3, 1, descriptionTextBox);

		Button btnNewButton = new Button("Add Course");
		btnNewButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				String code = subjectTextBox.getText();
				int num = numberIntegerBox.getValue();
				String name = nameTextBox.getText();
				String desc = descriptionTextBox.getText();
				UserServiceAsync loginService = UserService.Util.getInstance();
				// Set up the callback object.
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
					}

					@Override
					public void onSuccess(Void v)
					{
						subjectTextBox.setText("");
						numberIntegerBox.setText("");
						nameTextBox.setText("");
						descriptionTextBox.setText("");
					}
				};

				loginService.adminAddCourse(code, num, name, desc, callback);
			}
		});
		verticalPanel.add(btnNewButton);

		Button btnBackToSearch = new Button("Back to Search");
		btnBackToSearch.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				main.setContent(new ClassSearch(main),"classSearch");
			}
		});
		verticalPanel.add(btnBackToSearch);
	}

}
