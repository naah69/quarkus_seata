package io.quarkiverse.seata.config.client;

import static io.seata.common.DefaultValues.*;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.CLIENT_TM_PREFIX, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataTmConfig {

    /**
     * commitRetryCount
     */
    @ConfigItem(defaultValue = DEFAULT_TM_COMMIT_RETRY_COUNT + "")
    public int commitRetryCount;
    /**
     * rollbackRetryCount
     */
    @ConfigItem(defaultValue = DEFAULT_TM_ROLLBACK_RETRY_COUNT + "")
    public int rollbackRetryCount;
    /**
     * defaultGlobalTransactionTimeout
     */
    @ConfigItem(defaultValue = DEFAULT_GLOBAL_TRANSACTION_TIMEOUT + "")
    public int defaultGlobalTransactionTimeout;
    /**
     * degradeCheck
     */
    @ConfigItem(defaultValue = DEFAULT_TM_DEGRADE_CHECK + "")
    public boolean degradeCheck;
    /**
     * degradeCheckAllowTimes
     */
    @ConfigItem(defaultValue = DEFAULT_TM_DEGRADE_CHECK_ALLOW_TIMES + "")
    public int degradeCheckAllowTimes;
    /**
     * degradeCheckPeriod
     */
    @ConfigItem(defaultValue = DEFAULT_TM_DEGRADE_CHECK_PERIOD + "")
    public int degradeCheckPeriod;
    /**
     * interceptorOrder
     */
    @ConfigItem(defaultValue = TM_INTERCEPTOR_ORDER + "")
    public int interceptorOrder;

}
