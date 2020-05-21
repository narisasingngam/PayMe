package com.example.payme;

public class EmailFormat {

    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
