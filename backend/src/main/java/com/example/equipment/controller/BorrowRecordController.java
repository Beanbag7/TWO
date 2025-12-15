package com.example.equipment.controller;

import com.example.equipment.common.Result;
import com.example.equipment.entity.BorrowRecord;
import com.example.equipment.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrowDB")
@CrossOrigin(origins = "*")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @GetMapping
    public Result<List<BorrowRecord>> findAll() throws Exception {
        return Result.success(borrowRecordService.findAll());
    }

    @GetMapping("/user/{userId}")
    public Result<List<BorrowRecord>> findMyRecords(@PathVariable Integer userId) throws Exception {
        return Result.success(borrowRecordService.findMyRecords(userId));
    }

    @PostMapping("/borrow")
    public Result<String> borrow(@RequestBody BorrowRecord record) throws Exception {
        borrowRecordService.borrow(record);
        return Result.success("Borrowed successfully");
    }

    @PostMapping("/{id}/return")
    public Result<String> returnEquipment(@PathVariable Integer id, @RequestBody Map<String, Object> body) throws Exception {
        borrowRecordService.returnEquipment(id, (Integer) body.get("returnStaffId"), (String) body.get("notes"));
        return Result.success("Returned successfully");
    }
}
