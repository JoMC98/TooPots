package es.uji.ei1027.toopots.rowMapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SubscriptionRowMapper implements RowMapper<Integer> {
    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer integer = rs.getInt("activityType");
        return integer;
    }
}

