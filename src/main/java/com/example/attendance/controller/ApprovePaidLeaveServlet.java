// src/main/java/com/example/attendance/servlet/ApprovePaidLeaveServlet.java
package com.example.attendance.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.PaidLeaveDAO;

@WebServlet("/paidleave/admin/approve")
public class ApprovePaidLeaveServlet extends HttpServlet {
    private PaidLeaveDAO dao = new PaidLeaveDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "アクセス権限がありません");
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));
        String action = req.getParameter("action");
        String newStatus = "approve".equals(action) ? "APPROVED" : "REJECTED";
        dao.updateStatus(id, newStatus);

        resp.sendRedirect(req.getContextPath() + "/paidleave/admin/list");
    }
}
