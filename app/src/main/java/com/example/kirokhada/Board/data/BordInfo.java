package com.example.kirokhada.Board.data;

public class BordInfo {
    private String title;
    private String author;
    private String rating;
    private String keyword;
    private String contents;
    private String email;
    private String date;
    private String sc;
    private String userName;
    private String userProfileUrl;

    public BordInfo(String title, String author, String rating, String keyword, String contents, String email, String date, String sc, String name,String userProfileUrl) {
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.keyword = keyword;
        this.contents = contents;
        this.email = email;
        this.date = date;
        this.sc = sc;
        this.userProfileUrl = userProfileUrl;
    }

    public BordInfo(){}

    public String getTitle() {
        return this.title;
    }

    public String getTime() {
        return this.date;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getUserName() {
        return this.userName;
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

    public String getSC() {return this.sc;}

    public String getUserProfileUrl() {return this.userProfileUrl;}
}
