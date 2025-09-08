package com.example.attendance.controller;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.NotificationDAO;
import com.example.attendance.dao.PaidLeaveDAO;
import com.example.attendance.dto.PaidLeaveDTO;         

@WebServlet("/paidleave/admin/approve")
public class ApprovePaidLeaveServlet extends HttpServlet {
    private final PaidLeaveDAO dao              = new PaidLeaveDAO();
    private final NotificationDAO notificationDao = new NotificationDAO(); 

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (!"admin".equalsIgnoreCase(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "アクセス権限がありません");
            return;
        }

        int id         = Integer.parseInt(req.getParameter("id"));
        String action  = req.getParameter("action");
        String newStatus = "approve".equals(action) ? "APPROVED" : "REJECTED";

        
        dao.updateStatus(id, newStatus);

       
        Optional<PaidLeaveDTO> opt = dao.findAll().stream()
            .filter(d -> d.getId() == id)
            .findFirst();
        if (opt.isPresent()) {
            PaidLeaveDTO dto = opt.get();
            String username = dto.getUsername();
            String msg = String.format("有給申請(ID=%d)が%sされました。", 
                                        id, 
                                        newStatus.equals("APPROVED") ? "承認" : "却下");
            notificationDao.add(username, msg);
        }
        

        resp.sendRedirect(req.getContextPath() + "/paidleave/admin/list");
    }
}
