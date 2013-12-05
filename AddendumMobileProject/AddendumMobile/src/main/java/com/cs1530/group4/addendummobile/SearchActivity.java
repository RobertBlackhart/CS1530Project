package com.cs1530.group4.addendummobile;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.Post;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener
{
	long lastSearch;
	SupportMenuItem refresh;
	SearchActivity context = this;
	ListView postList;
	String searchString;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		postList = (ListView) findViewById(R.id.postList);

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);

		MenuItem searchItem = menu.findItem(R.id.search);
		refresh = (SupportMenuItem) menu.findItem(R.id.refresh);

		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		if (searchView != null)
		{
			searchView.setIconified(false);
			searchView.setQueryHint("Search for posts");
			searchView.setOnQueryTextListener(this);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_settings)
		{
			return true;
		}
		if(id == R.id.refresh)
		{
			if(searchString != null)
				new PostSearchTask(context,postList,refresh,0).execute(searchString);
			return true;
		}
		if(id == android.R.id.home)
		{
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onQueryTextSubmit(String s)
	{
		if(System.currentTimeMillis()-lastSearch < 2000) //workaround for event being fired on both ACTION_DOWN and ACTION_UP
			return false;

		lastSearch = System.currentTimeMillis();
		searchString = s;
		new PostSearchTask(context,postList,refresh,0).execute(s);

		return true;
	}

	@Override
	public boolean onQueryTextChange(String s)
	{
		return false;
	}
}
