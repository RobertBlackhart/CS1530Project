package com.cs1530.group4.addendum.client;

import java.util.ArrayList;
import java.util.Collections;

import com.cs1530.group4.addendum.shared.Post;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * This represents the UI for a user's profile page.  It lists their personal information and their achievements.
 */
public class Profile extends Composite
{
	
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();
	
	/** The username associated with this profile */
	String username;
	
	/** The application's MainView. */
	MainView main;
	
	//** The offset of post results to fetch from. */
	int startIndex = 0;
	
	/** The nextPage anchor. */
	Anchor nextPage;
	
	/** The prevPage anchor. */
	Anchor prevPage;

	/**
	 * Instantiates a new Profile.
	 *
	 * @param m a reference to the application's {@link MainView}
	 * @param u the username associated with this profile
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public Profile(MainView m, String u)
	{
		main = m;
		username = u;
		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		verticalPanel.setWidth("100%");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel);
		
		Image profileImage = new Image("/addendum/getImage?username="+username);
		horizontalPanel.add(profileImage);
		profileImage.setSize("256", "256");
		
		Label usernameLabel = new Label(username);
		usernameLabel.setStyleName("gwt-Label-User");
		horizontalPanel.add(usernameLabel);
		
		final VerticalPanel postPanel = new VerticalPanel();
		postSearch("username:"+username,postPanel);
		
		nextPage = new Anchor("Next 10 Posts");
		nextPage.setStyleName("courseAnchor");
		nextPage.setVisible(false);
		nextPage.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				prevPage.setVisible(true);
				startIndex += 10;
				postSearch("username:"+username,postPanel);
			}
		});
		prevPage = new Anchor("Prev 10 Posts");
		prevPage.setStyleName("courseAnchor");
		prevPage.setVisible(false);
		prevPage.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				nextPage.setVisible(true);
				startIndex -= 10;
				if(startIndex < 10)
					prevPage.setVisible(false);
			
				postSearch("username:"+username,postPanel);
			}
		});
		
		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		HorizontalPanel nextPrevPanel = new HorizontalPanel();
		nextPrevPanel.add(prevPage);
		nextPrevPanel.add(nextPage);
		centerPanel.add(nextPrevPanel);
		centerPanel.setWidth("100%");
		
		verticalPanel.add(postPanel);
		verticalPanel.add(centerPanel);
	}
	
	/**
	 * Searches the database for posts which match the given query.  It will then display all results in a new panel.
	 *
	 * @param searchString the search string
	 * @param postPanel the {@link VerticalPanel} to attach the posts to
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#postSearch(int, String, String)}
	 */
	public void postSearch(final String searchString, final VerticalPanel postPanel)
	{
		AsyncCallback<ArrayList<Post>> callback = new AsyncCallback<ArrayList<Post>>()
		{
			@Override
			public void onFailure(Throwable caught)
			{}

			@Override
			public void onSuccess(ArrayList<Post> posts)
			{
				if(posts.size() == 0)
				{
					postPanel.clear();
					postPanel.add(new Label("No results found for '" + searchString + "'"));
					return;
				}
				postPanel.clear();

				Collections.sort(posts, Post.PostScoreComparator);
				int count = 0;
				for(Post post : posts)
				{
					count++;
					if(count == 11)
					{
						nextPage.setVisible(true);
						break;
					}

					nextPage.setVisible(false);
					postPanel.add(new UserPost(main, post));
				}
			}
		};
		userService.postSearch(startIndex, searchString, username, callback);
	}
}