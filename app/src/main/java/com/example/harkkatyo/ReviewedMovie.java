package com.example.harkkatyo;

public class ReviewedMovie extends Movie{

    private String reviewDate = "";     //user chooses date they have seen the movie on
    private String reviewComment = "";  //comment by user
    private String reviewStars = "";

    public ReviewedMovie(String name, String date ,String stars) {
        super(name);
        setReviewDate(date);
        setReviewStars(stars);
    }
    public ReviewedMovie(String name, String date, String stars, String comment) {
        super(name);
        setReviewDate(date);
        setReviewStars(stars);
        setReviewComment(comment);
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public String getReviewStars() {
        return reviewStars;
    }

    public void setReviewStars(String reviewStars) {
        this.reviewStars = reviewStars;
    }

}
