<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ログイン — 備品管理システム</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<main class="container" style="max-width:400px">
    <h1>備品管理システム</h1>
    <h2>ログイン</h2>

    <c:if test="${not empty errorMessage}">
        <p class="error"><c:out value="${errorMessage}"/></p>
    </c:if>

    <form method="post" action="<c:url value='/login'/>" class="form">
        <label>
            <span>ログイン ID</span>
            <input type="text" name="loginId" value="<c:out value='${loginId}'/>"
                   maxlength="50" required autofocus autocomplete="username">
        </label>
        <label>
            <span>パスワード</span>
            <input type="password" name="password" maxlength="200"
                   required autocomplete="current-password">
        </label>
        <p class="actions">
            <button type="submit" class="button">ログイン</button>
        </p>
    </form>
</main>
<footer style="text-align:center;margin-top:2rem;font-size:0.8rem;color:#888;border-top:1px solid #e5e7eb;padding-top:0.5rem;">
    備品管理システム
</footer>
</body>
</html>
