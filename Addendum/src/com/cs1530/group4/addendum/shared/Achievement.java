package com.cs1530.group4.addendum.shared;

import java.io.Serializable;

public class Achievement implements Serializable
{
	private static final long serialVersionUID = -8457244121843347327L;
	private String name, descriptionText;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescriptionText()
	{
		return descriptionText;
	}

	public void setDescriptionText(String descriptionText)
	{
		this.descriptionText = descriptionText;
	}
}
