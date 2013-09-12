package com.cs1530.group4.classweb.client;

import com.cs1530.group4.classweb.shared.Post;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPost extends Composite
{
	UserServiceAsync userService = UserService.Util.getInstance();
	boolean votedUp = false, votedDown = false;
	int upDownVotes;

	public UserPost(final Post post)
	{
		upDownVotes = post.getUpvotes() - post.getDownvotes();
		HorizontalPanel border = new HorizontalPanel();
		border.setBorderWidth(1);
		border.setWidth("100%");
		initWidget(border);

		VerticalPanel scorePanel = new VerticalPanel();
		scorePanel.getElement().getStyle().setProperty("marginLeft", "5px");
		scorePanel.getElement().getStyle().setProperty("marginRight", "5px");
		border.add(scorePanel);
		border.setCellWidth(scorePanel, "34px");

		final Image upArrow = new Image("images/default_up.png");
		final Image downArrow = new Image("images/default_down.png");
		final Label scoreLabel = new Label(String.valueOf(upDownVotes));
		upArrow.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if(votedDown)
					upDownVotes++;
				votedDown = false;
				downArrow.setUrl("images/default_down.png");
				if(votedUp)
				{
					scoreLabel.setText(String.valueOf(--upDownVotes));
					votedUp = false;
					upArrow.setUrl("images/default_up.png");
				}
				else
				{
					scoreLabel.setText(String.valueOf(++upDownVotes));
					votedUp = true;
					upArrow.setUrl("images/voted_up.png");
				}

				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					@Override
					public void onFailure(Throwable caught){}
					@Override
					public void onSuccess(Void v){}
				};
				userService.upvotePost(post.getPostKey(), callback);
			}
		});
		downArrow.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if(votedUp)
					upDownVotes--;
				votedUp = false;
				upArrow.setUrl("images/default_up.png");
				if(votedDown)
				{
					scoreLabel.setText(String.valueOf(++upDownVotes));
					votedDown = false;
					downArrow.setUrl("images/default_down.png");
				}
				else
				{
					scoreLabel.setText(String.valueOf(--upDownVotes));
					votedDown = true;
					downArrow.setUrl("images/voted_down.png");
				}
				
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					@Override
					public void onFailure(Throwable caught){}
					@Override
					public void onSuccess(Void v){}
				};
				userService.downvotePost(post.getPostKey(), callback);
			}
		});
		scorePanel.add(upArrow);
		upArrow.setSize("24px", "24px");

		scoreLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		scorePanel.add(scoreLabel);

		scorePanel.add(downArrow);
		downArrow.setSize("24px", "24px");

		VerticalPanel postPanel = new VerticalPanel();
		postPanel.setWidth("100%");
		border.add(postPanel);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		postPanel.add(horizontalPanel);

		Image image = new Image("contact_picture.png");
		image.getElement().getStyle().setProperty("marginRight", "10px");
		horizontalPanel.add(image);
		image.setSize("46px", "46px");

		VerticalPanel verticalPanel = new VerticalPanel();
		horizontalPanel.add(verticalPanel);

		Label lblUsername = new Label(post.getUsername());
		lblUsername.setStyleName("gwt-Label-bold");
		verticalPanel.add(lblUsername);

		DateTimeFormat dtf = new DateTimeFormat("h:mm a", new DefaultDateTimeFormatInfo()){};
		Label lblPosttime = new Label(dtf.format(post.getPostTime()));
		lblPosttime.setStyleName("gwt-Label-grey");
		verticalPanel.add(lblPosttime);

		HTML postContent = new HTML(post.getPostContent());
		postPanel.add(postContent);

		HTML separator = new HTML("<hr  style=\"width:100%;\" />");
		postPanel.add(separator);

		VerticalPanel commentPanel = new VerticalPanel();
		commentPanel.setSpacing(5);
		postPanel.add(commentPanel);

		getComments(commentPanel);
		
		PromptedTextBox addAComment = new PromptedTextBox("Add a comment...","promptText");
		addAComment.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				
			}
		});
		commentPanel.add(addAComment);
	}

	private void getComments(VerticalPanel commentPanel)
	{
		int numComments = Random.nextInt(3) + 1;
		for(int i = 0; i < numComments; i++)
			commentPanel.add(new PostComment());
	}

}
