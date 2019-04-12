package es.uji.ei1027.toopots.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Instructor {

    //Atributos

    private int id;
    private String photo;
    private String residence;
    private int accountNumber;
    private String state;

    //Constructor

    public Instructor(){
    }


//Getters

    public int getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getResidence() {
        return residence;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getState() {
        return state;
    }

    //Setters


    public void setId(int id) {
        this.id = id;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setState(String state) {
        this.state = state;
    }


    //toString

    @Override
    public String toString() {
        return "Instructor{" +
                "id=" + id +
                ", photo='" + photo + '\'' +
                ", residence='" + residence + '\'' +
                ", accountNumber=" + accountNumber +
                ", state='" + state + '\'' +
                '}';
    }
}
