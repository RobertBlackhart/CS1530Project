package com.cs1530.group4.addendummobile;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.shared.Achievement;
import com.shared.Comment;
import com.shared.Post;

import java.util.ArrayList;
import java.util.Date;

public class TrophyAdapter extends ArrayAdapter<Achievement>
{
	Context context;
	ArrayList<Achievement> achievements;

	public TrophyAdapter(Context context, int textViewResourceId, ArrayList<Achievement> achievements)
	{
		super(context, textViewResourceId, achievements);
		this.achievements = achievements;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(R.layout.achievement_row, null);

		TextView name = (TextView) v.findViewById(R.id.name);
		TextView description = (TextView) v.findViewById(R.id.description);

		name.setText(achievements.get(position).getName());
		description.setText(achievements.get(position).getDescriptionText());

		return v;
	}
}