package com.cs1530.group4.addendum.client;

import com.cs1530.group4.addendum.shared.User;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

/**
 * This represents a dialog for the creation of a new user account.
 */
public class NewUserDialog extends DialogBox
{
	/** The application's {@link MainView}. */
	MainView main;
	
	/** This label is used to display error messages from the server. */
	Label errorLabel;
	
	/** This label is shown if the {@link #EMAIL_PATTERN} is not matched by the entered email address. */
	Label invalidEmailLabel;
	
	/** The email text box. */
	TextBox firstNameTextBox;
	
	/** The last name text box. */
	TextBox lastNameTextBox;
	
	/** The username text box. */
	TextBox usernameTextBox;
	
	/** The email text box. */
	TextBox emailTextBox;
	
	/** The password text box. */
	PasswordTextBox passwordTextBox;
	
	/** A reference to this NewUserDialog. */
	NewUserDialog dialog = this;
	
	/** The regex to determine a valid email. */
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/**
	 * Instantiates a new NewUserDialog.
	 *
	 * @param m the application's {@link MainView}
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public NewUserDialog(MainView m)
	{
		main = m;
		FlexTable flexTable = new FlexTable();
		add(flexTable);
		flexTable.setSize("528px", "432px");

		Label lblFillInYour = new Label("Fill in your details below");
		lblFillInYour.setStyleName("newUserDialogTitle");
		flexTable.setWidget(0, 0, lblFillInYour);

		firstNameTextBox = new TextBox();
		firstNameTextBox.setAlignment(TextAlignment.CENTER);
		firstNameTextBox.setStyleName("ADCTextbox");
		firstNameTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					createUser(firstNameTextBox.getText(), lastNameTextBox.getText(), emailTextBox.getText(), usernameTextBox.getText(), passwordTextBox.getText());
			}
		});

		Label lblFirstName = new Label("First Name:");
		lblFirstName.setStyleName("whatever");
		flexTable.setWidget(1, 1, lblFirstName);
		flexTable.setWidget(1, 4, firstNameTextBox);
		firstNameTextBox.setSize("200px", "30px");

		lastNameTextBox = new TextBox();
		lastNameTextBox.setAlignment(TextAlignment.CENTER);
		lastNameTextBox.setStyleName("ADCTextbox");
		lastNameTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					createUser(firstNameTextBox.getText(), lastNameTextBox.getText(), emailTextBox.getText(), usernameTextBox.getText(), passwordTextBox.getText());
			}
		});

		Label lblLastName = new Label("Last Name:");
		lblLastName.setStyleName("whatever");
		flexTable.setWidget(2, 1, lblLastName);
		flexTable.setWidget(2, 4, lastNameTextBox);
		lastNameTextBox.setSize("200px", "30px");

		usernameTextBox = new TextBox();
		usernameTextBox.setAlignment(TextAlignment.CENTER);
		usernameTextBox.setStyleName("ADCTextbox");
		usernameTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					createUser(firstNameTextBox.getText(), lastNameTextBox.getText(), emailTextBox.getText(), usernameTextBox.getText(), passwordTextBox.getText());
			}
		});
		
		Label lblEmail = new Label("Email:");
		lblEmail.setStyleName("whatever");
		flexTable.setWidget(3, 1, lblEmail);
		
		emailTextBox = new TextBox();
		emailTextBox.setStyleName("ADCTextbox");
		emailTextBox.setAlignment(TextAlignment.CENTER);
		flexTable.setWidget(3, 4, emailTextBox);
		emailTextBox.setSize("200px", "30px");
		
		invalidEmailLabel = new Label("not a valid email address");
		invalidEmailLabel.setVisible(false);
		invalidEmailLabel.setStyleName("gwt-Label-Error");
		flexTable.setWidget(4, 0, invalidEmailLabel);

		Label lblUsername = new Label("Username:");
		lblUsername.setStyleName("whatever");
		flexTable.setWidget(5, 1, lblUsername);
		flexTable.setWidget(5, 4, usernameTextBox);
		usernameTextBox.setSize("200px", "30px");

		passwordTextBox = new PasswordTextBox();
		passwordTextBox.setAlignment(TextAlignment.CENTER);
		passwordTextBox.setStyleName("ADCTextbox");
		passwordTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					createUser(firstNameTextBox.getText(), lastNameTextBox.getText(), emailTextBox.getText(), usernameTextBox.getText(), passwordTextBox.getText());
			}
		});

		Label lblPassword = new Label("Password:");
		lblPassword.setStyleName("whatever");
		flexTable.setWidget(6, 1, lblPassword);
		flexTable.setWidget(6, 4, passwordTextBox);
		passwordTextBox.setSize("200px", "30px");

		errorLabel = new Label("username already in use");
		errorLabel.setVisible(false);
		errorLabel.setStyleName("gwt-Label-Error");
		flexTable.setWidget(7, 0, errorLabel);
		errorLabel.setWidth("500px");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName((String) null);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(8, 0, horizontalPanel);

		Button btnCancel = new Button("Cancel");
		btnCancel.setStyleName("ADCButton");
		btnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				dialog.hide();
			}
		});
		horizontalPanel.add(btnCancel);
		btnCancel.setSize("70px", "30px");

		Button btnOk = new Button("OK");
		btnOk.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				createUser(firstNameTextBox.getText(), lastNameTextBox.getText(), emailTextBox.getText(), usernameTextBox.getText(), passwordTextBox.getText());
			}
		});
		btnOk.setStyleName("ADCButton");
		horizontalPanel.add(btnOk);
		btnOk.setSize("70px", "30px");
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 5);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getFlexCellFormatter().setColSpan(8, 0, 5);
		flexTable.getCellFormatter().setHorizontalAlignment(8, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getCellFormatter().setHorizontalAlignment(7, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getFlexCellFormatter().setColSpan(7, 0, 5);
		flexTable.getFlexCellFormatter().setColSpan(4, 0, 5);
		setStyleName("ADCBasic");

		setGlassEnabled(true);
		center();
	}

	/**
	 * Validates the user's entered data.  Displays any error messages.
	 *
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param email the email
	 * @param username the username
	 * @param password the password
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#createUser(String, String, String, String, String)}
	 */
	private void createUser(String firstName, String lastName, String email, final String username, String password)
	{
		if(firstName.length() == 0 || lastName.length() == 0 || email.length() == 0 || username.length() == 0 || password.length() == 0)
		{
			Window.alert("All fields are required");
			return;
		}
		
		if(!email.matches(EMAIL_PATTERN))
		{
			invalidEmailLabel.setText("not a valid email address");
			invalidEmailLabel.setVisible(true);
			return;
		}

		UserServiceAsync userService = UserService.Util.getInstance();
		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
			}

			@Override
			public void onSuccess(String result)
			{
				if(result.equals("success"))
				{
					dialog.hide();
					
					if(usernameTextBox.getText().equals("Administrator"))
					{
						Cookies.setCookie("loggedIn", username);
						User user = new User(username);
						Storage localStorage = Storage.getLocalStorageIfSupported();
						localStorage.setItem("loggedIn", user.serialize());
						main.setContent(new AdminPanel(main), "adminPanel");
					}
					else
					{
						Window.alert("Please check your email to verify your account");
					}
				}
				else if(result.equals("user_exists"))
					errorLabel.setVisible(true);
				else if(result.equals("email_exists"))
				{
					invalidEmailLabel.setText("email addres is already in use");
					invalidEmailLabel.setVisible(true);
				}
			}
		};

		userService.createUser(username, password, email, firstName, lastName, callback);
	}
}