package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Activity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityRowMapper implements RowMapper<Activity> {
    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getInt("idActivity"));
        activity.setName(rs.getString("name"));
        activity.setPlace(rs.getString("place"));
        activity.setDescription(rs.getString("description"));
        activity.setDates(rs.getObject("dates", LocalDate.class));
        activity.setPricePerPerson(rs.getFloat("pricePerPerson"));
        activity.setMaxNumberPeople(rs.getInt("maxNumberPeople"));
        activity.setMinNumberPeople(rs.getInt("minNumberPeople"));
        activity.setMeetingPoint(rs.getString("meetingPoint"));
        activity.setMeetingTime(rs.getObject("meetingTime", LocalTime.class));
        activity.setCreationDate(rs.getObject("creationDate", LocalDate.class));
        activity.setState(rs.getString("state"));
        activity.setActivityType(rs.getInt("activityType"));
        activity.setIdInstructor(rs.getInt("idInstructor"));
        activity.setCancelationReason(rs.getString("cancelationReason"));
        return activity;
    }
}