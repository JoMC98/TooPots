package es.uji.ei1027.toopots.model;


import java.time.LocalDate;


public class Customer {

    //Atributos
    private int id;
    private String sex;
    private LocalDate birthDate;


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

    public LocalDate getBirthDate() {
        return birthDate;
    }


    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthDate(LocalDate birthDate) {
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
