package com.terracotta.followmateapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.inscripts.Keyboards.SmileyKeyBoard;
import com.inscripts.Keyboards.StickerKeyboard;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.adapter.ChatroomChatAdapter;
import com.terracotta.followmateapp.helper.DatabaseHandler;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.helper.Utils;
import com.terracotta.followmateapp.pojo.ChatroomChatMessage;

import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import butterknife.ButterKnife;

public class OpenChatWindowFragment extends Fragment implements View.OnKeyListener {

    private View view;
    String TAG = "FollowMate";
    SessionManager mSessionManager;

    private String chatroomName, chatroomId;
    private EditText messageField;
    private Button sendbtn;
    private CometChatroom cometChatroom;
    private ArrayList<ChatroomChatMessage> messageList;
    private ChatroomChatAdapter adapter;
    private ListView chatListView;
    private BroadcastReceiver customReceiver;
    private static Uri fileUri;
    private SmileyKeyBoard smiliKeyBoard;
    // private ImageButton smilieyButton, stickerButton;
    private StickerKeyboard stickerKeyboard;

    private CometChat cometchat;

    static OnFragmentInteractionListener mListener;
    static ChatFragment mchatFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.sample_activity_chat, container, false);
        ButterKnife.bind(this, view);
        //for settinf menu for this fragment individually
        setHasOptionsMenu(true);
        mSessionManager = new SessionManager(getActivity());


        chatroomName = mSessionManager.getStringData("cName");
        chatroomId = mSessionManager.getStringData("chatroomid");

        messageField = (EditText) view.findViewById(R.id.editTextChatMessage);
        sendbtn = (Button) view.findViewById(R.id.buttonSendMessage);
        cometChatroom = CometChatroom.getInstance(getActivity());

        chatListView = (ListView) view.findViewById(R.id.listViewChatMessages);
        messageList = new ArrayList<>();
        DatabaseHandler helper = new DatabaseHandler(getActivity());
        Log.e("myId in Chatrrom chat Activity : ", SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId));
        Log.e("chatroom in Chatrrom chat Activity : ", chatroomId);
        messageList = helper.getAllChatroomMessageByChatroomID(Long.parseLong(chatroomId));
        adapter = new ChatroomChatAdapter(getActivity(), messageList);
        chatListView.setAdapter(adapter);
        chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatListView.setStackFromBottom(true);

//        smilieyButton = (ImageButton) view.findViewById(R.id.buttonSendSmiley);
//        stickerButton = (ImageButton) view.findViewById(R.id.buttonSendSticker);
//        smiliKeyBoard = new SmileyKeyBoard();
//        smiliKeyBoard.enable(this, this, R.id.footer_for_emoticons, messageField);
//        final RelativeLayout chatFooter = (RelativeLayout) view.findViewById(R.id.relativeBottomArea);
//        smiliKeyBoard.checkKeyboardHeight(chatFooter);
//        smiliKeyBoard.enableFooterView(messageField);
//
//        stickerKeyboard = new StickerKeyboard();
//        stickerKeyboard.enable(this, new StickerClickInterface() {
//            @Override
//            public void getClickedSticker(int gridviewItemPosition) {
//                final String data = stickerKeyboard.getClickedSticker(gridviewItemPosition);
//                cometChatroom.sendSticker(data, new Callbacks() {
//
//                    @Override
//                    public void successCallback(JSONObject response) {
//                        try {
//                            ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"), data,
//                                    Utils.convertTimestampToDate(System.currentTimeMillis()), "Me :", true, "18",
//                                    SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId), chatroomId);
//                            DatabaseHandler helper = new DatabaseHandler(getActivity());
//                            helper.insertChatroomMessage(newmessage);
//                            addMessage(newmessage);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void failCallback(JSONObject response) {
//
//                    }
//                });
//            }
//        }, R.id.footer_for_emoticons, messageField);
//        stickerKeyboard.checkKeyboardHeight(chatFooter);
//        //StickerKeyboard.setStickerSize(400);
//        stickerKeyboard.enableFooterView(messageField);

        sendbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

