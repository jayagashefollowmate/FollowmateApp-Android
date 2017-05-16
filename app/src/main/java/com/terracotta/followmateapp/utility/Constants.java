package com.terracotta.followmateapp.utility;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.terracotta.followmateapp.fragment.MapMainFragment;
import com.terracotta.followmateapp.model.ContactListMeModel;
import com.terracotta.followmateapp.model.ContactListOtherModel;
import com.terracotta.followmateapp.model.MarkerPointsModel;

import java.util.ArrayList;

/**
 * Created by aspl37 on 4/8/16.
 */
public class Constants {
    public static final String ADDSPRINT_FOLLOWME = "ADDSPRINT_FOLLOWME";
    public static final String ADDSPRINT_FOLLOWOTHER = "ADDSPRINT_FOLLOWOTHER";

    public static final String KEY_REMEMBER_ME = "rememberMe";
    public static final String LOGEDIN = "Logedin";

    public static final String SIM_SERIAL_NO = "sim_serial_no";


    //public static final String BASE_URL = "http://192.168.1.24/follow_mate/app/";
    //public static final String BASE_URL = "http://174.141.234.198/~followmate/app/";

    /*public static final String BASE_URL = "http://server.ashoresystems.com/~followmate/app/";
    public static final String BASE_URL_IMAGE = "http://174.141.234.198/~followmate/";*/


    public static final String BASE_URL = "http://followmate.com.au/app/";
    public static final String BASE_URL_IMAGE = "http://followmate.com.au/app/";

    public static final String URL_LOGIN = BASE_URL + "user_sing_in";
    public static final String URL_GET_ACTIVE_USERS = BASE_URL + "get_active_users";
    public static final String URL_USER_STATUS = BASE_URL + "read_user_status";
    public static final String URL_SIGNUP = BASE_URL + "user_sing_up";
    public static final String URL_FORGETPASS = BASE_URL + "forgot_password";
    public static final String URL_VERIFY = BASE_URL + "user_verification";
    public static final String URL_CHECK_EMAIL_EXIST = BASE_URL + "check_email_exists";
    public static final String URL_UPDATE_PASS = BASE_URL + "update_user_pwd";
    public static final String URL_CHANGE_PASS = BASE_URL + "change_existing_password";
    public static final String URL_UPDATE_PROFILE = BASE_URL + "update_user_profile";
    public static final String URL_ADD_SPRINT = BASE_URL + "follow_me_data";
    public static final String URL_UPDATE_SPRINT = BASE_URL + "edit_sprint";
    public static final String URL_SPRINT_LIST = BASE_URL + "get_list_user_follow_me_data";
    public static final String URL_SPRINT_LIST_FOLLOWER = BASE_URL + "get_list_user_follower_data";
    public static final String URL_CHECK_PHNO_EXIST = BASE_URL + "check_phone_exists";
    public static final String URL_UPDATE_STATUS = BASE_URL + "response_notification";
    public static final String URL_UPDATE_STATUS_DECLINE = BASE_URL + "response_notification_on_decline";
    public static final String URL_DELTE_SPRINT = BASE_URL + "delete_sprint";
    public static final String URL_UPDATE_LAT_LNG = BASE_URL + "update_lat_long";
    public static final String URL_GET_SPRINT_DATA_LAT_LNG = BASE_URL + "read_all_Sprintdata_on_activity_start";
    public static final String URL_GET_SPRINT_DATA_REFRESHED = BASE_URL + "read_sprint_refresh_data";
    public static final String URL_GET_SPRINT_ID_IF_ACTIVE = BASE_URL + "give_sprint_ID_type";
    public static final String URL_NOTIFICATION = BASE_URL + "get_notification_list";
    public static final String URL_LOGOUT = BASE_URL + "user_logout";
    public static final String URL_UNFOLLOW = BASE_URL + "unfollow_sprint";
    public static final String URL_UNFOLLOW_SELF = BASE_URL + "unfollow_himself_sprint";
    public static final String URL_CONTACTLIST = BASE_URL + "contact_list";
    public static final String URL_CHECK_SIM_SERIAL_NO = BASE_URL + "check_sim_serial_no";


    public static final String REGIDTRATIONID = "RegistrationID";

    public static final String VISIBLE_FRAGMENT = "VISIBLE_FRAGMENT";



    public static String typeSprintForMapMe="";
    public static String typeSprintForMapOther="";


