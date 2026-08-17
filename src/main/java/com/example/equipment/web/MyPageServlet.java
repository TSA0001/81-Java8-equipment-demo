package com.example.equipment.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.equipment.model.UserAccount;
import com.example.equipment.service.LoanService;
import com.example.equipment.service.ServiceException;

/**
 * マイページ: ログインユーザー自身の貸出一覧・履歴を表示する。
 * GET /mypage/loans  → 自分の貸出中一覧
 * GET /mypage/history → 自分の全履歴
 */
@WebServlet(name = "MyPageServlet", urlPatterns = {"/mypage/loans", "/mypage/history"})
public class MyPageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final LoanService loanService = new LoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount loginUser = session == null ? null
                : (UserAccount) session.getAttribute(LoginServlet.SESSION_USER);
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        try {
            if ("/mypage/history".equals(path)) {
                request.setAttribute("loans", loanService.findByUserId(loginUser.getUserId()));
                request.setAttribute("title", "自分の貸出履歴");
                request.setAttribute("mode", "history");
            } else {
                // /mypage/loans — 自分の貸出中のみ（フィルタ）
                java.util.List<com.example.equipment.model.Loan> all =
                        loanService.findByUserId(loginUser.getUserId());
                java.util.List<com.example.equipment.model.Loan> active =
                        new java.util.ArrayList<com.example.equipment.model.Loan>();
                for (com.example.equipment.model.Loan loan : all) {
                    if (loan.getStatus() == com.example.equipment.model.LoanStatus.ACTIVE) {
                        active.add(loan);
                    }
                }
                request.setAttribute("loans", active);
                request.setAttribute("title", "自分の貸出中一覧");
                request.setAttribute("mode", "active");
            }
            request.getRequestDispatcher("/WEB-INF/jsp/myLoans.jsp").forward(request, response);
        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/myLoans.jsp").forward(request, response);
        }
    }
}
