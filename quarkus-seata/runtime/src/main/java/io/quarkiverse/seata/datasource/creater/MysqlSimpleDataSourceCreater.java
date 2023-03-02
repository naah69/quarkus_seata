package io.quarkiverse.seata.datasource.creater;

import javax.sql.DataSource;

import io.quarkus.agroal.runtime.DataSourceJdbcRuntimeConfig;
import io.quarkus.datasource.runtime.DataSourceRuntimeConfig;

/**
 * MysqlSimpleDataSourceCreater
 *
 * @author nayan
 * @date 2023/3/2 15:49
 */
public class MysqlSimpleDataSourceCreater implements SimpleDataSourceCreater {

    private static final MysqlSimpleDataSourceCreater INSTANCE = new MysqlSimpleDataSourceCreater();

    private MysqlSimpleDataSourceCreater() {
    }

    public static MysqlSimpleDataSourceCreater getInstance() {
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
        clazz.getMethod("setURL", String.class).invoke(dataSource, getJdbcUrl(jdbcConfig));

    }

    @Override
    public String getSimpleDataSourceClassString() {
        return "com.mysql.cj.jdbc.MysqlDataSource";
    }

}
