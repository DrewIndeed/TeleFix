package com.example.telefixmain.Model;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class Vendor {
    private String id;
    private String name;
    private String rating;
    private String reviewsCount;
    private String location;
    private String contact;
    private String website;
    private String openTime;
    private String closeTime;
    private String img;
    private String lat;
    private String lng;
    private HashMap<String, String> inspectionPrice;
    private HashMap<String, String> repairPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(String reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public HashMap<String, String> getInspectionPrice() {
        return inspectionPrice;
    }

    public void setInspectionPrice(HashMap<String, String> inspectionPrice) {
        this.inspectionPrice = inspectionPrice;
    }

    public HashMap<String, String> getRepairPrice() {
        return repairPrice;
    }

    public void setRepairPrice(HashMap<String, String> repairPrice) {
        this.repairPrice = repairPrice;
    }

    @NonNull
    @Override
    public String toString() {
        return "Vendor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
