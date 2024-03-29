package io.quarkiverse.seata.config.client;

import static io.seata.common.DefaultValues.DEFAULT_DISABLE_GLOBAL_TRANSACTION;

import java.util.HashMap;
import java.util.Map;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.SERVICE_PREFIX, phase = ConfigPhase.RUN_TIME)
public class SeataServiceConfig {
    /**
     * vgroup->rgroup
     */
    public Map<String, String> vgroupMapping = new HashMap<>();
    /**
     * group list
     */
    public Map<String, String> grouplist = new HashMap<>();
    /**
     * degrade current not support
     */
    @ConfigItem(defaultValue = "false")
    public boolean enableDegrade;
    /**
     * disable globalTransaction todo runtime
     */
    @ConfigItem(defaultValue = DEFAULT_DISABLE_GLOBAL_TRANSACTION + "")
    public boolean disableGlobalTransaction;
}
