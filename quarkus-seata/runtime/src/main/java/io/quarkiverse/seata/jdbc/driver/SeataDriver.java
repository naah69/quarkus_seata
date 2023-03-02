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

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

import io.quarkiverse.seata.datasource.DataSourceProxyHolder;
import io.quarkiverse.seata.datasource.creater.SimpleDataSourceFactory;
import io.quarkus.agroal.runtime.DataSourceJdbcRuntimeConfig;
import io.quarkus.agroal.runtime.DataSourcesJdbcRuntimeConfig;
import io.quarkus.arc.Arc;
import io.quarkus.datasource.runtime.DataSourceBuildTimeConfig;
import io.quarkus.datasource.runtime.DataSourceRuntimeConfig;
import io.quarkus.datasource.runtime.DataSourcesBuildTimeConfig;
import io.quarkus.datasource.runtime.DataSourcesRuntimeConfig;
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
        return dataSourceMap.computeIfAbsent(url, this::buildDataSource);
    }

    private DataSourceProxy buildDataSource(String url) {
        String dataSourceName = getDataSourceName(url);
        DataSourceJdbcRuntimeConfig jdbcConfig = Arc.container()
                .instance(DataSourcesJdbcRuntimeConfig.class)
                .get().namedDataSources.get(dataSourceName).jdbc;
        DataSourceBuildTimeConfig dsBuildtimeConfig = Arc.container()
                .instance(DataSourcesBuildTimeConfig.class)
                .get().namedDataSources.get(dataSourceName);
        DataSourceRuntimeConfig dsRuntimeConfig = Arc.container()
                .instance(DataSourcesRuntimeConfig.class)
                .get().namedDataSources.get(dataSourceName);
        DataSource dataSource = SimpleDataSourceFactory.createDataSource(jdbcConfig, dsBuildtimeConfig, dsRuntimeConfig);

        DataSourceProxy dataSourceProxy = new DataSourceProxy(dataSource);
        DataSourceProxyHolder.put(dataSource, dataSourceProxy);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("seata proxy datasource by jdk dynamic proxy");
        }
        return dataSourceProxy;
    }

    private static String getDataSourceName(String url) {
        return url.substring(JDBC_SEATA_PREFIX.length(), url.contains("?") ? url.indexOf("?") : url.length());
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
