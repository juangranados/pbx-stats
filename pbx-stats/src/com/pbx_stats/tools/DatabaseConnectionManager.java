package com.pbx_stats.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
/**
 * Clase que contiene un objeto DataSource al cual las clases pueden solicitar conexiones
 * mediante su método. 
 * @author Juan Granados
 */
public class DatabaseConnectionManager {
	//Datasource
    private DataSource ds;
    /**
     * Inicializa el datasource mediante JNDI
     * @throws NamingException
     */
    public void init() throws NamingException {
    	InitialContext initContext = new InitialContext();
		Context env = (Context) initContext.lookup("java:comp/env");
		ds = (DataSource) env.lookup("jdbc/pbx-stats");
    }
    /**
     * Crea una conexión con la bas de datos
     * @return Conexión
     * @throws NamingException Error al consultar los datos de conexión JNDI
     * @throws SQLException Error de acceso a MySQL
     */
    public Connection getConnection() throws NamingException, SQLException {
        if(ds == null) init();
        return ds.getConnection();
    }
    /**
     * Devuelve el la variable de tipo Datasource
     * @return objeto de tipo Datasource
     * @throws NamingException Error al consultar los datos de conexión JNDI
     * @throws SQLException Error de acceso a MySQL
     */
    public DataSource getDataSource() throws NamingException, SQLException {
        if(ds == null) init();
        return ds;
    }
    /**
     * Cierra la conexión MySQL, el ResultSet y el PreparedStatement
     * @param connection Conexión
     * @param resultSet ResulSet
     * @param preparedStatement PreparedStatement
     */
	public static void closeQuietly(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement) {
        if (resultSet != null) try { resultSet.close(); } catch (SQLException logOrIgnore) {}
        if (connection != null) try { connection.close(); } catch (SQLException logOrIgnore) {}
        if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException logOrIgnore) {}
    }
}
