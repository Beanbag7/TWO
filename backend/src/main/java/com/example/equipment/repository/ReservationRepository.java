package com.example.equipment.repository;

import com.example.equipment.entity.Reservation;
import com.example.equipment.utils.JDBCUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationRepository {

    @Autowired
    private JDBCUtils jdbcUtils;

    public List<Reservation> findAll() throws SQLException {
        String sql = "SELECT r.*, u.full_name, e.equipment_name FROM reservations r " +
                     "LEFT JOIN users u ON r.user_id = u.user_id " +
                     "LEFT JOIN equipments e ON r.equipment_id = e.equipment_id " + 
                     "ORDER BY r.reservation_time DESC";
        return queryList(sql);
    }
    
    public List<Reservation> findByUserId(Integer userId) throws SQLException {
        String sql = "SELECT r.*, u.full_name, e.equipment_name FROM reservations r " +
                     "LEFT JOIN users u ON r.user_id = u.user_id " +
                     "LEFT JOIN equipments e ON r.equipment_id = e.equipment_id " + 
                     "WHERE r.user_id = ? ORDER BY r.reservation_time DESC";
        return queryList(sql, userId);
    }
    
    public void save(Reservation r) throws SQLException {
        // Ensure table has 'note' column. If not, this throws.
        // For simplicity in this env, we assume we might need to alter table or user handles it? 
        // I'll assume I can add it. 
        String sql = "INSERT INTO reservations (user_id, equipment_id, scheduled_borrow_date, scheduled_return_date, status, reservation_time, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getUserId());
            stmt.setInt(2, r.getEquipmentId());
            stmt.setDate(3, Date.valueOf(r.getScheduledBorrowDate()));
            stmt.setDate(4, Date.valueOf(r.getScheduledReturnDate()));
            stmt.setString(5, "待审核");
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(7, r.getNotes());
            stmt.executeUpdate();
        }
    }
    
    public void updateStatus(Integer id, String status, Integer approverId, String reason) throws SQLException {
        String sql = "UPDATE reservations SET status = ?, approver_id = ?, approval_time = ?, rejection_reason = ? WHERE reservation_id = ?";
         try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            if (approverId != null) stmt.setInt(2, approverId); else stmt.setNull(2, Types.INTEGER);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(4, reason);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }
    }
    
    public Reservation findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation r = new Reservation();
                    r.setReservationId(rs.getInt("reservation_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setEquipmentId(rs.getInt("equipment_id"));
                    r.setScheduledBorrowDate(rs.getDate("scheduled_borrow_date").toLocalDate());
                    r.setScheduledReturnDate(rs.getDate("scheduled_return_date").toLocalDate());
                    r.setStatus(rs.getString("status"));
                    // Should map other fields if needed, but for logic mainly ID fields matter
                    return r;
                }
            }
        }
        return null;
    }

    private List<Reservation> queryList(String sql, Object... params) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setReservationId(rs.getInt("reservation_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setEquipmentId(rs.getInt("equipment_id"));
                    r.setScheduledBorrowDate(rs.getDate("scheduled_borrow_date").toLocalDate());
                    r.setScheduledReturnDate(rs.getDate("scheduled_return_date").toLocalDate());
                    r.setStatus(rs.getString("status"));
                    r.setReservationTime(rs.getTimestamp("reservation_time").toLocalDateTime());
                    r.setUserName(rs.getString("full_name"));
                    r.setEquipmentName(rs.getString("equipment_name"));
                    // Try/Catch for backward compatibility if column missing? 
                    // No, usually strict.
                    try { r.setNotes(rs.getString("notes")); } catch (SQLException e) {} 
                    list.add(r);
                }
            }
        }
        return list;
    }
}
