package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Instructor;
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
    public void addInstructor(Instructor instructor) {
        jdbcTemplate.update("INSERT INTO Instructor VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, DEFAULT, ?, ?)",
                instructor.getName(), instructor.getNif(), instructor.getMail(), instructor.getResidence(),
                instructor.getAccountNumber(),instructor.getUsername(),instructor.getPasswd(),"Acceptat");
    }

    /* Esborra el Instructor de la base de dades */
    public void deleteInstructor(int id) {
        jdbcTemplate.update("DELETE from Instructor where idInstructor=?", id);
    }

    /* Actualitza els atributs del Instructor
       (excepte el id, que és la clau primària) */
    public void updateInstructor(Instructor instructor) {
        jdbcTemplate.update("UPDATE Instructor SET name=?, nif=?, mail=?, residence=?, accountNumber=?, username=?, "+
                        "registerDate=?, passwd=?, state=?",
                instructor.getName(), instructor.getNif(), instructor.getMail(), instructor.getResidence(), instructor.getAccountNumber(),
                instructor.getUsername(),instructor.getRegisterDate(), instructor.getPasswd(), instructor.getState());
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

    /* Obté tots els instructors. Torna una llista buida si no n'hi ha cap. */
    public List<Instructor> getInstructors() {
        try {
            return jdbcTemplate.query("SELECT * from Instructor", new InstructorRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Instructor>();
        }
    }

}
