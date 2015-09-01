<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Index URL</title>
<style>
    .error {
        color: #ff0000;
    }
</style>
</head>
<body>

    <h2>Введите url WEB страницы для индекса</h2>
    <form:form method="POST" commandName="siteToIndex" action="">
        <table>
            <tr>
                <td><label for="name">Сайт: </label> </td>
                <td><form:input path="siteUrl" id="name"/></td>
                <td><form:errors path="siteUrl" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label for="depth">Глубина индексирования: </label> </td>
                <td><form:input path="depth" id="depth" type="number" min="0" max="6" step="1"/></td>
                <td><form:errors path="depth" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label for="crawlOtherSite">Захватывать ссылки с других доменов: </label> </td>
                <td><form:checkbox path="crawlOtherSite" id="crawlOtherSite"/></td>
                <td><form:errors path="crawlOtherSite" cssClass="error"/></td>
            </tr>
      
            <tr>
                <td colspan="2"  align="right">
                            <input type="submit" value="Index"/>
                </td>
            </tr>
        </table>
    </form:form>
    <br/>
    <p>Всего захвачено линков: ${siteToIndex.linksCrawled}</p>
    <p>Всего добавлено линков в индекс: ${siteToIndex.linksIndexed}</p>
    <br/>
    <a href="<c:url value='/' />">Вернуться к поиску</a>

</body>
</html>