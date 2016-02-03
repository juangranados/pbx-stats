package com.pbx_stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pbx_stats.beans.Pbx;
import com.pbx_stats.beans.Pricing;
import com.pbx_stats.tools.AdvancedEncryptionStandard;
import com.pbx_stats.tools.DatabaseConnectionManager;

/**
 * Clase para manejar todas las centralitas de la base de datos
 * 
 * @author Juan Granados
 *
 */
public class Pbxs {
	// Lista para almacenar las centralitas
	public List<Pbx> pbxsList = new ArrayList<Pbx>();
	// Log para generar entradas en el log mediante log4j.
	private static final Logger log = LogManager.getLogger("PBXs: ");
	// Clave de encriptación para almacenar y recuperar las contraseñas de
	// acceso a la centralita
	private static String encryptionKey = "0123456789abcdef";

	/**
	 * Constructor que genera la lista de centralitas
	 * 
	 * @param localDatabase
	 *            Conexión a la base de datos local
	 */
	/**
	 * Crea el objeto Pbxs que contiene todas las centralitas de la base de
	 * datos local
	 * 
	 * @param localDatabase
	 *             Clase que contiene un datasource para el acceso la base de datos local
	 * @throws NamingException
	 *             No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException
	 *             Error al consultar la base de datos local
	 */
	public Pbxs(DatabaseConnectionManager localDatabase) throws NamingException, SQLException {
		ResultSet rs = null;
		Connection con = null;
		try {
			con = localDatabase.getConnection();
			rs = con.prepareStatement(
					"select idpbx,name,ip,port,username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype from pbxs order by idpbx")
					.executeQuery();
			while (rs.next()) {
				pbxsList.add(new Pbx(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10),
						rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15)));
			}
			DatabaseConnectionManager.closeQuietly(con, rs,null);
		} catch (NamingException | SQLException e) {
			DatabaseConnectionManager.closeQuietly(con, rs,null);
			log.error("Error al crear el objeto Pbxs: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Recibe un id de centralita y devuelve un objeto de tipo Pbx con las
	 * propiedades de ésta
	 * 
	 * @param idpbx
	 *            Id de la centralita en la base de datos local
	 * @return Objeto Pbx con los datos de la centralita
	 */
	public Pbx getPbxById(int idpbx) {
		for (Pbx pbx : pbxsList) {
			if (pbx.getIdpbx() == idpbx) {
				return pbx;
			}
		}
		return null;
	}

	/**
	 * Devuelve una lista de objetos Pbx a los que [si | no] tiene acceso un id
	 * de usuario
	 * 
	 * @param localDatabase
	 *            Clase que contiene un datasource para el acceso la base de datos local
	 * @param iduser
	 *            Id del usuario
	 * @param reverse
	 *            Si es True, devuelve la lista de objetos Pbx a los que no
	 *            tiene acceso el usuario
	 * @return lista de objetos Pbx a los que [si | no] tiene acceso un id de
	 *         usuario
	 * @throws NamingException
	 *             No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException
	 *             Error al consultar la base de datos local
	 */
	public List<Pbx> getPbxsByUser(DatabaseConnectionManager localDatabase, int iduser, boolean reverse)
			throws NamingException, SQLException {
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement preparedStatement=null;
		try {
			con = localDatabase.getConnection();
			List<Pbx> pbxsByUsersList = new ArrayList<Pbx>();
			String selectTableSQL;
			if (reverse) {
				selectTableSQL = "select idpbx,name,ip,port,username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype from pbxs where idpbx not in (select idpbx from user_pbx where iduser=?)";
			} else {
				selectTableSQL = "select idpbx,name,ip,port,username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype from pbxs where idpbx in (select idpbx from user_pbx where iduser=?)";
			}
			preparedStatement = con.prepareStatement(selectTableSQL);
			preparedStatement.setInt(1, iduser);
			rs = preparedStatement.executeQuery();
			if (!rs.next())
				return null;
			do {
				pbxsByUsersList.add(new Pbx(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9),
						rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14),
						rs.getString(15)));
			} while (rs.next());
			DatabaseConnectionManager.closeQuietly(con, rs, preparedStatement);
			return pbxsByUsersList;
		} catch (NamingException | SQLException e) {
			DatabaseConnectionManager.closeQuietly(con, rs, preparedStatement);
			log.error("Error al obtener la lista de Pbxs por usuario: " + e.getMessage());
			throw e;
		}
	}
	/**
	 * Edita una Pbx según su id
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param idpbx id de la cantralita a editar
	 * @param name nuevo nombre de la centralita
	 * @param ip nuevo ip de la centralita
	 * @param port nuevo puerto de conexión
	 * @param username nuevo usuario para la conexión
	 * @param password nueva contraseña de conexión
	 * @param db nueva base de datos de CDR	
	 * @param cdrname nuevo nombre de la tabla CDR
	 * @param datetime nuevo nombre del campo fecha
	 * @param src nuevo nombre del campo origen
	 * @param dst nuevo nombre del campo destino
	 * @param duration nuevo nombre del campo duración
	 * @param billsec nuevo nombre del campo duración real
	 * @param disposition nuevo nombre del campo resultado de la llamada
	 * @param calltype nuevo nombre del campo tipo de llamada
	 * @throws Exception Excepción al encriptar la contraseña de acceso a la centralita
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public void editPbx(DatabaseConnectionManager localDatabase, int idpbx, String name, String ip, int port,
			String username, String password, String db, String cdrname, String datetime, String src, String dst,
			String duration, String billsec, String disposition, String calltype)
					throws Exception, NamingException, SQLException {
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try {
			con = localDatabase.getConnection();
			String updateTableSQL;
			if (password.equals("none")) {
				updateTableSQL = "UPDATE pbxs SET name=?,ip=?,port=?,username=?,db=?,cdrname=?,datetime=?,src=?,dst=?,duration=?,billsec=?,disposition=?,calltype=? WHERE idpbx=?";
			} else {
				updateTableSQL = "UPDATE pbxs SET  name=?,ip=?,port=?,username=?,password=?,db=?,cdrname=?,datetime=?,src=?,dst=?,duration=?,billsec=?,disposition=?,calltype=? WHERE idpbx=?";
			}
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, ip);
			preparedStatement.setInt(3, port);
			preparedStatement.setString(4, username);
			if (!password.equals("none")) {
				try {
					preparedStatement.setString(5, AdvancedEncryptionStandard.encrypt(password, encryptionKey));
				} catch (Exception e) {
					log.error("Error al encriptar la contraseña: " + e.getMessage());
					DatabaseConnectionManager.closeQuietly(con,null,preparedStatement);
					throw e;
				}
				preparedStatement.setString(6, db);
				preparedStatement.setString(7, cdrname);
				preparedStatement.setString(8, datetime);
				preparedStatement.setString(9, src);
				preparedStatement.setString(10, dst);
				preparedStatement.setString(11, duration);
				preparedStatement.setString(12, billsec);
				preparedStatement.setString(13, disposition);
				preparedStatement.setString(14, calltype);
				preparedStatement.setInt(15, idpbx);
			} else {
				preparedStatement.setString(5, db);
				preparedStatement.setString(6, cdrname);
				preparedStatement.setString(7, datetime);
				preparedStatement.setString(8, src);
				preparedStatement.setString(9, dst);
				preparedStatement.setString(10, duration);
				preparedStatement.setString(11, billsec);
				preparedStatement.setString(12, disposition);
				preparedStatement.setString(13, calltype);
				preparedStatement.setInt(14, idpbx);
			}
			preparedStatement.executeUpdate();
			for (Pbx pbx : pbxsList) {
				if (pbx.getIdpbx() == idpbx) {
					pbx.setName(name);
					pbx.setIp(ip);
					pbx.setPort(port);
					pbx.setUsername(username);
					if (!password.equals("none"))
						pbx.setPassword(password);
					pbx.setDb(db);
					pbx.setCdrname(cdrname);
					pbx.setDatetime(datetime);
					pbx.setSrc(src);
					pbx.setDst(dst);
					pbx.setDuration(duration);
					pbx.setBillsec(billsec);
					pbx.setDisposition(disposition);
					pbx.setCalltype(calltype);
					break;
				}
			}
			DatabaseConnectionManager.closeQuietly(con,null,preparedStatement);
		} catch (NamingException | SQLException e) {
			DatabaseConnectionManager.closeQuietly(con,null,preparedStatement);
			log.error("Error al obtener la lista de Pbxs por usuario: " + e.getMessage());
			throw e;
		}
	}
	/**
	 * Inserta una nueva centralita en la base de datos local
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param idpbx id de la cantralita a editar
	 * @param name nuevo nombre de la centralita
	 * @param ip nuevo ip de la centralita
	 * @param port nuevo puerto de conexión
	 * @param username nuevo usuario para la conexión
	 * @param password nueva contraseña de conexión
	 * @param db nueva base de datos de CDR	
	 * @param cdrname nuevo nombre de la tabla CDR
	 * @param datetime nuevo nombre del campo fecha
	 * @param src nuevo nombre del campo origen
	 * @param dst nuevo nombre del campo destino
	 * @param duration nuevo nombre del campo duración
	 * @param billsec nuevo nombre del campo duración real
	 * @param disposition nuevo nombre del campo resultado de la llamada
	 * @param calltype nuevo nombre del campo tipo de llamada
	 * @throws Exception Excepción al encriptar la contraseña de acceso a la centralita
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public int newPbx(DatabaseConnectionManager localDatabase, String name, String ip, int port, String username,
			String password, String db, String cdrname, String datetime, String src, String dst, String duration,
			String billsec, String disposition, String calltype) throws Exception, NamingException, SQLException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement=null;
		try{
		con = localDatabase.getConnection();
		String updateTableSQL = "insert into pbxs (name,ip,port,username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, ip);
			preparedStatement.setInt(3, port);
			preparedStatement.setString(4, username);
			try {
				preparedStatement.setString(5, AdvancedEncryptionStandard.encrypt(password, encryptionKey));
			} catch (Exception e) {
				DatabaseConnectionManager.closeQuietly(con,rs,preparedStatement);
				log.error("Error al encriptar contraseña: " + e.getMessage());
				throw e; 
			}
			preparedStatement.setString(6, db);
			preparedStatement.setString(7, cdrname);
			preparedStatement.setString(8, datetime);
			preparedStatement.setString(9, src);
			preparedStatement.setString(10, dst);
			preparedStatement.setString(11, duration);
			preparedStatement.setString(12, billsec);
			preparedStatement.setString(13, disposition);
			preparedStatement.setString(14, calltype);
			preparedStatement.executeUpdate();
			String selectTableSQL = "select idpbx from pbxs where name=?";
			preparedStatement = con.prepareStatement(selectTableSQL);
			preparedStatement.setString(1, name);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				int idpbx = rs.getInt(1);
				pbxsList.add(new Pbx(rs.getInt(1), name, ip, port, username, password, db, cdrname, datetime, src, dst,
						duration, billsec, disposition, calltype));
				Pricing.insertPricing(localDatabase, idpbx, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
				DatabaseConnectionManager.closeQuietly(con, rs,preparedStatement);
				return idpbx;
			} else {
				DatabaseConnectionManager.closeQuietly(con, rs,preparedStatement);
				log.error("No se ha encontrado la centralita a modificar");
				DatabaseConnectionManager.closeQuietly(con, rs,preparedStatement);
				return -1;
			}
		} catch (NamingException | SQLException e) {
			log.error("Error al insertar una nueva centralita en la BBDD: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, rs,preparedStatement);
			throw e;
		}
	}
	/**
	 * Borra la centralita con el id especificado de la base de datos local
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param idpbx id de la cantralita a borrar
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public void deletePbx(DatabaseConnectionManager localDatabase, int idpbx) throws NamingException, SQLException {
		String updateTableSQL = "delete from pbxs where idpbx=?";
		Connection con=null;
		PreparedStatement preparedStatement=null;
		try {
		con = localDatabase.getConnection();
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setInt(1, idpbx);
			preparedStatement.executeUpdate();
			DatabaseConnectionManager.closeQuietly(con, null,preparedStatement);
		} catch (NamingException | SQLException e) {
			log.error("Error al eliminar una centralita de la BBDD: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, null,preparedStatement);
			throw e;
		}
		Iterator<Pbx> it = pbxsList.iterator();
		while (it.hasNext()) {
			Pbx user = it.next();
			if (user.getIdpbx() == idpbx) {
				it.remove();
			}
		}
	}

	
	/**
	 * Modifica los usuarios que tienen acceso a una Pbx
	 * @param localDatabase Clase que contiene un datasource para el acceso la base de datos local
	 * @param idpbx id de la centralita que se asignará a la lista de usuarios
	 * @param users matriz de nombres de usuario
	 * @return true si la operación se ejecuta correctamente
	 * @throws NamingException No se ha podido recuperar los datos de conexión al datasource
	 * @throws SQLException Error al consultar la base de datos local
	 */
	public boolean editPbxUsers(DatabaseConnectionManager localDatabase, int idpbx, String[] users) throws SQLException,NamingException {
		String updateTableSQL = "delete from user_pbx where idpbx=?";
		Connection con=null;
		PreparedStatement preparedStatement=null;
		try {
			con=localDatabase.getConnection();
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setInt(1, idpbx);
			preparedStatement.executeUpdate();
		} catch (SQLException | NamingException e) {
			log.error("Error al eliminar las centralitas a las que tiene acceso el usuario: " + e.getMessage());
			DatabaseConnectionManager.closeQuietly(con, null,preparedStatement);
			throw e;
		}
		if (users == null) {
			DatabaseConnectionManager.closeQuietly(con, null,preparedStatement);
			return true;
		}
		updateTableSQL = "insert into user_pbx values(?,?)";
		for (String iduser : users) {
			try {
				preparedStatement = con.prepareStatement(updateTableSQL);
				preparedStatement.setInt(1, Integer.parseInt(iduser));
				preparedStatement.setInt(2, idpbx);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				log.error("Error al modificar las centralitas a las que tiene acceso el usuario: " + e.getMessage());
				DatabaseConnectionManager.closeQuietly(con, null,preparedStatement);
				throw e;
			}
		}
		DatabaseConnectionManager.closeQuietly(con, null,preparedStatement);
		return true;
	}
}
