package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs1530.group4.addendum.shared.UserProfile;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class GetUserProfileServlet extends HttpServlet
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
		String username = req.getParameter("username");
		UserProfile userProfile = new UserProfile();

		Entity profileEntity = null;
		if(memcache.contains("userProfile_" + username))
			profileEntity = ((Entity) memcache.get("userProfile_" + username));
		else
		{
			try
			{
				profileEntity = datastore.get(KeyFactory.createKey("UserProfile", username));
				memcache.put("userProfile_" + username, profileEntity);
			}
			catch(EntityNotFoundException ex)
			{}
		}

		if(profileEntity != null)
		{
			if(profileEntity.hasProperty("address"))
				userProfile.setAddress((String) profileEntity.getProperty("address"));
			if(profileEntity.hasProperty("birthday"))
				userProfile.setBirthday((String) profileEntity.getProperty("birthday"));
			if(profileEntity.hasProperty("braggingRights"))
				userProfile.setBraggingRights((String) profileEntity.getProperty("braggingRights"));
			if(profileEntity.hasProperty("college"))
				userProfile.setCollege((String) profileEntity.getProperty("college"));
			if(profileEntity.hasProperty("email"))
				userProfile.setEmail((String) profileEntity.getProperty("email"));
			if(profileEntity.hasProperty("gender"))
				userProfile.setGender((String) profileEntity.getProperty("gender"));
			if(profileEntity.hasProperty("highSchool"))
				userProfile.setHighSchool((String) profileEntity.getProperty("highSchool"));
			if(profileEntity.hasProperty("introduction"))
				userProfile.setIntroduction((String) profileEntity.getProperty("introduction"));
			if(profileEntity.hasProperty("name"))
				userProfile.setName((String) profileEntity.getProperty("name"));
			if(profileEntity.hasProperty("phone"))
				userProfile.setPhone((String) profileEntity.getProperty("phone"));
			if(profileEntity.hasProperty("tagline"))
				userProfile.setTagline((String) profileEntity.getProperty("tagline"));
		}
		
		userProfile.setUsername(username);
		Entity userEntity = getUserEntity(username);
		if(userEntity != null && userEntity.hasProperty("courseList"))
			userProfile.setCourseList((ArrayList<String>) userEntity.getProperty("courseList"));
		else
			userProfile.setCourseList(new ArrayList<String>());

		resp.setContentType("application/json");
		Gson gson = new Gson();
		String json = gson.toJson(userProfile);
		resp.getWriter().print(json);
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
				memcache.put("user_"+username, user);
			}
			catch(EntityNotFoundException ex)
			{}
		}
		return user;
	}
}