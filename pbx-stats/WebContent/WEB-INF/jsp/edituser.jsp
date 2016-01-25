<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Editar usuario</title>
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
	<form method="post" action="?action=confirmEditUser&iduser=${iduser}"
		class="form-user">
		<h2 class="form-user-heading">Editar usuario</h2>
		<fieldset class="form-group">
			<label for="CorreoElectrónico">Editar correo electrónico</label> <input
				type="email" class="form-control" name="editedEmail" required
				placeholder="Introduce email" value="${editIdEmail}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Nombre">Editar nombre</label> <input
				type="text" class="form-control" name="editedName" required
				placeholder="Nombre" value="${editName}">
		</fieldset>
		<fieldset class="form-group">
			<label for="Password">Nueva contraseña</label> <input type="password"
				class="form-control" name="editedPassword" placeholder="Contraseña">
		</fieldset>
		<div class="checkbox">
			<label> <input name="editedAdminckbox" type="checkbox"
				<c:if test="${editAdminUser == true}">
					checked="checked"
				</c:if>
				>Usuario administrador
			</label>
		</div>
		<select multiple="multiple" name="pbxs[]">
			<c:forEach var="pbxbyuser" items="${pbxsByUserList}">
				<option value="${pbxbyuser.idpbx}" selected="selected">${pbxbyuser.name}</option>
			</c:forEach>
			<c:forEach var="reversePbxbyuser" items="${reversePbxsByUserList}">
				<option value="${reversePbxbyuser.idpbx}">${reversePbxbyuser.name}</option>
			</c:forEach>
		</select>
		<br>
		<button type="submit" class="btn btn-success">Confirmar</button>
		<button type="submit" class="btn btn-danger" formaction="?action=cancelEditUser">Cancelar</button>
	</form>
	<script>
		$('select[name="pbxs[]"]').bootstrapDualListbox({
			  nonSelectedListLabel: 'Centralitas disponibles',
			  selectedListLabel: 'Centralitas a las que tiene acceso'
			})
	</script>
</body>
</html>