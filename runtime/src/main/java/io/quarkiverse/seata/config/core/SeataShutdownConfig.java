package io.quarkiverse.seata.config.core;

import static io.seata.common.DefaultValues.DEFAULT_SHUTDOWN_TIMEOUT_SEC;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = StarterConstants.SHUTDOWN_PREFIX, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataShutdownConfig {
    /**
     * wait
     */
    @ConfigItem(defaultValue = DEFAULT_SHUTDOWN_TIMEOUT_SEC + "")
    public long wait;
}
