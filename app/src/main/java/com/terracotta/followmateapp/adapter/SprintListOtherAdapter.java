package com.terracotta.followmateapp.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.model.SprintListMeModel;
import com.terracotta.followmateapp.subfragment.SprintListFollowOtherFragment;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


//Developement team:               Mobility Team
//Creation Date:                   9 Aug 2016
//Information                      shows contact list
// Comment:


public class SprintListOtherAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    SessionManager mSessionManager;

    SprintListFollowOtherFragment sprintListFollowOtherFragment;
    private List<SprintListMeModel> listSearchresult = null;


    //font
    Typeface roboto;
    Calendar StartCal = Calendar.getInstance();


    public SprintListOtherAdapter(Context context, ArrayList<SprintListMeModel> listSearchresult, SprintListFollowOtherFragment sprintListFollowOtherFragment) {
        mContext = context;
        this.listSearchresult = listSearchresult;
        this.sprintListFollowOtherFragment = sprintListFollowOtherFragment;
        inflater = LayoutInflater.from(mContext);

    }

    public class ViewHolder {
        TextView txtView_activity, txtView_StartDateTime, txtView_EndDateTime;
        ImageView imgview_timeticker_SprintList_me, button_delete_sprint;

    }

    @Override
    public int getCount() {
        return listSearchresult.size();
    }

    @Override
    public SprintListMeModel getItem(int position) {
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


        view = inflater.inflate(R.layout.row_sprint_list, null);
        mSessionManager = new SessionManager(mContext);

        holder.txtView_activity = (TextView) view
                .findViewById(R.id.txtView_activity);
        holder.txtView_EndDateTime = (TextView) view
                .findViewById(R.id.txtView_EndDateTime);
        holder.txtView_StartDateTime = (TextView) view
                .findViewById(R.id.txtView_StartDateTime);
        holder.button_delete_sprint = (ImageView) view.findViewById(R.id.button_delete_sprint);
        holder.imgview_timeticker_SprintList_me = (ImageView) view.findViewById(R.id.imgview_timeticker_SprintList_me);

        //font
        roboto = Typeface
                .createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        holder.txtView_activity.setTypeface(roboto);
        holder.txtView_EndDateTime.setTypeface(roboto);
        holder.txtView_StartDateTime.setTypeface(roboto);
       // holder.button_delete_sprint.setTypeface(roboto);


        String Start_Date = listSearchresult.get(position).getlistsprintdata().get(position).getStart_date_time();

        //set format to SimpleDateFormat and convert string date to Calender
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(Start_Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }// all done
        StartCal = sdf.getCalendar();
        Log.e("StartCal", "StartCal " + StartCal.getTime());
        Log.e("Date to show ", "Date to show " + String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", StartCal));

        String time = null;
        int AM_PM = StartCal.get(Calendar.AM_PM);
        if (AM_PM == 0) {
            time = String.valueOf(StartCal.get(Calendar.DATE)) + ":" + String.valueOf(StartCal.get(Calendar.MINUTE) + " am");

        } else if (AM_PM == 1) {
            time = String.valueOf(StartCal.get(Calendar.DATE)) + ":" + String.valueOf(StartCal.get(Calendar.MINUTE) + " pm");

        }
        Log.e("time", "time" + time);





        //convert current and sprint date in string with same format
        Calendar currentCal = Calendar.getInstance();
        SimpleDateFormat sdfcompare = new SimpleDateFormat("yyyy-MM-dd");
        String CurrentDate = sdfcompare.format(currentCal.getTime());
        System.out.println("Current date in String Format: " + CurrentDate);

        Date convertedCurrentDate = null;
        try {
            convertedCurrentDate = sdfcompare.parse(Start_Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String Sprintdate = sdfcompare.format(convertedCurrentDate);
        System.out.println("Sprint date in String Format: " +Sprintdate);



        holder.txtView_activity.setText(listSearchresult.get(position).getlistsprintdata().get(position).getActivity());
        holder.txtView_EndDateTime.setText(" for " + listSearchresult.get(position).getlistsprintdata().get(position).getDuration());

        if(!CurrentDate.equals(Sprintdate)) {
            holder.txtView_StartDateTime.setText(String.format("%1$td %1$tb %1$tY at  %1$tI:%1$tM %1$Tp", StartCal));
        }else if(CurrentDate.equals(Sprintdate)) {
            holder.txtView_StartDateTime.setText(String.format("Today at  %1$tI:%1$tM %1$Tp", StartCal));
        }



        if (listSearchresult.get(position).getlistsprintdata().get(position).getStatus().equals("1")) {
            holder.imgview_timeticker_SprintList_me.setVisibility(View.VISIBLE);
        } else {
            holder.imgview_timeticker_SprintList_me.setVisibility(View.GONE);
        }

        if (listSearchresult.get(position).getlistsprintdata().get(position).getSprint_created_by().equals(mSessionManager.getStringData(Constants.USER_ID))) {

            holder.button_delete_sprint.setVisibility(View.VISIBLE);
        } else {
            holder.button_delete_sprint.setVisibility(View.GONE);
        }
        holder.button_delete_sprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    Toast.makeText(mContext, position + "position Sprint deleted", Toast.LENGTH_SHORT).show();
                holder.button_delete_sprint.setEnabled(false);
                if (listSearchresult.get(position).getlistsprintdata().get(position).getStatus().equals("1")) {
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                    mSessionManager.setStartflagMe(false);
                }
                callDeleteService(listSearchresult.get(position).getlistsprintdata().get(position).getSprintID(),holder.button_delete_sprint);
            }
        });

       /* view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSessionManager.putStringData(Constants.USER_Activity_me, listSearchresult.get(position).getlistsprintdata().get(position).getActivity());
                mSessionManager.putStringData(Constants.USER_START_TIME_me, listSearchresult.get(position).getlistsprintdata().get(position).getStart_date_time());
                mSessionManager.putStringData(Constants.USER_END_TIME_me, listSearchresult.get(position).getlistsprintdata().get(position).getEnd_date_time());
                mSessionManager.putStringData(Constants.USER_DURATION_me, listSearchresult.get(position).getlistsprintdata().get(position).getDuration());


            }
        });*/

        return view;
    }


    void callDeleteService(final String SprintID, final ImageView button_delete) {


        RequestQueue queue = Volley.newRequestQueue(mContext);
        // Tag used to cancel the request

        String url = Constants.URL_DELTE_SPRINT;

        final ProgressDialog pDialog = new ProgressDialog(mContext);

        pDialog.setMessage("Deleting Sprint...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                button_delete.setEnabled(true);
                // mPostCommentResponse.requestCompleted();
                Log.e("Followmate", "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String user_email = null, user_country_code = null, user_name = null, user_id = null, user_mobile = null, user_verified = null, status = null, user_profile = null, user;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        sprintListFollowOtherFragment.callListWebService();

                    } else if (ISSucess.equals("2")) {
                        sprintListFollowOtherFragment.HideList();

                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                button_delete.setEnabled(true);
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e("Followmate", "Service--i/p-" + error);
                pDialog.dismiss();


                /*new AlertDialog.Builder(mContext)
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                Toast.makeText(mContext, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("sprint_id", SprintID);


                Log.e("Followmate", "URL: " + Constants.URL_DELTE_SPRINT + " user_id: " +
                        mSessionManager.getStringData(Constants.USER_ID) +
                        " sprint_id: " + SprintID);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        mstringrequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
    }


}
