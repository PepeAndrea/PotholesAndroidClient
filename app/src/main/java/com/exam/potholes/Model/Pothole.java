package com.exam.potholes.Model;

public class Pothole {

    String username;
    Double latitude,longitude,variation;

    public Pothole(String username, Double latitude, Double longitude, Double variation) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.variation = variation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getVariation() {
        return variation;
    }

    public void setVariation(Double variation) {
        this.variation = variation;
    }

    @Override
    public String toString() {
        return "Pothole{" +
                "username='" + username + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", variation=" + variation +
                '}';
    }
}
