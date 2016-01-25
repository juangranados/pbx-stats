package com.pbx_stats;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pbx_stats.beans.Pbx;
import com.pbx_stats.beans.User;
import com.pbx_stats.tools.Utils;

/**
 * Servlet implementation class UsuariosServletController
 */
public class UsersServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger("Servlet: ");
	private String rutaJSP;
	private DataSource ds;
	private Connection con;
	Users userList;
	Pbxs pbxsList;
	
    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		rutaJSP = config.getInitParameter("rutaJSP");
		try {
			InitialContext initContext = new InitialContext();
			Context env = (Context) initContext.lookup("java:comp/env");
			ds = (DataSource) env.lookup("jdbc/pbx-stats");
		} catch (NamingException e) {
			log.error("Error al configurar JNDI: " + e.getMessage());
		}
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			log.error("Error creando la conexión: " + e.getMessage());
		}
		userList = new Users(con);
		try {
			pbxsList = new Pbxs(con);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UsersServletController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (Utils.isLoggedIn(request) && Utils.isAdmin(request)) {
			String action = request.getParameter("action");
			if (action != null) {
				if (action.equals("users")) {	
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				} 
				else if (action.equals("editUser")) {
					User user = userList.getUserById(con, Integer.parseInt(request.getParameter("iduser")));
					request.setAttribute("editIdEmail",user.getEmail());
					request.setAttribute("editName", user.getName());
					request.setAttribute("editAdminUser", user.isAdmin());
					request.setAttribute("iduser",user.getIduser());
					List<Pbx> pbxsByUserList = pbxsList.getPbxsByUser(con,Integer.parseInt(request.getParameter("iduser")),Boolean.FALSE);
					request.setAttribute("pbxsByUserList", pbxsByUserList);
					List<Pbx> reversePbxsByUserList = pbxsList.getPbxsByUser(con,Integer.parseInt(request.getParameter("iduser")),Boolean.TRUE);
					request.setAttribute("reversePbxsByUserList", reversePbxsByUserList);
					setControllerResponse("edituser").forward(request, response);
				}
				else if (action.equals("confirmEditUser")){
					int iduser = Integer.parseInt(request.getParameter("iduser"));
					String name = request.getParameter("editedName");
					String email = request.getParameter("editedEmail");
					String password = request.getParameter("editedPassword");
					Boolean isAdmin = Boolean.FALSE;
					if (password.isEmpty()){
						password="none";
					}
					if (request.getParameter("editedAdminckbox")!=null){
						isAdmin = Boolean.TRUE;
					}
					if (userList.editUser(con,iduser,name,email,password,isAdmin)){
						String[] pbxs = request.getParameterValues("pbxs[]");
						if (userList.editUserPbxs(con,iduser,pbxs)){
							request.setAttribute("usersMessage","Usuario con ID: " + Integer.parseInt(request.getParameter("iduser")) + " modificado");
						}
						else{
							request.setAttribute("usersMessage","Error al modificar usuario");
						}
					}
					else{
						request.setAttribute("usersMessage","Error al modificar usuario");
					}
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				}
				else if (action.equals("cancelEditUser")){
					request.setAttribute("usersMessage","Se ha cancelado la edición del usuario");
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				}
				else if (action.equals("newuser")){
					request.setAttribute("pbxsList", pbxsList.pbxsList);
					setControllerResponse("newuser").forward(request, response);
				}
				else if (action.equals("confirmNewUser")){
					String name = request.getParameter("newName");
					String email = request.getParameter("newEmail");
					String password = request.getParameter("newPassword");
					int iduser;
					String isAdmin=request.getParameter("newAdminckbox");
					if (isAdmin!=null){
						iduser=userList.newUser(con,name,email,password,Boolean.TRUE);
					}
					else{
						iduser=userList.newUser(con,name,email,password,Boolean.FALSE);
					}
					if (iduser!=-1){
						String[] pbxs = request.getParameterValues("pbxs[]");
						if (userList.editUserPbxs(con,iduser,pbxs)){
							request.setAttribute("usersMessage","Usuario con ID: " + iduser + " creado");
						}
						else{
							request.getSession().setAttribute("usersMessage","Error al crear usuario");
						}
					}
					else{
						request.getSession().setAttribute("usersMessage","Error al crear usuario");
					}
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				}
				else if (action.equals("cancelNewUser")){
					request.setAttribute("usersMessage","Se ha cancelado la creación de un nuevo usuario");
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				}
				else if (action.equals("deleteUser")){
					
					if (userList.deleteUser(con,Integer.parseInt(request.getParameter("iduser")))){
						request.setAttribute("usersMessage","Usuario con ID " + Integer.parseInt(request.getParameter("iduser")) + " borrado correctamente");
					}
					else{
						request.setAttribute("usersMessage","Error al eliminar usuario");
					}
					request.setAttribute("userList", userList.userList);
					setControllerResponse("users").forward(request, response);
				}
			} else {
				setControllerResponse("main").forward(request, response);
			}
		}
		else{
			getServletContext().getRequestDispatcher("/main").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	public RequestDispatcher setControllerResponse(String vista) {
		String url = rutaJSP + vista + ".jsp";
		return getServletContext().getRequestDispatcher(url);
	}
}
