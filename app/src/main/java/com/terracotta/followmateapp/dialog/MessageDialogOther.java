package com.terracotta.followmateapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.subfragment.AddSprintFollowOtherFragment;

/**
 * Created by aspl31 on 3/10/16.
 */
public class MessageDialogOther extends android.app.DialogFragment {

    String message = " ";
    int messageID = 0;
    SessionManager mSessionManager;
    //font
    Typeface roboto;
    OnMessageDialogInteraction onMessageDialogInteraction;
    String WhichDialog;

    public MessageDialogOther(String message, AddSprintFollowOtherFragment addSprintFollowOtherFragment, String WhichDialog) {
        this.message = message;
        this.WhichDialog = WhichDialog;
        onMessageDialogInteraction = (OnMessageDialogInteraction) addSprintFollowOtherFragment;
    }

    public MessageDialogOther() {

    }

    public MessageDialogOther(int messageID) {
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
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (WhichDialog.equals("Add")) {
                    Constants.ISDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                    onMessageDialogInteraction.onOkClick();
                    dismiss();
                    //GoToList();
                }else{
                    Constants.ISDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                    dismiss();
                }
            }
        });
        return builder.create();
    }


    public interface OnMessageDialogInteraction {
        void onOkClick();
    }
}
