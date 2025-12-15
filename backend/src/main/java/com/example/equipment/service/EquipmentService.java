package com.example.equipment.service;

import com.example.equipment.entity.Equipment;
import com.example.equipment.entity.EquipmentType;
import com.example.equipment.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    public List<Equipment> findAll() throws SQLException {
        return equipmentRepository.findAll();
    }

    public Equipment findById(Integer id) throws SQLException {
        return equipmentRepository.findById(id);
    }
    
    public List<EquipmentType> findAllTypes() throws SQLException {
        return equipmentRepository.findAllTypes();
    }
    
    public void create(Equipment equipment) throws SQLException {
        equipmentRepository.save(equipment);
    }
    
    public List<Equipment> findMaintenanceList() throws SQLException {
        return equipmentRepository.findMaintenanceList();
    }
    
    public void markAsRepaired(Integer equipmentId, String notes) throws SQLException {
        equipmentRepository.markAsRepaired(equipmentId, notes);
    }
    
    public List<Equipment> findDamageHistory() throws SQLException {
        return equipmentRepository.findDamageHistory();
    }
    
    public com.example.equipment.dto.DashboardStats getDashboardStats() throws SQLException {
        return equipmentRepository.getDashboardStats();
    }
}
