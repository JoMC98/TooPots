package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Certification;
import es.uji.ei1027.toopots.rowMapper.CertificationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CertificationDao {

    private JdbcTemplate jdbcTemplate;

    @Value("${upload.file.directory}")
    private String uploadDirectory;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix el Certificat a la base de dades */
    public void addCertification(Certification certification) {
        jdbcTemplate.update("INSERT INTO Certification VALUES(DEFAULT, ?, ?, ?)",
                certification.getCertificate(), certification.getDoc(),certification.getIdInstructor());
    }

    /* Obté els certificats de l'instructor. Torna null si no existeix. */
    public List<Certification> getCertifications(int idInstructor) {
        try {
            return jdbcTemplate.query("SELECT * from Certification WHERE idInstructor=?",
                    new CertificationRowMapper(), idInstructor);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

}
