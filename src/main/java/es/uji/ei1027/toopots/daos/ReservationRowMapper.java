package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.Reservation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class ReservationRowMapper implements RowMapper<Reservation> {
    public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("idReservation"));
        reservation.setBookingDate(rs.getDate("bookingDate"));
        reservation.setActivityDate(rs.getDate("activityDate"));
        reservation.setNumUnder16(rs.getInt("numUnder16"));
        reservation.setNumStudents(rs.getInt("numStudents"));
        reservation.setNumAdults(rs.getInt("numAdults"));
        reservation.setNumOver60(rs.getInt("numOver60"));
        reservation.setTotalPrice(rs.getInt("totalPrice"));
        reservation.setTransactionNumber(rs.getInt("transactionNumber"));
        reservation.setIdActivity(rs.getInt("idActivity"));
        reservation.setIdCustomer(rs.getInt("idCustomer"));
        reservation.setState(rs.getString("state"));

        return reservation;
    }
}