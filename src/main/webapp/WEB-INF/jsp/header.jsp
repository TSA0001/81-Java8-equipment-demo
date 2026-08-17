<%-- 共通ヘッダ: 各 JSP の <body> 直後に jsp:include で呼び出す --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="site-header">
    <span class="site-title">備品管理システム</span>
    <nav class="site-nav">
        <a href="<c:url value='/home'/>">トップ</a>
        <a href="<c:url value='/items'/>">備品一覧</a>
        <a href="<c:url value='/loans'/>">貸出履歴</a>
        <c:if test="${sessionScope.loginUser.role == 'ADMIN'}">
            <a href="<c:url value='/items/new'/>">備品登録</a>
            <a href="<c:url value='/loans'><c:param name='mode' value='active'/></c:url>">貸出中一覧</a>
        </c:if>
        <a href="<c:url value='/mypage/loans'/>">マイページ</a>
    </nav>
    <div class="site-user">
        <c:choose>
            <c:when test="${not empty sessionScope.loginUser}">
                <c:out value="${sessionScope.loginUser.userName}"/>
                <c:if test="${sessionScope.loginUser.role == 'ADMIN'}">（管理者）</c:if>
                <a href="<c:url value='/logout'/>">ログアウト</a>
            </c:when>
            <c:otherwise>
                <a href="<c:url value='/login'/>">ログイン</a>
            </c:otherwise>
        </c:choose>
    </div>
</header>
