package com.terracotta.followmateapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.dialog.MessageFragmentDialog;
import com.terracotta.followmateapp.utility.AndroidUtils;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author Avion Team.
 * @Date 27/7/16.
 * @SignUpActivity Creates new user's account.
 */
public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Bind(R.id.imageview_back)
    ImageView imageview_back;
    @Bind(R.id.imageview_profile)
    ImageView imageview_profile;
    @Bind(R.id.edittext_email)
    EditText editText_email;
    @Bind(R.id.edittext_name)
    EditText editText_name;
    @Bind(R.id.edittext_contactnumber)
    EditText editText_contactnumber;
    @Bind(R.id.edittext_password)
    EditText editText_password;
    @Bind(R.id.edittext_confirmpassword)
    EditText editText_confirmpassword;
    @Bind(R.id.button_create_account)
    Button button_createaccount;
    @Bind(R.id.Spinner_country_codes)
    Spinner Spinner_country_codes;

    SessionManager mSessionManager;

    //  ArrayList<String> CountryCode = new ArrayList<String>();
    String mImagepath;
    private Uri imageUri;
    MessageFragmentDialog messageFragmentDialog;

    String TAG = "FollowMate";
    String message = null;
    Boolean ISPROFILECHANGED = false;
    //Bitmap FinalProfileImage = null;
    byte[] image;
    //for image of base 64
    String strBase64 = "";
    String CountryCodestr;

    private AdView mAdView;
    String strMobileNumber1, strMobileNumber2;

    //font
    Typeface roboto;


    public static Bitmap decodeSampledBitmapFromResource(String pathToFile,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToFile, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathToFile, options);
    }


    private void setCountryCode() {


        // TODO Auto-generated method stub


        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, Constants.Codes);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.row_spinner_item);

        // attaching data adapter to spinner
        Spinner_country_codes.setAdapter(dataAdapter);
        Spinner_country_codes.setOnItemSelectedListener(SignUpActivity.this);


    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mSessionManager = new SessionManager(this);
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_SIGNUP);

        setFont();
        setCountryCode();

        // getPhoneNumber();


        if (Constants.ISDIALOGOPEN) {

            // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
            messageFragmentDialog = new MessageFragmentDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageFragmentDialog.show(getFragmentManager(), "dialog");
            messageFragmentDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, SignUpActivity.this.getClass().getSimpleName());
        }


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("359938694B6EDFD2B7F3F2E49AC3DEF6")
                .addTestDevice("2F1D92A5D28200D604CE11DDC734867A")
                .build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                //Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);


    }

    /*private void getPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);


        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        boolean isDualSIM = telephonyInfo.isDualSIM();
        Log.i("IS DUAL SIM : ", " IS DUAL SIM : " + isDualSIM);


        if (isDualSIM) {
            strMobileNumber1 = telephonyManager.getLine1Number();

            Log.e("strMobileNumber1", "strMobileNumber1 " + strMobileNumber1);
            //strMobileNumber2 = telephonyManager.getLine2Number();
        } else {
            strMobileNumber1 = telephonyManager.getLine1Number();
            Log.e("strMobileNumber1", "strMobileNumber1 " + strMobileNumber1);
        }
    }*/


    private void setFont() {
        //font
        roboto = Typeface
                .createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");


        editText_email.setTypeface(roboto);
        editText_name.setTypeface(roboto);
        editText_contactnumber.setTypeface(roboto);
        editText_password.setTypeface(roboto);
        editText_confirmpassword.setTypeface(roboto);
        button_createaccount.setTypeface(roboto);
    }

    @OnClick(R.id.imageview_back)
    public void onback() {
        Constants.FinalProfileImage=null;
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
        finish();
    }

    @OnClick(R.id.button_create_account)
    public void createAccount() {


        if (isValid()) {

            if (ISPROFILECHANGED) {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Constants.FinalProfileImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    image = stream.toByteArray();
                    strBase64 = Base64.encodeToString(image, 0);
                    Log.e("###strBase64", "###strBase64" + strBase64);
                } catch (Exception e) {
                    ISPROFILECHANGED = false;
                }
            }

            checkMobileExist();


        }
    }

    private void checkMobileExist() {


       // lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CHECK_PHNO_EXIST;

        final ProgressDialog pDialog = new ProgressDialog(SignUpActivity.this);

        pDialog.setMessage("Checking mobile number...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;


                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);

                    String user_email = null, user_pass = null, user_name = null, user_id = null, status = null, user_mobile = null, user_profile = null;

                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        //exist
                        message = "Mobile number Already Exist";


                        // Toast.makeText(SignUpActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, SignUpActivity.this.getClass().getSimpleName());
                        pDialog.dismiss();
                        //unlockScreenOrientation();


                    } else {
                        //not exist
                        pDialog.dismiss();

                        // do not uncomment it : pratibha had comment this
                       // unlockScreenOrientation();
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                        Constants.FinalProfileImage=null;
                        callRegisterService();


                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                //unlockScreenOrientation();
                Constants.ISDIALOGOPENPH = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                Toast.makeText(SignUpActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("mobile", editText_contactnumber.getText().toString().trim());


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
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);

    }


    @OnClick(R.id.imageview_profile)
    public void selectProfileImage() {


        {
            if (!checkforcamera()) {
                requestPermissionCamera();
            } else {
                if (!checkforExternalStorage()) {
                    requestPermissionExternalStorage();
                } else {


                    showImageDialog();
                }
            }
        }


    }

    private void showImageDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
            if (options[item].equals("Take Photo")) {
                captureImage();
            } else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
            }
        });
        builder.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(Constants.FinalProfileImage!=null) {

            imageview_profile.setBackground(null);
            imageview_profile.setImageBitmap(Constants.FinalProfileImage);}

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    private boolean isValid() {
        editText_email.setError(null);
        editText_name.setError(null);
        editText_contactnumber.setError(null);
        editText_password.setError(null);
        editText_confirmpassword.setError(null);
        if (editText_name.getText().toString().length() == 0) {
            editText_name.setError(getString(R.string.error_name));
            editText_name.requestFocus();
            return false;
        } else if (!editText_name.getText().toString()
                .matches("[a-zA-Z\\s]+")) {
            editText_name.setError(getString(R.string.error_name_invalid));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().length() == 0) {
            editText_email.setError(getString(R.string.error_email));
            editText_email.requestFocus();
            return false;
        } else if (AndroidUtils.isEmailValid(editText_email.getText().toString()
                .trim()) == false) {
            editText_email.setError(getString(R.string.error_email));
            editText_email.requestFocus();
            return false;
        } else if (editText_contactnumber.getText().toString().length() != 10) {
            editText_contactnumber.setError(getString(R.string.error_contact));
            editText_contactnumber.requestFocus();
            return false;
        } else if (editText_password.getText().length() == 0) {
            editText_password.setError(getString(R.string.error_password));
            editText_password.requestFocus();
            return false;
        } else if (editText_password.getText().length() < 4) {
            editText_password.setError(getString(R.string.error_password_minlength));
            editText_password.requestFocus();
            return false;
        } else if (editText_password.getText().length() > 15) {
            editText_password.setError(getString(R.string.error_password_length));
            editText_password.requestFocus();
            return false;
        } else if (editText_confirmpassword.getText().length() == 0) {
            editText_confirmpassword.setError(getString(R.string.error_confirmpassword));
            editText_confirmpassword.requestFocus();
            return false;
        } else if (editText_confirmpassword.getText().length() < 4) {
            editText_confirmpassword.setError(getString(R.string.error_password_minlength));
            editText_confirmpassword.requestFocus();
            return false;
        } else if (!editText_confirmpassword.getText().toString().trim().equals(editText_password.getText().toString())) {
            editText_confirmpassword.setError(getString(R.string.error_confirmpassword_missmatch));
            editText_confirmpassword.requestFocus();
            return false;
        }
        return true;
    }

    private void captureImage() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String root_sd = Environment.getExternalStorageDirectory()
                .toString();
        File direct = new File(root_sd + "/Followmate");
        if (!direct.exists()) {
            if (!direct.mkdir()) {
                System.out.println("Followmate folder not created");
            } else {
                System.out.println("Followmate folder created");
            }
        }
        Date d = new Date();
        CharSequence s = DateFormat.format("MM-dd-yy-hh-mm-ss",
                d.getTime());
        File photo = new File(Environment.getExternalStorageDirectory()
                + "/Followmate/"
                + s.toString() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        Constants.imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        try {
                            ISPROFILECHANGED = true;

                            Uri selectedImage = data.getData();
                            String[] filePath = {MediaStore.Images.Media.DATA};
                            Cursor c = getContentResolver().query(
                                    selectedImage, filePath, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePath[0]);
                            String picturePath = c.getString(columnIndex);
                            c.close();
                            Bitmap thumbnail = decodeSampledBitmapFromResource(
                                    picturePath, 500, 500);

                            Constants.FinalProfileImage = thumbnail;

                            Log.e("thumbnail", "thumbnail" + thumbnail);


                            if (thumbnail!=null || !thumbnail.equals("") || !thumbnail.equals("null")) {
                                imageview_profile.setBackground(null);
                                imageview_profile.setImageBitmap(thumbnail);
                            } else {
                                imageview_profile.setImageResource(R.mipmap.default_pic);
                                ISPROFILECHANGED = false;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 1:
                Uri selectedImage = Constants.imageUri;
                if (selectedImage != null) {
                    getContentResolver().notifyChange(selectedImage, null);

                    try {
                        ISPROFILECHANGED = true;
                        mImagepath = selectedImage.toString();
                        mImagepath = mImagepath.replace("file:///", "/");
                        File f = new File(mImagepath);
                        String filename = f.getName();
                        String[] separatedfilenamelastdot = filename.split("\\.");
                        String ext = separatedfilenamelastdot[1];
                        if (ext.equalsIgnoreCase("png")
                                || ext.equalsIgnoreCase("jpeg")
                                || ext.equalsIgnoreCase("jpg")
                                || ext.equalsIgnoreCase("gif")) {
                            File image_file = new File(mImagepath);
                            Bitmap thumbnail = decodeFile(image_file);
                            Log.e("thumbnail", "thumbnail" + thumbnail);
                            Constants.FinalProfileImage = thumbnail;


                            if (!thumbnail.equals("") || !thumbnail.equals("null")) {
                                imageview_profile.setImageBitmap(thumbnail);
                            } else {
                                imageview_profile.setImageResource(R.mipmap.default_pic);
                                ISPROFILECHANGED = false;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Camera--" + e);
                    }
                }
                break;
        }

    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            final int REQUIRED_SIZE = 70;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, o2);
            return bitmap;
        } catch (FileNotFoundException e) {
        }
        return null;
    }


    //marshmellow permission for camera
    private boolean checkforcamera() {


        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkforcamera true", "checkforcamera true");
            return true;


        } else {
            Log.e("checkforcamera false", "checkforcamera false");
            return false;

        }
    }

    //marshmellow permission for External storage required for camera
    private boolean checkforExternalStorage() {


        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkExterStorage true", "checkforExternalStorage true");
            return true;


        } else {
            Log.e("checkExteStorage false", "checkforExternalStorage false");
            return false;

        }
    }

    //request permission for camera if not granted already
    private void requestPermissionCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            Toast.makeText(this, "Please allow permissions in App Settings for additional functionality like camera, GPS and External Storage.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 3);
        }

    }


    //request permission for ExternalStorage if not granted already
    private void requestPermissionExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Toast.makeText(this, "Please allow permissions in App Settings for additional functionality like camera, GPS and External Storage.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

    }


    //Webservice to register user
    void callRegisterService() {
        // do not uncomment it : pratibha had comment this
        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_SIGNUP;

        final ProgressDialog pDialog = new ProgressDialog(SignUpActivity.this);

        pDialog.setMessage("Registering...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String status = null, user_id = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");

                    if (ISSucess.equals("1")) {
                        message = job.getString("message");
                        JSONArray UserData = job.getJSONArray("data");


                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");

                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, editText_name.getText().toString().trim());
                            mSessionManager.putStringData(Constants.USER_EMAIL, editText_email.getText().toString().trim());
                            mSessionManager.putStringData(Constants.USER_PHNO, editText_contactnumber.getText().toString().trim());
                        }

                        //unlockScreenOrientation();
                        Intent verification = new Intent(SignUpActivity.this, VerificationActivity.class);
                        verification.putExtra("message", message);
                        startActivity(verification);
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } else {
                        message = job.getString("message");

                        //   Toast.makeText(SignUpActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);

                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, SignUpActivity.this.getClass().getSimpleName());
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //unlockScreenOrientation();
               // Constants.ISDIALOGOPEN = false;
               // mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
               // mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                Toast.makeText(SignUpActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_name", editText_name.getText().toString().trim());
                params.put("user_email", editText_email.getText().toString().trim());
                params.put("user_mobile", editText_contactnumber.getText().toString().trim());
                params.put("user_password", editText_password.getText().toString().trim());
                params.put("user_reg_type", "1");//normal registration
                params.put("user_photo", strBase64);
                params.put("country_code", CountryCodestr);


                Log.e(TAG, "URL: " + Constants.URL_SIGNUP + " user_name: " + editText_name.getText().toString().trim() + " user_email: " +
                        editText_email.getText().toString().trim() + " user_mobile " + editText_contactnumber.getText().toString().trim() +
                        " user_password: " + editText_password.getText().toString().trim() + " user_reg_type: " + "1"
                        + " user_photo: " + strBase64 + " country_code: " + CountryCodestr);

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
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
    }


    //method for lock  screen
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }


       /* final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_90:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            case Surface.ROTATION_180:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }*/
    }

    //method for unlock  screen
    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        CountryCodestr = adapterView.getItemAtPosition(pos).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public synchronized void onBackPressed() {
        //super.onBackPressed();
        Constants.FinalProfileImage=null;
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();

    }


}
