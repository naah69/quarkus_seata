package io.quarkiverse.seata.datasource.creater;

import java.net.URI;

import javax.sql.DataSource;

import io.quarkus.agroal.runtime.DataSourceJdbcRuntimeConfig;
import io.quarkus.datasource.runtime.DataSourceRuntimeConfig;

/**
 * PostgreSqlSimpleDataSourceCreater
 *
 * @author nayan
 * @date 2023/3/2 15:09
 */
public class PostgreSqlSimpleDataSourceCreater implements SimpleDataSourceCreater {

    private static final PostgreSqlSimpleDataSourceCreater INSTANCE = new PostgreSqlSimpleDataSourceCreater();

    private PostgreSqlSimpleDataSourceCreater() {
    }

    public static PostgreSqlSimpleDataSourceCreater getInstance() {
        return INSTANCE;
    }

    @Override
    public void setUsername(DataSource dataSource, DataSourceJdbcRuntimeConfig jdbcConfig,
            DataSourceRuntimeConfig dsRuntimeConfig) throws Exception {
        Class<? extends DataSource> clazz = dataSource.getClass();
        clazz.getMethod("setUser", String.class).invoke(dataSource, getUsername(dsRuntimeConfig));

    }

    @Override
    public void setPassword(DataSource dataSource, DataSourceJdbcRuntimeConfig jdbcConfig,
            DataSourceRuntimeConfig dsRuntimeConfig) throws Exception {
        Class<? extends DataSource> clazz = dataSource.getClass();
        clazz.getMethod("setPassword", String.class).invoke(dataSource, getPassword(dsRuntimeConfig));

    }

    @Override
    public void setJdbcUrl(DataSource dataSource, DataSourceJdbcRuntimeConfig jdbcConfig,
            DataSourceRuntimeConfig dsRuntimeConfig) throws Exception {
        Class<? extends DataSource> clazz = dataSource.getClass();
        URI jdbcUri = getJdbcUri(jdbcConfig);
        clazz.getMethod("setServerName", String.class).invoke(dataSource, jdbcUri.getAuthority());
        clazz.getMethod("setDatabaseName", String.class).invoke(dataSource, getDatabaseName(jdbcUri));
    }

    @Override
    public String getSimpleDataSourceClassString() {
        return "org.postgresql.ds.PGSimpleDataSource";
    }

    @Override
    public String getJdbcUrl(DataSourceJdbcRuntimeConfig jdbcConfig) {
        String jdbcUrl = jdbcConfig.url.get();
        return jdbcUrl.substring(jdbcUrl.indexOf(":") + 1);
    }

}
