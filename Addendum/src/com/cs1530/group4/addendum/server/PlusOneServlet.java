package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class PlusOneServlet extends HttpServlet
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

	@SuppressWarnings("unchecked")
	private void performActions(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		boolean success = false;
		Entity comment = getComment(req.getParameter("commentKey"));
		String requestingUser = req.getParameter("user");
		
		if(comment != null)
		{
			ArrayList<String> plusOneUsers = new ArrayList<String>();
			int plusOnes = 0;

			if(comment.hasProperty("usersPlusOne") && comment.getProperty("usersPlusOne") != null)
				plusOneUsers = (ArrayList<String>) comment.getProperty("usersPlusOne");
			if(comment.hasProperty("plusOne"))
				plusOnes = Integer.valueOf(comment.getProperty("plusOne").toString());

			if(plusOneUsers.remove(requestingUser))
			{
				plusOnes--;
				success = false;
			}
			else
			{
				plusOneUsers.add(requestingUser);
				plusOnes++;
				success = true;
				
				Entity userStatsEntity = UserServiceImpl.getUserStats(requestingUser);
				int numPlusOnes = 1;
				if(userStatsEntity.hasProperty("numPlusOnes"))
				{
					numPlusOnes = Integer.valueOf(userStatsEntity.getProperty("numPlusOnes").toString()) + 1;
					userStatsEntity.setProperty("numPlusOnes", numPlusOnes);
				}
				else
					userStatsEntity.setProperty("numPlusOnes",numPlusOnes);

				UserServiceImpl.checkParticipation(userStatsEntity,requestingUser);
				
				datastore.put(userStatsEntity);
				memcache.put("userStats_"+requestingUser, userStatsEntity);
			}

			comment.setProperty("usersPlusOne", plusOneUsers);
			comment.setProperty("plusOne", plusOnes);

			memcache.put(comment.getKey(), comment);
			datastore.put(comment);
		}

		resp.getWriter().print(success);
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