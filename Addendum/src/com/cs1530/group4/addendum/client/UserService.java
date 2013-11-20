package com.cs1530.group4.addendum.client;

import java.util.ArrayList;
import java.util.Date;

import com.cs1530.group4.addendum.shared.Achievement;
import com.cs1530.group4.addendum.shared.Comment;
import com.cs1530.group4.addendum.shared.Course;
import com.cs1530.group4.addendum.shared.Post;
import com.cs1530.group4.addendum.shared.User;
import com.cs1530.group4.addendum.shared.UserProfile;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface UserService describes the RPC methods that the server will implement.
 */
@RemoteServiceRelativePath("login")
public interface UserService extends RemoteService
{
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util
	{
		
		/** Static instance of UserServiceAsync. */
		private static UserServiceAsync instance;

		/**
		 * Gets the single instance of UserService.
		 *
		 * @return static instance of UserService
		 */
		public static UserServiceAsync getInstance()
		{
			if(instance == null)
			{
				instance = GWT.create(UserService.class);
			}
			return instance;
		}
	}
	
	/**
	 * Attempt to login the user with the given credentials.
	 *
	 * @param username the user's username
	 * @param password the user's password
	 * @return A {@link User} object representing the logged in user or null if there was an error
	 * 
	 * @custom.accessed User
	 * @custom.changed None
	 * @custom.called None
	 */
	User doLogin(String username, String password);
	
	/**
	 * Create a new user.  If the username or email already exist in the datastore, returns an error.
	 *
	 * @param username the username
	 * @param password the password
	 * @param email the email
	 * @param firstName the first name
	 * @param lastName the last name
	 * @return the status of the transaction
	 * 
	 * @custom.accessed User
	 * @custom.changed User
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	String createUser(String username, String password, String email, String firstName, String lastName);
	
	/**
	 * Search for a {@link Course} in the datastore by one or more search criteria.
	 *
	 * @param subjectCode the subject code
	 * @param catalogueNumber the catalogue number
	 * @param courseName the course name
	 * @param courseDescription the course description
	 * @return the list of {@link Course}s that match the given search terms
	 * 
	 * @custom.accessed Course
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getEntityFromDatastore(String subjectCode, int catalogueNumber)}, {@link com.cs1530.group4.addendum.server.UserServiceImpl#getCourse(Entity entity)}
	 */
	ArrayList<Course> courseSearch(String subjectCode, int catalogueNumber, String courseName, String courseDescription);
	
	/**
	 * A request to have a new {@link Course} added to the database.
	 *
	 * @param subjectCode the subject code
	 * @param catalogueNumber the catalogue number
	 * @param courseName the course name
	 * @param courseDescription the course description
	 * 
	 * @custom.accessed None
	 * @custom.changed CourseRequest
	 * @custom.called None
	 */
	void newCourseRequest(String subjectCode, int catalogueNumber, String courseName, String courseDescription);
	
	/**
	 * Admin confirm that the course request should be added to the database.
	 *
	 * @param subjectCode the subject code
	 * @param catalogNumber the catalog number
	 * @param courseName the course name
	 * @param courseDescription the course description
	 * 
	 * @custom.accessed Course
	 * @custom.changed None
	 * @custom.called None
	 */
	void adminAddCourse(String subjectCode, int catalogNumber, String courseName, String courseDescription);
	
	/**
	 * Makes a {@link User} a member of a new course
	 *
	 * @param username the username
	 * @param courseIds the course ids
	 * 
	 * @custom.accessed User
	 * @custom.changed User
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	void userAddCourse(String username, ArrayList<String> courseIds);
	
	/**
	 * Gets the user courses.
	 *
	 * @param username the username
	 * @return the user courses
	 * 
	 * @custom.accessed User
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	ArrayList<String> getUserCourses(String username);
	
	/**
	 * Gets the posts.
	 *
	 * @param startIndex the start index
	 * @param streamLevels the stream levels
	 * @param requestingUsers the requesting user
	 * @param sort the sort
	 * @return the posts
	 * 
	 * @custom.accessed Post
	 * @custom.changed None
	 * @custom.called None
	 */
	ArrayList<Post> getPosts(int startIndex, ArrayList<String> streamLevels, String requestingUsers, String sort);
	
	/**
	 * Upload a new post.
	 *
	 * @param username the username
	 * @param postHtml the post html
	 * @param postPlain the post plain
	 * @param streamLevel the stream level
	 * @param time the time
	 * @param attachmentKeys the attachment keys
	 * @param attachmentNames the attachment names
	 * 
	 * @custom.accessed None
	 * @custom.changed Post
	 * @custom.called None
	 */
	void uploadPost(String username, String postHtml, String postPlain, String streamLevel, Date time, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames);
	
	/**
	 * Upload a new comment.
	 *
	 * @param postKey the post key
	 * @param comment the comment
	 * @return the string
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called None
	 */
	String uploadComment(String postKey, Comment comment, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames);
	
	/**
	 * Upvote post.
	 *
	 * @param postKey the post key
	 * @param user the user
	 * @return the boolean
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#changeScore(String postKey, String property, String user)}
	 */
	Boolean upvotePost(String postKey, String user);
	
	/**
	 * Downvote post.
	 *
	 * @param postKey the post key
	 * @param user the user
	 * @return the boolean
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#changeScore(String postKey, String property, String user)}
	 */
	Boolean downvotePost(String postKey, String user);
	
