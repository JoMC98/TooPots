package es.uji.ei1027.toopots.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Activity implements Comparable<Activity> {

    //Atributos
    private int id;
    private String name;
    private String place;
    private String description;
    private LocalDate dates;
    private float pricePerPerson;
    private int maxNumberPeople;
    private int minNumberPeople;
    private String meetingPoint;
    private LocalTime meetingTime;

    private LocalDate creationDate;

    private String state;
    private int activityType;
    private int idInstructor;

    private String cancelationReason;

    //Para usar con las tarifas
    private ActivityRates tarifaMenores;
    private ActivityRates tarifaEstudiantes;
    private ActivityRates tarifaMayores;
    private ActivityRates tarifaGrupos;
    private static String[] rateNames = {"Menors de 16 anys", "Estudiants", "Majors de 60 anys", "Grups de 10 persones o mes"};

    //Para usar en listado de actividades del monitor
    private int ocupation;
    private String photoPrincipal;

    //Para usar en oferta de actividades
    private String activityTypeName;
    private String activityTypeLevel;

    //Para usar en reservas
    private float reservationPriceTotal;
    private int idReservation;


    //Constructor
    public Activity() {
        tarifaMenores = new ActivityRates();
        tarifaMenores.setRateName(rateNames[0]);

        tarifaEstudiantes = new ActivityRates();
        tarifaEstudiantes.setRateName(rateNames[1]);

        tarifaMayores = new ActivityRates();
        tarifaMayores.setRateName(rateNames[2]);

        tarifaGrupos = new ActivityRates();
        tarifaGrupos.setRateName(rateNames[3]);

    }

    //Getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public LocalDate getDates() {
        return dates;
    }

    public String getDescription() {
        return description;
    }

    public float getPricePerPerson() {
        return pricePerPerson;
    }

    public int getMaxNumberPeople() {
        return maxNumberPeople;
    }

    public int getMinNumberPeople() {
        return minNumberPeople;
    }

    public String getMeetingPoint() {
        return meetingPoint;
    }

    public LocalTime getMeetingTime() {
        return meetingTime;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public String getState() {
        return state;
    }

    public int getActivityType() {
        return activityType;
    }

    public int getIdInstructor() {
        return idInstructor;
    }

    public String getCancelationReason() {
        return cancelationReason;
    }

    public ActivityRates getTarifaMenores() {
        return tarifaMenores;
    }

    public ActivityRates getTarifaEstudiantes() {
        return tarifaEstudiantes;
    }

    public ActivityRates getTarifaMayores() {
        return tarifaMayores;
    }

    public ActivityRates getTarifaGrupos() {
        return tarifaGrupos;
    }

    public int getOcupation() {
        return ocupation;
    }

    public String getPhotoPrincipal() {
        return photoPrincipal;
    }

    public String getActivityTypeName() {
        return activityTypeName;
    }

    public String getActivityTypeLevel() {
        return activityTypeLevel;
    }

    public float getReservationPriceTotal() {
        return reservationPriceTotal;
    }

    public int getIdReservation() {
        return idReservation;
    }

    //Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDates(LocalDate dates) {
        this.dates = dates;
    }

    public void setPricePerPerson(float pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public void setMaxNumberPeople(int maxNumberPeople) {
        this.maxNumberPeople = maxNumberPeople;
    }

    public void setMinNumberPeople(int minNumberPeople) {
        this.minNumberPeople = minNumberPeople;
    }

    public void setMeetingPoint(String meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public void setMeetingTime(LocalTime meetingTime) {
        this.meetingTime = meetingTime;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public void setIdInstructor(int idInstructor) {
        this.idInstructor = idInstructor;
    }

    public void setCancelationReason(String cancelationReason) {
        this.cancelationReason = cancelationReason;
    }

    public void setTarifaMenores(ActivityRates tarifaMenores) {
        this.tarifaMenores = tarifaMenores;
    }

    public void setTarifaEstudiantes(ActivityRates tarifaEstudiantes) {
        this.tarifaEstudiantes = tarifaEstudiantes;
    }

    public void setTarifaMayores(ActivityRates tarifaMayores) {
        this.tarifaMayores = tarifaMayores;
    }

    public void setTarifaGrupos(ActivityRates tarifaGrupos) {
        this.tarifaGrupos = tarifaGrupos;
    }

    public void setOcupation(int ocupation) {this.ocupation = ocupation;}

    public void setPhotoPrincipal(String photoPrincipal) {
        this.photoPrincipal = photoPrincipal;
    }

    public void setActivityTypeName(String activityTypeName) {
        this.activityTypeName = activityTypeName;
    }

    public void setActivityTypeLevel(String activityTypeLevel) {
        this.activityTypeLevel = activityTypeLevel;
    }

    public void setReservationPriceTotal(float reservationPriceTotal) {
        this.reservationPriceTotal = reservationPriceTotal;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    //To String
    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", description='" + description + '\'' +
                ", dates=" + dates +
                ", pricePerPerson=" + pricePerPerson +
                ", maxNumberPeople=" + maxNumberPeople +
                ", minNumberPeople=" + minNumberPeople +
                ", meetingPoint='" + meetingPoint + '\'' +
                ", meetingTime=" + meetingTime +
                ", creationDate=" + creationDate +
                ", state='" + state + '\'' +
                ", activityType=" + activityType +
                ", idInstructor=" + idInstructor +
                ", cancellationReason=" + cancelationReason +
                '}';
    }


    public int compareTo(Activity altre) {
        return this.getDates().compareTo(altre.getDates());
    }

}

