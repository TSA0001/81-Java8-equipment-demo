<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>備品詳細</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main class="container">
    <h1>備品詳細</h1>
    <c:if test="${not empty flashMessage}"><p class="flash"><c:out value="${flashMessage}"/></p></c:if>
    <c:if test="${not empty errorMessage}"><p class="error"><c:out value="${errorMessage}"/></p></c:if>
    <c:if test="${not empty item}">
        <dl class="meta">
            <dt>管理番号</dt><dd><c:out value="${item.managementNo}"/></dd>
            <dt>備品名</dt><dd><c:out value="${item.itemName}"/></dd>
            <dt>カテゴリ</dt><dd><c:out value="${item.categoryName}"/></dd>
            <dt>購入日</dt><dd><c:out value="${item.purchaseDate}"/></dd>
            <dt>保管場所</dt><dd><c:out value="${item.storageLocation}"/></dd>
            <dt>状態</dt><dd><c:out value="${item.statusLabel}"/></dd>
            <dt>備考</dt><dd><c:out value="${item.note}"/></dd>
            <dt>バージョン</dt><dd><c:out value="${item.version}"/></dd>
        </dl>

        <c:if test="${not empty activeLoan}">
            <h2>現在の貸出</h2>
            <dl class="meta">
                <dt>利用者</dt><dd><c:out value="${activeLoan.userName}"/></dd>
                <dt>貸出日</dt><dd><c:out value="${activeLoan.loanDate}"/></dd>
                <dt>返却予定日</dt><dd><c:out value="${activeLoan.plannedReturnDate}"/><c:if test="${activeLoan.overdue}">（期限超過）</c:if></dd>
            </dl>
        </c:if>

        <p class="actions">
            <c:if test="${item.status == 'AVAILABLE'}">
                <a class="button" href="<c:url value='/loans/new'><c:param name='itemId' value='${item.itemId}'/></c:url>">貸出</a>
            </c:if>
            <c:if test="${item.status == 'LOANED'}">
                <a class="button" href="<c:url value='/loans/return'><c:param name='itemId' value='${item.itemId}'/></c:url>">返却</a>
            </c:if>
            <c:if test="${sessionScope.loginUser.role == 'ADMIN'}">
                <a class="button secondary" href="<c:url value='/items/edit'><c:param name='id' value='${item.itemId}'/></c:url>">編集</a>
                <c:if test="${item.status != 'LOANED'}">
                    <a class="button danger" href="<c:url value='/items/delete'><c:param name='id' value='${item.itemId}'/></c:url>">削除</a>
                </c:if>
            </c:if>
            <a href="<c:url value='/items'/>">一覧へ</a>
        </p>

        <h2>貸出履歴</h2>
        <table class="data-table">
            <thead>
            <tr>
                <th>利用者</th>
                <th>貸出日</th>
                <th>返却予定日</th>
                <th>返却日</th>
                <th>状態</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty loanHistory}">
                    <tr><td colspan="5">履歴はありません。</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="loan" items="${loanHistory}">
                        <tr class="${loan.overdue ? 'overdue' : ''}">
                            <td><c:out value="${loan.userName}"/></td>
                            <td><c:out value="${loan.loanDate}"/></td>
                            <td><c:out value="${loan.plannedReturnDate}"/></td>
                            <td><c:out value="${loan.actualReturnDate}"/></td>
                            <td><c:out value="${loan.statusLabel}"/><c:if test="${loan.overdue}">（期限超過）</c:if></td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </c:if>
</main>
</body>
</html>
