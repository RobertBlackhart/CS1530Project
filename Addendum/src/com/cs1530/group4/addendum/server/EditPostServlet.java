package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("serial")
public class EditPostServlet extends HttpServlet
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	IndexSpec postIndexSpec = IndexSpec.newBuilder().setName("postsIndex").build();
	Index postIndex = SearchServiceFactory.getSearchService().getIndex(postIndexSpec);

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
		resp.setContentType("text/plain");
		Entity post = getPost(req.getParameter("postKey"));
		Gson gson = new Gson();
		ArrayList<String> attachmentKeys = gson.fromJson(req.getParameter("attachmentKeys"), new TypeToken<ArrayList<String>>(){}.getType());
		ArrayList<String> attachmentNames = gson.fromJson(req.getParameter("attachmentNames"), new TypeToken<ArrayList<String>>(){}.getType());
		
		if(post != null)
		{
			post.setProperty("postContent", new Text(formatCode(req.getParameter("html"))));
			post.setProperty("edited", new Date());
			post.setProperty("attachmentKeys", attachmentKeys);
			post.setProperty("attachmentNames", attachmentNames);
			Document doc = Document.newBuilder() //you can't update a document once its in the index, but you can replace it with a new one
								.setId(String.valueOf(post.getKey().getId()))
								.addField(Field.newBuilder().setName("username").setText((String) post.getProperty("username")))
								.addField(Field.newBuilder().setName("content").setText(req.getParameter("html")))
								.addField(Field.newBuilder().setName("time").setDate((Date) post.getProperty("time"))).build();
			postIndex.put(doc);
			memcache.put(post.getKey(), post);
			datastore.put(post);
			
			Entity userStatsEntity = UserServiceImpl.getUserStats((String)post.getProperty("username"));
			UserServiceImpl.checkParticipation(userStatsEntity,(String)post.getProperty("username"));
			
			datastore.put(userStatsEntity);
			memcache.put("userStats_"+(String)post.getProperty("username"), userStatsEntity);
			
			resp.getWriter().print("done");
		}
		else
			resp.getWriter().print("error");
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
}