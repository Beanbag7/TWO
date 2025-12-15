package com.example.equipment.repository;

import com.example.equipment.entity.BorrowRecord;
import com.example.equipment.utils.JDBCUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BorrowRecordRepository {

    @Autowired
    private JDBCUtils jdbcUtils;

    public List<BorrowRecord> findAll() throws SQLException {
        String sql = "SELECT b.*, u.full_name, e.equipment_name FROM borrow_records b " +
                     "LEFT JOIN users u ON b.user_id = u.user_id " +
                     "LEFT JOIN equipments e ON b.equipment_id = e.equipment_id ORDER BY b.actual_borrow_time DESC";
        return queryList(sql);
    }
    
    public List<BorrowRecord> findByUserId(Integer userId) throws SQLException {
         String sql = "SELECT b.*, u.full_name, e.equipment_name FROM borrow_records b " +
                     "LEFT JOIN users u ON b.user_id = u.user_id " +
                     "LEFT JOIN equipments e ON b.equipment_id = e.equipment_id WHERE b.user_id = ? ORDER BY b.actual_borrow_time DESC";
        return queryList(sql, userId);
    }

    public void save(BorrowRecord b) throws SQLException {
        String sql = "INSERT INTO borrow_records (user_id, equipment_id, reservation_id, actual_borrow_time, expected_return_time, borrow_status, checkout_staff_id, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateEqSql = "UPDATE equipments SET status = '借出' WHERE equipment_id = ?";
         try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, b.getUserId());
            stmt.setInt(2, b.getEquipmentId());
            if (b.getReservationId() != null) stmt.setInt(3, b.getReservationId()); else stmt.setNull(3, Types.INTEGER);
            if (b.getExpectedReturnTime() == null || b.getExpectedReturnTime().isBefore(LocalDateTime.now())) {
                 throw new IllegalArgumentException("预计归还时间不能为空且必须晚于当前时间");
            }
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(5, Timestamp.valueOf(b.getExpectedReturnTime()));
            stmt.setString(6, "使用中");
            stmt.setInt(7, b.getCheckoutStaffId());
            stmt.setString(8, b.getNotes());
            stmt.executeUpdate();
            
            // Update Equipment Status
            try (PreparedStatement updateStmt = conn.prepareStatement(updateEqSql)) {
                updateStmt.setInt(1, b.getEquipmentId());
                updateStmt.executeUpdate();
            }
        }
    }
    
    public void returnEquipment(Integer id, Integer returnStaffId, String notes) throws SQLException {
        String sql = "UPDATE borrow_records SET actual_return_time = ?, borrow_status = ?, return_staff_id = ?, notes = ? WHERE record_id = ?";
        String getRecordDetailsSql = "SELECT equipment_id, user_id FROM borrow_records WHERE record_id = ?";
        String updateEqSql = "UPDATE equipments SET status = ? WHERE equipment_id = ?";
        String insertDamageSql = "INSERT INTO damage_records (borrow_record_id, reported_by, damage_description, severity, is_resolved, damage_time) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = jdbcUtils.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            try {
                // Determine status
                String status = (notes != null && notes.contains("Damaged")) ? "损坏" : "已归还";
                
                // 1. Update Borrow Record
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setString(2, status);
                    if (returnStaffId != null) stmt.setInt(3, returnStaffId); else stmt.setNull(3, Types.INTEGER);
                    stmt.setString(4, notes);
                    stmt.setInt(5, id);
                    stmt.executeUpdate();
                }

                // 2. Get Details (Equipment ID and User ID)
                int eqId = -1;
                int userId = -1;
                try (PreparedStatement getStmt = conn.prepareStatement(getRecordDetailsSql)) {
                    getStmt.setInt(1, id);
                    try (ResultSet rs = getStmt.executeQuery()) {
                        if (rs.next()) {
                            eqId = rs.getInt("equipment_id");
                            userId = rs.getInt("user_id");
                        }
                    }
                }

                // 3. Update Equipment Status
                if (eqId != -1) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateEqSql)) {
                        String eqStatus = status.equals("损坏") ? "维护中" : "可用";
                        updateStmt.setString(1, eqStatus);
                        updateStmt.setInt(2, eqId);
                        updateStmt.executeUpdate();
                    }
                }

                // 4. Insert Damage Record if damaged
                if (status.equals("损坏")) {
                    try (PreparedStatement damageStmt = conn.prepareStatement(insertDamageSql)) {
                        damageStmt.setInt(1, id);
                        // If returnStaffId is null (self-return), reported by User. Else reported by Staff.
                        int reporterId = (returnStaffId != null) ? returnStaffId : userId;
                        damageStmt.setInt(2, reporterId);
                        damageStmt.setString(3, notes);
                        damageStmt.setString(4, "中度"); // Default severity
                        damageStmt.setBoolean(5, false); // Not resolved
                        damageStmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                        damageStmt.executeUpdate();
                    }
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private List<BorrowRecord> queryList(String sql, Object... params) throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord b = new BorrowRecord();
                    b.setRecordId(rs.getInt("record_id"));
                    b.setUserId(rs.getInt("user_id"));
                    b.setEquipmentId(rs.getInt("equipment_id"));
                    b.setReservationId(rs.getInt("reservation_id"));
                    if (rs.wasNull()) b.setReservationId(null);
                    b.setActualBorrowTime(rs.getTimestamp("actual_borrow_time").toLocalDateTime());
                    b.setExpectedReturnTime(rs.getTimestamp("expected_return_time").toLocalDateTime());
                    Timestamp art = rs.getTimestamp("actual_return_time");
                    if (art != null) b.setActualReturnTime(art.toLocalDateTime());
                    b.setBorrowStatus(rs.getString("borrow_status"));
                    b.setUserName(rs.getString("full_name"));
                    b.setEquipmentName(rs.getString("equipment_name"));
                    list.add(b);
                }
            }
        }
        return list;
    }
}
