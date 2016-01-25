package com.pbx_stats.tools;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
/**
 * Clase Utils
 * Contiene diversos m�todos est�ticos que se usan en la aplicaci�n
 * @author Juan Granados
 *
 */
public class Utils {
	
	//Log para generar entradas en el log mediante log4j.
	private static final Logger log = LogManager.getLogger("Utils: ");
	public Utils(){
	}
	/**
	 * Comprueba si la combinaci�n de usuario y contrase�a es correcta
	 * @param con conexi�n con la base de datos mysql
	 * @param email email del usuario
	 * @param password contrase�a del usuario
	 * @return id del usuario, -1 si no se encuentra la combinaci�n de usuario y contrase�a
	 * @throws SQLException
	 */
	public static int checkLogging (Connection con,String email, String password) throws SQLException{
		String sql ="select iduser,password from users where email = ?";	
		try {
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, email);
			ResultSet rs = st.executeQuery();
			if(rs.next()){
				int id = rs.getInt(1);
				String hash=rs.getString(2);
				if (BCrypt.checkpw(password, hash)){
					rs.close();
					return id;
				}
			}
			rs.close();
			return -1;

		} catch (SQLException e) {
			log.error("Error al comprobar usuario y contrase�a en la BBDD: " + e.getMessage());
			throw e;
		}
	}
	/**
	 * Consulta si el usuario es administrador en la base de datos
	 * @param con conexi�n con la base de datos mysql
	 * @param iduser id del usuario
	 * @return true si es administrador. False si no es administrador.
	 * @throws SQLException
	 */
	public static boolean checkIsAdmin(Connection con, int iduser) throws SQLException{
		String sql = "select admin from users where iduser = ?";
		try {
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, iduser);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) == 0) {
					rs.close();
					return false;
				}
				rs.close();
				return true;
			}
		} catch (SQLException e) {
			log.error("Error al comprobar la pertenecia al grupo administradores en la BBDD: " + e.getMessage());
			throw e;
		}
		return false;
	}
	/**
	 * Consulta en la sesi�n de Tomcat el par�metro iduser, que se almacena si el usuario est� logueado
	 * @param request request del servlet
	 * @return True si existe la variable. False si no existe
	 */
	public static boolean isLoggedIn(HttpServletRequest request) {
		return request.getSession().getAttribute("iduser") != null;
	}
	/**
	 * Consulta en la sesi�n de Tomcat el par�metro userAdmin, que se almacena si el usuario es administrador
	 * @param request request del servlet
	 * @return
	 */
	public static boolean isAdmin(HttpServletRequest request) {
		return request.getSession().getAttribute("userAdmin") != null;
	}
	
}
