package com.terracotta.followmateapp.model;

import java.util.List;

/**
 * Created by aspl31 on 9/9/16.
 */
public class InviteContactModel {

    String response;
    String message;
    List<ContactData> dataList;

    public InviteContactModel( String response,String message,List<ContactData> dataList){
        this.response = response;
        this.message = message;
        this.dataList = dataList;
    }


    public static class ContactData {


        private String userName;

        private String userMbl;

        private String flag;

        private String profile_img;

        /**
         * No args constructor for use in serialization
         *
         */
        public ContactData() {
        }

        /**
         *
         * @param flag
         * @param userMbl
         * @param userName
         */
        public ContactData(String userName, String userMbl, String flag, String profile_img) {
            this.userName = userName;
            this.userMbl = userMbl;
            this.flag = flag;
            this.profile_img = profile_img;
        }

        /**
         *
         * @return
         * The userName
         */
        public String getUserName() {
            return userName;
        }

        public String getProfileImage() {
            return profile_img;
        }

        /**
         *
         * @param userName
         * The user_name
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }

        /**
         *
         * @return
         * The userMbl
         */
        public String getUserMbl() {
            return userMbl;
        }

        /**
         *
         * @param userMbl
         * The user_mbl
         */
        public void setUserMbl(String userMbl) {
            this.userMbl = userMbl;
        }

        /**
         *
         * @return
         * The flag
         */
        public String getFlag() {
            return flag;
        }

        /**
         *
         * @param flag
         * The flag
         */
        public void setFlag(String flag) {
            this.flag = flag;
        }

    }

    public String getReponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }
}
