package com.example.attendance.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.AttendanceDAO;
import com.example.attendance.dto.Attendance;
import com.example.attendance.dto.User;
@WebServlet("/attendance")
public class AttendanceServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);

        // セッション・ログインチェック
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        // 成功メッセージの表示処理
        String message = (String) session.getAttribute("successMessage");
        if (message != null) {
            req.setAttribute("successMessage", message);
            session.removeAttribute("successMessage");
        }

        if ("export_csv".equals(action) && "admin".equals(user.getRole())) {
            exportCsv(req, resp);
            return;
        }

        if ("filter".equals(action) && "admin".equals(user.getRole())) {
            handleAdminFilter(req, resp);
            return;
        }

        // 権限ごとの画面遷移
        if ("admin".equals(user.getRole())) {
            loadAdminDashboard(req, resp);
        } else {
            req.setAttribute("attendanceRecords", attendanceDAO.findByUserId(user.getUsername()));
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/employee_menu.jsp");
            rd.forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        switch (action) {
            case "check_in":
                attendanceDAO.checkIn(user.getUsername());
                session.setAttribute("successMessage", "出勤を記録しました。");
                break;

            case "check_out":
                attendanceDAO.checkOut(user.getUsername());
                session.setAttribute("successMessage", "退勤を記録しました。");
                break;

            case "add_manual":
                if ("admin".equals(user.getRole())) {
                    handleAddManual(req, session);
                }
                break;

            case "update_manual":
                if ("admin".equals(user.getRole())) {
                    handleUpdateManual(req, session);
                }
                break;

            case "delete_manual":
                if ("admin".equals(user.getRole())) {
                    handleDeleteManual(req, session);
                }
                break;
        }

        // リダイレクト先
        if ("admin".equals(user.getRole())) {
            resp.sendRedirect("attendance?action=filter"
                    + "&filterUserId=" + getOrEmpty(req.getParameter("filterUserId"))
                    + "&startDate=" + getOrEmpty(req.getParameter("startDate"))
                    + "&endDate=" + getOrEmpty(req.getParameter("endDate")));
        } else {
            resp.sendRedirect("attendance");
        }
    }

    /** 管理者用フィルター処理 */
    private void handleAdminFilter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filterUserId = req.getParameter("filterUserId");
        LocalDate startDate = parseLocalDate(req.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(req.getParameter("endDate"));

        List<Attendance> filteredRecords = attendanceDAO.findFilteredRecords(filterUserId, startDate, endDate);
        req.setAttribute("allAttendanceRecords", filteredRecords);

        Map<String, Long> totalHoursByUser = calculateTotalHours(filteredRecords);
        req.setAttribute("totalHoursByUser", totalHoursByUser);

        req.setAttribute("monthlyWorkingHours", attendanceDAO.getMonthlyWorkingHours(filterUserId));
        req.setAttribute("monthlyCheckInCounts", attendanceDAO.getMonthlyCheckInCounts(filterUserId));

        RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
        rd.forward(req, resp);
    }

    /** 管理者ダッシュボード初期表示 */
    private void loadAdminDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Attendance> allRecords = attendanceDAO.findAll();
        req.setAttribute("allAttendanceRecords", allRecords);

        Map<String, Long> totalHoursByUser = calculateTotalHours(allRecords);
        req.setAttribute("totalHoursByUser", totalHoursByUser);

        req.setAttribute("monthlyWorkingHours", attendanceDAO.getMonthlyWorkingHours(null));
        req.setAttribute("monthlyCheckInCounts", attendanceDAO.getMonthlyCheckInCounts(null));

        RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
        rd.forward(req, resp);
    }

    /** 勤怠時間合計の計算 */
    private Map<String, Long> calculateTotalHours(List<Attendance> records) {
        return records.stream().collect(Collectors.groupingBy(
                Attendance::getUserId,
                Collectors.summingLong(att -> {
                    if (att.getCheckInTime() != null && att.getCheckOutTime() != null) {
                        return java.time.temporal.ChronoUnit.HOURS.between(att.getCheckInTime(), att.getCheckOutTime());
                    }
                    return 0L;
                })
        ));
    }

    /** 手動追加 */
    private void handleAddManual(HttpServletRequest req, HttpSession session) {
        try {
            String userId = req.getParameter("userId");
            LocalDateTime checkIn = LocalDateTime.parse(req.getParameter("checkInTime"));
            LocalDateTime checkOut = parseLocalDateTime(req.getParameter("checkOutTime"));

            attendanceDAO.addManualAttendance(userId, checkIn, checkOut);
            session.setAttribute("successMessage", "勤怠記録を手動で追加しました。");
        } catch (DateTimeParseException e) {
            session.setAttribute("errorMessage", "日付/時刻の形式が不正です。");
        }
    }

    /** 手動更新 */
    private void handleUpdateManual(HttpServletRequest req, HttpSession session) {
        String userId = req.getParameter("userId");
        LocalDateTime oldCheckIn = LocalDateTime.parse(req.getParameter("oldCheckInTime"));
        LocalDateTime oldCheckOut = parseLocalDateTime(req.getParameter("oldCheckOutTime"));
        LocalDateTime newCheckIn = LocalDateTime.parse(req.getParameter("newCheckInTime"));
        LocalDateTime newCheckOut = parseLocalDateTime(req.getParameter("newCheckOutTime"));

        if (attendanceDAO.updateManualAttendance(userId, oldCheckIn, oldCheckOut, newCheckIn, newCheckOut)) {
            session.setAttribute("successMessage", "勤怠記録を手動で更新しました。");
        } else {
            session.setAttribute("errorMessage", "勤怠記録の更新に失敗しました。");
        }
    }

    /** 手動削除 */
    private void handleDeleteManual(HttpServletRequest req, HttpSession session) {
        String userId = req.getParameter("userId");
        LocalDateTime checkIn = LocalDateTime.parse(req.getParameter("checkInTime"));
        LocalDateTime checkOut = parseLocalDateTime(req.getParameter("checkOutTime"));

        if (attendanceDAO.deleteManualAttendance(userId, checkIn, checkOut)) {
            session.setAttribute("successMessage", "勤怠記録を削除しました。");
        } else {
            session.setAttribute("errorMessage", "勤怠記録の削除に失敗しました。");
        }
    }

    /** CSVエクスポート */
    private void exportCsv(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"attendance_records.csv\"");

        PrintWriter writer = resp.getWriter();
        writer.append("User ID,Check-in Time,Check-out Time\n");

        String filterUserId = req.getParameter("filterUserId");
        LocalDate startDate = parseLocalDate(req.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(req.getParameter("endDate"));

        List<Attendance> records = attendanceDAO.findFilteredRecords(filterUserId, startDate, endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Attendance record : records) {
            writer.append(String.format("%s,%s,%s\n",
                    record.getUserId(),
                    record.getCheckInTime() != null ? record.getCheckInTime().format(formatter) : "",
                    record.getCheckOutTime() != null ? record.getCheckOutTime().format(formatter) : ""));
        }
        writer.flush();
    }

    /** ユーティリティ: null安全な文字列取得 */
    private String getOrEmpty(String value) {
        return (value != null) ? value : "";
    }

    /** ユーティリティ: LocalDate パース */
    private LocalDate parseLocalDate(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** ユーティリティ: LocalDateTime パース */
    private LocalDateTime parseLocalDateTime(String value) {
        if (value == null || value.isEmpty()) return null;
        return LocalDateTime.parse(value);
    }
}
