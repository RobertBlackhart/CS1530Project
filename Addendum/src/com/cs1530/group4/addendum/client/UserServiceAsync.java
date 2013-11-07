package com.cs1530.group4.addendum.client;

import java.util.ArrayList;
import java.util.Date;

import com.cs1530.group4.addendum.shared.Comment;
import com.cs1530.group4.addendum.shared.Course;
import com.cs1530.group4.addendum.shared.Post;
import com.cs1530.group4.addendum.shared.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Interface UserService describes the RPC methods that the server will implement.
 */
public interface UserServiceAsync
{
	/**
	 * Attempt to login the user with the given credentials.
	 *
	 * @param username the user's username
	 * @param password the user's password
	 * @param callback the callback to return A {@link User} object representing the logged in user or null if there was an error
	 * 
	 * @custom.accessed User
	 * @custom.changed None
	 * @custom.called None
	 */
	void doLogin(String username, String password, AsyncCallback<User> callback);
	
	/**
	 * Create a new user.  If the username or email already exist in the datastore, returns an error.
	 *
	 * @param username the username
	 * @param password the password
	 * @param email the email
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param callback the callback to return the status of the transaction
	 * 
	 * @custom.accessed User
	 * @custom.changed User
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	void createUser(String username, String password, String email, String firstName, String lastName, AsyncCallback<String> callback);
	
	/**
	 * Search for a {@link Course} in the datastore by one or more search criteria.
	 *
	 * @param subjectCode the subject code
	 * @param catalogueNumber the catalogue number
	 * @param courseName the course name
	 * @param courseDescription the course description
	 * @param callback the callback to return the list of {@link Course}s that match the given search terms
	 * 
	 * @custom.accessed Course
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getEntityFromDatastore(String subjectCode, int catalogueNumber)}, {@link com.cs1530.group4.addendum.server.UserServiceImpl#getCourse(Entity entity)}
	 */
	void courseSearch(String subjectCode, int catalogueNumber, String courseName, String courseDescription, AsyncCallback<ArrayList<Course>> callback);
	
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
	void newCourseRequest(String subjectCode, int catalogueNumber, String courseName, String courseDescription, AsyncCallback<Void> callback);
	
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
	void adminAddCourse(String subjectCode, int catalogNumber, String courseName, String courseDescription, AsyncCallback<Void> callback);
	
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
	void userAddCourse(String username, ArrayList<String> courseIds, AsyncCallback<Void> callback);
	
	/**
	 * Gets the user courses.
	 *
	 * @param username the username
	 * @param callback the callback to return the user courses
	 * 
	 * @custom.accessed User
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	void getUserCourses(String username, AsyncCallback<ArrayList<String>> callback);
	
	/**
	 * Gets the posts.
	 *
	 * @param startIndex the start index
	 * @param streamLevels the stream levels
	 * @param requestingUsers the requesting user
	 * @param sort the sort
	 * @param callback the callback to return the posts
	 * 
	 * @custom.accessed Post
	 * @custom.changed None
	 * @custom.called None
	 */
	void getPosts(int startIndex, ArrayList<String> streamLevels, String requestingUsers, String sort, AsyncCallback<ArrayList<Post>> callback);
	
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
	void uploadPost(String username, String postHtml, String postPlain, String streamLevel, Date time, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames, AsyncCallback<Void> callback);
	
	/**
	 * Upload a new comment.
	 *
	 * @param postKey the post key
	 * @param comment the comment
	 * @param callback the callback to return the string
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called None
	 */
	void uploadComment(String postKey, Comment comment, AsyncCallback<String> callback);
	
	/**
	 * Upvote post.
	 *
	 * @param postKey the post key
	 * @param user the user
	 * @param callback the callback to return the boolean
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#changeScore(String postKey, String property, String user)}
	 */
	void upvotePost(String postKey, String user, AsyncCallback<Boolean> callback);
	
	/**
	 * Downvote post.
	 *
	 * @param postKey the post key
	 * @param user the user
	 * @param callback the callback to return the boolean
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#changeScore(String postKey, String property, String user)}
	 */
	void downvotePost(String postKey, String user, AsyncCallback<Boolean> callback);
	
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
	void editPost(String postKey, String postHtml, String postPlain, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames, AsyncCallback<Void> callback);
	
