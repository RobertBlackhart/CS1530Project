package com.cs1530.group4.addendum.shared;

import java.io.Serializable;

public class UserProfile implements Serializable
{
	private static final long serialVersionUID = -5048084475792082373L;
	private String tagline, introduction, braggingRights, highSchool, college, name, gender, birthday, phone, email, address;

	public String getTagline()
	{
		return tagline;
	}

	public void setTagline(String tagline)
	{
		this.tagline = tagline;
	}

	public String getIntroduction()
	{
		return introduction;
	}

	public void setIntroduction(String introduction)
	{
		this.introduction = introduction;
	}

	public String getBraggingRights()
	{
		return braggingRights;
	}

	public void setBraggingRights(String braggingRights)
	{
		this.braggingRights = braggingRights;
	}

	public String getHighSchool()
	{
		return highSchool;
	}

	public void setHighSchool(String highSchool)
	{
		this.highSchool = highSchool;
	}

	public String getCollege()
	{
		return college;
	}

	public void setCollege(String college)
	{
		this.college = college;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getBirthday()
	{
		return birthday;
	}

	public void setBirthday(String birthday)
	{
		this.birthday = birthday;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}
}
