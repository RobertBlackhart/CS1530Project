package com.cs1530.group4.addendum.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueuedEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueuedHandler;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;
import org.moxieapps.gwt.uploader.client.events.UploadProgressHandler;
import org.moxieapps.gwt.uploader.client.events.UploadStartEvent;
import org.moxieapps.gwt.uploader.client.events.UploadStartHandler;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessEvent;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessHandler;

import com.cs1530.group4.addendum.shared.Comment;
import com.cs1530.group4.addendum.shared.Post;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * A class representing the UI for a user to compose and edit comments
 */
public class CommentBox extends Composite
{
	
	/** A reference to this CommentBox object */
	CommentBox commentBox = this;
	
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();
	
	/** The text area. */
	RichTextArea textArea;
	
	/** The error label. */
	Label errorLabel;
	
	/** A flag used to indicate if this is an edit or a new comment. */
	boolean isEdit = false;
	
	/** The comment object to be edited. */
	Comment editComment;
	
	/** A list of blobKeys for attachments */
	ArrayList<String> attachmentKeys = new ArrayList<String>();
	
	/** A list of file names for attachments */
	ArrayList<String> attachmentNames = new ArrayList<String>();
	
	/** A list of blobKeys for attachments to delete*/
	ArrayList<String> deleteList = new ArrayList<String>();
	
	VerticalPanel mainPanel;
	
