package com.cs1530.group4.addendum.client;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;
import gwtupload.client.PreloadedImage;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This represents a dialog box allowing a user to select an image file to use as their profile picture.
 */
public class ProfilePictureUpload extends DialogBox
{
	/** The panel to show the uploaded image. */
	private FlowPanel panelImages = new FlowPanel();
	
	/** A reference to this ProfilePictureUpload object. */
	ProfilePictureUpload dialog = this;

	/**
	 * Instantiates a new ProfilePictureUpload.
	 *
	 * @param username the user's username
	 * 
	 * @.accessed None
	 * @.changed None
	 * @.called None
	 */
	public ProfilePictureUpload(String username)
	{
		//TODO: make dialog not look sucky
		//I set the css for ok button but I couldn't find the other one? --Ricky 10/4/13
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
			}
		});
		MultiUploader uploader = new MultiUploader();
		uploader.setServletPath(uploader.getServletPath() + "?username="+username);
		vPanel.add(uploader);
		vPanel.add(panelImages);
		buttonPanel.add(okButton);
		vPanel.add(buttonPanel);
		uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
		
		add(vPanel);
		setGlassEnabled(true);
		center();
	}

	/** The on finish uploader handler. */
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler()
	{
		public void onFinish(IUploader uploader)
		{
			if(uploader.getStatus() == Status.SUCCESS)
			{
				new PreloadedImage(uploader.fileUrl(), showImage);

				// The server sends useful information to the client by default
				UploadedInfo info = uploader.getServerInfo();
				// You can send any customized message and parse it 
				System.out.println("Server message " + info.message);
			}
		}
	};

	/** The on load preloaded image handler. */
	private OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler()
	{
		public void onLoad(PreloadedImage image)
		{
			image.setWidth("75px");
			panelImages.add(image);
		}
	};
}