    public final static String USER_ID = "USER_ID";
    public final static String USER_PHNO = "USER_PHNO";
    public final static String USER_NAME = "USER_NAME";
    public final static String USER_PROFILE = "USER_PROFILE";
    public final static String USER_PASSWORD = "USER_PASSWORD";
    public final static String USER_VERICODE = "USER_VERICODE";
    public final static String USER_COUNTRY_CODE = "USER_COUNTRY_CODE";
    public final static String ISSPRINT_START = "ISSPRINT_START";
    public final static String ISSPRINT_START_OTHER = "ISSPRINT_START";

    public final static String IS_CREATEDBY_MATCHED = "IS_CREATEDBY_MATCHED";
    public final static String IS_CREATEDBY_MATCHED_OTHER = "IS_CREATEDBY_MATCHED_OTHER";

    public final static String LastLatitude = "LastLatitude";
    public final static String LASTLONGITUDE = "LASTLONGITUDE";


    public final static String USER_EMAIL = "USER_EMAIL";

    public final static String USER_Activity_me = "USER_Activity_me";
    public final static String USER_START_TIME_me = "USER_START_TIME_me";
    public final static String USER_END_TIME_me = "USER_END_TIME_me";
    public final static String USER_DURATION_me = "USER_DURATION_me";

    public final static String USER_Activity_other = "USER_Activity_other";
    public final static String USER_START_TIME_other = "USER_START_TIME_other";
    public final static String USER_END_TIME_other = "USER_END_TIME_other";
    public final static String USER_DURATION_other = "USER_DURATION_other";

    public final static String SPRINT_ID_MAP = "SPRINT_ID_MAP";
    public final static String SPRINT_ID_MAP_OTHER = "SPRINT_ID_MAP_OTHER";
    public final static String SPRINT_ID_NOTI = "SPRINT_ID_NOTI";


    public static MapMainFragment mapMainFragment = null;


    public final static String SPRINT_ACTIVITY_MAP = "SPRINT_ACTIVITY_MAP";
    public final static String SPRINT_SPRINT_CREATED_BY_MAP = "SPRINT_SPRINT_CREATED_BY_MAP";
    public final static String SPRINT_SPRINT_CREATED_BY_MAP_OTHER = "SPRINT_SPRINT_CREATED_BY_MAP_OTHER";


    public final static String SPRINT_ID = "SPRINT_ID";
    public final static String SPRINT_ID_OTHER = "SPRINT_ID_OTHER";

    public final static String MAIN_USER_ID = "MAIN_USER_ID";


    public static Boolean ISDIALOGOPEN = false;

    public static Boolean ISDIALOGOPENPH = false;
    public static Boolean ISFORGETDIALOGOPEN = false;
    public final static String DIALOGMESSAGE = "DIALOGMESSAGE";
    public final static String DIALOGCLASS = "DIALOGCLASS";

    public static Bitmap FinalProfileImage = null;

    public static Uri imageUri;
    public final static String WHICHDIALOG = "WHICHDIALOG";

    public final static String ADDFOLLOWMESTATUS = "ADDFOLLOWMESTATUS";
    public final static String ADDFOLLOWOTHERSTATUS = "ADDFOLLOWOTHERSTATUS";


    public final static String WHICH_BUTTON_IN_FOCUS_FOLLOW = "WHICH_BUTTON_IN_FOCUS_FOLLOW";
    public final static String WHICH_BUTTON_IN_FOCUS_MAP = "WHICH_BUTTON_IN_FOCUS_MAP";
    public final static String WHICH_BUTTON_IN_FOCUS_CHAT = "WHICH_BUTTON_IN_FOCUS_CHAT";

    public final static String FOLLOW_ME = "FOLLOW_ME";
    public final static String FOLLOW_OTHER = "FOLLOW_OTHER";
    public final static String MAP_ME = "MAP_ME";
    public final static String MAP_OTHER = " MAP_OTHER";

    public final static String INDIVIDUAL = "INDIVIDUAL";
    public final static String GROUP = "GROUP";


    public static Double LATITUDE = 0.0;
    public static Double LONGITUDE = 0.0;


    public static String WHICH_CONTACT_LIST = " WHICH_CONTACT_LIST";

    public static String WHICH_LIST="SINGLE";

    //for map options
    public static boolean IS_NORMAL = false;
    public static boolean IS_SATELLITE = false;
    public static boolean IS_ROUTE_OPTION = false;
    public static boolean IS_DRIVING = false;
    public static boolean IS_BICYCLE = true;
    public static boolean IS_WALK = false;
    public static boolean IS_TRANSIT = false;
    public static boolean IS_TRAFFIC = false;
    public static boolean IS_HIGHWAY = false;
    public static boolean IS_TOLL = false;
    public static boolean IS_FERRIES = false;


