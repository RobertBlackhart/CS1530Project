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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class CourseSearchServlet extends HttpServlet
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
		ArrayList<Course> courses = new ArrayList<Course>();
		String subjectCode = req.getParameter("subjectCcode");
		int catalogueNumber = Integer.valueOf(req.getParameter("catalogueNumber"));
		String courseName = req.getParameter("courseName");
		String courseDescription = req.getParameter("courseDescription");
		
		Entity courseEntity = null;
		if(memcache.contains("course_" + subjectCode + catalogueNumber))
			courseEntity = ((Entity) memcache.get("course_" + subjectCode + catalogueNumber));
		else
			courseEntity = getEntityFromDatastore(subjectCode, catalogueNumber);

		if(courseEntity != null) //we have an exact match
		{
			courses.add(getCourse(courseEntity));
		}
		else //we have to try to get some partial matches
		{
			if(!subjectCode.equals(""))
			{
				//add all courses that have the specified subjectCode (i.e. CS)
				Query q = new Query("Course").setFilter(new FilterPredicate("subjectCode", FilterOperator.EQUAL, subjectCode.toUpperCase()));
				PreparedQuery pq = datastore.prepare(q);
				for(Entity result : pq.asIterable())
				{
					Course course = getCourse(result);
					if(!courses.contains(course))
						courses.add(course);
				}
			}

			if(catalogueNumber != 0)
			{
				//add all courses that have the specified catalogueNumber (i.e. 1530) - probably only one
				//TODO possibly implement > and < operators
				Query q = new Query("Course").setFilter(new FilterPredicate("catalogueNumber", FilterOperator.EQUAL, catalogueNumber));
				PreparedQuery pq = datastore.prepare(q);
				for(Entity result : pq.asIterable())
				{
					Course course = getCourse(result);
					if(!courses.contains(course))
						courses.add(course);
				}
			}

			IndexSpec indexSpec = IndexSpec.newBuilder().setName("coursesIndex").build();
			Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

			if(!courseName.equals(""))
			{
				//do partial matching on courseName
				Results<ScoredDocument> results = index.search(courseName);
				for(ScoredDocument document : results)
				{
					String code = document.getOnlyField("subjectCode").getText();
					int num = document.getOnlyField("catalogueNumber").getNumber().intValue();
					Entity entity = getEntityFromDatastore(code, num);
					if(entity != null)
					{
						Course course = getCourse(entity);
						if(!courses.contains(course))
							courses.add(course);
					}
				}
			}

			if(!courseDescription.equals(""))
			{
				//do partial matching on courseDescription
				Results<ScoredDocument> results = index.search(courseDescription);
				for(ScoredDocument document : results)
				{
					String code = document.getOnlyField("subjectCode").getText();
					int num = document.getOnlyField("catalogueNumber").getNumber().intValue();
					Entity entity = getEntityFromDatastore(code, num);
					if(entity != null)
					{
						Course course = getCourse(entity);
						if(!courses.contains(course))
							courses.add(course);
					}
				}
			}
		}

		resp.setContentType("application/json");
		Gson gson = new Gson();
		String json = gson.toJson(courses);
		resp.getWriter().print(json);
	}
	
	private Entity getEntityFromDatastore(String subjectCode, int catalogueNumber)
	{
		Entity courseEntity = null;
		try
		{
			courseEntity = datastore.get(KeyFactory.createKey("Course", subjectCode + catalogueNumber + "")); //"" should guard against null problems
		}
		catch(EntityNotFoundException e1)
		{
			courseEntity = null;
		}

		return courseEntity;
	}

	private Course getCourse(Entity entity)
	{
		String code = entity.getProperty("subjectCode").toString();
		int num = Integer.parseInt(entity.getProperty("catalogueNumber").toString());
		String name = entity.getProperty("courseName").toString();
		String desc = "";
		try
		{
			desc = ((Text)entity.getProperty("courseDescription")).getValue();
		}
		catch(ClassCastException ex) //earlier implementation stored the description as a String instead of Text
		{
			desc = (String) entity.getProperty("courseDescription");
		}
		Course course = new Course(code, num, name, desc);
		course.setCourseRequestKey(String.valueOf(entity.getKey().getId()));
		return course;
	}
}