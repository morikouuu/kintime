package com.example.attendance.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.attendance.dao.PaidLeaveDAO;
import com.example.attendance.dto.PaidLeaveDTO;
import com.example.attendance.dto.User;

@WebServlet("/paidleave/edit")
public class EditPaidLeaveServlet extends HttpServlet {
    private final PaidLeaveDAO dao = new PaidLeaveDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
      User user = (User)req.getSession().getAttribute("user");
      if (user == null) {
        resp.sendRedirect(req.getContextPath()+"/login.jsp");
        return;
      }

      int id = Integer.parseInt(req.getParameter("id"));
      PaidLeaveDTO dto = dao.findById(id);
      if (dto == null 
       || !dto.getUsername().equals(user.getUsername())
       || !"PENDING".equals(dto.getStatus())) {
        resp.sendRedirect(req.getContextPath() + "/paidleave/list");
        return;
      }

      req.setAttribute("dto", dto);
      req.getRequestDispatcher("/jsp/editPaidLeave.jsp")
         .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
      User user = (User)req.getSession().getAttribute("user");
      if (user == null) {
        resp.sendRedirect(req.getContextPath()+"/login.jsp");
        return;
      }

      int    id    = Integer.parseInt(req.getParameter("id"));
      String start = req.getParameter("startDate");
      String end   = req.getParameter("endDate");
      String reason= req.getParameter("reason");

      try {
        dao.update(id, user.getUsername(), start, end, reason);
        resp.sendRedirect(req.getContextPath() + "/paidleave/list");
      } catch (Exception e) {
        req.setAttribute("error", "更新に失敗しました");
        PaidLeaveDTO dto = dao.findById(id);
        req.setAttribute("dto", dto);
        req.getRequestDispatcher("/jsp/editPaidLeave.jsp")
           .forward(req, resp);
      }
    }
}
