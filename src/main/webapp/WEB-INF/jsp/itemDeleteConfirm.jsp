<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>備品削除確認</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<main class="container">
    <h1>備品削除確認</h1>
    <c:if test="${not empty errorMessage}">
        <p class="error"><c:out value="${errorMessage}"/></p>
    </c:if>
    <c:if test="${not empty item}">
        <p>次の備品を論理削除します。よろしいですか？</p>
        <dl class="meta">
            <dt>管理番号</dt><dd><c:out value="${item.managementNo}"/></dd>
            <dt>備品名</dt><dd><c:out value="${item.itemName}"/></dd>
            <dt>状態</dt><dd><c:out value="${item.statusLabel}"/></dd>
        </dl>
        <form method="post" action="<c:url value='/items/delete'/>" class="actions">
            <input type="hidden" name="itemId" value="<c:out value='${item.itemId}'/>">
            <input type="hidden" name="version" value="<c:out value='${item.version}'/>">
            <button type="submit" class="button danger">削除する</button>
            <a href="<c:url value='/items/detail'><c:param name='id' value='${item.itemId}'/></c:url>">キャンセル</a>
        </form>
    </c:if>
</main>
</body>
</html>
