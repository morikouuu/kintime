package com.example.attendance.controller;

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.User;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action  = req.getParameter("action");
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            resp.sendRedirect("login.jsp");
            return;
        }

        // メッセージ取得＆クリア
        String success = (String) session.getAttribute("successMessage");
        if (success != null) {
            req.setAttribute("successMessage", success);
            session.removeAttribute("successMessage");
        }
        String error = (String) session.getAttribute("errorMessage");
        if (error != null) {
            req.setAttribute("errorMessage", error);
            session.removeAttribute("errorMessage");
        }

        // 編集モードなら対象ユーザーをセット
        if ("edit".equals(action)) {
            String username = req.getParameter("username");
            User userToEdit = userDAO.findByUsername(username);
            req.setAttribute("userToEdit", userToEdit);
        }

        // 一覧用リストを常にセット
        Collection<User> users = userDAO.getAllUsers();
        req.setAttribute("users", users);

        RequestDispatcher rd = req.getRequestDispatcher("/jsp/user_management.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action  = req.getParameter("action");
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            resp.sendRedirect("login.jsp");
            return;
        }

        if ("add".equals(action)) {
            String username   = req.getParameter("username");
            String password   = req.getParameter("password");
            String role       = req.getParameter("role");
            int    remainingDays = parseInt(req.getParameter("remainingDays"), 0);

            if (userDAO.findByUsername(username) == null) {
                userDAO.addUser(new User(
                    username,
                    UserDAO.hashPassword(password),
                    role,
                    true,
                    remainingDays
                ));
                session.setAttribute("successMessage", "ユーザーを追加しました");
            } else {
                session.setAttribute("errorMessage", "ユーザーIDは既に存在します。");
            }

        } else if ("update".equals(action)) {
            String  username      = req.getParameter("username");
            String  role          = req.getParameter("role");
            boolean enabled       = req.getParameter("enabled") != null;
            String  rdParam       = req.getParameter("remainingDays");

            User existingUser = userDAO.findByUsername(username);
            if (existingUser != null) {
                // ← ここで remainingDays を取得＆既存値フォールバック
                int remainingDays = parseInt(rdParam, existingUser.getRemainingDays());

                // ← ５引数版コンストラクタを呼ぶ
                userDAO.updateUser(new User(
                    username,
                    existingUser.getPassword(),
                    role,
                    enabled,
                    remainingDays
                ));
                session.setAttribute("successMessage", "ユーザー情報を更新しました。");
            }

        } else if ("delete".equals(action)) {
            String username = req.getParameter("username");
            userDAO.deleteUser(username);
            session.setAttribute("successMessage", "ユーザーを削除しました。");

        } else if ("reset_password".equals(action)) {
            String username    = req.getParameter("username");
            String newPassword = req.getParameter("newPassword");
            userDAO.resetPassword(username, newPassword);
            session.setAttribute("successMessage",
                username + " のパスワードをリセットしました。(新パスワード: " + newPassword + ")");

        } else if ("toggle_enabled".equals(action)) {
            String  username = req.getParameter("username");
            boolean enabled  = Boolean.parseBoolean(req.getParameter("enabled"));
            userDAO.toggleUserEnabled(username, enabled);
            session.setAttribute("successMessage",
                username + " のアカウントを"
                + (enabled ? "有効化" : "無効化") + "しました。");
        }

        resp.sendRedirect("users?action=list");
    }

    /**
     * 文字列を int にパースし、空文字や例外時は defaultValue を返す
     */
    private int parseInt(String str, int defaultValue) {
        try {
            return (str != null && !str.isEmpty())
                ? Integer.parseInt(str)
                : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
