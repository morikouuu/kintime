
package com.example.attendance.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.attendance.dto.PaidLeaveDTO;

public class PaidLeaveDAO {
    private static final List<PaidLeaveDTO> store = new ArrayList<>();
    private static final AtomicInteger idGen = new AtomicInteger(1);
    
    public PaidLeaveDAO() {
        if (store.isEmpty()) {
            insert(new PaidLeaveDTO("employee1",   "2025-09-10", "2025-09-12", "旅行",   "PENDING"));
            
        }
    }

    public void insert(PaidLeaveDTO dto) {
        dto.setId(idGen.getAndIncrement());
        store.add(dto);
    }

    public List<PaidLeaveDTO> findAll() {
        List<PaidLeaveDTO> copy = new ArrayList<>(store);
        copy.sort(Comparator.comparingInt(PaidLeaveDTO::getId).reversed());
        return copy;
    }

    public void updateStatus(int id, String newStatus) {
        for (PaidLeaveDTO dto : store) {
            if (dto.getId() == id) {
                dto.setStatus(newStatus);
                break;
            }
        }
    }
}
