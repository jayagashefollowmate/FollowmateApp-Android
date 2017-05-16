package com.terracotta.followmateapp.model;


//Developement team:               Mobility Team
//Creation Date:                   9 Aug 2016
//Information                      Model to get and set contact List
// Comment:


public class ContactListOtherModel {

    private String user_id, name, phoneNumber, status,profile_img;


    public ContactListOtherModel(String user_id, String name, String phoneNumber, String status, String profile_img) {
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

    public String getProfileImage() {
        return profile_img;
    }

    public void setstatus(String status) {
        this.status=status;
    }
/*
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(user_id);
        out.writeString(name);
        out.writeString(phoneNumber);
        out.writeString(status);
        out.writeString(profile_img);
    }

    private ContactListOtherModel(Parcel in){
        user_id = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        status = in.readString();
        profile_img = in.readString();
    }

    public static final Parcelable.Creator<ContactListOtherModel> CREATOR = new Parcelable.Creator<ContactListOtherModel>() {
        public ContactListOtherModel createFromParcel(Parcel in) {
            return new ContactListOtherModel(in);
        }

        public ContactListOtherModel[] newArray(int size) {
            return new ContactListOtherModel[size];
        }
    };*/
}





