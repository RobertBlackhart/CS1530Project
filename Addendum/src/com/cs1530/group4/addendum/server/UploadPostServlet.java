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
public class UploadPostServlet extends HttpServlet
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
		double secondsSinceRedditEpoch = System.currentTimeMillis() / 1000 - 1134028003;
		double score = secondsSinceRedditEpoch / 45000;
		String username = req.getParameter("username");
		String postText = req.getParameter("text");
		String streamLevel = req.getParameter("level");
		Gson gson = new Gson();
		Date now = gson.fromJson(req.getParameter("time"), Date.class);
		ArrayList<String> attachmentKeys = gson.fromJson(req.getParameter("attachmentKeys"), new TypeToken<ArrayList<String>>(){}.getType());
		ArrayList<String> attachmentNames = gson.fromJson(req.getParameter("attachmentNames"), new TypeToken<ArrayList<String>>(){}.getType());
		
		Entity post = new Entity("Post");
		post.setProperty("username", username);
		post.setProperty("postContent", new Text(formatCode(postText)));
		post.setProperty("streamLevel", streamLevel);
		post.setProperty("score", score);
		post.setProperty("time", now);
		post.setProperty("upvotes", 0);
		post.setProperty("downvotes", 0);
		post.setProperty("attachmentKeys", attachmentKeys);
		post.setProperty("attachmentNames", attachmentNames);
		post.setProperty("usersVotedUp", new ArrayList<String>());
		post.setProperty("usersVotedDown", new ArrayList<String>());
		datastore.put(post);
		memcache.put(post.getKey(), post); //when looking up posts, do a key only query and check if they are in memcache first

		//for text search within posts
		Document doc = Document.newBuilder()
				.setId(String.valueOf(post.getKey().getId()))
				.addField(Field.newBuilder().setName("username").setText(username))
				.addField(Field.newBuilder().setName("content").setText(postText))
				.addField(Field.newBuilder().setName("time").setDate(now))
				.addField(Field.newBuilder().setName("level").setText(streamLevel))
				.build();
		postIndex.put(doc);
		
		Entity userStatsEntity = UserServiceImpl.getUserStats(username);
		int numPosts = 1;
		if(userStatsEntity.hasProperty("numPosts"))
		{
			numPosts = Integer.valueOf(userStatsEntity.getProperty("numPosts").toString()) + 1;
			userStatsEntity.setProperty("numPosts", numPosts);
		}
		else
			userStatsEntity.setProperty("numPosts",numPosts);

		UserServiceImpl.checkParticipation(userStatsEntity,username);
		
		datastore.put(userStatsEntity);
		memcache.put("userStats_"+username, userStatsEntity);
		
		Entity achievementEntity = null;
		if(numPosts == 1)
			achievementEntity = UserServiceImpl.getAchievementEntity("nicePost");
		if(achievementEntity != null)
			UserServiceImpl.addUserToAchievement(achievementEntity,username);
		
		resp.setContentType("text/plain");
		resp.getWriter().print("done");
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