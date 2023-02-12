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
	<h1>Prijava korisnika</h1>
	<label for="korisnik">Korisničko ime: </label>
	<input type="text" name="korisnik" id="korisnik">
	<br>
	<label for="lozinka">Lozinka: </label>
	<input type="text" name="lozinka" id="lozinka">  
	<br>
	<button onclick="odiNaLink()" id = "but">Prijavi se</button>
	<c:out value="${requestScope.prijava}"/>
	<script type="text/javascript">
		function odiNaLink() {
			var link = "http://localhost:8080/mdurasek-aplikacija_4/mvc/projekt/pogled_4.2/"
					+ document.getElementById("korisnik").value + "/" + document.getElementById("lozinka").value;
			window.location.href = link;
			nastavi()
			window.location.reload();
		}
		function spavajZa(vrijeme){
		    var now = new Date().getTime();
		    while(new Date().getTime() < now + vrijeme){ 
		    }
		}
		function nastavi(){
		    spavajZa(2000);
		}
	</script>
	
</body>
</html>