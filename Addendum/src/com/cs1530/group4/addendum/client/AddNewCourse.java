package com.cs1530.group4.addendum.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This represents a UI to add new classes to a queue for the site administrator to approve
 */
public class AddNewCourse extends DialogBox
{
	/** A reference to this AddNewCourse dialog */
	private AddNewCourse dialog = this;

	/** The description text box. */
	private TextBox descriptionTextBox;

	/** The course name text box. */
	private TextBox nameTextBox;

	/** The subject code text box. */
	private TextBox subjectTextBox;

	/** The course number integer box. */
	private IntegerBox numberIntegerBox;

	/**
	 * Instantiates a new AddNewCourse
	 * 
	 * @param main a reference to the application's {@link MainView}
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public AddNewCourse(final MainView main)
	{

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setStyleName("whatever");
		add(verticalPanel);
		verticalPanel.setSize("600px", "430px");

		FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		verticalPanel.setCellHeight(flexTable, "100%");
		flexTable.setSize("100%", "100%");

		Label lblSubjectCode = new Label("SubjectCode");
		lblSubjectCode.setStyleName("whatever");
		flexTable.setWidget(0, 1, lblSubjectCode);
		lblSubjectCode.setSize("150px", "");

		subjectTextBox = new TextBox();
		subjectTextBox.setAlignment(TextAlignment.CENTER);
		subjectTextBox.setStyleName("ADCTextbox");
		flexTable.setWidget(0, 3, subjectTextBox);
		subjectTextBox.setSize("240px", "50%");

		Label lblNumber = new Label("Number");
		lblNumber.setStyleName("whatever");
		flexTable.setWidget(1, 1, lblNumber);

		numberIntegerBox = new IntegerBox();
		numberIntegerBox.setAlignment(TextAlignment.CENTER);
		numberIntegerBox.setStyleName("ADCTextbox");
		flexTable.setWidget(1, 3, numberIntegerBox);
		numberIntegerBox.setSize("240px", "50%");

		Label lblName = new Label("Name");
		lblName.setStyleName("whatever");
		flexTable.setWidget(2, 1, lblName);

		nameTextBox = new TextBox();
		nameTextBox.setAlignment(TextAlignment.CENTER);
		nameTextBox.setStyleName("ADCTextbox");
		flexTable.setWidget(2, 3, nameTextBox);
		nameTextBox.setSize("240px", "50%");

		Label lblDescription = new Label("Description");
		lblDescription.setStyleName("whatever");
		flexTable.setWidget(3, 1, lblDescription);

		descriptionTextBox = new TextBox();
		descriptionTextBox.setAlignment(TextAlignment.CENTER);
		descriptionTextBox.setStyleName("ADCTextbox");
		flexTable.setWidget(3, 3, descriptionTextBox);
		descriptionTextBox.setSize("240px", "50%");

		Button btnNewButton = new Button("Add Course");
		btnNewButton.setText("ADD COURSE");
		btnNewButton.setStyleName("ADCButton");
		flexTable.setWidget(4, 3, btnNewButton);
		btnNewButton.setSize("200px", "75%");
		btnNewButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				String code = subjectTextBox.getText();
				int num = 0;
				if(numberIntegerBox.getValue() != null)
					num = numberIntegerBox.getValue();
				String name = nameTextBox.getText();
				String desc = descriptionTextBox.getText();
				UserServiceAsync userService = UserService.Util.getInstance();
				// Set up the callback object.
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						//TODO: show error message?
					}

					@Override
					public void onSuccess(Void v)
					{
						dialog.hide();
						//TODO: show new class on user's profile
					}
				};

				userService.newCourseRequest(code, num, name, desc, callback);
			}
		});
		flexTable.getCellFormatter().setHorizontalAlignment(4, 3, HasHorizontalAlignment.ALIGN_LEFT);

		Button btnBackToSearch = new Button("Back to Search");
		btnBackToSearch.setText("BACK TO SEARCH");
		btnBackToSearch.setStyleName("ADCButton");
		flexTable.setWidget(4, 1, btnBackToSearch);
		btnBackToSearch.setSize("215px", "75%");
		flexTable.getCellFormatter().setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_CENTER);
		btnBackToSearch.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				dialog.hide();
				new ClassSearch(main);
			}
		});
		setStyleName("ADCBasic");

		setGlassEnabled(true);
		center();
	}
}