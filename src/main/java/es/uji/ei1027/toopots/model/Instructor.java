package es.uji.ei1027.toopots.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Instructor {

    //Atributos

    private int id;
    private String name;
    private String nif;
    private String mail;
    private String photo;
    private String residence;
    private int accountNumber;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date registerDate;

    private String state;

    //Constructor

    public Instructor(){

    }


//Getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNif() {
        return nif;
    }

    public String getMail() {
        return mail;
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

    public Date getRegisterDate() {
        return registerDate;
    }

    public String getState() {
        return state;
    }

    //Setters


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public void setState(String state) {
        this.state = state;
    }


    //toString
    @Override
    public String toString() {
        return "Instructor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nif='" + nif + '\'' +
                ", mail='" + mail + '\'' +
                ", photo='" + photo + '\'' +
                ", residence='" + residence + '\'' +
                ", accountNumber=" + accountNumber +
                ", registerDate=" + registerDate +
                ", state='" + state + '\'' +
                '}';
    }
}