    //ArrayList to store Order List data
    public static ArrayList<ContactListMeModel> arrayList_ContactList = new ArrayList<ContactListMeModel>();
    public static ArrayList<ContactListMeModel> arrayList_ContactList_Other = new ArrayList<ContactListMeModel>();

    public static ArrayList<ContactListMeModel> arrayList_ContactList_toShow = new ArrayList<ContactListMeModel>();
    public static ArrayList<ContactListMeModel> arraylist_old_toCrossCheckStatus = new ArrayList<ContactListMeModel>();

    public static ArrayList<String> arrayList_selected_ContactList = new ArrayList<String>();
    public static ArrayList<String> arrayList_removed_ContactList = new ArrayList<String>();
    public static ArrayList<String> arrayList_new_ContactList = new ArrayList<String>();
    public static ArrayList<String> arrayList_old_ContactList = new ArrayList<String>();

    public static String old = "";

    /*public static ArrayList<String> arrayList_selected_ContactList_other = new ArrayList<String>();
    public static ArrayList<String> arrayList_removed_ContactList_other = new ArrayList<String>();
    public static ArrayList<String> arrayList_new_ContactList_other = new ArrayList<String>();
    public static ArrayList<String> arrayList_old_ContactList_other = new ArrayList<String>();
*/

    public static ArrayList<Boolean> arrayList_SelectedContactList = new ArrayList<Boolean>();

    public static int TOTAL_CONTACTS_SELECTED = 0;
    public static int ADD_CONTACT_OTHER_SELECTED;

    public static ArrayList<ContactListOtherModel> arraylist_old_toCrossCheckStatus_other = new ArrayList<ContactListOtherModel>();

    public static ArrayList<ContactListOtherModel> arrayList_ContactList_other = new ArrayList<ContactListOtherModel>();
    public static ArrayList<ContactListOtherModel> arrayList_SelectedContactList_other = new ArrayList<ContactListOtherModel>();
    public static int TOTAL_CONTACTS_SELECTED_OTHER = 0;

    public static ArrayList<String> Codes = new ArrayList<>();


    //for handling last visited page before closing the app
    public final static String LAST_VISITED = "LAST_VISITED";
    public final static String LAST_VISITED_FRAGMENT = "LAST_VISITED_FRAGMENT";
    public final static String ACTIVITY_CHANGEPASSWORD = "ACTIVITY_CHANGEPASSWORD";
    public final static String ACTIVITY_CONTACT_LIST = "ACTIVITY_CONTACT_LIST";
    public final static String ACTIVITY_LOGIN = "ACTIVITY_LOGIN";
    public final static String ACTIVITY_NOTIFICATION = "ACTIVITY_NOTIFICATION";
    public final static String ACTIVITY_PROFILE = "ACTIVITY_PROFILE";
    public final static String ACTIVITY_SIGNUP = "ACTIVITY_SIGNUP";
    public final static String ACTIVITY_CHAT_FRAGMENT = "ACTIVITY_CHAT_FRAGMENT";
    public final static String ACTIVITY_FOLLOW_FRAGMENT = "ACTIVITY_FOLLOW_FRAGMENT";
    public final static String ACTIVITY_MAP_FRAGMENT = "ACTIVITY_MAP_FRAGMENT";
    public final static String ACTIVITY_ADD_SPRINT_ME = "ACTIVITY_ADD_SPRINT_ME";
    public final static String ACTIVITY_ADD_SPRINT_OTHER = "ACTIVITY_ADD_SPRINT_OTHER";
    public final static String ACTIVITY_SPRINT_LIST_ME = "ACTIVITY_SPRINT_LIST_ME";
    public final static String ACTIVITY_SPRINT_LIST_OTHER = "ACTIVITY_SPRINT_LIST_OTHER";


    //arrays of map fragment
    public static ArrayList<MarkerPointsModel> markerPoints = new ArrayList<MarkerPointsModel>();
    public static ArrayList<LatLng> total_arrayList = new ArrayList<LatLng>();
    public static ArrayList<MarkerPointsModel> markerPoints_Other = new ArrayList<MarkerPointsModel>();
    public static ArrayList<LatLng> total_arrayList_Other = new ArrayList<LatLng>();

    //public static ArrayList<String> arrayList = new ArrayList<>();



}
