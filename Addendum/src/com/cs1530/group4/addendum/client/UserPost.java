package com.cs1530.group4.addendum.client;

import java.sql.Date;
import java.util.ArrayList;

import com.cs1530.group4.addendum.shared.Comment;
import com.cs1530.group4.addendum.shared.Post;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * UserPost visually represents a post by a user of Addendum. It contains their
 * uploaded content as well as any comments associated with this post.
 */
public class UserPost extends Composite implements MouseOverHandler, MouseOutHandler
{
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();

	/**
	 * An integer representing {@link Post#getUpvotes()} -
	 * {@link Post#getDownvotes()}
	 */
	int upDownVotes;

	/** The comment panel. */
	HTMLPanel commentPanel;

	/** The scroll panel that encloses the {@link #commentPanel}. */
	ScrollPanel scroll;

	/** The icon for the dropdown menu allowing access to post actions. */
	Image menu;

	/** The popup shown by clicking {@link #menu}. */
	MenuPopup popup = null;

	/** The logged in user. */
	String loggedInUser = Cookies.getCookie("loggedIn");

	/** The application's MainView. */
	MainView main;

	/** A textbox which when clicked opens into a {@link #commentBox}. */
	PromptedTextBox addAComment;

	/** The interface for entering new comments. */
	CommentBox commentBox;

	/** A reference to this {@link UserPost} object. */
	UserPost userPost = this;

	/** The {@link Post} associated with this object. */
	Post post;

	/** A flag representing the expanded state of the comments. */
	boolean isExpanded = false;

