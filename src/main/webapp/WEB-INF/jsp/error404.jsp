<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>404 Not Found</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/app.css">
</head>
<body>
<main class="container">
    <h1>ページが見つかりません</h1>
    <p><a href="<%= request.getContextPath() %>/home">トップへ戻る</a></p>
</main>
</body>
</html>
