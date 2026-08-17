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

/**
 * AuthFilter のユニットテスト。
 * 未ログイン時は /login へリダイレクトされ、
 * ログイン済みはフィルタチェーンが通過することを検証する。
 */
public class AuthFilterTest {

    private AuthFilter filter;

    @Before
    public void setUp() throws Exception {
        filter = new AuthFilter();
        filter.init(mock(FilterConfig.class));
    }

    @Test
    public void unauthenticatedRequestIsRedirectedToLogin() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getServletPath()).thenReturn("/items");
        when(req.getSession(false)).thenReturn(null);
        when(req.getContextPath()).thenReturn("/equipment-management");

        filter.doFilter(req, res, chain);

        verify(res).sendRedirect("/equipment-management/login");
    }

    @Test
    public void authenticatedRequestPassesThrough() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        HttpSession session = mock(HttpSession.class);
        when(req.getServletPath()).thenReturn("/items");
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute(LoginServlet.SESSION_USER))
                .thenReturn(new com.example.equipment.model.UserAccount());

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    @Test
    public void loginPathIsPublic() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getServletPath()).thenReturn("/login");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    @Test
    public void healthPathIsPublic() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getServletPath()).thenReturn("/health");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    @Test
    public void cssPathIsPublic() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getServletPath()).thenReturn("/css/app.css");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }
}
