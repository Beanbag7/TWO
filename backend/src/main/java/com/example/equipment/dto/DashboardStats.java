package com.example.equipment.dto;

import lombok.Data;

@Data
public class DashboardStats {
    private int totalEquipments;
    private int availableEquipments;
    private int borrowedEquipments;
    private int maintenanceEquipments;
    private int pendingReservations;
}
