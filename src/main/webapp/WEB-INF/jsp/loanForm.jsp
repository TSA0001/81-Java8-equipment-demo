<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>備品貸出</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main class="container">
    <h1>備品貸出</h1>
    <c:if test="${not empty errorMessage}"><p class="error"><c:out value="${errorMessage}"/></p></c:if>
    <c:if test="${not empty errorMessages}">
        <ul class="error-list"><c:forEach var="msg" items="${errorMessages}"><li><c:out value="${msg}"/></li></c:forEach></ul>
    </c:if>

    <c:if test="${not empty item}">
        <dl class="meta">
            <dt>管理番号</dt><dd><c:out value="${item.managementNo}"/></dd>
            <dt>備品名</dt><dd><c:out value="${item.itemName}"/></dd>
            <dt>状態</dt><dd><c:out value="${item.statusLabel}"/></dd>
        </dl>

        <form method="post" action="<c:url value='/loans/new'/>" class="form">
            <input type="hidden" name="itemId" value="<c:out value='${form.itemId}'/>">
            <label>利用者
                <select name="userId" required>
                    <option value="">選択してください</option>
                    <c:forEach var="user" items="${users}">
                        <option value="${user.userId}" <c:if test="${form.userId == user.userId}">selected</c:if>>
                            <c:out value="${user.userName}"/>（<c:out value="${user.loginId}"/>）
                        </option>
                    </c:forEach>
                </select>
            </label>
            <label>貸出日
                <input type="date" name="loanDate" value="<c:out value='${form.loanDate}'/>" required>
            </label>
            <label>返却予定日
                <input type="date" name="plannedReturnDate" value="<c:out value='${form.plannedReturnDate}'/>" required>
            </label>
            <label>備考
                <textarea name="loanNote" rows="3" maxlength="1000"><c:out value="${form.loanNote}"/></textarea>
            </label>
            <p class="actions">
                <button type="submit" class="button">貸し出す</button>
                <a href="<c:url value='/items/detail'><c:param name='id' value='${item.itemId}'/></c:url>">キャンセル</a>
            </p>
        </form>
    </c:if>
</main>
</body>
</html>
