package com.example.pranavbapat.assignment5;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by pranav.bapat on 3/28/17.
 */

public class User {
    private int userID;
    private String nickname;
    private String password;
    private String country;
    private String state;
    private String city;
    private LatLng hometownLatLng;
    private int year;

    public int getUserID() {return userID;}

    public void setUserID(int userID) {this.userID = userID;}

    public String getCity() {
        return city;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public LatLng getHometownLatLng() {
        return hometownLatLng;
    }

    public void setHometownLatLng(LatLng hometownLatLng) {
        this.hometownLatLng = hometownLatLng;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isValidUserInfo() {
        boolean validUserInfo = true;
        if (nickname == null || nickname.isEmpty())
            validUserInfo = false;
        if (password == null || password.isEmpty() || password.length() < 3)
            validUserInfo = false;
        if( city == null || city.isEmpty())
            validUserInfo = false;
        return validUserInfo;
    }
}
