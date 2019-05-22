package es.uji.ei1027.toopots.model;

import java.util.Date;
import java.util.Objects;

public class ActivityRates implements Comparable<ActivityRates> {
    private int idActivity;
    private String rateName;
    private float price;

    //Constructor
    public ActivityRates() {
    }
    public ActivityRates(int idActivity, String rateName, float price) {
        this.idActivity = idActivity;
        this.rateName = rateName;
        this.price = price;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityRates that = (ActivityRates) o;
        return Float.compare(that.price, price) == 0;
    }

    public int compareTo(ActivityRates altre) {
        return Float.compare(this.getPrice(), altre.getPrice()) * -1;
    }
}