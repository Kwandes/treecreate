package dev.hotdeals.treecreate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LandingPageRepo
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean addNewsletterEmail(String timePlusDate, String newsletterEmail)
    {
        // Alex suggested the timePLusDate name
        String query = "INSERT INTO newsletterEmail ( timePlusDate, email ) VALUES ( ?, ?)";
        int rowsAffected;
        rowsAffected = jdbcTemplate.update(query, timePlusDate, newsletterEmail);
        boolean status = rowsAffected > 0; //sets to TRUE if rows have been affected, sets to FALSE if no row has been affected
        return status;
    }

}
