package com.cs1530.group4.addendum.client;

import java.util.LinkedHashMap;
import java.util.Map;

import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartHandler;
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
import org.moxieapps.gwt.uploader.client.events.UploadSuccessEvent;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * This represents a dialog box allowing a user to select an image file to use as their profile picture.
 */
public class ProfilePictureUpload extends DialogBox
{
	/** The blobKey referring to the just uploaded image. */
	String key;
	
	/** The name of the user for which to change the profile image. */
	String username;
	
	/** The application's MainView. */
	MainView main;
	
	/** The panel to show the uploaded image. */
	private FlowPanel panelImages = new FlowPanel();
	
	/** A reference to this ProfilePictureUpload object. */
	ProfilePictureUpload dialog = this;
	
	/** The a static instance of the service used for RPC calls. */
	UserServiceAsync userService = UserService.Util.getInstance();

	/**
	 * Instantiates a new ProfilePictureUpload.
	 * 
	 * @param m the {@link MainView} of the application 
	 * @param u the user's username
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	public ProfilePictureUpload(MainView m, String u)
	{
		main = m;
		username = u;
		setStyleName("NewPostBackground");
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(5);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(new Label("For best results, use a picture with a square aspect ration (i.e. 128px x 128px)"));
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Button okButton = new Button("OK");
		okButton.setStyleName("ADCButton");
		okButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				dialog.hide();
				main.setContent(new Stream(main), "stream");
			}
		});
		Button btnCancel = new Button("Cancel");
		btnCancel.setStyleName("ADCButton");
		btnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				dialog.hide();
				if(key != null)
					deleteAttachment(key);
			}
		});
		
		setupFileUploader(vPanel);
		
		vPanel.add(panelImages);
		buttonPanel.add(btnCancel);
		buttonPanel.add(okButton);
		vPanel.add(buttonPanel);
		add(vPanel);
		
		setGlassEnabled(true);
		center();
	}

	private void setupFileUploader(VerticalPanel vPanel)
	{
		final VerticalPanel progressBarPanel = new VerticalPanel();
		final Map<String, ProgressBar> progressBars = new LinkedHashMap<String, ProgressBar>();
		final Map<String, Image> cancelButtons = new LinkedHashMap<String, Image>();
		final Uploader uploader = new Uploader();
		setUploadUrl(uploader,username);
		uploader.setButtonImageURL("/images/add_files.png")
				.setButtonWidth(133)
				.setButtonHeight(22)
				.setFileSizeLimit("5 MB")
				.setButtonCursor(Uploader.Cursor.HAND)
				.setButtonAction(Uploader.ButtonAction.SELECT_FILE)
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
	}).setUploadCompleteHandler(new UploadCompleteHandler()
	{
		public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent)
		{
			cancelButtons.get(uploadCompleteEvent.getFile().getId()).removeFromParent();
			uploader.startUpload();
			return true;
		}
	}).setFileDialogStartHandler(new FileDialogStartHandler()
	{
		public boolean onFileDialogStartEvent(FileDialogStartEvent fileDialogStartEvent)
		{
			if(uploader.getStats().getUploadsInProgress() <= 0)
			{
				// Clear the uploads that have completed, if none are in process  
				progressBarPanel.clear();
				progressBars.clear();
				cancelButtons.clear();
			}
			return true;
		}
	}).setFileDialogCompleteHandler(new FileDialogCompleteHandler()
	{
		public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent)
		{
			if(fileDialogCompleteEvent.getTotalFilesInQueue() > 0)
			{
				if(uploader.getStats().getUploadsInProgress() <= 0)
				{
					uploader.startUpload();
				}
			}
			return true;
		}
	})
        .setFileQueueErrorHandler(new FileQueueErrorHandler() {  
            public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {  
                Window.alert("Upload of file " + fileQueueErrorEvent.getFile().getName() + " failed due to [" +  
                        fileQueueErrorEvent.getErrorCode().toString() + "]: " + fileQueueErrorEvent.getMessage()  
                );  
                return true;  
            }  
        })  
        .setUploadErrorHandler(new UploadErrorHandler() {  
            public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {  
                cancelButtons.get(uploadErrorEvent.getFile().getId()).removeFromParent();  
                Window.alert("Upload of file " + uploadErrorEvent.getFile().getName() + " failed due to [" +  
                        uploadErrorEvent.getErrorCode().toString() + "]: " + uploadErrorEvent.getMessage()  
                );  
                return true;  
            }  
        }).setUploadSuccessHandler(new UploadSuccessHandler()
		{
			@Override
			public boolean onUploadSuccess(UploadSuccessEvent event)
			{
				key = event.getServerData();
				return true;
			}
		});

	VerticalPanel verticalPanel = new VerticalPanel();
	verticalPanel.add(uploader);

    HorizontalPanel horizontalPanel = new HorizontalPanel();  
    horizontalPanel.add(verticalPanel);  
    horizontalPanel.add(progressBarPanel);  
    horizontalPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);  
    horizontalPanel.setCellHorizontalAlignment(uploader, HorizontalPanel.ALIGN_LEFT);  
    horizontalPanel.setCellHorizontalAlignment(progressBarPanel, HorizontalPanel.ALIGN_RIGHT);  

    vPanel.add(horizontalPanel);  
}  

protected class CancelProgressBarTextFormatter extends ProgressBar.TextFormatter {  
    @Override  
    protected String getText(ProgressBar bar, double curProgress) {  
        if (curProgress < 0) {  
            return "Cancelled";  
        }  
        return ((int) (100 * bar.getPercent())) + "%";  
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
	private void setUploadUrl(final Uploader uploader, String username)
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
	
		userService.getUploadUrl(username, callback);
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
