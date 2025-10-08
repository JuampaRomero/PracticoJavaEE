package PrestadorSalud.config;

import jakarta.annotation.sql.DataSourceDefinition;

@DataSourceDefinition(
        name = "java:/jdbc/PrestadorDS",
        className = "org.postgresql.ds.PGSimpleDataSource",
        databaseName = "prestador_db",
        serverName = "localhost",
        portNumber = 5432,
        user = "postgres",
        password = "postgres",
        properties = {
                "sslmode=disable"
        }
)
public class PostgresDataSourceConfig {
    // La definición permite que el contenedor cree el DataSource requerido por la unidad de persistencia.
}
