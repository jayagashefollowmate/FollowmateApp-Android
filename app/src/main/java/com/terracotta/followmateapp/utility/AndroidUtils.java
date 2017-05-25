package com.terracotta.followmateapp.utility;

import android.content.Context;
import android.graphics.Color;

import com.terracotta.followmateapp.SplashActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @Author Avion Team.
 * @Date 27/7/16.
 * @AndroidUtils Provides extra stuff i.e validation, string manipulation, dialogs
 */

public class AndroidUtils {
    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void showErrorDialog(String str, final SplashActivity splashActivity) {
        SweetAlertDialog errorDialog = new SweetAlertDialog(splashActivity, SweetAlertDialog.ERROR_TYPE);
        errorDialog.getProgressHelper().setBarColor(Color.parseColor("#FF4081"));
        errorDialog.setTitleText("Error");
        errorDialog.setContentText(str);
        errorDialog.setCancelable(true);
        errorDialog.show();

        errorDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                splashActivity.finish();
            }
        });
    }





}
