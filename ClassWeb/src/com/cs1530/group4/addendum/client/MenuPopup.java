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

public class MenuPopup extends PopupPanel implements MouseOverHandler, MouseOutHandler
{
	UserServiceAsync userService = UserService.Util.getInstance();
	MenuPopup popup = this;
	Widget relativeWidget;
	boolean open = false;

	public MenuPopup(final MainView main, Widget w, final Post post)
	{
		relativeWidget = w;
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
							main.setContent(new Profile(main, user), "profile-" + user);
						}
					};
					userService.deletePost(post.getPostKey(), callback);
				}
			}
		});
		addDomHandler(this, MouseOutEvent.getType());
		addDomHandler(this, MouseOverEvent.getType());

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(5);
		vPanel.add(editPost);
		vPanel.add(deletePost);
		add(vPanel);
	}

	public boolean isOpen()
	{
		return open;
	}

	public void setOpen(boolean open)
	{
		this.open = open;
	}

	@Override
	public void onMouseOut(MouseOutEvent event)
	{
		relativeWidget.setVisible(false);
	}

	@Override
	public void onMouseOver(MouseOverEvent event)
	{
		relativeWidget.setVisible(true);
	}
}
