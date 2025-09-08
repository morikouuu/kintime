package com.example.attendance.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.PaidLeaveDAO;
import com.example.attendance.dto.PaidLeaveDTO;
import com.example.attendance.dto.User;

@WebServlet("/paidleave/apply")
public class ApplyPaidLeaveServlet extends HttpServlet {

    private final PaidLeaveDAO dao = new PaidLeaveDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ── セッション・ログインチェック
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // ── 申請画面を表示
        req.getRequestDispatcher("/jsp/applyPaidLeave.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ── セッション・ログインチェック
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String username = user.getUsername(); // ← ここで文字列として取得
        String start    = req.getParameter("startDate");
        String end      = req.getParameter("endDate");
        String reason   = req.getParameter("reason");

        
        PaidLeaveDTO dto = new PaidLeaveDTO(username, start, end, reason, "PENDING");
        dao.insert(dto);

        
        req.setAttribute("message", "有給申請を登録しました。ID=" + dto.getId());
        req.getRequestDispatcher("/jsp/applyPaidLeaveResult.jsp")
           .forward(req, resp);
    }
}
