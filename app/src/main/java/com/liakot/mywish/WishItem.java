package com.liakot.mywish;

import java.sql.Time;
import java.util.Date;

public class WishItem {

    private String wishTittle;
    private String wishHint;
    private String date;
    private String time;
    private String wishName;
    private String imageUri;
    private String uniqueId;
    private String imageName;


    public WishItem() {

    }

    //-------for WishListItem----
    public WishItem(String wishTittle, String wishHint, String date, String time, String uniqueId) {
        this.wishTittle = wishTittle;
        this.wishHint = wishHint;
        this.date = date;
        this.time = time;
        this.uniqueId = uniqueId;
    }

    // -------------- for SecretListItem-----------
    public WishItem(String wishTittle, String wishHint, String date, String time, String wishName, String uniqueId) {
        this.wishTittle = wishTittle;
        this.wishHint = wishHint;
        this.date = date;
        this.time = time;
        this.wishName = wishName;
        this.uniqueId = uniqueId;
    }
    //---------for FavouriteListItem--------------
    public WishItem(String wishTittle, String wishHint, String date, String time, String wishName, String imageUri, String uniqueId, String imageName) {
        this.wishTittle = wishTittle;
        this.wishHint = wishHint;
        this.date = date;
        this.time = time;
        this.wishName = wishName;
        this.imageUri = imageUri;
        this.uniqueId = uniqueId;
        this.imageName = imageName;
    }

    public String getWishTittle() {
        return wishTittle;
    }

    public void setWishTittle(String wishTittle) {
        this.wishTittle = wishTittle;
    }

    public String getWishHint() {
        return wishHint;
    }

    public void setWishHint(String wishHint) {
        this.wishHint = wishHint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWishName() {
        return wishName;
    }

    public void setWishName(String wishName) {
        this.wishName = wishName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
