package com.example.equipment.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class JDBCUtils {

    @Autowired
    private DataSource dataSource;

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    // Additional helper methods could be added here if not using JdbcTemplate directly
    // But since we are in Spring Boot, injecting this component allows getting raw connections easily.
}
