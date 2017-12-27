package com.enhabyto.rajpetroleum;

/**
 * Created by this on 11/23/2017.
 */


public class ImageUploadInfo {


    public String imageURL, current_rate, set_by, updated_on;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String url) {

        this.imageURL= url;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getCurrent_rate() {
        return current_rate;
    }

    public String getSet_by() {
        return set_by;
    }

    public String getUpdated_on() {
        return updated_on;
    }

}