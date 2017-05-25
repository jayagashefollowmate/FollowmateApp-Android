package com.terracotta.followmateapp.librarytestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.enums.ChatroomType;
import com.inscripts.interfaces.Callbacks;
import com.terracotta.followmateapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateChatRoomActivity extends AppCompatActivity {


    EditText edtChatroomName;
    private static CometChatroom cometChatroom;
    Button btnCreate, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);

        cometChatroom = CometChatroom.getInstance(getApplicationContext());
        edtChatroomName = (EditText) findViewById(R.id.edtChatroomName);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCreate = (Button) findViewById(R.id.btnCreate);


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtChatroomName.getText().toString().trim().equals("")) {
                    Toast.makeText(CreateChatRoomActivity.this, "Please Enter Group name", Toast.LENGTH_LONG).show();
                } else {
                    createChatRoom();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void createChatRoom() {


        cometChatroom.createChatroom(edtChatroomName.getText().toString(), "", ChatroomType.INVITE_ONLY, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                Log.e("successCallback createChatroom --> ", response.toString());
                Toast.makeText(getApplicationContext(), "Group Created Successfully", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void failCallback(JSONObject response) {
                Log.e("failCallback createChatroom --> ", response.toString());
                String message = null;
                try {
                     message=response.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_LONG).show();

            }
        });

    }
}
