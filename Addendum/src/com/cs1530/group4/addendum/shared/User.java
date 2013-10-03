package com.cs1530.group4.addendum.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{
	private static final long serialVersionUID = 3724822216046123105L;
	private String username;
	private ArrayList<String> courseList = new ArrayList<String>();
	
	public User(){}
	public User(String u)
	{
		setUsername(u);
	}
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public ArrayList<String> getCourseList()
	{
		return courseList;
	}
	public void setCourseList(ArrayList<String> courseList)
	{
		this.courseList = courseList;
	}
	public String serialize()
	{
		String serialString = username+"+";
		for(String course : courseList)
		{
			serialString += course+"-";
		}
		
		return serialString;
	}
	public static User deserialize(String serialString)
	{
		User user = new User();
		String[] username = serialString.split("\\+");
		user.setUsername(username[0]);
		if(username.length > 1)
		{
			String[] courses = username[1].split("-");
			ArrayList<String> courseList = new ArrayList<String>();
			for(String course : courses)
			{
				courseList.add(course);
			}
			user.setCourseList(courseList);
		}
		return user;
	}
}
