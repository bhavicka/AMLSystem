package com.tss.AmlSystem.config.multitenancy;

import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class TenantConnectionProvider extends AbstractMultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    // 1. This is the method that was missing!
    // It returns the default provider used when no tenant is specified.
    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return new ConnectionProvider() {
            @Override
            public Connection getConnection() throws SQLException {
                return dataSource.getConnection();
            }

            @Override
            public void closeConnection(Connection conn) throws SQLException {
                conn.close();
            }

            @Override
            public boolean supportsAggressiveRelease() { return false; }

            @Override
            public boolean isUnwrappableAs(Class<?> unwrapType) { return false; }

            @Override
            public <T> T unwrap(Class<T> unwrapType) { return null; }
        };
    }

    // 2. This fetches the provider for a specific tenant ID
    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        return getAnyConnectionProvider();
    }

    // 3. This is where the magic happens: switching the schema
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = super.getConnection(tenantIdentifier);
        try (var statement = connection.createStatement()) {
            // For PostgreSQL: switches the search path to your new schema
            statement.execute("SET search_path TO " + tenantIdentifier);
        } catch (SQLException e) {
            throw new SQLException("Could not alter connection to schema [" + tenantIdentifier + "]", e);
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("SET search_path TO public");
        }
        super.releaseConnection(tenantIdentifier, connection);
    }
}