//        smilieyButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                smiliKeyBoard.showKeyboard(chatFooter);
//            }
//        });
//
//        stickerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stickerKeyboard.showKeyboard(chatFooter);
//            }
//        });

        customReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Log.e("onReceive","onReceive");
                if (intent.hasExtra("Newmessage")) {
                    String message = intent.getStringExtra("Message");
                    String userName = intent.getStringExtra("from") + " :";
                    String messageId = intent.getStringExtra("message_id");
                    String time = Utils.convertTimestampToDate(Utils.correctTimestamp(Long.parseLong(intent
                            .getStringExtra("time"))));
                    String from = intent.getStringExtra("fromid");
                    String to;
                    String messagetype = intent.getStringExtra("message_type");

                    if (intent.hasExtra("to")) {
                        to = intent.getStringExtra("to");
                    } else {
                        to = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId);
                    }


                    Log.e("onReceive", "onReceive message " + message + " userName " + userName + " messageId " + messageId + " from " + from + " messagetype " + messagetype);


                    boolean ismyMessage = intent.getBooleanExtra("selfmessage", false);
                    ChatroomChatMessage newmessage;
                    if (intent.hasExtra("imageMessage")) {
                        if (intent.hasExtra("myphoto")) {
                            newmessage = new ChatroomChatMessage(messageId, message, time, userName, true,
                                    messagetype, from, to);
                        } else {
                            newmessage = new ChatroomChatMessage(messageId, message, time, userName, false,
                                    messagetype, from, to);
                        }
                    } else if (intent.hasExtra("videoMessage")) {
                        if (intent.hasExtra("myvideo")) {
                            newmessage = new ChatroomChatMessage(messageId, message, time, userName, true,
                                    messagetype, from, to);
                        } else {
                            newmessage = new ChatroomChatMessage(messageId, message, time, userName, false,
                                    messagetype, from, to);
                        }
                    } else {
                        if (ismyMessage) {
                            newmessage = new ChatroomChatMessage(messageId, message, time, userName, true,
                                    messagetype, from, to);
                        } else {
                            newmessage = new ChatroomChatMessage(messageId, message, time, userName, false,
                                    messagetype, from, to);
                        }
                    }
                    addMessage(newmessage);
                }
            }

        };




        return view;
    }

    public static OpenChatWindowFragment newInstance(ChatFragment chatFragment) {
        mchatFragment = chatFragment;
        try {
            mListener = (OnFragmentInteractionListener) mchatFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(mchatFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        OpenChatWindowFragment openChatWindowFragment = new OpenChatWindowFragment();
        return openChatWindowFragment;
    }

    private void sendMessage() {
        final String message = messageField.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            messageField.setText("");
            /*
             * Send message to active chatroom.
			 */
            cometChatroom.sendMessage(chatroomId, message, new Callbacks() {

                @Override
                public void successCallback(JSONObject response) {
                    try {
                        ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"), message,
                                Utils.convertTimestampToDate(System.currentTimeMillis()), "Me :", true, "10",
                                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId), chatroomId);
                        DatabaseHandler helper = new DatabaseHandler(getActivity());
                        helper.insertChatroomMessage(newmessage);
                        addMessage(newmessage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failCallback(JSONObject response) {
                    Logger.debug("send message fail = " + response);
                }
            });
        }
    }

    private void addMessage(ChatroomChatMessage newmessage) {
        if (newmessage != null) {
            boolean duplicate = false;
            for (ChatroomChatMessage msg : messageList) {
                if (msg != null) {
                    if (msg.getMessage_id().equals(newmessage.getMessage_id())) {
                        duplicate = true;
                        break;
                    }
                }
            }
            if (!duplicate) {
                messageList.add(newmessage);
                adapter.notifyDataSetChanged();
                chatListView.setSelection(messageList.size() - 1);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chatroom_chat, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_invite_user:
                //startActivity(new Intent(ChatroomChatActivity.this, InviteUsersListActivity.class));
                mListener.onFragmentInteractionInviteUser();
                break;


           /* case R.id.action_call_cruser:
                break;*/

//            case R.id.action_send_image:
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.desert);
//                cometChatroom.sendImage(bitmap, new Callbacks() {
//
//                    @Override
//                    public void successCallback(JSONObject response) {
//
//                    }
//
//                    @Override
//                    public void failCallback(JSONObject response) {
//                        Toast.makeText(getApplicationContext(), "Image send failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
            case R.id.action_pick_image:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                break;
            case R.id.action_capture_photo:
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = Utils.getOutputMediaFile(1, false);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent1, 2);
                break;
            case R.id.action_capture_video:
                Intent intent2 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                fileUri = Utils.getOutputMediaFile(2, false);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                intent2.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 15000000); // 15 Mb
                startActivityForResult(intent2, 3);
                break;
            case R.id.action_share_video:
                Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent3.setType("video/*");
                startActivityForResult(intent3, 4);
                break;
           /* case R.id.action_share_audio:
                AssetManager mgr = getActivity().getAssets();
                try {
                    AssetManager am = getActivity().getAssets();
                    InputStream inputStream = am.open("song.aac");
                    File file = new File(getActivity().getCacheDir() + "/song.aac");

                    try {
                        OutputStream outputStream = new FileOutputStream(file);
                        byte buffer[] = new byte[1024];
                        int length = 0;

                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        outputStream.close();
                        inputStream.close();

                    } catch (IOException e) {

                    }
                    cometChatroom.sendAudioFile(file, new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Logger.error("success " + response);
                            try {
                                ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"), response
                                        .getString("original_file"), Utils.convertTimestampToDate(System
                                        .currentTimeMillis()), "Me :", true, "16", SharedPreferenceHelper
                                        .get(Keys.SharedPreferenceKeys.myId), chatroomId);
                                addMessage(newmessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failCallback(JSONObject response) {
                            Logger.error("fail " + response);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_share_file:
                Intent ii = new Intent(Intent.ACTION_GET_CONTENT);
                ii.setType("File*//*");
                startActivityForResult(ii, 1234);
                break;*/
           /* case R.id.action_getchathistory:
                cometChatroom.getChatroomChatHistory(Long.parseLong(chatroomId), -1L, new Callbacks() {
                    @Override
                    public void successCallback(JSONObject jsonObject) {
                        Log.e("abc", "response of history " + jsonObject);
                    }

                    @Override
                    public void failCallback(JSONObject jsonObject) {

                    }
                });*/

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == getActivity().RESULT_OK) {
                if (requestCode == 1) {
                    String path = Utils.getPath(data.getData(), true);

                    cometChatroom.sendImage(new File(path), new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Logger.debug("share video send succes = " + response);
                        }

                        @Override
                        public void failCallback(JSONObject response) {
                            Logger.debug("share video send fail = " + response);
                        }
                    });
                } else if (requestCode == 2) {
                    Uri selectedImageUri = data.getData();
                    File newfile = new File(new URI(fileUri.toString()));
                    String filePath = newfile.getAbsolutePath();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap image = BitmapFactory.decodeFile(filePath, options);
                    if (image != null) {
                        cometChatroom.sendImage(image, new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.debug("image send succes = " + response);
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Logger.debug("image send fail = " + response);

                            }
                        });
                    }
                } else if (requestCode == 3) {
                    try {
                        File newfile = new File(new URI(fileUri.toString()));
                        cometChatroom.sendVideo(newfile, new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.debug("video send succes = " + response);
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Logger.debug("video send fail = " + response);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == 4) {
                    try {
                        String path = Utils.getPath(data.getData(), false);
                        cometChatroom.sendVideo(path, new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.debug("share video send succes = " + response);
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Logger.debug("share video send fail = " + response);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == 1234) {
                    try {
                        File f = new File(data.getData().getPath());
                        cometChatroom.sendFile(f, new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.error("success " + response);
                                try {
                                    ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"),
                                            response.getString("original_file"), Utils.convertTimestampToDate(System
                                            .currentTimeMillis()), "Me :", true, "17", SharedPreferenceHelper
                                            .get(Keys.SharedPreferenceKeys.myId), chatroomId);
                                    addMessage(newmessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failCallback(JSONObject response) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStart","onStart");
        if (customReceiver != null) {
            Log.e("Broadcast started","Broadcast started");
            getActivity().registerReceiver(customReceiver, new IntentFilter("Chatroom_message"));
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStop","onStop");
        if (customReceiver != null) {
            Log.e("unregisterReceiver","unregisterReceiver");

            getActivity().unregisterReceiver(customReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume","onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause","onPause");

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK) {

            backToList();

            return true;
        } else {
            return false;
        }
    }

    private void backToList() {
        mListener.onFragmentInteractionOpenGroupListChat();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteractionInviteUser();

        public void onFragmentInteractionOpenGroupListChat();

    }
}
