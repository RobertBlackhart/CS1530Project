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
package com.cs1530.group4.classweb.server;


import com.cs1530.group4.classweb.client.UserService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements UserService
{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	
	@Override
	public Boolean doLogin(String username, String password)
	{
		Entity user = null;
		if(memcache.contains("user_"+username))
			user = ((Entity)memcache.get("user_"+username));
		else
		{
			try
			{
				user = datastore.get(KeyFactory.createKey("User", username));
			}
			catch(EntityNotFoundException e1)
			{
				return false;
			}
		}
		
		if(user != null)
		{
			if(user.hasProperty("password"))
			{
				if(user.getProperty("password").toString().equals(password))
					return true;
				else
					return false;
			}
		}
		else
			return false;
		
		return false;
	}
	
	@Override
	public Boolean createUser(String username, String password, String firstName, String lastName)
	{
		Entity user = null;
		if(memcache.contains("user_"+username))
			user = ((Entity)memcache.get("user_"+username));
		else
		{
			try
			{
				user = datastore.get(KeyFactory.createKey("User", username));
			}
			catch(EntityNotFoundException e1){user = null;}
		}
		
		if(user != null)
			return false;
		else
		{
			user = new Entity("User",username);
			user.setProperty("username", username);
			user.setProperty("password", password);
			user.setProperty("firstName", firstName);
			user.setProperty("lastName", lastName);
			memcache.put("user_"+username, user);
			datastore.put(user);
			return true;
		}
	}
}
