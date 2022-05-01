package com.example.harkkatyo;

public class Info { //singleton principle to move some data between fragments

    private static Info info = new Info();

    private boolean loggedIn = false;
    private String loggedInAs= "";

    public static Info getInstance(){
        return info;
    }

    public void setLoggedIn(boolean b){
        loggedIn = b;
    }

    public boolean getLoggedIn(){
        return loggedIn;
    }

    public void setLoggedInAs(String s){
        loggedInAs = s;
    }

    public String getLoggedInAs(){
        return loggedInAs;
    }


}