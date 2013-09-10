/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.cs1530.group4.classweb.server;

import java.util.ArrayList;

import com.cs1530.group4.classweb.client.UserService;
import com.cs1530.group4.classweb.shared.Course;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements UserService
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	IndexSpec indexSpec = IndexSpec.newBuilder().setName("coursesIndex").build();
	Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

	@Override
	public Boolean doLogin(String username, String password)
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
			catch(EntityNotFoundException e1)
			{
				return false;
			}
		}

		if(user != null)
		{
			if(user.hasProperty("password"))
			{
				if(user.getProperty("password").toString().equals(password))
					return true;
				else
					return false;
			}
		}
		else
			return false;

		return false;
	}

	@Override
	public Boolean createUser(String username, String password, String firstName, String lastName)
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
			catch(EntityNotFoundException e1)
			{
				user = null;
			}
		}

		if(user != null)
			return false;
		else
		{
			user = new Entity("User", username);
			user.setProperty("username", username);
			user.setProperty("password", password);
			user.setProperty("firstName", firstName);
			user.setProperty("lastName", lastName);
			memcache.put("user_" + username, user);
			datastore.put(user);
			return true;
		}
	}

	@Override
	public ArrayList<Course> courseSearch(String subjectCode, int catalogueNumber, String courseName, String courseDescription)
	{
		ArrayList<Course> courses = new ArrayList<Course>();
		Entity courseEntity = null;
		if(memcache.contains("course_" + subjectCode + catalogueNumber))
			courseEntity = ((Entity) memcache.get("course_" + subjectCode + catalogueNumber));
		else
			courseEntity = getEntityFromDatastore(subjectCode,catalogueNumber);
		
		if(courseEntity != null) //we have an exact match
		{
			courses.add(createCourseFromEntity(courseEntity));
			return courses;
		}
		else
		//we have to try to get some partial matches
		{
			if(!subjectCode.equals(""))
			{
				//add all courses that have the specified subjectCode (i.e. CS)
				Query q = new Query("Course").setFilter(new FilterPredicate("subjectCode", FilterOperator.EQUAL, subjectCode.toUpperCase()));
				PreparedQuery pq = datastore.prepare(q);
				for(Entity result : pq.asIterable())
				{
					Course course = createCourseFromEntity(result);
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
					Course course = createCourseFromEntity(result);
					if(!courses.contains(course))
						courses.add(course);
				}
			}

			if(!courseName.equals(""))
			{
				//do partial matching on courseName
				Results<ScoredDocument> results = index.search(courseName);
				for(ScoredDocument document : results)
				{
					String code = document.getOnlyField("subjectCode").getText();
					int num = document.getOnlyField("catalogueNumber").getNumber().intValue();
					Entity entity = getEntityFromDatastore(code,num);
					if(entity != null)
					{
						Course course = createCourseFromEntity(entity);
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
					Entity entity = getEntityFromDatastore(code,num);
					if(entity != null)
					{
						Course course = createCourseFromEntity(entity);
						if(!courses.contains(course))
							courses.add(course);
					}
				}
			}
		}

		return courses;
	}

	private Entity getEntityFromDatastore(String subjectCode, int catalogueNumber)
	{
		Entity courseEntity = null;
		try
		{
			courseEntity = datastore.get(KeyFactory.createKey("Course",subjectCode + catalogueNumber + "")); //"" should guard against null problems
		}
		catch(EntityNotFoundException e1)
		{
			courseEntity = null;
		}
		
		return courseEntity;
	}

	private Course createCourseFromEntity(Entity entity)
	{
		String code = entity.getProperty("subjectCode").toString();
		int num = Integer.parseInt(entity.getProperty("catalogueNumber").toString());
		String name = entity.getProperty("courseName").toString();
		String desc = entity.getProperty("courseDescription").toString();
		return new Course(code, num, name, desc);
	}

	@Override
	public void adminAddCourse(String subjectCode, int catalogueNumber, String courseName, String courseDescription)
	{
		Document doc = Document.newBuilder()
			    .setId(subjectCode+catalogueNumber)
			    .addField(Field.newBuilder().setName("subjectCode").setText(subjectCode))
			    .addField(Field.newBuilder().setName("catalogueNumber").setNumber(catalogueNumber))
			    .addField(Field.newBuilder().setName("courseName").setText(courseName))
			    .addField(Field.newBuilder().setName("courseDescription").setText(courseDescription))
			    .build();
		index.put(doc);
		
		Entity courseEntity = new Entity("Course",subjectCode+catalogueNumber);
		courseEntity.setProperty("subjectCode", subjectCode.toUpperCase()); //always upper case for search purposes
		courseEntity.setProperty("catalogueNumber",catalogueNumber);
		courseEntity.setProperty("courseName",courseName);
		courseEntity.setProperty("courseDescription", courseDescription);
		memcache.put("course_"+subjectCode+catalogueNumber, courseEntity);
		datastore.put(courseEntity);
	}

	@Override
	public void userAddCourse(String username, ArrayList<String> courseIds)
	{
		Entity user = null;
		try
		{
			user = datastore.get(KeyFactory.createKey("User", username));
		}
		catch(EntityNotFoundException ex)
		{
			ex.printStackTrace();
			return; //shouldn't happen, but if it does then just do nothing anyway
		}
		
		ArrayList<String> courseList = new ArrayList<String>();
		if(user.hasProperty("courseList"))
			courseList = (ArrayList<String>)user.getProperty("courseList");
		for(String courseId : courseIds)
		{
			if(!courseList.contains(courseId))
				courseList.add(courseId);
		}
		user.setProperty("courseList",courseList);
		datastore.put(user);
	}

	@Override
	public ArrayList<String> getUserCourses(String username)
	{
		Entity user = null;
		try
		{
			user = datastore.get(KeyFactory.createKey("User", username));
		}
		catch(EntityNotFoundException ex)
		{
			ex.printStackTrace();
			return null; //shouldn't happen, but if it does then just do nothing anyway
		}
		
		ArrayList<String> courseList = new ArrayList<String>();
		if(user.hasProperty("courseList"))
			courseList = (ArrayList<String>)user.getProperty("courseList");
		
		return courseList;
	}
}
