<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>備品返却</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main class="container">
    <h1>備品返却</h1>
    <c:if test="${not empty errorMessage}"><p class="error"><c:out value="${errorMessage}"/></p></c:if>
    <c:if test="${not empty errorMessages}">
        <ul class="error-list"><c:forEach var="msg" items="${errorMessages}"><li><c:out value="${msg}"/></li></c:forEach></ul>
    </c:if>

    <c:if test="${not empty item and not empty loan}">
        <dl class="meta">
            <dt>管理番号</dt><dd><c:out value="${item.managementNo}"/></dd>
            <dt>備品名</dt><dd><c:out value="${item.itemName}"/></dd>
            <dt>利用者</dt><dd><c:out value="${loan.userName}"/></dd>
            <dt>貸出日</dt><dd><c:out value="${loan.loanDate}"/></dd>
            <dt>返却予定日</dt><dd><c:out value="${loan.plannedReturnDate}"/></dd>
        </dl>

        <form method="post" action="<c:url value='/loans/return'/>" class="form">
            <input type="hidden" name="loanId" value="<c:out value='${form.loanId}'/>">
            <input type="hidden" name="itemId" value="<c:out value='${form.itemId}'/>">
            <input type="hidden" name="loanVersion" value="<c:out value='${form.loanVersion}'/>">
            <input type="hidden" name="itemVersion" value="<c:out value='${form.itemVersion}'/>">
            <label>返却日
                <input type="date" name="actualReturnDate" value="<c:out value='${form.actualReturnDate}'/>" required>
            </label>
            <label>返却後の備品状態
                <select name="returnStatus" required>
                    <option value="AVAILABLE" <c:if test="${form.returnStatus == 'AVAILABLE'}">selected</c:if>>利用可能</option>
                    <option value="REPAIRING" <c:if test="${form.returnStatus == 'REPAIRING'}">selected</c:if>>修理中</option>
                </select>
            </label>
            <label>備考
                <textarea name="returnNote" rows="3" maxlength="1000"><c:out value="${form.returnNote}"/></textarea>
            </label>
            <p class="actions">
                <button type="submit" class="button">返却する</button>
                <a href="<c:url value='/items/detail'><c:param name='id' value='${item.itemId}'/></c:url>">キャンセル</a>
            </p>
        </form>
    </c:if>
</main>
</body>
</html>
