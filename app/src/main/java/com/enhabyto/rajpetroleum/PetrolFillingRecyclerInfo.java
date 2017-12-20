package com.enhabyto.rajpetroleum;

/**
 * Created by this on 27-Nov-17.
 */

public class PetrolFillingRecyclerInfo {

    public String address, date_time, money_paid, name, petrol_filled, state, token_number, imageURL
            , gps_latitude, gps_longitude, gps_location;



    public PetrolFillingRecyclerInfo() {

    }

    public PetrolFillingRecyclerInfo(String address, String date_time, String name, String petrol_filled, String state
                                     , String token_number, String imageURL, String money_paid) {
        this.address = address;
        this.date_time = date_time;
        this.name = name;
        this.petrol_filled = petrol_filled;
        this.state = state;
        this.token_number = token_number;
        this.imageURL = imageURL;
        this.money_paid = money_paid;


    }

    public String getAddress(){
        return address;
    }

    public String getDate_time(){
        return date_time;
    }

    public String getName(){
        return name;
    }

    public String getPetrol_filled(){ return petrol_filled; }

    public String getState(){
        return state;
    }

    public String getToken_number(){
        return token_number;
    }

    public String getImageURL(){
        return imageURL;
    }

    public String getMoney_paid(){ return money_paid; }

    public String getGps_latitude(){ return gps_latitude; }

    public String getGps_location(){ return gps_location; }

    public String getGps_longitude(){ return gps_longitude; }


}