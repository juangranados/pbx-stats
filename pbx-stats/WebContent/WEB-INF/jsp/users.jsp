<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Lista de usuarios</title>
<!-- CSS y JS Boostrap-->
<jsp:include page="/WEB-INF/jsp/boostrap.jsp"/>
<!-- CSS Barra de navegación -->
<link href="css/styles.css" rel="stylesheet">
</head>
<body>
	<!-- Static navbar -->
	<div class="container">
		<jsp:include page="/WEB-INF/jsp/header.jsp"/>
		<c:if test="${usersMessage != null}">
			<br>
			<p class="bg-info">
				<c:out value="${usersMessage}" />
			</p>
		</c:if>
		<a href="/${initParam.appContext}/users?action=newuser" class="btn btn-info" role="button">Nuevo usuario</a>
		<h2>Lista de usuarios</h2>
		<p>Selecciona el usuario que quieres modificar</p>
		<table class="table table-hover">
			<thead>
				<tr>
					<th>ID</th>
					<th>Nombre</th>
					<th>email</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="user" items="${userList}">
					<tr>
						<td><a
							href="/${initParam.appContext}/users?action=editUser&iduser=${user.iduser}">${user.iduser}</a></td>
						<td><a
							href="/${initParam.appContext}/users?action=editUser&iduser=${user.iduser}">${user.name}</a></td>
						<td><a
							href="/${initParam.appContext}/users?action=editUser&iduser=${user.iduser}">${user.email}</a></td>
						<td><a href="/${initParam.appContext}/users?action=deleteUser&iduser=${user.iduser}" class="btn btn-danger btn-xs" role="button">Eliminar</a></td>
					<tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>