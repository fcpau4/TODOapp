package com.example.a47276138y.todoapp;

/**
 * Created by Arfera on 21/02/2017.
 */

public class PhotoData {

    private String absolute;
    private String location;

    public PhotoData(){}

    public PhotoData(String pathPhoto, String loc){
        absolute = pathPhoto;
        location = loc;
    }

    public String getAbsolute() {
        return absolute;
    }

    public void setAbsolute(String absolute) {
        this.absolute = absolute;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
