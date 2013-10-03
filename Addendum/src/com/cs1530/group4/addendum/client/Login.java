package com.cs1530.group4.addendum.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Login extends Composite
{
	private TextBox usernameTextBox;
	private PasswordTextBox passwordTextBox;
	private Label errorLabel;
	private MainView main;
	private CheckBox rememberMeCheckBox;

	public Login(MainView m)
	{
		main = m;
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		initWidget(verticalPanel);
		verticalPanel.setSize("843px", "657px");

		Label welcomeLabel = new Label("ADDENDUM");
		welcomeLabel.setDirectionEstimator(false);
		welcomeLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		welcomeLabel.getElement().getStyle().setProperty("marginBottom", "20px");
		welcomeLabel.setStyleName("LoginTitle");
		verticalPanel.add(welcomeLabel);
		verticalPanel.setCellVerticalAlignment(welcomeLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(welcomeLabel, HasHorizontalAlignment.ALIGN_CENTER);
		welcomeLabel.setSize("100%", "136px");

		if(Cookies.getCookie("loggedIn") != null)
			m.setContent(new Profile(m, Cookies.getCookie("loggedIn")), "profile-" + Cookies.getCookie("loggedIn"));

		errorLabel = new Label("Could not login.  Invalid username or password.");
		errorLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		errorLabel.setVisible(false);
		errorLabel.setStyleName("gwt-Label-Error");
		verticalPanel.add(errorLabel);
		errorLabel.setWidth("315px");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName("LoginBox");
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setSize("543px", "154px");

		DecoratorPanel decoratorPanel = new DecoratorPanel();
		horizontalPanel.add(decoratorPanel);
		decoratorPanel.setStyleName("LoginBox");

		FlexTable flexTable = new FlexTable();
		decoratorPanel.setWidget(flexTable);
		flexTable.setHeight("155px");

		Label lblAlreadyAUser = new Label("Already a User?");
		lblAlreadyAUser.setStyleName("gwt-Label-User");
		flexTable.setWidget(0, 0, lblAlreadyAUser);

		Label lblUsername = new Label("Username:");
		lblUsername.setStyleName("gwt-Label-Login");
		flexTable.setWidget(1, 0, lblUsername);

		usernameTextBox = new TextBox();
		usernameTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		usernameTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					login();
			}
		});
		usernameTextBox.setFocus(true);
		flexTable.setWidget(1, 1, usernameTextBox);

		Label lblPassword = new Label("Password:");
		lblPassword.setStyleName("gwt-Label-Login");
		flexTable.setWidget(2, 0, lblPassword);

		passwordTextBox = new PasswordTextBox();
		passwordTextBox.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		passwordTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					login();
			}
		});
		flexTable.setWidget(2, 1, passwordTextBox);

		rememberMeCheckBox = new CheckBox("Remember me on this Computer");
		flexTable.setWidget(3, 1, rememberMeCheckBox);

		Button btnNewButton = new Button("Sign In");
		btnNewButton.setStyleName("LoginButton");
		btnNewButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				login();
			}
		});
		flexTable.setWidget(4, 1, btnNewButton);
		btnNewButton.setSize("110px", "25px");
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);

		DecoratorPanel decoratorPanel_1 = new DecoratorPanel();
		decoratorPanel_1.setStyleName("LoginBox");
		horizontalPanel.add(decoratorPanel_1);
		decoratorPanel_1.setSize("240px", "104px");

		VerticalPanel verticalPanel_1 = new VerticalPanel();
		verticalPanel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		decoratorPanel_1.setWidget(verticalPanel_1);
		verticalPanel_1.setSize("244px", "155px");

		Label lblCreateANew = new Label("Don't Have an Account?");
		verticalPanel_1.add(lblCreateANew);
		lblCreateANew.setStyleName("gwt-Label-User");

		Button button = new Button("Create Account");
		button.setText("CREATE ACCOUNT");
		button.setStyleName("LoginButton");
		verticalPanel_1.add(button);
		button.setSize("167px", "25px");
		button.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				main.setContent(new NewUserDialog(main), "login");
			}
		});
		setStyleName("Login");
	}

	protected void login()
	{
		String username = usernameTextBox.getText();
		String password = passwordTextBox.getText();

		if(username.length() == 0 || password.length() == 0)
		{
			Window.alert("Username or Password is empty.");
			return;
		}
		else
		{
			UserServiceAsync loginService = UserService.Util.getInstance();
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
						acceptLogin();
					else
						rejectLogin();
				}
			};

			loginService.doLogin(username, password, callback);
		}
	}

	private void acceptLogin()
	{
		if(rememberMeCheckBox.getValue())
		{
			Date expires = new Date();
			expires.setTime(expires.getTime() + (1000 * 60 * 60 * 24 * 14)); //14 days from now
			Cookies.setCookie("loggedIn", usernameTextBox.getText(), expires);
		}
		else
			Cookies.setCookie("loggedIn", usernameTextBox.getText());

		if(usernameTextBox.getText().equals("Administrator"))
			main.setContent(new AdminPanel(main), "adminPanel");
		else
			main.setContent(new Profile(main, usernameTextBox.getText()), "profile-" + usernameTextBox.getText());
	}

	private void rejectLogin()
	{
		errorLabel.setVisible(true);
	}
}