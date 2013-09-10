package com.cs1530.group4.classweb.shared;

import java.io.Serializable;

public class Course implements Serializable
{
	private static final long serialVersionUID = -6434443948276427528L;
	private String subjectCode, courseName, courseDescription;
	private int courseNumber;
	
	public Course(){}
	public Course(String code, int num, String name, String desc)
	{
		subjectCode = code;
		courseNumber = num;
		courseName = name;
		courseDescription = desc;
	}

	public String getSubjectCode()
	{
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode)
	{
		this.subjectCode = subjectCode;
	}

	public String getCourseName()
	{
		return courseName;
	}

	public void setCourseName(String courseName)
	{
		this.courseName = courseName;
	}

	public String getCourseDescription()
	{
		return courseDescription;
	}

	public void setCourseDescription(String courseDescription)
	{
		this.courseDescription = courseDescription;
	}

	public int getCourseNumber()
	{
		return courseNumber;
	}

	public void setCourseNumber(int courseNumber)
	{
		this.courseNumber = courseNumber;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Course)
		{
			Course other = (Course)o;
			if(other.courseNumber == courseNumber)
				return true;
			else
				return false;
		}
		return false;
	}
}