	/**
	 * Instantiates a new comment box.
	 *
	 * @param addComment the {@link PromptedTextBox} to hide when showing this CommentBox
	 * @param post the {@link Post} object that this comment will be associated with
	 * @param userPost the {@link UserPost} object that will contain this CommentBox
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public CommentBox(final PromptedTextBox addComment, final Post post, final UserPost userPost, Comment comment)
	{
		mainPanel = new VerticalPanel();
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setStyleName("CommentBoxBackground");
		mainPanel.getElement().getStyle().setProperty("padding", "10px");
		initWidget(mainPanel);
		mainPanel.setSize("100%", "124px");
				
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName("CommentBox");
		mainPanel.add(horizontalPanel);
		horizontalPanel.setWidth("100%");
		
		Image image = new Image("/addendum/getImage?username="+Cookies.getCookie("loggedIn"));
		image.getElement().getStyle().setProperty("marginRight", "10px");
		horizontalPanel.add(image);
		image.setSize("28px", "28px");
		
		VerticalPanel editorPanel = new VerticalPanel();
		editorPanel.setStyleName("body");
		textArea = new RichTextArea();
		RichTextToolbar toolbar = new RichTextToolbar(textArea);
		textArea.addStyleName("small");
		textArea.setSize("95%", "75px");
		editorPanel.add(toolbar);
		editorPanel.add(textArea);
		horizontalPanel.add(editorPanel);
		editorPanel.setWidth("100%");
		
		editComment = comment;
		if(comment != null)
		{
			textArea.setHTML(comment.getContent());
			isEdit = true;
		}
		else
		{
			textArea.setText("");
			isEdit = false;
		}
		
		setAttachments();
		setupFileUploader();
		
		errorLabel = new Label("Error: Message length must be greater than 0");
		errorLabel.setStyleName("gwt-Label-Error");
		errorLabel.setVisible(false);
		mainPanel.add(errorLabel);
		
		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel_1.setSpacing(10);
		mainPanel.add(horizontalPanel_1);
		horizontalPanel_1.setSize("307px", "43px");
		mainPanel.setCellHorizontalAlignment(horizontalPanel_1, HasHorizontalAlignment.ALIGN_RIGHT);
		
		final Button btnSubmit = new Button("Post Comment");
		btnSubmit.setStyleName("ADCButton");
		btnSubmit.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if(textArea.getText().length() == 0)
				{
					errorLabel.setVisible(true);
					return;
				}
				
				btnSubmit.setEnabled(false);
				
				final Comment comment;
				if(isEdit)
				{
					editComment.setContent(textArea.getHTML());
					comment = editComment;
				}
				else
					comment = new Comment(Cookies.getCookie("loggedIn"),textArea.getHTML());
				
				comment.setAttachmentNames(attachmentNames);
				comment.setAttachmentKeys(attachmentKeys);
				
				AsyncCallback<String> callback = new AsyncCallback<String>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						btnSubmit.setEnabled(true);
						errorLabel.setVisible(true);
						errorLabel.setText("There was a problem uploading your post, please try again later.");
					}
					@Override
					public void onSuccess(String keyString)
					{
						comment.setCommentKey(keyString);
						userPost.addSubmittedComment(comment,isEdit);
						commentBox.setVisible(false);
						addComment.setVisible(true);
						btnSubmit.setEnabled(true);
						errorLabel.setText("Error: Message length must be greater than 0");
						errorLabel.setVisible(false);
					}
				};
				if(isEdit)
				{
					for(String key : deleteList)
					{
						deleteAttachment(key);
						attachmentNames.remove(attachmentKeys.indexOf(key));
						attachmentKeys.remove(key);
					}
					userService.editComment(comment.getCommentKey(), comment.getContent(), attachmentKeys, attachmentNames, callback);
				}
				else
					userService.uploadComment(post.getPostKey(), comment, attachmentKeys, attachmentNames, callback);
			}
		});
		btnSubmit.setSize("131px", "29px");
		
		Button btnCancel = new Button("Cancel");
		btnCancel.setStyleName("ADCButton");
		btnCancel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				commentBox.setVisible(false);
				addComment.setVisible(true);
				btnSubmit.setEnabled(true);
				errorLabel.setVisible(false);
			}		
		});
		horizontalPanel_1.add(btnCancel);
		horizontalPanel_1.add(btnSubmit);
		btnCancel.setSize("131px", "29px");
		setStyleName("CommentBoxBackground");
	}
	
	/**
	 * Sets the up file uploader.
	 *
	 * @param vPanel a {@link VerticalPanel} object to attach the newly created {@link Uploader} object to
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	private void setupFileUploader()
	{
		final VerticalPanel progressBarPanel = new VerticalPanel();
		progressBarPanel.getElement().getStyle().setProperty("marginLeft", "10px");
		final Map<String, ProgressBar> progressBars = new LinkedHashMap<String, ProgressBar>();
		final Map<String, Image> cancelButtons = new LinkedHashMap<String, Image>();
		final Uploader uploader = new Uploader();

		setUploadUrl(uploader);
		uploader.setButtonImageURL("/images/add_files.png")
				.setButtonWidth(133)
				.setButtonHeight(22)
				.setFileSizeLimit("50 MB")
				.setButtonCursor(Uploader.Cursor.HAND)
				.setButtonAction(Uploader.ButtonAction.SELECT_FILES)
				.setFileQueuedHandler(new FileQueuedHandler()
		{
			public boolean onFileQueued(final FileQueuedEvent fileQueuedEvent)
			{
				// Create a Progress Bar for this file  
				final ProgressBar progressBar = new ProgressBar(0.0, 1.0, 0.0, new CancelProgressBarTextFormatter());
				progressBar.setTitle(fileQueuedEvent.getFile().getName());
				progressBar.setHeight("18px");
				progressBar.setWidth("200px");
				progressBars.put(fileQueuedEvent.getFile().getId(), progressBar);

				// Add Cancel Button Image  
				final Image cancelButton = new Image("/images/cancel.png");
				cancelButton.setStyleName("cancelButton");
				cancelButton.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						uploader.cancelUpload(fileQueuedEvent.getFile().getId(), false);
						progressBars.get(fileQueuedEvent.getFile().getId()).setProgress(-1.0d);
						cancelButton.removeFromParent();
					}
				});
				cancelButtons.put(fileQueuedEvent.getFile().getId(), cancelButton);

				// Add the Bar and Button to the interface  
				HorizontalPanel progressBarAndButtonPanel = new HorizontalPanel();
				Label fileName = new Label(fileQueuedEvent.getFile().getName());
				fileName.setWidth("200px");
				fileName.setStyleName("attachmentFileName");
				progressBarAndButtonPanel.add(fileName);
				progressBarAndButtonPanel.add(progressBar);
				progressBarAndButtonPanel.add(cancelButton);
				progressBarPanel.add(progressBarAndButtonPanel);

				return true;
			}
		}).setUploadProgressHandler(new UploadProgressHandler()
		{
			public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent)
			{
				ProgressBar progressBar = progressBars.get(uploadProgressEvent.getFile().getId());
				progressBar.setProgress((double) uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal());
				return true;
			}
		}).setUploadStartHandler(new UploadStartHandler()
		{
			@Override
			public boolean onUploadStart(UploadStartEvent uploadStartEvent)
			{
				setUploadUrl(uploader);
				return true;
			}
		}).setUploadCompleteHandler(new UploadCompleteHandler()
		{
			public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent)
			{
				cancelButtons.get(uploadCompleteEvent.getFile().getId()).removeFromParent();
				// Call upload to see if any additional files are queued  
				uploader.startUpload();
				return true;
			}
		}).setFileDialogCompleteHandler(new FileDialogCompleteHandler()
		{
			public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent)
			{
				if(uploader.getStats().getUploadsInProgress() <= 0)
				{
					uploader.startUpload();
				}
				return true;
			}
		}).setFileQueueErrorHandler(new FileQueueErrorHandler()
		{
			public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent)
			{
				Window.alert("Upload of file " + fileQueueErrorEvent.getFile().getName() + " failed due to [" + fileQueueErrorEvent.getErrorCode().toString() + "]: " + fileQueueErrorEvent.getMessage());
				return true;
			}
		}).setUploadErrorHandler(new UploadErrorHandler()
		{
			public boolean onUploadError(UploadErrorEvent uploadErrorEvent)
			{
				cancelButtons.get(uploadErrorEvent.getFile().getId()).removeFromParent();
				Window.alert("Upload of file " + uploadErrorEvent.getFile().getName() + " failed due to [" + uploadErrorEvent.getErrorCode().toString() + "]: " + uploadErrorEvent.getMessage());
				return true;
			}
		}).setUploadSuccessHandler(new UploadSuccessHandler()
		{

			@Override
			public boolean onUploadSuccess(UploadSuccessEvent event)
			{
				attachmentKeys.add(event.getServerData());
				attachmentNames.add(event.getFile().getName());
				return true;
			}
		});

		HorizontalPanel uploaderPanel = new HorizontalPanel();
		uploaderPanel.getElement().getStyle().setProperty("marginTop", "10px");
		uploaderPanel.add(uploader);
		uploaderPanel.add(progressBarPanel);
		uploaderPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		uploaderPanel.setCellHorizontalAlignment(uploader, HorizontalPanel.ALIGN_LEFT);
		uploaderPanel.setCellHorizontalAlignment(progressBarPanel, HorizontalPanel.ALIGN_RIGHT);

		mainPanel.add(uploaderPanel);
	}
	
	private void setAttachments()
	{
		final VerticalPanel attachmentsPanel = new VerticalPanel();
		attachmentsPanel.setWidth("100%");
		mainPanel.add(attachmentsPanel);
		Label lblAttachments = new Label("Attachments:");
		lblAttachments.setStyleName("NewPostBackLabel");
		attachmentsPanel.add(lblAttachments);
		
		if(editComment != null)
		{
			attachmentKeys = editComment.getAttachmentKeys();
			attachmentNames = editComment.getAttachmentNames();
		}
		
		if(attachmentKeys != null)
		{
			for(int i=0; i<attachmentKeys.size(); i++)
			{
				final HorizontalPanel attachmentLine = new HorizontalPanel();
				attachmentLine.setSpacing(5);
				attachmentLine.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				
				final String key = attachmentKeys.get(i);
				String name = attachmentNames.get(i);
				Anchor anchor = new Anchor(name, "/addendum/getImage?key="+key,"_blank");
				Image delete = new Image("/images/delete.png");
				delete.setSize("16px", "16px");
				delete.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						deleteList.add(key);
						attachmentsPanel.remove(attachmentLine);
					}
				});
				
				attachmentLine.add(anchor);
				attachmentLine.add(delete);
				attachmentsPanel.add(attachmentLine);
			}
		}
	}
	
	/**
	 * Sets the upload url to submit an attachment to.  This is created dynamically for each attachment by {@link com.google.appengine.api.blobstore.BlobstoreService#createUploadUrl(String)}
	 *
	 * @param uploader the {@link Uploader} on which to set the upload url
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUploadUrl()}
	 */
	private void setUploadUrl(final Uploader uploader)
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				
			}

			@Override
			public void onSuccess(String result)
			{
				uploader.setUploadURL(result);
			}
		};

		userService.getUploadUrl(null,callback);
	}
	
	/**
	 * A convienence class to format text for attachment upload progress bars.
	 */
	protected class CancelProgressBarTextFormatter extends ProgressBar.TextFormatter
	{
		
		/* (non-Javadoc)
		 * @see com.google.gwt.widgetideas.client.ProgressBar.TextFormatter#getText(com.google.gwt.widgetideas.client.ProgressBar, double)
		 */
		@Override
		protected String getText(ProgressBar bar, double curProgress)
		{
			if(curProgress < 0) { return "Cancelled"; }
			return ((int) (100 * bar.getPercent())) + "%";
		}
	}
	
	/**
	 * Delete attachment.
	 *
	 * @param key the BlobKey string to delete
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#deleteAttachment(String)}
	 */
	private void deleteAttachment(String key)
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
				
			}
		};
		
		userService.deleteAttachment(key,callback);
	}
}