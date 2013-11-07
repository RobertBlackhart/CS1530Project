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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class UploadSuccessServlet extends HttpServlet 
{
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{				
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		Collection<List<BlobKey>> blobKeys = blobs.values();
		Iterator<List<BlobKey>> itr = blobKeys.iterator();
		while(itr.hasNext())
		{
			List<BlobKey> keyList = itr.next();
			BlobKey blobKey = keyList.get(0);
					
			resp.setContentType("text/html");
			resp.getWriter().print(blobKey.getKeyString());
			
			if(req.getParameter("username") != null)
			{
				String username = req.getParameter("username");
				Entity user = getUserEntity(username);
				user.setProperty("profileImage", blobKey.getKeyString());
				datastore.put(user);
				memcache.put("user_"+username, user);
			}
		}
	}
	
	private Entity getUserEntity(String username)
	{
		Entity user = null;
		if(memcache.contains("user_" + username))
			user = ((Entity) memcache.get("user_" + username));
		else
		{
			try
			{
				user = datastore.get(KeyFactory.createKey("User", username));
			}
			catch(EntityNotFoundException ex)
			{
				ex.printStackTrace();
			}
		}
		return user;
	}
}
