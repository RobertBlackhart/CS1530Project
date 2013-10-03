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

public class ProfilePictureUpload extends DialogBox
{
	private FlowPanel panelImages = new FlowPanel();
	DialogBox dialog = this;

	public ProfilePictureUpload(String username)
	{
		//TODO: make dialog not look sucky
		setStyleName("NewPostBackground");
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(5);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(new Label("For best results, use a picture with a square aspect ration (i.e. 128px x 128px)"));
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Button okButton = new Button("OK");
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

	private OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler()
	{
		public void onLoad(PreloadedImage image)
		{
			image.setWidth("75px");
			panelImages.add(image);
		}
	};
}
