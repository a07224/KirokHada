package com.example.kirokhada.Board.data;

public class WriteInfo {

    private String title, author, rating, keyword, contents, email, date, uid;

    public WriteInfo(String title, String author, String rating, String keyword, String contents, String email, String date, String uid) {
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.keyword = keyword;
        this.contents = contents;
        this.email = email;
        this.date = date;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getRating() {
        return rating;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getContents() {
        return contents;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getUid() {
        return uid;
    }

}
