package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs1530.group4.addendum.server.TupleMap.Pair;
import com.cs1530.group4.addendum.shared.Achievement;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class GetAchievementsServlet extends HttpServlet
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	
	static TupleMap<String,String,String> achievements = new TupleMap<String,String,String>();
	{
		achievements.put("niceComment", "Nice Comment", "Write your first comment");
		achievements.put("goodComment", "Good Comment", "Write 25 comments");
		achievements.put("greatComment", "Great Comment", "Write 100 comments");
		achievements.put("casual", "Casual", "Participate for 2 days straight");
		achievements.put("committed", "Committed", "Participate for 5 days straight");
		achievements.put("dedicated", "Dedicated", "Participate for 15 days straight");
		achievements.put("smart", "Smart", "Have 1 comment accepted as an answer");
		achievements.put("brilliant", "Brilliant", "Have 10 comments accepted as an answer");
		achievements.put("genius", "Genius", "Have 50 comments accepted as an answer");
		achievements.put("supporter", "Supporter", "First upvote given");
		achievements.put("critic", "Critic", "First downvote given");
		achievements.put("nicePost", "Nice Post", "Write your first post");
		achievements.put("goodPost", "Good Post", "Write a post that gets 25 upvotes");
		achievements.put("greatPost", "Great Post", "Write a post that gets 100 upvotes");
	}

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
		ArrayList<Achievement> results = null;
		String username = req.getParameter("username");
		
		if(req.getParameter("type").equals("earned"))
			results = getAchievements(username);
		else
			results = getUnearnedAchievements(username);
		
		resp.setContentType("application/json");
		Gson gson = new Gson();
		String json = gson.toJson(results);
		resp.getWriter().print(json);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Achievement> getAchievements(String username)
	{
		ArrayList<Achievement> achievements = new ArrayList<Achievement>();
		
		for(Entity entity : getAchievementEntities())
		{
			ArrayList<String> users = (ArrayList<String>)entity.getProperty("usersEarned");
			if(users.contains(username))
			{
				Achievement achievement = new Achievement();
				achievement.setName((String)entity.getProperty("name"));
				achievement.setDescriptionText((String)entity.getProperty("description"));
				achievements.add(achievement);
			}
		}
		
		return achievements;
	}
	
	private ArrayList<Entity> getAchievementEntities()
	{
		ArrayList<Entity> entities = new ArrayList<Entity>();
		ArrayList<Key> datastoreGets = new ArrayList<Key>();
		
		Query q = new Query("Achievement").setKeysOnly();
		for(Entity entity : datastore.prepare(q).asIterable())
		{
			if(memcache.contains(entity.getKey()))
				entities.add((Entity)memcache.get(entity.getKey()));
			else
				datastoreGets.add(entity.getKey());
		}
		entities.addAll(datastore.get(datastoreGets).values());
		for(Entity entity : entities)
			memcache.put(entity.getKey(), entity);
		
		return entities;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Achievement> getUnearnedAchievements(String username)
	{
		ArrayList<Achievement> achievementList = new ArrayList<Achievement>();
		
		for(Entry<String,Pair<String,String>> entry : achievements)
		{
			Achievement achievement = new Achievement();
			achievement.setName(entry.getValue().getLeft());
			achievement.setDescriptionText(entry.getValue().getRight());
			achievementList.add(achievement);
		}
		for(Entity entity : getAchievementEntities())
		{
			ArrayList<String> users = (ArrayList<String>)entity.getProperty("usersEarned");
			if(users.contains(username))
			{
				Achievement achievement = new Achievement();
				achievement.setName((String)entity.getProperty("name"));
				achievement.setDescriptionText((String)entity.getProperty("description"));
				achievementList.remove(achievement);
			}
		}
		
		return achievementList;
	}
}