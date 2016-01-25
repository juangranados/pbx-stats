<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Nueva centralita</title>
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
	<form method="post" action="?action=confirmNewPbx"
		class="form-user">
		<h2 class="form-user-heading">Nueva centralita</h2>
		<fieldset class="form-group">
			<label for="Nombre">Nombre</label> <input
				type="text" class="form-control" name="newName" required
				placeholder="Introduce nombre">
		</fieldset>
		<fieldset class="form-group">
			<label for="IP">Dirección IP</label> <input
				type="text" class="form-control" name="newIp" required
				placeholder="Introduce ip">
		</fieldset>
		<fieldset class="form-group">
			<label for="Port">Puerto</label> <input
				type="number" class="form-control" name="newPort" required
				placeholder="Introduce número de puerto">
		</fieldset>
		<fieldset class="form-group">
			<label for="Usuario">Nombre de usuario</label> <input
				type="text" class="form-control" name="newUsername" required
				placeholder="Introduce el nombre de usuario">
		</fieldset>
		<fieldset class="form-group">
			<label for="Password">Contraseña</label> <input
				type="password" class="form-control" name="newPassword" required
				placeholder="Introduce nueva contraseña">
		</fieldset>
		<fieldset class="form-group">
			<label for="BBDD">Base de datos</label> <input
				type="text" class="form-control" name="newDb" required
				placeholder="Introduce nombre de la base de datos">
		</fieldset>
		<fieldset class="form-group">
			<label for="cdr">Nombre de la base de datos CDR</label> <input
				type="text" class="form-control" name="newCdrname" required
				placeholder="Introduce el nombre de la base de datos CDR">
		</fieldset>
		<fieldset class="form-group">
			<label for="src">Nombre del campo origen (src)</label> <input
				type="text" class="form-control" name="newSrc" required
				placeholder="Introduce el nombre nombre del campo origen">
		</fieldset>
		<fieldset class="form-group">
			<label for="dst">Nombre del campo destino (scr)</label> <input
				type="text" class="form-control" name="newDst" required
				placeholder="Introduce el nombre nombre del campo destino">
		</fieldset>
		<fieldset class="form-group">
			<label for="datetime">Nombre del campo fecha (datetime)</label> <input
				type="text" class="form-control" name="newDatetime" required
				placeholder="Introduce el nombre nombre del campo fecha">
		</fieldset>
		<fieldset class="form-group">
			<label for="duration">Nombre del campo duración (duration)</label> <input
				type="text" class="form-control" name="newDuration" required
				placeholder="Introduce el nombre nombre del campo duración">
		</fieldset>
		<fieldset class="form-group">
			<label for="billsec">Nombre del campo duración real (billsec)</label> <input
				type="text" class="form-control" name="newBillsec" required
				placeholder="Introduce el nombre nombre del campo billsec">
		</fieldset>
		<fieldset class="form-group">
			<label for="disposition">Nombre del campo estado (disposition)</label> <input
				type="text" class="form-control" name="newDisposition" required
				placeholder="Introduce el nombre nombre del campo disposition">
		</fieldset>
		<fieldset class="form-group">
			<label for="calltype">Nombre del campo tipo de llamada (calltype)</label> <input
				type="text" class="form-control" name="newCalltype" required
				placeholder="Introduce el nombre nombre del campo calltype">
		</fieldset>
		<select multiple="multiple" name="users[]">
			<c:forEach var="user" items="${userList}">
				<option value="${user.iduser}">${user.name}</option>
			</c:forEach>
		</select>
		<br>
		<button type="submit" class="btn btn-success">Confirmar</button>
		<button type="submit" class="btn btn-danger" formaction="?action=cancelNewPbx">Cancelar</button>
	</form>
	<script>
		$('select[name="users[]"]').bootstrapDualListbox({
			  nonSelectedListLabel: 'Usuarios disponibles',
			  selectedListLabel: 'Usuarios que tienen acceso'
			})
	</script>
</body>
</html>