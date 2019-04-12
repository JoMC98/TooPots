package es.uji.ei1027.toopots.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Reservation {

    //Atributos

    private int id;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date bookingDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date activityDate;

    //Por edad
    private int numUnder16;
    private int numStudents;
    private int numAdults;
    private int numOver60;

    private int totalPrice;
    private int transactionNumber;
    private int idActivity;
    private int idCustomer;
    private String state;

    //Constructor

    public Reservation() {
    }

    //Getters
    public int getId() {
        return id;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public int getNumUnder16() {
        return numUnder16;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public int getNumAdults() {
        return numAdults;
    }

    public int getNumOver60() {
        return numOver60;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getTransactionNumber() {
        return transactionNumber;
    }

    public int getIdActivity() {
        return idActivity;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public String getState() {
        return state;
    }

    public int getNumPeople() {
        return numUnder16 + numStudents + numAdults + numOver60;
    }

    //Setters


    public void setId(int id) {
        this.id = id;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public void setNumUnder16(int numUnder16) {
        this.numUnder16 = numUnder16;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }

    public void setNumOver60(int numOver60) {
        this.numOver60 = numOver60;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTransactionNumber(int transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public void setState(String state) {
        this.state = state;
    }

    //toString
    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", bookingDate=" + bookingDate +
                ", activityDate=" + activityDate +
                ", numUnder16=" + numUnder16 +
                ", numStudents=" + numStudents +
                ", numAdults=" + numAdults +
                ", numOver60=" + numOver60 +
                ", totalPrice=" + totalPrice +
                ", transactionNumber=" + transactionNumber +
                ", idActivity=" + idActivity +
                ", idCustomer=" + idCustomer +
                ", state='" + state + '\'' +
                '}';
    }
}
