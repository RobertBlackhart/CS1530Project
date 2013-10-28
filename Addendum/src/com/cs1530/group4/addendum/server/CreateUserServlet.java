package com.cs1530.group4.addendum.server;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class CreateUserServlet extends HttpServlet
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		performActions(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		performActions(req, resp);
	}
	
	private void performActions(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String username = req.getParameter("username");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String firstName = req.getParameter("firstName");
		String lastName = req.getParameter("lastName");
		
		String response = "success";
		Entity user = getUserEntity(username);

		if(user != null)
			response = "user_exists";
		if(!validateEmail(email))
			response = "email_exists";

		else
		{
			try
			{
				String uuid = UUID.randomUUID().toString();
				user = new Entity("User", username);
				user.setProperty("username", username);
				user.setProperty("password", password);
				user.setProperty("email", email);
				user.setProperty("emailValid", false);
				user.setProperty("uuid", uuid);
				user.setProperty("firstName", firstName);
				user.setProperty("lastName", lastName);

				sendEmail(email, uuid, username);

				memcache.put("user_" + username, user);
				datastore.put(user);
				response = "success";
			}
			catch(Exception ex)
			{
				response = "error";
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		resp.getWriter().print(response);
	}
	
	private boolean validateEmail(String email)
	{
		Query q = new Query("User");
		q.setFilter(new FilterPredicate("email", FilterOperator.EQUAL, email));
		for(Entity entity : datastore.prepare(q).asIterable())
			return false;

		return true;
	}
	
	private void sendEmail(String email, String uuid, String username)
	{
		String msgBody = "Welcome to Addendum!\n\n" + "In order to validate your account, please click on the link below or copy and paste it into" + "your browser's address bar:\n\n" + "http://studentclassnet.appspot.com/addendum/validate?username=" + username + "&uuid=" + uuid;

		try
		{
			Message msg = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
			msg.setFrom(new InternetAddress("addendumapp@gmail.com", "Addendum"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			msg.setSubject("Welcome to Addendum");
			msg.setText(msgBody);
			Transport.send(msg);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Entity getUserEntity(String username)
	{
		Entity user = null;
		if(memcache.contains("user_" + username))
			user = ((Entity) memcache.get("user_" + username));
		else
		{
			try
			{
				user = datastore.get(KeyFactory.createKey("User", username));
			}
			catch(EntityNotFoundException ex)
			{}
		}
		return user;
	}
}