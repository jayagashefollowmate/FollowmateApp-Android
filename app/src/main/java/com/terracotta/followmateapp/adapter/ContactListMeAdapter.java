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

import com.terracotta.followmateapp.model.ContactListMeModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.lazyLoader.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//Developement team:               Mobility Team
//Creation Date:                   9 Aug 2016
//Information                      shows contact list
// Comment:


public class ContactListMeAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<ContactListMeModel> listSearchresult = null;
    String WhichActivity;
    SessionManager mSessionManager;

    boolean CheckedCheckbox[];
    ImageLoader imageLoader;


    //font
    Typeface roboto;


    public ContactListMeAdapter(Context context, ArrayList<ContactListMeModel> listSearchresult, String WhichActivity) {
        mContext = context;
        this.listSearchresult = listSearchresult;
        inflater = LayoutInflater.from(mContext);
        this.WhichActivity = WhichActivity;
        mSessionManager = new SessionManager(mContext);
        CheckedCheckbox = new boolean[listSearchresult.size()];
        Arrays.fill(CheckedCheckbox, false);



        for(int position=0;position<listSearchresult.size();position++){
            if (listSearchresult.get(position).isCheck()) {

                if (!mSessionManager.getStringData(Constants.USER_PHNO).equals(listSearchresult.get(position).getPhoneNumber())) {
                    Constants.arrayList_SelectedContactList.set(position, true);
                    Constants.TOTAL_CONTACTS_SELECTED = Constants.TOTAL_CONTACTS_SELECTED + 1;
                    CheckedCheckbox[position] = true;
                    Log.e("Selected", "Selected" + Constants.TOTAL_CONTACTS_SELECTED);
                }
            }
        }
    }

    public class ViewHolder {
        TextView txtView_contact_name, txtView_contact_phno;
        CheckBox checkBox_contact;
        ImageView imgview_contact_status, imageview_profile;

    }

    @Override
    public int getCount() {
        return listSearchresult.size();
    }

    @Override
    public ContactListMeModel getItem(int position) {
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
            view = inflater.inflate(R.layout.row_contact_list, null);
        } else if (WhichActivity.equals("Without")) {
            view = inflater.inflate(R.layout.row_contact_listwithout_check, null);
        }

        holder.txtView_contact_name = (TextView) view
                .findViewById(R.id.txtView_contact_name);
        holder.txtView_contact_phno = (TextView) view
                .findViewById(R.id.txtView_contact_phno);
        holder.imageview_profile = (ImageView) view
                .findViewById(R.id.imageview_profile);

        imageLoader = new ImageLoader(mContext);

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
            holder.checkBox_contact = (CheckBox) view
                    .findViewById(R.id.checkBox_contact);
            holder.checkBox_contact.setChecked(CheckedCheckbox[position]);

            if (listSearchresult.get(position).isCheck()) {
                holder.checkBox_contact.setChecked(true);

            }

            holder.checkBox_contact.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //is chkIos checked?
                    if (((CheckBox) v).isChecked()) {
                        //Checked
                        if (!mSessionManager.getStringData(Constants.USER_PHNO).equals(listSearchresult.get(position).getPhoneNumber())) {
                            Constants.arrayList_SelectedContactList.set(position, true);
                            Constants.TOTAL_CONTACTS_SELECTED = Constants.TOTAL_CONTACTS_SELECTED + 1;
                            CheckedCheckbox[position] = true;
                            listSearchresult.get(position).setCheck(true);
                            //Constants.arrayList_ContactList.get(position).setCheck(true);
                            Log.e("Selected", "Selected" + Constants.TOTAL_CONTACTS_SELECTED);
                            Log.e("CheckedCheckbox", "CheckedCheckbox" +  position+" "+CheckedCheckbox[position]);

                        }
                    } else {
                        //UnChecked
                        if (!mSessionManager.getStringData(Constants.USER_PHNO).equals(listSearchresult.get(position).getPhoneNumber())) {
                            Constants.arrayList_SelectedContactList.set(position, false);
                            Constants.TOTAL_CONTACTS_SELECTED = Constants.TOTAL_CONTACTS_SELECTED - 1;
                            CheckedCheckbox[position] = false;
                            listSearchresult.get(position).setCheck(false);
                           // Constants.arrayList_ContactList.get(position).setCheck(false);
                            Log.e("Selected", "Selected" + Constants.TOTAL_CONTACTS_SELECTED);
                            Log.e("CheckedCheckbox", "CheckedCheckbox" +  position+" "+ CheckedCheckbox[position]);

                        }
                    }

                }
            });


        } else {
            holder.imgview_contact_status = (ImageView) view.findViewById(R.id.imgview_contact_status);



            for (int i = 0; i < Constants.arrayList_ContactList_toShow.size(); i++) {
                for (int j = 0; j < Constants.arraylist_old_toCrossCheckStatus.size(); j++) {
                    if (Constants.arraylist_old_toCrossCheckStatus.get(j).getUserID().equals(Constants.arrayList_ContactList_toShow.get(i).getUserID())) {
                        Constants.arrayList_ContactList_toShow.get(i).setstatus(Constants.arraylist_old_toCrossCheckStatus.get(j).getstatus());
                    }
                }
            }






            if (listSearchresult.get(position).getstatus().equals("0")) {
                holder.imgview_contact_status.setImageResource(R.mipmap.pending);
            } else if (listSearchresult.get(position).getstatus().equals("1")) {
                holder.imgview_contact_status.setImageResource(R.mipmap.tick);
            } else if (listSearchresult.get(position).getstatus().equals("2")) {
                holder.imgview_contact_status.setImageResource(R.mipmap.decline);
            } else if (listSearchresult.get(position).getstatus().equals("3")) {
                holder.imgview_contact_status.setImageResource(R.mipmap.unfollow);
            }

        }


        holder.txtView_contact_name.setText(listSearchresult.get(position).getName());
        holder.txtView_contact_phno.setText(listSearchresult.get(position).getPhoneNumber());


        return view;
    }

}
