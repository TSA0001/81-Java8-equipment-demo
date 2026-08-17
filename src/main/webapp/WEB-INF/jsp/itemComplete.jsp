<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>備品登録完了</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<main class="container">
    <h1>備品登録完了</h1>
    <p class="flash">備品を登録しました。</p>
    <dl class="meta">
        <dt>管理番号</dt><dd><c:out value="${item.managementNo}"/></dd>
        <dt>備品名</dt><dd><c:out value="${item.itemName}"/></dd>
    </dl>
    <p class="actions">
        <a class="button" href="<c:url value='/items'/>">一覧へ</a>
        <a href="<c:url value='/items/detail'><c:param name='id' value='${item.itemId}'/></c:url>">詳細へ</a>
    </p>
</main>
</body>
</html>
