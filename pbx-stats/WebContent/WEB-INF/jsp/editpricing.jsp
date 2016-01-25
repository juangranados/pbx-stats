<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<head>
<title>Editar facturación</title>
<meta charset="utf-8">
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/bootstrap-duallistbox.css">
<script	src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
<script	src="http://cdnjs.cloudflare.com/ajax/libs/prettify/r298/run_prettify.min.js"></script>
<script src="src/jquery.bootstrap-duallistbox.js"></script>
<link href="css/user.css" rel="stylesheet" TYPE="text/css">
<script	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
	<form method="post" action="?action=confirmEditPricing&idPbx=${pricing.idpbx}"
		class="form-user">
		<h2 class="form-user-heading">Editar precios facturación</h2>
		<p>Los precios están expresados en céntimos por minuto, sin decimales</p>
		<fieldset class="form-group">
			<label for="Nombre">Editar precio número fijo</label> <input
				type="number" class="form-control" name="fijo" required
				placeholder="Introduce valor" value="${pricing.fijo}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar establecimiento número fijo</label> <input
				type="number" class="form-control" name="efijo" required
				placeholder="Introduce valor" value="${pricing.efijo}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar precio número móvil</label> <input
				type="number" class="form-control" name="movil" required
				placeholder="Introduce valor" value="${pricing.movil}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar establecimiento número móvil</label> <input
				type="number" class="form-control" name="emovil" required
				placeholder="Introduce valor" value="${pricing.emovil}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar precio número tarificación adicional</label> <input
				type="number" class="form-control" name="adicional" required
				placeholder="Introduce valor" value="${pricing.adicional}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar establecimiento número tarificación adicional</label> <input
				type="number" class="form-control" name="eadicional" required
				placeholder="Introduce valor" value="${pricing.eadicional}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar precio número coste compartido</label> <input
				type="number" class="form-control" name="compartido" required
				placeholder="Introduce valor" value="${pricing.compartido}">
		</fieldset>
				<fieldset class="form-group">
			<label for="Nombre">Editar establecimiento número coste compartido</label> <input
				type="number" class="form-control" name="ecompartido" required
				placeholder="Introduce valor" value="${pricing.ecompartido}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar precio número internacional</label> <input
				type="number" class="form-control" name="internacional" required
				placeholder="Introduce valor" value="${pricing.internacional}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar establecimiento número internacional</label> <input
				type="number" class="form-control" name="einternacional" required
				placeholder="Introduce valor" value="${pricing.einternacional}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar precio número desconocido</label> <input
				type="number" class="form-control" name="desconocido" required
				placeholder="Introduce valor" value="${pricing.desconocido}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar establecimiento número desconocido</label> <input
				type="number" class="form-control" name="edesconocido" required
				placeholder="Introduce valor" value="${pricing.edesconocido}">
		</fieldset>
		<br>
		<button type="submit" class="btn btn-success">Confirmar</button>
		<button type="submit" class="btn btn-danger" formaction="?action=cancelEditPricing">Cancelar</button>
	</form>
</body>
</html>