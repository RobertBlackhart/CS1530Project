package com.cs1530.group4.addendum.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class ImageUploadServlet extends HttpServlet 
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		String postUrl = blobstoreService.createUploadUrl("/addendum/uploadSuccess");
			
		resp.setContentType("text/plain");
		resp.getWriter().println(postUrl);
	}
}
