package es.uji.ei1027.toopots.rowMapper;

import es.uji.ei1027.toopots.model.Subscription;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SubscriptionRowMapper implements RowMapper<Subscription> {
    public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {
        Subscription subscription = new Subscription();
        subscription.setIdActivityType(rs.getInt("idActivityType"));
        subscription.setIdCustomer(rs.getInt("idCustomer"));

        return subscription;
    }
}
