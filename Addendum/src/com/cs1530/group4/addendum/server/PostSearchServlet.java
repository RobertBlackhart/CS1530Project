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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class PostSearchServlet extends HttpServlet
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
		IndexSpec postIndexSpec = IndexSpec.newBuilder().setName("postsIndex").build();
		Index postIndex = SearchServiceFactory.getSearchService().getIndex(postIndexSpec);
		ArrayList<Post> results = new ArrayList<Post>();
		ArrayList<Key> datastoreGet = new ArrayList<Key>();
		String searchText = req.getParameter("searchText");
		String requestingUser = req.getParameter("requestingUser");

		try
		{
			QueryOptions options = QueryOptions.newBuilder().setLimit(11).setOffset(0).build();
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
		{}

		resp.setContentType("application/json");
		Gson gson = new Gson();
		String json = gson.toJson(results);
		resp.getWriter().print(json);
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
			post.setReportReason((String) entity.getProperty("reportReason"));
		if(entity.hasProperty("attachmentKeys"))
			post.setAttachmentKeys((ArrayList<String>)entity.getProperty("attachmentKeys"));
		if(entity.hasProperty("attachmentNames"))
			post.setAttachmentNames((ArrayList<String>)entity.getProperty("attachmentNames"));

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
		if(entity.hasProperty("accepted"))
			comment.setAccepted(Boolean.valueOf(entity.getProperty("accepted").toString()));

		return comment;
	}
}