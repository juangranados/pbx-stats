<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Resultados de la consulta ${reportName}</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- CSS y JS Boostrap-->
  <jsp:include page="/WEB-INF/jsp/boostrap.jsp" />
  <!-- CSS Barra de navegación -->
  <link href="css/styles.css" rel="stylesheet">
</head>
<body>
	<div class="container">
		<jsp:include page="/WEB-INF/jsp/header.jsp" />
		<!-- Main component for a primary marketing message or call to action -->
		<div class="jumbotron">
			<h2 class="text-warning">Se ha producido un error</h2>
			<p>${error}</p>
			<p>
				<a class="btn btn-lg btn-primary"
					href="/${initParam.appContext}/main" role="button">Ir a inicio &raquo;</a>
			</p>
		</div>
	</div>
	<!-- /container -->
</body>
</html>