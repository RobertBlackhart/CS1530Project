package com.cs1530.group4.classweb.client;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PostComment extends Composite
{	
	public PostComment()
	{
		FlexTable flexTable = new FlexTable();
		initWidget(flexTable);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		flexTable.setWidget(0, 0, horizontalPanel);
		
		Image image = new Image("contact_picture.png");
		image.getElement().getStyle().setProperty("marginRight","10px");
		horizontalPanel.add(image);
		image.setSize("28px", "28px");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		
		Label lblUsername = new Label("username");
		lblUsername.setStyleName("gwt-Label-bold");
		verticalPanel.add(lblUsername);
		
		String hour = Random.nextInt(2) + "" + Random.nextInt(3);
		String minute = Random.nextInt(6) + "" + Random.nextInt(10);
		String ampm = "AM";
		if(Random.nextInt(2) == 1)
			ampm = "PM";
		if(Integer.parseInt(hour) == 0)
		{
			hour = "12";
			ampm = "AM";
		}
		Label lblCommenttime = new Label(hour+":"+minute+" "+ampm);
		lblCommenttime.setStyleName("gwt-Label-grey");
		verticalPanel.add(lblCommenttime);
		
		horizontalPanel.add(verticalPanel);
		
		Label lblCommentContent = new Label("comment content comment content comment content comment content comment content comment content comment content comment content comment content comment content comment content ");
		flexTable.setWidget(1, 0, lblCommentContent);
	}

}
