package com.terracotta.followmateapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.terracotta.followmateapp.model.NotificationModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.lazyLoader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspl31 on 6/9/16.
 */
public class NotificationAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<NotificationModel.Sprint_data> listSearchresult = null;
    SessionManager mSessionManager;
    Typeface roboto;
    public NotificationInteractionListner notificationInteractionListner;
    ImageLoader imageLoader;

    public NotificationAdapter(Context context, ArrayList<NotificationModel.Sprint_data> listSearchresult) {
        mContext = context;
        this.listSearchresult = listSearchresult;
        inflater = LayoutInflater.from(mContext);
        mSessionManager = new SessionManager(mContext);
        imageLoader = new ImageLoader(mContext);
    }

    public class ViewHolder {
        TextView textview_message, textview_accept, textview_decline;
        ImageView imageview_profile;
        LinearLayout layout_bottom;
    }

    @Override
    public int getCount() {
        return listSearchresult.size();
    }

    @Override
    public NotificationModel.Sprint_data getItem(int position) {
        return listSearchresult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        //  if (view == null) {
        Log.e("arrayList_.size", "arrayList_.size---" + this.listSearchresult.size());

        holder = new ViewHolder();
        view = inflater.inflate(R.layout.row_notification, null);


        holder.textview_message = (TextView) view
                .findViewById(R.id.textview_message);
        holder.textview_accept = (TextView) view
                .findViewById(R.id.textview_accept);
        holder.textview_decline = (TextView) view
                .findViewById(R.id.textview_decline);
        holder.imageview_profile = (ImageView) view
                .findViewById(R.id.imageview_profile);
        holder.layout_bottom = (LinearLayout) view
                .findViewById(R.id.layout_bottom);




        if ((listSearchresult.get(i).getSprintStages().equalsIgnoreCase("1") || listSearchresult.get(i).getSprintStages().equalsIgnoreCase("4")) && listSearchresult.get(i).getStatus().equalsIgnoreCase("0")) {
            holder.layout_bottom.setVisibility(View.VISIBLE);
        } else {
            holder.layout_bottom.setVisibility(View.GONE);
        }

        if (!listSearchresult.get(i).getProfileImage().equals(" ")) {
            System.out.println("set---" + listSearchresult.get(i).getProfileImage());
            imageLoader.DisplayImage(listSearchresult.get(i).getProfileImage(), holder.imageview_profile);
        } else {
            System.out.println("set---" + listSearchresult.get(i).getProfileImage());
        }

        notificationInteractionListner = (NotificationInteractionListner) mContext;

        holder.textview_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.textview_decline.setOnClickListener(null);
                mSessionManager.putStringData(Constants.SPRINT_ID_NOTI, listSearchresult.get(i).getSprintId());
                mSessionManager.putStringData(Constants.MAIN_USER_ID, listSearchresult.get(i).getMainUserId());
                notificationInteractionListner.OnAcceptClick(i);
                holder.textview_decline.setOnClickListener(this);
                // callAcceptUpdateStatusService();
            }
        });

        holder.textview_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.textview_accept.setOnClickListener(null);
                mSessionManager.putStringData(Constants.SPRINT_ID_NOTI, listSearchresult.get(i).getSprintId());
                mSessionManager.putStringData(Constants.MAIN_USER_ID, listSearchresult.get(i).getMainUserId());
                notificationInteractionListner.OnDeclineClick(i);
                holder.textview_accept.setOnClickListener(this);
                //callDeclineUpdateStatusService();
            }
        });

        Log.e(" listSearchresult.get(i).getNotificationText()"," listSearchresult.get(i).getNotificationText()"+ listSearchresult.get(i).getNotificationText());

    /*    if (listSearchresult.get(i).getNotificationText().contains("@")) {
            String str = listSearchresult.get(i).getNotificationText().substring(0, listSearchresult.get(i).getNotificationText().indexOf("@"));
            holder.textview_message.setText(Html.fromHtml(str));
        } else if (listSearchresult.get(i).getNotificationText().contains("$")) {
            String str = listSearchresult.get(i).getNotificationText().substring(0, listSearchresult.get(i).getNotificationText().indexOf("$"));
            holder.textview_message.setText(Html.fromHtml(str));
        } else if (listSearchresult.get(i).getNotificationText().contains("(")) {
            String str = listSearchresult.get(i).getNotificationText().substring(0, listSearchresult.get(i).getNotificationText().indexOf("("));
            holder.textview_message.setText(Html.fromHtml(str));
        } else if (listSearchresult.get(i).getNotificationText().contains("%")) {
            String str = listSearchresult.get(i).getNotificationText().substring(0, listSearchresult.get(i).getNotificationText().indexOf("%"));
            holder.textview_message.setText(Html.fromHtml(str));
        } else if (listSearchresult.get(i).getNotificationText().contains("!")) {
            String str = listSearchresult.get(i).getNotificationText().substring(0, listSearchresult.get(i).getNotificationText().indexOf("!"));
            holder.textview_message.setText(Html.fromHtml(str));
        }else if (listSearchresult.get(i).getNotificationText().contains("#")) {
            String str = listSearchresult.get(i).getNotificationText().substring(0, listSearchresult.get(i).getNotificationText().indexOf("#"));
            holder.textview_message.setText(Html.fromHtml(str));
        } else if (listSearchresult.get(i).getNotificationText().contains(")")) {
            String str = listSearchresult.get(i).getNotificationText().substring(0, listSearchresult.get(i).getNotificationText().indexOf(")"));
            holder.textview_message.setText(Html.fromHtml(str));
        }
*/
        holder.textview_message.setText(Html.fromHtml(listSearchresult.get(i).getNotificationText()));


        //font
        roboto = Typeface
                .createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        holder.textview_message.setTypeface(roboto);
        holder.textview_accept.setTypeface(roboto);
        holder.textview_decline.setTypeface(roboto);

        return view;
    }

    public interface NotificationInteractionListner {
        public void OnAcceptClick(int i);

        public void OnDeclineClick(int i);
    }

}
