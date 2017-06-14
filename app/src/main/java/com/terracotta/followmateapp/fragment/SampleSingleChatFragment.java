package com.terracotta.followmateapp.fragment;

import android.app.Activity;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ImageButton;
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
import com.inscripts.Keyboards.StickerKeyboard;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.EmojiClickInterface;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.adapter.SingleChatAdapter;
import com.terracotta.followmateapp.helper.DatabaseHandler;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.helper.Utils;
import com.terracotta.followmateapp.pojo.SingleChatMessage;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SampleSingleChatFragment extends Fragment implements EmojiClickInterface,View.OnKeyListener {


    private View view;
    String TAG = "FollowMate";
    SessionManager mSessionManager;


    private long friendId;
    private String friendName, channel;
    private ListView listview;
    private EditText messageField;
    private Button sendButton;
    private ArrayList<SingleChatMessage> messages;
    private SingleChatAdapter adapter;
    private CometChat cometchat;
    private BroadcastReceiver receiver;
    private static Uri fileUri;
    private boolean flag = true;
   // private SmileyKeyBoard smiliKeyBoard;
    private ImageButton smilieyButton, stickerButton;
    private DatabaseHandler dbhelper;
    private StickerKeyboard stickerKeyboard;


    static OnFragmentInteractionListener mListener;
    static ChatFragment mchatFragment;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            Log.e("setUserVisibleHint", "setUserVisibleHint CHAT");
            System.out.println("-------ChatFragment");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for getActivity() fragment
        view = inflater.inflate(R.layout.sample_activity_chat, container, false);
        // ButterKnife.bind(getActivity(), view);
        //for settinf menu for getActivity() fragment individually
        setHasOptionsMenu(true);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);

        mSessionManager = new SessionManager(getActivity());
       /* Intent intent = getIntent();
        friendId = intent.getLongExtra("user_id", 0);
        friendName = intent.getStringExtra("user_name");
        channel = intent.getStringExtra("channel");*/

        friendId = mSessionManager.getLongData("user_id_CometChat");
        friendName = mSessionManager.getStringData("user_name_CometChat");
        channel = mSessionManager.getStringData("channel_CometChat");

		/* Get the singleton CometChat instance for use. */
        cometchat = CometChat.getInstance(getActivity(),
                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));

        //getSupportActionBar().setTitle("Chat with " + friendName);
        messages = new ArrayList<>();

        listview = (ListView) view.findViewById(R.id.listViewChatMessages);
        dbhelper = new DatabaseHandler(getActivity());
//        Log.e("My ID ----->" , SharedPreferenceHelper.get(SharedPreferenceKeys.myId));

        Log.e("SampleSingleChatActivity", "SampleSingleChatActivity get data");
        Log.e("myid---------->", String.valueOf(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId)));
        messages = dbhelper.getAllMessages(Long.parseLong(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId)), friendId);
        adapter = new SingleChatAdapter(getActivity(), messages);
        listview.setAdapter(adapter);

        messageField = (EditText) view.findViewById(R.id.editTextChatMessage);
        sendButton = (Button) view.findViewById(R.id.buttonSendMessage);
/*
        smilieyButton = (ImageButton) view.findViewById(R.id.buttonSendSmiley);
        stickerButton = (ImageButton) view.findViewById(R.id.buttonSendSticker);
        smiliKeyBoard = new SmileyKeyBoard();
        smiliKeyBoard.enable(getActivity(), this, R.id.footer_for_emoticons, messageField);
        final RelativeLayout chatFooter = (RelativeLayout) view.findViewById(R.id.relativeBottomArea);
        smiliKeyBoard.checkKeyboardHeight(chatFooter);
        smiliKeyBoard.enableFooterView(messageField);*/
