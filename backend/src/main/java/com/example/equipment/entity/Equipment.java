package com.example.equipment.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Equipment {
    private Integer equipmentId;
    private Integer typeId;
    private String equipmentName;
    private String brand;
    private String model;
    private String serialNumber;
    private String status;
    private LocalDate purchaseDate;
    private BigDecimal price;
    private String storageLocation;
    
    // For joining
    private String typeName;
    private String damageDescription;
    private java.time.LocalDateTime damageTime;
    private String resolutionNotes;
}
