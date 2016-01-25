<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Resultados de la consulta ${reportName}</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- CSS y JS Boostrap-->
  <jsp:include page="/WEB-INF/jsp/boostrap.jsp" />
  <!-- CSS Barra de navegación -->
  <link href="css/styles.css" rel="stylesheet">
</head>
<body>
<div class="container">
	<jsp:include page="/WEB-INF/jsp/header.jsp" />
	  <h2>${reportName}</h2>       
	  <table class="table table-hover">
	    <thead>
	      <tr>
	      <c:forEach var="label" items="${results[0]}">
	        <th>${label}</th>
	      </c:forEach>
	      </tr>
	    </thead>
	    <tbody>
	    	<c:set var="firstRow" value="true"/>
	    	<c:forEach var="row" items="${results}">
	    		<c:if test="${firstRow == 'false'}">
	    			<tr>
	    			<c:set var="src" value="${row[0]}"/>
	    			<c:forEach var="col" items="${row}">  				
	    					<td>${col}</td>
	    			</c:forEach>
	    		</tr>
	    		</c:if>
	    		<c:if test="${firstRow == 'true'}">
	    			<c:set var="firstRow" value="false"/>
	    		</c:if>
	    	</c:forEach>
	    </tbody>
	  </table>
	</div>

</body>
</html>