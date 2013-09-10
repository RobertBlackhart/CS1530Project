package com.cs1530.group4.classweb.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Profile extends Composite
{
	MainView main;
	
	public Profile(MainView m, String username)
	{
		main = m;
		DockPanel dockPanel = new DockPanel();
		initWidget(dockPanel);
		
		VerticalPanel userPanel = new VerticalPanel();
		userPanel.getElement().getStyle().setProperty("marginLeft","10px");
		userPanel.getElement().getStyle().setProperty("marginRight","30px");
		dockPanel.add(userPanel,DockPanel.WEST);
		
		VerticalPanel updatesPanel = new VerticalPanel();
		updatesPanel.setSpacing(15);
		updatesPanel.getElement().getStyle().setProperty("marginRight","10px");
		dockPanel.add(updatesPanel,DockPanel.CENTER);
		
		getPosts(updatesPanel);
		
		Image image = new Image("contact_picture.png");
		userPanel.add(image);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		userPanel.add(horizontalPanel);
		
		Label usernameLabel = new Label(username);
		usernameLabel.getElement().getStyle().setProperty("marginRight","10px");
		horizontalPanel.add(usernameLabel);
		
		Anchor logoutAnchor = new Anchor("Logout");
		horizontalPanel.add(logoutAnchor);
		logoutAnchor.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				Cookies.removeCookie("loggedIn");
				main.setContent(new Login(main));
			}
		});
		usernameLabel.getElement().getStyle().setProperty("marginBottom","30px");
		
		VerticalPanel classPanel = new VerticalPanel();
		userPanel.add(classPanel);
		
		getClasses(classPanel);
		Anchor addRemoveClassesAnchor = new Anchor("Add/Remove Classes");
		addRemoveClassesAnchor.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				//TODO: implement class addition/removal screen
			}
		});
		classPanel.add(addRemoveClassesAnchor);
	}
	
	private void getPosts(VerticalPanel updatesPanel)
	{
		int numPosts = Random.nextInt(10)+1;
		for(int i=0; i<numPosts; i++)
		{
			updatesPanel.add(new UserPost());
		}
	}
	
	private void getClasses(VerticalPanel classPanel)
	{
		int numClasses = Random.nextInt(10)+1;
		for(int i=0; i<numClasses; i++)
		{
			Label lblClass = new Label("Class "+(i+1));
			classPanel.add(lblClass);
		}
	}
}
