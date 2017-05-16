package com.terracotta.followmateapp.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;


//Developement team:               Mobility Team
//Creation Date:                   8th  2016
//Information                      Dialog with OK button,used to show any message.
// Comment:


@SuppressLint("ValidFragment")
public class MessageFragmentDialog extends DialogFragment {

    String message = " ";
    int messageID = 0;
    SessionManager mSessionManager;
    //font
    static Typeface roboto;

    public MessageFragmentDialog(String message) {
        this.message = message;
    }

    public MessageFragmentDialog() {

    }

    public MessageFragmentDialog(int messageID) {
        this.messageID = messageID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View convertview = inflater.inflate(R.layout.dialog_message_box, null);
        mSessionManager = new SessionManager(getActivity());


        TextView textmessage = (TextView) convertview
                .findViewById(R.id.textView_dialog);
        //font
        roboto = Typeface
                .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        textmessage.setTypeface(roboto);


        if (message.equalsIgnoreCase(" ")) {
            //textmessage.setText(messageID);
            textmessage.setText(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
        } else {
            textmessage.setText(message);
        }
        Button button_ok = (Button) convertview
                .findViewById(R.id.button_dialog);
        button_ok.setTypeface(roboto);

        builder.setView(convertview);
        button_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                dismiss();

            }
        });


        return builder.create();

    }
}
