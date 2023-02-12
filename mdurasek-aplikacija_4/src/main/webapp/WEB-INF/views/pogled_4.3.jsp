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
	<h1>Pregled korisnika</h1>
		<table>
		<tr>
			<th>Korisnik</th><th>Lozinka</th><th>Prezime</th><th>Ime</th><th>Email</th>
		</tr>
		<button onclick="odiNaLink()" id = "but">Obriši token</button>
		<c:forEach var="a" items="${requestScope.korisnici}">
			<tr>
				<td>${a.korisnik}</td>
				<td>${a.lozinka}</td>
				<td>${a.prezime}</td>
				<td>${a.ime}</td>
				<td>${a.email}</td>
			</tr>
		</c:forEach>
	</table>
	<script type="text/javascript">
		function odiNaLink() {
			var link = "http://localhost:8080/mdurasek-aplikacija_4/mvc/projekt/pogled_4.3/d";
			window.location.href = link;
		}
		
	</script>
</body>
</html>