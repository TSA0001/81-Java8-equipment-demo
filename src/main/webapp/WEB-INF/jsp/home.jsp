<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>備品管理システム</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main class="container">
    <h1>備品管理システム</h1>
    <dl class="meta">
        <dt>Java バージョン</dt>
        <dd><c:out value="${javaVersion}"/></dd>
        <dt>Java ベンダー</dt>
        <dd><c:out value="${javaVendor}"/></dd>
    </dl>
    <p>
        <a class="button" href="<c:url value='/items'/>">備品一覧へ</a>
        <a class="button secondary" href="<c:url value='/loans'/>">貸出履歴</a>
        <a class="button secondary" href="<c:url value='/mypage/loans'/>">マイページ</a>
    </p>
</main>
</body>
</html>
