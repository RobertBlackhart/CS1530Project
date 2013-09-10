package com.cs1530.group4.classweb.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class UserPost extends Composite
{
	boolean votedUp = false, votedDown = false;
	int score;
	
	public UserPost()
	{
		score = Random.nextInt(1000);
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setBorderWidth(1);
		initWidget(hPanel);
		
		VerticalPanel verticalPanel_1 = new VerticalPanel();
		hPanel.add(verticalPanel_1);
		
		final Image upArrow = new Image("images/default_up.png");
		final Image downArrow = new Image("images/default_down.png");
		final Label scoreLabel = new Label(String.valueOf(score));
		upArrow.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if(votedDown)
					score++;
				votedDown = false;
				downArrow.setUrl("images/default_down.png");
				if(votedUp)
				{
					scoreLabel.setText(String.valueOf(--score));
					votedUp = false;
					upArrow.setUrl("images/default_up.png");
				}
				else
				{
					scoreLabel.setText(String.valueOf(++score));
					votedUp = true;
					upArrow.setUrl("images/voted_up.png");
				}
			}
		});
		downArrow.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if(votedUp)
					score--;
				votedUp = false;
				upArrow.setUrl("images/default_up.png");
				if(votedDown)
				{
					scoreLabel.setText(String.valueOf(++score));
					votedDown = false;
					downArrow.setUrl("images/default_down.png");
				}
				else
				{
					scoreLabel.setText(String.valueOf(--score));
					votedDown = true;
					downArrow.setUrl("images/voted_down.png");
				}
			}
		});
		verticalPanel_1.add(upArrow);
		upArrow.setSize("24px", "24px");
		
		scoreLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_1.add(scoreLabel);
		
		verticalPanel_1.add(downArrow);
		downArrow.setSize("24px", "24px");
		
		VerticalPanel vPanel = new VerticalPanel();
		hPanel.add(vPanel);
		
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
