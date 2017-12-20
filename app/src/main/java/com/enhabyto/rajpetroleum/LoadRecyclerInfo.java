package com.enhabyto.rajpetroleum;

/**
 * Created by this on 28-Nov-17.
 */

public class LoadRecyclerInfo {

    public String location, next_stoppage, oil_loaded, state_name, date_time, oil_left, pump_name, oil_unloaded
            , gps_latitude, gps_longitude, gps_location;



    public LoadRecyclerInfo() {

    }

    public LoadRecyclerInfo(String location, String next_stoppage, String oil_loaded, String state_name
            , String date_time,String oil_left, String pump_name, String oil_unloaded) {


        this.location = location;
        this.next_stoppage = next_stoppage;
        this.oil_loaded = oil_loaded;
        this.state_name = state_name;
        this.date_time = date_time;
        this.oil_left = oil_left;
        this.pump_name = pump_name;
        this.oil_unloaded = oil_unloaded;


    }

    public String getLocation(){
        return location;
    }

    public String getNext_stoppage(){
        return next_stoppage;
    }

    public String getOil_loaded(){ return oil_loaded; }

    public String getState_name(){ return state_name; }

    public String getDate_time(){ return date_time; }

    public String getOil_left(){ return oil_left; }

    public String getPump_name(){ return pump_name; }

    public String getOil_unloaded(){ return oil_unloaded; }

    public String getGps_latitude(){ return gps_latitude; }

    public String getGps_location(){ return gps_location; }

    public String getGps_longitude(){ return gps_longitude; }

}
