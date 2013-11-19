package com.cs1530.group4.addendum.client;

import java.util.ArrayList;
import java.util.Date;
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

import com.cs1530.group4.addendum.shared.Post;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * The Class NewPost represents a dialog for the user to create a new post with.  It will take the text and attachments and upload them to the server.
 */
public class NewPost extends DialogBox
{
	/** A reference to this NewPost object. */
	NewPost postBox = this;
	
	/** The editor area. */
	RichTextArea editor;
	
	/** The stream level box. */
	ListBox streamLevelBox;
	
	/** The application's MainView. */
	MainView main;
	
	/** The error label. */
	Label errorLabel;
	
	/** A list of blobKeys for attachments */
	ArrayList<String> attachmentKeys = new ArrayList<String>();
	
	/** A list of file names for attachments */
	ArrayList<String> attachmentNames = new ArrayList<String>();
	
	/** A list of blobKeys for attachments to delete*/
	ArrayList<String> deleteList = new ArrayList<String>();
	
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();
	
	/** The post represented by this NewPost object.  It will be null if this is a new post and !null if this is an edit. */
	Post post;

	/**
	 * Instantiates a new NewPost object.
	 *
	 * @param m the {@link MainView} of the application
	 * @param streams a list of streamLevels, or classes, that the user is a member of
	 * @param p a {@link Post} object to load from or null to start a new post
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link #setupFileUploader(VerticalPanel)}
	 */
	public NewPost(MainView m, ArrayList<String> streams, Post p)
	{
		setStyleName("NewPostBackground");
		main = m;
		post = p;

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		editor = new RichTextArea();
		editor.setSize("600px", "400px");
		RichTextToolbar toolbar = new RichTextToolbar(editor);
		toolbar.setStyleName("NewPostToolBar");

		if(post != null)
		{
			editor.setHTML(post.getPostContent());
			attachmentKeys = post.getAttachmentKeys();
			attachmentNames = post.getAttachmentNames();
		}

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setStyleName("NewPostBottomLine");
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buttonPanel.setWidth("100%");

		Label lblNewLabel = new Label("NEW POST");
		lblNewLabel.setStyleName("NewPostBackLabel");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		vPanel.add(lblNewLabel);

		vPanel.add(toolbar);
		toolbar.setWidth("100%");
		vPanel.add(editor);

		HorizontalPanel streamPanel = new HorizontalPanel();
		streamPanel.getElement().getStyle().setProperty("marginBottom", "5px");

		final Button submitButton = new Button("Submit");
		submitButton.setStyleName("LoginButton");
		submitButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				submitButton.setEnabled(false);
				submitPost(submitButton, post);
			}
		});

		setupFileUploader(vPanel);

		HTML html = new HTML("<hr width=100%/>", true);
		vPanel.add(html);
		streamPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		streamPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(streamPanel);

		Label lblMakeThisPost = new Label("MAKE THIS POST VISIBLE TO: ");
		lblMakeThisPost.setStyleName("NewPostBackLabel");
		streamPanel.add(lblMakeThisPost);

		streamLevelBox = new ListBox();
		streamLevelBox.setStyleName("NewPostBackLabel");
		if(post == null)
			streamLevelBox.addItem("Everyone");
		for(String stream : streams)
		{
			if(stream.equals("all"))
				streamLevelBox.addItem("Everyone");
			else
				streamLevelBox.addItem("Members of " + stream);
		}
		streamPanel.add(streamLevelBox);
		streamLevelBox.setWidth("171px");

		errorLabel = new Label("Error: Message length must be greater than 0");
		errorLabel.setStyleName("gwt-Label-Error");
		errorLabel.setVisible(false);

		Button discardButton = new Button("Discard");
		discardButton.setStyleName("LoginButton");
		discardButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				postBox.hide();
			}
		});
		buttonPanel.add(discardButton);
		buttonPanel.add(submitButton);

		vPanel.add(errorLabel);
		vPanel.add(buttonPanel);
		add(vPanel);

		setGlassEnabled(true);
		center();
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
	private void setupFileUploader(VerticalPanel vPanel)
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
		
		final VerticalPanel attachmentsPanel = new VerticalPanel();
		vPanel.add(attachmentsPanel);
		attachmentsPanel.setWidth("100%");
		
		Label lblAttachments = new Label("Attachments:");
		lblAttachments.setStyleName("NewPostBackLabel");
		attachmentsPanel.add(lblAttachments);
		
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

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.getElement().getStyle().setProperty("marginTop", "10px");
		horizontalPanel.add(uploader);
		horizontalPanel.add(progressBarPanel);
		horizontalPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(uploader, HorizontalPanel.ALIGN_LEFT);
		horizontalPanel.setCellHorizontalAlignment(progressBarPanel, HorizontalPanel.ALIGN_RIGHT);

		vPanel.add(horizontalPanel);
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
	 * Submit this post to the server.  After a successful submission this will refresh the page.
	 *
	 * @param submitButton the submit button
	 * @param post the post
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link #deleteAttachment(String)}, {@link com.cs1530.group4.addendum.server.UserServiceImpl#editPost(String, String, String, ArrayList, ArrayList)}, {@link com.cs1530.group4.addendum.server.UserServiceImpl#uploadPost(String, String, String, String, Date, ArrayList, ArrayList)}
	 */
	private void submitPost(final Button submitButton, final Post post)
	{
		if(editor.getText().length() == 0)
		{
			errorLabel.setVisible(true);
			return;
		}

		AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				submitButton.setEnabled(true);
				errorLabel.setVisible(true);
				errorLabel.setText("There was a problem uploading your post, please try again later.");
			}

			@Override
			public void onSuccess(Void v)
			{
				postBox.hide();
				main.setContent(new Stream(main), "stream");
			}
		};

		String stream = streamLevelBox.getItemText(streamLevelBox.getSelectedIndex());
		if(stream.equals("Everyone"))
			stream = "all";
		else
			stream = stream.substring(11);
		if(post == null)
			userService.uploadPost(Cookies.getCookie("loggedIn"), editor.getHTML(), editor.getText(), stream, new Date(), attachmentKeys, attachmentNames, callback);
		else
		{
			for(String key : deleteList)
			{
				deleteAttachment(key);
				attachmentNames.remove(attachmentKeys.indexOf(key));
				attachmentKeys.remove(key);
			}
			post.setAttachmentNames(attachmentNames);
			post.setAttachmentKeys(attachmentKeys);
			userService.editPost(post.getPostKey(), editor.getHTML(), editor.getText(), attachmentKeys, attachmentNames, callback);
		}
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
}