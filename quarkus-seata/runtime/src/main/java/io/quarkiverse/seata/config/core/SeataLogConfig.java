package io.quarkiverse.seata.config.core;

import static io.seata.common.DefaultValues.DEFAULT_LOG_EXCEPTION_RATE;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.LOG_PREFIX, phase = ConfigPhase.RUN_TIME)
public class SeataLogConfig {

    /**
     * exceptionRate
     */
    @ConfigItem(defaultValue = DEFAULT_LOG_EXCEPTION_RATE + "")
    public int exceptionRate;

}
