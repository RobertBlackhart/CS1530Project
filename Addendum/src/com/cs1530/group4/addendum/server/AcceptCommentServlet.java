package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs1530.group4.addendum.shared.Comment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class AcceptCommentServlet extends HttpServlet
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
		String associatedPost = req.getParameter("postKey");
		String accepter = req.getParameter("accepter");
		boolean accepted = Boolean.valueOf(req.getParameter("newValue"));
		Gson gson = new Gson();
		Comment comment = gson.fromJson(req.getParameter("comment"), Comment.class);
		
		ArrayList<Key> datastoreGet = new ArrayList<Key>();
		ArrayList<Entity> commentEntities = new ArrayList<Entity>();
		
		Query q = new Query("Comment").setKeysOnly();
		q.setFilter(new FilterPredicate("postKey", FilterOperator.EQUAL, associatedPost));
		for(Entity entity : datastore.prepare(q).asIterable())
		{
			if(memcache.contains(entity.getKey()))
				commentEntities.add((Entity)memcache.get(entity.getKey()));
			else
				datastoreGet.add(entity.getKey());
		}
		commentEntities.addAll(datastore.get(datastoreGet).values());
		
		for(Entity entity : commentEntities)
		{
			entity.setProperty("accepted", false);
			memcache.put(entity.getKey(), entity);
		}
		datastore.put(commentEntities);
		
		Entity entity = getCommentEntity(comment.getCommentKey());
		entity.setProperty("accepted", accepted);
		datastore.put(entity);
		memcache.put(entity.getKey(), entity);
		
		Entity userStatsEntity = UserServiceImpl.getUserStats(comment.getUsername());
		int numAccepted = 1;
		if(userStatsEntity.hasProperty("numAccepted"))
		{
			numAccepted = Integer.valueOf(userStatsEntity.getProperty("numAccepted").toString()) + 1;
			userStatsEntity.setProperty("numAccepted", numAccepted);
		}
		else
			userStatsEntity.setProperty("numAccepted",numAccepted);
		
		datastore.put(userStatsEntity);
		memcache.put("userStats_"+comment.getUsername(), userStatsEntity);
		
		Entity achievementEntity = null;
		if(numAccepted == 1)
			achievementEntity = UserServiceImpl.getAchievementEntity("smart");
		if(numAccepted == 10)
			achievementEntity = UserServiceImpl.getAchievementEntity("brilliant");
		if(numAccepted == 50)
			achievementEntity = UserServiceImpl.getAchievementEntity("genius");
		if(achievementEntity != null)
			UserServiceImpl.addUserToAchievement(achievementEntity,comment.getUsername());

		//counts as participation for the accepter, not the comment author
		userStatsEntity = UserServiceImpl.getUserStats(accepter);
		UserServiceImpl.checkParticipation(userStatsEntity,accepter);
		//save new entity as well
		datastore.put(userStatsEntity);
		memcache.put("userStats_"+accepter, userStatsEntity);
	}

	private Entity getCommentEntity(String commentKey)
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