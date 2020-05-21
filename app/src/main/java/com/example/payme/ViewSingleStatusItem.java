package com.example.payme;

import java.util.List;

public class ViewSingleStatusItem {
    private String title;

    private String oweName;

    private String price;

    private String Date;

    private String Image_Url;

    public ViewSingleStatusItem(String title,String oweName, String price, String Date, String Image_Url){
        this.title = title;
        this.oweName = oweName;
        this.price = price;
        this.Date = Date;
    }

    public ViewSingleStatusItem(){
        //constructor
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public String getOweName() {
        return oweName;
    }

    public void setOweName(String oweName) {
        this.oweName = oweName;
    }

}
