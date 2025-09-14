package com.example.attendance.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.attendance.dao.PaidLeaveDAO;
import com.example.attendance.dto.User;

@WebServlet("/paidleave/delete")
public class DeletePaidLeaveServlet extends HttpServlet {
    private final PaidLeaveDAO dao = new PaidLeaveDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
      User user = (User)req.getSession().getAttribute("user");
      if (user == null) {
        resp.sendRedirect(req.getContextPath()+"/login.jsp");
        return;
      }

      int id = Integer.parseInt(req.getParameter("id"));
      dao.delete(id, user.getUsername());
      resp.sendRedirect(req.getContextPath() + "/paidleave/list");
    }
}
