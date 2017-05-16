package com.terracotta.followmateapp.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aspl31 on 12/9/16.
 */
public class MarkerPointsModel {

    private String uID, name, dist, time, mobile;
    LatLng latLng;

    public MarkerPointsModel(String uid, String name, LatLng latLng, String dist, String time, String mobile) {

        this.uID = uid;
        this.name = name;
        this.latLng = latLng;
        this.dist = dist;
        this.time = time;
        this.mobile = mobile;
    }


    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getTime() {
        return time;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getuID() {
        return uID;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}
