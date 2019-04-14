package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CustomerRowMapper implements RowMapper<Customer> {
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("idCustomer"));
        customer.setSex(rs.getString("sex"));
        customer.setBirthDate(rs.getObject("birthDate", LocalDate.class));
        return customer;
    }
}
