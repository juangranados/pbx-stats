package com.pbx_stats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import com.pbx_stats.beans.User;
import com.pbx_stats.tools.BCrypt;
import com.pbx_stats.tools.DatabaseConnectionManager;

public class Users {
	public List<User> userList = new ArrayList<User>();
	private static final Logger log = LogManager.getLogger("Users: ");
	/**
	 * Constructor de la clase. Crea la lista de objetos User con los valores almacenados en la base de datos local e inicializa el log
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public Users(DatabaseConnectionManager localDatabase) throws SQLException,NamingException {
		Connection con=null;
		ResultSet rs = null;
		try {
			con=localDatabase.getConnection();
			rs = con.prepareStatement("select iduser,name,email,password,admin from users").executeQuery();
			while (rs.next()) {
				userList.add(
						new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5)));
			}
			DatabaseConnectionManager.closeQuietly(con, rs, null);
		} catch (SQLException | NamingException e) {
			log.error("Error al recuperar usuarios de la BBDD: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, rs, null);
			throw e;
		}
	}
	/**
	 * Devuelve una lista de usuarios que tienen acceso a la Pbx especificada
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param idpbx Id de la Pbx a consultar
	 * @param reverse Si es true se devuelve la lista de usuarios que NO tienen acceso a la Pbx especificada
	 * @return Lista de usuarios que tienen acceso a la Pbx especificada
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public List<User> getUsersByPbx(DatabaseConnectionManager localDatabase, int idpbx, Boolean reverse) throws SQLException,NamingException{
		Connection con=null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			con=localDatabase.getConnection();
			List<User> pbxsByUserList = new ArrayList<User>();
			String selectTableSQL;
			if (reverse){
				selectTableSQL = "select iduser,name,email,password,admin from users where iduser not in (select iduser from user_pbx where idpbx=?)";
			}
			else {
				selectTableSQL = "select iduser,name,email,password,admin from users where iduser in (select iduser from user_pbx where idpbx=?)";
			}
			preparedStatement = con.prepareStatement(selectTableSQL);
			preparedStatement.setInt(1, idpbx);
			rs = preparedStatement.executeQuery();
			if (!rs.next())
				return null;
			do{
				pbxsByUserList.add(new User(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getBoolean(5)));
			}while(rs.next());
			DatabaseConnectionManager.closeQuietly(con, rs, preparedStatement);
			return pbxsByUserList;
		} catch (SQLException | NamingException e) {
			log.error("Error al recuperar centralitas por usuario de la BBDD: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, rs, preparedStatement);
			throw e;
		}	
	}
	/**
	 * Recibe un Id de usuario y devuelve un objeto User con los valores del usuario que están almacenados en lista de usuarios de la clase
	 * @param iduser Id de usuario a consultar en la lista de usuarios
	 * @return Objeto User con los datos del usuario
	 */
	public User getUserById(int iduser){
		for (User user : userList) {
			if (user.getIduser() == iduser) {
				return user;
			}
		}
		return null;
	}
	/**
	 * Recibe un Id de usuario y devuelve su email
	 * @param iduser Id de usuario a consultar la lista de usuarios
	 * @return email del usuario
	 */
	public String getUserEmail(int iduser) {
		for (User user : userList) {
			if (user.getIduser() == iduser) {
				return user.getEmail();
			}
		}
		return null;
	}
	/**
	 * Recibe un Id de usuario y devuelve su nombre
	 * @param iduser Id de usuario a consultar la lista de usuarios
	 * @return nombre del usuario
	 */
	public String getUserName(int iduser) {
		for (User user : userList) {
			if (user.getIduser() == iduser) {
				return user.getName();
			}
		}
		return null;
	}
	/**
	 * Recibe un Id de usuario y devuelve true si es administrador
	 * @param iduser Id de usuario a consultar la lista de usuarios
	 * @return True si es administrador
	 */
	public Boolean getUserAdmin(int iduser) {
		for (User user : userList) {
			if (user.getIduser() == iduser) {
				return user.isAdmin();
			}
		}
		return null;
	}
	/**
	 * Edita un usuario con los parámetros especificados en la base de datos local y actualiza el objeto en la lista de usuarios de la clase
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param id Id del usuario
	 * @param name Nombre de usuario
	 * @param email email del usuario
	 * @param password Contraseña del usuario
	 * @param isAdmin Indica si es administrador
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public void editUser(DatabaseConnectionManager localDatabase, int id, String name, String email, String password, boolean isAdmin) throws SQLException,NamingException{
		String updateTableSQL;
		Connection con=null;
		PreparedStatement preparedStatement = null;
		if (password.equals("none")) {
			updateTableSQL = "UPDATE users SET name=?,email =?,admin=? WHERE iduser = ?";
		} else {
			updateTableSQL = "UPDATE users SET name=?,email =?,password=?,admin=? WHERE iduser = ?";
		}
		try {
			con=localDatabase.getConnection();
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, email);
			if (!password.equals("none")) {
				preparedStatement.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
				preparedStatement.setBoolean(4, isAdmin);
				preparedStatement.setInt(5, id);
			} else {
				preparedStatement.setBoolean(3, isAdmin);
				preparedStatement.setInt(4, id);
			}
			preparedStatement.executeUpdate();
			for (User user : userList) {
				if (user.getIduser() == id) {
					user.setAdmin(isAdmin);
					user.setEmail(email);
					user.setName(name);
					if (!password.equals("none"))
						user.setPassword(password);
				}
			}
			DatabaseConnectionManager.closeQuietly(con, null, preparedStatement);
		} catch (SQLException | NamingException e) {
			log.error("Error al recuperar centralitas por usuario de la BBDD: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, null, preparedStatement);
			throw e;
		}
	}
	/**
	 * Crea un nuevo usuario en la base de datos local y en la lista de usuarios de la clase
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param name Nombre de usuario
	 * @param email email del usuario
	 * @param password Contraseña del usuario
	 * @param isAdmin Indica si es administrador
	 * @return Id del nuevo usuario, -1 si existe un error en la inserción.
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public int newUser(DatabaseConnectionManager localDatabase, String name, String email, String password, boolean isAdmin) throws SQLException,NamingException{
		String updateTableSQL = "insert into users (name,email,password,admin) values (?,?,?,?)";
		Connection con=null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			con=localDatabase.getConnection();
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
			preparedStatement.setBoolean(4, isAdmin);
			preparedStatement.executeUpdate();
			String selectTableSQL = "select iduser from users where email=?";
			preparedStatement = con.prepareStatement(selectTableSQL);
			preparedStatement.setString(1, email);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				int iduser = rs.getInt(1);
				userList.add(new User(rs.getInt(1), name, email, password, isAdmin));
				DatabaseConnectionManager.closeQuietly(con, rs, preparedStatement);
				return iduser;
			} else {
				DatabaseConnectionManager.closeQuietly(con, rs, preparedStatement);
				return -1;
			}

		} catch (SQLException | NamingException e) {
			log.error("Error al insertar un nuevo usuario en la BBDD: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, rs, preparedStatement);
			throw e;
		}
	}

	/**
	 * Edita las centralitas a la que tiene acceso un usuario
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param iduser Id del usuario 
	 * @param pbxs Matriz de nombres de centralitas
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public void editUserPbxs(DatabaseConnectionManager localDatabase, int iduser, String[] pbxs)
			throws SQLException, NamingException {
		String updateTableSQL = "delete from user_pbx where iduser=?";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try {
			con = localDatabase.getConnection();
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setInt(1, iduser);
			preparedStatement.executeUpdate();
			updateTableSQL = "insert into user_pbx values(?,?)";
			for (String idpbx : pbxs) {

				preparedStatement = con.prepareStatement(updateTableSQL);
				preparedStatement.setInt(1, iduser);
				preparedStatement.setInt(2, Integer.parseInt(idpbx));
				preparedStatement.executeUpdate();
			}
			DatabaseConnectionManager.closeQuietly(con, null, preparedStatement);
		} catch (SQLException | NamingException e) {
			log.error("Error al modificar las centralitas a las que tiene acceso el usuario: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, null, preparedStatement);
			throw e;
		}
	}
	/**
	 * Elimina un usuario en la base de datos local y en la lista de objetos de usuario de la clase
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param iduser Id del usuario 
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public void deleteUser(DatabaseConnectionManager localDatabase, int iduser) throws SQLException, NamingException{
		String updateTableSQL = "delete from users where iduser=?";
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try {
			con = localDatabase.getConnection();
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setInt(1, iduser);
			preparedStatement.executeUpdate();
			DatabaseConnectionManager.closeQuietly(con, null, preparedStatement);
		} catch (SQLException | NamingException e) {
			log.error("Error al eliminar un usuario de la BBDD: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, null, preparedStatement);
			throw e;
		}
		Iterator<User> it = userList.iterator();
		while (it.hasNext()) {
			User user = it.next();
			if (user.getIduser()==iduser) {
				it.remove();
			}
		}
	}
}
