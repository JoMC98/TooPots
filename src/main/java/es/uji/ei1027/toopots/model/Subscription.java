package es.uji.ei1027.toopots.model;

public class Subscription {


    private int idActivityType;
    private int idCustomer;

    public Subscription() {
    }

    public int getIdActivityType() {
        return idActivityType;
    }

    public void setIdActivityType(int idActivityType) {
        this.idActivityType = idActivityType;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }


    @Override
    public String toString() {
        return "Subscription{" +
                "idActivityType=" + idActivityType +
                ", idCustomer=" + idCustomer +
                '}';
    }
}
