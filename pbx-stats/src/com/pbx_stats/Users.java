package com.pbx_stats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import com.pbx_stats.beans.User;
import com.pbx_stats.tools.BCrypt;

public class Users {
	public List<User> userList = new ArrayList<User>();
	private static final Logger log = LogManager.getLogger("Users: ");

	public Users(Connection con) {
		BasicConfigurator.configure();
		log.setAdditivity(true);
		try {
			ResultSet rs = con.prepareStatement("select iduser,name,email,password,admin from users").executeQuery();
			while (rs.next()) {
				userList.add(
						new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5)));
			}
		} catch (SQLException e) {
			log.error("Error al recuperar usuarios de la BBDD: " + e.getMessage());
		}
	}
	
	public List<User> getUsersByPbx(Connection con, int idpbx, Boolean reverse) {
		try {
			List<User> pbxsByUserList = new ArrayList<User>();
			String selectTableSQL;
			if (reverse){
				selectTableSQL = "select iduser,name,email,password,admin from users where iduser not in (select iduser from user_pbx where idpbx=?)";
			}
			else {
				selectTableSQL = "select iduser,name,email,password,admin from users where iduser in (select iduser from user_pbx where idpbx=?)";
			}
			PreparedStatement preparedStatement = con.prepareStatement(selectTableSQL);
			preparedStatement.setInt(1, idpbx);
			ResultSet rs = preparedStatement.executeQuery();
			if (!rs.next())
				return null;
			do{
				pbxsByUserList.add(new User(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getBoolean(5)));
			}while(rs.next());
			return pbxsByUserList;
		} catch (SQLException e) {
			log.error("Error al recuperar centralitas por usuario de la BBDD: " + e.getMessage());
			return null;
		}	
	}
	
	public User getUserById(Connection con, int iduser){
		for (User user : userList) {
			if (user.getIduser() == iduser) {
				return user;
			}
		}
		return null;
	}
	public String getUserEmail(int iduser) {
		for (User user : userList) {
			if (user.getIduser() == iduser) {
				return user.getEmail();
			}
		}
		return null;
	}

	public String getUserName(int iduser) {
		for (User user : userList) {
			if (user.getIduser() == iduser) {
				return user.getName();
			}
		}
		return null;
	}

	public Boolean getUserAdmin(int iduser) {
		for (User user : userList) {
			if (user.getIduser() == iduser) {
				return user.isAdmin();
			}
		}
		return null;
	}

	public boolean editUser(Connection con, int id, String name, String email, String password, boolean isAdmin) {
		String updateTableSQL;
		if (password.equals("none")) {
			updateTableSQL = "UPDATE users SET name=?,email =?,admin=? WHERE iduser = ?";
		} else {
			updateTableSQL = "UPDATE users SET name=?,email =?,password=?,admin=? WHERE iduser = ?";
		}
		try {
			PreparedStatement preparedStatement = con.prepareStatement(updateTableSQL);
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
			return true;
		} catch (SQLException e) {
			log.error("Error al modificar usuario en la BBDD: " + e.getMessage());
			return false;
		}
	}

	public int newUser(Connection con, String name, String email, String password, boolean isAdmin) {
		String updateTableSQL = "insert into users (name,email,password,admin) values (?,?,?,?)";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
			preparedStatement.setBoolean(4, isAdmin);
			preparedStatement.executeUpdate();
			String selectTableSQL = "select iduser from users where email=?";
			preparedStatement = con.prepareStatement(selectTableSQL);
			preparedStatement.setString(1, email);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				int iduser = rs.getInt(1);
				userList.add(new User(rs.getInt(1), name, email, password, isAdmin));
				return iduser;
			} else {
				return -1;
			}

		} catch (SQLException e) {
			log.error("Error al insertar un nuevo usuario en la BBDD: " + e.getMessage());
			return -1;
		}
	}

	// Modifica las pbx a las que tiene acceso el usuario
	public boolean editUserPbxs(Connection con, int iduser, String[] pbxs) {
		String updateTableSQL = "delete from user_pbx where iduser=?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setInt(1, iduser);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("Error al eliminar las centralitas a las que tiene acceso el usuario: " + e.getMessage());
			return false;
		}
		if (pbxs == null) {
			return true;
		}
		updateTableSQL = "insert into user_pbx values(?,?)";
		for (String idpbx : pbxs) {
			try {
				preparedStatement = con.prepareStatement(updateTableSQL);
				preparedStatement.setInt(1, iduser);
				preparedStatement.setInt(2, Integer.parseInt(idpbx));
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				log.error("Error al modificar las centralitas a las que tiene acceso el usuario: " + e.getMessage());
				return false;
			}
		}
		return true;
	}

	public boolean deleteUser(Connection con, int iduser) {
		String updateTableSQL = "delete from users where iduser=?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setInt(1, iduser);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("Error al eliminar un usuario de la BBDD: " + e.getMessage());
			return false;
		}

		Iterator<User> it = userList.iterator();
		while (it.hasNext()) {
			User user = it.next();
			if (user.getIduser()==iduser) {
				it.remove();
			}
		}
		return true;
	}
}
