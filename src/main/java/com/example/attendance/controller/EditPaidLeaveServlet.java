package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.PaidLeaveDAO;
import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.PaidLeaveDTO;
import com.example.attendance.dto.User;

@WebServlet("/paidleave/edit")
public class EditPaidLeaveServlet extends HttpServlet {
    private final PaidLeaveDAO paidLeaveDao = new PaidLeaveDAO();
    private final UserDAO      userDao      = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        // ログインチェック
        HttpSession session = req.getSession(false);
        User user = (session != null) 
            ? (User) session.getAttribute("user")
            : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

       
        int id = Integer.parseInt(req.getParameter("id"));
        PaidLeaveDTO dto = paidLeaveDao.findById(id);
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
        // ログインチェック
        HttpSession session = req.getSession(false);
        User user = (session != null) 
            ? (User) session.getAttribute("user")
            : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        
        int    id           = Integer.parseInt(req.getParameter("id"));
        String startDateStr = req.getParameter("startDate");
        String endDateStr   = req.getParameter("endDate");
        String reason       = req.getParameter("reason");

        
        PaidLeaveDTO oldDto = paidLeaveDao.findById(id);
        if (oldDto == null 
         || !oldDto.getUsername().equals(user.getUsername())
         || !"PENDING".equals(oldDto.getStatus())) {
            resp.sendRedirect(req.getContextPath() + "/paidleave/list");
            return;
        }
        long oldDays = ChronoUnit.DAYS.between(
            LocalDate.parse(oldDto.getStartDate()),
            LocalDate.parse(oldDto.getEndDate())
        ) + 1;

        
        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate   = LocalDate.parse(endDateStr);
            
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            req.setAttribute("errorMessage", ex.getMessage());
            req.setAttribute("dto", oldDto);
            req.getRequestDispatcher("/jsp/editPaidLeave.jsp")
               .forward(req, resp);
            return;
        }

       
        long newDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int remaining    = user.getRemainingDays();
        int available    = remaining + (int) oldDays;

        // 残日数チェック
        if (newDays > available) {
            req.setAttribute("errorMessage",
                "残日数が不足しています。（残 " 
              + available + "日、申請 " + newDays + "日）");
            req.setAttribute("dto", oldDto);
            req.getRequestDispatcher("/jsp/editPaidLeave.jsp")
               .forward(req, resp);
            return;
        }

        // DAO を使って更新
        paidLeaveDao.update(id,
                            user.getUsername(),
                            startDateStr,
                            endDateStr,
                            reason);

        // UserDAO で残日数を更新し、セッションにも反映
        int newRemaining = available - (int) newDays;
        userDao.updateRemainingDays(user.getUsername(), newRemaining);
        user.setRemainingDays(newRemaining);
        session.setAttribute("user", user);

        // 編集後は一覧へリダイレクト
        resp.sendRedirect(req.getContextPath() + "/paidleave/list");
    }
}
