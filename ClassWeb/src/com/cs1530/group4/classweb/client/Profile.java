package com.cs1530.group4.classweb.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Profile extends Composite
{
	MainView main;
	String username, streamLevel = "all";
	UserServiceAsync userService = UserService.Util.getInstance();

	public Profile(MainView m, String u)
	{
		main = m;
		username = u;
		DockPanel dockPanel = new DockPanel();
		initWidget(dockPanel);

		VerticalPanel userPanel = new VerticalPanel();
		userPanel.getElement().getStyle().setProperty("marginLeft", "10px");
		userPanel.getElement().getStyle().setProperty("marginRight", "30px");
		dockPanel.add(userPanel, DockPanel.WEST);

		Image image = new Image("contact_picture.png");
		userPanel.add(image);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		userPanel.add(horizontalPanel);

		Label usernameLabel = new Label(username);
		usernameLabel.getElement().getStyle().setProperty("marginRight", "10px");
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
		usernameLabel.getElement().getStyle().setProperty("marginBottom", "30px");

		VerticalPanel classPanel = new VerticalPanel();
		classPanel.setSpacing(3);
		userPanel.add(classPanel);

		Button addRemove = new Button("Add/Remove Classes");
		addRemove.getElement().getStyle().setProperty("marginTop", "10px");
		addRemove.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				main.setContent(new ClassSearch(main));
				//TODO: implement class removal screen
			}
		});
		Anchor allAnchor = new Anchor("All Classes");
		allAnchor.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				// TODO: show all stream
			}
		});
		getClasses(classPanel, addRemove, allAnchor);

		final TabPanel tabPanel = new TabPanel();
		dockPanel.add(tabPanel, DockPanel.CENTER);

		VerticalPanel popularUpdatesPanel = new VerticalPanel();
		VerticalPanel newUpdatesPanel = new VerticalPanel();
		tabPanel.add(popularUpdatesPanel, "Popular", false);
		tabPanel.add(newUpdatesPanel, "New", false);
		tabPanel.getElement().getStyle().setProperty("marginRight", "10px");
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>()
		{
			@Override
			public void onSelection(SelectionEvent<Integer> event)
			{
				int tabId = event.getSelectedItem();
				VerticalPanel panel = (VerticalPanel) tabPanel.getWidget(tabId);
				getPosts(panel,streamLevel);
			}
		});
		popularUpdatesPanel.setSpacing(15);
		newUpdatesPanel.setSpacing(15);

		tabPanel.selectTab(0);
		tabPanel.setSize("100%","100%");
		getPosts(popularUpdatesPanel,streamLevel);
	}

	private void getPosts(VerticalPanel updatesPanel, String streamLevel)
	{
		int numPosts = Random.nextInt(10) + 1;
		for(int i = 0; i < numPosts; i++)
		{
			updatesPanel.add(new UserPost());
		}
	}

	private void getClasses(final VerticalPanel classPanel, final Button addRemove, final Anchor allAnchor)
	{
		AsyncCallback<ArrayList<String>> callback = new AsyncCallback<ArrayList<String>>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
			}

			@Override
			public void onSuccess(ArrayList<String> courses)
			{
				classPanel.clear();
				classPanel.add(allAnchor);
				for(final String course : courses)
				{
					Anchor courseAnchor = new Anchor(course);
					courseAnchor.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(ClickEvent event)
						{
							streamLevel = course;
						}
					});
					classPanel.add(courseAnchor);
				}
				classPanel.add(addRemove);
			}
		};

		userService.getUserCourses(username, callback);
	}
}
