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
import com.google.gwt.user.client.ui.HorizontalSplitPanel;

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
		flexTable.setSize("100%", "50%");

		Label lblSubjectCode = new Label("Subject Code");
		flexTable.setWidget(0, 0, lblSubjectCode);
		flexTable.getCellFormatter().setStyleName(0, 0, "wahtever");

		subjectTextBox = new TextBox();
		flexTable.setWidget(0, 1, subjectTextBox);
		subjectTextBox.setWidth("100%");

		Label lblNumber = new Label("Number");
		flexTable.setWidget(1, 0, lblNumber);
		
		numberIntegerBox = new IntegerBox();
		flexTable.setWidget(1, 1, numberIntegerBox);
		numberIntegerBox.setWidth("100%");

		Label lblName = new Label("Name");
		flexTable.setWidget(2, 0, lblName);

		nameTextBox = new TextBox();
		flexTable.setWidget(2, 1, nameTextBox);
		nameTextBox.setWidth("100%");

		Label lblDescription = new Label("Description");
		flexTable.setWidget(3, 0, lblDescription);

		descriptionTextBox = new TextBox();
		flexTable.setWidget(3, 1, descriptionTextBox);
		descriptionTextBox.setWidth("100%");
		
		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		verticalPanel.add(horizontalSplitPanel);
		horizontalSplitPanel.setHeight("37px");
		
				Button btnNewButton = new Button("Add Course");
				horizontalSplitPanel.setLeftWidget(btnNewButton);
				btnNewButton.setSize("100%", "100%");
				
						Button btnBackToSearch = new Button("Back to Search");
						horizontalSplitPanel.setRightWidget(btnBackToSearch);
						btnBackToSearch.setSize("100%", "100%");
						btnBackToSearch.addClickHandler(new ClickHandler()
						{
							public void onClick(ClickEvent event)
							{
								main.setContent(new ClassSearch(main),"classSearch");
							}
						});
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
	}

}
