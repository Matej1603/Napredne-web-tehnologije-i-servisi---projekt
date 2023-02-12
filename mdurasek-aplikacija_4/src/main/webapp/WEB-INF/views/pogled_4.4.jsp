<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Aplikacija 4</title>
</head>
<body>
<a href="${pageContext.servletContext.contextPath}/mvc/projekt/pocetak">
		Početna
	</a><br>
	<h1>Aplikacija 1</h1>
	<c:out value="Status: ${requestScope.status}"/>
	
		<select id = "odabir">
			<option value="INIT">Inicijalizacija poslužitelja</option>
			<option value="QUIT">Prekid rada poslužitelja</option>
			<option value="LOAD">Učitavanje podataka</option>
			<option value="CLEAR">Brisanje podataka</option>
		</select> <input type="button" value="Naredba" onclick = "odiNaLink()">
	<c:out value="${requestScope.greska}"/>
	<script type="text/javascript">
		function odiNaLink() {
			var selectElement = document.querySelector('#odabir');
		    var output = selectElement.value;
			var link = "http://localhost:8080/mdurasek-aplikacija_4/mvc/projekt/pogled_4.4/" + output;
			window.location.href = link;
			nastavi()
			window.location.reload();
		}
		function spavajZa(vrijeme) {
			var now = new Date().getTime();
			while (new Date().getTime() < now + vrijeme) {
			}
		}
		function nastavi() {
			spavajZa(2000);
		}
	</script>
</body>
</html>