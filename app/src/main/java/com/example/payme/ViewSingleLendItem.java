package com.example.payme;

public class ViewSingleLendItem {

    private String title;

    private String lendName;

    private String price;

    private String Date;

    public ViewSingleLendItem(String title,String lendName, String price, String Date){
        this.title = title;
        this.lendName = lendName;
        this.price = price;
        this.Date = Date;
    }

    public ViewSingleLendItem(){
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

    public String getLendName() {
        return lendName;
    }

    public void setLendName(String lendName) {
        this.lendName = lendName;
    }
}
