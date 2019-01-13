package com.personal.benjamin.weatherapp;

public class weatherDataObject{
    //custom object for storing the data i want using private variables and functions to get the data
    private String summary;
    private String tempL;
    private String tempH;
    private Integer wBearing;
    private String wSpeed;
    private Integer sunrise;
    private Integer sunset;
    private Integer Time;

    public weatherDataObject(){
        super();
    }

    public Integer getTime() {
        return Time;
    }

    public void setTime(Integer time) {
        Time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTempL() {
        return tempL;
    }

    public void setTempL(String tempL) {
        this.tempL = tempL;
    }

    public String getTempH() {
        return tempH;
    }

    public void setTempH(String tempH) {
        this.tempH = tempH;
    }

    public Integer getwBearing() {
        return wBearing;
    }

    public void setwBearing(Integer wBearing) {
        this.wBearing = wBearing;
    }

    public String getwSpeed() {
        return wSpeed;
    }

    public void setwSpeed(String wSpeed) {
        this.wSpeed = wSpeed;
    }

    public Integer getSunrise() {
        return sunrise;
    }

    public void setSunrise(Integer sunrise) {
        this.sunrise = sunrise;
    }

    public Integer getSunset() {
        return sunset;
    }

    public void setSunset(Integer sunset) {
        this.sunset = sunset;
    }
}
