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
package com.cs1530.group4.addendum.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cs1530.group4.addendum.client.UserService;
import com.cs1530.group4.addendum.shared.Comment;
import com.cs1530.group4.addendum.shared.Course;
import com.cs1530.group4.addendum.shared.Post;
import com.cs1530.group4.addendum.shared.User;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements UserService
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	IndexSpec postIndexSpec = IndexSpec.newBuilder().setName("postsIndex").build();
	Index postIndex = SearchServiceFactory.getSearchService().getIndex(postIndexSpec);
	IndexSpec courseIndexSpec = IndexSpec.newBuilder().setName("coursesIndex").build();
	Index courseIndex = SearchServiceFactory.getSearchService().getIndex(courseIndexSpec);

	@SuppressWarnings("unchecked")
	@Override
	public User doLogin(String username, String password)
	{
		Entity userEntity = getUserEntity(username);
		User user = null;

		if(userEntity != null)
		{
			//if(userEntity.hasProperty("emailValid") && !(Boolean)userEntity.getProperty("emailValid"))
				//return user;
			
			if(userEntity.hasProperty("password"))
			{
				if(userEntity.getProperty("password").toString().equals(password))
				{
					user = new User(username);
					if(userEntity.hasProperty("courseList"))
					{
						ArrayList<String> courseList = (ArrayList<String>)userEntity.getProperty("courseList");
						if(courseList.remove("No"))
						{
							user.setCourseList(courseList);
							userEntity.setProperty("courseList", courseList);
							datastore.put(userEntity);
							memcache.put("user_"+user.getUsername(), userEntity);
						}
						user.setCourseList(courseList);
					}
					else
						user.setCourseList(new ArrayList<String>());
				}
			}
		}
		
		return user;
	}

	@Override
	public String createUser(String username, String password, String email, String firstName, String lastName)
	{
		Entity user = getUserEntity(username);

		if(user != null)
			return "user_exists";
		if(!validateEmail(email))
			return "email_exists";
		
		else
		{
			String uuid = UUID.randomUUID().toString();
			user = new Entity("User", username);
			user.setProperty("username", username);
			user.setProperty("password", password);
			user.setProperty("email", email);
			user.setProperty("emailValid",false);
			user.setProperty("uuid", uuid);
			user.setProperty("firstName", firstName);
			user.setProperty("lastName", lastName);
			
			sendEmail(email, uuid, username);
			
			memcache.put("user_" + username, user);
			datastore.put(user);
			return "success";
		}
	}
	
	private void sendEmail(String email, String uuid, String username)
	{
        String msgBody = "Welcome to Addendum!\n\n" +
        		"In order to validate your account, please click on the link below or copy and paste it into" +
        		"your browser's address bar:\n\n" +
        		"http://studentclassnet.appspot.com/addendum/validate?username="+username+"&uuid="+uuid;
        
        try 
        {
        	Message msg = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
            msg.setFrom(new InternetAddress("addendumapp@gmail.com", "Addendum"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            msg.setSubject("Welcome to Addendum");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean validateEmail(String email)
	{
		Query q = new Query("User");
		q.setFilter(new FilterPredicate("email", FilterOperator.EQUAL, email));
		for(Entity entity : datastore.prepare(q).asIterable())
			return false;
		
		return true;
	}

	@Override
	public ArrayList<Course> courseSearch(String subjectCode, int catalogueNumber, String courseName, String courseDescription)
	{
		ArrayList<Course> courses = new ArrayList<Course>();
		Entity courseEntity = null;
		if(memcache.contains("course_" + subjectCode + catalogueNumber))
			courseEntity = ((Entity) memcache.get("course_" + subjectCode + catalogueNumber));
		else
			courseEntity = getEntityFromDatastore(subjectCode, catalogueNumber);

		if(courseEntity != null) //we have an exact match
		{
			courses.add(getCourse(courseEntity));
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

		return courses;
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
		String desc = entity.getProperty("courseDescription").toString();
		Course course = new Course(code, num, name, desc);
		course.setCourseRequestKey(String.valueOf(entity.getKey().getId()));
		return course;
	}

	@Override
	public void newCourseRequest(String subjectCode, int catalogueNumber, String courseName, String courseDescription)
	{
		System.out.println("here");
		Entity courseEntity = new Entity("CourseRequest", subjectCode + catalogueNumber);
		courseEntity.setProperty("subjectCode", subjectCode.toUpperCase()); //always upper case for search purposes
		courseEntity.setProperty("catalogueNumber", catalogueNumber);
		courseEntity.setProperty("courseName", courseName);
		courseEntity.setProperty("courseDescription", new Text(courseDescription));
		datastore.put(courseEntity);
		memcache.put(courseEntity.getKey(), courseEntity);
	}

	@Override
	public ArrayList<Course> getCourseRequests()
	{
		ArrayList<Course> courseRequests = new ArrayList<Course>();
		ArrayList<Key> keys = new ArrayList<Key>();

		Query q = new Query("CourseRequest").setKeysOnly();
		for(Entity entity : datastore.prepare(q).asIterable())
		{
			if(memcache.contains(entity.getKey()))
				courseRequests.add(getCourse((Entity)memcache.get(entity.getKey())));
			else
				keys.add(entity.getKey());
		}
		for(Entity entity : datastore.get(keys).values())
		{
			courseRequests.add(getCourse(entity));
		}
		return courseRequests;
	}
	
	@Override
	public void removeCourseRequest(Course course, boolean add)
	{
		if(add)
		{
			adminAddCourse(course.getSubjectCode(), course.getCourseNumber(), course.getCourseName(), course.getCourseDescription());
		}
		Key key = KeyFactory.createKey("CourseRequest",Long.valueOf(course.getCourseRequestKey()));
		datastore.delete(key);
		memcache.delete(key);
	}

	@Override
	public ArrayList<Post> getFlaggedPosts()
	{
		ArrayList<Post> reportedPosts = new ArrayList<Post>();

		Query q = new Query("Post");
		q.setFilter(new FilterPredicate("reported",FilterOperator.EQUAL,"true"));
		for(Entity entity : datastore.prepare(q).asIterable())
		{
			reportedPosts.add(postFromEntity(entity,"Administrator"));
		}
		return reportedPosts;
	}
	
	@Override
	public void flagPost(String postKey, String reason, boolean setFlagged)
	{
		Entity post = getPost(postKey);
		if(post != null)
		{
			post.setProperty("reported", true);
			post.setProperty("reportReason", reason);
			memcache.put(post.getKey(), post);
			datastore.put(post);
		}
	}

	@Override
	public void adminAddCourse(String subjectCode, int catalogueNumber, String courseName, String courseDescription)
	{
		Document doc = Document.newBuilder().setId(subjectCode + catalogueNumber).addField(Field.newBuilder().setName("subjectCode").setText(subjectCode)).addField(Field.newBuilder().setName("catalogueNumber").setNumber(catalogueNumber)).addField(Field.newBuilder().setName("courseName").setText(courseName)).addField(Field.newBuilder().setName("courseDescription").setText(courseDescription)).build();
		courseIndex.put(doc);

		Entity courseEntity = new Entity("Course", subjectCode + catalogueNumber);
		courseEntity.setProperty("subjectCode", subjectCode.toUpperCase()); //always upper case for search purposes
		courseEntity.setProperty("catalogueNumber", catalogueNumber);
		courseEntity.setProperty("courseName", courseName);
		courseEntity.setProperty("courseDescription", new Text(courseDescription));
		memcache.put("course_" + subjectCode + catalogueNumber, courseEntity);
		datastore.put(courseEntity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void userAddCourse(String username, ArrayList<String> courseIds)
	{
		Entity user = getUserEntity(username);

		ArrayList<String> courseList = new ArrayList<String>();
		if(user != null && user.hasProperty("courseList"))
			courseList = (ArrayList<String>) user.getProperty("courseList");
		for(String courseId : courseIds)
		{
			if(!courseList.contains(courseId))
				courseList.add(courseId.trim());
		}
		user.setProperty("courseList", courseList);
		datastore.put(user);
		memcache.put("user_" + username, user);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<String> getUserCourses(String username)
	{
		Entity user = getUserEntity(username);

		ArrayList<String> courseList = new ArrayList<String>();
		if(user != null && user.hasProperty("courseList"))
			courseList = (ArrayList<String>) user.getProperty("courseList");

		return courseList;
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
			}
			catch(EntityNotFoundException ex){}
		}
		return user;
	}

	@Override
	public void uploadPost(String username, String postHtml, String postPlain, String streamLevel, Date time)
	{
		double secondsSinceRedditEpoch = System.currentTimeMillis() / 1000 - 1134028003;
		double score = secondsSinceRedditEpoch / 45000;

		Entity post = new Entity("Post");
		post.setProperty("username", username);
		post.setProperty("postContent", new Text(formatCode(postHtml)));
		post.setProperty("streamLevel", streamLevel);
		post.setProperty("score", score);
		post.setProperty("time", time);
		post.setProperty("upvotes", 0);
		post.setProperty("downvotes", 0);
		post.setProperty("usersVotedUp", new ArrayList<String>());
		post.setProperty("usersVotedDown", new ArrayList<String>());
		datastore.put(post);
		memcache.put(post.getKey(), post); //when looking up posts, do a key only query and check if they are in memcache first

		//for text search within posts
		Document doc = Document.newBuilder().setId(String.valueOf(post.getKey().getId()))
				.addField(Field.newBuilder().setName("username").setText(username))
				.addField(Field.newBuilder().setName("content").setText(postPlain))
				.addField(Field.newBuilder().setName("time").setDate(time))
				.addField(Field.newBuilder().setName("level").setText(streamLevel)).build();
		postIndex.put(doc);
	}

	private String formatCode(String postHtml)
	{
		int begin = postHtml.indexOf("[CODE]");
		int end = postHtml.indexOf("[/CODE]");

		if(begin != -1 && end != -1)
		{
			postHtml = postHtml.substring(0, begin) + "<b>CODE:</b><br><div style=\"background-color:#99CCFF;text-indent:10px;\">" + postHtml.substring(begin + 6, end) + "</div>";
		}
		return postHtml;
	}

	@Override
	public ArrayList<Post> postSearch(int startIndex, String searchText, String requestingUser)
	{
		ArrayList<Post> results = new ArrayList<Post>();
		ArrayList<Key> datastoreGet = new ArrayList<Key>();
		
		try
		{
			QueryOptions options = QueryOptions.newBuilder()
		            .setLimit(11)  
		            .setOffset(startIndex)
		            .build();
			com.google.appengine.api.search.Query query = com.google.appengine.api.search.Query.newBuilder().setOptions(options).build(searchText);
			Results<ScoredDocument> docs = postIndex.search(query);
			for(ScoredDocument document : docs)
			{
				Key entityKey = KeyFactory.createKey("Post", Long.valueOf(document.getId()));
				if(memcache.contains(entityKey))
					results.add(postFromEntity((Entity) memcache.get(entityKey), requestingUser));
				else
					datastoreGet.add(entityKey);
			}

			Map<Key, Entity> gets = datastore.get(datastoreGet);
			for(Entity entity : gets.values())
			{
				results.add(postFromEntity(entity, requestingUser));
				memcache.put(entity.getKey(), entity);
			}
		}
		catch(Exception ex)
		{
		}

		return results;
	}

	@Override
	public void editPost(String postKey, String postHtml, String postPlain)
	{
		Entity post = getPost(postKey);
		if(post != null)
		{
			post.setProperty("postContent", new Text(formatCode(postHtml)));
			post.setProperty("edited", new Date());
			Document doc = Document.newBuilder() //you can't update a document once its in the index, but you can replace it with a new one
			.setId(String.valueOf(post.getKey().getId())).addField(Field.newBuilder().setName("username").setText((String) post.getProperty("username"))).addField(Field.newBuilder().setName("content").setText(postPlain)).addField(Field.newBuilder().setName("time").setDate((Date) post.getProperty("time"))).build();
			postIndex.put(doc);
			memcache.put(post.getKey(), post);
			datastore.put(post);
		}
	}

	@Override
	public ArrayList<Post> getPosts(int startIndex, ArrayList<String> streamLevels, String requestingUser, String sort)
	{
		ArrayList<Post> posts = new ArrayList<Post>();
		ArrayList<Key> datastoreGet = new ArrayList<Key>();

		//FetchOptions options = FetchOptions.Builder.withOffset(startIndex);
		Filter filter = null;
		if(streamLevels.size() > 1)
		{
			ArrayList<Filter> filters = new ArrayList<Filter>();
			for(String streamLevel : streamLevels)
			{
				filters.add(new FilterPredicate("streamLevel", FilterOperator.EQUAL, streamLevel));
			}
			filter = CompositeFilterOperator.or(filters);
		}
		else
			filter = new FilterPredicate("streamLevel", FilterOperator.EQUAL, streamLevels.get(0));
		Query q = new Query("Post").setKeysOnly();
		q.setFilter(filter);

		for(Entity entity : datastore.prepare(q).asIterable())
		{
			if(memcache.contains(entity.getKey()))
				posts.add(postFromEntity((Entity) memcache.get(entity.getKey()), requestingUser));
			else
				datastoreGet.add(entity.getKey());
		}
		Map<Key, Entity> results = datastore.get(datastoreGet);
		for(Entity entity : results.values())
		{
			posts.add(postFromEntity(entity, requestingUser));
			memcache.put(entity.getKey(), entity);
		}
		
		if(sort.equals("Popular"))
			Collections.sort(posts, Post.PostScoreComparator);
		if(sort.equals("New"))
			Collections.sort(posts, Post.PostTimeComparator);
		
		ArrayList<Post> returnPosts = new ArrayList<Post>();
		for(int i=startIndex; i<Math.min(startIndex+11, posts.size()); i++)
			returnPosts.add(posts.get(i));
		
		return returnPosts;
	}

	@SuppressWarnings("unchecked")
	private Post postFromEntity(Entity entity, String requestingUser)
	{
		Post post = new Post();
		if(entity.getProperty("postContent") instanceof String)
			post.setPostContent((String) entity.getProperty("postContent"));
		else
			post.setPostContent(((Text) entity.getProperty("postContent")).getValue());
		post.setStreamLevel((String) entity.getProperty("streamLevel"));
		post.setPostTime((Date) entity.getProperty("time"));
		post.setUsername((String) entity.getProperty("username"));
		post.setUpvotes(Integer.valueOf(entity.getProperty("upvotes").toString()));
		post.setDownvotes(Integer.valueOf(entity.getProperty("downvotes").toString()));
		post.setScore(Double.valueOf(entity.getProperty("score").toString()));
		post.setPostKey(String.valueOf(entity.getKey().getId()));
		post.setComments(getComments(post.getPostKey()));
		if(entity.hasProperty("edited"))
			post.setLastEdit((Date) entity.getProperty("edited"));
		if(entity.hasProperty("usersVotedUp") && entity.getProperty("usersVotedUp") != null)
		{
			ArrayList<String> usersVotedUp = (ArrayList<String>) entity.getProperty("usersVotedUp");
			post.setUpvoted(usersVotedUp.contains(requestingUser));
		}
		if(entity.hasProperty("usersVotedDown") && entity.getProperty("usersVotedDown") != null)
		{
			ArrayList<String> usersVotedDown = (ArrayList<String>) entity.getProperty("usersVotedDown");
			post.setDownvoted(usersVotedDown.contains(requestingUser));
		}
		if(entity.hasProperty("reported"))
			post.setReported(Boolean.valueOf(entity.getProperty("reported").toString()));
		else
			post.setReported(false);
		if(entity.hasProperty("reportReason"))
			post.setReportReason((String)entity.getProperty("reportReason"));

		return post;
	}

	private ArrayList<Comment> getComments(String postKey)
	{
		ArrayList<Comment> comments = new ArrayList<Comment>();
		ArrayList<Key> datastoreGet = new ArrayList<Key>();
		Query q = new Query("Comment").setKeysOnly();
		q.setFilter(new FilterPredicate("postKey", FilterOperator.EQUAL, postKey));

		for(Entity entity : datastore.prepare(q).asIterable())
		{
			if(memcache.contains(entity.getKey()))
				comments.add(commentFromEntity((Entity) memcache.get(entity.getKey())));
			else
				datastoreGet.add(entity.getKey());
		}
		Map<Key, Entity> results = datastore.get(datastoreGet);
		for(Entity entity : results.values())
		{
			comments.add(commentFromEntity(entity));
			memcache.put(entity.getKey(), entity);
		}

		Collections.sort(comments);
		return comments;
	}

	private Comment commentFromEntity(Entity entity)
	{
		Comment comment = new Comment();
		if(entity.getProperty("content") instanceof String)
			comment.setContent((String) entity.getProperty("content"));
		else
			comment.setContent(((Text) entity.getProperty("content")).getValue());
		comment.setCommentTime((Date) entity.getProperty("time"));
		comment.setUsername((String) entity.getProperty("username"));
		comment.setCommentKey(String.valueOf(entity.getKey().getId()));
		if(entity.hasProperty("edited"))
			comment.setLastEdit((Date) entity.getProperty("edited"));

		return comment;
	}

	@Override
	public Boolean upvotePost(String postKey, String user)
	{
		return changeScore(postKey, "upvotes", user);
	}

	@Override
	public Boolean downvotePost(String postKey, String user)
	{
		return changeScore(postKey, "downvotes", user);
	}

	@SuppressWarnings("unchecked")
	private Boolean changeScore(String postKey, String property, String user)
	{
		boolean success = false;
		Entity post = getPost(postKey);
		if(post != null)
		{
			ArrayList<String> upUsers = new ArrayList<String>();
			ArrayList<String> downUsers = new ArrayList<String>();
			if(post.hasProperty("usersVotedUp") && post.getProperty("usersVotedUp") != null)
				upUsers = (ArrayList<String>) post.getProperty("usersVotedUp");
			if(post.hasProperty("usersVotedDown") && post.getProperty("usersVotedDown") != null)
				downUsers = (ArrayList<String>) post.getProperty("usersVotedDown");
			if(property.equals("upvotes") && upUsers.contains(user))
			{
				post.setProperty("upvotes", Integer.valueOf(post.getProperty("upvotes").toString())-1);
				upUsers.remove(user);
			}
			else if(property.equals("downvotes") && downUsers.contains(user))
			{
				post.setProperty("downvotes", Integer.valueOf(post.getProperty("downvotes").toString())-1);
				downUsers.remove(user);
			}
			else if(property.equals("upvotes"))
			{
				post.setProperty(property, Integer.valueOf(post.getProperty(property).toString()) + 1);
				upUsers.add(user);
				if(downUsers.remove(user))
					post.setProperty(property, Integer.valueOf(post.getProperty(property).toString()) + 1);
				post.setProperty("usersVotedUp", upUsers);
				post.setProperty("usersVotedDown", downUsers);
				success = true;
			}
			else if(property.equals("downvotes"))
			{
				post.setProperty(property, Integer.valueOf(post.getProperty(property).toString()) + 1);
				downUsers.add(user);
				if(upUsers.remove(user))
					post.setProperty(property, Integer.valueOf(post.getProperty(property).toString()) + 1);
				post.setProperty("usersVotedUp", upUsers);
				post.setProperty("usersVotedDown", downUsers);
				success = true;
			}
			updateScore(post, user);
			memcache.put(post.getKey(), post);
			datastore.put(post);
		}

		return success;
	}

	private Entity getPost(String postKey)
	{
		Entity post = null;
		Key key = KeyFactory.createKey("Post", Long.valueOf(postKey).longValue());
		if(memcache.contains(key))
			post = (Entity) memcache.get(key);
		else
		{
			try
			{
				post = datastore.get(key);
			}
			catch(EntityNotFoundException ex)
			{
				ex.printStackTrace();
			}
		}

		return post;
	}

	private void updateScore(Entity entity, String user)
	{
		Post post = postFromEntity(entity, user);
		int s = post.getUpvotes() - post.getDownvotes();
		double order = Math.log10(Math.max(Math.abs(s), 1));
		int sign = 1;
		if(s == 0)
			sign = 0;
		if(s < 0)
			sign = -1;
		double secondsSinceRedditEpoch = System.currentTimeMillis() / 1000 - 1134028003;
		double score = order + sign * secondsSinceRedditEpoch / 45000;
		entity.setProperty("score", score);
		memcache.put(entity.getKey(), entity);
		datastore.put(entity);
	}

	@Override
	public String uploadComment(String postKey, Comment comment)
	{
		Entity commentEntity = new Entity("Comment");
		commentEntity.setProperty("postKey", postKey);
		commentEntity.setProperty("time", comment.getCommentTime());
		commentEntity.setProperty("username", comment.getUsername());
		commentEntity.setProperty("content", new Text(comment.getContent()));
		datastore.put(commentEntity);
		memcache.put(commentEntity.getKey(), commentEntity); //when looking up posts, do a key only query and check if they are in memcache first
		return String.valueOf(commentEntity.getKey().getId());
	}

	@Override
	public String editComment(String commentKey, String commentText)
	{
		Entity comment = getComment(commentKey);
		if(comment != null)
		{
			comment.setProperty("content", new Text(commentText));
			comment.setProperty("edited", new Date());
			memcache.put(comment.getKey(), comment);
			datastore.put(comment);
		}
		return commentKey;
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

	@Override
	public void deletePost(String postKey)
	{
		Entity post = getPost(postKey);
		if(post != null)
		{
			memcache.delete(post.getKey());
			datastore.delete(post.getKey());
			postIndex.delete(String.valueOf(post.getKey().getId()));
		}

		Query q = new Query("Comment");
		q.setFilter(new FilterPredicate("postKey", FilterOperator.EQUAL, postKey));
		for(Entity result : datastore.prepare(q).asIterable())
		{
			memcache.delete(result.getKey());
			datastore.delete(result.getKey());
		}
	}

	@Override
	public void deleteComment(String commentKey)
	{
		Entity comment = getComment(commentKey);
		if(comment != null)
		{
			memcache.delete(comment.getKey());
			datastore.delete(comment.getKey());
		}
	}

	@Override
	public Boolean resetPassword(String username)
	{
		Entity user = getUserEntity(username);
		if(user != null && user.hasProperty("uuid") && user.hasProperty("email"))
		{
			String msgBody = "To reset your password, please click on the link below or copy and paste it into" +
	        		"your browser's address bar:\n\n" +
	        		"http://studentclassnet.appspot.com/addendum/passwordReset?username="+username+"&uuid="+user.getProperty("uuid").toString();
	        
	        try 
	        {
	        	Message msg = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
	            msg.setFrom(new InternetAddress("addendumapp@gmail.com", "Addendum"));
	            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getProperty("email").toString()));
	            msg.setSubject("Password Reset");
	            msg.setText(msgBody);
	            Transport.send(msg);
	            return true;
	        } 
	        catch(Exception e)
			{
				e.printStackTrace();
			}
		}
        
        return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public User changePassword(String username, String newPassword)
	{
		Entity entity = getUserEntity(username);
		if(entity != null)
		{
			entity.setProperty("password", newPassword);
			memcache.put("user_"+username, entity);
			datastore.put(entity);
			
			User user = new User(username);
			if(entity.hasProperty("courseList"))
				user.setCourseList((ArrayList<String>)entity.getProperty("courseList"));
			
			return user;
		}
		else
			return null;
	}

	@Override
	public void flagComment(String commentKey, String reason, boolean setFlagged)
	{
		// TODO Auto-generated method stub
		
	}
}
