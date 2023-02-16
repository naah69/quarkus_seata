package io.quarkiverse.seata.jdbc.driver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.enterprise.inject.literal.NamedLiteral;
import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

import io.agroal.api.AgroalDataSource;
import io.quarkiverse.seata.datasource.DataSourceProxyHolder;
import io.quarkus.arc.Arc;
import io.seata.rm.datasource.DataSourceProxy;

/**
 * SeataDriver
 *
 * @author nayan
 * @date 2023/2/16 18:28
 */
public class SeataDriver implements Driver {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SeataDriver.class);
    private static final int MAJOR_DRIVER_VERSION = 5;

    private static final int MINOR_DRIVER_VERSION = 1;

    private static final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();
    public static final String JDBC_SEATA_PREFIX = "jdbc:seata:";

    static {
        try {
            DriverManager.registerDriver(new SeataDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return acceptsURL(url) ? getDataSource(url).getConnection() : null;
    }

    private DataSource getDataSource(String url) {
        return dataSourceMap.computeIfAbsent(url, this::getAgroalDataSource);
    }

    private DataSourceProxy getAgroalDataSource(String url) {
        String dataSourceName = url.substring(JDBC_SEATA_PREFIX.length(), url.contains("?") ? url.indexOf("?") : url.length());
        AgroalDataSource agroalDataSource = Arc.container()
                .instance(AgroalDataSource.class, NamedLiteral.of(dataSourceName))
                .get();
        DataSourceProxy dataSourceProxy = new DataSourceProxy(agroalDataSource);
        DataSourceProxyHolder.put(agroalDataSource, dataSourceProxy);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("seata proxy datasource by jdk dynamic proxy");
        }
        return dataSourceProxy;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return null != url && url.startsWith(JDBC_SEATA_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return MAJOR_DRIVER_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return MINOR_DRIVER_VERSION;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

}
