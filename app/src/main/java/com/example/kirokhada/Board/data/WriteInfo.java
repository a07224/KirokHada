package com.example.kirokhada.Board.data;

public class WriteInfo {

    private String title;
    private String author;
    private String rating;
    private String keyword;
    private String contents;
    private String email;
    private String date;
    private String profileURL;
    private String sc;


    public WriteInfo(String title, String author, String rating, String keyword, String contents, String email, String date, String profileURL, String sc) {
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.keyword = keyword;
        this.contents = contents;
        this.email = email;
        this.date = date;
        this.sc = sc;
        this.profileURL = profileURL;
    }

    public String getSc() {
        return this.sc;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getRating() {
        return this.rating;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public String getContents() {
        return this.contents;
    }

    public String getEmail() {
        return this.email;
    }

    public String getDate() {
        return this.date;
    }

    public String getUserProfileURL() {
        return this.profileURL;
    }
}
