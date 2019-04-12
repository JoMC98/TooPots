package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.ActivityRates;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityRatesRowMapper implements RowMapper<ActivityRates> {
    public ActivityRates mapRow(ResultSet rs, int rowNum) throws SQLException {
        ActivityRates activityRates = new ActivityRates();
        activityRates.setIdActivity(rs.getInt("idActivity"));
        activityRates.setRateName(rs.getString("rateName"));
        activityRates.setPrice(rs.getFloat("price"));
        return activityRates;
    }
}