package io.quarkiverse.seata.config.core;

import static io.seata.common.DefaultValues.DEFAULT_SHUTDOWN_TIMEOUT_SEC;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.SHUTDOWN_PREFIX, phase = ConfigPhase.RUN_TIME)
public class SeataShutdownConfig {
    /**
     * wait
     */
    @ConfigItem(defaultValue = DEFAULT_SHUTDOWN_TIMEOUT_SEC + "")
    public long wait;
}
