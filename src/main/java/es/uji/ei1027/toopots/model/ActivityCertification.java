package es.uji.ei1027.toopots.model;

public class ActivityCertification {

    //Atributos
    private int idCertification;
    private int activityType;

    //Constructor
    public ActivityCertification() {

    }

    //Getters
    public int getIdCertification() {
        return idCertification;
    }

    public int getActivityType() {
        return activityType;
    }

    //Setters
    public void setIdCertification(int idCertification) {
        this.idCertification = idCertification;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    //ToString

    @Override
    public String toString() {
        return "ActivityCertification{" +
                "idCertification=" + idCertification +
                ", activityType=" + activityType +
                '}';
    }
}
