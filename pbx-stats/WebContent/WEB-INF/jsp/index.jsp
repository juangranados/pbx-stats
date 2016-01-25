<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<!-- CSS y JS Boostrap-->
<jsp:include page="/WEB-INF/jsp/boostrap.jsp" />
<!-- CSS Barra de navegación -->
<link href="css/styles.css" rel="stylesheet">
<title>Inicio</title>
</head>
<body>
	<div class="container">
		<jsp:include page="/WEB-INF/jsp/header.jsp" />
		<!-- Main component for a primary marketing message or call to action -->
		<div class="jumbotron">
			<h1>PBX Stats</h1>
			<p>Esta aplicación permite la generación de informes a partir del
				registro de llamadas de centralitas basadas en Asterisk.
			<p>
				<a class="btn btn-lg btn-primary"
					href="/${initParam.appContext}/reports?action=extGeneralPricing&storeConditions=false" role="button">Ir a
					facturación &raquo;</a>
			</p>
		</div>
	</div>
	<!-- /container -->
</body>
</html>