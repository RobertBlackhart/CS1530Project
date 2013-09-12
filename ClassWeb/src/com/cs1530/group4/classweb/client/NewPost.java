package com.cs1530.group4.classweb.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewPost extends DialogBox
{
	DialogBox postBox = this;
	RichTextArea editor;
	ListBox streamLevelBox;
	MainView main;

	public NewPost(MainView m, ArrayList<String> streams)
	{
		main = m;
		setHTML("New Post");

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		editor = new RichTextArea();
		editor.setSize("600px", "400px");
		RichTextToolbar toolbar = new RichTextToolbar(editor);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buttonPanel.setWidth("100%");
		Button submitButton = new Button("Submit");
		submitButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				UserServiceAsync userService = UserService.Util.getInstance();
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
				{
					@Override
					public void onFailure(Throwable caught){}
					
					@Override
					public void onSuccess(Boolean result)
					{
						main.setContent(new Profile(main,Cookies.getCookie("loggedIn")));
						postBox.hide();
					}
				};
				
				String stream = streamLevelBox.getItemText(streamLevelBox.getSelectedIndex());
				if(stream.equals("Everyone"))
					stream = "all";
				else
					stream = stream.substring(11);
				userService.uploadPost(Cookies.getCookie("loggedIn"), editor.getHTML(), stream, callback);
			}
		});
		Button discardButton = new Button("Discard");
		discardButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				postBox.hide();
			}
		});
		buttonPanel.add(discardButton);
		buttonPanel.add(submitButton);

		vPanel.add(toolbar);
		vPanel.add(editor);
		
		HorizontalPanel streamPanel = new HorizontalPanel();
		streamPanel.getElement().getStyle().setProperty("marginBottom", "5px");
		streamPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		streamPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(streamPanel);
		
		Label lblMakeThisPost = new Label("Make this post visible to: ");
		streamPanel.add(lblMakeThisPost);
		
		streamLevelBox = new ListBox();
		streamLevelBox.addItem("Everyone");
		for(String stream: streams)
			streamLevelBox.addItem("Members of " + stream);
		streamPanel.add(streamLevelBox);
		vPanel.add(buttonPanel);
		add(vPanel);

		setGlassEnabled(true);
		center();
	}
}
