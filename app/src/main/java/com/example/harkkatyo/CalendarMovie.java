package com.example.harkkatyo;

public class CalendarMovie extends ReviewedMovie{

    public CalendarMovie(String name, String date, String stars) {
        super(name, date, stars);
    }
    public CalendarMovie(String name, String date, String stars, String comment) {
        super(name, date, stars, comment);
    }

    @Override
    public String toString() {
        String content = this.getMovieName()+ "\nRating: "+this.getReviewStars()+" stars. "+this.getReviewComment();
        return content;
    }
}
