package com.example.attendance.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.PaidLeaveDAO;
import com.example.attendance.dto.PaidLeaveDTO;
import com.example.attendance.dto.User;

@WebServlet("/paidleave/list")
public class PaidLeaveListServlet extends HttpServlet {
    private final PaidLeaveDAO dao = new PaidLeaveDAO();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
      HttpSession session = req.getSession(false);
      User user = (session != null) 
        ? (User) session.getAttribute("user")
        : null;
      if (user == null) {
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
        return;
      }

      List<PaidLeaveDTO> all = dao.findAll();
      // 自分＋申請中のみ
      List<PaidLeaveDTO> mine = all.stream()
        .filter(d -> d.getUsername().equals(user.getUsername()))
        .filter(d -> "PENDING".equals(d.getStatus()))
        .collect(Collectors.toList());

      req.setAttribute("list", mine);
      req.getRequestDispatcher("/jsp/paidLeaveList.jsp")
         .forward(req, resp);
    }
}
