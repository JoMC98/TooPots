package es.uji.ei1027.toopots.model;

public class SummaryPrice {

    //ATRIBUTOS
    String rateName;
    int cantidad;
    float basePrice;
    float totalPrice;
    boolean grupo;

    //CONSTRUCTORES
    public SummaryPrice() {
    }

    public SummaryPrice(String rateName, int cantidad, float basePrice, float totalPrice, boolean grupo) {
        this.rateName = rateName;
        this.cantidad = cantidad;
        this.basePrice = basePrice;
        this.totalPrice = totalPrice;
        this.grupo = grupo;
    }

    //GETTER
    public String getRateName() {
        return rateName;
    }

    public int getCantidad() {
        return cantidad;
    }

    public float getBasePrice() {
        return basePrice;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public boolean isGrupo() {
        return grupo;
    }

    //SETTER

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setBasePrice(float basePrice) {
        this.basePrice = basePrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setGrupo(boolean grupo) {
        this.grupo = grupo;
    }
}
