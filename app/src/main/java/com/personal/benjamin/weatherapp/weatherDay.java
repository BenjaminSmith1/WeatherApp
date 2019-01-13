package com.personal.benjamin.weatherapp;

import com.orm.SugarRecord;

public class weatherDay extends SugarRecord<weatherDataObject> { //the table structure of the database
    //these are the fields
    private String summary;
    private String tempL;
    private String tempH;
    private Integer wBearing;
    private String wSpeed;
    private Integer sunrise;
    private Integer sunset;
    private Integer time;

    public weatherDay(){}
 //the constructor which creates the table
    public weatherDay(String summary, String tempL, String tempH,Integer wBearing, String wSpeed,Integer sunrise, Integer sunset, Integer time){
        this.summary = summary;
        this.tempL = tempL;
        this.tempH = tempH;
        this.wBearing = wBearing;
        this.wSpeed = wSpeed;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.time = time;
    }
//functions to get the data
    public String getSummary() {
        return this.summary;
    }

    public Integer getSunrise() {
        return this.sunrise;
    }

    public Integer getSunset() {
        return this.sunset;
    }

    public Integer getTime() {
        return this.time;
    }

    public Integer getwBearing() {
        return this.wBearing;
    }

    public String getwSpeed() {
        return this.wSpeed;
    }

    public String getTempH() {
        return this.tempH;
    }

    public String getTempL() {
        return this.tempL;
    }
}