	/**
	 * Instantiates a new UserPost.
	 * 
	 * @param m
	 *            The MainView of the application
	 * @param p
	 *            The post object that this UserPost represents.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link #displayComments()}
	 */
	public UserPost(MainView m, Post p)
	{
		main = m;
		post = p;
		upDownVotes = post.getUpvotes() - post.getDownvotes();
		HorizontalPanel border = new HorizontalPanel();
		border.setStyleName("postBorder");
		border.setBorderWidth(1);
		border.setWidth("100%");
		initWidget(border);

		HTMLPanel scorePanel = new HTMLPanel("<div></div>");
		scorePanel.setStyleName("scorePanel");
		border.add(scorePanel);
		border.setCellHorizontalAlignment(scorePanel, HasHorizontalAlignment.ALIGN_CENTER);
		border.setCellWidth(scorePanel, "34px");

		final Image upArrow = new Image("images/default_up.png");
		upArrow.setStyleName("imageButton");
		upArrow.setTitle("Click to vote this post up.");
		final Image downArrow = new Image("images/default_down.png");
		downArrow.setStyleName("imageButton");
		downArrow.setTitle("Click to vote this post down.");
		if(post.isUpvoted())
			upArrow.setUrl("images/voted_up.png");
		if(post.isDownvoted())
			downArrow.setUrl("images/voted_down.png");
		final Label scoreLabel = new Label(String.valueOf(upDownVotes));
		upArrow.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
				{
					@Override
					public void onFailure(Throwable caught)
					{}

					@Override
					public void onSuccess(Boolean success)
					{
						if(success) //user has not upvoted before or they have downvoted before
						{
							if(post.isDownvoted())
								upDownVotes++;
							post.setDownvoted(false);
							downArrow.setUrl("images/default_down.png");
							downArrow.setTitle("Click to vote this post down.");
							if(post.isUpvoted())
							{
								scoreLabel.setText(String.valueOf(--upDownVotes));
								post.setUpvoted(false);
								upArrow.setUrl("images/default_up.png");
								upArrow.setTitle("Click to vote this post up.");
							}
							else
							{
								scoreLabel.setText(String.valueOf(++upDownVotes));
								post.setUpvoted(true);
								upArrow.setUrl("images/voted_up.png");
								upArrow.setTitle("Click to undo this upvote.");
							}
						}
						else
						//user has upvoted before - undo it
						{
							scoreLabel.setText(String.valueOf(--upDownVotes));
							post.setUpvoted(false);
							upArrow.setUrl("images/default_up.png");
							upArrow.setTitle("Click to vote this post up.");
						}
					}
				};
				userService.upvotePost(post.getPostKey(), Cookies.getCookie("loggedIn"), callback);
			}
		});
		downArrow.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
				{
					@Override
					public void onFailure(Throwable caught)
					{}

					@Override
					public void onSuccess(Boolean success)
					{
						if(success)
						{
							if(post.isUpvoted())
								upDownVotes--;
							post.setUpvoted(false);
							upArrow.setUrl("images/default_up.png");
							upArrow.setTitle("Click to vote this post up.");
							if(post.isDownvoted())
							{
								scoreLabel.setText(String.valueOf(++upDownVotes));
								post.setDownvoted(false);
								downArrow.setUrl("images/default_down.png");
								downArrow.setTitle("Click to vote this post down.");
							}
							else
							{
								scoreLabel.setText(String.valueOf(--upDownVotes));
								post.setDownvoted(true);
								downArrow.setUrl("images/voted_down.png");
								downArrow.setTitle("Click to undo this downvote.");
							}
						}
						else
						{
							post.setDownvoted(false);
							scoreLabel.setText(String.valueOf(++upDownVotes));
							downArrow.setUrl("images/default_down.png");
							downArrow.setTitle("Click to vote this post down.");
						}
					}
				};
				userService.downvotePost(post.getPostKey(), Cookies.getCookie("loggedIn"), callback);
			}
		});
		scorePanel.add(upArrow);
		upArrow.setSize("24px", "24px");

		scoreLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		scorePanel.add(scoreLabel);

		scorePanel.add(downArrow);
		downArrow.setSize("24px", "24px");

		HTMLPanel postPanel = new HTMLPanel("<div></div>");
		border.add(postPanel);

		String label = post.getStreamLevel();
		if(label.equals("all"))
			label = "Everyone";
		Label lblNewLabel = new Label(label);
		lblNewLabel.setStyleName("postSpaceTitle");
		postPanel.add(lblNewLabel);

		HTML html = new HTML("<hr  style=\"width:100%;\" />");
		postPanel.add(html);

		menu = new Image("images/menu.png");
		postPanel.add(menu);
		menu.setStyleName("triangleMenu");
		menu.setSize("24px", "24px");
		menu.setVisible(false);
		menu.getElement().getStyle().setProperty("marginRight", "5px");
		menu.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if(popup.isShowing())
					popup.hide();
				else
					popup.showRelativeTo(menu);
			}
		});
		popup = new MenuPopup(main, menu, post);

		Image image = new Image("/addendum/getImage?username=" + post.getUsername());
		postPanel.add(image);
		image.setStyleName("postProfileImage");

		Anchor usernameLabel = new Anchor(post.getUsername());
		postPanel.add(usernameLabel);
		usernameLabel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				main.setContent(new Profile(main, post.getUsername(), true), "profile-" + post.getUsername());
			}
		});

		usernameLabel.setStyleName("gwt-Label-bold");
		Label lblPosttime = new Label(getTimeText());
		postPanel.add(lblPosttime);
		lblPosttime.setStyleName("gwt-Label-grey");

		HTML postContent = new HTML(post.getPostContent());
		postPanel.add(postContent);
		postContent.setStyleName("userPostContent");

		HTML separator = new HTML("<hr  style=\"width:100%;\" />");
		postPanel.add(separator);

		addDomHandler(this, MouseOverEvent.getType());
		addDomHandler(this, MouseOutEvent.getType());

		scroll = new ScrollPanel();
		commentPanel = new HTMLPanel("<div></div>");
		commentPanel.setWidth("100%");
		commentPanel.setStyleName("CommentPanelbackcolor");

		VerticalPanel attachmentsPanel = new VerticalPanel();
		attachmentsPanel.setStyleName("attachmentPanel");
		postPanel.add(attachmentsPanel);

		if(post.getAttachmentKeys() != null && post.getAttachmentKeys().size() > 0)
		{
			Label lblAttachments = new Label("Attachments:");
			lblAttachments.setStyleName("NewPostBackLabel");
			attachmentsPanel.add(lblAttachments);

			for(int i = 0; i < post.getAttachmentKeys().size(); i++)
			{
				String key = post.getAttachmentKeys().get(i);
				String name = post.getAttachmentNames().get(i);
				Anchor anchor = new Anchor(name, "/addendum/getImage?key=" + key, "_blank");
				attachmentsPanel.add(anchor);
			}

			postPanel.add(new HTML("<hr  style=\"width:100%;\" />"));
		}

		scroll.add(commentPanel);
		postPanel.add(scroll);

		if(post.getComments() != null)
		{
			displayComments();
		}

		final FlowPanel addCommentPanel = new FlowPanel();
		postPanel.add(addCommentPanel);
		addCommentPanel.setWidth("100%");

		addAComment = new PromptedTextBox("Add a comment...", "promptText");
		addAComment.getElement().getStyle().setProperty("margin", "10px");
		addAComment.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				showCommentBox(null);
			}
		});
		addCommentPanel.add(addAComment);
	}

	private String getTimeText()
	{
		String timeFormatString = "h:mm a";
		String editFormatString = "h:mm a";
		Date now = new Date(System.currentTimeMillis());
		if(post.getPostTime().getDate() != now.getDate())
			timeFormatString = "MMM d, yyyy";
		if(post.getLastEdit() != null && post.getLastEdit().getDate() != now.getDate())
			editFormatString = "MMM d, yyyy";
		DateTimeFormat dtf = new DateTimeFormat(timeFormatString, new DefaultDateTimeFormatInfo())
		{};
		DateTimeFormat editDtf = new DateTimeFormat(editFormatString, new DefaultDateTimeFormatInfo())
		{};
		String timeLabel = dtf.format(post.getPostTime());
		if(post.getLastEdit() != null)
			timeLabel += " (last edit - " + editDtf.format(post.getLastEdit()) + ")";

		return timeLabel;
	}

	/**
	 * This method handles the logic for displaying the comments associated with
	 * this UserPost. If there are more than 2 comments, the comments will be
	 * collapsed into an expandable structure. Additionally, if the comments
	 * physical height is greater than 300px, they will be placed into a
	 * scrollable panel.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link #adjustCommentScroll()}
	 */
	public void displayComments()
	{
		commentPanel.clear();

		if(post.getComments().size() > 2)
		{
			final HorizontalPanel expandPanel = new HorizontalPanel();
			final HorizontalPanel hidePanel = new HorizontalPanel();
			final ArrayList<Comment> comments = post.getComments();

			Label expandComments = new Label(comments.size() + " comments");
			expandComments.setStyleName("expandCloseLink");
			Image expandImage = new Image("images/open_arrow.png");
			expandImage.setStyleName("expandCloseLink");
			expandPanel.add(expandComments);
			expandPanel.add(expandImage);

			Label hideComments = new Label("Hide comments");
			hideComments.setStyleName("expandCloseLink");
			Image hideImage = new Image("images/close_arrow.png");
			hideImage.setStyleName("expandCloseLink");
			hidePanel.add(hideComments);
			hidePanel.add(hideImage);

			ClickHandler expandHandler = new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					isExpanded = true;
					commentPanel.clear();
					commentPanel.add(hidePanel);
					for(Comment comment : post.getComments())
						commentPanel.add(new PostComment(main, comment, userPost));

					adjustCommentScroll();
				}
			};
			expandComments.addClickHandler(expandHandler);
			expandImage.addClickHandler(expandHandler);

			ClickHandler hideHandler = new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					isExpanded = false;
					commentPanel.clear();
					commentPanel.add(expandPanel);
					boolean addedAccepted = false;
					commentPanel.add(expandPanel);
					for(Comment comment : post.getComments())
					{
						if(comment.isAccepted() && !comment.equals(comments.get(comments.size() - 1)))
						{
							commentPanel.add(new PostComment(main, comment, userPost));
							addedAccepted = true;
						}
					}
					if(!addedAccepted)
						commentPanel.add(new PostComment(main, comments.get(comments.size() - 1), userPost));
					adjustCommentScroll();
				}
			};
			hideComments.addClickHandler(hideHandler);
			hideImage.addClickHandler(hideHandler);

			if(isExpanded)
			{
				commentPanel.add(hidePanel);
				for(Comment comment : post.getComments())
					commentPanel.add(new PostComment(main, comment, userPost));
			}
			else
			{
				boolean addedAccepted = false;
				commentPanel.add(expandPanel);
				for(Comment comment : post.getComments())
				{
					if(comment.isAccepted() && !comment.equals(comments.get(comments.size() - 1)))
					{
						commentPanel.add(new PostComment(main, comment, userPost));
						addedAccepted = true;
					}
				}
				if(!addedAccepted)
					commentPanel.add(new PostComment(main, comments.get(comments.size() - 1), userPost));
			}
		}
		else
		{
			for(Comment comment : post.getComments())
				commentPanel.add(new PostComment(main, comment, userPost));
		}

		adjustCommentScroll();
	}

	/**
	 * This method determines the height of the scroll panel for the comments.
	 * If the comments total height is > 300px, the ScrollPanel will show it's
	 * scrollbar.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	private void adjustCommentScroll()
	{
		if(commentPanel.getOffsetHeight() > 300)
			scroll.setHeight("300px");
		else
			scroll.setHeight("100%");
	}

	/**
	 * Display the comment editing box below the existing post and comments.
	 * 
	 * @param comment
	 *            The comment to be edited or null to start a new comment.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public void showCommentBox(Comment comment)
	{
		if(commentBox != null)
			((FlowPanel) addAComment.getParent()).remove(commentBox);
		
		commentBox = new CommentBox(addAComment, post, userPost,comment);
		commentBox.setWidth("100%");
		((FlowPanel) addAComment.getParent()).add(commentBox);
				
		addAComment.setVisible(false);
		commentBox.textArea.setFocus(true);
	}

	/**
	 * Adds the submitted comment to the list of comments for the post.
	 * 
	 * @param comment
	 *            The comment to be added.
	 * @param isEdit
	 *            A flag to say if this was an edit or a new comment.
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link #displayComments()}, {@link #adjustCommentScroll()}
	 */
	public void addSubmittedComment(Comment comment, boolean isEdit)
	{
		if(isEdit)
		{
			comment.setLastEdit(new Date(System.currentTimeMillis()));
			for(int i = 0; i < post.getComments().size(); i++)
			{
				String commentKey = post.getComments().get(i).getCommentKey();
				if(commentKey != null && commentKey.equals(comment.getCommentKey()))
				{
					post.getComments().remove(i);
					post.getComments().add(i, comment);
				}
			}

			displayComments();

			return;
		}
		commentPanel.add(new PostComment(main, comment, userPost));
		commentPanel.setStyleName("gwt-DecoratorPanel-newComment");
		adjustCommentScroll();
		post.getComments().add(comment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google
	 * .gwt.event.dom.client.MouseOutEvent)
	 */
	@Override
	public void onMouseOut(MouseOutEvent event)
	{
		menu.setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google
	 * .gwt.event.dom.client.MouseOverEvent)
	 */
	@Override
	public void onMouseOver(MouseOverEvent event)
	{
		menu.setVisible(true);
	}
}