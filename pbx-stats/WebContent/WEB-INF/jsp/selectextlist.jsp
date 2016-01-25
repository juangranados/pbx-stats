<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Selección de extensiones</title>
<jsp:include page="/WEB-INF/jsp/boostrap.jsp" />
<link href="css/styles.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" href="css/bootstrap-duallistbox.css">
<script	src="http://cdnjs.cloudflare.com/ajax/libs/prettify/r298/run_prettify.min.js"></script>
<script src="src/jquery.bootstrap-duallistbox.js"></script>
</head>
<body>
</body>
<div class="container">
	<jsp:include page="/WEB-INF/jsp/header.jsp" />
	<h3>Extensiones con datos entre el rango de fechas especificado</h3>
	<form method="post" action="?action=${reportType}Filtered&storeConditions=true&fechaInicio=${fechaInicio}&horaInicio=${horaInicio}&fechaFin=${fechaFin}&horaFin=${horaFin}&idPbx=${idPbx}" class="form-user">
	<select multiple="multiple" size="10" name="sourceList">
		<c:forEach var="src" items="${srcList}">
			<option value="${src}">${src}</option>
		</c:forEach>
	</select>
	<br>
	<button type="submit" class="btn btn-primary">Generar informe</button>
	</form>
</div>
<script>
	$('select[name="sourceList"]').bootstrapDualListbox({
		  nonSelectedListLabel: 'Extensiones disponibles',
		  selectedListLabel: 'Extensiones seleccionadas'
	})
</script>
</html>