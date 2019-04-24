package es.uji.ei1027.toopots.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class Instructor {

    //Atributos

    private int id;
    private String photo;
    private String residence;
    private int accountNumber;
    private String state;

    //Para poder mostrarlo en la lista de solicitudes y instructores
    private String name;
    private List<Certification> certifications;
    private List<ActivityType> activities;


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

    public String getName() {
        return name;
    }

    public List<Certification> getCertifications() {
        return certifications;
    }

    public List<ActivityType> getActivities() {
        return activities;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }

    public void setActivities(List<ActivityType> activities) {
        this.activities = activities;
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
