package com.example.payme;

import java.util.ArrayList;
import java.util.List;

public class Friend {

    private static Friend instance;
    private String email;

    private List<String> listEmail;


    private Friend() {
        this.listEmail = new ArrayList<>();
    }

    public static Friend getInstance()
    {
        if (instance == null)
            instance = new Friend();

        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addEmail(String friend){
        listEmail.add(friend);
    }

    public List<String> getListEmail() {
        return listEmail;
    }

    public void emptyList(){
        listEmail.clear();
    }

    public void setListEmail(List<String> listEmail) {
        this.listEmail = listEmail;
    }

    public int getlistFriend(){
        if(listEmail.size() == 0){
            return 1;
        }
        return listEmail.size() + 1;
    }

}