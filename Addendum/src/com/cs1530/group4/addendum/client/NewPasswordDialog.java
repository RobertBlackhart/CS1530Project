package com.cs1530.group4.addendum.client;

import com.cs1530.group4.addendum.shared.User;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;

/**
 * This represents the UI for a user to change their password
 */
public class NewPasswordDialog extends DialogBox
{
	
	/** A reference to this NewPassworDialog object. */
	NewPasswordDialog dialog = this;
	
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();

	/**
	 * Instantiates a new new password dialog.
	 *
	 * @param username the user's username
	 * @param main the appliation's {@link MainView}
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public NewPasswordDialog(final String username, final MainView main)
	{
		FlexTable flexTable = new FlexTable();
		setWidget(flexTable);
		flexTable.setSize("100%", "100%");

		Label lblNewPassword = new Label("New Password:");
		lblNewPassword.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		lblNewPassword.setStyleName("whatever-small");
		flexTable.setWidget(0, 0, lblNewPassword);

		final PasswordTextBox passwordTextBox = new PasswordTextBox();
		passwordTextBox.setStyleName("ADCTextbox");
		flexTable.setWidget(0, 1, passwordTextBox);

		Label lblConfirmPassword = new Label("Confirm Password:");
		lblConfirmPassword.setStyleName("whatever-small");
		flexTable.setWidget(1, 0, lblConfirmPassword);

		final PasswordTextBox passwordTextBox_1 = new PasswordTextBox();
		passwordTextBox_1.setStyleName("ADCTextbox");
		flexTable.setWidget(1, 1, passwordTextBox_1);

		Button changePasswordButton = new Button("Change My Password");
		changePasswordButton.setStyleName("ADCButton");
		changePasswordButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if(!passwordTextBox.getText().equals(passwordTextBox_1.getText()))
				{
					Window.alert("Passwords do not match");
					return;
				}
				
				AsyncCallback<User> callback = new AsyncCallback<User>()
				{
					@Override
					public void onFailure(Throwable caught){}
					@Override
					public void onSuccess(User result)
					{
						if(result != null)
						{
							dialog.hide();
							Cookies.setCookie("loggedIn", username);
							Storage localStorage = Storage.getLocalStorageIfSupported();
							localStorage.setItem("loggedIn", result.serialize());
							main.setContent(new Stream(main), "profile-"+username);
						}
						else
							Window.alert("There was a problem changing your password.");
					}
				};
				
				userService.changePassword(username, passwordTextBox_1.getText(), callback);
			}
		});
		flexTable.setWidget(2, 1, changePasswordButton);

		setStyleName("ADCBasic");
		setGlassEnabled(true);
		center();
	}
}