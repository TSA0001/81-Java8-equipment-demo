<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>備品一覧・検索</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<main class="container">
    <h1>備品一覧・検索</h1>

    <c:if test="${not empty flashMessage}">
        <p class="flash"><c:out value="${flashMessage}"/></p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p class="error"><c:out value="${errorMessage}"/></p>
    </c:if>

    <form method="get" action="<c:url value='/items'/>" class="form search-form">
        <div class="search-grid">
            <label>管理番号
                <input type="text" name="managementNo" value="<c:out value='${criteria.managementNo}'/>" maxlength="20">
            </label>
            <label>備品名
                <input type="text" name="itemName" value="<c:out value='${criteria.itemName}'/>" maxlength="100">
            </label>
            <label>カテゴリ
                <select name="categoryId">
                    <option value="">すべて</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.categoryId}"
                            <c:if test="${criteria.categoryId == category.categoryId}">selected</c:if>>
                            <c:out value="${category.categoryName}"/>
                        </option>
                    </c:forEach>
                </select>
            </label>
            <label>保管場所
                <input type="text" name="storageLocation" value="<c:out value='${criteria.storageLocation}'/>" maxlength="100">
            </label>
            <label>状態
                <select name="status">
                    <option value="">すべて</option>
                    <c:forEach var="st" items="${statuses}">
                        <option value="<c:out value='${st}'/>"
                            <c:if test="${criteria.status == st.name()}">selected</c:if>>
                            <c:out value="${st.label}"/>
                        </option>
                    </c:forEach>
                </select>
            </label>
        </div>
        <p class="actions">
            <button type="submit" class="button">検索</button>
            <a href="<c:url value='/items'/>">条件クリア</a>
            <c:if test="${sessionScope.loginUser.role == 'ADMIN'}">
                <a class="button secondary" href="<c:url value='/items/new'/>">備品登録</a>
            </c:if>
        </p>
    </form>

    <c:if test="${searched}">
        <p class="notice">検索結果: <c:out value="${items.size()}"/> 件</p>
    </c:if>

    <table class="data-table">
        <thead>
        <tr>
            <th>管理番号</th>
            <th>備品名</th>
            <th>カテゴリ</th>
            <th>保管場所</th>
            <th>状態</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty items}">
                <tr>
                    <td colspan="6">表示する備品はありません。</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="item" items="${items}">
                    <tr>
                        <td><c:out value="${item.managementNo}"/></td>
                        <td><c:out value="${item.itemName}"/></td>
                        <td><c:out value="${item.categoryName}"/></td>
                        <td><c:out value="${item.storageLocation}"/></td>
                        <td><c:out value="${item.statusLabel}"/></td>
                        <td class="ops">
                            <a href="<c:url value='/items/detail'><c:param name='id' value='${item.itemId}'/></c:url>">詳細</a>
                            <c:if test="${sessionScope.loginUser.role == 'ADMIN'}">
                                <a href="<c:url value='/items/edit'><c:param name='id' value='${item.itemId}'/></c:url>">編集</a>
                            </c:if>
                            <c:if test="${item.status == 'AVAILABLE'}">
                                <a href="<c:url value='/loans/new'><c:param name='itemId' value='${item.itemId}'/></c:url>">貸出</a>
                            </c:if>
                            <c:if test="${item.status == 'LOANED'}">
                                <a href="<c:url value='/loans/return'><c:param name='itemId' value='${item.itemId}'/></c:url>">返却</a>
                            </c:if>
                            <c:if test="${sessionScope.loginUser.role == 'ADMIN' and item.status != 'LOANED'}">
                                <a href="<c:url value='/items/delete'><c:param name='id' value='${item.itemId}'/></c:url>">削除</a>
                            </c:if>
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
