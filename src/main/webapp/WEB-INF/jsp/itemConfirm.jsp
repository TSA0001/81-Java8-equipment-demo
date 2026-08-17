<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>備品登録確認</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<main class="container">
    <h1>備品登録確認</h1>
    <p>内容を確認し、問題なければ登録してください。</p>

    <dl class="meta">
        <dt>管理番号</dt><dd><c:out value="${form.managementNo}"/></dd>
        <dt>備品名</dt><dd><c:out value="${form.itemName}"/></dd>
        <dt>カテゴリ ID</dt><dd><c:out value="${form.categoryId}"/></dd>
        <dt>購入日</dt><dd><c:out value="${form.purchaseDate}"/></dd>
        <dt>保管場所</dt><dd><c:out value="${form.storageLocation}"/></dd>
        <dt>状態</dt><dd><c:out value="${form.status}"/></dd>
        <dt>備考</dt><dd><c:out value="${form.note}"/></dd>
    </dl>

    <c:forEach var="category" items="${categories}">
        <c:if test="${category.categoryId == form.categoryId}">
            <p>カテゴリ名: <c:out value="${category.categoryName}"/></p>
        </c:if>
    </c:forEach>

    <form method="post" action="<c:url value='/items/create'/>" class="actions">
        <button type="submit" class="button">登録する</button>
        <a class="button secondary" href="<c:url value='/items/new'/>">戻る</a>
        <a href="<c:url value='/items/cancel'/>">キャンセル</a>
    </form>
</main>
</body>
</html>
