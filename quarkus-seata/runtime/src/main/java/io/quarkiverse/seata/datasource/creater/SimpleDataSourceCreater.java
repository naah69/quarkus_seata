package io.quarkiverse.seata.datasource.creater;

import java.net.URI;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.agroal.runtime.DataSourceJdbcRuntimeConfig;
import io.quarkus.datasource.runtime.DataSourceRuntimeConfig;

/**
 * DataSourceCreater
 *
 * @author nayan
 * @date 2023/3/2 15:07
 */
public interface SimpleDataSourceCreater {

    Logger LOGGER = LoggerFactory.getLogger(SimpleDataSourceCreater.class);

    default DataSource createObject(DataSourceJdbcRuntimeConfig jdbcConfig, DataSourceRuntimeConfig dsRuntimeConfig) {
        try {
            DataSource dataSource = createObject();
            setJdbcUrl(dataSource, jdbcConfig, dsRuntimeConfig);
            setUsername(dataSource, jdbcConfig, dsRuntimeConfig);
            setPassword(dataSource, jdbcConfig, dsRuntimeConfig);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("jdbc url: {}, username: {}, password:{}", getJdbcUrl(jdbcConfig),
                        getUsername(dsRuntimeConfig), getPassword(dsRuntimeConfig));
            }
            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default DataSource createObject() throws Exception {
        Class simpleDataSourceClass = getSimpleDataSourceClass();
        return (DataSource) simpleDataSourceClass.getDeclaredConstructor().newInstance();
    }

    void setUsername(DataSource dataSource, DataSourceJdbcRuntimeConfig jdbcConfig,
            DataSourceRuntimeConfig dsRuntimeConfig) throws Exception;

    default String getUsername(DataSourceRuntimeConfig dsRuntimeConfig) {
        return dsRuntimeConfig.username.get();
    }

    void setPassword(DataSource dataSource, DataSourceJdbcRuntimeConfig jdbcConfig,
            DataSourceRuntimeConfig dsRuntimeConfig) throws Exception;

    default String getPassword(DataSourceRuntimeConfig dsRuntimeConfig) {
        return dsRuntimeConfig.password.get();
    }

    default String getDatabaseName(DataSourceJdbcRuntimeConfig jdbcConfig) {
        URI jdbcUri = getJdbcUri(jdbcConfig);
        return getDatabaseName(jdbcUri);
    }

    default String getDatabaseName(URI jdbcUri) {
        return jdbcUri.getPath().substring(1).split("\\?")[0];
    }

    void setJdbcUrl(DataSource dataSource, DataSourceJdbcRuntimeConfig jdbcConfig,
            DataSourceRuntimeConfig dsRuntimeConfig) throws Exception;

    default String getJdbcUrl(DataSourceJdbcRuntimeConfig jdbcConfig) {
        return jdbcConfig.url.get();
    }

    default URI getJdbcUri(DataSourceJdbcRuntimeConfig jdbcConfig) {
        return URI.create(getJdbcUrl(jdbcConfig));
    }

    default Class getSimpleDataSourceClass() throws ClassNotFoundException {
        return Class.forName(getSimpleDataSourceClassString());
    }

    String getSimpleDataSourceClassString();

}
