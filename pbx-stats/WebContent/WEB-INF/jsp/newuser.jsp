<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Nuevo usuario</title>
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
	<form method="post" action="?action=confirmNewUser" class="form-user">
		<h2 class="form-user-heading">Editar usuario</h2>
		<fieldset class="form-group">
			<label for="CorreoElectrónico">Editar correo electrónico</label> <input
				type="email" class="form-control" name="newEmail"
				placeholder="Introduce email">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Nombre</label> <input
				type="text" class="form-control" name="newName"
				placeholder="Nombre">
		</fieldset>
		<fieldset class="form-group">
			<label for="Password">Nueva contraseña</label> <input type="password"
				class="form-control" name="newPassword" placeholder="Contraseña">
		</fieldset>
		<div class="checkbox">
			<label> <input name="newAdminckbox" type="checkbox">Usuario administrador
			</label>
		</div>
		<select multiple="multiple" size="10" name="pbxs[]">
			<c:forEach var="pbx" items="${pbxsList}">
				<option value="${pbx.idpbx}">${pbx.name}</option>
			</c:forEach>
		</select>
		<br>
		<button type="submit" class="btn btn-success">Confirmar</button>
		<button type="submit" class="btn btn-danger" formaction="?action=cancelNewUser">Cancelar</button>
	</form>
	<script>
		$('select[name="pbxs[]"]').bootstrapDualListbox({
			  nonSelectedListLabel: 'Centralitas disponibles',
			  selectedListLabel: 'Centralitas a las que tiene acceso'
			})
	</script>
</body>
</html>