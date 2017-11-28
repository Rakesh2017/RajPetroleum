package com.enhabyto.rajpetroleum;

/**
 * Created by this on 28-Nov-17.
 */

public class StoppageRecyclerInfo {

    public String place_name, money_paid, money_paid_for, description, date_time;



    public StoppageRecyclerInfo() {

    }

    public StoppageRecyclerInfo(String place_name, String money_paid_for, String description, String money_paid, String date_time) {


        this.money_paid = money_paid;
        this.place_name = place_name;
        this.description = description;
        this.money_paid_for = money_paid_for;
        this.date_time = date_time;


    }

    public String getPlace_name(){
        return place_name;
    }

    public String getMoney_paid_for(){
        return money_paid_for;
    }

    public String getDescription(){ return description; }

    public String getMoney_paid(){ return money_paid; }

    public String getDate_time(){ return date_time; }


}
