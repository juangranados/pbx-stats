<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Lista de centralitas</title>
<!-- CSS y JS Boostrap-->
<jsp:include page="/WEB-INF/jsp/boostrap.jsp" />
<!-- CSS Barra de navegación -->
<link href="css/styles.css" rel="stylesheet">
</head>
<body>
	<!-- Static navbar -->
	<div class="container">
		<jsp:include page="/WEB-INF/jsp/header.jsp" />
		<c:if test="${pbxsMessage != null}">
			<br>
			<p class="bg-info">
				<c:out value="${pbxsMessage}" />
			</p>
		</c:if>
		<a href="/${initParam.appContext}/pbxs?action=newPbx"
			class="btn btn-info" role="button">Nueva centralita</a>
		<h2>Lista de centralitas</h2>
		<p>Selecciona la centralita que quieres modificar</p>
		<table class="table table-hover">
			<thead>
				<tr>
					<th>ID</th>
					<th>Nombre</th>
					<th>IP</th>
					<th>Puerto</th>
					<th>Usuario</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="pbx" items="${pbxsList}">
					<tr>
						<td><a
							href="/${initParam.appContext}/pbxs?action=editPbx&idPbx=${pbx.idpbx}">${pbx.idpbx}</a></td>
						<td><a
							href="/${initParam.appContext}/pbxs?action=editPbx&idPbx=${pbx.idpbx}">${pbx.name}</a></td>
						<td><a
							href="/${initParam.appContext}/pbxs?action=editPbx&idPbx=${pbx.idpbx}">${pbx.ip}</a></td>
						<td><a
							href="/${initParam.appContext}/pbxs?action=editPbx&idPbx=${pbx.idpbx}">${pbx.port}</a></td>
						<td><a
							href="/${initParam.appContext}/pbxs?action=editPbx&idPbx=${pbx.idpbx}">${pbx.username}</a></td>
						<td>
						<td>
							<a href="/${initParam.appContext}/pbxs?action=editPricing&idPbx=${pbx.idpbx}"
							class="btn btn-info btn-xs" role="button">Editar facturación</a>
							<a href="/${initParam.appContext}/pbxs?action=deletePbx&idPbx=${pbx.idpbx}"
							class="btn btn-danger btn-xs" role="button">Eliminar</a>
						</td>
					<tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>