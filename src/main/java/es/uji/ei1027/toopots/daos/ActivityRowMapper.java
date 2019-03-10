package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Activity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class ActivityRowMapper implements RowMapper<Activity> {
    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getInt("idActivity"));
        activity.setName(rs.getString("name"));
        activity.setPlace(rs.getString("place"));
        activity.setPricePerPerson(rs.getFloat("pricePerPerson"));
        activity.setMaxNumberPeople(rs.getInt("maxNumberPeople"));
        activity.setMinNumberPeople(rs.getInt("minNumberPeople"));
        activity.setMeetingPoint(rs.getString("meetingPoint"));
        activity.setMeetingTime(rs.getObject("meetingTime", LocalTime.class));
        activity.setCreationDate(rs.getDate("creationDate"));
        activity.setState(rs.getString("state"));
        activity.setActivityType(rs.getInt("activityType"));
        activity.setIdInstructor(rs.getInt("idInstructor"));
        return activity;
    }
}