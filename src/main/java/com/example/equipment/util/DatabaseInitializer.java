package com.example.equipment.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DB 未作成時のみ schema / seed を投入する。
 */
public final class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    private DatabaseInitializer() {
    }

    public static synchronized void initializeIfNeeded() throws SQLException {
        try (Connection connection = ConnectionFactory.getConnection()) {
            if (tableExists(connection, "ITEMS")) {
                LOGGER.info("Database already initialized. Skip schema/seed.");
                return;
            }
            LOGGER.info("Initializing database schema and seed data. path=" + DbSettings.getDbPath());
            executeScript(connection, "/db/schema.sql");
            executeScript(connection, "/db/seed.sql");
            LOGGER.info("Database initialization completed.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database initialization failed", e);
            throw e;
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static void executeScript(Connection connection, String classpathLocation) throws SQLException {
        List<String> statements = readStatements(classpathLocation);
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                if (sql.trim().isEmpty()) {
                    continue;
                }
                statement.execute(sql);
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private static List<String> readStatements(String classpathLocation) throws SQLException {
        InputStream in = DatabaseInitializer.class.getResourceAsStream(classpathLocation);
        if (in == null) {
            throw new SQLException("SQL resource not found: " + classpathLocation);
        }
        List<String> statements = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                current.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    String sql = current.toString().trim();
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    statements.add(sql);
                    current.setLength(0);
                }
            }
            if (current.toString().trim().length() > 0) {
                statements.add(current.toString().trim());
            }
        } catch (IOException e) {
            throw new SQLException("Failed to read SQL resource: " + classpathLocation, e);
        }
        return statements;
    }
}
