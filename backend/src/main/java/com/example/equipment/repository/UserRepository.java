package com.example.equipment.repository;

import com.example.equipment.entity.User;
import com.example.equipment.utils.JDBCUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JDBCUtils jdbcUtils;

    public User findByUsername(String employeeStudentId) throws SQLException {
        String sql = "SELECT * FROM users WHERE employee_student_id = ?";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeStudentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }

    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, password, employee_student_id, phone_number, email, department, user_type, is_blacklisted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmployeeStudentId());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getDepartment());
            stmt.setString(7, user.getUserType());
            stmt.setBoolean(8, user.getIsBlacklisted() != null ? user.getIsBlacklisted() : false);
            stmt.executeUpdate();
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setPassword(rs.getString("password"));
        user.setEmployeeStudentId(rs.getString("employee_student_id"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setEmail(rs.getString("email"));
        user.setDepartment(rs.getString("department"));
        user.setUserType(rs.getString("user_type"));
        user.setIsBlacklisted(rs.getBoolean("is_blacklisted"));
        return user;
    }
}
