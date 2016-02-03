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
* HttpServlet principal de la aplicación.
* Procesa la primera petición a la aplicación web y gestiona el login de usuario.
* Si se realiza una petición a otra parte de la aplicación sin que el usuario se haya logueado
* el resto de servlets la redirigirán a éste para que se procese el login.
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
	//Datasource para la conexión SQL
	DatabaseConnectionManager localDatabase;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServletController() {
        super();
    }
    /**
	* Método init
	* Consulta el valor de la ruta en la que se encuentra la carpeta con los archivos JSP.
	* Recupera los datos del datasource de la conexión mysql mediante JNDI
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
	 * Método doGet
	 * Procesa las peticiones HTTP GET.
	 * En función del valor del parámetro action se realizará una acción u otra.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//Si el usuario está logueado.
		if (Utils.isLoggedIn(request)) {
			//recuperación del valor del parámetro action.
			String action = request.getParameter("action");
			//Si se ha especificado el parámetro action en el request.
			if (action != null) {
				//Si la action es login, se redirige a la vista login.jsp.
				if (action.equals("login")) {
					//Se redirige a la vista login.jsp.
					setControllerResponse(action).forward(request, response);
				//Si la action es logout, se destruye la sesión y se redirige a la vista login.jsp.
				} else if (action.equals("logout")) {
					//Se destruye la sesión.
					request.getSession().invalidate();
					//Se redirige a la vista login.jsp.
					setControllerResponse("login").forward(request, response);
					log.info("Se ha cerrado la sesion del usuario");
					
				}
			//Si no se especifica el parámetro action se redirige a la vista a index.jsp. Menú principal de la aplicación.
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
	 * Método doPost
	 * Procesa las peticiones HTTP POST.
	 * En función del valor del parámetro action se realizará una acción u otra.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//Se recupera el parámetro action
		String action = request.getParameter("action");
		//Se almnacena la sesión de Tomcat
		HttpSession session = request.getSession();
		//Configuración de la codificación del request a UTF-8
		request.setCharacterEncoding("UTF-8");
		//Si el usuario esta logueado
		if (Utils.isLoggedIn(request)) {
			//Si el parámetro action tiene valor
			if (action != null) {
				//Si es index, se redirige a la vista index.jsp, pantalla principal de la aplicación
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
			//Si la accion es igual a doLogin, el usuario ha introducido email y contraseña, se procede a validarlo contra la BBDD
			if (action.equals("doLogin")) {
				//Se recupera el valor de los parámetros email y contraseña
				String email = request.getParameter("email");
				String password = request.getParameter("password");
				//Se consulta en la BBDD el usuario y su contraseña mediante el método checkLogging(conexion, email, password) estático de 
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
				//Si el método devuelve un id de usuario válido
				if (iduser != -1) {
					//Se consulta en la BBDD si el usuario es administrador el método checkIsAdmin(conexion, iduser) estático de 
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
					//Se almacena el id de usuario y si es administrador dentro de la sesión de Tomcat.
					session.setAttribute("iduser", iduser);
					session.setAttribute("userAdmin", userAdmin);
					log.info("Logueado correctamente como " + email);
					if (userAdmin) {
						log.info("El usuario " + email + " es administrador");
					}
					//Se almacena el nombre de usuario en una cookie si está activada la casilla.
					try {
						if (request.getParameter("ckbox").equals("on")) {
							// se crea una cookie con nombre usuario y valor
							// usuario
							Cookie cookie = new Cookie("usuario", email);
							// caducidad de un día
							cookie.setMaxAge(60 * 60 * 24);
							// se añade al objeto response
							response.addCookie(cookie);
							log.info("Cookie creada");
						}
					} catch (Exception e) {
						log.info("No está seleccionado recordar usuario");
					}
					// Se redirige a la página index.jsp
					setControllerResponse("index").forward(request, response);
				} 
				//Usuario y/o contraseña no son correctos 
				else {
					//Se escribe el error en el atributo error para mostrarlo en la vista
					request.setAttribute("error", "Usuario o contraseña incorrectos");
					// Se redirige a la página login.jsp
					setControllerResponse("login").forward(request, response);
				}
				
			}
			//Si action no es doLogin, se redirige a login
			else {
				// Se redirige a la página login.jsp
				setControllerResponse("login").forward(request, response);
			}
		}
	}
	/**
	 * Redirige a la vista que recibe por parámetro
	 * @param vista: la página JSP a redirigir
	 * @return
	 */
	public RequestDispatcher setControllerResponse(String vista) {
		String url = rutaJSP + vista + ".jsp";
		return getServletContext().getRequestDispatcher(url);
	}
}
