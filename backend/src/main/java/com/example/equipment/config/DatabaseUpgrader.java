package com.example.equipment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class DatabaseUpgrader {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void upgrade() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check if column exists
            boolean columnExists = false;
            try (ResultSet rs = conn.getMetaData().getColumns(null, null, "reservations", "notes")) {
                if (rs.next()) {
                    columnExists = true;
                }
            }
            
            if (!columnExists) {
                System.out.println("Adding 'notes' column to reservations table...");
                stmt.executeUpdate("ALTER TABLE reservations ADD COLUMN notes VARCHAR(500)");
                System.out.println("'notes' column added successfully.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
