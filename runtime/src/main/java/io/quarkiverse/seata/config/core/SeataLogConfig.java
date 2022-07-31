package io.quarkiverse.seata.config.core;

import static io.seata.common.DefaultValues.DEFAULT_LOG_EXCEPTION_RATE;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = StarterConstants.LOG_PREFIX, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataLogConfig {

    /**
     * exceptionRate
     */
    @ConfigItem(defaultValue = DEFAULT_LOG_EXCEPTION_RATE + "")
    public int exceptionRate;

}
