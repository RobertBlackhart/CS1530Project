package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs1530.group4.addendum.shared.Comment;
import com.cs1530.group4.addendum.shared.Post;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class GetPostsServlet extends HttpServlet
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
		String streamLevel = req.getParameter("level");
		String sort = req.getParameter("sort");
		Entity user = getUserEntity(username);
		ArrayList<String> courses = new ArrayList<String>();
		courses.add(streamLevel);
		if(streamLevel == null)
		{
			if(user.hasProperty("courseList"))
				courses = (ArrayList<String>)user.getProperty("courseList");
			courses.add("all");
		}
		
		ArrayList<Post> posts = getPosts(0,courses,username,sort);
		
		resp.setContentType("application/json");
		Gson gson = new Gson();
		String json = gson.toJson(posts);
		resp.getWriter().print(json);
	}
	
	public ArrayList<Post> getPosts(int startIndex, ArrayList<String> streamLevels, String requestingUser, String sort)
	{
		ArrayList<Post> posts = new ArrayList<Post>();
		ArrayList<Key> datastoreGet = new ArrayList<Key>();

		FetchOptions options = FetchOptions.Builder.withOffset(startIndex);
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

		for(Entity entity : datastore.prepare(q).asList(options))
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
			catch(EntityNotFoundException ex)
			{
				ex.printStackTrace();
			}
		}
		return user;
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
		post.setComments(getComments(post.getPostKey(), requestingUser));
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
	
	private ArrayList<Comment> getComments(String postKey, String requestingUser)
	{
		ArrayList<Comment> comments = new ArrayList<Comment>();
		ArrayList<Key> datastoreGet = new ArrayList<Key>();
		Query q = new Query("Comment").setKeysOnly();
		q.setFilter(new FilterPredicate("postKey", FilterOperator.EQUAL, postKey));

		for(Entity entity : datastore.prepare(q).asIterable())
		{
			if(memcache.contains(entity.getKey()))
				comments.add(commentFromEntity((Entity) memcache.get(entity.getKey()), requestingUser));
			else
				datastoreGet.add(entity.getKey());
		}
		Map<Key, Entity> results = datastore.get(datastoreGet);
		for(Entity entity : results.values())
		{
			comments.add(commentFromEntity(entity, requestingUser));
			memcache.put(entity.getKey(), entity);
		}

		Collections.sort(comments);
		return comments;
	}
	
	@SuppressWarnings("unchecked")
	private Comment commentFromEntity(Entity entity, String requestingUser)
	{
		Comment comment = new Comment();
		if(entity.getProperty("content") instanceof String)
			comment.setContent((String) entity.getProperty("content"));
		else
			comment.setContent(((Text) entity.getProperty("content")).getValue());
		if(entity.hasProperty("plusOne"))
			comment.setPlusOnes(Integer.valueOf(entity.getProperty("plusOne").toString()));
		else
			comment.setPlusOnes(0);
		if(entity.hasProperty("usersPlusOne") && entity.getProperty("usersPlusOne") != null)
		{
			ArrayList<String> usersPlusOne = (ArrayList<String>) entity.getProperty("usersPlusOne");
			comment.setPlusOned(usersPlusOne.contains(requestingUser));
		}
		comment.setCommentTime((Date) entity.getProperty("time"));
		comment.setUsername((String) entity.getProperty("username"));
		comment.setCommentKey(String.valueOf(entity.getKey().getId()));
		if(entity.hasProperty("edited"))
			comment.setLastEdit((Date) entity.getProperty("edited"));

		return comment;
	}
}