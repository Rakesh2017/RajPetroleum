package com.enhabyto.rajpetroleum;

/**
 * Created by this on 09-Dec-17.
 */

public class ScheduleDetailRecyclerViewInfo {
    public String truck_number, date, status;



    public ScheduleDetailRecyclerViewInfo() {

    }

    public ScheduleDetailRecyclerViewInfo(String truck_number, String date, String status) {



    }

    public String getTruck_number(){
        return truck_number;
    }

    public String getDate(){
        return date;
    }

    public String getStatus(){ return status; }


}
