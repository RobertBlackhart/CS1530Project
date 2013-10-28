package com.cs1530.group4.addendum.client;

import java.util.ArrayList;

import com.cs1530.group4.addendum.shared.Post;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This represents the UI for actions to be done on a {@link UserPost}
 */
public class MenuPopup extends PopupPanel implements MouseOverHandler, MouseOutHandler
{
	
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();
	
	/** A reference to this MenuPopup object. */
	MenuPopup popup = this;
	
	/** The widget to display this relative to. */
	Widget relativeWidget;

	/**
	 * Instantiates a new MenuPopup.
	 *
	 * @param main the applicatin's {@link MainView}
	 * @param w the widget to display this relative to
	 * @param post the {@link Post} that will be acted on
	 * 
	 * @.accessed None
	 * @.changed None
	 * @.called None
	 */
	public MenuPopup(final MainView main, Widget w, final Post post)
	{
		super(true);
		setStyleName("MenuPopUp");
		relativeWidget = w;
		addAutoHidePartner(w.getElement());
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(5);
		if(post.getUsername().equals(Cookies.getCookie("loggedIn")))
		{
			Label editPost = new Label("Edit Post");
			editPost.setStyleName("menuitem");
			editPost.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					popup.hide();
					ArrayList<String> originalStream = new ArrayList<String>();
					originalStream.add(post.getStreamLevel());
					NewPost editor = new NewPost(main, originalStream, post);
					editor.show();
				}
			});
			Label deletePost = new Label("Delete Post");
			deletePost.setStyleName("menuitem");
			deletePost.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					popup.hide();
					if(Window.confirm("Are you sure you want to delete this post?"))
					{
						AsyncCallback<Void> callback = new AsyncCallback<Void>()
						{
							@Override
							public void onFailure(Throwable caught)
							{
							}

							@Override
							public void onSuccess(Void v)
							{
								String user = Cookies.getCookie("loggedIn");
								main.setContent(new Stream(main), "profile-" + user);
							}
						};
						userService.deletePost(post.getPostKey(), callback);
					}
				}
			});

			vPanel.add(editPost);
			vPanel.add(deletePost);
		}
		else
		{
			Label flagPost = new Label("Report Post");
			flagPost.setStyleName("menuitem");
			flagPost.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					popup.hide();
					new FlagForm(post.getPostKey(),FlagForm.POST);
				}
			});
			vPanel.add(flagPost);
		}
		addDomHandler(this, MouseOutEvent.getType());
		addDomHandler(this, MouseOverEvent.getType());

		add(vPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
	 */
	@Override
	public void onMouseOut(MouseOutEvent event)
	{
		relativeWidget.setVisible(false);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
	 */
	@Override
	public void onMouseOver(MouseOverEvent event)
	{
		relativeWidget.setVisible(true);
	}
}
