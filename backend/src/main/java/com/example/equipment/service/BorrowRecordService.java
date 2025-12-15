package com.example.equipment.service;

import com.example.equipment.entity.BorrowRecord;
import com.example.equipment.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    public List<BorrowRecord> findAll() throws Exception {
        return borrowRecordRepository.findAll();
    }
    
    public List<BorrowRecord> findMyRecords(Integer userId) throws Exception {
        return borrowRecordRepository.findByUserId(userId);
    }
    
    public void borrow(BorrowRecord record) throws Exception {
        borrowRecordRepository.save(record);
    }
    
    public void returnEquipment(Integer id, Integer returnStaffId, String notes) throws Exception {
        borrowRecordRepository.returnEquipment(id, returnStaffId, notes);
    }
}
