package com.cs1530.group4.addendum.server;

import gwtupload.server.exceptions.UploadActionException;
import gwtupload.server.gae.AppEngineUploadAction;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class UploadServlet extends AppEngineUploadAction
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	
	public String executeAction(HttpServletRequest req, List<FileItem> sessionFiles) throws UploadActionException
	{
		String username = req.getParameter("username");
		
		Entity user = getUserEntity(username);
		if(user == null)
			return null;
		
		try
		{
			InputStream imgStream = sessionFiles.get(0).getInputStream();
            Blob imageBlob = new Blob(IOUtils.toByteArray(imgStream));
			user.setProperty("profileImage", imageBlob);
			datastore.put(user);
			memcache.put("user_"+username, user);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
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