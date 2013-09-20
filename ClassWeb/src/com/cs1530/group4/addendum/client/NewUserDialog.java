package com.cs1530.group4.addendum.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class NewUserDialog extends Composite
{
	MainView main;
	Label errorLabel;
	TextBox firstNameTextBox, lastNameTextBox, usernameTextBox;
	PasswordTextBox passwordTextBox;
	
	public NewUserDialog(MainView m)
	{
		main = m;
		FlexTable flexTable = new FlexTable();
		initWidget(flexTable);

		Label lblFillInYour = new Label("Fill in your details below");
		flexTable.setWidget(0, 0, lblFillInYour);

		Label lblFirstName = new Label("First Name:");
		flexTable.setWidget(1, 0, lblFirstName);

		firstNameTextBox = new TextBox();
		firstNameTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					createUser(firstNameTextBox.getText(),lastNameTextBox.getText(),usernameTextBox.getText(), passwordTextBox.getText());
			}
		});
		flexTable.setWidget(1, 2, firstNameTextBox);

		Label lblLastName = new Label("Last Name:");
		flexTable.setWidget(2, 0, lblLastName);

		lastNameTextBox = new TextBox();
		lastNameTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					createUser(firstNameTextBox.getText(),lastNameTextBox.getText(),usernameTextBox.getText(), passwordTextBox.getText());
			}
		});
		flexTable.setWidget(2, 2, lastNameTextBox);

		Label lblUsername = new Label("Username:");
		flexTable.setWidget(3, 0, lblUsername);

		usernameTextBox = new TextBox();
		usernameTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					createUser(firstNameTextBox.getText(),lastNameTextBox.getText(),usernameTextBox.getText(), passwordTextBox.getText());
			}
		});
		flexTable.setWidget(3, 2, usernameTextBox);
		
		errorLabel = new Label("username already in use");
		errorLabel.setVisible(false);
		errorLabel.setStyleName("gwt-Label-Error");
		flexTable.setWidget(4, 0, errorLabel);

		Label lblPassword = new Label("Password:");
		flexTable.setWidget(5, 0, lblPassword);

		passwordTextBox = new PasswordTextBox();
		passwordTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					createUser(firstNameTextBox.getText(),lastNameTextBox.getText(),usernameTextBox.getText(), passwordTextBox.getText());
			}
		});
		flexTable.setWidget(5, 2, passwordTextBox);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName((String) null);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(6, 0, horizontalPanel);

		Button btnCancel = new Button("Cancel");
		btnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				main.setContent(new Login(main),"login");
			}
		});
		horizontalPanel.add(btnCancel);

		Button btnOk = new Button("OK");
		btnOk.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				createUser(firstNameTextBox.getText(),lastNameTextBox.getText(),usernameTextBox.getText(), passwordTextBox.getText());
			}
		});
		btnOk.setStyleName("gwt-Button-LeftMargin");
		horizontalPanel.add(btnOk);
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 3);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getFlexCellFormatter().setColSpan(6, 0, 3);
		flexTable.getCellFormatter().setHorizontalAlignment(6, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getFlexCellFormatter().setColSpan(4, 0, 3);
		flexTable.getCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);
	}

	protected void createUser(String firstName, String lastName, final String username, String password)
	{
		if(firstName.length() == 0 || lastName.length() == 0 || username.length() == 0 || password.length() == 0)
		{
			Window.alert("All fields are required");
			return;
		}				
		
		UserServiceAsync userService = UserService.Util.getInstance();
		// Set up the callback object.
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
			}

			@Override
			public void onSuccess(Boolean result)
			{
				if(result)
				{
					Cookies.setCookie("loggedIn", usernameTextBox.getText());
					main.setContent(new Profile(main, username),"profile-"+username);
				}
				else
					errorLabel.setVisible(true);
			}
		};

		userService.createUser(username, password, firstName, lastName, callback);
	}
}
