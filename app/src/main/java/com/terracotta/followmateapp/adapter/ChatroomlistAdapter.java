package com.terracotta.followmateapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.librarytestapp.ChatroomListActivity;
import com.terracotta.followmateapp.pojo.Chatroom;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ChatroomlistAdapter extends BaseAdapter {

    private LayoutInflater inflator;
    private ArrayList<Chatroom> chatroomList;
    private Context mContext;

    public ChatroomlistAdapter(Context context, ArrayList<Chatroom> chatroomList) {
        inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.chatroomList = chatroomList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return chatroomList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return chatroomList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    private static class ViewHolder {
        TextView chatroomName;
        ImageView chatroomProtectedIcon, ivDeleteChatRoom;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View vi = view;
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            vi = inflator.inflate(R.layout.custom_chatroomlist_item, null);
            holder.chatroomName = (TextView) vi.findViewById(R.id.textViewChatroomName);
            holder.chatroomProtectedIcon = (ImageView) vi.findViewById(R.id.imageViewPasswordProtectedIcon);
            holder.ivDeleteChatRoom = (ImageView) vi.findViewById(R.id.ivDeleteChatRoom);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        final Chatroom chatroom = chatroomList.get(position);
        String type = chatroom.getType();
        if (type.equals("1")) {
            holder.chatroomProtectedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.chatroomProtectedIcon.setVisibility(View.GONE);
        }

        if (chatroom.getCreatedBy().equals("owner")) {
            holder.ivDeleteChatRoom.setVisibility(View.VISIBLE);
        } else {
            holder.ivDeleteChatRoom.setVisibility(View.GONE);
        }

        holder.chatroomName.setText(chatroom.getChatroomName());

        holder.ivDeleteChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog( mContext, SweetAlertDialog.WARNING_TYPE )
                        .setTitleText( "Delete Group?" )
                        .setContentText( "Are you sure you want to delete Group?" )
                        .setConfirmText( "Yes" )
                        .setConfirmClickListener( new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick (SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                ChatroomListActivity.deleteChatRoom(chatroom.getChatroomId());
                            }
                        } )
                        .setCancelText( "No" )
                        .setCancelClickListener( new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick (SweetAlertDialog dialog) {
                                dialog.cancel();

                            }
                        } )
                        .showCancelButton( true )
                        .show();



            }
        });
        return vi;
    }

}
