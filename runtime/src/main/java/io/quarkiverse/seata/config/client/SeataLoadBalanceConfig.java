package io.quarkiverse.seata.config.client;

import static io.seata.common.DefaultValues.DEFAULT_LOAD_BALANCE;
import static io.seata.common.DefaultValues.VIRTUAL_NODES_DEFAULT;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.LOAD_BALANCE_PREFIX_KEBAB_STYLE, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataLoadBalanceConfig {

    /**
     * type
     */
    @ConfigItem(defaultValue = DEFAULT_LOAD_BALANCE)
    public String type;
    /**
     * the load balance virtual nodes
     */
    @ConfigItem(defaultValue = VIRTUAL_NODES_DEFAULT + "")
    public int virtualNodes;

}
