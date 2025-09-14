
package com.example.attendance.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
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
    
    public PaidLeaveDTO findById(int id) {
        return store.stream()
            .filter(d -> d.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public void update(int id, String username, String start, String end, String reason) {
        for (PaidLeaveDTO dto : store) {
            if (dto.getId() == id
             && dto.getUsername().equals(username)
             && "PENDING".equals(dto.getStatus())) {
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setReason(reason);
                break;
            }
        }
    }
    
    public void delete(int id, String username) {
        Iterator<PaidLeaveDTO> it = store.iterator();
        while (it.hasNext()) {
            PaidLeaveDTO dto = it.next();
            if (dto.getId() == id
             && dto.getUsername().equals(username)
             && "PENDING".equals(dto.getStatus())) {
                it.remove();
                break;
            }
        }
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
