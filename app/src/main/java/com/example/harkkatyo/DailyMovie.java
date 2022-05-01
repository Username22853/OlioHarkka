package com.example.harkkatyo;

public class DailyMovie extends Movie{

    String time ="";
    String theater ="";

    public DailyMovie(String name, String time, String theater) {
        super(name);
        setTheater(theater);
        setTime(time);
    }

    public void setTheater(String theater) {
        this.theater = theater;
    }
    public String getTheater() {
        return theater;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        String form = getMovieName()+"\n"+getTime()+" "+ getTheater()+"\n";
        return form;
    }
}
