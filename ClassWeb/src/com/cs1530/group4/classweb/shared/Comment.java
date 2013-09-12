package com.cs1530.group4.classweb.shared;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable
{
	private static final long serialVersionUID = 6157099786142218688L;
	private String username, content;
	
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public Date getCommentTime()
	{
		return commentTime;
	}
	public void setCommentTime(Date commentTime)
	{
		this.commentTime = commentTime;
	}
	private Date commentTime;
}
