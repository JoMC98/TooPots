package es.uji.ei1027.toopots.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Users {
    private int id;
    private String username;
    private String passwd;
    private String rol;

    private String nif;
    private String name;
    private String mail;

    private LocalDate registerDate;

    //Para poder asignar actividades
    private List<Certification> certifications;
    private List<ActivityType> activities;

    //Constructor
    public Users() {
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getRol() {
        return rol;
    }

    public String getNif() {
        return nif;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }


    //ToString
    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                ", rol='" + rol + '\'' +
                ", nif='" + nif + '\'' +
                ", name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                ", registerDate=" + registerDate +
                '}';
    }
}
