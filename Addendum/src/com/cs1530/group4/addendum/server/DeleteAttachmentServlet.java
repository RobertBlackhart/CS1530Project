package com.cs1530.group4.addendum.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class DeleteAttachmentServlet extends HttpServlet
{
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		blobstoreService.delete(new BlobKey(req.getParameter("key")));
	}
}