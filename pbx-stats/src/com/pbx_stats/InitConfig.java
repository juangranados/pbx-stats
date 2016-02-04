package com.pbx_stats;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pbx_stats.tools.DatabaseConnectionManager;

/**
 * Application Lifecycle Listener implementation class InitConfig
 *
 */
public class InitConfig implements ServletContextListener {
	private static final Logger log = LogManager.getLogger("InitConfig: ");
	private static final String ATTRIBUTE_NAME = "localDatabase";
    private DatabaseConnectionManager DatabaseConnectionManager;
    /**
     * Default constructor. 
     */
    public InitConfig() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }
    /**
     * Inicialización del contexto
     * Registro del driver MySQL
     * Creación del objeto DatabaseConnectionManager que contiene un Datasouce 
     * para que los Servlets y clases soliciten conexiones SQL
     */
    public void contextInitialized(ServletContextEvent event)  { 
    	try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
		} catch (SQLException e1) {
			log.error("Error al inicializar el driver MySQL: " + e1.getMessage());
		}
    	ServletContext servletContext = event.getServletContext();
    	DatabaseConnectionManager=new DatabaseConnectionManager();
    	try {
			DatabaseConnectionManager.init();
			servletContext.setAttribute(ATTRIBUTE_NAME,DatabaseConnectionManager);
			log.info("Se ha iniciado la aplicación Pbx-Stats");
		} catch (NamingException e) {
			log.error("Error al recuperar datos de conexión MySQL: " + e.getMessage());
		}
    }
    /**
     * Devuelve el objeto DatabaseConnectionManager
     * @param servletContext Contexto del servidor, normalmente obtenido mediante getServletContext()
     * @return DatabaseConnectionManager
     */
    public static DatabaseConnectionManager getLocalDatabase(ServletContext servletContext) {
        return (DatabaseConnectionManager) servletContext.getAttribute(ATTRIBUTE_NAME);
    }
}
