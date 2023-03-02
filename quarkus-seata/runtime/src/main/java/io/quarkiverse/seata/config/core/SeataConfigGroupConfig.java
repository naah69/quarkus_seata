package io.quarkiverse.seata.config.core;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.CONFIG_PREFIX, phase = ConfigPhase.RUN_TIME)
public class SeataConfigGroupConfig {
    /**
     * file, nacos, apollo, zk, consul, etcd3, springCloudConfig
     */
    @ConfigItem(defaultValue = "file")
    public String type;

    /**
     * properties,yaml(only type in nacos, zk, consul, etcd3)
     */
    @ConfigItem(defaultValue = "properties")
    public String dataType;

    public SeataConfigGroupFileConfig file;

}
