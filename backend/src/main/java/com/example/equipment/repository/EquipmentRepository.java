package com.example.equipment.repository;

import com.example.equipment.dto.DashboardStats;
import com.example.equipment.entity.Equipment;
import com.example.equipment.entity.EquipmentType;
import com.example.equipment.utils.JDBCUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EquipmentRepository {

    @Autowired
    private JDBCUtils jdbcUtils;

    public List<Equipment> findAll() throws SQLException {
        String sql = "SELECT e.*, t.type_name FROM equipments e LEFT JOIN equipment_types t ON e.type_id = t.type_id";
        List<Equipment> list = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapEquipment(rs));
            }
        }
        return list;
    }
    
    public List<EquipmentType> findAllTypes() throws SQLException {
        String sql = "SELECT * FROM equipment_types";
        List<EquipmentType> list = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapType(rs));
            }
        }
        return list;
    }
    
    public void save(Equipment e) throws SQLException {
        String sql = "INSERT INTO equipments (type_id, equipment_name, brand, model, serial_number, status, purchase_date, price, storage_location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
         try (Connection conn = jdbcUtils.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, e.getTypeId());
             stmt.setString(2, e.getEquipmentName());
             stmt.setString(3, e.getBrand());
             stmt.setString(4, e.getModel());
             stmt.setString(5, e.getSerialNumber());
             stmt.setString(6, "可用");
             if (e.getPurchaseDate() != null) stmt.setDate(7, Date.valueOf(e.getPurchaseDate())); else stmt.setNull(7, Types.DATE);
             stmt.setBigDecimal(8, e.getPrice());
             stmt.setString(9, e.getStorageLocation());
             stmt.executeUpdate();
         }
    }

    public Equipment findById(Integer id) throws SQLException {
         String sql = "SELECT e.*, t.type_name FROM equipments e LEFT JOIN equipment_types t ON e.type_id = t.type_id WHERE e.equipment_id = ?";
         try (Connection conn = jdbcUtils.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, id);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     return mapEquipment(rs);
                 }
             }
         }
         return null;
    }

    private Equipment mapEquipment(ResultSet rs) throws SQLException {
        Equipment e = new Equipment();
        e.setEquipmentId(rs.getInt("equipment_id"));
        e.setTypeId(rs.getInt("type_id"));
        e.setEquipmentName(rs.getString("equipment_name"));
        e.setBrand(rs.getString("brand"));
        e.setModel(rs.getString("model"));
        e.setSerialNumber(rs.getString("serial_number"));
        e.setStatus(rs.getString("status"));
        Date pDate = rs.getDate("purchase_date");
        if (pDate != null) e.setPurchaseDate(pDate.toLocalDate());
        e.setPrice(rs.getBigDecimal("price"));
        e.setStorageLocation(rs.getString("storage_location"));
        // Join field
        e.setTypeName(rs.getString("type_name"));
        return e;
    }

    private EquipmentType mapType(ResultSet rs) throws SQLException {
        EquipmentType t = new EquipmentType();
        t.setTypeId(rs.getInt("type_id"));
        t.setTypeName(rs.getString("type_name"));
        t.setMaxBorrowDays(rs.getInt("max_borrow_days"));
        t.setDescription(rs.getString("description"));
        return t;
    }
    public List<Equipment> findMaintenanceList() throws SQLException {
        // Query to get equipment in maintenance/damage status with their latest unresolved damage record
        String sql = "SELECT e.*, t.type_name, dr.damage_description, dr.damage_time " +
                     "FROM equipments e " +
                     "LEFT JOIN equipment_types t ON e.type_id = t.type_id " +
                     "LEFT JOIN borrow_records br ON br.equipment_id = e.equipment_id " +
                     "LEFT JOIN damage_records dr ON dr.borrow_record_id = br.record_id " +
                     "WHERE e.status IN ('维护中', '损坏') AND (dr.is_resolved = FALSE OR dr.is_resolved IS NULL) " +
                     "ORDER BY dr.damage_time DESC";
        
        List<Equipment> list = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Integer> processedIds = new ArrayList<>();
            while (rs.next()) {
                int eqId = rs.getInt("equipment_id");
                // Avoid duplicates due to multiple damage records (though we try to filter resolved)
                if (processedIds.contains(eqId)) continue;
                processedIds.add(eqId);
                
                Equipment e = mapEquipment(rs);
                e.setDamageDescription(rs.getString("damage_description"));
                Timestamp dt = rs.getTimestamp("damage_time");
                if (dt != null) e.setDamageTime(dt.toLocalDateTime());
                list.add(e);
            }
        }
        return list;
    }

    public void markAsRepaired(Integer equipmentId, String notes) throws SQLException {
        String updateEqSql = "UPDATE equipments SET status = '可用' WHERE equipment_id = ?";
        // Find unrelated damage records for this equipment to mark resolved? 
        // We know damage_record links to borrow_record links to equipment.
        String resolveDamageSql = "UPDATE damage_records dr " +
                                  "JOIN borrow_records br ON dr.borrow_record_id = br.record_id " +
                                  "SET dr.is_resolved = TRUE, dr.resolution_notes = ? " +
                                  "WHERE br.equipment_id = ? AND dr.is_resolved = FALSE";
                                  
        try (Connection conn = jdbcUtils.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update Equipment
                try (PreparedStatement stmt = conn.prepareStatement(updateEqSql)) {
                    stmt.setInt(1, equipmentId);
                    stmt.executeUpdate();
                }
                
                // Resolve Damages
                try (PreparedStatement stmt = conn.prepareStatement(resolveDamageSql)) {
                    stmt.setString(1, notes);
                    stmt.setInt(2, equipmentId);
                    stmt.executeUpdate();
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
    
    public List<Equipment> findDamageHistory() throws SQLException {
        String sql = "SELECT e.*, t.type_name, dr.damage_description, dr.damage_time, dr.resolution_notes, dr.is_resolved " +
                     "FROM damage_records dr " +
                     "JOIN borrow_records br ON dr.borrow_record_id = br.record_id " +
                     "JOIN equipments e ON br.equipment_id = e.equipment_id " +
                     "LEFT JOIN equipment_types t ON e.type_id = t.type_id " +
                     "WHERE dr.is_resolved = TRUE " +
                     "ORDER BY dr.damage_time DESC";
                     
        List<Equipment> list = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Equipment e = mapEquipment(rs);
                e.setDamageDescription(rs.getString("damage_description"));
                Timestamp dt = rs.getTimestamp("damage_time");
                if (dt != null) e.setDamageTime(dt.toLocalDateTime());
                e.setResolutionNotes(rs.getString("resolution_notes"));
                list.add(e);
            }
        }
        return list;
    }
    public DashboardStats getDashboardStats() throws SQLException {
        DashboardStats stats = new DashboardStats();
        String sqlEq = "SELECT COUNT(*) as total, " +
                       "SUM(CASE WHEN status = '可用' THEN 1 ELSE 0 END) as available, " +
                       "SUM(CASE WHEN status = '借出' THEN 1 ELSE 0 END) as borrowed, " +
                       "SUM(CASE WHEN status IN ('维护中', '损坏') THEN 1 ELSE 0 END) as maintenance " +
                       "FROM equipments";
                       
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlEq);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.setTotalEquipments(rs.getInt("total"));
                stats.setAvailableEquipments(rs.getInt("available"));
                stats.setBorrowedEquipments(rs.getInt("borrowed"));
                stats.setMaintenanceEquipments(rs.getInt("maintenance"));
            }
        }
        
        String sqlRes = "SELECT COUNT(*) FROM reservations WHERE status = '待审核'";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlRes);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.setPendingReservations(rs.getInt(1));
            }
        }
        
        return stats;
    }
}
