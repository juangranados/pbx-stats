<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- Static navbar -->
<nav class="navbar navbar-default">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="/${initParam.appContext}/main">PBX Stats</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="active"><a href="/${initParam.appContext}/main">Inicio</a></li>
				<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-haspopup="true"
						aria-expanded="false">Informes<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="/${initParam.appContext}/reports?action=extGeneralReceived&storeConditions=false">Llamadas recibidas</a></li>
							<li><a href="/${initParam.appContext}/reports?action=extGeneralMade&storeConditions=false">Llamadas realizadas</a></li>
							<li><a href="/${initParam.appContext}/reports?action=extGeneralUnansweredReceived&storeConditions=false">Llamadas recibidas no contestadas</a></li>
							<li><a href="/${initParam.appContext}/reports?action=extGeneralUnansweredMade&storeConditions=false">Llamadas realizadas no contestadas</a></li>
							<li><a href="/${initParam.appContext}/reports?action=extGeneralPricing&storeConditions=false">Facturación entre dos fechas</a></li>
						</ul>
					</li>
				<li><a href="#">Perfil</a></li>
				<li><a href="/${initParam.appContext}/main?action=logout">Cerrar sesión</a></li>
				<c:if test="${userAdmin != null and userAdmin eq true}">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-haspopup="true"
						aria-expanded="false">Administración<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="/${initParam.appContext}/users?action=users">Usuarios</a></li>
							<li><a href="/${initParam.appContext}/pbxs?action=pbxs">Centralitas</a></li>
						</ul>
					</li>
				</c:if>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
	<!--/.container-fluid -->
</nav>