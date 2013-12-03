package com.cs1530.group4.addendum.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class VoteServlet extends HttpServlet
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
		String postKey = req.getParameter("postKey");
		String user = req.getParameter("user");
		
		if(req.getParameter("direction").equals("up"))
			resp.getWriter().print(upvotePost(postKey,user));
		else
			resp.getWriter().print(downvotePost(postKey,user));
	}
	
	public Boolean upvotePost(String postKey, String user)
	{
		Entity userStatsEntity = UserServiceImpl.getUserStats(user);
		int numUpvote = 1;
		if(userStatsEntity.hasProperty("numUpvote"))
		{
			numUpvote = Integer.valueOf(userStatsEntity.getProperty("numUpvote").toString()) + 1;
			userStatsEntity.setProperty("numUpvote", numUpvote);
		}
		else
			userStatsEntity.setProperty("numUpvote",numUpvote);
		
		Entity achievementEntity = null;
		if(numUpvote == 1)
			achievementEntity = UserServiceImpl.getAchievementEntity("supporter");
		
		if(achievementEntity != null)
			UserServiceImpl.addUserToAchievement(achievementEntity,user);
		
		UserServiceImpl.checkParticipation(userStatsEntity,user);
		datastore.put(userStatsEntity);
		memcache.put("userStats_"+user, userStatsEntity);
			
		return UserServiceImpl.changeScore(postKey, "upvotes", user);
	}

	public Boolean downvotePost(String postKey, String user)
	{
		Entity userStatsEntity = UserServiceImpl.getUserStats(user);
		int numDownvote = 1;
		if(userStatsEntity.hasProperty("numDownvote"))
		{
			numDownvote = Integer.valueOf(userStatsEntity.getProperty("numDownvote").toString()) + 1;
			userStatsEntity.setProperty("numDownvote", numDownvote);
		}
		else
			userStatsEntity.setProperty("numDownvote",numDownvote);
		
		Entity achievementEntity = null;
		if(numDownvote == 1)
			achievementEntity = UserServiceImpl.getAchievementEntity("critic");
		
		if(achievementEntity != null)
			UserServiceImpl.addUserToAchievement(achievementEntity,user);
		
		UserServiceImpl.checkParticipation(userStatsEntity,user);
		datastore.put(userStatsEntity);
		memcache.put("userStats_"+user, userStatsEntity);
		
		return UserServiceImpl.changeScore(postKey, "downvotes", user);
	}
}