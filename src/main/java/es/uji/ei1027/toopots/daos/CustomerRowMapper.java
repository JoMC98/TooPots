package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer> {
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("idCustomer"));
        customer.setNif(rs.getString("nif"));
        customer.setName(rs.getString("name"));
        customer.setMail(rs.getString("mail"));
        customer.setSex(rs.getString("sex"));
        customer.setBirthDate(rs.getDate("birthDate"));
        customer.setRegisterDate(rs.getDate("registerDate"));
        return customer;
    }
}
