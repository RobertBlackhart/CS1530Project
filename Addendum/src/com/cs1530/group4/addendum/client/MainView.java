package com.cs1530.group4.addendum.client;

import com.cs1530.group4.addendum.shared.User;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the {@link EntryPoint} for the application.  It will display any UI needed by the application as well as manage it's history stack.
 */
public class MainView implements EntryPoint, ValueChangeHandler<String>
{
	
	/** The content panel. */
	private VerticalPanel contentPanel;
	
	/** A reference to this MainView object. */
	private MainView main = this;

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad()
	{
		initialize();
		History.fireCurrentHistoryState();
	}

	/**
	 * Clears the current content and sets the content of the UI.  Also adds the previous screen to the applications history stack.
	 *
	 * @param content the content
	 * @param historyToken the history token
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public void setContent(Widget content, String historyToken)
	{
		History.newItem(historyToken);
		contentPanel.clear();
		contentPanel.add(content);
	}

	/**
	 * Initialize the application's UI.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	private void initialize()
	{
		RootPanel rootPanel = RootPanel.get("container");
		contentPanel = new VerticalPanel();
		rootPanel.add(contentPanel);
		
		History.addValueChangeHandler(this);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event)
	{
		if(contentPanel == null)
			initialize();

		Widget content = null;			
		String[] historyToken;
		if(event.getValue() == null || event.getValue().equals("")) //fixes bug that happens when using ssl
			historyToken = "stream".split("-");
		else
			historyToken = event.getValue().split("-");
		
		// Parse the history token
		if(historyToken[0].equals("setCookie"))
		{
			String username = historyToken[1];
			Cookies.setCookie("loggedIn", username);
			Storage localStorage = Storage.getLocalStorageIfSupported();
			localStorage.setItem("loggedIn", new User(username).serialize());
			
			History.newItem("stream");
			return;
		}
		else if(historyToken[0].equals("passwordReset"))
		{
			new NewPasswordDialog(historyToken[1],this);
			return;
		}
		else if(historyToken[0].equals("profile"))
		{
			content = new Profile(main,historyToken[1],true);
		}
		else if(historyToken[0].equals("login"))
			content = new Login(main);
		else if(historyToken[0].equals("stream"))
		{
			String loggedUser = Cookies.getCookie("loggedIn");
			if(loggedUser == null)
				content = new Login(main);
			else
				content = new Stream(main);
		}
		else if(historyToken[0].equals("adminPanel"))
			content = new AdminPanel(main);
		else
		{
			String username = Cookies.getCookie("loggedIn");
			if(username == null)
				content = new Login(main);
			else
				content = new Stream(main);
		}
		
		contentPanel.clear();
		contentPanel.add(content);
	}
}
