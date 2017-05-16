package com.terracotta.followmateapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.inscripts.Keyboards.StickerKeyboard;
import com.inscripts.custom.EmojiTextView;
import com.squareup.picasso.Picasso;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.Keys.SharedPreferenceKeys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.librarytestapp.AVChatActivity;
import com.terracotta.followmateapp.librarytestapp.WebviewActivity;
import com.terracotta.followmateapp.pojo.SingleChatMessage;

import java.util.ArrayList;

public class SingleChatAdapter extends BaseAdapter {

    private ArrayList<SingleChatMessage> messageList;
    private static final int TYPES_COUNT = 2;
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;
    private Context context;
    StickerKeyboard stickerKeyboard = new StickerKeyboard();

    public SingleChatAdapter(Context context, ArrayList<SingleChatMessage> messages) {
        messageList = messages;
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        SingleChatMessage message = (SingleChatMessage) getItem(position);
        if (message.getIsMyMessage()) {
            return TYPE_RIGHT;
        }
        return TYPE_LEFT;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView timestamp;
        ImageView image, msgtick,imageViewVideoMessage;
        EmojiTextView message;
        VideoView VideoViewImageMessage;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (view == null) {
            if (getItemViewType(position) == TYPE_RIGHT) {
                view = LayoutInflater.from(context).inflate(R.layout.oneonone_chat_bubble_right, parent, false);
                holder.message = (EmojiTextView) view.findViewById(R.id.textViewMessage);
                holder.timestamp = (TextView) view.findViewById(R.id.textViewTime);
                holder.image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
                holder.msgtick = (ImageView) view.findViewById(R.id.imageViewmessageTicks);
                holder.imageViewVideoMessage = (ImageView) view.findViewById(R.id.imageViewVideoMessage);
                holder.VideoViewImageMessage=(VideoView)view.findViewById(R.id.VideoViewImageMessage);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.oneonone_chat_bubble_left, parent, false);
                holder.message = (EmojiTextView) view.findViewById(R.id.textViewMessage);
                holder.timestamp = (TextView) view.findViewById(R.id.textViewTime);
                holder.image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
                holder.msgtick = (ImageView) view.findViewById(R.id.imageViewmessageTicks);
                holder.imageViewVideoMessage = (ImageView) view.findViewById(R.id.imageViewVideoMessage);
                holder.VideoViewImageMessage=(VideoView)view.findViewById(R.id.VideoViewImageMessage);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        SingleChatMessage message = messageList.get(position);
        String messageType = message.getMessageType();
        if (messageType.equals("12")) {
            holder.message.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            holder.VideoViewImageMessage.setVisibility(View.GONE);
            holder.imageViewVideoMessage.setVisibility(View.GONE);
            Picasso.with(context).load(message.getMessage()).into(holder.image);
        } else if (messageType.equals("14")) {
            holder.image.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);

            holder.VideoViewImageMessage.setVisibility(View.VISIBLE);
            holder.imageViewVideoMessage.setVisibility(View.VISIBLE);
            holder.message.setText(message.getMessage());


            String link=message.getMessage();

            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.VideoViewImageMessage);
            Uri video = Uri.parse(link);
            holder.VideoViewImageMessage.setMediaController(mediaController);
            holder.VideoViewImageMessage.setVideoURI(video);
        } else if( messageType.equals("16") || messageType.equals("17")) {
            holder.image.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);

            holder.VideoViewImageMessage.setVisibility(View.GONE);
            holder.imageViewVideoMessage.setVisibility(View.GONE);
            holder.message.setText(message.getMessage());
        } /*else if (messageType.equals("18")) {
            holder.message.setVisibility(View.VISIBLE);
			holder.image.setVisibility(View.GONE);
			holder.message.setText(StickerKeyboard.showSticker(context, message.getMessage()));
		}*/ else if (messageType.equals("19") || messageType.equals("20")) {
            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.VideoViewImageMessage.setVisibility(View.GONE);
            holder.imageViewVideoMessage.setVisibility(View.GONE);
            holder.message.setMovementMethod(LinkMovementMethod.getInstance());
            holder.message.setText(createWebviewLink(message));
        } else if (messageType.equals("21")) {
            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.VideoViewImageMessage.setVisibility(View.GONE);
            holder.imageViewVideoMessage.setVisibility(View.GONE);
            holder.message.setMovementMethod(LinkMovementMethod.getInstance());
            holder.message.setText(createClickableText(message));
        } else {
            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.VideoViewImageMessage.setVisibility(View.GONE);
            holder.imageViewVideoMessage.setVisibility(View.GONE);
            String msg = message.getMessage();
            if (msg.contains("|#|")) {
                holder.message.setMovementMethod(LinkMovementMethod.getInstance());
                holder.message.setText(createClickableText(message));
            } else {
                holder.message.setEmojiText(msg);
            }
        }
        int messtick = message.getTickStatus();
        holder.msgtick.setVisibility(View.VISIBLE);
        switch (messtick) {
            case Keys.MessageTicks.sent:
                holder.msgtick.setImageResource(R.drawable.iconsent);
                break;
            case Keys.MessageTicks.deliverd:
                holder.msgtick.setImageResource(R.drawable.icondeliverd);
                break;
            case Keys.MessageTicks.read:
                holder.msgtick.setImageResource(R.drawable.iconread);
                break;
            case Keys.MessageTicks.notick:
                holder.msgtick.setVisibility(View.GONE);
                break;
            default:
                holder.msgtick.setVisibility(View.GONE);
                break;
        }
        holder.timestamp.setText(message.getTimestamp());


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

    private SpannableString createWebviewLink(SingleChatMessage message) {
        final String originalMessage = message.getMessage();
        String msg = "";
        if (message.getMessageType().equals("19")) {
            msg = "Has sent whiteboard request\nClick here to open";
        } else if (message.getMessageType().equals("20")) {
            msg = "Has sent writeboard request\nClick here to open";
        }
        SpannableString mystring = new SpannableString(msg);
        ClickableSpan span = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent i = new Intent(context, WebviewActivity.class);
                i.putExtra("webview_url", originalMessage);
                context.startActivity(i);

            }
        };
        mystring.setSpan(span, 0, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return mystring;

    }

    private SpannableString createClickableText(final SingleChatMessage message) {
        String msg = message.getMessage();
        if (message.getMessageType().equals("21")) {
            msg = "Has shared screen|#|" + msg;
        }
        int startIndex = msg.indexOf("|#|");
        final String roomName = msg.substring(startIndex + 3);
        msg = msg.substring(0, startIndex) + "\nClick here to Join";
        SpannableString mystring = new SpannableString(msg);
        ClickableSpan span = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent i = new Intent(context, AVChatActivity.class);
                SharedPreferenceHelper.save(SharedPreferenceKeys.CALLID, roomName);
                i.putExtra("user_id", message.getFrom());
                i.putExtra("pluginType", 2); // 2 is for AVbroadcast
                i.putExtra("iamBroadcaster", false);
                context.startActivity(i);
            }
        };
        mystring.setSpan(span, startIndex, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return mystring;
    }
}
