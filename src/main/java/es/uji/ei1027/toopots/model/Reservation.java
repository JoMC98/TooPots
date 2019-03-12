package es.uji.ei1027.toopots.model;

import java.util.Date;

public class Reservation {

    //Atributos

    private int id;
    private Date bookingDate;
    private Date activityDate;
    private int numberPeople;
    private int pricePerPerson;
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

    public int getNumberPeople() {
        return numberPeople;
    }

    public int getPricePerPerson() {
        return pricePerPerson;
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

    public void setNumberPeople(int numberPeople) {
        this.numberPeople = numberPeople;
    }

    public void setPricePerPerson(int pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
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
                ", numberPeople=" + numberPeople +
                ", pricePerPerson=" + pricePerPerson +
                ", totalPrice=" + totalPrice +
                ", transactionNumber=" + transactionNumber +
                ", idActivity=" + idActivity +
                ", idCustomer=" + idCustomer +
                ", state='" + state + '\'' +
                '}';
    }
}
