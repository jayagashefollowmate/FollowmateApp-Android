package com.terracotta.followmateapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.inscripts.custom.EmojiTextView;
import com.squareup.picasso.Picasso;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.pojo.ChatroomChatMessage;

import java.util.ArrayList;

public class ChatroomChatAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChatroomChatMessage> messagelist;
    private static final int TYPES_COUNT = 2;
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    public ChatroomChatAdapter(Context context, ArrayList<ChatroomChatMessage> messages) {
        this.context = context;
        messagelist = messages;
    }

    @Override
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        ChatroomChatMessage message = (ChatroomChatMessage) getItem(position);
        if (message.getIsMyMessage()) {
            return TYPE_RIGHT;
        }
        return TYPE_LEFT;
    }

    @Override
    public int getCount() {
        return messagelist.size();
    }

    @Override
    public Object getItem(int position) {
        return messagelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView timestamp, userName;
        ImageView image,imageViewVideoMessage;
        EmojiTextView message;
        VideoView VideoViewImageMessage;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (view == null) {
            if (getItemViewType(position) == TYPE_RIGHT) {
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_chat_bubble_right, parent, false);
                holder.message = (EmojiTextView) view.findViewById(R.id.textViewMessage);
                holder.timestamp = (TextView) view.findViewById(R.id.textViewTime);
                holder.userName = (TextView) view.findViewById(R.id.textViewUserName);
                holder.image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
                holder.imageViewVideoMessage = (ImageView) view.findViewById(R.id.imageViewVideoMessage);
                holder.VideoViewImageMessage=(VideoView)view.findViewById(R.id.VideoViewImageMessage);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_chat_bubble_left, parent, false);
                holder.message = (EmojiTextView) view.findViewById(R.id.textViewMessage);
                holder.timestamp = (TextView) view.findViewById(R.id.textViewTime);
                holder.userName = (TextView) view.findViewById(R.id.textViewUserName);
                holder.image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
                holder.imageViewVideoMessage = (ImageView) view.findViewById(R.id.imageViewVideoMessage);
                holder.VideoViewImageMessage=(VideoView)view.findViewById(R.id.VideoViewImageMessage);

            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ChatroomChatMessage message = messagelist.get(position);
        String messagetype = message.getMessagetype();
        if (messagetype.equals("12")) {
            holder.message.setVisibility(View.GONE);
             holder.image.setVisibility(View.VISIBLE);
            holder.VideoViewImageMessage.setVisibility(View.GONE);
            holder.imageViewVideoMessage.setVisibility(View.GONE);
            Picasso.with(context).load(message.getMessage()).into(holder.image);
        } else if (messagetype.equals("14")) {
            holder.message.setVisibility(View.GONE);
            holder.VideoViewImageMessage.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.imageViewVideoMessage.setVisibility(View.VISIBLE);
            holder.message.setText(message.getMessage());





            String link=message.getMessage();

            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.VideoViewImageMessage);
            Uri video = Uri.parse(link);
            holder.VideoViewImageMessage.setMediaController(mediaController);
            holder.VideoViewImageMessage.setVideoURI(video);
          //  holder.VideoViewImageMessage.start();
         //   holder.VideoViewImageMessage.pause();


        }  else if (messagetype.equals("16") || messagetype.equals("17")){
            holder.message.setVisibility(View.VISIBLE);
            holder.VideoViewImageMessage.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
            holder.imageViewVideoMessage.setVisibility(View.GONE);
            holder.message.setText(message.getMessage());

        }




        /*else if (messagetype.equals("18")) {
            holder.message.setVisibility(View.VISIBLE);
			holder.image.setVisibility(View.GONE);
			holder.message.setText(StickerKeyboard.showSticker(context, message.getMessage()));
		}*/ else {
            holder.message.setVisibility(View.VISIBLE);
            holder.VideoViewImageMessage.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
            holder.message.setEmojiText(message.getMessage());
            holder.imageViewVideoMessage.setVisibility(View.GONE);
        }

        holder.timestamp.setText(message.getTimestamp());
        holder.userName.setText(message.getUserName());
        final String linkmessage = holder.message.getText().toString();

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linkmessage.contains("http://")) {
                    Toast.makeText(context, "link", Toast.LENGTH_SHORT).show();

                }
            }
        });

        final ViewHolder finalHolder = holder;
        holder.imageViewVideoMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalHolder.imageViewVideoMessage.setVisibility(View.GONE);
                finalHolder.VideoViewImageMessage.start();
            }
        });



        return view;
    }

}
