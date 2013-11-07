package com.cs1530.group4.addendum.client;

import java.util.ArrayList;

import com.cs1530.group4.addendum.shared.Comment;
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
 * This class represents a popup menu that contains actions to be performed on a comment
 */
public class CommentMenuPopup extends PopupPanel implements MouseOverHandler, MouseOutHandler
{
	
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();
	
	/** A reference to this CommentMenuPopup object. */
	CommentMenuPopup popup = this;
	
	/** The widget to display this popup relative to. */
	Widget relativeWidget;

	/**
	 * Instantiates a new comment menu popup.
	 *
	 * @param main the application's {@link MainView}
	 * @param w the {@link Widget} to show this popup relative to
	 * @param comment the {@link Comment} that this popup will act on
	 * @param userPost the {@link UserPost} that this is displayed as a part of
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public CommentMenuPopup(final MainView main, Widget w, final Comment comment, final UserPost userPost)
	{
		super(true);
		setStyleName("MenuPopUp");
		relativeWidget = w;
		addAutoHidePartner(w.getElement());
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(5);
		if(comment.getUsername().equals(Cookies.getCookie("loggedIn")))
		{
			Label editComment = new Label("Edit Comment");
			editComment.setStyleName("menuitem");
			editComment.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					popup.hide();
					userPost.showCommentBox(comment);
				}
			});
			Label deleteComment = new Label("Delete Comment");
			deleteComment.setStyleName("menuitem");
			deleteComment.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					popup.hide();
					if(Window.confirm("Are you sure you want to delete this comment?"))
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
								ArrayList<Comment> comments = userPost.post.getComments();
								for(int i=0; i<comments.size(); i++)
								{
									if(comments.get(i).getCommentKey() != null && comments.get(i).getCommentKey().equals(comment.getCommentKey()))
									{
										comments.remove(i);
										break;
									}
								}
								userPost.commentPanel.clear();
								for(Comment comment : comments)
									userPost.commentPanel.add(new PostComment(main,comment,userPost));
							}
						};
						userService.deleteComment(comment.getCommentKey(), callback);
					}
				}
			});

			vPanel.add(editComment);
			vPanel.add(deleteComment);
		}
		else
		{
			Label flagComment = new Label("Report Comment");
			flagComment.setStyleName("menuitem");
			flagComment.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					popup.hide();
					new FlagForm(comment.getCommentKey(),FlagForm.COMMENT);
				}
			});
			vPanel.add(flagComment);
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
