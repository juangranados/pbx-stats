package com.pbx_stats;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pbx_stats.tools.DatabaseConnectionManager;
import com.pbx_stats.tools.Utils;

/**
* HttpServlet principal de la aplicaci�n.
* Procesa la primera petici�n a la aplicaci�n web y gestiona el login de usuario.
* Si se realiza una petici�n a otra parte de la aplicaci�n sin que el usuario se haya logueado
* el resto de servlets la redirigir�n a �ste para que se procese el login.
* @author      Juan Granados.
* @version     %I%, %G%
* @since       1.0
**/


public class MainServletController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	//Log para generar entradas en el log mediante log4j.
	private static final Logger log = LogManager.getLogger("MainServletController: ");
	//String para almacenar la ruta de la carpeta donde se encuentran los JSP.
	private String rutaJSP;
	//Datasource para la conexi�n SQL
	DatabaseConnectionManager localDatabase;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServletController() {
        super();
    }
    /**
	* M�todo init
	* Consulta el valor de la ruta en la que se encuentra la carpeta con los archivos JSP.
	* Recupera los datos del datasource de la conexi�n mysql mediante JNDI
	**/
	@Override
	public void init(ServletConfig config) throws ServletException {
		//Llamada al constructor de la clase padre
		super.init(config);
		//Recuperar el valor del directorio que contiene los archivos JSP
		rutaJSP = config.getInitParameter("rutaJSP");
		localDatabase = InitConfig.getLocalDatabase(getServletContext());
	}

	/**
	 * M�todo doGet
	 * Procesa las peticiones HTTP GET.
	 * En funci�n del valor del par�metro action se realizar� una acci�n u otra.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//Si el usuario est� logueado.
		if (Utils.isLoggedIn(request)) {
			//recuperaci�n del valor del par�metro action.
			String action = request.getParameter("action");
			//Si se ha especificado el par�metro action en el request.
			if (action != null) {
				//Si la action es login, se redirige a la vista login.jsp.
				if (action.equals("login")) {
					//Se redirige a la vista login.jsp.
					setControllerResponse(action).forward(request, response);
				//Si la action es logout, se destruye la sesi�n y se redirige a la vista login.jsp.
				} else if (action.equals("logout")) {
					//Se destruye la sesi�n.
					request.getSession().invalidate();
					//Se redirige a la vista login.jsp.
					setControllerResponse("login").forward(request, response);
					log.info("Se ha cerrado la sesion del usuario");
					
				}
			//Si no se especifica el par�metro action se redirige a la vista a index.jsp. Men� principal de la aplicaci�n.
			} else
				//Se redirige a la vista a index.jsp.
				setControllerResponse("index").forward(request, response);
		//Si el usuario no se ha logueado, se redirige a la vista login.jsp.
		} else {
			//Se redirige a la vista login.jsp.
			setControllerResponse("login").forward(request, response);
		}
	}

	/**
	 * M�todo doPost
	 * Procesa las peticiones HTTP POST.
	 * En funci�n del valor del par�metro action se realizar� una acci�n u otra.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//Se recupera el par�metro action
		String action = request.getParameter("action");
		//Se almnacena la sesi�n de Tomcat
		HttpSession session = request.getSession();
		//Configuraci�n de la codificaci�n del request a UTF-8
		request.setCharacterEncoding("UTF-8");
		//Si el usuario esta logueado
		if (Utils.isLoggedIn(request)) {
			//Si el par�metro action tiene valor
			if (action != null) {
				//Si es index, se redirige a la vista index.jsp, pantalla principal de la aplicaci�n
				if (action.equals("index")) {
					setControllerResponse("index").forward(request, response);
				//En caso contrario, se regirige a la vista login.jsp
				} else {
					setControllerResponse("login").forward(request, response);
				}
			}
		} 
		//Si el usuario no esta logueado
		else {
			//Si la accion es igual a doLogin, el usuario ha introducido email y contrase�a, se procede a validarlo contra la BBDD
			if (action.equals("doLogin")) {
				//Se recupera el valor de los par�metros email y contrase�a
				String email = request.getParameter("email");
				String password = request.getParameter("password");
				//Se consulta en la BBDD el usuario y su contrase�a mediante el m�todo checkLogging(conexion, email, password) est�tico de 
				//la clase Utils. Si se produce un error, se guarda el mensaje para mostrarlo en la pantalla de login y se redirige a login.jsp
				int iduser=-1;
				try {
					iduser = Utils.checkLogging(localDatabase, email, password);
				} catch (Exception e) {
					log.error("Error de acceso a la base de datos de usuarios: " + e.getMessage());
					request.setAttribute("error", "Error de acceso a la base de datos de usuarios: " + e.getMessage());
					setControllerResponse("login").forward(request, response);
					return;
				}
				//Si el m�todo devuelve un id de usuario v�lido
				if (iduser != -1) {
					//Se consulta en la BBDD si el usuario es administrador el m�todo checkIsAdmin(conexion, iduser) est�tico de 
					//la clase Utils. Si se produce un error, se guarda el mensaje para mostrarlo en la pantalla de login y se redirige a login.jsp
					Boolean userAdmin=false;
					try {
						userAdmin = Utils.checkIsAdmin(localDatabase, iduser);
					} catch (Exception e1) {
						log.error("Error de acceso a la base de datos de usuarios: " + e1.getMessage());
						request.setAttribute("error", "Error de acceso a la base de datos de usuarios: " + e1.getMessage());
						setControllerResponse("login").forward(request, response);
						return;
					}
					//Se almacena el id de usuario y si es administrador dentro de la sesi�n de Tomcat.
					session.setAttribute("iduser", iduser);
					session.setAttribute("userAdmin", userAdmin);
					log.info("Logueado correctamente como " + email);
					if (userAdmin) {
						log.info("El usuario " + email + " es administrador");
					}
					//Se almacena el nombre de usuario en una cookie si est� activada la casilla.
					try {
						if (request.getParameter("ckbox").equals("on")) {
							// se crea una cookie con nombre usuario y valor
							// usuario
							Cookie cookie = new Cookie("usuario", email);
							// caducidad de un d�a
							cookie.setMaxAge(60 * 60 * 24);
							// se a�ade al objeto response
							response.addCookie(cookie);
							log.info("Cookie creada");
						}
					} catch (Exception e) {
						log.info("No est� seleccionado recordar usuario");
					}
					// Se redirige a la p�gina index.jsp
					setControllerResponse("index").forward(request, response);
				} 
				//Usuario y/o contrase�a no son correctos 
				else {
					//Se escribe el error en el atributo error para mostrarlo en la vista
					request.setAttribute("error", "Usuario o contrase�a incorrectos");
					// Se redirige a la p�gina login.jsp
					setControllerResponse("login").forward(request, response);
				}
				
			}
			//Si action no es doLogin, se redirige a login
			else {
				// Se redirige a la p�gina login.jsp
				setControllerResponse("login").forward(request, response);
			}
		}
	}
	/**
	 * Redirige a la vista que recibe por par�metro
	 * @param vista: la p�gina JSP a redirigir
	 * @return
	 */
	public RequestDispatcher setControllerResponse(String vista) {
		String url = rutaJSP + vista + ".jsp";
		return getServletContext().getRequestDispatcher(url);
	}
}
