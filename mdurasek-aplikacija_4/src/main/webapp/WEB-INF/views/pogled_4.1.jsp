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
	<h1>Registracija korisnika</h1>
	<label for="korisnik">Korisničko ime: </label>
	<input type="text" name="korisnik" id="korisnik">
	<br>
	<label for="lozinka">Lozinka: </label>
	<input type="text" name="lozinka" id="lozinka">  
	<br>
	<label for="korisnik">Prezime: </label>
	<input type="text" name="prezime" id="prezime">
	<br>
	<label for="lozinka">Ime: </label>
	<input type="text" name="ime" id="ime">  
	<br>
	<label for="lozinka">Email: </label>
	<input type="text" name="email" id="email">  
	<br>
		<button onclick="odiNaLink()" id = "but">Registriraj korisnika</button>
	<c:out value="${requestScope.reg}"/>
	<script type="text/javascript">
		function odiNaLink() {
			var link = "http://localhost:8080/mdurasek-aplikacija_4/mvc/projekt/pogled_4.1/"
					+ document.getElementById("korisnik").value + "/" + 
					document.getElementById("lozinka").value + "/" + document.getElementById("prezime").value + "/" + 
					document.getElementById("ime").value + "/" + document.getElementById("email").value;
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