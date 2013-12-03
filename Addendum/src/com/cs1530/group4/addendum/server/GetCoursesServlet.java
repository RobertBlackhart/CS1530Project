package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs1530.group4.addendum.shared.Course;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class GetCoursesServlet extends HttpServlet
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
		ArrayList<Course> userCourses = new ArrayList<Course>();
		String username = req.getParameter("username");
		Entity userEntity = getUserEntity(username);

		if(userEntity != null)
		{
			ArrayList<String> courses = (ArrayList<String>) userEntity.getProperty("courseList");
			for(String course : courses)
			{
				Entity courseEntity = null;
				if(memcache.contains("course_" + course))
					courseEntity = ((Entity) memcache.get("course_" + course));
				else
				{
					try
					{
						courseEntity = datastore.get(KeyFactory.createKey("Course", course));
						memcache.put("course_"+course,courseEntity);
					}
					catch(EntityNotFoundException e1){}
				}
				if(courseEntity != null)
					userCourses.add(UserServiceImpl.getCourse(courseEntity));
			}
		}
				
		resp.setContentType("application/json");
		Gson gson = new Gson();
		String json = gson.toJson(userCourses);
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
			{
				ex.printStackTrace();
			}
		}
		return user;
	}
}