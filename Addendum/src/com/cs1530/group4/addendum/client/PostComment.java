package com.cs1530.group4.addendum.client;

import java.sql.Date;
import java.util.ArrayList;

import com.cs1530.group4.addendum.shared.Comment;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FlowPanel;

public class PostComment extends Composite implements MouseOverHandler, MouseOutHandler
{
	Image menu, plusOneButton;
	CommentMenuPopup popup;
	UserServiceAsync userService = UserService.Util.getInstance();

	public PostComment(final MainView main, final Comment comment, final Stream profile, final UserPost userPost)
	{
		FlowPanel rowPanel = new FlowPanel();
		rowPanel.setStyleName("CommentPanelbackcolor");
		initWidget(rowPanel);
		rowPanel.setWidth("100%");

		String timeFormatString = "h:mm a";
		String editFormatString = "h:mm a";
		Date now = new Date(System.currentTimeMillis());
		if(comment.getCommentTime().getDate() != now.getDate())
			timeFormatString = "MMM d, yyyy";
		if(comment.getLastEdit() != null && comment.getLastEdit().getDate() != now.getDate())
			editFormatString = "MMM d, yyyy";
		DateTimeFormat dtf = new DateTimeFormat(timeFormatString, new DefaultDateTimeFormatInfo())
		{
		};
		DateTimeFormat editDtf = new DateTimeFormat(editFormatString, new DefaultDateTimeFormatInfo())
		{
		};
		String timeLabel = dtf.format(comment.getCommentTime());
		if(comment.getLastEdit() != null)
			timeLabel += " (last edit - " + editDtf.format(comment.getLastEdit()) + ")";

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setWidth("100%");

		addDomHandler(this, MouseOverEvent.getType());
		addDomHandler(this, MouseOutEvent.getType());

		HTML content = new HTML(comment.getContent());
		content.setStyleName("CommentSeperator");

		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel.add(horizontalPanel_1);

		Image image = new Image("/addendum/getImage?username=" + comment.getUsername());
		horizontalPanel_1.add(image);
		image.getElement().getStyle().setProperty("marginRight", "10px");
		image.setSize("28px", "28px");

		FlowPanel verticalPanel = new FlowPanel();
		horizontalPanel_1.add(verticalPanel);

		Anchor usernameLabel = new Anchor(comment.getUsername());
		usernameLabel.setStyleName("gwt-Label-bold");
		verticalPanel.add(usernameLabel);

		HorizontalPanel timePanel = new HorizontalPanel();
		Label lblCommenttime = new Label(timeLabel);
		lblCommenttime.setStyleName("gwt-Label-grey");

		final Label plusOnesLabel = new Label("+" + String.valueOf(comment.getPlusOnes()));
		plusOnesLabel.setStyleName("gwt-Label-grey-bold");
		if(comment.getPlusOnes() == 0)
			plusOnesLabel.setVisible(false);

		plusOneButton = new Image("/images/plus_one_default.png");
		plusOneButton.setStyleName("plusOneButton");
		plusOneButton.setVisible(false);
		if(comment.isPlusOned())
			plusOneButton.setUrl("/images/plus_one_checked.png");
		plusOneButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
				{

					@Override
					public void onFailure(Throwable caught)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(Boolean result)
					{
						comment.setPlusOned(result);
						
						if(result) //was increased
						{
							comment.setPlusOnes(comment.getPlusOnes()+1);
							plusOnesLabel.setText("+"+comment.getPlusOnes());
							plusOnesLabel.setVisible(true);
							plusOneButton.setUrl("/images/plus_one_checked.png");
						}
						else
						{
							comment.setPlusOnes(comment.getPlusOnes()-1);
							plusOnesLabel.setText("+"+comment.getPlusOnes());
							if(comment.getPlusOnes() == 0)
								plusOnesLabel.setVisible(false);
							plusOneButton.setUrl("/images/plus_one_default.png");
						}
						
						ArrayList<Comment> comments = userPost.post.getComments();
						for(int i=0; i<comments.size(); i++)
						{
							if(comments.get(i).getCommentKey() != null && comments.get(i).getCommentKey().equals(comment.getCommentKey()))
							{
								comments.remove(i);
								comments.add(i,comment);
								break;
							}
						}
						
						userPost.displayComments();
					}

				};
				userService.plusOne(comment.getCommentKey(),Cookies.getCookie("loggedIn"),callback);
			}
		});

		timePanel.add(lblCommenttime);
		timePanel.add(plusOnesLabel);
		timePanel.add(plusOneButton);
		verticalPanel.add(timePanel);

		if(profile != null)
		{
			usernameLabel.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					profile.postSearch("username:" + comment.getUsername());
				}
			});
		}

		menu = new Image("images/menu.png");
		menu.setSize("24px", "24px");
		menu.setVisible(false);
		menu.getElement().getStyle().setProperty("marginRight", "5px");
		popup = new CommentMenuPopup(main, menu, comment, comment.getUsername().equals(Cookies.getCookie("loggedIn")), userPost);
		menu.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if(popup.isShowing())
					popup.hide();
				else
					popup.showRelativeTo(menu);
			}
		});

		HorizontalPanel menuPanel = new HorizontalPanel();
		menuPanel.setWidth("100%");
		menuPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		menuPanel.add(menu);
		horizontalPanel.add(menuPanel);

		rowPanel.add(horizontalPanel);
		rowPanel.add(content);
	}

	@Override
	public void onMouseOut(MouseOutEvent event)
	{
		menu.setVisible(false);
		plusOneButton.setVisible(false);
	}

	@Override
	public void onMouseOver(MouseOverEvent event)
	{
		menu.setVisible(true);
		plusOneButton.setVisible(true);
	}
}