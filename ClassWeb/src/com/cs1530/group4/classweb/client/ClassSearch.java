package com.cs1530.group4.classweb.client;

import java.util.ArrayList;

import com.cs1530.group4.classweb.shared.Course;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ClassSearch extends Composite
{
	private TextBox subjectTextBox;
	private IntegerBox numberTextBox;
	private TextBox nameTextBox;
	private TextBox descriptionTextBox;
	private ListBox resultsBox;
	UserServiceAsync userService = UserService.Util.getInstance();

	public ClassSearch(final MainView main)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		initWidget(verticalPanel);

		Label lblPleaseFillIn = new Label("Please fill in one or more fields");
		verticalPanel.add(lblPleaseFillIn);

		Anchor addNewCourse = new Anchor("Add New Course");
		addNewCourse.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				main.setContent(new AdminAddCourse(main));
			}
		});
		verticalPanel.add(addNewCourse);

		FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);

		Label lblSubjectCode = new Label("Subject Code");
		flexTable.setWidget(0, 0, lblSubjectCode);

		subjectTextBox = new TextBox();
		flexTable.setWidget(0, 1, subjectTextBox);

		Label lblCatalogueNumber = new Label("Catalogue Number");
		flexTable.setWidget(1, 0, lblCatalogueNumber);

		numberTextBox = new IntegerBox();
		flexTable.setWidget(1, 1, numberTextBox);

		Label lblCourseName = new Label("Course Name");
		flexTable.setWidget(2, 0, lblCourseName);

		nameTextBox = new TextBox();
		flexTable.setWidget(2, 1, nameTextBox);

		Label lblCourseDescription = new Label("Course Description");
		flexTable.setWidget(3, 0, lblCourseDescription);

		descriptionTextBox = new TextBox();
		flexTable.setWidget(3, 1, descriptionTextBox);

		Button btnSearch = new Button("Search");
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

		resultsBox = new ListBox(true);
		verticalPanel.add(resultsBox);
		resultsBox.setVisibleItemCount(5);
		resultsBox.setWidth("500px");

		Button btnAddSelectedClasses = new Button("Add Selected Classes");
		btnAddSelectedClasses.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				ArrayList<String> courses = new ArrayList<String>();
				for(int i = 0; i < resultsBox.getItemCount(); i++)
				{
					if(resultsBox.isItemSelected(i))
					{
						String item = resultsBox.getValue(i);
						String course = item.substring(0, item.indexOf(' '));
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
						main.setContent(new Profile(main,Cookies.getCookie("loggedIn")));
					}
				};
				
				if(courses.size() > 0)
					userService.userAddCourse(Cookies.getCookie("loggedIn"), courses, callback);
				else
					main.setContent(new Profile(main,Cookies.getCookie("loggedIn")));
			}
		});
		verticalPanel.add(btnAddSelectedClasses);
	}
}
