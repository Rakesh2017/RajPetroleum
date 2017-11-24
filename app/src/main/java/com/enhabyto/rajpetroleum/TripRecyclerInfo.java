package com.enhabyto.rajpetroleum;

/**
 * Created by this on 24-Nov-17.
 */

public class TripRecyclerInfo {

    public String driverContact;
    public String driverName;
    public String tripStarted;


    public TripRecyclerInfo() {

    }

    public TripRecyclerInfo(String contact, String name, String trip) {

        this.driverContact = contact;
        this.driverName = name;
        this.tripStarted = trip;

    }

    public String getContact_tx() {
        return driverContact;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getTripStarted() {
        return tripStarted;
    }

}