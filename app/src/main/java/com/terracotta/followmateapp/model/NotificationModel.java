package com.terracotta.followmateapp.model;

import java.util.List;

public class NotificationModel {

    String response;
    String message;

    List<Sprint_data> dataList;


    public NotificationModel(String response, String message, List<Sprint_data> dataList) {
        this.response = response;
        this.message = message;
        this.dataList = dataList;
    }


    public List<Sprint_data> getDataList() {
        return dataList;
    }


    public static class Sprint_data {

        private String nid;

        private String sprintId;

        private String notificationText;

        private String sprintStages;

        private String mainUserId;

        private String source;

        private String sourceName;

        private String destination;

        private String destinationName;

        private String status;

        private String profile_img;

        /**
         * No args constructor for use in serialization
         */
        public Sprint_data() {
        }

        /**
         * @param source
         * @param sprintId
         * @param status
         * @param sourceName
         * @param nid
         * @param notificationText
         * @param sprintStages
         * @param mainUserId
         * @param destinationName
         * @param destination
         */
        public Sprint_data(String nid, String sprintId, String notificationText, String sprintStages, String mainUserId, String source, String sourceName, String destination, String destinationName, String status, String profile_img) {
            this.nid = nid;
            this.sprintId = sprintId;
            this.notificationText = notificationText;
            this.sprintStages = sprintStages;
            this.mainUserId = mainUserId;
            this.source = source;
            this.sourceName = sourceName;
            this.destination = destination;
            this.destinationName = destinationName;
            this.status = status;
            this.profile_img = profile_img;
        }

        /**
         * @return The nid
         */
        public String getNid() {
            return nid;
        }

        public String getProfileImage() {
            return profile_img;
        }

        /**
         * @param nid The nid
         */
        public void setNid(String nid) {
            this.nid = nid;
        }

        /**
         * @return The sprintId
         */
        public String getSprintId() {
            return sprintId;
        }

        /**
         * @param sprintId The sprint_id
         */
        public void setSprintId(String sprintId) {
            this.sprintId = sprintId;
        }

        /**
         * @return The notificationText
         */
        public String getNotificationText() {
            return notificationText;
        }

        /**
         * @param notificationText The notification_text
         */
        public void setNotificationText(String notificationText) {
            this.notificationText = notificationText;
        }

        /**
         * @return The sprintStages
         */
        public String getSprintStages() {
            return sprintStages;
        }

        /**
         * @param sprintStages The sprint_stages
         */
        public void setSprintStages(String sprintStages) {
            this.sprintStages = sprintStages;
        }

        /**
         * @return The mainUserId
         */
        public String getMainUserId() {
            return mainUserId;
        }

        /**
         * @param mainUserId The main_user_id
         */
        public void setMainUserId(String mainUserId) {
            this.mainUserId = mainUserId;
        }

        /**
         * @return The source
         */
        public String getSource() {
            return source;
        }

        /**
         * @param source The source
         */
        public void setSource(String source) {
            this.source = source;
        }

        /**
         * @return The sourceName
         */
        public String getSourceName() {
            return sourceName;
        }

        /**
         * @param sourceName The source_name
         */
        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }

        /**
         * @return The destination
         */
        public String getDestination() {
            return destination;
        }

        /**
         * @param destination The destination
         */
        public void setDestination(String destination) {
            this.destination = destination;
        }

        /**
         * @return The destinationName
         */
        public String getDestinationName() {
            return destinationName;
        }

        /**
         * @param destinationName The destination_name
         */
        public void setDestinationName(String destinationName) {
            this.destinationName = destinationName;
        }

        /**
         * @return The status
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status The status
         */
        public void setStatus(String status) {
            this.status = status;
        }

    }

    public String getReponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }

}

