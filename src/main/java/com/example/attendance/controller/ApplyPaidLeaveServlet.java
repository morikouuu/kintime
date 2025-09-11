package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
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


@WebServlet("/paidleave/apply")
public class ApplyPaidLeaveServlet extends HttpServlet {

    private final PaidLeaveDAO paidLeaveDao = new PaidLeaveDAO();
    private final UserDAO      userDao      = new UserDAO();  

    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
    		throws ServletException, IOException { 
    	HttpSession session = req.getSession(false); 
    	if (session == null || session.getAttribute("user") == null
    			) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; } 
    	req.getRequestDispatcher("/jsp/applyPaidLeave.jsp") .forward(req, resp); }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ── セッション・ログインチェック
        HttpSession session = req.getSession(false);
        User currentUser = (session != null)
            ? (User) session.getAttribute("user")
            : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        
        LocalDate start = LocalDate.parse(req.getParameter("startDate"));
        LocalDate end   = LocalDate.parse(req.getParameter("endDate"));
        int daysToRequest = (int) ChronoUnit.DAYS.between(start, end) + 1;

        
        if (currentUser.getRemainingDays() < daysToRequest) {
            req.setAttribute("errorMessage",
                "残り有給日数が不足しています。（残 " 
                + currentUser.getRemainingDays() + "日、申請 " 
                + daysToRequest + "日）");
            // 入力値を戻す
            req.setAttribute("startDate", req.getParameter("startDate"));
            req.setAttribute("endDate",   req.getParameter("endDate"));
            req.setAttribute("reason",    req.getParameter("reason"));
            req.getRequestDispatcher("/jsp/applyPaidLeave.jsp")
               .forward(req, resp);
            return;
        }

       
        String username = currentUser.getUsername();
        String reason   = req.getParameter("reason");
        PaidLeaveDTO dto = new PaidLeaveDTO(username,
                                            req.getParameter("startDate"),
                                            req.getParameter("endDate"),
                                            reason,
                                            "PENDING");
        paidLeaveDao.insert(dto);

        
        int newRemain = currentUser.getRemainingDays() - daysToRequest;
        userDao.updateRemainingDays(username, newRemain);
        currentUser.setRemainingDays(newRemain);
        session.setAttribute("user", currentUser);

        
        req.setAttribute("message", "有給申請を登録しました。ID=" + dto.getId());
        req.getRequestDispatcher("/jsp/applyPaidLeaveResult.jsp")
           .forward(req, resp);
    }
}
