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

/**
 * 未ログインユーザーを /login へリダイレクトするフィルタ。
 * /login, /logout, /health, /css/* は認証不要とする。
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (isPublicPath(req)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute(LoginServlet.SESSION_USER) != null;
        if (!loggedIn) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op（Java 6 以降: interface メソッドへの @Override が使用可能）
    }

    /**
     * 認証不要なパスかどうか判定する。
     */
    private boolean isPublicPath(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path == null) {
            return false;
        }
        return "/login".equals(path)
                || "/logout".equals(path)
                || "/health".equals(path)
                || path.startsWith("/css/");
    }
}
