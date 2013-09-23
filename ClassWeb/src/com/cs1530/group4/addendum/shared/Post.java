package com.cs1530.group4.addendum.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Post implements Serializable
{
	private static final long serialVersionUID = -6112430428377944014L;
	private String postContent, username, streamLevel, postKey;
	private Date postTime;
	private int upvotes, downvotes;
	private double score;
	private ArrayList<Comment> comments;
	private boolean upvoted, downvoted;

	public boolean isUpvoted()
	{
		return upvoted;
	}

	public void setUpvoted(boolean upvoted)
	{
		this.upvoted = upvoted;
	}

	public boolean isDownvoted()
	{
		return downvoted;
	}

	public void setDownvoted(boolean downvoted)
	{
		this.downvoted = downvoted;
	}

	public static Comparator<Post> PostTimeComparator = new Comparator<Post>()
	{
		@Override
		public int compare(Post post1, Post post2)
		{
			return(post2.getPostTime().compareTo(post1.getPostTime()));
		}
	};
	public static Comparator<Post> PostScoreComparator = new Comparator<Post>()
	{
		@Override
		public int compare(Post post1, Post post2)
		{
			return (int) (post2.getScore()-post1.getScore());
		}
	};

	public ArrayList<Comment> getComments()
	{
		return comments;
	}

	public void setComments(ArrayList<Comment> comments)
	{
		this.comments = comments;
	}

	public String getPostContent()
	{
		return postContent;
	}

	public int getUpvotes()
	{
		return upvotes;
	}

	public void setUpvotes(int upvotes)
	{
		this.upvotes = upvotes;
	}

	public int getDownvotes()
	{
		return downvotes;
	}

	public void setDownvotes(int downvotes)
	{
		this.downvotes = downvotes;
	}

	public void setPostContent(String postContent)
	{
		this.postContent = postContent;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public Date getPostTime()
	{
		return postTime;
	}

	public void setPostTime(Date postTime)
	{
		this.postTime = postTime;
	}

	public String getStreamLevel()
	{
		return streamLevel;
	}

	public void setStreamLevel(String streamLevel)
	{
		this.streamLevel = streamLevel;
	}

	public double getScore()
	{
		return score;
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	public String getPostKey()
	{
		return postKey;
	}

	public void setPostKey(String postKey)
	{
		this.postKey = postKey;
	}
}
