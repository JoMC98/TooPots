package es.uji.ei1027.toopots.model;

import java.util.Date;

public class ActivityRates {
    private int idActivity;
    private String rateName;
    private float price;

    //Constructor
    public ActivityRates() {
    }

    //Getters
    public int getIdActivity() {
        return idActivity;
    }

    public String getRateName() {
        return rateName;
    }

    public float getPrice() {
        return price;
    }


    //Setters
    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public void setPrice(float price) {
        this.price = price;
    }


    //ToString
    @Override
    public String toString() {
        return "ActivityRates{" +
                "idActivity=" + idActivity +
                ", rateName='" + rateName + '\'' +
                ", price=" + price +
                '}';
    }
}