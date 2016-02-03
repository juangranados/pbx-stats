package com.pbx_stats.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseConnectionManager {

    private DataSource ds;

    public void init() throws NamingException {
    	InitialContext initContext = new InitialContext();
		Context env = (Context) initContext.lookup("java:comp/env");
		ds = (DataSource) env.lookup("jdbc/pbx-stats");
    }

    public Connection getConnection() throws NamingException, SQLException {
        if(ds == null) init();
        return ds.getConnection();
    }
    
    public DataSource getDataSource() throws NamingException, SQLException {
        if(ds == null) init();
        return ds;
    }

	public static void closeQuietly(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement) {
        if (resultSet != null) try { resultSet.close(); } catch (SQLException logOrIgnore) {}
        if (connection != null) try { connection.close(); } catch (SQLException logOrIgnore) {}
        if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException logOrIgnore) {}
    }
}
