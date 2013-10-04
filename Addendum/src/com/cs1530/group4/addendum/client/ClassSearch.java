package com.cs1530.group4.addendum.client;

import java.util.ArrayList;

import com.cs1530.group4.addendum.shared.Course;
import com.cs1530.group4.addendum.shared.User;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ClassSearch extends DialogBox
{
	ClassSearch dialog = this;
	private TextBox subjectTextBox;
	private IntegerBox numberTextBox;
	private TextBox nameTextBox;
	private TextBox descriptionTextBox;
	private ListBox resultsBox;
	UserServiceAsync userService = UserService.Util.getInstance();

	public ClassSearch(final MainView main)
	{
		setStyleName("");
		VerticalPanel verticalPanel = new VerticalPanel();
		
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		add(verticalPanel);
		verticalPanel.setSize("600px", "600px");

		Label lblPleaseFillIn = new Label("PLEASE FILL IN ONE OR MORE FIELDS");
		lblPleaseFillIn.setStyleName("CSLabel");
		verticalPanel.add(lblPleaseFillIn);

		FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		flexTable.setWidth("500px");

		Label lblSubjectCode = new Label("Subject Code");
		lblSubjectCode.setStyleName("whatever");
		flexTable.setWidget(0, 0, lblSubjectCode);
		lblSubjectCode.setSize("100%", "25%");

		subjectTextBox = new TextBox();
		subjectTextBox.setAlignment(TextAlignment.CENTER);
		subjectTextBox.setStyleName("ADCTextbox");
		flexTable.setWidget(0, 1, subjectTextBox);
		subjectTextBox.setSize("220px", "75%");

		Label lblCatalogueNumber = new Label("Catalogue Number");
		lblCatalogueNumber.setStyleName("whatever");
		flexTable.setWidget(1, 0, lblCatalogueNumber);
		lblCatalogueNumber.setSize("300px", "50%");

		numberTextBox = new IntegerBox();
		numberTextBox.setAlignment(TextAlignment.CENTER);
		numberTextBox.setStyleName("ADCTextbox");
		flexTable.setWidget(1, 1, numberTextBox);
		numberTextBox.setSize("220px", "75%");

		Label lblCourseName = new Label("Course Name");
		lblCourseName.setStyleName("whatever");
		flexTable.setWidget(2, 0, lblCourseName);
		lblCourseName.setSize("200px", "50%");

		nameTextBox = new TextBox();
		nameTextBox.setAlignment(TextAlignment.CENTER);
		nameTextBox.setStyleName("ADCTextbox");
		flexTable.setWidget(2, 1, nameTextBox);
		nameTextBox.setSize("220px", "75%");

		Label lblCourseDescription = new Label("Course Description");
		lblCourseDescription.setStyleName("whatever");
		flexTable.setWidget(3, 0, lblCourseDescription);
		lblCourseDescription.setSize("200", "50%");

		descriptionTextBox = new TextBox();
		descriptionTextBox.setAlignment(TextAlignment.CENTER);
		descriptionTextBox.setStyleName("ADCTextbox");
		flexTable.setWidget(3, 1, descriptionTextBox);
		descriptionTextBox.setSize("220px", "75%");

		Button btnSearch = new Button("Search");
		btnSearch.setStyleName("ADCButton");
		btnSearch.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				resultsBox.clear();
				String code = subjectTextBox.getText();
				Integer num = numberTextBox.getValue();
				String name = nameTextBox.getText();
				String desc = descriptionTextBox.getText();
				if(code == null)
					code = "";
				if(num == null)
					num = 0;
				if(name == null)
					name = "";
				if(desc == null)
					desc = "";

				// Set up the callback object.
				AsyncCallback<ArrayList<Course>> callback = new AsyncCallback<ArrayList<Course>>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
					}

					@Override
					public void onSuccess(ArrayList<Course> courses)
					{
						if(courses.size() == 0)
						{
							resultsBox.addItem("No Results Found");
							return;
						}
						for(Course course : courses)
						{
							resultsBox.addItem(course.getSubjectCode() + course.getCourseNumber() + " || " + course.getCourseName() + " || " + course.getCourseDescription());
						}
					}
				};

				userService.courseSearch(code, num, name, desc, callback);
			}
		});
		verticalPanel.add(btnSearch);
		btnSearch.setSize("300px", "25px");

		resultsBox = new ListBox(true);
		resultsBox.setStyleName("CSListbox");
		verticalPanel.add(resultsBox);
		resultsBox.setVisibleItemCount(5);
		resultsBox.setWidth("500px");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);

		Button btnCancel = new Button("Cancel");
		btnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				dialog.hide();
			}
		});
		horizontalPanel.add(btnCancel);
		btnCancel.setStyleName("ADCButton");

		Button btnAddSelectedClasses = new Button("Add Selected Classes");
		horizontalPanel.add(btnAddSelectedClasses);
		btnAddSelectedClasses.setStyleName("ADCButton");
		btnAddSelectedClasses.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				final ArrayList<String> courses = new ArrayList<String>();
				for(int i = 0; i < resultsBox.getItemCount(); i++)
				{
					if(resultsBox.isItemSelected(i))
					{
						String item = resultsBox.getValue(i);
						String course = item.substring(0, item.indexOf(' '));
						if(!course.equals("No"))
							courses.add(course);
					}
				}
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
					}

					@Override
					public void onSuccess(Void v)
					{
						dialog.hide();
						Storage localStorage = Storage.getLocalStorageIfSupported();
						User user = new User(Cookies.getCookie("loggedIn"));
						if(localStorage.getItem("loggedIn") != null)
						{
							user = User.deserialize(localStorage.getItem("loggedIn"));
							for(String course : courses)
							{
								if(!user.getCourseList().contains(course))
									user.getCourseList().add(course);
							}
							localStorage.setItem("loggedIn", user.serialize());
						}
						main.setContent(new Stream(main, user), "profile-" + Cookies.getCookie("loggedIn"));
					}
				};

				if(courses.size() > 0)
					userService.userAddCourse(Cookies.getCookie("loggedIn"), courses, callback);
				else
				{
					Storage localStorage = Storage.getLocalStorageIfSupported();
					User user = new User(Cookies.getCookie("loggedIn"));
					if(localStorage.getItem("loggedIn") != null)
						user = User.deserialize(localStorage.getItem("loggedIn"));
					main.setContent(new Stream(main, user), "profile-" + Cookies.getCookie("loggedIn"));
				}
			}
		});

		FlexTable flexTable_1 = new FlexTable();
		verticalPanel.add(flexTable_1);
		verticalPanel.setCellVerticalAlignment(flexTable_1, HasVerticalAlignment.ALIGN_BOTTOM);

		Label lblNewLabel = new Label("Couldn't find your course? Why not try ");
		flexTable_1.setWidget(0, 0, lblNewLabel);

		Anchor addNewCourse = new Anchor("Adding a New Course");
		flexTable_1.setWidget(0, 1, addNewCourse);
		addNewCourse.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				dialog.hide();
				new AddNewCourse(main);
			}
		});
		setStyleName("ADCBasic");
		setGlassEnabled(true);
		center();
	}
}