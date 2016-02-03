package com.pbx_stats.beans;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pbx_stats.tools.AdvancedEncryptionStandard;

public class Pbx {
	private int idpbx;
	private String name;
	private String ip;
	private int port;
	private String username;
	private String password;
	private String db;
	private String cdrname;
	private String datetime;
	private String src;
	private String dst;
	private String duration;
	private String billsec;
	private String disposition;
	private String calltype;
	private String encryptionKey;
	private static final Logger log = LogManager.getLogger("Pbx: ");
	
	public Pbx(int idpbx, String name,String ip,int port,String username, String password, String database, String cdrname, String datetime, String src, String dst,
			String duration, String billsec, String disposition,String calltype) throws NamingException {
		super();
		try {
			this.encryptionKey=InitialContext.doLookup("java:comp/env/encryptionString");  
		} catch (NamingException e) {
			log.error("Error al configurar JNDI para recuperar la clave de encriptación: " + e.getMessage());
			throw e;
		}
		this.idpbx = idpbx;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.password = password;
		this.db = database;
		this.cdrname = cdrname;
		this.datetime = datetime;
		this.src = src;
		this.dst = dst;
		this.duration = duration;
		this.billsec=billsec;
		this.disposition=disposition;
		this.calltype=calltype;
		
	}
	public int getIdpbx() {
		return idpbx;
	}
	public void setIdpbx(int idpbx) {
		this.idpbx = idpbx;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getPassword() throws Exception {
		try {
			return AdvancedEncryptionStandard.decrypt(password,encryptionKey);
		} catch (Exception e) {
			log.error("Error al encriptar la contraseña: " + e.getMessage());
			throw e;
		}
	}
	public void setPassword(String password) throws Exception {
		
		try {
			this.password = AdvancedEncryptionStandard.encrypt(password,encryptionKey);
		} catch (Exception e) {
			log.error("Error al desencriptar la contraseña: " + e.getMessage());
			this.password=null;
			throw e;
		}
	}
	public String getCdrname() {
		return cdrname;
	}
	public void setCdrname(String cdrname) {
		this.cdrname = cdrname;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDb() {
		return db;
	}
	public void setDb(String database) {
		this.db = database;
	}
	public String getBillsec() {
		return billsec;
	}
	public void setBillsec(String billsec) {
		this.billsec = billsec;
	}
	public String getDisposition() {
		return disposition;
	}
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	public String getCalltype() {
		return calltype;
	}
	public void setCalltype(String calltype) {
		this.calltype = calltype;
	}
}
