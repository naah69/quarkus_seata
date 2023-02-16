package io.quarkiverse.seata.jdbc;

import io.quarkus.agroal.runtime.AgroalConnectionConfigurer;
import io.quarkus.agroal.runtime.JdbcDriver;

/**
 * SeataAgroalConnectionConfigurer
 *
 * @author nayan
 * @date 2023/2/16 18:48
 */
@JdbcDriver("seata")
public class SeataAgroalConnectionConfigurer implements AgroalConnectionConfigurer {

}
