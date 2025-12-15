package com.example.equipment.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Integer reservationId;
    private Integer userId;
    private Integer equipmentId;
    private LocalDate scheduledBorrowDate;
    private LocalDate scheduledReturnDate;
    private String status; // 待审核, 已批准, etc.
    private LocalDateTime reservationTime;
    private Integer approverId;
    private LocalDateTime approvalTime;
    private String rejectionReason;
    
    // Joins
    private String userName;
    private String equipmentName;
    private String notes;
}
