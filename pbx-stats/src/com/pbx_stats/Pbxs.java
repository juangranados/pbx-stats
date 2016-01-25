package com.pbx_stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pbx_stats.beans.Pbx;
import com.pbx_stats.beans.Pricing;
import com.pbx_stats.tools.AdvancedEncryptionStandard;
/**
 * Clase para manejar todas las centralitas de la base de datos
 * @author Juan Granados
 *
 */
public class Pbxs {
	//Lista para almacenar las centralitas
	public List<Pbx> pbxsList = new ArrayList<Pbx>();
	//Log para generar entradas en el log mediante log4j.
	private static final Logger log = LogManager.getLogger("PBXs: ");
	//Clave de encriptación para almacenar y recuperar las contraseñas de acceso a la centralita
	private static String encryptionKey = "0123456789abcdef";
	
	/**
	 * Constructor que genera la lista de centralitas 
	 * @param con Conexión a la base de datos local
	 */
	public Pbxs(Connection con) throws Exception{
		try {
			ResultSet rs = con.prepareStatement("select idpbx,name,ip,port,username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype from pbxs order by idpbx").executeQuery();
			while(rs.next()){
				pbxsList.add(new Pbx(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15)));
			}
		} catch (Exception e) {
			log.error("Error al recuperar centralitas de la BBDD: " + e.getMessage());
			throw e;
		}
	}
	public Pbx getPbxById(int idpbx)
	{
		for (Pbx pbx : pbxsList) {
			if (pbx.getIdpbx() == idpbx) {
				return pbx;
			}
		}
		return null;
	}
	public List<Pbx> getPbxsByUser(Connection con,int iduser,boolean reverse){
		try {
			List<Pbx> pbxsByUsersList = new ArrayList<Pbx>();
			String selectTableSQL;
			if (reverse){
				selectTableSQL = "select idpbx,name,ip,port,username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype from pbxs where idpbx not in (select idpbx from user_pbx where iduser=?)";
			}
			else {
				selectTableSQL = "select idpbx,name,ip,port,username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype from pbxs where idpbx in (select idpbx from user_pbx where iduser=?)";
			}
			PreparedStatement preparedStatement = con.prepareStatement(selectTableSQL);
			preparedStatement.setInt(1, iduser);
			ResultSet rs = preparedStatement.executeQuery();
			if (!rs.next())
				return null;
			do{
				pbxsByUsersList.add(new Pbx(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15)));
			}while(rs.next());
			return pbxsByUsersList;
		} catch (SQLException e) {
			log.error("Error al recuperar centralitas por usuario de la BBDD: " + e.getMessage());
			return null;
		}	
	}
	public boolean editPbx(Connection con,int idpbx, String name,String ip, int port,String username, String password, String db, String cdrname, String datetime, String src, String dst, String duration,String billsec,String disposition,String calltype){
		String updateTableSQL;
		if (password.equals("none")){
			updateTableSQL = "UPDATE pbxs SET name=?,ip=?,port=?,username=?,db=?,cdrname=?,datetime=?,src=?,dst=?,duration=?,billsec=?,disposition=?,calltype=? WHERE idpbx=?";
		}
		else{
			updateTableSQL = "UPDATE pbxs SET  name=?,ip=?,port=?,username=?,password=?,db=?,cdrname=?,datetime=?,src=?,dst=?,duration=?,billsec=?,disposition=?,calltype=? WHERE idpbx=?";
		}
		try {
			PreparedStatement preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, ip);
			preparedStatement.setInt(3, port);
			preparedStatement.setString(4, username);
			if (!password.equals("none")){
				try {
					preparedStatement.setString(5, AdvancedEncryptionStandard.encrypt(password,encryptionKey));
				} catch (Exception e) {
					log.error("Error al encriptar la contraseña: " + e.getMessage());
					return false;
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
			}
			else{
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
			preparedStatement .executeUpdate();
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
			return true;
		} catch (SQLException e) {
			log.error("Error al modificar pbx en la BBDD: " + e.getMessage());
			return false;
		}
	}
	public int newPbx(Connection con, String name,String ip, int port,String username, String password, String db,String cdrname, String datetime, String src, String dst, String duration,String billsec,String disposition,String calltype) {
		String updateTableSQL = "insert into pbxs (name,ip,port,username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, ip);
			preparedStatement.setInt(3, port);
			preparedStatement.setString(4, username);
			try {
				preparedStatement.setString(5, AdvancedEncryptionStandard.encrypt(password,encryptionKey));
			} catch (Exception e) {
				log.error("Error al encriptar contraseña: " + e.getMessage());
				return -1;
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
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				int idpbx = rs.getInt(1);
				pbxsList.add(new Pbx(rs.getInt(1), name, ip, port, username,password,db,cdrname,datetime,src,dst,duration,billsec,disposition,calltype));
				Pricing.insertPricing(con, idpbx, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
				return idpbx;
			} else {
				return -1;
			}

		} catch (SQLException e) {
			log.error("Error al insertar una nueva centralita en la BBDD: " + e.getMessage());
			return -1;
		}
	}
	public boolean deletePbx(Connection con, int idpbx) {
		String updateTableSQL = "delete from pbxs where idpbx=?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setInt(1, idpbx);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("Error al eliminar una centralita de la BBDD: " + e.getMessage());
			return false;
		}

		Iterator<Pbx> it = pbxsList.iterator();
		while (it.hasNext()) {
			Pbx user = it.next();
			if (user.getIdpbx()==idpbx) {
				it.remove();
			}
		}
		return true;
	}
	// Modifica los usuarios que tienen acceso a la centralita
		public boolean editPbxUsers(Connection con, int idpbx, String[] users) {
			String updateTableSQL = "delete from user_pbx where idpbx=?";
			PreparedStatement preparedStatement;
			try {
				preparedStatement = con.prepareStatement(updateTableSQL);
				preparedStatement.setInt(1, idpbx);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				log.error("Error al eliminar las centralitas a las que tiene acceso el usuario: " + e.getMessage());
				return false;
			}
			if (users == null) {
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
					return false;
				}
			}
			return true;
		}
}
