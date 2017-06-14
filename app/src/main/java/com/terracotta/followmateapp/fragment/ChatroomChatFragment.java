package com.terracotta.followmateapp.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inscripts.Keyboards.SmileyKeyBoard;
import com.inscripts.Keyboards.StickerKeyboard;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.adapter.ChatroomChatAdapter;
import com.terracotta.followmateapp.helper.DatabaseHandler;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.helper.Utils;
import com.terracotta.followmateapp.pojo.ChatroomChatMessage;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class ChatroomChatFragment extends Fragment implements View.OnKeyListener {


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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            Log.e("setUserVisibleHint", "setUserVisibleHint CHAT");
            System.out.println("-------ChatFragment");
            mSessionManager = new SessionManager(getActivity());
            mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_CHAT_FRAGMENT);
            mSessionManager.putStringData(Constants.VISIBLE_FRAGMENT, "ChatFragment");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.sample_activity_chat, container, false);
        ButterKnife.bind(this, view);


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
        //for settinf menu for this fragment individually
        setHasOptionsMenu(true);
        mSessionManager = new SessionManager(getActivity());




        chatroomName = mSessionManager.getStringData("cName");
        chatroomId = mSessionManager.getStringData("chatroomid");

/* Get the singleton CometChat instance for use. */
        cometchat = CometChat.getInstance(getActivity(),
                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));

        messageField = (EditText) view.findViewById(R.id.editTextChatMessage);
        sendbtn = (Button) view.findViewById(R.id.buttonSendMessage);
        cometChatroom = CometChatroom.getInstance(getActivity());



        joinChatRoom();

        chatListView = (ListView) view.findViewById(R.id.listViewChatMessages);
        messageList = new ArrayList<>();
        DatabaseHandler helper = new DatabaseHandler(getActivity());
        Log.e("myId in Chatrrom chat Activity : ", SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId));
        Log.e("chatroom in Chatrrom chat Activity : ", chatroomId);
        messageList = helper.getAllChatroomMessageByChatroomID(Long.parseLong(chatroomId));
        // messageList = helper.getAllChatroomMessage(Long.parseLong(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId)), Long.parseLong(chatroomId));
        adapter = new ChatroomChatAdapter(getActivity(), messageList);
        chatListView.setAdapter(adapter);
        chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatListView.setStackFromBottom(true);

      /*  smilieyButton = (ImageButton) view.findViewById(R.id.buttonSendSmiley);
        stickerButton = (ImageButton) view.findViewById(R.id.buttonSendSticker);*/
      /*  smiliKeyBoard = new SmileyKeyBoard();
        smiliKeyBoard.enable(getActivity(), this, R.id.footer_for_emoticons, messageField);
        final RelativeLayout chatFooter = (RelativeLayout) view.findViewById(R.id.relativeBottomArea);
        smiliKeyBoard.checkKeyboardHeight(chatFooter);
        smiliKeyBoard.enableFooterView(messageField);

        stickerKeyboard = new StickerKeyboard();
        stickerKeyboard.enable(getActivity(), new StickerClickInterface() {
            @Override
            public void getClickedSticker(int gridviewItemPosition) {
                final String data = stickerKeyboard.getClickedSticker(gridviewItemPosition);
                cometChatroom.sendSticker(data, new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        try {
                            ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"), data,
                                    Utils.convertTimestampToDate(System.currentTimeMillis()), "Me :", true, "18",
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

                    }
                });
            }
        }, R.id.footer_for_emoticons, messageField);
        stickerKeyboard.checkKeyboardHeight(chatFooter);
        //StickerKeyboard.setStickerSize(400);
        stickerKeyboard.enableFooterView(messageField);*/

        sendbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

       /* smilieyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                smiliKeyBoard.showKeyboard(chatFooter);
            }
        });

        stickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickerKeyboard.showKeyboard(chatFooter);
            }
        });*/

        customReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

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


    public static ChatroomChatFragment newInstance(ChatFragment chatFragment) {
        mchatFragment = chatFragment;
        try {
            mListener = (OnFragmentInteractionListener) mchatFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(mchatFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        ChatroomChatFragment chatroomChatFragment = new ChatroomChatFragment();
        return chatroomChatFragment;
    }

/*
    @Override
    public void getClickedEmoji(int gridviewItemPosition) {
        smiliKeyBoard.getClickedEmoji(gridviewItemPosition);
    }*/

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
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_logout:
                getLogout();
                return true;

            case R.id.action_invite_user:
                // startActivity(new Intent(getActivity(), InviteUsersListActivity.class));

                mListener.onFragmentInteractionInviteUser();
                break;



            /*case R.id.action_send_image:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.desert);
                cometchat.sendImage(bitmap, String.valueOf(friendId), new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                    }

                    @Override
                    public void failCallback(JSONObject response) {
                        Toast.makeText(getActivity(), "Image send failed", Toast.LENGTH_SHORT).show();
                    }
                });
                break;*/
            case R.id.action_pick_image:

                joinChatRoom();
                Log.e("action_pick_image", "action_pick_image");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 111);
                break;

            case R.id.action_capture_photo:
                joinChatRoom();
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = Utils.getOutputMediaFile(1, false);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent1, 222);
                break;
            case R.id.action_capture_video:
                joinChatRoom();
                Intent intent2 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                fileUri = Utils.getOutputMediaFile(2, false);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                intent2.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 15000000); // 15 Mb
                startActivityForResult(intent2, 3);
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
                    cometchat.sendAudioFile(file, String.valueOf(friendId), new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            try {
                                Logger.error("success " + response);
                                SingleChatMessage newmessage = new SingleChatMessage(response.getString("id"), response
                                        .getString("original_file"), Utils.convertTimestampToDate(System
                                        .currentTimeMillis()), true, SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId),
                                        String.valueOf(friendId), "16", Keys.MessageTicks.sent);
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
                break;*/
            case R.id.action_share_video:
                Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent3.setType("video/*");
                startActivityForResult(intent3, 4);
                break;

   /*         case R.id.action_share_file:
                Intent ii = new Intent(Intent.ACTION_GET_CONTENT);
                ii.setType("File*//*");
                startActivityForResult(ii, 1234);
                break;*/
           /* case R.id.action_whiteboard:
                cometchat.sendWhiteBoardRequest(String.valueOf(friendId), new Callbacks() {
                    @Override
                    public void successCallback(JSONObject jsonObject) {
                        Log.e("abc", "url " + jsonObject);
                        try {
                            Log.e("abc", "response " + jsonObject);
                            Intent i = new Intent(getActivity(), WebviewActivity.class);
                            i.putExtra("webview_url", jsonObject.get("whiteboard_url").toString());
                            startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failCallback(JSONObject jsonObject) {

                    }
                });
            case R.id.action_writeboard:
                cometchat.sendWriteBoardRequest(String.valueOf(friendId), new Callbacks() {
                    @Override
                    public void successCallback(JSONObject jsonObject) {
                        try {
                            Log.e("abc", "response " + jsonObject);
                            Intent i = new Intent(getActivity(), WebviewActivity.class);
                            i.putExtra("webview_url", jsonObject.get("writeboard_url").toString());
                            startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failCallback(JSONObject jsonObject) {

                    }
                });
            case R.id.action_reportconversation:
                cometchat.reportConversation(String.valueOf(friendId), "This is reason", new Callbacks() {
                    @Override
                    public void successCallback(JSONObject jsonObject) {

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
                if (requestCode == 111) {

                    final String path = Utils.getPath(data.getData(), true);

//                    File sd = Environment.getExternalStorageDirectory();
//                    File image = new File(path);
//                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

                    File f = new File(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));


                    cometChatroom.sendImage(f, new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Log.e("share pick image send succes = ", path + response.toString());
                            try {
                                ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"),
                                        response.getString("original_file"), Utils.convertTimestampToDate(System
                                        .currentTimeMillis()), "Me :", true, "12", SharedPreferenceHelper
                                        .get(Keys.SharedPreferenceKeys.myId), chatroomId);
                                addMessage(newmessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failCallback(JSONObject response) {
                            Log.e("share pick image send fail = ", path + response.toString());
                        }
                    });

                } else if (requestCode == 222) {
                    // Uri selectedImageUri = data.getData();
                    File newfile = new File(new URI(fileUri.toString()));
                    String filePath = newfile.getAbsolutePath();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap image = BitmapFactory.decodeFile(filePath, options);
                    if (image != null) {
                        cometChatroom.sendImage(image, new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Log.e("capture pick image send succes = ", response.toString());
                                try {
                                    ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"),
                                            response.getString("original_file"), Utils.convertTimestampToDate(System
                                            .currentTimeMillis()), "Me :", true, "12", SharedPreferenceHelper
                                            .get(Keys.SharedPreferenceKeys.myId), chatroomId);
                                    addMessage(newmessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Log.e("capture pick image send fail = ", response.toString());

                            }
                        });
                    }
                } else if (requestCode == 3) {
                    try {
                        File newfile = new File(new URI(fileUri.toString()));
                        cometChatroom.sendVideo(newfile, new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Log.e("video send succes = ", response.toString());
                                try {
                                    ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"),
                                            response.getString("original_file"), Utils.convertTimestampToDate(System
                                            .currentTimeMillis()), "Me :", true, "14", SharedPreferenceHelper
                                            .get(Keys.SharedPreferenceKeys.myId), chatroomId);
                                    addMessage(newmessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Log.e("video send succes = ", response.toString());
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
                                try {
                                    ChatroomChatMessage newmessage = new ChatroomChatMessage(response.getString("id"),
                                            response.getString("original_file"), Utils.convertTimestampToDate(System
                                            .currentTimeMillis()), "Me :", true, "14", SharedPreferenceHelper
                                            .get(Keys.SharedPreferenceKeys.myId), chatroomId);
                                    addMessage(newmessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
        if (customReceiver != null) {
            getActivity().registerReceiver(customReceiver, new IntentFilter("Chatroom_message"));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (customReceiver != null) {
            getActivity().unregisterReceiver(customReceiver);
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //Do the Stuf only if fragment is visible
            Log.e("##########", "##########" + "ChatFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    void getLogout() {

        //lockScreenOrientation();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Constants.URL_LOGOUT;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Logout...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String respons) {

                Log.e(TAG, "Service--o/p-" + respons);
                JSONArray jarray;
                JSONObject job;

                try {
                    //unlockScreenOrientation();
                    job = new JSONObject(respons);
                    if (job.get("message").equals("success")) {
                        mSessionManager.Logout(getActivity());
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Some error occured while logout", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                Log.e(TAG, "URL: " + Constants.URL_LOGOUT +
                        " user_id: " + mSessionManager.getStringData(Constants.USER_ID));
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
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


    //method for lock  screen
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }

    //method for unlock  screen
    private void unlockScreenOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteractionInviteUser();

        public void onFragmentInteractionOpenGroupListChat();

    }

    private void joinChatRoom() {
        cometChatroom.joinChatroom(mSessionManager.getStringData("chatroomid"), mSessionManager.getStringData("cName"),
                "", new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        Log.e("Joined the chatroom in invite", "Joined the chatroom  in invite" + response);


                    }

                    @Override
                    public void failCallback(JSONObject response) {
                        Log.e("Joined the chatroom in invite", "Joined the chatroom fail in invite" + response.toString());
                    }
                });

    }
}


// Original revrted code