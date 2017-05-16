package com.terracotta.followmateapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.terracotta.followmateapp.model.ContactListOtherModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.lazyLoader.ImageLoader;

import java.util.ArrayList;
import java.util.List;


//Developement team:               Mobility Team
//Creation Date:                   9 Aug 2016
//Information                      shows contact list
// Comment:


public class ContactListOtherAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<ContactListOtherModel> listSearchresult = null;
    String WhichActivity;

    //font
    Typeface roboto;
    ImageLoader imageLoader;


    public ContactListOtherAdapter(Context context, ArrayList<ContactListOtherModel> listSearchresult, String WhichActivity) {
        mContext = context;
        this.listSearchresult = listSearchresult;
        inflater = LayoutInflater.from(mContext);
        this.WhichActivity = WhichActivity;
        imageLoader = new ImageLoader(mContext);


    }

    public class ViewHolder {
        TextView txtView_contact_name, txtView_contact_phno;
        CheckBox checkBox_contact;
        ImageView imgview_contact_status,imageview_profile;

    }

    @Override
    public int getCount() {
        return listSearchresult.size();
    }

    @Override
    public ContactListOtherModel getItem(int position) {
        return listSearchresult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        //  if (view == null) {
        Log.e("arrayList_.size", "arrayList_.size---" + this.listSearchresult.size());
        holder = new ViewHolder();

        if (WhichActivity.equals("With")) {
            view = inflater.inflate(R.layout.row_contact_list_other, null);
        } else if (WhichActivity.equals("Without")) {
            view = inflater.inflate(R.layout.row_contact_listwithout_check, null);
        }

        holder.txtView_contact_name = (TextView) view
                .findViewById(R.id.txtView_contact_name);
        holder.txtView_contact_phno = (TextView) view
                .findViewById(R.id.txtView_contact_phno);
        holder.imageview_profile = (ImageView) view
                .findViewById(R.id.imageview_profile);

        if (!listSearchresult.get(position).getProfileImage().equals(" ")) {
            System.out.println("set---" + listSearchresult.get(position).getProfileImage());
            imageLoader.DisplayImage(listSearchresult.get(position).getProfileImage(), holder.imageview_profile);
        }

        //font
        roboto = Typeface
                .createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        holder.txtView_contact_name.setTypeface(roboto);
        holder.txtView_contact_phno.setTypeface(roboto);


        if (WhichActivity.equals("With")) {


        } else {







            Log.e("user_status", " user_name in adapter " + listSearchresult.get(position).getName() + " user_status in adapter" + listSearchresult.get(position).getstatus());
            holder.imgview_contact_status = (ImageView) view.findViewById(R.id.imgview_contact_status);

            if (listSearchresult.get(position).getstatus().equals("0")) {
                holder.imgview_contact_status.setImageResource(R.mipmap.pending);
            } else if (listSearchresult.get(position).getstatus().equals("1")) {
                holder.imgview_contact_status.setImageResource(R.mipmap.tick);
            } else if (listSearchresult.get(position).getstatus().equals("2")) {
                holder.imgview_contact_status.setImageResource(R.mipmap.decline);
            }else if (listSearchresult.get(position).getstatus().equals("3")) {
                holder.imgview_contact_status.setImageResource(R.mipmap.unfollow);
            }

        }

        holder.txtView_contact_name.setText(listSearchresult.get(position).getName());
        holder.txtView_contact_phno.setText(listSearchresult.get(position).getPhoneNumber());
        return view;
    }

}
