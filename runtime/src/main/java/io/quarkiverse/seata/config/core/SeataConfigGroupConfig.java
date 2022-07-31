package io.quarkiverse.seata.config.core;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = StarterConstants.CONFIG_PREFIX, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataConfigGroupConfig {
    /**
     * file, nacos, apollo, zk, consul, etcd3, springCloudConfig
     */
    @ConfigItem(defaultValue = "file")
    public String type;

    /**
     * properties,yaml(only type in nacos, zk, consul, etcd3)
     */
    @ConfigItem(defaultValue = "yaml")
    public String dataType;

    public SeataConfigGroupFileConfig file;

    @ConfigGroup
    public class SeataConfigGroupFileConfig {
        /**
         * name
         */
        @ConfigItem(defaultValue = "file.conf")
        String name;
    }
}