/*
        stickerKeyboard = new StickerKeyboard();
        stickerKeyboard.enable(getActivity(), new StickerClickInterface() {
            @Override
            public void getClickedSticker(int gridviewItemPosition) {

                Log.e("SampleSingleChatActivity stickerKeyboard", "SampleSingleChatActivity stickerKeyboard");

                final String data = stickerKeyboard.getClickedSticker(gridviewItemPosition);
                cometchat.sendSticker(data, String.valueOf(friendId), new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        try {
                            SingleChatMessage newmessage = new SingleChatMessage(response.getString("id"), data, Utils
                                    .convertTimestampToDate(System.currentTimeMillis()), true, SharedPreferenceHelper
                                    .get(Keys.SharedPreferenceKeys.myId), String.valueOf(friendId), "18",
                                    Keys.MessageTicks.sent);

                            addMessage(newmessage);
                            dbhelper.insertOneOnOneMessage(newmessage);
                            Log.e("stickerKeyboard : successCallback", newmessage.toString());
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

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final String message = messageField.getText().toString().trim();
                if (message.length() > 0) {
                    messageField.setText("");

                    Log.e("SampleSingleChatActivity sendButton", "SampleSingleChatActivity sendButton");

					/* Send a message to the current user */
                    cometchat.sendMessage(String.valueOf(friendId), message, new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            try {
                                SingleChatMessage newmessage = new SingleChatMessage(response.getString("id"), message,
                                        Utils.convertTimestampToDate(System.currentTimeMillis()), true,
                                        SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId),
                                        String.valueOf(friendId), "10", 1);
                                if (Utils.msgtoTickList.containsKey(response.getString("id"))) {
                                    newmessage.setTickStatus(Utils.msgtoTickList.get(response.getString("id")));
                                    Utils.msgtoTickList.remove(response.getString("id"));
                                }
                                addMessage(newmessage);
                                dbhelper.insertOneOnOneMessage(newmessage);
                                Log.e("sendButton : successCallback", newmessage.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failCallback(JSONObject response) {
                            Toast.makeText(getActivity(), "Error in sending message", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Blank message", Toast.LENGTH_SHORT).show();
                }
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


		/* Receiver for updating messages. */
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("action")) {
                    String action = intent.getStringExtra("action");
                   /* ActionBar ab = getSupportActionBar();
                    if (action.equals("typing_start")) {
                        ab.setSubtitle("typing...");
                    } else if (action.equals("typing_stop")) {
                        ab.setSubtitle("");
                    } else*/
                    if (action.equals("message_deliverd")) {
                        String from = intent.getStringExtra("from");
                        String message_id = intent.getStringExtra("message_id");
                        if (dbhelper != null) {
                            // SingleChatMessage msg = dbhelper.getMessageDetails(message_id);
                            for (SingleChatMessage msg : messages) {
                                if (msg.getMessageId().equals(message_id)) {
                                    msg.setTickStatus(Keys.MessageTicks.deliverd);
                                    dbhelper.updateMessageDetails(msg);
                                    adapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                        }
                    } else if (action.equals("message_read")) {
                        String from = intent.getStringExtra("from");
                        String message_id = intent.getStringExtra("message_id");
                        for (SingleChatMessage msg : messages) {
                            if (message_id.equals("0")) {
                                // Message id 0 means mark all the message you sent as read
                                if (msg.getIsMyMessage()) {
                                    if (msg.getTickStatus() != Keys.MessageTicks.read) {
                                        msg.setTickStatus(Keys.MessageTicks.read);
                                        dbhelper.updateMessageDetails(msg);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                if (msg.getMessageId().equals(message_id)) {
                                    if (msg.getIsMyMessage()) {
                                        msg.setTickStatus(Keys.MessageTicks.read);
                                        dbhelper.updateMessageDetails(msg);
                                        adapter.notifyDataSetChanged();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    int senderId = intent.getIntExtra("user_id", 0);
                    String messageId = intent.getStringExtra("message_id");
                    String from = intent.getStringExtra("from");
                    String to = intent.getStringExtra("to");
                    String messagetype = intent.getStringExtra("message_type");
                    String time = Utils.convertTimestampToDate(Utils.correctTimestamp(Long.parseLong(intent
                            .getStringExtra("time"))));
                    SingleChatMessage newMessage = null;
                    if (0 != senderId && senderId == friendId) {

                        String message = intent.getStringExtra("message");
                        newMessage = new SingleChatMessage(messageId, message, time, false, from, to, messagetype,
                                Keys.MessageTicks.notick);
                    } else if (intent.hasExtra("myphoto")) {
                        String message = intent.getStringExtra("message");
                        newMessage = new SingleChatMessage(messageId, message, time, true, from, to, messagetype,
                                Keys.MessageTicks.sent);
                    } else if (intent.hasExtra("myVideo")) {
                        String message = intent.getStringExtra("message");
                        newMessage = new SingleChatMessage(messageId, message, time, true, from, to, messagetype,
                                Keys.MessageTicks.sent);
                    }
                    cometchat.sendReadReceipt(messageId, channel, new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {

                        }

                        @Override
                        public void failCallback(JSONObject response) {
                        }
                    });

                    if (newMessage != null) {
                        addMessage(newMessage);
                    }
                }
            }
        };

        flag = true;
        messageField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (flag) {
                    flag = false;
                    try {
                        cometchat.isTyping(true, channel, new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                //Log.e("abc", "typing fail " + response);
                            }
                        });
                        Timer timer = new Timer();

						/* Send stop typing message after 5 seconds */
                        timer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                cometchat.isTyping(false, channel, new Callbacks() {

                                    @Override
                                    public void successCallback(JSONObject response) {

                                    }

                                    @Override
                                    public void failCallback(JSONObject response) {
                                    }
                                });
                                flag = true;
                            }
                        }, 5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        /*
         * Send read receipt when chat window is open, pass 0 to mark all messages as read
		 * */
        if (messages.size() > 0) {
            cometchat.sendReadReceipt("0", channel, new Callbacks() {

                @Override
                public void successCallback(JSONObject response) {

                }

                @Override
                public void failCallback(JSONObject response) {

                }
            });
        }

        listview.setSelection(adapter.getCount() - 1);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.single_chat, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_logout:
                getLogout();
                return true;

         /*   case R.id.action_audio_call_user:
                AudioChat audio = AudioChat.getInstance(getActivity());
                audio.sendAudioChatRequest(String.valueOf(friendId), new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        Intent i = new Intent(getActivity(), SampleOutgoiningCallActivity.class);
                        i.putExtra("user_id", String.valueOf(friendId));
                        i.putExtra("pluginType", 0);
                        startActivity(i);
                    }

                    @Override
                    public void failCallback(JSONObject response) {

                    }
                });

                break;
            case R.id.action_call_user:
                AVChat a = AVChat.getAVChatInstance(getActivity());
                a.sendAVChatRequest(String.valueOf(friendId), new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        Intent i = new Intent(getActivity(), SampleOutgoiningCallActivity.class);
                        i.putExtra("user_id", String.valueOf(friendId));
                        startActivity(i);
                    }

                    @Override
                    public void failCallback(JSONObject response) {
                    }
                });
                break;
            case R.id.action_start_broadcast:
                AVBroadcast broadcast = AVBroadcast.getAVBroadcastInstance(getActivity());
                broadcast.sendAVBroadcastRequest(String.valueOf(friendId), new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        try {
                            Logger.error("broadcast message room " + response);
                            Intent i = new Intent(getActivity(), AVChatActivity.class);
                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.CALLID, response.getString("callid"));
                            i.putExtra("user_id", String.valueOf(friendId));
                            i.putExtra("pluginType", 2); // 2 is for AVbroadcast
                            i.putExtra("iamBroadcaster", true);
                            startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failCallback(JSONObject response) {

                    }
                });
                break;
            case R.id.action_block_user:
                cometchat.blockUser(String.valueOf(friendId), new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        getActivity().finish();
                    }

                    @Override
                    public void failCallback(JSONObject response) {
                    }
                });
                break;*/
          /*  case R.id.action_send_image:
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
          /*  case R.id.action_share_audio:
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

            /*case R.id.action_share_file:
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


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //Do the Stuf only if fragment is visible
            Log.e("##########", "##########" + "ChatFragment");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiver, new IntentFilter("NEW_SINGLE_MESSAGE"));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == 1) {
                    String path = Utils.getPath(data.getData(), true);

                    cometchat.sendImage(new File(path), String.valueOf(friendId), new Callbacks() {

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
                    // Uri selectedImageUri = data.getData();
                    File newfile = new File(new URI(fileUri.toString()));
                    String filePath = newfile.getAbsolutePath();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap image = BitmapFactory.decodeFile(filePath, options);
                    if (image != null) {
                        cometchat.sendImage(image, String.valueOf(friendId), new Callbacks() {

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
                        cometchat.sendVideo(newfile, String.valueOf(friendId), new Callbacks() {

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
                        cometchat.sendVideo(path, String.valueOf(friendId), new Callbacks() {

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
                        cometchat.sendFile(f, String.valueOf(friendId), new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.error("success " + response);
                                try {
                                    SingleChatMessage newmessage = new SingleChatMessage(response.getString("id"),
                                            response.getString("original_file"), Utils.convertTimestampToDate(System
                                            .currentTimeMillis()), true, SharedPreferenceHelper
                                            .get(Keys.SharedPreferenceKeys.myId), String.valueOf(friendId), "17",
                                            Keys.MessageTicks.sent);
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
    public void getClickedEmoji(int gridviewItemPosition) {
       // smiliKeyBoard.getClickedEmoji(gridviewItemPosition);
    }

    public void addMessage(SingleChatMessage newMessage) {
        boolean duplicate = false;
        for (SingleChatMessage message : messages) {
            if (message.getMessageId().equals(newMessage.getMessageId())) {
                duplicate = true;
                break;
            }
        }
        if (!duplicate) {
            messages.add(newMessage);
            adapter.notifyDataSetChanged();
            listview.setSelection(adapter.getCount() - 1);
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


    public static SampleSingleChatFragment newInstance(ChatFragment chatFragment) {
        mchatFragment = chatFragment;
        try {
            mListener = (OnFragmentInteractionListener) mchatFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(mchatFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        SampleSingleChatFragment sampleSingleChatFragment = new SampleSingleChatFragment();
        return sampleSingleChatFragment;
    }

    public SampleSingleChatFragment() {
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
        mListener.onFragmentInteraction();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();
    }
}