package com.cs1530.group4.addendum.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResetPasswordDialog extends DialogBox
{
	ResetPasswordDialog dialog = this;
	UserServiceAsync userService = UserService.Util.getInstance();

	public ResetPasswordDialog()
	{
		VerticalPanel verticalPanel = new VerticalPanel();

		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		add(verticalPanel);
		verticalPanel.setSize("250px", "110px");

		Label lblUsername = new Label("Username:");
		lblUsername.setStyleName("whatever");
		verticalPanel.add(lblUsername);

		final TextBox usernameTextBox = new TextBox();
		usernameTextBox.setStyleName("ADCTextbox");
		verticalPanel.add(usernameTextBox);

		Button resetButton = new Button("Send Password Reset Email");
		resetButton.setStyleName("ADCButton");
		resetButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
				{
					@Override
					public void onFailure(Throwable caught){}
					@Override
					public void onSuccess(Boolean result)
					{
						if(result)
						{
							Window.alert("Please check your email for reset instructions.");
							dialog.hide();
						}
						else
							Window.alert("Unable to find account for " + usernameTextBox.getText());
					}
				};
				
				userService.resetPassword(usernameTextBox.getText(),callback);
			}
		});
		verticalPanel.add(resetButton);

		setStyleName("ADCBasic");
		setGlassEnabled(true);
		center();
	}
}