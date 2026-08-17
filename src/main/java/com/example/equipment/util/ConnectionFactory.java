package com.example.equipment.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC 接続の取得。
 */
public final class ConnectionFactory {

    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private static volatile boolean driverLoaded;

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        ensureDriver();
        Connection connection = DriverManager.getConnection(
                DbSettings.getJdbcUrl(), DbSettings.getUser(), DbSettings.getPassword());
        connection.setAutoCommit(true);
        return connection;
    }

    private static void ensureDriver() throws SQLException {
        if (driverLoaded) {
            return;
        }
        synchronized (ConnectionFactory.class) {
            if (driverLoaded) {
                return;
            }
            try {
                Class.forName("org.h2.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException | ExceptionInInitializerError e) {
                // Java 7 multi-catch: ClassNotFoundException と初期化エラーをまとめて処理
                LOGGER.log(Level.SEVERE, "H2 driver not found", e);
                throw new SQLException("H2 JDBC driver is not available", e);
            }
        }
    }
}
