package io.quarkiverse.seata.datasource.creater;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import io.quarkus.agroal.runtime.DataSourceJdbcRuntimeConfig;
import io.quarkus.datasource.runtime.DataSourceBuildTimeConfig;
import io.quarkus.datasource.runtime.DataSourceRuntimeConfig;

/**
 * SimpleDataSourceFactory
 *
 * @author nayan
 * @date 2023/3/2 14:51
 */
public class SimpleDataSourceFactory {

    private static final Map<String, SimpleDataSourceCreater> DB_SIMPLE_DATASOURCE_CREATER_MAPPING = new ConcurrentHashMap<>();

    static {
        setSimpleDataSourceCreater("mysql", MysqlSimpleDataSourceCreater.getInstance());
        setSimpleDataSourceCreater("postgresql", PostgreSqlSimpleDataSourceCreater.getInstance());
    }

    public static DataSource createDataSource(
            DataSourceJdbcRuntimeConfig jdbcRuntimeConfig,
            DataSourceBuildTimeConfig dsBuildtimeConfig,
            DataSourceRuntimeConfig dsRuntimeConfig) {

        return getSimpleDataSourceCreater(dsBuildtimeConfig.dbKind.get()).createObject(jdbcRuntimeConfig,
                dsRuntimeConfig);
    }

    public static void setSimpleDataSourceCreater(String dbType, SimpleDataSourceCreater creater) {
        DB_SIMPLE_DATASOURCE_CREATER_MAPPING.put(dbType, creater);
    }

    public static SimpleDataSourceCreater getSimpleDataSourceCreater(String dbType) {
        return DB_SIMPLE_DATASOURCE_CREATER_MAPPING.get(dbType);
    }

}
