// src/main/java/com/example/attendance/servlet/AdminPaidLeaveListServlet.java
package com.example.attendance.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.PaidLeaveDAO;
import com.example.attendance.dto.PaidLeaveDTO;

@WebServlet("/paidleave/admin/list")
public class AdminPaidLeaveListServlet extends HttpServlet {
    private PaidLeaveDAO dao = new PaidLeaveDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "アクセス権限がありません");
            return;
        }

        List<PaidLeaveDTO> list = dao.findAll();
        req.setAttribute("requests", list);
        req.getRequestDispatcher("/jsp/applyPaidLeaveList.jsp").forward(req, resp);
    }
}
