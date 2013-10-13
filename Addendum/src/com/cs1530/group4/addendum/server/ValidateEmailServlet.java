package com.cs1530.group4.addendum.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class ValidateEmailServlet extends HttpServlet
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		performActions(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		performActions(req, resp);
	}
	
	private void performActions(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String uuid = req.getParameter("uuid");
		String username = req.getParameter("username");
		
		Entity user = getUserEntity(username);
		if(user != null && user.getProperty("uuid").toString().equals(uuid))
		{
			user.setProperty("emailValid",true);
			memcache.put("user_"+username, user);
			datastore.put(user);
			resp.sendRedirect("http://studentclassnet.appspot.com/#setCookie-"+username);
		}
		else
			resp.sendRedirect("http://studentclassnet.appspot.com/");
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