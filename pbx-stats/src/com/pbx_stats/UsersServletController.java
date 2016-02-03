package com.pbx_stats;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pbx_stats.beans.Pbx;
import com.pbx_stats.beans.User;
import com.pbx_stats.tools.DatabaseConnectionManager;
import com.pbx_stats.tools.Utils;

/**
 * Servlet implementation class UsuariosServletController
 */
public class UsersServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger("UsersServletController: ");
	private String rutaJSP;
	DatabaseConnectionManager localDatabase;
	Users userList;
	Pbxs pbxsList;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		rutaJSP = config.getInitParameter("rutaJSP");
		localDatabase = InitConfig.getLocalDatabase(getServletContext());
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UsersServletController() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (Utils.isLoggedIn(request) && Utils.isAdmin(request)) {
			try {
				userList = new Users(localDatabase);
				pbxsList = new Pbxs(localDatabase);
			} catch (Exception e) {
				log.error(
						"Se ha producido un error al inicializar los objetos que contienen la lista de usuarios y centralitas: "
								+ e.getMessage());
			}
			String action = request.getParameter("action");
			if (action != null) {
				if (action.equals("users")) {
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				} else if (action.equals("editUser")) {
					User user = userList.getUserById(Integer.parseInt(request.getParameter("iduser")));
					request.setAttribute("editIdEmail", user.getEmail());
					request.setAttribute("editName", user.getName());
					request.setAttribute("editAdminUser", user.isAdmin());
					request.setAttribute("iduser", user.getIduser());
					try {
						List<Pbx> pbxsByUserList = pbxsList.getPbxsByUser(localDatabase,
								Integer.parseInt(request.getParameter("iduser")), Boolean.FALSE);
						request.setAttribute("pbxsByUserList", pbxsByUserList);
						List<Pbx> reversePbxsByUserList = pbxsList.getPbxsByUser(localDatabase,
								Integer.parseInt(request.getParameter("iduser")), Boolean.TRUE);
						request.setAttribute("reversePbxsByUserList", reversePbxsByUserList);
						setControllerResponse("edituser").forward(request, response);
					} catch (Exception e) {
						request.setAttribute("error", "Error de acceso a la base de datos local: " + e.getMessage());
						setControllerResponse("error").forward(request, response);
						return;
					}
				} else if (action.equals("confirmEditUser")) {
					int iduser = Integer.parseInt(request.getParameter("iduser"));
					String name = request.getParameter("editedName");
					String email = request.getParameter("editedEmail");
					String password = request.getParameter("editedPassword");
					Boolean isAdmin = Boolean.FALSE;
					if (password.isEmpty()) {
						password = "none";
					}
					if (request.getParameter("editedAdminckbox") != null) {
						isAdmin = Boolean.TRUE;
					}
					try {
						userList.editUser(localDatabase, iduser, name, email, password, isAdmin);
						String[] pbxs = request.getParameterValues("pbxs[]");
						userList.editUserPbxs(localDatabase, iduser, pbxs);
						request.setAttribute("usersMessage",
								"Usuario con ID: " + Integer.parseInt(request.getParameter("iduser")) + " modificado");
					} catch (Exception e) {
						request.setAttribute("usersMessage", "Error al modificar usuario: " + e.getMessage());
					}
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);

				} else if (action.equals("cancelEditUser")) {
					request.setAttribute("usersMessage", "Se ha cancelado la edición del usuario");
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				} else if (action.equals("newuser")) {
					request.setAttribute("pbxsList", pbxsList.pbxsList);
					setControllerResponse("newuser").forward(request, response);
				} else if (action.equals("confirmNewUser")) {
					String name = request.getParameter("newName");
					String email = request.getParameter("newEmail");
					String password = request.getParameter("newPassword");
					int iduser;
					String isAdmin = request.getParameter("newAdminckbox");
					try {
						if (isAdmin != null) {
							iduser = userList.newUser(localDatabase, name, email, password, Boolean.TRUE);
						} else {
							iduser = userList.newUser(localDatabase, name, email, password, Boolean.FALSE);
						}
						if (iduser != -1) {
							String[] pbxs = request.getParameterValues("pbxs[]");
							userList.editUserPbxs(localDatabase, iduser, pbxs);
							request.setAttribute("usersMessage", "Usuario con ID: " + iduser + " creado");
						} else {
							request.getSession().setAttribute("usersMessage", "Error al crear usuario");
						}
					} catch (Exception e) {
						request.getSession().setAttribute("usersMessage", "Error al crear usuario: " + e.getMessage());
					}
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				} else if (action.equals("cancelNewUser")) {
					request.setAttribute("usersMessage", "Se ha cancelado la creación de un nuevo usuario");
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				} else if (action.equals("deleteUser")) {
					try {
						userList.deleteUser(localDatabase, Integer.parseInt(request.getParameter("iduser")));
						request.setAttribute("usersMessage", "Usuario con ID "
								+ Integer.parseInt(request.getParameter("iduser")) + " borrado correctamente");
					} catch (Exception e) {
						request.setAttribute("usersMessage", "Error al eliminar usuario: " + e.getMessage());
					}
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				}
			} else {
				setControllerResponse("main").forward(request, response);
			}
		} else {
			getServletContext().getRequestDispatcher("/main").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public RequestDispatcher setControllerResponse(String vista) {
		String url = rutaJSP + vista + ".jsp";
		return getServletContext().getRequestDispatcher(url);
	}
}
