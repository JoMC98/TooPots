package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Instructor;
import es.uji.ei1027.toopots.rowMapper.InstructorRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InstructorDao {

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix el Instructor a la base de dades */
    public void addInstructor(Instructor instructor, int id) {
        jdbcTemplate.update("INSERT INTO Instructor VALUES(?, ?, ?, ?)",
                id, instructor.getPhoto(), instructor.getResidence(), instructor.getAccountNumber());
    }


    /* Actualitza els atributs del Instructor
       (excepte el id, que és la clau primària) */
    public void updateInstructor(Instructor instructor) {
        jdbcTemplate.update("UPDATE Instructor SET residence=?, accountNumber=? where idInstructor = ?",
                instructor.getResidence(), instructor.getAccountNumber(), instructor.getId());
    }

    /* Obté l'Instructor amb el id donat. Torna null si no existeix. */
    public Instructor getInstructor(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Instructor WHERE idInstructor=?",
                    new InstructorRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

}
