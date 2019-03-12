package es.uji.ei1027.toopots.model;

import java.util.Date;

public class Customer {

    //Atributos
    private int id;
    private String nif;
    private String name;
    private String mail;
    private String sex;
    private Date birthDate;
    private Date registerDate;
    private String username;
    private String passwd;

    //Constructor
    public Customer() {
    }

    //Getters


    public int getId() {
        return id;
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

    public String getSex() {
        return sex;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswd() {
        return passwd;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
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

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    //ToString

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", nif='" + nif + '\'' +
                ", name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                ", sex='" + sex + '\'' +
                ", birthDate=" + birthDate +
                ", registerDate=" + registerDate +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
