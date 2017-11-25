package com.enhabyto.rajpetroleum;

/**
 * Created by this on 24-Nov-17.
 */

public class TripRecyclerInfo {

    public String driverContact;
    public String driverName;
    public String tripStarted;
    public String truckNumber;


    public TripRecyclerInfo() {

    }

    public TripRecyclerInfo(String contact, String name, String trip, String truck) {

        this.driverContact = contact;
        this.driverName = name;
        this.tripStarted = trip;
        this.truckNumber = truck;

    }

    public String getContact_tx() {
        return driverContact;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getTripStarted() { return tripStarted; }

    public String getTruckNumber() { return truckNumber; }

}