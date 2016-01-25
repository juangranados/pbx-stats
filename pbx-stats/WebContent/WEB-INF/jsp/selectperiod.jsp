<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
<title>Selección de periodo</title>
<link href="css/timepicker.css" rel="stylesheet" type="text/css"/>
<jsp:include page="/WEB-INF/jsp/boostrap.jsp" />
<link href="css/styles.css" rel="stylesheet" type="text/css"/>
<link href="css/datepicker.css" rel="stylesheet" TYPE="text/css"/>
<link href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
<script src="http://code.jquery.com/jquery-1.8.3.js"></script>
<script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>
<script type="text/javascript" src="timepicker/jquery.ui.timepicker.js?v=0.3.3"></script>
<script type="text/javascript" src="https://apis.google.com/js/plusone.js"></script>
<script src="src/jquery.bootstrap-duallistbox.js"></script>

<script type="text/javascript">
            $(document).ready(function() {
                $('#timepickerInicio').timepicker({
                    showPeriodLabels: false
                });
              });
</script>

<script type="text/javascript">
            $(document).ready(function() {
                $('#timepickerFin').timepicker({
                    showPeriodLabels: false
                });
              });
</script>

<script>
	$.datepicker.regional['es'] = {
		closeText : 'Cerrar',
		prevText : '<Ant',
 		nextText: 'Sig>',
		currentText : 'Hoy',
		monthNames : [ 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
				'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre',
				'Diciembre' ],
		monthNamesShort : [ 'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul',
				'Ago', 'Sep', 'Oct', 'Nov', 'Dic' ],
		dayNames : [ 'Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves',
				'Viernes', 'Sábado' ],
		dayNamesShort : [ 'Dom', 'Lun', 'Mar', 'Mié', 'Juv', 'Vie', 'Sáb' ],
		dayNamesMin : [ 'Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sá' ],
		weekHeader : 'Sm',
		dateFormat : 'dd/mm/yy',
		firstDay : 1,
		isRTL : false,
		showMonthAfterYear : false,
		yearSuffix : ''
	};
	$.datepicker.setDefaults($.datepicker.regional['es']);
	$(function() {
		$("#fecha").datepicker();
	});
</script>
<script>
	$(document).ready(
			function() {
				$("#datepickerInicio").datepicker(
						{ 	dateFormat: 'dd-mm-yy',
							numberOfMonths : 2,
							maxDate : 0,
							onSelect : function(selected) {
								$("#datepickerFin").datepicker("option",
										"minDate", selected)
							}
						});
				$("#datepickerFin").datepicker(
						{
							dateFormat: 'dd-mm-yy',
							numberOfMonths : 2,
							maxDate : 0,
							onSelect : function(selected) {
								$("#datepickerInicio").datepicker("option",
										"maxDate", selected)
							}
						});
			});
</script>
</head>
<body>
<body>
	<div class="container">
		<jsp:include page="/WEB-INF/jsp/header.jsp" />
		<form action="/${initParam.appContext}/reports?action=selectPeriodResponse&reportType=${reportType}Result&storeConditions=true" method="post" class="form-datepicker">
			<h4 class="form-datepicker-heading">Selección de fechas para el informe ${reportName}</h4>
			<div class="form-group">
				<label for="fecha">Fecha inicio</label> <input type="text"
					class="form-control" id="datepickerInicio" name="fechaInicio" required>
			</div>
			<div class="form-group">
				<label for="fecha">Fecha fin</label> <input type="text"
					class="form-control" id="datepickerFin" name="fechaFin" required>
			</div>
			<div class="form-group">
				<label for="hora">Hora inicio</label> <input type="text"
					class="form-control" id="timepickerInicio" name="horaInicio" value="00:00" required>
			</div>
			<div class="form-group">
				<label for="hora">Hora fin</label> <input type="text"
					class="form-control" id="timepickerFin" name="horaFin" value="23:59" required>
			</div>
			<label>Selecciona centralita</label>
			<select class="form-control" name="idPbx">
				<c:forEach var="pbx" items="${PbxsByUserList}">
				    <option value="${pbx.idpbx}">${pbx.name}</option>
				</c:forEach>
			</select> 
			<br>
			<c:if test="${filter != null}">
				<div class="checkbox">
					<label> <input name="selectExt" type="checkbox">Seleccionar extensiones</label>
				</div>
			</c:if>
		<br>
			<button type="submit" class="btn btn-primary">Generar informe</button>
		</form>
	</div>
</body>
</html>