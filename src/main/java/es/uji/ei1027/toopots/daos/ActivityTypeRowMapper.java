package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.ActivityType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityTypeRowMapper implements RowMapper<ActivityType> {
    public ActivityType mapRow(ResultSet rs, int rowNum) throws SQLException {
        ActivityType activityType = new ActivityType();
        activityType.setId(rs.getInt("idActivityType"));
        activityType.setName(rs.getString("nameType"));
        activityType.setLevel(rs.getString("activityLevel"));
        activityType.setDescription(rs.getString("description"));
        return activityType;
    }
}
