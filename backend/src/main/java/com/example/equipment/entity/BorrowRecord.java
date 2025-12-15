package com.example.equipment.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorrowRecord {
    private Integer recordId;
    private Integer userId;
    private Integer equipmentId;
    private Integer reservationId;
    private LocalDateTime actualBorrowTime;
    private LocalDateTime expectedReturnTime;
    private LocalDateTime actualReturnTime;
    private String borrowStatus; // 使用中, 已归还, 逾期, 损坏
    private Integer checkoutStaffId;
    private Integer returnStaffId;
    private String notes;
    
    // Join details
    private String userName;
    private String equipmentName;
}
