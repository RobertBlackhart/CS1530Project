package com.cs1530.group4.addendum.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs1530.group4.addendum.shared.Comment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class UploadCommentServlet extends HttpServlet
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
		Gson gson = new Gson();
		Comment comment = gson.fromJson(req.getParameter("comment"), Comment.class);
		String postKey = req.getParameter("postKey");
		
		Entity commentEntity = new Entity("Comment");
		commentEntity.setProperty("postKey", postKey);
		commentEntity.setProperty("time", comment.getCommentTime());
		commentEntity.setProperty("username", comment.getUsername());
		commentEntity.setProperty("content", new Text(comment.getContent()));
		commentEntity.setProperty("plusOne", 0);
		datastore.put(commentEntity);
		memcache.put(commentEntity.getKey(), commentEntity); //when looking up posts, do a key only query and check if they are in memcache first
		
		resp.setContentType("text/plain");
		resp.getWriter().print("done");
	}
}