package com.example.equipment.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import com.example.equipment.model.UserAccount;

/**
 * AdminFilter のユニットテスト。
 * 一般利用者（USER ロール）が管理者専用パスにアクセスした場合に
 * 403 Forbidden が返ることを検証する。
 */
public class AdminFilterTest {

    private AdminFilter filter;

    @Before
    public void setUp() throws Exception {
        filter = new AdminFilter();
        filter.init(mock(FilterConfig.class));
    }

    @Test
    public void adminUserPassesThrough() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        UserAccount admin = new UserAccount();
        admin.setRole("ADMIN");

        HttpSession session = mock(HttpSession.class);
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute(LoginServlet.SESSION_USER)).thenReturn(admin);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    @Test
    public void regularUserReceives403() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        UserAccount user = new UserAccount();
        user.setRole("USER");

        HttpSession session = mock(HttpSession.class);
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute(LoginServlet.SESSION_USER)).thenReturn(user);

        filter.doFilter(req, res, chain);

        verify(res).sendError(HttpServletResponse.SC_FORBIDDEN, "管理者権限が必要です。");
    }

    @Test
    public void noSessionReceives403() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getSession(false)).thenReturn(null);

        filter.doFilter(req, res, chain);

        verify(res).sendError(HttpServletResponse.SC_FORBIDDEN, "管理者権限が必要です。");
    }
}
