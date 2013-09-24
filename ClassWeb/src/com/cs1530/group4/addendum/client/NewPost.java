package com.cs1530.group4.addendum.client;

import java.util.ArrayList;

import com.cs1530.group4.addendum.shared.Post;
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

	public NewPost(MainView m, ArrayList<String> streams, final Post post)
	{
		main = m;
		setHTML("New Post");

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		editor = new RichTextArea();
		if(post != null)
			editor.setHTML(post.getPostContent());
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
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					@Override
					public void onFailure(Throwable caught){}
					
					@Override
					public void onSuccess(Void v)
					{
						String user = Cookies.getCookie("loggedIn");
						main.setContent(new Profile(main,user),"profile-"+user);
						postBox.hide();
					}
				};
				
				String stream = streamLevelBox.getItemText(streamLevelBox.getSelectedIndex());
				if(stream.equals("Everyone"))
					stream = "all";
				else
					stream = stream.substring(11);
				if(post == null)
					userService.uploadPost(Cookies.getCookie("loggedIn"), editor.getHTML(), stream, callback);
				else
					userService.editPost(post.getPostKey(), editor.getHTML(), callback);
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
		if(post == null)
			streamLevelBox.addItem("Everyone");
		for(String stream: streams)
		{
			if(stream.equals("all"))
				streamLevelBox.addItem("Everyone");
			else
				streamLevelBox.addItem("Members of " + stream);
		}
		streamPanel.add(streamLevelBox);
		vPanel.add(buttonPanel);
		add(vPanel);

		setGlassEnabled(true);
		center();
	}
}
