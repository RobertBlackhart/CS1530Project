package com.cs1530.group4.addendum.client;

import java.sql.Date;

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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PostComment extends Composite implements MouseOverHandler, MouseOutHandler
{
	Image menu;
	CommentMenuPopup popup;

	public PostComment(final MainView main, final Comment comment, final Stream profile, UserPost userPost)
	{
		VerticalPanel rowPanel = new VerticalPanel();
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

		VerticalPanel verticalPanel = new VerticalPanel();
		horizontalPanel_1.add(verticalPanel);

		Anchor usernameLabel = new Anchor(comment.getUsername());
		usernameLabel.setStyleName("gwt-Label-bold");
		verticalPanel.add(usernameLabel);
		Label lblCommenttime = new Label(timeLabel);
		lblCommenttime.setStyleName("gwt-Label-grey");
		verticalPanel.add(lblCommenttime);
		
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
		popup = new CommentMenuPopup(main, menu, comment, comment.getUsername().equals(Cookies.getCookie("loggedIn")),userPost);
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
	}

	@Override
	public void onMouseOver(MouseOverEvent event)
	{
		menu.setVisible(true);
	}
}