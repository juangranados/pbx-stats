package com.pbx_stats.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Pricing {
	private int idpbx;
	private int fijo;
	private int movil;
	private int adicional;
	private int compartido;
	private int internacional;
	private int efijo;
	private int emovil;
	private int eadicional;
	private int ecompartido;
	private int einternacional;
	private int desconocido;
	private int edesconocido;
	private static final Logger log = LogManager.getLogger("Pricing: ");
	
	public static void insertPricing(
		Connection con,int idpbx, int fijo, int movil, int adicional, int compartido, int internacional, int efijo,
		int emovil, int eadicional, int ecompartido, int einternacional, int desconocido, int edesconocido){
		
		String updateTableSQL = "INSERT INTO pricing VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement(updateTableSQL);
			preparedStatement.setInt(1, idpbx);
			preparedStatement.setInt(2, fijo);
			preparedStatement.setInt(3, movil);
			preparedStatement.setInt(4,adicional);
			preparedStatement.setInt(5,compartido);
			preparedStatement.setInt(6,internacional);
			preparedStatement.setInt(7,desconocido);
			preparedStatement.setInt(8,efijo);
			preparedStatement.setInt(9,emovil);
			preparedStatement.setInt(10,eadicional);
			preparedStatement.setInt(11,ecompartido);
			preparedStatement.setInt(12,einternacional);
			preparedStatement.setInt(13,edesconocido);
			preparedStatement.executeUpdate();
			log.info("Se ha insertado una nueva facturación para la centralita con id: " + idpbx);
		} catch (SQLException e) {
		log.error("Se ha producido un error al insertar una nueva facturación para la centralita con id: " + idpbx + " " + e.getMessage());
		}	
	}
	
	public static Boolean updatePricing(
			Connection con,int idpbx, int fijo, int movil, int adicional, int compartido, int internacional, int efijo,
			int emovil, int eadicional, int ecompartido, int einternacional, int desconocido, int edesconocido){
			
			String updateTableSQL = "UPDATE pricing SET fijo=?, movil=?, adicional=?, compartido=?, internacional=?, desconocido=?, efijo=?, emovil=?, eadicional=?, ecompartido=?, einternacional=?, edesconocido=? WHERE idpbx=?;";
			PreparedStatement preparedStatement;
			try {
				preparedStatement = con.prepareStatement(updateTableSQL);
				preparedStatement.setInt(1, fijo);
				preparedStatement.setInt(2, movil);
				preparedStatement.setInt(3,adicional);
				preparedStatement.setInt(4,compartido);
				preparedStatement.setInt(5,internacional);
				preparedStatement.setInt(6,desconocido);
				preparedStatement.setInt(7,efijo);
				preparedStatement.setInt(8,emovil);
				preparedStatement.setInt(9,eadicional);
				preparedStatement.setInt(10,ecompartido);
				preparedStatement.setInt(11,einternacional);
				preparedStatement.setInt(12,edesconocido);
				preparedStatement.setInt(13,idpbx);
				preparedStatement.executeUpdate();
				log.info("Se ha modificado la facturación para la centralita con id: " + idpbx);
				return true;
			} catch (SQLException e) {
			log.error("Se ha producido un error al modificar la facturación para la centralita con id: " + idpbx + " " + e.getMessage());
			return false;
			}	
		}
	
	public Pricing(int idpbx, int fijo, int movil, int adicional, int compartido, int internacional, int efijo,
			int emovil, int eadicional, int ecompartido, int einternacional, int desconocido, int edesconocido) {
		super();
		this.idpbx = idpbx;
		this.fijo = fijo;
		this.movil = movil;
		this.adicional = adicional;
		this.compartido = compartido;
		this.internacional = internacional;
		this.efijo = efijo;
		this.emovil = emovil;
		this.eadicional = eadicional;
		this.ecompartido = ecompartido;
		this.einternacional = einternacional;
		this.desconocido = desconocido;
		this.edesconocido = edesconocido;
	}
	public Pricing(Connection con,int idPbx){
		String selectTableSQL;
		selectTableSQL = "select * from pricing where idpbx=?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement(selectTableSQL);
			preparedStatement.setInt(1, idPbx);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()){
				this.idpbx=rs.getInt(1);
				this.fijo=rs.getInt(2);
				this.movil=rs.getInt(3);
				this.adicional=rs.getInt(4);
				this.compartido=rs.getInt(5);
				this.internacional=rs.getInt(6);
				this.desconocido=rs.getInt(7);
				this.efijo=rs.getInt(8);
				this.emovil=rs.getInt(9);
				this.eadicional=rs.getInt(10);
				this.ecompartido=rs.getInt(11);
				this.einternacional=rs.getInt(12);
				this.edesconocido=rs.getInt(13);
			}	
		} catch (SQLException e) {
			log.error("Se ha producido un error al inicializar la clase pricing: " + e.getMessage());
		}
	}
	public int getIdpbx() {
		return idpbx;
	}
	public void setIdpbx(int idpbx) {
		this.idpbx = idpbx;
	}
	public int getFijo() {
		return fijo;
	}
	public void setFijo(int fijo) {
		this.fijo = fijo;
	}
	public int getMovil() {
		return movil;
	}
	public void setMovil(int movil) {
		this.movil = movil;
	}
	public int getAdicional() {
		return adicional;
	}
	public void setAdicional(int adicional) {
		this.adicional = adicional;
	}
	public int getCompartido() {
		return compartido;
	}
	public void setCompartido(int compartido) {
		this.compartido = compartido;
	}
	public int getInternacional() {
		return internacional;
	}
	public void setInternacional(int internacional) {
		this.internacional = internacional;
	}
	public int getEfijo() {
		return efijo;
	}
	public void setEfijo(int efijo) {
		this.efijo = efijo;
	}
	public int getEmovil() {
		return emovil;
	}
	public void setEmovil(int emovil) {
		this.emovil = emovil;
	}
	public int getEadicional() {
		return eadicional;
	}
	public void setEadicional(int eadicional) {
		this.eadicional = eadicional;
	}
	public int getEcompartido() {
		return ecompartido;
	}
	public void setEcompartido(int ecompartido) {
		this.ecompartido = ecompartido;
	}
	public int getEinternacional() {
		return einternacional;
	}
	public void setEinternacional(int einternacional) {
		this.einternacional = einternacional;
	}

	public int getDesconocido() {
		return desconocido;
	}

	public void setDesconocido(int desconocido) {
		this.desconocido = desconocido;
	}

	public int getEdesconocido() {
		return edesconocido;
	}

	public void setEdesconocido(int edesconocido) {
		this.edesconocido = edesconocido;
	}
}
