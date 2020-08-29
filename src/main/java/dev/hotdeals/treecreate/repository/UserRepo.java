package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles communication between the backend and the database
 * Uses the User Model class to represent the 'user' table from the 'treecreate' schema
 */
@Repository
public class UserRepo
{
    @Autowired
    JdbcTemplate template; // handles actual query calls to the DB

    public UserRepo()
    {
    }

    public List<User> fetchAll()
    {
        String query = "SELECT * FROM user";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
        List<User> userList;
        try
        {
            userList = template.query(query, userRowMapper);
        } catch (EmptyResultDataAccessException e)
        {
            userList = new ArrayList<>(); // return an empty arrayList
        }
        return userList;
    }

    public User fetchById(int id)
    {
        String query = "SELECT * FROM user WHERE id = ? LIMIT 1";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
        User user;
        try
        {
            user = template.queryForObject(query, userRowMapper, id);
        } catch (EmptyResultDataAccessException e)
        {
            user = null;
        }
        return user;
    }

    public User searchByUsername(String username)
    {
        String query = "SELECT * FROM user WHERE username = ? LIMIT 1";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class); // a collection type that holds the results of the query
        User user; // list of users that will be returned
        try
        {
            user = template.queryForObject(query, userRowMapper, username);
        } catch (EmptyResultDataAccessException e)
        {
            user = null;
        }
        return user;
    }

    public boolean addUser(User user)
    {
        String query = "INSERT INTO user (username, password, email, access_level) VALUES (?, ?, ?, ?)";
        int rowsAffected; // Used for validating success
        try
        {
            rowsAffected = template.update(query, user.getUsername(), user.getPassword(), user.getEmail(), user.getAccessLevel());
        } catch (EmptyResultDataAccessException e)
        {
            rowsAffected = 0;
        }
        return rowsAffected > 0; // returns false if no rows were affected, aka the insert has failed
    }

    public boolean deleteUser(int id)
    {
        String query = "DELETE FROM user WHERE id = ?";
        int rowsAffected; // Used for validating success
        try
        {
            rowsAffected = template.update(query, id); // call the database
        } catch (EmptyResultDataAccessException e)
        {
            rowsAffected = 0;
        }
        return rowsAffected > 0; // returns false if no rows were affected, aka the insert has failed
    }
}