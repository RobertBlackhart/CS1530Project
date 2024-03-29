package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class EditCommentServlet extends HttpServlet
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
		resp.setContentType("text/plain");
		Entity comment = getComment(req.getParameter("commentKey"));
		if(comment != null)
		{
			comment.setProperty("content", new Text(req.getParameter("commentText")));
			comment.setProperty("edited", new Date());
			memcache.put(comment.getKey(), comment);
			datastore.put(comment);
			
			Entity userStatsEntity = UserServiceImpl.getUserStats((String)comment.getProperty("username"));
			UserServiceImpl.checkParticipation(userStatsEntity,(String)comment.getProperty("username"));
			
			datastore.put(userStatsEntity);
			memcache.put("userStats_"+(String)comment.getProperty("username"), userStatsEntity);
			
			resp.getWriter().print("done");
		}
		else
			resp.getWriter().print("error");
	}

	private Entity getComment(String commentKey)
	{
		Entity comment = null;
		Key key = KeyFactory.createKey("Comment", Long.valueOf(commentKey).longValue());
		if(memcache.contains(key))
			comment = (Entity) memcache.get(key);
		else
		{
			try
			{
				comment = datastore.get(key);
			}
			catch(EntityNotFoundException ex)
			{
				System.out.println(commentKey + " is an invalid commentKey");
			}
		}

		return comment;
	}
}