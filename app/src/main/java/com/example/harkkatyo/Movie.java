package com.example.harkkatyo;

public class Movie {
    private String movieName ="";

    public Movie(String name){
        movieName = name;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    @Override
    public String toString() {
        return movieName;
    }
}

