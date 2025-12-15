package com.example.equipment.entity;

import lombok.Data;

@Data
public class User {
    private Integer userId;
    private String fullName;
    private String password;
    private String employeeStudentId;
    private String phoneNumber;
    private String email;
    private String department;
    private String userType;
    private Boolean isBlacklisted;
}
