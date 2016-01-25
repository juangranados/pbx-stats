<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Selección de reporte de esten</title>
<!-- CSS y JS Boostrap-->
<jsp:include page="/WEB-INF/jsp/boostrap.jsp"/>
<!-- CSS Barra de navegación -->
<link href="css/styles.css" rel="stylesheet">
</head>
<body>
	<div class="container">
		<jsp:include page="/WEB-INF/jsp/header.jsp"/>
		<!-- Main component for a primary marketing message or call to action -->
		<div class="jumbotron">
			<p>Selecciona el tipo de reporte a generar</p>
			<p>
				<a class="btn btn-lg btn-primary"
					href="/${initParam.appContext}/reports?action=ext" role="button">Extensiones
					&raquo;</a>
			</p>
		</div>
	</div>
</body>
</html>