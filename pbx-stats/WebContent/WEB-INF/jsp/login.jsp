<%@ page language="java" contentType="text/html; charset=utf-8"
pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../ico/favicon.ico">

    <title>Inicio de sesi�n</title>

    <!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet" TYPE="text/css">

    <!-- Custom styles for this template -->
    <link href="css/signin.css" rel="stylesheet" TYPE="text/css">

  </head>

  <body>
  
	<c:forEach items="${cookie}" var="currentCookie">  
	    <c:if test="${currentCookie.value.name == 'usuario'}">
	    	<c:set var="email" value="${currentCookie.value.value}"/>
	    </c:if>
	</c:forEach>
	
    <div class="container">
    <form method="post" action="?action=doLogin" class="form-signin">
        <h2 class="form-signin-heading">Inicio de sesi�n</h2>
        <label for="inputEmail" class="sr-only">Correo electr�nico</label>
        <input type="email" id="inputEmail" class="form-control" placeholder="Correo Electr�nico" required autofocus name="email" value="${email}">
        <label for="inputPassword" class="sr-only">Contrase�a</label>
        <input type="password" id="inputPassword" class="form-control" placeholder="Contrase�a" required name="password">
		<c:if test="${requestScope.error != null}">
			<br>
			<p class="bg-danger">
			<c:out value="${requestScope.error}"/>
			</p>
		</c:if>
        <div class="checkbox">
          <label>
            <input name="ckbox" type="checkbox" checked="checked">Recordarme
          </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Iniciar sesi�n</button>
      </form>

    </div> <!-- /container -->
    
  </body>
</html>
    