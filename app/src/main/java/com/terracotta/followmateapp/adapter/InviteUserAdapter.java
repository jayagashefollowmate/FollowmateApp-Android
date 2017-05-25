package com.terracotta.followmateapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.pojo.SingleUser;

import org.json.JSONArray;

import java.util.ArrayList;


public class InviteUserAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	private ArrayList<SingleUser> buddyList;
	public static JSONArray selectedUsers;

	public InviteUserAdapter(Context con, ArrayList<SingleUser> userlist) {
		inflator = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		buddyList = userlist;
		selectedUsers = new JSONArray();
		//mHighlightedPositions = new boolean[getCount()];
	}

	@Override
	public int getCount() {
		return buddyList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return buddyList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	private static class ViewHolder {
		TextView userName, statusMessage;
		ImageView status;
		CheckBox checkBox;
	}

	@Override
	public View getView(final int position, View vi, ViewGroup parent) {

		ViewHolder holder;

		if (vi == null) {
			holder = new ViewHolder();
			vi = inflator.inflate(R.layout.custom_invite_user_item, null);
			holder.userName = (TextView) vi.findViewById(R.id.textViewUserName);
			holder.statusMessage = (TextView) vi.findViewById(R.id.textViewStatusMessage);
			holder.status = (ImageView) vi.findViewById(R.id.imageViewStatusIcon);
			holder.checkBox = (CheckBox) vi.findViewById(R.id.checkBox);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

//        if(mHighlightedPositions[position]) {
//            mIvFav.setBackgroundResource(R.mipmap.fav);
//        }else {
//            mIvFav.setBackgroundResource(R.mipmap.unfav);
//        }
//
//        mIvFav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mHighlightedPositions[position]) {
//                    // clicked for second time and marked as un fav
//                    mHighlightedPositions[position] = false;
//                }else {
//                    // clicked for first time and marked as fav
//                    mHighlightedPositions[position] = true;
//                    mEditor.putBoolean(Constants.FAV_SUB_INDEX + position, Constants.arrFavSubjectPos.add(position));
//                    mEditor.putBoolean(Constants.FAV_SUB_NAME + position, Constants.arrFavSubjectName.add(mSubjectList.get(position)));
//                    mEditor.commit();
//                }
//                mActivity.refreshList(mSubjectList.get(position));
//            }
//        });

		final SingleUser user = buddyList.get(position);
		holder.checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedUsers.put(user.getId());
				Log.e("Selected User  ", user.getId() + " Name : " + user.getName());
			}
		});


		if (user != null) {
			holder.userName.setText(user.getName());
			holder.statusMessage.setText(user.getStatusMessage());
			String status = user.getStatus().trim();
			switch (status) {
				case "available":
					holder.status.setImageResource(R.drawable.ic_user_available);
					break;
				case "away":
					holder.status.setImageResource(R.drawable.ic_user_away);
					break;
				case "busy":
					holder.status.setImageResource(R.drawable.ic_user_busy);
					break;
				case "offline":
					holder.status.setImageResource(R.drawable.ic_user_offline);
					break;
				case "invisible":
					holder.status.setImageResource(R.drawable.ic_user_offline);
					break;
				default:
					holder.status.setImageResource(R.drawable.ic_user_available);
					break;
			}
		}
		return vi;
	}

}