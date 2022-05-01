package com.example.harkkatyo;

public class Theater { // class for displaying movies in the daily movies menu

    private String theaterId = "";
    private String theaterName="";

    public Theater(String ID, String name){
        this.theaterId = ID;
        this.theaterName = name;
    }

    public String getTheaterName(){
        return theaterName;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public String getTheaterID() {
        return theaterId;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    @Override
    public String toString() {
        return theaterName;
    }

}