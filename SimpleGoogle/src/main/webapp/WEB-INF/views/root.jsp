<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Home</title>
</head>
<body>
    <h2>Введите текст для поиска</h2>

<form method="GET" action="search">
<input type="hidden" name="resultPerPage" value="10" />
<table>
	<tr>
		<td><input type="text" name="q" /></td>
	</tr>

	<tr>
		<td colspan="1" align="right"><input type="submit" value="Search" />
		<input type="reset" value="Reset" /></td>
	</tr>
</table>
</form>
</body>
</html>