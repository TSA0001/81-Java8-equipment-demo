package com.example.equipment.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.equipment.dao.UserDao;
import com.example.equipment.model.UserAccount;

/**
 * ログイン・ログアウト Servlet。
 * GET /login  → ログイン画面表示
 * POST /login → 認証処理
 * GET /logout → セッション破棄してログイン画面へ
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    static final String SESSION_USER = "loginUser";

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
        case "/logout":
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        default:
            // ログイン済みならトップへ
            if (isLoggedIn(request)) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String loginId = trimOrNull(request.getParameter("loginId"));
        String password = request.getParameter("password");

        if (loginId == null || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "ログイン ID とパスワードを入力してください。");
            request.setAttribute("loginId", loginId);
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        try {
            UserAccount user = userDao.authenticate(loginId, password);
            if (user == null) {
                request.setAttribute("errorMessage", "ログイン ID またはパスワードが正しくありません。");
                request.setAttribute("loginId", loginId);
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }
            // セッション固定化攻撃対策: 認証後に新しいセッションを発行
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_USER, user);
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "認証処理に失敗しました。しばらく経ってから再試行してください。");
            request.setAttribute("loginId", loginId);
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_USER) != null;
    }

    private String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
