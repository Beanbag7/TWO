package com.example.equipment.controller;

import com.example.equipment.common.Result;
import com.example.equipment.dto.LoginRequest;
import com.example.equipment.entity.User;
import com.example.equipment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow frontend
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody LoginRequest request) throws Exception {
        User user = userService.login(request);
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody User user) throws Exception {
        userService.register(user);
        return Result.success("Registered successfully");
    }
    
    // Add profile, etc.
}
