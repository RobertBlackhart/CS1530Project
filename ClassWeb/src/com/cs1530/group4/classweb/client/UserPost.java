package com.cs1530.group4.classweb.client;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPost extends Composite
{

	public UserPost()
	{
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setBorderWidth(1);
		initWidget(vPanel);
		
		FlexTable flexTable = new FlexTable();
		vPanel.add(flexTable);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		flexTable.setWidget(0, 0, horizontalPanel);
		
		Image image = new Image("contact_picture.png");
		image.getElement().getStyle().setProperty("marginRight","10px");
		horizontalPanel.add(image);
		image.setSize("46px", "46px");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		horizontalPanel.add(verticalPanel);
		
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
		Label lblPosttime = new Label(hour+":"+minute+" "+ampm);
		lblPosttime.setStyleName("gwt-Label-grey");
		verticalPanel.add(lblPosttime);
		
		Label lblNewLabel = new Label("post content post content post content post contentpost content post content post content post contentpost content post content post content post contentpost content post content post content post contentpost content post content post content post contentpost content post content post content post contentpost content post content post content post contentpost content post content post content post content");
		flexTable.setWidget(1, 0, lblNewLabel);
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 1);
		
		HTML panel = new HTML("<hr  style=\"width:100%;\" />");
		flexTable.setWidget(2, 0, panel);
		
		VerticalPanel commentPanel = new VerticalPanel();
		commentPanel.setSpacing(5);
		flexTable.setWidget(3, 0, commentPanel);
		
		getComments(commentPanel);
	}
	
	private void getComments(VerticalPanel commentPanel)
	{
		int numComments = Random.nextInt(10)+1;
		for(int i=0; i<numComments; i++)
			commentPanel.add(new PostComment());
	}

}