	/**
	 * Edits the comment.
	 *
	 * @param commentKey the comment key
	 * @param commentText the comment text
	 * @param callback the callback to return the string
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getComment(String commentKey)}
	 */
	void editComment(String commentKey, String commentText, AsyncCallback<String> callback);
	
	/**
	 * Delete post.
	 *
	 * @param postKey the post key
	 * 
	 * @custom.accessed None
	 * @custom.changed Post
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getPost(String postKey)}
	 */
	void deletePost(String postKey, AsyncCallback<Void> callback);
	
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
	void flagPost(String postKey, String reason, boolean setFlagged, AsyncCallback<Void> callback);
	
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
	void flagComment(String commentKey, String reason, boolean setFlagged, AsyncCallback<Void> callback);
	
	/**
	 * Delete comment.
	 *
	 * @param commentKey the comment key
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getComment(String commentKey)}
	 */
	void deleteComment(String commentKey, AsyncCallback<Void> callback);
	
	/**
	 * Post search.
	 *
	 * @param startIndex the start index
	 * @param searchText the search text
	 * @param requestingUser the requesting user
	 * @param callback the callback to return the array list
	 * 
	 * @custom.accessed Post
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#postFromEntity(Entity entity, String requestingUser)}
	 */
	void postSearch(int startIndex, String searchText, String requestingUser, AsyncCallback<ArrayList<Post>> callback);
	
	/**
	 * Gets the course requests.
	 *
	 * @param callback the callback to return the course requests
	 * 
	 * @custom.accessed CourseRequest
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getCourse(Entity entity)}
	 */
	void getCourseRequests(AsyncCallback<ArrayList<Course>> callback);
	
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
	void removeCourseRequest(Course course, boolean add, AsyncCallback<Void> callback);
	
	/**
	 * Gets the flagged posts.
	 *
	 * @param callback the callback to return the flagged posts
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#postFromEntity(Entity entity, String requestingUser)}
	 */
	void getFlaggedPosts(AsyncCallback<ArrayList<Post>> callback);
	
	/**
	 * Sends a password reset email.
	 *
	 * @param username the username
	 * @param callback the callback to return the boolean
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	void resetPassword(String username, AsyncCallback<Boolean> callback);
	
	/**
	 * Change password.
	 *
	 * @param username the username
	 * @param newPassword the new password
	 * @param callback the callback to return the user
	 * 
	 * @custom.accessed None
	 * @custom.changed User
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	void changePassword(String username, String newPassword, AsyncCallback<User> callback);
	
	/**
	 * Plus one.
	 *
	 * @param commentKey the comment key
	 * @param requestingUser the requesting user
	 * @param callback the callback to return the boolean
	 * 
	 * @custom.accessed None
	 * @custom.changed Comment
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getComment(String commentKey)}
	 */
	void plusOne(String commentKey, String requestingUser, AsyncCallback<Boolean> callback);
	
	/**
	 * Removes the course.
	 *
	 * @param course the course
	 * @param user the user
	 * @param callback the callback to return the user
	 * 
	 * @custom.accessed None
	 * @custom.changed User
	 * @custom.called {@link com.cs1530.group4.addendum.server.UserServiceImpl#getUserEntity(String username)}
	 */
	void removeCourse(String course, String user, AsyncCallback<User> callback);
	
	/**
	 * Gets the upload url.
	 *
	 * @param username Optional argument used to specify the user this upload will be associated with (i.e. in the case of a profile image).  Can be null
	 * @param callback the callback to return the upload url
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.google.appengine.api.blobstore.BlobstoreService#createUploadUrl(String)}
	 */
	void getUploadUrl(String username, AsyncCallback<String> callback);
	
	/**
	 * Delete attachment.
	 *
	 * @param key the key
	 * 
	 * @custom.accessed None
	 * @custom.changed None
	 * @custom.called {@link com.google.appengine.api.blobstore.BlobstoreService#delete(com.google.appengine.api.blobstore.BlobKey...)}
	 */
	void deleteAttachment(String key, AsyncCallback<Void> callback);
}
