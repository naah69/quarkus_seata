package io.quarkiverse.seata.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.seata.common.DefaultValues;

/**
 * SeataConfig
 *
 * @author nayan
 * @date 2022/7/30 10:30 AM
 */
@ConfigRoot(prefix = StarterConstants.SEATA_PREFIX, name = "", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataConfig {
    /**
     * whether enable auto configuration
     */
    @ConfigItem(defaultValue = "true")
    public boolean enabled;

    /**
     * transaction service group
     */
    @ConfigItem(defaultValue = DefaultValues.DEFAULT_TX_GROUP)
    public String txServiceGroup;
    /**
     * Whether enable auto proxying of datasource bean
     */
    @ConfigItem(defaultValue = "true")
    public boolean enableAutoDataSourceProxy;
    /**
     * data source proxy mode
     */
    @ConfigItem(defaultValue = DefaultValues.DEFAULT_DATA_SOURCE_PROXY_MODE)
    public String dataSourceProxyMode;
    /**
     * Whether use JDK proxy instead of CGLIB proxy
     */
    @ConfigItem(defaultValue = "true")
    public boolean useJdkProxy;
    /**
     * The scan packages. If empty, will scan all beans.
     */
    public Optional<String[]> scanPackages;
    /**
     * Specifies beans that won't be scanned in the GlobalTransactionScanner
     */
    public Optional<String[]> excludesForScanning;
    /**
     * Specifies which datasource bean are not eligible for auto-proxying
     */
    public Optional<String[]> excludesForAutoProxying;

    /**
     * used for aliyun accessKey
     */
    public Optional<String> accessKey;

    /**
     * used for aliyun secretKey
     */
    public Optional<String> secretKey;
}
