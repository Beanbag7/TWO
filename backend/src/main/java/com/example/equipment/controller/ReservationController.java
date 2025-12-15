package com.example.equipment.controller;

import com.example.equipment.common.Result;
import com.example.equipment.entity.Reservation;
import com.example.equipment.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public Result<List<Reservation>> findAll() throws Exception {
        return Result.success(reservationService.findAll());
    }

    @GetMapping("/user/{userId}")
    public Result<List<Reservation>> findMyReservations(@PathVariable Integer userId) throws Exception {
        return Result.success(reservationService.findMyReservations(userId));
    }
    
    @PostMapping
    public Result<String> create(@RequestBody Reservation reservation) throws Exception {
        reservationService.create(reservation);
        return Result.success("Reservation created");
    }
    
    @PostMapping("/{id}/approve")
    public Result<String> approve(@PathVariable Integer id, @RequestBody Map<String, Integer> body) throws Exception {
        System.out.println("Approving reservation " + id);
        reservationService.approve(id, body.get("approverId"));
        return Result.success("Approved");
    }
    
    @PostMapping("/{id}/reject")
    public Result<String> reject(@PathVariable Integer id, @RequestBody Map<String, Object> body) throws Exception {
        reservationService.reject(id, (Integer) body.get("approverId"), (String) body.get("reason"));
        return Result.success("Rejected");
    }
}
