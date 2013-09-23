package com.cs1530.group4.addendum.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MenuPopup extends PopupPanel
{
	public MenuPopup(Widget relativeWidget)
	{
		Label editPost = new Label("Edit Post");
		Label deletePost = new Label("Delete Post");
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(5);
		vPanel.add(editPost);
		vPanel.add(deletePost);
		add(vPanel);
		showRelativeTo(relativeWidget);
	}
}
