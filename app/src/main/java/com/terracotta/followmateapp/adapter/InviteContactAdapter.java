package com.terracotta.followmateapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.terracotta.followmateapp.model.InviteContactModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.lazyLoader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspl31 on 9/9/16.
 */
public class InviteContactAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<InviteContactModel.ContactData> listSearchresult = null;
    SessionManager mSessionManager;
    Typeface roboto;
    // public NotificationInteractionListner notificationInteractionListner;
    InviteContactInteractionListner inviteContactInteractionListner;
    ImageLoader imageLoader;

    public InviteContactAdapter(Context context, ArrayList<InviteContactModel.ContactData> listSearchresult) {
        mContext = context;
        this.listSearchresult = listSearchresult;
        inflater = LayoutInflater.from(mContext);
        mSessionManager = new SessionManager(mContext);
        inviteContactInteractionListner = (InviteContactInteractionListner)context;
        imageLoader = new ImageLoader(mContext);
    }

    public class ViewHolder {
        TextView textview_name, textview_number;
        Button button_invite;
        ImageView imageview_status;
        ImageView profileimage;
    }


    @Override
    public int getCount() {
        return listSearchresult.size();
    }

    @Override
    public InviteContactModel.ContactData getItem(int i) {
        return listSearchresult.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        //  if (view == null) {


        holder = new ViewHolder();
        view = inflater.inflate(R.layout.row_invitecontact_list, null);



        holder.textview_name = (TextView) view
                .findViewById(R.id.textview_name);
        holder.textview_number = (TextView) view
                .findViewById(R.id.textview_number);
        holder.button_invite = (Button) view
                .findViewById(R.id.button_invite);
        holder.profileimage = (ImageView) view
                .findViewById(R.id.imageview_profile);
        holder.imageview_status = (ImageView) view
                .findViewById(R.id.imageview_status);

        holder.textview_name.setText(Html.fromHtml(listSearchresult.get(i).getUserName()));
        holder.textview_number.setText(Html.fromHtml(listSearchresult.get(i).getUserMbl()));
        if (listSearchresult.get(i).getFlag().equals("n")) {
            holder.button_invite.setVisibility(View.VISIBLE);
        }else if(listSearchresult.get(i).getFlag().equals("y")){
            holder.imageview_status.setVisibility(View.VISIBLE);
        }

        if(!listSearchresult.get(i).getProfileImage().trim().equals("")){
            //imageLoader.DisplayImage(listSearchresult.get(i).getProfileImage(),holder.profileimage);

            if(listSearchresult.get(i).getFlag().equals("n")) {
                Picasso.with(mContext).load(R.mipmap.default_pic).into(holder.profileimage);
            }else {
                Picasso.with(mContext).load(listSearchresult.get(i).getProfileImage()).placeholder(R.mipmap.default_pic).into(holder.profileimage);
            }

            Log.e("image URL -> ", listSearchresult.get(i).getProfileImage());
        }



        holder.button_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteContactInteractionListner.OnInvite(listSearchresult.get(i).getUserMbl());
            }
        });

        roboto = Typeface
                .createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        holder.textview_name.setTypeface(roboto);
        holder.textview_number.setTypeface(roboto);
        holder.button_invite.setTypeface(roboto);

        return view;
    }

    public interface InviteContactInteractionListner{
        public void OnInvite(String number);
    }
}
