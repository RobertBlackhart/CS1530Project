package com.cs1530.group4.addendum.client;

import java.util.ArrayList;
import java.util.Collections;

import com.cs1530.group4.addendum.shared.Post;
import com.cs1530.group4.addendum.shared.User;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * The Stream class is the main view for the users of Addendum. It allows them
 * to interact with other user's content and post content of their own.
 */
public class Stream extends Composite
{
	/** The application's MainView. */
	MainView main;

	/** The stream level that we are currently filtering by. */
	String streamLevel = "all";

	/** The current sort method. */
	String sortMethod = "Popular";

	/** An object representing the currently logged in user. */
	User user;

	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();

	/** The current tab. */
	HTMLPanel currentTab;

	/** The class panel. */
	VerticalPanel classPanel;

	/** The offset of post results to fetch from. */
	int startIndex = 0;

	/** The offset of the search results to fetch from. */
	int searchStart = 0;

	/** The nextPage anchor. */
	Anchor nextPage;

	/** The prevPage anchor. */
	Anchor prevPage;

	/** The tab panel. */
	TabPanel tabPanel;

	/** The search panel. */
	HTMLPanel searchPanel;

	/** A reference to this Stream object. */
	Stream stream = this;

	/** The addClass button. */
	Button addClassButton;

	/**
	 * The Stream class is responsible for displaying posts to the user as well
	 * as all of the other UI associated with creating posts and comments.
	 * 
	 * @param m
	 *            the MainView of the application
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link #getClasses()}
	 */
	public Stream(MainView m)
	{
		main = m;
		Storage localStorage = Storage.getLocalStorageIfSupported();
		User u = User.deserialize(localStorage.getItem("loggedIn"));
		user = u;
		HTMLPanel vPanel = new HTMLPanel("");
		initWidget(vPanel);

		HTMLPanel grid = new HTMLPanel("");
		vPanel.add(grid);

		final PromptedTextBox searchBox = new PromptedTextBox("Search for a post...", "promptText");
		grid.add(searchBox);
		searchBox.setStyleName("profileSearchbox");

		Button createPost = new Button("Create a new post");
		grid.add(createPost);
		createPost.setHeight("30px");
		createPost.setStyleName("createPostButton");
		createPost.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				NewPost editor = new NewPost(main, user.getCourseList(), null);
				editor.show();
			}
		});
		searchBox.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
				{
					postSearch(searchBox.getText());
				}
			}
		});

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("profileMainPanel");
		vPanel.add(hPanel);

		HTMLPanel userPanel = new HTMLPanel("<div></div>");
		userPanel.setStyleName("userPanel");
		hPanel.add(userPanel);

		HTMLPanel absolutePanel = new HTMLPanel("");
		absolutePanel.setStyleName("profilePic");
		userPanel.add(absolutePanel);

		Image image = new Image("/addendum/getImage?username=" + user.getUsername());
		image.setStyleName("streamProfileImage");
		final Label changeImageLabel = new Label("Change Image");
		changeImageLabel.setStyleName("changeProfilePictureLabel");
		MouseOverHandler mouseOver = new MouseOverHandler()
		{
			@Override
			public void onMouseOver(MouseOverEvent event)
			{
				changeImageLabel.setVisible(true);
			}
		};
		MouseOutHandler mouseOut = new MouseOutHandler()
		{
			@Override
			public void onMouseOut(MouseOutEvent event)
			{
				changeImageLabel.setVisible(false);
			}
		};
		ClickHandler changePictureHandler = new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ProfilePictureUpload profilePic = new ProfilePictureUpload(main, user.getUsername());
				profilePic.show();
			}
		};
		image.addMouseOverHandler(mouseOver);
		changeImageLabel.addMouseOverHandler(mouseOver);
		image.addMouseOutHandler(mouseOut);
		changeImageLabel.addMouseOutHandler(mouseOut);
		image.addClickHandler(changePictureHandler);
		changeImageLabel.addClickHandler(changePictureHandler);

		absolutePanel.add(image);
		image.setSize("128px", "128px");

		changeImageLabel.setVisible(false);
		absolutePanel.add(changeImageLabel);

		HTMLPanel horizontalPanel = new HTMLPanel("<div></div>");
		horizontalPanel.setStyleName("usernamePanel");
		userPanel.add(horizontalPanel);

		Anchor usernameLabel = new Anchor(user.getUsername());
		usernameLabel.setStyleName("USername");
		usernameLabel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				main.setContent(new Profile(main, user.getUsername(), true), "profile-" + user.getUsername());
			}
		});
		horizontalPanel.add(usernameLabel);

		Anchor logoutAnchor = new Anchor("Logout");
		logoutAnchor.setStyleName("logout");
		horizontalPanel.add(logoutAnchor);
		logoutAnchor.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				Cookies.removeCookie("loggedIn");
				Storage localStorage = Storage.getLocalStorageIfSupported();
				localStorage.removeItem("loggedIn");
				main.setContent(new Login(main), "login");
			}
		});

		classPanel = new VerticalPanel();
		classPanel.setSpacing(3);
		classPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		classPanel.setStyleName("classPanel");
		userPanel.add(classPanel);

		HTMLPanel nextPrevPanel = new HTMLPanel("<div></div>");
		nextPrevPanel.setStyleName("nextPrevPanel");
		nextPage = new Anchor("Next 10 Posts");
		nextPage.setStyleName("courseAnchor");
		nextPage.setVisible(false);
		nextPage.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				prevPage.setVisible(true);
				if(tabPanel.getTabBar().getTabHTML(tabPanel.getTabBar().getSelectedTab()).equals("Search Results"))
					searchStart += 10;
				else
					startIndex += 10;
				ArrayList<String> streamLevels = new ArrayList<String>();
				streamLevels.add(streamLevel);
				if(streamLevel.equals("all"))
					streamLevels.addAll(user.getCourseList());
				getPosts(currentTab, streamLevels, sortMethod);
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
				if(tabPanel.getTabBar().getTabHTML(tabPanel.getTabBar().getSelectedTab()).equals("Search Results"))
				{
					searchStart -= 10;
					if(searchStart < 10)
						prevPage.setVisible(false);
				}
				else
				{
					startIndex -= 10;
					if(startIndex < 10)
						prevPage.setVisible(false);
				}
				ArrayList<String> streamLevels = new ArrayList<String>();
				streamLevels.add(streamLevel);
				if(streamLevel.equals("all"))
					streamLevels.addAll(user.getCourseList());
				getPosts(currentTab, streamLevels, sortMethod);
			}
		});
		nextPrevPanel.add(prevPage);
		nextPrevPanel.add(nextPage);
		vPanel.add(nextPrevPanel);

		tabPanel = new TabPanel();
		tabPanel.setSize("800px", "89");

		HTMLPanel popularUpdatesPanel = new HTMLPanel("");
		popularUpdatesPanel.setWidth("100%");
		HTMLPanel newUpdatesPanel = new HTMLPanel("");
		newUpdatesPanel.setWidth("100%");
		tabPanel.add(popularUpdatesPanel, "Popular", false);
		tabPanel.add(newUpdatesPanel, "New", false);
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>()
		{
			@Override
			public void onSelection(SelectionEvent<Integer> event)
			{
				if(tabPanel.getTabBar().getTabHTML(event.getSelectedItem()).equals("Search Results"))
				{
					if(searchStart < 10)
						prevPage.setVisible(false);
					else
						prevPage.setVisible(true);
					return;
				}

				startIndex = 0;
				prevPage.setVisible(false);

				sortMethod = tabPanel.getTabBar().getTabHTML(event.getSelectedItem());
				currentTab = (HTMLPanel) tabPanel.getWidget(event.getSelectedItem());
				if(user.getCourseList() != null)
				{
					ArrayList<String> streamLevels = new ArrayList<String>();
					streamLevels.add(streamLevel);
					if(streamLevel.equals("all"))
						streamLevels.addAll(user.getCourseList());

					getPosts(currentTab, streamLevels, sortMethod);
				}
			}
		});

		currentTab = (HTMLPanel) tabPanel.getWidget(0);

		getClasses();
		tabPanel.selectTab(0);

		hPanel.add(tabPanel);
		setStyleName("profilePanel");
	}

	/**
	 * Fetches the posts from the datastore that have a streamLevel == any of
	 * the streamLevels in the ArrayList provided. After fetching - sorts,
	 * creates and adds the posts to the updatesPanel provided.
	 * 
	 * @param updatesPanel
	 *            The panel in which to display a list of posts returned by the
	 *            server
	 * @param streamLevels
	 *            A list of stream levels (aka course names) which to filter the
	 *            posts by
	 * @param sortMethod
	 *            A string signifying in which order to sort the posts (by date,
	 *            by score, etc)
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getPosts(int, ArrayList, String, String)}
	 */
	private void getPosts(final HTMLPanel updatesPanel, ArrayList<String> streamLevels, final String sortMethod)
	{
		updatesPanel.clear();
		AsyncCallback<ArrayList<Post>> callback = new AsyncCallback<ArrayList<Post>>()
		{
			@Override
			public void onFailure(Throwable caught)
			{}

			@Override
			public void onSuccess(ArrayList<Post> posts)
			{
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
					updatesPanel.add(new UserPost(main, post));
				}

				if(count == 0)
				{
					Label noPostsFound = new Label("No posts found");
					noPostsFound.setStyleName("noPosts");
					updatesPanel.add(noPostsFound);
				}
			}
		};
		userService.getPosts(startIndex, streamLevels, user.getUsername(), sortMethod, callback);
	}

	/**
	 * Populate the user's class panel with the classes they belong to. Call
	 * after adding or removing a class or to initialize the stream.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	private void getClasses()
	{
		ClickHandler courseClick = new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				for(int i = 0; i < classPanel.getWidgetCount(); i++)
				{
					if(classPanel.getWidget(i) instanceof HTMLPanel)
						((HTMLPanel) classPanel.getWidget(i)).getWidget(1).setStyleName("courseAnchor");
					if(classPanel.getWidget(i) instanceof Anchor)
						classPanel.getWidget(i).setStyleName("courseAnchor");
				}
				Anchor source = (Anchor) event.getSource();
				source.setStyleName("arrow_box");
				startIndex = 0;
				prevPage.setVisible(false);
				streamLevel = source.getText().trim();
				if(streamLevel.equals("ALL CLASSES"))
					streamLevel = "all";
				ArrayList<String> streamLevels = new ArrayList<String>();
				streamLevels.add(streamLevel);
				if(streamLevel.equals("all"))
					streamLevels.addAll(user.getCourseList());
				getPosts(currentTab, streamLevels, sortMethod);
			}
		};
		Anchor allAnchor = new Anchor("All Classes");
		allAnchor.setText("ALL CLASSES");
		allAnchor.setStyleName("arrow_box");
		allAnchor.addClickHandler(courseClick);

		classPanel.clear();
		classPanel.add(allAnchor);

		addClassButton = new Button("Add A Class");
		addClassButton.setStyleName("ADCButton-addRemoveClasses");
		addClassButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				new ClassSearch(main);
			}
		});
		for(final String course : user.getCourseList())
		{
			Anchor courseAnchor = new Anchor(course);
			courseAnchor.setStyleName("courseAnchor");
			courseAnchor.addClickHandler(courseClick);

			Image removeCourse = new Image("/images/delete.png");
			removeCourse.setStyleName("removeCourse");
			removeCourse.setTitle("Remove " + course + "from my list");
			removeCourse.setAltText(course.trim());
			removeCourse.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					if(Window.confirm("Are you sure you want to permanently remove this class?"))
						removeCourse(((Image) event.getSource()).getAltText());
				}
			});
			HTMLPanel classRow = new HTMLPanel("");
			classRow.add(removeCourse);
			classRow.add(courseAnchor);
			classPanel.add(classRow);
		}
		classPanel.add(addClassButton);
	}

	/**
	 * Removes the specified course from the users list of courses.
	 * 
	 * @param course
	 *            The course name to be removed (in the format
	 *            'CourseName+CourseNumber')
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#removeCourse(String, String)}
	 */
	private void removeCourse(String course)
	{
		AsyncCallback<User> callback = new AsyncCallback<User>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				Window.alert("There was a problem deleting this class.  Please check your connection and try again.");
			}

			@Override
			public void onSuccess(User u)
			{
				user = u;
				Storage localStorage = Storage.getLocalStorageIfSupported();
				localStorage.setItem("loggedIn", u.serialize());
				getClasses();
			}
		};

		userService.removeCourse(course, Cookies.getCookie("loggedIn"), callback);
	}

	/**
	 * Searches the database for posts which match the given query. It will then
	 * display all results in a new panel.
	 * 
	 * @param searchString
	 *            the search string
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#postSearch(int, String, String)}
	 */
	public void postSearch(final String searchString)
	{
		AsyncCallback<ArrayList<Post>> callback = new AsyncCallback<ArrayList<Post>>()
		{
			@Override
			public void onFailure(Throwable caught)
			{}

			@Override
			public void onSuccess(ArrayList<Post> posts)
			{
				if(searchPanel == null)
				{
					searchPanel = new HTMLPanel("");
					searchPanel.setWidth("100%");
					tabPanel.add(searchPanel, "Search Results");
				}
				tabPanel.selectTab(2);
				searchPanel.clear();

				if(posts.size() == 0)
				{
					searchPanel.clear();
					Label noResults = new Label("No results found for '" + searchString + "'");
					noResults.setStyleName("noPosts");
					searchPanel.add(noResults);
					return;
				}

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
					searchPanel.add(new UserPost(main, post));
				}
			}
		};
		userService.postSearch(searchStart, searchString, user.getUsername(), callback);
	}
}