	/**
	 * Edits the post.
	 *
	 * @param postKey the post key
	 * @param postHtml the post html
	 * @param postPlain the post plain
	 * @param attachmentKeys the attachment keys
	 * @param attachmentNames the attachment names
	 * 
	 * @custom.accessed None
	 * @custom.changed Post
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getPost(String postKey)}
	 */
	void editPost(String postKey, String postHtml, String postPlain, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames);
	
	/**
	 * Edits the comment.
	 *
	 * @param commentKey the comment key
	 * @param commentText the comment text
	 * @return the string
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getComment(String commentKey)}
	 */
	String editComment(String commentKey, String commentText, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames);
	
	/**
	 * Delete post.
	 *
	 * @param postKey the post key
	 * 
	 * @custom.accessed None
	 * @custom.changed Post
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getPost(String postKey)}
	 */
	void deletePost(String postKey);
	
	/**
	 * Flag post.
	 *
	 * @param postKey the post key
	 * @param reason the reason
	 * @param setFlagged the set flagged
	 * 
	 * @custom.accessed None
	 * @custom.changed Post
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getPost(String postKey)}
	 */
	void flagPost(String postKey, String reason, boolean setFlagged);
	
	/**
	 * Flag comment.
	 *
	 * @param commentKey the comment key
	 * @param reason the reason
	 * @param setFlagged the set flagged
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getComment(String commentKey)}
	 */
	void flagComment(String commentKey, String reason, boolean setFlagged);
	
	/**
	 * Delete comment.
	 *
	 * @param commentKey the comment key
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getComment(String commentKey)}
	 */
	void deleteComment(String commentKey);
	
	/**
	 * Post search.
	 *
	 * @param startIndex the start index
	 * @param searchText the search text
	 * @param requestingUser the requesting user
	 * @return the array list
	 * 
	 * @custom.accessed Post
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#postFromEntity(Entity entity, String requestingUser)}
	 */
	ArrayList<Post> postSearch(int startIndex, String searchText, String requestingUser);
	
	/**
	 * Gets the course requests.
	 *
	 * @return the course requests
	 * 
	 * @custom.accessed CourseRequest
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getCourse(Entity entity)}
	 */
	ArrayList<Course> getCourseRequests();
	
	/**
	 * Removes the course request.
	 *
	 * @param course the course
	 * @param add the add
	 * 
	 * @custom.accessed None
	 * @custom.changed CourseRequest
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#adminAddCourse(String, int, String, String)}
	 */
	void removeCourseRequest(Course course, boolean add);
	
	/**
	 * Gets the flagged posts.
	 *
	 * @return the flagged posts
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#postFromEntity(Entity entity, String requestingUser)}
	 */
	ArrayList<Post> getFlaggedPosts();
	
	/**
	 * Sends a password reset email.
	 *
	 * @param username the username
	 * @return the boolean
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	Boolean resetPassword(String username);
	
	/**
	 * Change password.
	 *
	 * @param username the username
	 * @param newPassword the new password
	 * @return the user
	 * 
	 * @custom.accessed None
	 * @custom.changed User
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	User changePassword(String username, String newPassword);
	
	/**
	 * Plus one.
	 *
	 * @param commentKey the comment key
	 * @param requestingUser the requesting user
	 * @return the boolean
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getComment(String commentKey)}
	 */
	Boolean plusOne(String commentKey, String requestingUser);
	
	/**
	 * Removes the course.
	 *
	 * @param course the course
	 * @param user the user
	 * @return the user
	 * 
	 * @custom.accessed None
	 * @custom.changed User
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	User removeCourse(String course, String user);
	
	/**
	 * Gets the upload url.
	 *
	 * @param username Optional argument used to specify the user this upload will be associated with (i.e. in the case of a profile image).  Can be null
	 * @return the upload url
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.google.appengine.api.blobstore.BlobstoreService#createUploadUrl(String)}
	 */
	String getUploadUrl(String username);
	
	/**
	 * Delete attachment.
	 *
	 * @param key the key
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.google.appengine.api.blobstore.BlobstoreService#delete(com.google.appengine.api.blobstore.BlobKey...)}
	 */
	void deleteAttachment(String key);
	
	/**
	 * Get the basic information that a user has shared.
	 *
	 * @param username the username of the user for which to retrieve information
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	UserProfile getUserProfile(String username);
	
	/**
	 * Set the basic information that a user has shared.
	 *
	 * @param username the username of the user for which to save profile information
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	void setUserProfile(String username, UserProfile userProfile);
	
	/**
	 * Get a list of the user's achievements (trophies)
	 *
	 * @param username the username of the user for which to retrieve information
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	ArrayList<Achievement> getAchievements(String username);
	
	/**
	 * Get a list of the user's unearned achievements (trophies)
	 *
	 * @param username the username of the user for which to retrieve information
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	ArrayList<Achievement> getUnearnedAchievements(String username);
	
	/**
	 * Mark a comment in the database as accepted/unaccepted.
	 *
	 * @param comment the comment to mark as accepted/unaccepted
	 * @param accepted the state of acceptance to mark for this comment
	 * @param accepter the username of the person doing the accepting/unaccepting
	 * @param associatedPostKey the keystring of the post that this comment belongs to
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called None
	 */
	void acceptComment(Comment comment, String accepter, boolean accepted, String associatedPostKey);
}
