package com.example.download.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(User user) {
        String sql = "INSERT INTO user (user_name, password, email, enabled) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUserName(), user.getPassword(), user.getEmail(), user.isEnabled() ? 1 : 0);
    }

    public User findByUserName(String userName) {
        String sql = "SELECT * FROM user WHERE user_name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), userName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void enableUser(String email) {
        String sql = "UPDATE user SET enabled = 1 WHERE email = ?";
        jdbcTemplate.update(sql, email);
    }

    public boolean existsByUserName(String userName) {
        String sql = "SELECT COUNT(*) FROM user WHERE user_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userName);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}