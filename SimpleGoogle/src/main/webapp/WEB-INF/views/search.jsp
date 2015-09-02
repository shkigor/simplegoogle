<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.sometld.com/tlds" prefix="f" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Результаты поиска</title>

    <style>
		a.title { 	color:blue;
					text-decoration:none;
					padding-left: 25px;
					font-size: 120%;
				}
				
		a.title:visited {
    				color: #5A717B; /* Цвет посещенных ссылок */
   				}
   						
		a.title:hover {
    				text-decoration:underline;
   				}
				
		a.uri   { 	color:green;
					text-decoration:none;
					padding-left: 25px;
					font-size: 90%;
				}
		.content {
					padding-left: 25px;
					padding-bottom: 25px;
					font-size: 85%;
					width: 50%;
				 }
    </style>

<script type="text/javascript"
	src="<c:url value="/resources/js/jquery-1.11.3.js" />">
</script>

</head>
<body>
    <h2>Результаты поиска:</h2> 
    
        <form:form method="GET" commandName="searchTextConditions" action="">
        <form:input type="hidden" path="start" id="start"/>
        <table>
            <tr>
                <td><label for="text">Поиск: </label> </td>
                <td><form:input path="q" id="text"/></td>
				<td><input type="submit" value="Найти"/></td>
            </tr>
            <tr>
                <td><label for="perPage">Кол-во результатов на странице: </label> </td>
                <td><form:input path="resultPerPage" id="perPage" size="2" type="number" min="1" max="30" step="1"/></td>
                <td><form:errors path="resultPerPage" cssClass="error"> Ошибка: введите число от 1 до 30 </form:errors></td>
            </tr>
            <tr>
                <td>Найдено: </td>
                <td>${searchTextConditions.numTotalHits}</td>
            </tr>
        </table>
        
                 Сортировка результатов:       
    <form:radiobutton path="sortResult" onclick="javascript: submit()" value="SORT_RELEVANT"/>Релевантности
    <form:radiobutton path="sortResult" onclick="javascript: submit()" value="SORT_TITLE_ASC"/>Названию (возр.)
    <form:radiobutton path="sortResult" onclick="javascript: submit()" value="SORT_TITLE_DESC"/>Названию (убыв.)
        
        <br/>
        <c:choose>
            <c:when test="${searchTextConditions.start < searchTextConditions.resultPerPage}">
                <input type='submit' name='action' value='&lt;&lt;' disabled="disabled" />
            </c:when>
            <c:otherwise>
                <input type='submit' name='action' value='&lt;&lt;'/>
            </c:otherwise>
        </c:choose>
        
        Страница ${searchTextConditions.currentPage} из ${searchTextConditions.allPage}
        
	    <c:choose>
	        <c:when test="${(searchTextConditions.start + searchTextConditions.resultPerPage) >= searchTextConditions.numTotalHits}">
	            <input type='submit' name='action' value='&gt;&gt;' disabled="disabled" />
	        </c:when>
	        <c:otherwise>
	            <input type='submit' name='action' value='&gt;&gt;' />
	        </c:otherwise>
	    </c:choose>
        
        

        
        
    </form:form>
    <br/>
    
        <c:forEach items="${searchListResult}" var="result">
        <c:set var="string1" value="${result.content}"/>
		<c:set var="string2" value="${fn:substring(string1, 0, 200)}" />
            <%--  
            ${result.score}<br/>
            <a href="<c:url value="${result.uri}" />">${result.title}</a><br/>
            <a href="<c:url value="${result.uri}" />">${result.uri}</a><br/>
            ${result.content}...<br/>
             --%>
             
           <a href="<c:url value="${result.uri}"/>" class="title">${f:replaceAll(result.title, "\\[solo.ck\\](.*?)\\[/solo.ck\\]", "<b style=\'background-color:yellow;color:black;\'>$1</b>")}</a><br/>
           <a href="<c:url value="${result.uri}" />" class="uri">${result.uri}</a><br/>  
           <div class="content">${f:replaceAll(result.content, "\\[solo.ck\\](.*?)\\[/solo.ck\\]", "<b style=\'background-color:yellow;\'>$1</b>")}...</div>
            
        </c:forEach>
    
</body>
</html>