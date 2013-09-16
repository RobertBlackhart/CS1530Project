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
package com.cs1530.group4.addendum.client;

import java.util.ArrayList;

import com.cs1530.group4.addendum.shared.Comment;
import com.cs1530.group4.addendum.shared.Course;
import com.cs1530.group4.addendum.shared.Post;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface UserService extends RemoteService
{
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util
	{
		private static UserServiceAsync instance;

		public static UserServiceAsync getInstance()
		{
			if(instance == null)
			{
				instance = GWT.create(UserService.class);
			}
			return instance;
		}
	}
	
	Boolean doLogin(String username, String password);
	Boolean createUser(String username, String password, String firstName, String lastName);
	ArrayList<Course> courseSearch(String subjectCode, int catalogueNumber, String courseName, String courseDescription);
	void adminAddCourse(String subjectCode, int catalogueNumber, String courseName, String courseDescription);
	void userAddCourse(String username, ArrayList<String> courseIds);
	ArrayList<String> getUserCourses(String username);
	ArrayList<Post> getPosts(int startIndex, ArrayList<String> streamLevels);
	void uploadPost(String username, String postContent, String streamLevel);
	void uploadComment(String postKey, Comment comment);
	void upvotePost(String postKey);
	void downvotePost(String postKey);
}
