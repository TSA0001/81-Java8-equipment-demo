package com.example.equipment.util;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * アプリ起動時に DB を初期化する。
 */
@WebListener
public class AppBootstrapListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppBootstrapListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DatabaseInitializer.initializeIfNeeded();
            sce.getServletContext().setAttribute("dbReady", Boolean.TRUE);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database on startup", e);
            sce.getServletContext().setAttribute("dbReady", Boolean.FALSE);
            sce.getServletContext().setAttribute("dbError", e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // no-op
    }
}
