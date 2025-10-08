package PrestadorSalud.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LocalDatabaseProvisioner {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "5432";
    private static final String DEFAULT_DB = "prestador_db";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";

    private LocalDatabaseProvisioner() {
    }

    public static Map<String, Object> persistenceOverrides() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("jakarta.persistence.jdbc.url", jdbcUrl());
        overrides.put("jakarta.persistence.jdbc.user", databaseUser());
        overrides.put("jakarta.persistence.jdbc.password", databasePassword());
        overrides.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        return overrides;
    }

    public static void ensureDatabaseExists() {
        String dbName = databaseName();
        try (Connection ignored = DriverManager.getConnection(jdbcUrl(), databaseUser(), databasePassword())) {
            // Base accesible, no hay nada más que hacer.
            return;
        } catch (SQLException ex) {
            if (!"3D000".equals(ex.getSQLState())) {
                throw new IllegalStateException("No se pudo conectar a la base de datos local", ex);
            }
        }

        // Intentar crear la base conectándose a la BD administrativa.
        String adminUrl = adminJdbcUrl();
        try (Connection connection = DriverManager.getConnection(adminUrl, databaseUser(), databasePassword());
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE " + sanitizeDatabaseName(dbName));
        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudo crear la base de datos '" + dbName + "'", ex);
        }
    }

    private static String sanitizeDatabaseName(String dbName) {
        String normalized = dbName.toLowerCase(Locale.ROOT);
        if (!normalized.matches("[a-z0-9_]+")) {
            throw new IllegalArgumentException("El nombre de la base solo puede contener letras, números o guiones bajos");
        }
        return normalized;
    }

    private static String jdbcUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", databaseHost(), databasePort(), databaseName());
    }

    private static String adminJdbcUrl() {
        return String.format("jdbc:postgresql://%s:%s/postgres", databaseHost(), databasePort());
    }

    private static String databaseHost() {
        return property("prestador.db.host", "PRESTADOR_DB_HOST", DEFAULT_HOST);
    }

    private static String databasePort() {
        return property("prestador.db.port", "PRESTADOR_DB_PORT", DEFAULT_PORT);
    }

    private static String databaseName() {
        return property("prestador.db.name", "PRESTADOR_DB_NAME", DEFAULT_DB);
    }

    private static String databaseUser() {
        return property("prestador.db.user", "PRESTADOR_DB_USER", DEFAULT_USER);
    }

    private static String databasePassword() {
        return property("prestador.db.password", "PRESTADOR_DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String property(String sysProp, String envVar, String defaultValue) {
        String value = System.getProperty(sysProp);
        if (value == null || value.isBlank()) {
            value = System.getenv(envVar);
        }
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }
}
