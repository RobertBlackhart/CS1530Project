package com.cs1530.group4.addendum.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdminAddCourse extends Composite
{
	private TextBox descriptionTextBox;
	private TextBox nameTextBox;
	private TextBox subjectTextBox;
	private IntegerBox numberIntegerBox;

	public AdminAddCourse(final MainView main)
	{

		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		verticalPanel.setSize("433px", "100%");

		FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		verticalPanel.setCellHeight(flexTable, "100%");
		flexTable.setSize("526px", "244px");

		Label lblSubjectCode = new Label("Subject Code");
		lblSubjectCode.setStyleName("whatever");
		flexTable.setWidget(0, 1, lblSubjectCode);
		lblSubjectCode.setWidth("146px");

		subjectTextBox = new TextBox();
		flexTable.setWidget(0, 3, subjectTextBox);
		subjectTextBox.setWidth("90%");

		Label lblNumber = new Label("Number");
		lblNumber.setStyleName("whatever");
		flexTable.setWidget(1, 1, lblNumber);

		numberIntegerBox = new IntegerBox();
		flexTable.setWidget(1, 3, numberIntegerBox);
		numberIntegerBox.setWidth("90%");

		Label lblName = new Label("Name");
		lblName.setStyleName("whatever");
		flexTable.setWidget(2, 1, lblName);

		nameTextBox = new TextBox();
		flexTable.setWidget(2, 3, nameTextBox);
		nameTextBox.setWidth("90%");

		Label lblDescription = new Label("Description");
		lblDescription.setStyleName("whatever");
		flexTable.setWidget(3, 1, lblDescription);

		Image image = new Image((String) null);
		flexTable.setWidget(3, 2, image);

		descriptionTextBox = new TextBox();
		flexTable.setWidget(3, 3, descriptionTextBox);
		descriptionTextBox.setWidth("90%");

		Button btnNewButton = new Button("Add Course");
		flexTable.setWidget(4, 1, btnNewButton);
		btnNewButton.setWidth("90%");
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

		Button btnBackToSearch = new Button("Back to Search");
		flexTable.setWidget(4, 3, btnBackToSearch);
		btnBackToSearch.setWidth("95%");
		flexTable.getCellFormatter().setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter().setHorizontalAlignment(4, 3, HasHorizontalAlignment.ALIGN_LEFT);
		btnBackToSearch.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				main.setContent(new ClassSearch(main), "classSearch");
			}
		});
	}

}