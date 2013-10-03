package com.cs1530.group4.addendum.client;

import java.sql.Date;

import com.cs1530.group4.addendum.shared.Comment;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PostComment extends Composite
{	
	public PostComment(final Comment comment, final Profile profile)
	{
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("CommentPanelbackcolor");
		initWidget(flexTable);
		flexTable.setWidth("100%");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		flexTable.setWidget(0, 0, horizontalPanel);
		
		Image image = new Image("/addendum/getImage?username="+comment.getUsername());
		image.getElement().getStyle().setProperty("marginRight","10px");
		horizontalPanel.add(image);
		image.setSize("28px", "28px");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		
		Anchor usernameLabel = new Anchor(comment.getUsername());
		if(profile != null)
		{
			usernameLabel.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					profile.postSearch("username:"+comment.getUsername());
				}
			});
		}
		usernameLabel.setStyleName("gwt-Label-bold");
		verticalPanel.add(usernameLabel);
		
		String timeFormatString = "h:mm a";
		String editFormatString = "h:mm a";
		Date now = new Date(System.currentTimeMillis());
		if(comment.getCommentTime().getDate() != now.getDate())
			timeFormatString = "MMM d, yyyy";
		if(comment.getLastEdit() != null && comment.getLastEdit().getDate() != now.getDate())
			editFormatString = "MMM d, yyyy";
		DateTimeFormat dtf = new DateTimeFormat(timeFormatString, new DefaultDateTimeFormatInfo()){};
		DateTimeFormat editDtf = new DateTimeFormat(editFormatString, new DefaultDateTimeFormatInfo()){};
		String timeLabel = dtf.format(comment.getCommentTime());
		if(comment.getLastEdit() != null)
			timeLabel += editDtf.format(comment.getLastEdit());
		Label lblCommenttime = new Label(timeLabel);
		lblCommenttime.setStyleName("gwt-Label-grey");
		verticalPanel.add(lblCommenttime);
		
		horizontalPanel.add(verticalPanel);
		
		HTML content = new HTML(comment.getContent());
		content.setStyleName("CommentSeperator");
		flexTable.setWidget(1, 0, content);
		setStyleName("");
	}

}