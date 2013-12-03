package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs1530.group4.addendum.shared.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class AddDeleteCourseServlet extends HttpServlet
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
		String username = req.getParameter("username");
		String method = req.getParameter("method");
		String course = req.getParameter("course");
		
		if(method.equals("add"))
			userAddCourse(username,course);
		else
			removeCourse(course,username);

		resp.getWriter().print("done");
	}
	
	@SuppressWarnings("unchecked")
	public User removeCourse(String course, String username)
	{
		Entity userEntity = getUserEntity(username);
		ArrayList<String> courseList = new ArrayList<String>();
		if(userEntity.hasProperty("courseList"))
			courseList = (ArrayList<String>)userEntity.getProperty("courseList");
		courseList.remove(course);
		userEntity.setProperty("courseList", courseList);
		datastore.put(userEntity);
		memcache.put("user_"+username, userEntity);
		
		return UserServiceImpl.userFromEntity(userEntity);
	}
	
	@SuppressWarnings("unchecked")
	public void userAddCourse(String username, String course)
	{
		Entity user = getUserEntity(username);

		ArrayList<String> courseList = new ArrayList<String>();
		if(user != null && user.hasProperty("courseList"))
			courseList = (ArrayList<String>) user.getProperty("courseList");
		
		if(!courseList.contains(course))
			courseList.add(course);
		
		user.setProperty("courseList", courseList);
		datastore.put(user);
		memcache.put("user_" + username, user);
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
			{
				ex.printStackTrace();
			}
		}
		return user;
	}
}