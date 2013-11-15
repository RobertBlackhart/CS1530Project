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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * This gives the user a UI to reset their password.
 */
public class ResetPasswordDialog extends DialogBox
{
	
	/** A reference to this ResetPasswordDialog object. */
	ResetPasswordDialog dialog = this;
	
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();

	/**
	 * Instantiates a new reset password dialog.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public ResetPasswordDialog()
	{
		VerticalPanel verticalPanel = new VerticalPanel();

		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		add(verticalPanel);
		verticalPanel.setSize("250px", "110px");
		
		FlexTable flexTable = new FlexTable();
		verticalPanel.add(flexTable);
		
		Label lblEnterYourUsername = new Label("Enter your username below.  We will send you a password reset email to your registered address.");
		lblEnterYourUsername.setStyleName("whatever-small");
		lblEnterYourUsername.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(0, 0, lblEnterYourUsername);
		
		HTML html = new HTML("<hr />", true);
		flexTable.setWidget(1, 0, html);

		Label lblUsername = new Label("Username:");
		flexTable.setWidget(2, 0, lblUsername);
		lblUsername.setStyleName("whatever-small");

		final TextBox usernameTextBox = new TextBox();
		flexTable.setWidget(2, 1, usernameTextBox);
		usernameTextBox.setStyleName("ADCTextbox");
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		
		HTML html_1 = new HTML("<hr />", true);
		flexTable.setWidget(3, 0, html_1);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(4, 0, horizontalPanel);
		horizontalPanel.setWidth("100%");
		
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

		Button resetButton = new Button("Send Password Reset Email");
		horizontalPanel.add(resetButton);
		resetButton.setText("OK");
		resetButton.setStyleName("ADCButton");
		resetButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if(usernameTextBox.getText().length() == 0)
				{
					Window.alert("The username cannot be blank.");
					return;
				}
				
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						Window.alert("There was an unknown error.");
					}
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
		flexTable.getFlexCellFormatter().setColSpan(4, 0, 2);
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		flexTable.getFlexCellFormatter().setColSpan(3, 0, 2);

		setStyleName("ADCBasic");
		setGlassEnabled(true);
		center();
	}
}