package com.example.equipment.service;

import com.example.equipment.entity.Reservation;
import com.example.equipment.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> findAll() throws Exception {
        return reservationRepository.findAll();
    }
    
    public List<Reservation> findMyReservations(Integer userId) throws Exception {
        return reservationRepository.findByUserId(userId);
    }

    public void create(Reservation r) throws Exception {
        reservationRepository.save(r);
    }
    
    @Autowired
    private com.example.equipment.repository.BorrowRecordRepository borrowRecordRepository;

    public void approve(Integer id, Integer approverId) throws Exception {
        reservationRepository.updateStatus(id, "已批准", approverId, null);
        
        // Auto create borrow record
        Reservation r = reservationRepository.findById(id);
        if (r != null) {
            com.example.equipment.entity.BorrowRecord br = new com.example.equipment.entity.BorrowRecord();
            br.setUserId(r.getUserId());
            br.setEquipmentId(r.getEquipmentId());
            br.setReservationId(r.getReservationId());
            br.setCheckoutStaffId(approverId);
            br.setNotes("Auto created from approval");
            // Set expected return time to end date 17:00
            br.setExpectedReturnTime(r.getScheduledReturnDate().atTime(17, 0));
            
            borrowRecordRepository.save(br);
        }
    }
    
    public void reject(Integer id, Integer approverId, String reason) throws Exception {
        reservationRepository.updateStatus(id, "已拒绝", approverId, reason);
    }
    
     public void cancel(Integer id) throws Exception {
        // Simple cancel, no constraints checked for simplicity
        reservationRepository.updateStatus(id, "已取消", null, null);
    }
}
