package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
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
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

@SuppressWarnings("serial")
public class DeletePostServlet extends HttpServlet
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	IndexSpec postIndexSpec = IndexSpec.newBuilder().setName("postsIndex").build();
	Index postIndex = SearchServiceFactory.getSearchService().getIndex(postIndexSpec);
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

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
		resp.setContentType("text/plain");
		String postKey = req.getParameter("postKey");
		Entity post = getPost(postKey);
		
		if(post != null)
		{
			if(post.hasProperty("attachmentKeys") && post.getProperty("attachmentKeys") != null)
			{
				for(String key : (ArrayList<String>)post.getProperty("attachmentKeys"))
				{
					blobstoreService.delete(new BlobKey(key));
				}
			}
			
			memcache.delete(post.getKey());
			datastore.delete(post.getKey());
			postIndex.delete(String.valueOf(post.getKey().getId()));
		}

		Query q = new Query("Comment");
		q.setFilter(new FilterPredicate("postKey", FilterOperator.EQUAL, postKey));
		for(Entity result : datastore.prepare(q).asIterable())
		{
			deleteComment(String.valueOf(result.getKey().getId()));
		}
		
		resp.getWriter().print("done");
	}
	
	@SuppressWarnings("unchecked")
	public void deleteComment(String commentKey)
	{
		Entity comment = getCommentEntity(commentKey);
		if(comment != null)
		{
			if(comment.hasProperty("attachmentKeys") && comment.getProperty("attachmentKeys") != null)
			{
				for(String key : (ArrayList<String>)comment.getProperty("attachmentKeys"))
				{
					blobstoreService.delete(new BlobKey(key));
				}
			}
			
			memcache.delete(comment.getKey());
			datastore.delete(comment.getKey());
		}
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
}