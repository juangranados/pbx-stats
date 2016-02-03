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
import com.pbx_stats.beans.Pricing;
import com.pbx_stats.beans.User;
import com.pbx_stats.tools.DatabaseConnectionManager;
import com.pbx_stats.tools.Utils;

/**
 * Servlet implementation class pbxsServletController
 */
public class PbxsServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger("PbxsServletController: ");
	private String rutaJSP;
	DatabaseConnectionManager localDatabase;
	Users userList;
	Pbxs pbxsList;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PbxsServletController() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		rutaJSP = config.getInitParameter("rutaJSP");
		localDatabase = InitConfig.getLocalDatabase(getServletContext());
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
				if (action.equals("pbxs")) {
					request.setAttribute("pbxsList", pbxsList.pbxsList);
					setControllerResponse("pbxs").forward(request, response);
				} else if (action.equals("editPbx")) {
					Pbx pbx = pbxsList.getPbxById(Integer.parseInt(request.getParameter("idPbx")));
					request.setAttribute("editPbx", pbx);
					try {
						List<User> usersByPbxList = userList.getUsersByPbx(localDatabase,
								Integer.parseInt(request.getParameter("idPbx")), Boolean.FALSE);
						request.setAttribute("usersByPbxList", usersByPbxList);
						List<User> reverseUsersByPbxList = userList.getUsersByPbx(localDatabase,
								Integer.parseInt(request.getParameter("idPbx")), Boolean.TRUE);
						request.setAttribute("reverseUsersByPbxList", reverseUsersByPbxList);
						setControllerResponse("editpbx").forward(request, response);
					} catch (Exception ex) {
						request.setAttribute("error", "Error de acceso a la base de datos local: " + ex.getMessage());
						setControllerResponse("error").forward(request, response);
						return;
					}
				} else if (action.equals("confirmEditPbx")) {
					int idPbx = Integer.parseInt(request.getParameter("idPbx"));
					String name = request.getParameter("editedName");
					String ip = request.getParameter("editedIp");
					int port = Integer.parseInt(request.getParameter("editedPort"));
					String username = request.getParameter("editedUsername");
					String password = request.getParameter("editedPassword");
					String database = request.getParameter("editedDb");
					String cdrname = request.getParameter("editedCdrname");
					String datetime = request.getParameter("editedDatetime");
					String src = request.getParameter("editedSrc");
					String dst = request.getParameter("editedDst");
					String duration = request.getParameter("editedDuration");
					String billsec = request.getParameter("editedBillsec");
					String disposition = request.getParameter("editedDisposition");
					String calltype = request.getParameter("editedCalltype");
					if (password.isEmpty()) {
						password = "none";
					}
					try {
						pbxsList.editPbx(localDatabase, idPbx, name, ip, port, username, password, database, cdrname,
								datetime, src, dst, duration, billsec, disposition, calltype);
						String[] users = request.getParameterValues("users[]");

						pbxsList.editPbxUsers(localDatabase, idPbx, users);
						request.setAttribute("pbxsMessage", "Centralita con ID: "
								+ Integer.parseInt(request.getParameter("idPbx")) + " modificada");
					} catch (Exception ex) {
						request.setAttribute("pbxsMessage", "Error al modificar centralita: " + ex.getMessage());
					}
					request.setAttribute("pbxsList", pbxsList.pbxsList);
					setControllerResponse("pbxs").forward(request, response);
				} else if (action.equals("cancelEditPbx")) {
					request.setAttribute("pbxsMessage", "Se ha cancelado la edición de la centralita");
					request.setAttribute("pbxsList", pbxsList.pbxsList);
					setControllerResponse("pbxs").forward(request, response);
				} else if (action.equals("newPbx")) {
					request.setAttribute("userList", userList.userList);
					setControllerResponse("newpbx").forward(request, response);
				} else if (action.equals("confirmNewPbx")) {
					String name = request.getParameter("newName");
					String ip = request.getParameter("newIp");
					int port = Integer.parseInt(request.getParameter("newPort"));
					String username = request.getParameter("newUsername");
					String password = request.getParameter("newPassword");
					String db = request.getParameter("newDb");
					String cdrname = request.getParameter("newCdrname");
					String datetime = request.getParameter("newDatetime");
					String src = request.getParameter("newSrc");
					String dst = request.getParameter("newDst");
					String duration = request.getParameter("newDuration");
					String billsec = request.getParameter("newBillsec");
					String disposition = request.getParameter("newDisposition");
					String calltype = request.getParameter("newCalltype");
					try {
						int idPbx = pbxsList.newPbx(localDatabase, name, ip, port, username, password, db, cdrname,
								datetime, src, dst, duration, billsec, disposition, calltype);
						if (idPbx != -1) {
							String[] users = request.getParameterValues("users[]");
							if (pbxsList.editPbxUsers(localDatabase, idPbx, users)) {
								request.setAttribute("pbxsMessage", "Centralita con ID: " + idPbx + " creada");
							} else {
								request.getSession().setAttribute("pbxsMessage", "Error al crear centralita");
							}
						} else {
							request.getSession().setAttribute("pbxsMessage", "Error al crear centralita");
						}
					} catch (Exception ex) {
						request.setAttribute("error", "Error de acceso a la base de datos local: " + ex.getMessage());
						setControllerResponse("error").forward(request, response);
						return;
					}
					request.setAttribute("pbxsList", pbxsList.pbxsList);
					setControllerResponse("pbxs").forward(request, response);
				} else if (action.equals("cancelNewUser")) {
					request.setAttribute("pbxsMessage", "Se ha cancelado la creación de una nueva centralita");
					request.setAttribute("pbxsList", pbxsList.pbxsList);
					setControllerResponse("pbxs").forward(request, response);
				} else if (action.equals("deletePbx")) {
					try {
						pbxsList.deletePbx(localDatabase, Integer.parseInt(request.getParameter("idPbx")));
						request.setAttribute("pbxsMessage", "Centralita con ID "
								+ Integer.parseInt(request.getParameter("idPbx")) + " borrada correctamente");
						request.setAttribute("pbxsList", pbxsList.pbxsList);
					} catch (Exception e) {
						request.setAttribute("pbxsMessage", "Error al eliminar centralita: " + e.getMessage());
					}
					setControllerResponse("pbxs").forward(request, response);
				} else if (action.equals("editPricing")) {
					try {
						Pricing pricing = new Pricing(localDatabase, Integer.parseInt(request.getParameter("idPbx")));
						request.setAttribute("pricing", pricing);
						setControllerResponse("editpricing").forward(request, response);
					} catch (Exception ex) {
						request.setAttribute("error", "Error de acceso a la base de datos local: " + ex.getMessage());
						setControllerResponse("error").forward(request, response);
						return;
					}
				} else if (action.equals("confirmEditPricing")) {
					try {
						if (Pricing.updatePricing(localDatabase, Integer.parseInt(request.getParameter("idPbx")),
								Integer.parseInt(request.getParameter("fijo")),
								Integer.parseInt(request.getParameter("movil")),
								Integer.parseInt(request.getParameter("adicional")),
								Integer.parseInt(request.getParameter("compartido")),
								Integer.parseInt(request.getParameter("internacional")),
								Integer.parseInt(request.getParameter("efijo")),
								Integer.parseInt(request.getParameter("emovil")),
								Integer.parseInt(request.getParameter("eadicional")),
								Integer.parseInt(request.getParameter("ecompartido")),
								Integer.parseInt(request.getParameter("einternacional")),
								Integer.parseInt(request.getParameter("desconocido")),
								Integer.parseInt(request.getParameter("edesconocido")))) {
							request.setAttribute("pbxsMessage",
									"Se ha modificado la facturación para la centralita con ID "
											+ Integer.parseInt(request.getParameter("idPbx")) + " correctamente");
						} else {
							request.setAttribute("pbxsMessage", "Error al modificar la facturación de la centralita");
						}
						request.setAttribute("pbxsList", pbxsList.pbxsList);
						setControllerResponse("pbxs").forward(request, response);
					} catch (Exception ex) {
						request.setAttribute("error", "Error de acceso a la base de datos local: " + ex.getMessage());
						setControllerResponse("error").forward(request, response);
						return;
					}
				} else if (action.equals("cancelEditPricing")) {
					request.setAttribute("pbxsMessage",
							"Se ha cancelado la modificación de la facturación de la centralita");
					request.setAttribute("pbxsList", pbxsList.pbxsList);
					setControllerResponse("pbxs").forward(request, response);
				}
			} else {
				getServletContext().getRequestDispatcher("/main").forward(request, response);
			}
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
