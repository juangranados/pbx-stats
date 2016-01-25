<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Editar centralita</title>
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
	<form method="post" action="?action=confirmEditPbx&idPbx=${editPbx.idpbx}"
		class="form-user">
		<h2 class="form-user-heading">Editar centralita</h2>
		<fieldset class="form-group">
			<label for="Nombre">Editar Nombre</label> <input
				type="text" class="form-control" name="editedName" required
				placeholder="Introduce nombre" value="${editPbx.name}">
		</fieldset>
		<fieldset class="form-group">
			<label for="IP">Editar dirección IP</label> <input
				type="text" class="form-control" name="editedIp" required
				placeholder="Introduce ip" value="${editPbx.ip}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Port">Editar puerto</label> <input
				type="number" class="form-control" name="editedPort" required
				placeholder="Introduce puertp" value="${editPbx.port}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Usuario">Editar nombre de usuario</label> <input
				type="text" class="form-control" name="editedUsername" required
				placeholder="Introduce el nombre de usuario" value="${editPbx.username}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Password">Nueva contraseña</label> <input
				type="password" class="form-control" name="editedPassword"
				placeholder="Introduce nueva contraseña">
		</fieldset>
		<fieldset class="form-group">
			<label for="BBDD">Editar base de datos</label> <input
				type="text" class="form-control" name="editedDb" required
				placeholder="Introduce nombre de la base de datos" value="${editPbx.db}">
		</fieldset>
		<fieldset class="form-group">
			<label for="cdr">Editar nombre de la base de datos CDR</label> <input
				type="text" class="form-control" name="editedCdrname" required
				placeholder="Introduce el nombre de la base de datos CDR" value="${editPbx.cdrname}">
		</fieldset>
		<fieldset class="form-group">
			<label for="src">Editar nombre del campo origen (src)</label> <input
				type="text" class="form-control" name="editedSrc" required
				placeholder="Introduce el nombre nombre del campo origen" value="${editPbx.src}">
		</fieldset>
		<fieldset class="form-group">
			<label for="dst">Editar nombre del campo destino (scr)</label> <input
				type="text" class="form-control" name="editedDst" required
				placeholder="Introduce el nombre nombre del campo destino" value="${editPbx.dst}">
		</fieldset>
		<fieldset class="form-group">
			<label for="datetime">Editar nombre del campo fecha (datetime)</label> <input
				type="text" class="form-control" name="editedDatetime" required
				placeholder="Introduce el nombre nombre del campo fecha" value="${editPbx.datetime}">
		</fieldset>
		<fieldset class="form-group">
			<label for="duration">Editar nombre del campo duración (duration)</label> <input
				type="text" class="form-control" name="editedDuration" required
				placeholder="Introduce el nombre nombre del campo duración" value="${editPbx.duration}">
		</fieldset>
		<fieldset class="form-group">
			<label for="billsec">Nombre del campo duración real (billsec)</label> <input
				type="text" class="form-control" name="editedBillsec" required
				placeholder="Introduce el nombre nombre del campo billsec" value="${editPbx.billsec}">
		</fieldset>
		<fieldset class="form-group">
			<label for="disposition">Nombre del campo estado (disposition)</label> <input
				type="text" class="form-control" name="editedDisposition" required
				placeholder="Introduce el nombre nombre del campo disposition" value="${editPbx.disposition}">
		</fieldset>
		<fieldset class="form-group">
			<label for="calltype">Nombre del campo tipo de llamada (calltype)</label> <input
				type="text" class="form-control" name="editedCalltype" required
				placeholder="Introduce el nombre nombre del campo calltype" value="${editPbx.calltype}">
		</fieldset>
		<select multiple="multiple" name="users[]">
			<c:forEach var="userByPbx" items="${usersByPbxList}">
				<option value="${userByPbx.iduser}" selected="selected">${userByPbx.name}</option>
			</c:forEach>
			<c:forEach var="reverseUserByPbx" items="${reverseUsersByPbxList}">
				<option value="${reverseUserByPbx.iduser}">${reverseUserByPbx.name}</option>
			</c:forEach>
		</select>
		<br>
		<button type="submit" class="btn btn-success">Confirmar</button>
		<button type="submit" class="btn btn-danger" formaction="?action=cancelEditPbx">Cancelar</button>
	</form>
	<script>
		$('select[name="users[]"]').bootstrapDualListbox({
			  nonSelectedListLabel: 'Usuarios disponibles',
			  selectedListLabel: 'Usuarios que tienen acceso'
			})
	</script>
</body>
</html>