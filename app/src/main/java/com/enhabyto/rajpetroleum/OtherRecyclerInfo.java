package com.enhabyto.rajpetroleum;

/**
 * Created by this on 28-Nov-17.
 */

public class OtherRecyclerInfo {

    public String description, filling_name, money_paid, quantity, imageURL, date_time;



    public OtherRecyclerInfo() {

    }

    public OtherRecyclerInfo(String description, String date_time, String filling_name
            , String quantity, String imageURL, String money_paid) {

        this.description = description;
        this.date_time = date_time;
        this.filling_name = filling_name;
        this.quantity = quantity;
        this.imageURL = imageURL;
        this.money_paid = money_paid;


    }

    public String getDescription(){
        return description;
    }

    public String getDate_time(){
        return date_time;
    }

    public String getFilling_name(){
        return filling_name;
    }

    public String getQuantity(){ return quantity; }

    public String getImageURL(){
        return imageURL;
    }

    public String getMoney_paid(){ return money_paid; }


}