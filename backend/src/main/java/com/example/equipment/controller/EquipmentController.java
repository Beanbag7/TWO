package com.example.equipment.controller;

import com.example.equipment.common.Result;
import com.example.equipment.entity.Equipment;
import com.example.equipment.entity.EquipmentType;
import com.example.equipment.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
@CrossOrigin(origins = "*")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping
    public Result<List<Equipment>> findAll() throws Exception {
        return Result.success(equipmentService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Equipment> findById(@PathVariable Integer id) throws Exception {
        return Result.success(equipmentService.findById(id));
    }
    
    @GetMapping("/types")
    public Result<List<EquipmentType>> findAllTypes() throws Exception {
        return Result.success(equipmentService.findAllTypes());
    }
    
    @PostMapping
    public Result<String> create(@RequestBody Equipment equipment) throws Exception {
        equipmentService.create(equipment);
        return Result.success("Equipment created successfully");
    }
    
    @GetMapping("/maintenance")
    public Result<List<Equipment>> findMaintenanceList() throws Exception {
        return Result.success(equipmentService.findMaintenanceList());
    }
    
    @PostMapping("/{id}/repair")
    public Result<String> markAsRepaired(@PathVariable Integer id, @RequestBody java.util.Map<String, String> body) throws Exception {
        equipmentService.markAsRepaired(id, body.get("notes"));
        return Result.success("Equipment marked as repaired");
    }
    
    @GetMapping("/history")
    public Result<List<Equipment>> findDamageHistory() throws Exception {
        return Result.success(equipmentService.findDamageHistory());
    }
    
    @GetMapping("/stats")
    public Result<com.example.equipment.dto.DashboardStats> getDashboardStats() throws Exception {
        return Result.success(equipmentService.getDashboardStats());
    }
}
