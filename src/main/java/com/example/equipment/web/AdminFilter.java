package com.example.equipment.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.equipment.model.UserAccount;

/**
 * 管理者専用 URL を一般利用者から保護するフィルタ。
 * 管理者専用パス: /items/new, /items/confirm, /items/create, /items/cancel,
 *                /items/edit, /items/delete
 */
@WebFilter(filterName = "AdminFilter", urlPatterns = {
        "/items/new", "/items/confirm", "/items/create", "/items/cancel",
        "/items/edit", "/items/delete"
})
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        UserAccount user = session == null ? null : (UserAccount) session.getAttribute(LoginServlet.SESSION_USER);

        if (user == null || !"ADMIN".equals(user.getRole())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "管理者権限が必要です。");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op（Java 6 以降: interface メソッドへの @Override が使用可能）
    }
}
