package com.yarcrasy.gamex.Models;

public class Client {
    public String id;
    public String dni;
    public String fullName;
    public String email;
    public String address;
    public boolean isFrequent = false;

    public Client(String id, String dni, String fullName, String email, String address, boolean isFrequent) {
        this.id = id;
        this.dni = dni;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.isFrequent = isFrequent;
    }
}
