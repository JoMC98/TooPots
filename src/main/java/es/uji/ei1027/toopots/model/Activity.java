package es.uji.ei1027.toopots.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.Date;

public class Activity {

    //Atributos
    private int id;
    private String name;
    private String place;
    private float pricePerPerson;
    private int maxNumberPeople;
    private int minNumberPeople;
    private String meetingPoint;
    private LocalTime meetingTime;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date creationDate;

    private String state;
    private int activityType;
    private int idInstructor;

    //Constructor
    public Activity() {
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

    public Date getCreationDate() {
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

    public void setCreationDate(Date creationDate) {
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

    //ToString
    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", pricePerPerson=" + pricePerPerson +
                ", maxNumberPeople=" + maxNumberPeople +
                ", minNumberPeople=" + minNumberPeople +
                ", meetingPoint='" + meetingPoint + '\'' +
                ", meetingTime=" + meetingTime +
                ", creationDate=" + creationDate +
                ", state='" + state + '\'' +
                ", activityType=" + activityType +
                ", idInstructor=" + idInstructor +
                '}';
    }
}
