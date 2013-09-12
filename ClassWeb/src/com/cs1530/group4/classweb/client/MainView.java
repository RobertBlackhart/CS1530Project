/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.cs1530.group4.classweb.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MainView implements EntryPoint
{
	private VerticalPanel contentPanel;

	public void onModuleLoad()
	{
		RootPanel rootPanel = RootPanel.get("container");
		
		VerticalPanel topPanel = new VerticalPanel();
		contentPanel = new VerticalPanel();
		
		rootPanel.add(topPanel);
		rootPanel.add(contentPanel);
		contentPanel.setSize("", "");
		
		Label welcomeLabel = new Label("Welcome to ClassWeb");
		welcomeLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		welcomeLabel.getElement().getStyle().setProperty("marginBottom","20px");
		welcomeLabel.setStyleName("gwt-Label-Login");
		topPanel.add(welcomeLabel);

		if(Cookies.getCookie("loggedIn") != null)
		{
			contentPanel.add(new Profile(this, Cookies.getCookie("loggedIn")));
		}
		else
			contentPanel.add(new Login(this));
	}
	
	public void setContent(Widget content)
	{
		contentPanel.clear();
		contentPanel.add(content);
	}
}
