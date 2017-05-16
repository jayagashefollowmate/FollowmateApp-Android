package com.terracotta.followmateapp.model;


//Developement team:               Mobility Team
//Creation Date:                   9 Aug 2016
//Information                      Model to get and set contact List
// Comment:


import android.os.Parcel;
import android.os.Parcelable;

public class ContactListMeModel implements Parcelable{

    private String user_id, name, phoneNumber, status, profile_img;
    private boolean isCheck;

    public ContactListMeModel(String user_id, String name, String phoneNumber, String status, String profile_img) {
        this.user_id = user_id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.profile_img = profile_img;
    }


    public String getUserID() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getProfileImage() {
        return profile_img;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(name);
        parcel.writeString(phoneNumber);
        parcel.writeString(status);
        parcel.writeString(profile_img);
    }

    protected ContactListMeModel(Parcel in) {
        user_id = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        status = in.readString();
        profile_img = in.readString();
    }


    public static final Creator<ContactListMeModel> CREATOR = new Creator<ContactListMeModel>() {
        public ContactListMeModel createFromParcel(Parcel source) {
            return new ContactListMeModel(source);
        }

        public ContactListMeModel[] newArray(int size) {
            return new ContactListMeModel[size];
        }
    };
}





