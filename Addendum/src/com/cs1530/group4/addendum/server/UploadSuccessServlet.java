package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class UploadSuccessServlet extends HttpServlet 
{
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{				
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		Collection<List<BlobKey>> blobKeys = blobs.values();
		Iterator<List<BlobKey>> itr = blobKeys.iterator();
		while(itr.hasNext())
		{
			List<BlobKey> keyList = itr.next();
			BlobKey blobKey = keyList.get(0);
					
			resp.setContentType("text/plain");
			resp.getWriter().print(blobKey.getKeyString());
		}
	}
}
