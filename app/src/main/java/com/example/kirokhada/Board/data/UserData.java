package com.example.kirokhada.Board.data;

public class UserData {

    private String email;
    private String passwd;

    public UserData(String email, String passwd) {

        this.email = email;
        this.passwd = passwd;

    }

    public String getEmail() {
        return this.email;
    }

    public String getPasswd() {
        return this.passwd;
    }

}
