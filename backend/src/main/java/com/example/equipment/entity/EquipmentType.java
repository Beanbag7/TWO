package com.example.equipment.entity;

import lombok.Data;

@Data
public class EquipmentType {
    private Integer typeId;
    private String typeName;
    private Integer maxBorrowDays;
    private String description;
}
