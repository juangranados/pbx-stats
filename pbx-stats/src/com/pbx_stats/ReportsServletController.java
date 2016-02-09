package com.pbx_stats;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pbx_stats.beans.Pbx;
import com.pbx_stats.beans.Pricing;
import com.pbx_stats.tools.DatabaseConnectionManager;
import com.pbx_stats.tools.Utils;

/**
 * Servlet implementation class reportsServletController
 * Actúa como controlador para los reportes de la aplicación.
 */
public class ReportsServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger("ReportsServletController: ");
	private String rutaJSP;
	private String results[][]=null;
	DatabaseConnectionManager localDatabase;
	Pbxs pbxsList;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsServletController() {
    }

	/**
	* Método init
	* Consulta el valor de la ruta en la que se encuentra la carpeta con los archivos JSP.
	* Recupera los datos del datasource de la conexión mysql mediante JNDI
	**/
	public void init(ServletConfig config) throws ServletException {
		//Llamada al constructor de la clase padre
		super.init(config);
		//Recuperar el valor del directorio que contiene los archivos JSP
		rutaJSP = config.getInitParameter("rutaJSP");
		localDatabase = InitConfig.getLocalDatabase(getServletContext());
	}

	/**
	 * Método doGet
	 * Procesa las peticiones HTTP GET.
	 * En función del valor del parámetro action se realizará una acción u otra.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Si el servlet no se ha iniciado correctamente, se redirige a la vista error.jsp
		try {
			pbxsList = new Pbxs(localDatabase);
		} catch (Exception e1) {
			log.error("Error de acceso a la base de datos local: " + e1.getMessage());
			request.setAttribute("error", "Error de acceso a la base de datos local: " + e1.getMessage());
			setControllerResponse("error").forward(request, response);
			return;
		}
		//Si el usuario esta logueado
		if (Utils.isLoggedIn(request)) {
			String action = request.getParameter("action");
			//Si se ha especificado un valor para action
			if (action != null) {
				//Si se quiere generar un PDF a partir de un resultado
				if (action.equals("generatePDF")){
					//PDFGenerator.createReportPDF(results, request.getParameter("reporte"), null);
					//return;
					request.setAttribute("results", results);
					request.setAttribute("reporte",request.getParameter("reporte"));
					getServletContext().getRequestDispatcher("/PdfServletController").forward(request, response);
				}
				results=null;
				//Se recupera la sesión de Tomcat para consultar id de usuario y si éste es administrador
				HttpSession session = request.getSession(true);
				//Se recupera el id del usuario
				int iduser = Integer.parseInt(session.getAttribute("iduser").toString());
				//Se genera una lista con las centralitas a las que tiene acceso el usuario
				List<Pbx> PbxsByUserList;
				try {
					PbxsByUserList = pbxsList.getPbxsByUser(localDatabase,iduser,Boolean.FALSE);
				} catch (NamingException | SQLException e2) {
					request.setAttribute("error", "Error de acceso a la base de datos local. No se ha podido recuperar la lista de las centralitas a las que el usuario tiene acceso: " + e2.getMessage());
					setControllerResponse("error").forward(request, response);
				    return;
				}
				if (PbxsByUserList==null){
					request.setAttribute("error", "Error de acceso a la base de datos local. No se ha podido recuperar la lista de las centralitas a las que el usuario tiene acceso: ");
					setControllerResponse("error").forward(request, response);
				    return;
				}
				//Se incluye la lista anterior como parámetro del request
				request.setAttribute("PbxsByUserList", PbxsByUserList);
				//Se recupera el valor del parámetro storeConditions, que indica si se deben almacenar los parámetros del 
				//request para volver a enviarlos a la próxima vista.
				String storeConditions=request.getParameter("storeConditions");
				//Variables para almacenar los parámetros de la consulta
				String fechaInicio = null;
				String fechaFin = null;
				String horaInicio = null;
				String horaFin = null;
				Integer idPbx = null;
				String ext=null;
				String period=null; 
				//Se almacenan los parámetros recibidos en el request para usarlos en la siguiente vista
				if (storeConditions.equals("true")){
					fechaInicio = request.getParameter("fechaInicio");
					fechaFin = request.getParameter("fechaFin");
					horaInicio = request.getParameter("horaInicio");
					horaFin = request.getParameter("horaFin");
					idPbx = Integer.parseInt(request.getParameter("idPbx"));
					request.setAttribute("fechaInicio",fechaInicio);
					request.setAttribute("fechaFin",fechaFin);
					request.setAttribute("horaInicio",horaInicio);
					request.setAttribute("horaFin",horaFin);
					request.setAttribute("idPbx",idPbx);
					period=" entre " + fechaInicio + " " + horaInicio + " y " + fechaFin + " " + horaFin; 
				}
				//Si la accion contiene detalle o resumen, será necesario conocer la extensión, ya que el reporte es específico
				//para una extensión
				if (action.contains("Detail")||action.contains("Brief")){
					ext=request.getParameter("ext");
				}
				//Se recibe la respuesta del formulario selectperiod.jsp con el rango de fecha, hora y si se quiere 
				//especificar extensiones
				if (action.equals("selectPeriodResponse")){
					//Si se ha marcado la casilla de seleccionar extensiones
					if (request.getParameter("selectExt")!=null){
						//Se recupera una lista de extensiones con datos dentro de ese periodo (fecha y hora)
						List<String> srcList;
						try {
							srcList = Reports.getExtList(pbxsList.getPbxById(idPbx),fechaInicio,fechaFin,horaInicio,horaFin);
						} catch (Exception e) {
						    log.error("Error en la BBDD interna: " + e.getMessage());
						    request.setAttribute("error", "Error de acceso a la base de datos local: " + e.getMessage());
							setControllerResponse("error").forward(request, response);
						    return;
						}
						//Se almacena la lista de extensiones como parámetro en el request
						request.setAttribute("srcList",srcList);
						//Se almacena el tipo de reporte que se ha recibido de nuevo en el request
						request.setAttribute("reportType", request.getParameter("reportType"));
						//Se redirige a la la vista selectextlist.jsp para que el usuario elija las extensiones a las que le afecta el reporte
						setControllerResponse("selectextlist").forward(request, response);
					}
					//No se ha marcado la casilla de seleccionar extensiones
					else{
						//Se almacena el tipo de reporte que se ha recibido en el request
						String reportType=request.getParameter("reportType");
						//Se indica que en el siguiente doGet se van a almacenan los parámetros recibidos en el request 
						//para usarlos en la siguiente vista
						request.setAttribute("storeConditions",request.getParameter("storeConditions"));
						//Se redirige la petición de nuevo al servlet para que genere el reporte adecuado
						getServletContext().getRequestDispatcher("/reports?action=" + reportType).forward(request, response);
					}
				}
				//El usuario elige un reporte de llamadas realizadas general por extension. Se redirige a la vista selectperiod.jsp
				//para que se seleccione un periodo y se elija si se filtran extensiones
				//Se indica a la vista que el reporte puede filtrarse por extensiones mediante el parámetro filter
				else if (action.equals("extGeneralMade")){
					request.setAttribute("reportType", "extGeneralMade");
					request.setAttribute("reportName", "llamadas realizadas");
					request.setAttribute("filter", "filter");
					setControllerResponse("selectperiod").forward(request, response);
				}
				//El usuario elige un reporte de llamadas recibidas. Se redirige a la vista selectperiod.jsp
				//para que se seleccione un periodo
				else if (action.equals("extGeneralReceived")){
					request.setAttribute("reportType", "extGeneralReceived");
					request.setAttribute("reportName", "llamadas recibidas");
					setControllerResponse("selectperiod").forward(request, response);
				}
				//El usuario elige un reporte de llamadas recibidas no contestadas. Se redirige a la vista selectperiod.jsp
				//para que se seleccione un periodo
				else if (action.equals("extGeneralUnansweredReceived")){
					request.setAttribute("reportType", "extGeneralUnansweredReceived");
					request.setAttribute("reportName", "llamadas entrantes no contestadas");
					setControllerResponse("selectperiod").forward(request, response);
				}
				//El usuario elige un reporte de llamadas realizadas no contestadas. Se redirige a la vista selectperiod.jsp
				//para que se seleccione un periodo y se elija si se filtran extensiones
				//Se indica a la vista que el reporte puede filtrarse por extensiones mediante el parámetro filter
				else if (action.equals("extGeneralUnansweredMade")){
					request.setAttribute("reportType", "extGeneralUnansweredMade");
					request.setAttribute("reportName", "llamadas salientes no contestadas");
					request.setAttribute("filter", "filter");
					setControllerResponse("selectperiod").forward(request, response);
				}
				//El usuario elige un reporte de tarificación de llamadas realizadas. Se redirige a la vista selectperiod.jsp
				//para que se seleccione un periodo y se elija si se filtran extensiones
				//Se indica a la vista que el reporte puede filtrarse por extensiones mediante el parámetro filter
				else if (action.equals("extGeneralPricing")){
					request.setAttribute("reportType", "extGeneralPricing");
					request.setAttribute("reportName", "tarificación general");
					request.setAttribute("filter", "filter");
					setControllerResponse("selectperiod").forward(request, response);
				}
				
				//Una vez que se ha seleccionado el periodo, se genera el resultado y se imprime en la vista correspondiente
				else if (action.equals("extGeneralPricingResult")){
					
					try {
						Pricing pricing=new Pricing(localDatabase,idPbx);
						results = Reports.reportGeneralPricing(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),pricing,null,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Facturación general" + period);
					request.setAttribute("reportType", "extPricing");
					setControllerResponse("resultextgeneral").forward(request, response);
				}
				else if (action.equals("extGeneralPricingResultFiltered")){
					String[] srcList = request.getParameterValues("sourceList");
					
					try {
						Pricing pricing=new Pricing(localDatabase,idPbx);
						results = Reports.reportGeneralPricing(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),pricing,null,Boolean.FALSE,srcList);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Facturación general" + period);
					request.setAttribute("reportType", "extPricing");
					setControllerResponse("resultextgeneral").forward(request, response);
				}
				else if (action.equals("extPricingDetail")){
					
					try {
						Pricing pricing=new Pricing(localDatabase,idPbx);
						results = Reports.reportGeneralPricing(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),pricing,ext,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Facturación de las llamadas realizadas por " + ext + period);
					setControllerResponse("resultext").forward(request, response);
					}
				else if (action.equals("extPricingBrief")){
					
					
					try {
						Pricing pricing=new Pricing(localDatabase,idPbx);
						results = Reports.reportGeneralPricing(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),pricing,ext,Boolean.TRUE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Resumen de facturación de las llamadas realizadas por " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
				else if (action.equals("extGeneralMadeResult")){
					
					try {
						results = Reports.reportExtGeneral(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Outbound",null,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportType", "extMade");
					request.setAttribute("reportName", "Llamadas realizadas entre" + period);
					setControllerResponse("resultextgeneral").forward(request, response);
				}
				else if (action.equals("extGeneralMadeResultFiltered")){
					String[] srcList = request.getParameterValues("sourceList");
					
					try {
						results = Reports.reportExtGeneral(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Outbound",null,Boolean.FALSE,srcList);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportType", "extMade");
					request.setAttribute("reportName", "Llamadas realizadas entre" + period);
					setControllerResponse("resultextgeneral").forward(request, response);
				}
				else if (action.equals("extMadeDetail")){
					
					try {
						results = Reports.reportExtGeneral(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Outbound",ext,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas realizadas por " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
				else if (action.equals("extMadeBrief")){
					
					try {
						results = Reports.reportExtGeneral(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Outbound",ext,Boolean.TRUE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas realizadas por " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
				else if (action.equals("extGeneralReceivedResult")){				
					
					try {
						results = Reports.reportExtGeneral(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Inbound",null,Boolean.FALSE,null);
					} catch (Exception e) {
						log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas recibidas entre" + period);
					request.setAttribute("reportType", "extReceived");
					setControllerResponse("resultextgeneral").forward(request, response);
				}
				else if (action.equals("extReceivedDetail")){
					
					try {
						results = Reports.reportExtGeneral(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Inbound",ext,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas recibidas de " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
				else if (action.equals("extReceivedBrief")){
					
					try {
						results = Reports.reportExtGeneral(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Inbound",ext,Boolean.TRUE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas recibidas de " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
				else if (action.equals("extGeneralUnansweredReceivedResult")){
					
					try {
						results = Reports.reportExtUnanswered(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Inbound","NO ANSWER",null,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas recibidas no contestadas entre" + period);
					request.setAttribute("reportType", "extUnansweredReceived");
					setControllerResponse("resultextgeneral").forward(request, response);
				}
				else if (action.equals("extUnansweredReceivedDetail")){
					
					try {
						results = Reports.reportExtUnanswered(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Inbound","NO ANSWER",ext,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas no contestadas recibidas desde " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
				else if (action.equals("extUnansweredReceivedBrief")){
					
					try {
						results = Reports.reportExtUnanswered(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Inbound","NO ANSWER",ext,Boolean.TRUE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas no contestadas recibidas desde " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
				else if (action.equals("extGeneralUnansweredMadeResult")){
					
					try {
						results = Reports.reportExtUnanswered(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Outbound","NO ANSWER",null,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas realizadas no contestadas" + period);
					request.setAttribute("reportType", "extUnansweredMade");
					setControllerResponse("resultextgeneral").forward(request, response);
				}
				else if (action.equals("extGeneralUnansweredMadeResultFiltered")){
					String[] srcList = request.getParameterValues("sourceList");
					
					try {
						results = Reports.reportExtUnanswered(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Outbound","NO ANSWER",null,Boolean.FALSE,srcList);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas realizadas no contestadas" + period);
					request.setAttribute("reportType", "extUnansweredMade");
					setControllerResponse("resultextgeneral").forward(request, response);
				}
				else if (action.equals("extUnansweredMadeDetail")){
					
					try {
						results = Reports.reportExtUnanswered(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Outbound","NO ANSWER",ext,Boolean.FALSE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas no contestadas realizadas desde " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
				else if (action.equals("extUnansweredMadeBrief")){
					
					try {
						results = Reports.reportExtUnanswered(fechaInicio,fechaFin,horaInicio,horaFin,pbxsList.getPbxById(idPbx),"Outbound","NO ANSWER",ext,Boolean.TRUE,null);
					} catch (Exception e) {
					    log.error("Error en la BBDD de la centralita: " + e.getMessage());
					    request.setAttribute("error", "Error de acceso a la base de datos de la centralita: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
					    return;
					}
					request.setAttribute("results", results);
					request.setAttribute("reportName", "Llamadas no contestadas realizadas desde " + ext + period);
					setControllerResponse("resultext").forward(request, response);
				}
			}
		}
		//Si el usuario no esta logueado se redirige a la vista principal para que ésta redirija al login
		else{
			getServletContext().getRequestDispatcher("/main").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	public RequestDispatcher setControllerResponse(String vista) {
		String url = rutaJSP + vista + ".jsp";
		return getServletContext().getRequestDispatcher(url);
	}
}
