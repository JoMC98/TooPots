package es.uji.ei1027.toopots.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Customer {

    //Atributos
    private int id;
    private String sex;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date birthDate;


    //Constructor
    public Customer() {
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getSex() {
        return sex;
    }

    public Date getBirthDate() {
        return birthDate;
    }


    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    //ToString

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", sex='" + sex + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
