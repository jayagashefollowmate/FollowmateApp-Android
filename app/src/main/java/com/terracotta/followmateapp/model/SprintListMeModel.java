package com.terracotta.followmateapp.model;


//Developement team:               Mobility Team
//Creation Date:                   24 Aug 2016
//Information                      Model to get and set Sprint List
// Comment:


import java.util.List;

public class SprintListMeModel {


    private String sprint_id, activity, start_date_time, end_date_time,
            duration, sprint_created_by, status,
            SprintUSer, Sprintstatus, latitude, longitude;

    List<Sprint_data> listsprintdata;


    public SprintListMeModel(List<Sprint_data> listsprintdata) {
        this.listsprintdata = listsprintdata;
    }


    public List<Sprint_data> getlistsprintdata() {
        return listsprintdata;
    }

    //-------------------------------------------------------------------------------------

    public static class Sprint_data {
        public String sprint_id, activity, start_date_time, end_date_time,
                duration, sprint_created_by, status;

        List<Sprint_participant_data> listsprintparticipantdata;


        public Sprint_data(String sprint_id, String activity, String start_date_time, String end_date_time,
                           String duration, String sprint_created_by, String status, List<Sprint_participant_data> listsprintparticipantdata) {

            this.sprint_id = sprint_id;
            this.activity = activity;
            this.start_date_time = start_date_time;
            this.end_date_time = end_date_time;
            this.duration = duration;
            this.sprint_created_by = sprint_created_by;
            this.status = status;


            this.listsprintparticipantdata = listsprintparticipantdata;
        }

        public String getSprintID() {
            return sprint_id;
        }

        public String getActivity() {
            return activity;
        }

        public String getStart_date_time() {
            return start_date_time;
        }

        public String getEnd_date_time() {
            return end_date_time;
        }

        public String getDuration() {
            return duration;
        }

        public String getSprint_created_by() {
            return sprint_created_by;
        }

        public String getStatus() {
            return status;
        }

        public List<Sprint_participant_data> getlistsprintparticipantdata() {
            return listsprintparticipantdata;
        }


        //-----------------------------------------------------------------------------------------
        public static class Sprint_participant_data {

            String SprintUSer, Sprintstatus, latitude, longitude, mobile, name,profile_img;

            public Sprint_participant_data(String SprintUSer, String Sprintstatus, String latitude, String longitude, String mobile, String name,String profile_img) {
                this.SprintUSer = SprintUSer;
                this.Sprintstatus = Sprintstatus;
                this.latitude = latitude;
                this.longitude = longitude;
                this.mobile = mobile;
                this.name = name;
                this.profile_img = profile_img;
            }

            public String getSprintUSer() {
                return SprintUSer;
            }

            public String getSprintstatus() {
                return Sprintstatus;
            }

            public String getLatitude() {
                return latitude;
            }

            public String getLongitude() {
                return longitude;
            }

            public String getmobile() {
                return mobile;
            }

            public String getName() {
                return name;
            }

            public String getProfileImage() {
                return profile_img;
            }


        }

    }


}





