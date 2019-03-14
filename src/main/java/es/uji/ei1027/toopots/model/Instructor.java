package es.uji.ei1027.toopots.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Instructor {

    //Atributos

    private int id;
    private String name;
    private String nif;
    private String mail;
    private String residence;
    private int accountNumber;
    private String username;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date registerDate;

    private String passwd;
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

    public String getResidence() {
        return residence;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public String getPasswd() {
        return passwd;
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

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
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
                ", residence='" + residence + '\'' +
                ", accountNumber=" + accountNumber +
                ", username='" + username + '\'' +
                ", registerDate=" + registerDate +
                ", passwd='" + passwd + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

}
