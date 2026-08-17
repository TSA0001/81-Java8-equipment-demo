<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>
        <c:choose>
            <c:when test="${mode == 'edit'}">備品編集</c:when>
            <c:otherwise>備品登録</c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<main class="container">
    <h1>
        <c:choose>
            <c:when test="${mode == 'edit'}">備品編集</c:when>
            <c:otherwise>備品登録</c:otherwise>
        </c:choose>
    </h1>

    <c:if test="${not empty errorMessage}">
        <p class="error"><c:out value="${errorMessage}"/></p>
    </c:if>
    <c:if test="${not empty errorMessages}">
        <ul class="error-list">
            <c:forEach var="msg" items="${errorMessages}">
                <li><c:out value="${msg}"/></li>
            </c:forEach>
        </ul>
    </c:if>

    <c:choose>
        <c:when test="${mode == 'edit'}">
            <c:set var="formAction" value="/items/edit"/>
        </c:when>
        <c:otherwise>
            <c:set var="formAction" value="/items/confirm"/>
        </c:otherwise>
    </c:choose>

    <form method="post" action="<c:url value='${formAction}'/>" class="form">
        <c:if test="${mode == 'edit'}">
            <input type="hidden" name="itemId" value="<c:out value='${form.itemId}'/>">
            <input type="hidden" name="version" value="<c:out value='${form.version}'/>">
        </c:if>

        <label>管理番号
            <input type="text" name="managementNo" value="<c:out value='${form.managementNo}'/>" maxlength="20" required>
            <span class="hint">例: EQ-000010</span>
        </label>
        <c:if test="${not empty errors.managementNo}">
            <p class="field-error"><c:out value="${errors.managementNo}"/></p>
        </c:if>

        <label>備品名
            <input type="text" name="itemName" value="<c:out value='${form.itemName}'/>" maxlength="100" required>
        </label>
        <c:if test="${not empty errors.itemName}">
            <p class="field-error"><c:out value="${errors.itemName}"/></p>
        </c:if>

        <label>カテゴリ
            <select name="categoryId" required>
                <option value="">選択してください</option>
                <c:forEach var="category" items="${categories}">
                    <option value="${category.categoryId}"
                        <c:if test="${form.categoryId == category.categoryId}">selected</c:if>>
                        <c:out value="${category.categoryName}"/>
                    </option>
                </c:forEach>
            </select>
        </label>
        <c:if test="${not empty errors.categoryId}">
            <p class="field-error"><c:out value="${errors.categoryId}"/></p>
        </c:if>

        <label>購入日
            <input type="date" name="purchaseDate" value="<c:out value='${form.purchaseDate}'/>">
        </label>
        <c:if test="${not empty errors.purchaseDate}">
            <p class="field-error"><c:out value="${errors.purchaseDate}"/></p>
        </c:if>

        <label>保管場所
            <input type="text" name="storageLocation" value="<c:out value='${form.storageLocation}'/>" maxlength="100" required>
        </label>
        <c:if test="${not empty errors.storageLocation}">
            <p class="field-error"><c:out value="${errors.storageLocation}"/></p>
        </c:if>

        <label>状態
            <select name="status" required>
                <c:forEach var="st" items="${statuses}">
                    <option value="<c:out value='${st}'/>"
                        <c:if test="${form.status == st.name()}">selected</c:if>>
                        <c:out value="${st.label}"/>
                    </option>
                </c:forEach>
            </select>
        </label>
        <c:if test="${not empty errors.status}">
            <p class="field-error"><c:out value="${errors.status}"/></p>
        </c:if>

        <label>備考
            <textarea name="note" rows="4" maxlength="1000"><c:out value="${form.note}"/></textarea>
        </label>
        <c:if test="${not empty errors.note}">
            <p class="field-error"><c:out value="${errors.note}"/></p>
        </c:if>

        <p class="actions">
            <button type="submit" class="button">
                <c:choose>
                    <c:when test="${mode == 'edit'}">更新する</c:when>
                    <c:otherwise>確認画面へ</c:otherwise>
                </c:choose>
            </button>
            <a href="<c:url value='/items'/>">キャンセル</a>
        </p>
    </form>
</main>
</body>
</html>
