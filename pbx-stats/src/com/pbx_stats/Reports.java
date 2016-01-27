package com.pbx_stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.pbx_stats.beans.Pbx;
import com.pbx_stats.beans.Pricing;

/**
 * Clase Reports
 * Recibe datos para realizar una consulta y devuelve el resultado
 * @author Juan Granados
 *
 */
public class Reports {
	
	private static final Logger log = LogManager.getLogger("Reports.java");
	public Reports(){
	}
	/**
	 * Convierte un número de segundos en un String con formato HH:MM:SS
	 * @param nSecondTime número de segundos a convertir
	 * @return String con formato HH:MM:SS
	 */
	private static String ConvertSecondToHHMMSSString(int nSecondTime) {
	    return LocalTime.MIN.plusSeconds(nSecondTime).toString();
	}
	/**
	 * Convierte el formato de fecha
	 * @param date Fecha a convertir en dd-MM-yyyy
	 * @return La fecha en formato de yyyy-MM-dd
	 */
	private static String convertDate(String date){
		DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date1 = inputFormat.parse(date);
			return outputFormat.format(date1).toString();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Calcula el coste de una llamada en base a unas tarifas
	 * @param number Número de teléfono de destino
	 * @param duration Duración de la llamada
	 * @param numCalls Numero de llamadas a ese teléfono
	 * @param pricing Objeto pricing que contiene las tarifas de la centralita
	 * @return Una matriz de tres elementos: zona de la llamada, tipo de teléfono y coste
	 */
	private static String[] pricing(String number, int duration, int numCalls,Pricing pricing) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		String[] result = new String[3];
		double coste=0;
		double establecimiento=0;
		double total;
		try {
			PhoneNumber phoneNumber = phoneUtil.parse(number, "ES");
			if (phoneUtil.isValidNumber(phoneNumber)) {
				PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
				result[0] = geocoder.getDescriptionForNumber(phoneNumber, new Locale("es_ES"));
				String region = phoneUtil.getRegionCodeForNumber(phoneNumber);
				
				if (region.equals("ES")) {
					switch (phoneUtil.getNumberType(phoneNumber)) {
					case FIXED_LINE:
						result[1] = "Fijo";
						coste = pricing.getFijo();
						establecimiento = pricing.getEfijo()*numCalls;
						break;
					case MOBILE:
						result[1] = "Móvil";
						coste = pricing.getMovil();
						establecimiento = pricing.getEmovil()*numCalls;
						break;
					case FIXED_LINE_OR_MOBILE:
						result[1] = "Móvil";
						coste = pricing.getMovil();
						establecimiento = pricing.getEmovil()*numCalls;
						break;
					case TOLL_FREE:
						result[1] = "Gratuíto";
						result[2] = "0";
						break;
					case PREMIUM_RATE:
						result[1] = "Tarificacion adicional";
						coste = pricing.getAdicional();
						establecimiento = pricing.getEadicional()*numCalls;
						break;
					case SHARED_COST:
						result[1] = "Coste compartido";
						coste = pricing.getCompartido();
						establecimiento = pricing.getEcompartido()*numCalls;
						break;
					default:
						result[1] = "Desconocido";
						coste = pricing.getDesconocido();
						establecimiento = pricing.getEdesconocido()*numCalls;
						break;
					}
				} else {
					result[1] = "Internacional";
					coste = pricing.getInternacional();
					establecimiento = pricing.getEinternacional()*numCalls;
				}
			} else {
				result[0] = result[1] = "Desconocido";
				coste = pricing.getDesconocido();
				establecimiento = pricing.getEdesconocido()*numCalls;
			}
			total=(((duration * coste) / 60) + establecimiento) / 100;
			total=Math.round(total*100.0)/100.0;
			result[2] = Double.toString(total);
		} catch (NumberParseException e) {
			log.error("Error al analizar el número: " + number + ": " + e.toString());
			result[0]="Error";
			result[1]="Error";
			result[2]="0"; 
			return result;
		}
		return result;
	}
	private static Connection connect(Pbx pbx) throws SQLException{
		String jdbcString= "jdbc:mysql://"+pbx.getIp()+":"+pbx.getPort()+"/"+pbx.getDb()+"?user="+pbx.getUsername()+"&password="+pbx.getPassword();
		return DriverManager.getConnection(jdbcString);
	}
	/**
	 * Recibe una consulta sql para realizar en la base de datos de una centralita
	 * y devuelve su resultado en forma de matriz bidimensional.
	 * @param pbx Objeto PBX que representa a la centralita sobre la que se ejecuta la consulta.
	 * @param query Consulta a la base de datos CDR de la centralita
	 * @param billing Indica si se trata de una consulta de facturación
	 * @param pricing Objeto pricing que representa la tarificación de una centralita
	 * @param sourceFilter Lista de strings con las extensiones a las que se acotará la consulta
	 * @return Matriz bidimensional de Strings que contiene una tabla con el resultado
	 * @throws SQLException
	 */
	private static String[][] result(Pbx pbx, String query, boolean billing, Pricing pricing, List<String> sourceFilter,Boolean brief)
			throws SQLException {
		int f = 1;
		double total = 0;
		//Conexión con la base de datos que contiene la tabla CDR de la centralita.
		Connection con = connect(pbx);
		log.info("Conectado a " + pbx.getIp());
		log.info("Ejecutando: " + query);
		//Se prepara y ejecuta la consulta recibida
		PreparedStatement preparedStatement = con.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();
		//Se obtiene el número de columnas y filas de la consulta
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		rs.last();
		int rowsNumber = rs.getRow();
		//Matriz que almacena el resultado de la tarificación
		String[] pricingResult = null;
		//Se inicializa la variables para almacenar el resultado
		String result[][] = null;
		int c = 0;
		//Si se pide una tarificación de la consulta
		if (billing) {
			log.info("Se procesará una consulta de facturación.");
			//Se dimensiona la matriz que almacena el resultado para incluir la tarificación
			result = new String[rowsNumber + 2][columnsNumber + 3];
			for (c = 0; c < columnsNumber; c++) {
				//Se inicializa la primera fila que contiene el nombre de las columnas
				result[0][c] = new String(rsmd.getColumnLabel(c + 1));
			}
			//Se inicializa la primera fila que contiene el nombre de las columnas
			result[0][c] = "Zona";
			result[0][c + 1] = "Tipo";
			result[0][c + 2] = "Coste";
		} 
		//No se realizará el cálculo de la tarificación
		else {
			//Se dimensiona la matriz que almacena el resultado
			result = new String[rowsNumber + 1][columnsNumber];
			for (c = 0; c < columnsNumber; c++) {
				//Se inicializa la primera fila que contiene el nombre de las columnas
				result[0][c] = new String(rsmd.getColumnLabel(c + 1));
			}
		}
		//Se coloca el ResultSet a la primera posición de los resultados
		rs.beforeFirst();
		//Se procesa la consulta, mientras el resulset tenga resultados
		while (rs.next()) {
			//Se recorren las columnas
			for (c = 1; c <= columnsNumber; c++) {
				//Si hay filtrado de extensiones, se sale del bucle si la extension no está en la lista.
				//La fila sólo se procesa si está en la lista
				if (sourceFilter != null && c == 1) {
					if (!sourceFilter.contains(rs.getString(c))) {
						break;
					}
				}
				//Si el resultado incluye facturación y la columna es la 2 (número de destino), se procesa la tarificación
				if (billing && c == 2) {
					if(!brief){
						pricingResult = pricing(rs.getString(c), rs.getInt(c + 1),1,pricing);
					}else{
						pricingResult = pricing(rs.getString(c), rs.getInt(c + 1),rs.getInt(c + 2), pricing);
					}
				}
				if (billing && c == 3) {
					result[f][c - 1] = new String(ConvertSecondToHHMMSSString(rs.getInt(c)));
				} else {
					result[f][c - 1] = new String(rs.getString(c));
				}
			}
			if (billing) {
				if (sourceFilter != null && c == 1) {
					if (!sourceFilter.contains(rs.getString(c))) {
						continue;
					}
				}
				result[f][c - 1] = new String(pricingResult[0]);
				result[f][c] = new String(pricingResult[1]);
				result[f][c + 1] = new String(pricingResult[2]);
				total += Double.parseDouble(result[f][c + 1]);
			}
			f++;
		}
		if (billing) {
			for (c = 0; c <= columnsNumber; c++) {
				result[f][c] = "";
			}
			result[f][columnsNumber + 1] = "Total";
			total = Math.round(total * 100.0) / 100.0;
			result[f][columnsNumber + 2] = Double.toString(total);
		}
		rs.close();
		log.info("Se han procesado " + rowsNumber + " filas en la consulta.");
		log.info("Ha finalizado la ejecución de la consulta. Se devuelve el resultado al Servlet.");
		return result;
	}
	/**
	 * Genera el resultado de un reporte de llamadas por extensiones
	 * @param fechaInicio Fecha de inicio de la consulta
	 * @param fechaFin Fecha de fin de la consulta
	 * @param horaInicio Hora de inicio de la consulta
	 * @param horaFin Hora de fin de la consulta
	 * @param pbx Objeto PBX que representa a la centralita sobre la que se ejecuta la consulta.
	 * @param Calltype Tipo de llamada sobre la que se ejecuta la consulta
	 * @param source Opcional. Se especifica si se quiere generar un reporte de un número específico
	 * @param brief Indica si es un informe agrupado por catidad de llamadas (true) o completo
	 * @param srcFilter Opcional.Si se quire filtrar el reporte por una lista de extensiones 
	 * @return Matriz bidimensional de Strings que contiene una tabla con el resultado
	 * @throws SQLException
	 */
	public static String[][] reportExtGeneral(String fechaInicio,String fechaFin,String horaInicio,String horaFin,Pbx pbx,String Calltype,String source,Boolean brief,String[] srcFilter)throws SQLException{
		List<String> sourceFilter=null;
		if (srcFilter!=null){
			sourceFilter = new ArrayList<String>(Arrays.asList(srcFilter));
		}
		fechaInicio=convertDate(fechaInicio) + " " + horaInicio +":00" ;
		fechaFin=convertDate(fechaFin) + " " + horaFin + ":59";
		String src=pbx.getSrc();
		String billsec=pbx.getBillsec();
		String cdr=pbx.getCdrname();
		String datetime=pbx.getDatetime();
		String calltype=pbx.getCalltype();
		String dst=pbx.getDst();
		String query=null;
		if (source==null){
			query = "select " + src +" as Origen, count(*) as Llamadas,CONCAT(sec_to_time(sum(" + billsec + ")),'') as Tiempo from " + cdr +
					" where (" + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin + "' and " + calltype +"='" + Calltype + "') group by " + src + " order by count(*) desc";
			return result(pbx,query,Boolean.FALSE,null,sourceFilter,brief);
		}
		else{
			if (!brief){
				query = "select "+ dst + " as Destino," + "CONCAT(sec_to_time("+ billsec + "),'') as Duracion, "+ datetime + " as Fecha from " + cdr + 
						" where ("+ src + "='" + source + "' and " + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin + "' and " + calltype +"='" + Calltype + "') order by " + datetime;
			}
			else{
				query = "select "+ dst + " as Destino," + "count(*) as Llamadas," + "CONCAT(sec_to_time(sum("+ billsec + ")),'') as Duracion from " + cdr + 
						" where ("+ src + "='" + source + "' and " + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin + "' and " + calltype +"='" + Calltype + "') group by " + dst + " order by Llamadas desc";
			}
		}
		return result(pbx,query,Boolean.FALSE,null,null,brief);
	}
	/**
	 * Genera el resultado de un reporte de llamadas no contestadas por extensiones
	 * @param fechaInicio Fecha de inicio de la consulta
	 * @param fechaFin Fecha de fin de la consulta
	 * @param horaInicio Hora de inicio de la consulta
	 * @param horaFin Hora de fin de la consulta
	 * @param pbx Objeto PBX que representa a la centralita sobre la que se ejecuta la consulta.
	 * @param Calltype Tipo de llamada sobre la que se ejecuta la consulta
	 * @param source Opcional. Se especifica si se quiere generar un reporte de un número específico
	 * @param brief Indica si es un informe agrupado por catidad de llamadas (true) o completo
	 * @param srcFilter Opcional.Si se quire filtrar el reporte por una lista de extensiones 
	 * @return Matriz bidimensional de Strings que contiene una tabla con el resultado
	 * @throws SQLException
	 */
	public static String[][] reportExtUnanswered(String fechaInicio, String fechaFin,String horaInicio,String horaFin, Pbx pbx, String Calltype,String Disposition,String source,Boolean brief,String[] srcFilter) throws SQLException{
		List<String> sourceFilter=null;
		if (srcFilter!=null){
			sourceFilter = new ArrayList<String>(Arrays.asList(srcFilter));
		}
		fechaInicio=convertDate(fechaInicio) + " " + horaInicio +":00" ;
		fechaFin=convertDate(fechaFin) + " " + horaFin + ":59";
		String disposition=pbx.getDisposition();
		String datetime=pbx.getDatetime();
		String src=pbx.getSrc();
		String calltype=pbx.getCalltype();
		String cdr=pbx.getCdrname();
		String duration=pbx.getDuration();
		String dst=pbx.getDst();
		String query=null;
		if (source==null){
			query="select " + src + " as Origen,count(*) as Llamadas," + disposition + " as Estado from " + cdr + 
					" where (" + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin +"' and " + disposition +"='"+ Disposition + "' and " + calltype + "='" + Calltype + "')" + 
					"group by Origen order by count(*) desc";
			return result(pbx,query,Boolean.FALSE,null,sourceFilter,brief);
		}
		else{
			if (!brief)
			{
				query="select " + dst+ " as Destino," + datetime + " as Fecha," + "sec_to_time(" + duration + ") as Tiempo_espera," + disposition + " as Estado from " + cdr + 
						" where (" + src + "='" + source + "' and " + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin +"' and " + disposition +"='"+ Disposition + "' and " + calltype + "='" + Calltype + "')" + 
						"order by Fecha desc";
			}
			else{
				query="select " + dst+ " as Destino,count(*) as llamadas," + disposition + " as Estado from " + cdr + 
						" where (" + src + "='" + source + "' and " + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin +"' and " + disposition +"='"+ Disposition + "' and " + calltype + "='" + Calltype + "')" + 
						"group by Destino order by count(*) desc";
			}
		}
		return result(pbx,query,Boolean.FALSE,null,null,brief);
	}
	/**
	 * Genera el resultado de un reporte de llamadas por extensiones con tarificación de llamadas
	 * @param fechaInicio Fecha de inicio de la consulta
	 * @param fechaFin Fecha de fin de la consulta
	 * @param horaInicio Hora de inicio de la consulta
	 * @param horaFin Hora de fin de la consulta
	 * @param pbx Objeto PBX que representa a la centralita sobre la que se ejecuta la consulta.
	 * @param pricing Objeto pricing que contiene las tarifas de la centralita
	 * @param source Opcional. Se especifica si se quiere generar un reporte de un número específico
	 * @param brief Indica si es un informe agrupado por catidad de llamadas (true) o completo
	 * @param srcFilter Opcional.Si se quire filtrar el reporte por una lista de extensiones 
	 * @return Matriz bidimensional de Strings que contiene una tabla con el resultado
	 * @throws SQLException
	 */
	public static String[][] reportGeneralPricing(String fechaInicio, String fechaFin,String horaInicio,String horaFin, Pbx pbx, Pricing pricing,String source,Boolean brief,String[] srcFilter)throws SQLException{
		List<String> sourceFilter=null;
		if (srcFilter!=null){
			sourceFilter = new ArrayList<String>(Arrays.asList(srcFilter));
		}
		fechaInicio=convertDate(fechaInicio) + " " + horaInicio +":00" ;
		fechaFin=convertDate(fechaFin) + " " + horaFin + ":59";
		String dst=pbx.getDst();
		String billable=pbx.getBillsec();
		String datetime=pbx.getDatetime();
		String src=pbx.getSrc();
		String calltype=pbx.getCalltype();
		String cdr=pbx.getCdrname();
		String query=null;
		if (source==null){
			query="SELECT "+ src + " as Origen,"+ dst + " as Destino," + billable + " as Duracion," + cdr + "." + datetime + " as Fecha FROM " + cdr +
			" where ("+ calltype + "='Outbound' and " + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin + "' and " + billable + ">0) order by Fecha";
			return result(pbx,query,Boolean.TRUE,pricing,sourceFilter,brief);
		}
		else{
			if (!brief){
				query="SELECT "+ src + " as Origen,"+ dst + " as Destino," + billable + " as Duracion," + cdr + "." + datetime + " as Fecha FROM " + cdr +
					" where ("+ calltype + "='Outbound' and " + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin + "' and " + billable + ">0 and " + src + " ='" + source +"') order by Fecha";
			}
			else{
				query="SELECT "+ src + " as Origen,"+ dst + " as Destino," + "sum("+ billable + ") as Duracion,count(*) as Llamadas," + cdr + "." + datetime + " as Fecha FROM " + cdr +
						" where ("+ calltype + "='Outbound' and " + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin + "' and " + billable + ">0 and " + src + "='" + source +"') group by Destino order by Llamadas desc";
			}
		}
		return result(pbx,query,Boolean.TRUE,pricing,null,brief);
	}
	/**
	 * Devuelve un listado de extensiones que tienen datos registrados en la centralita entre el rango de fechas especificado. 
	 * @param fechaInicio Fecha de inicio de la consulta
	 * @param fechaFin Fecha de fin de la consulta
	 * @param horaInicio Hora de inicio de la consulta
	 * @param horaFin Hora de fin de la consulta
	 * @param pbx Objeto PBX que representa a la centralita sobre la que se ejecuta la consulta.
	 * @return Una matriz de Strings que contiene las extensiones con datos en ese intervalo de fechas y horas
	 * @throws SQLException
	 */
	public static List<String> getExtList(Pbx pbx, String fechaInicio, String fechaFin, String horaInicio, String horaFin)throws SQLException{
		Connection con;
			con = connect(pbx);
			List<String> srcList = new ArrayList<String>();
			fechaInicio=convertDate(fechaInicio) + " " + horaInicio +":00" ;
			fechaFin=convertDate(fechaFin) + " " + horaFin + ":59";
			String src=pbx.getSrc();
			String datetime=pbx.getDatetime();
			String cdr=pbx.getCdrname();
			String query = "select distinct "+ src +" from cdr where (calltype='Outbound' and " + cdr + "." + datetime + " >= '" + fechaInicio + "' and " + cdr + "." + datetime + " <= '" + fechaFin + "') order by " + src;
		    log.info("Conectado a " + pbx.getIp());
		    log.info("Ejecutando: " + query);
		    PreparedStatement preparedStatement = con.prepareStatement(query);
		    ResultSet rs = preparedStatement.executeQuery();
		    while(rs.next()){
		    	srcList.add(rs.getString(1));
		    }
		    log.info("Ha finalizado la ejecución de la consulta para obtener la lista de extensiones con datos. Se devuelve el resultado al Servlet");
		    return srcList;
	}
}
