package com.yarcrasy.gamex.Models;

import com.yarcrasy.gamex.DBConnector;

public class Rental {
    public String id;
    public Client client;
    public String rentalDate;
    public boolean isDelayed = false;
    public float totalAmount = 0.0f;

    public Rental(String id, Client client, String rentalDate) {
        this.id = id;
        this.client = client;
        this.rentalDate = rentalDate;
    }

    public Rental(String id, Client client, String rentalDate, boolean isDelayed, float totalAmount) {
        this.id = id;
        this.client = client;
        this.rentalDate = rentalDate;
        this.isDelayed = isDelayed;
        this.totalAmount = totalAmount;
    }

    public void setDelayed() {
        isDelayed = true;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }
}
