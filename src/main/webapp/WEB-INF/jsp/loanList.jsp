<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${empty title ? '貸出履歴' : title}"/></title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main class="container">
    <h1><c:out value="${empty title ? '貸出履歴' : title}"/></h1>
    <c:if test="${not empty errorMessage}"><p class="error"><c:out value="${errorMessage}"/></p></c:if>

    <c:if test="${sessionScope.loginUser.role == 'ADMIN'}">
        <p class="actions">
            <a class="button" href="<c:url value='/loans'/>">全履歴</a>
            <a class="button secondary" href="<c:url value='/loans'><c:param name='mode' value='active'/></c:url>">貸出中のみ</a>
        </p>
    </c:if>

    <table class="data-table">
        <thead>
        <tr>
            <th>利用者</th>
            <th>管理番号</th>
            <th>備品名</th>
            <th>貸出日</th>
            <th>返却予定日</th>
            <th>返却日</th>
            <th>状態</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty loans}">
                <tr><td colspan="7">表示する貸出情報がありません。</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="loan" items="${loans}">
                    <tr class="${loan.overdue ? 'overdue' : ''}">
                        <td><c:out value="${loan.userName}"/></td>
                        <td><c:out value="${loan.managementNo}"/></td>
                        <td><c:out value="${loan.itemName}"/></td>
                        <td><c:out value="${loan.loanDate}"/></td>
                        <td><c:out value="${loan.plannedReturnDate}"/></td>
                        <td><c:out value="${loan.actualReturnDate}"/></td>
                        <td>
                            <c:out value="${loan.statusLabel}"/>
                            <c:if test="${loan.overdue}">（期限超過）</c:if>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</main>
</body>
</html>
