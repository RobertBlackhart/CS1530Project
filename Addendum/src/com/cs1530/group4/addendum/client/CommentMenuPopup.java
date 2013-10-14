package com.cs1530.group4.addendum.client;

import java.util.ArrayList;

import com.cs1530.group4.addendum.shared.Comment;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CommentMenuPopup extends PopupPanel implements MouseOverHandler, MouseOutHandler
{
	UserServiceAsync userService = UserService.Util.getInstance();
	CommentMenuPopup popup = this;
	Widget relativeWidget;

	public CommentMenuPopup(final MainView main, Widget w, final Comment comment, boolean isUser, final UserPost userPost)
	{
		super(true);
		setStyleName("MenuPopUp");
		relativeWidget = w;
		addAutoHidePartner(w.getElement());
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(5);
		if(isUser)
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
									}
								}
								userPost.commentPanel.clear();
								for(Comment comment : comments)
									userPost.commentPanel.add(new PostComment(main,comment,userPost.profile,userPost));
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
