package io.quarkiverse.seata.config.client;

import static io.seata.common.DefaultValues.*;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = StarterConstants.LOCK_PREFIX, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataLockConfig {
    /**
     * retryInterval
     */
    @ConfigItem(defaultValue = DEFAULT_CLIENT_LOCK_RETRY_INTERVAL + "")
    public int retryInterval;
    /**
     * retryTimes
     */
    @ConfigItem(defaultValue = DEFAULT_CLIENT_LOCK_RETRY_TIMES + "")
    public int retryTimes;
    /**
     * retryPolicyBranchRollbackOnConflict
     */
    @ConfigItem(defaultValue = DEFAULT_CLIENT_LOCK_RETRY_POLICY_BRANCH_ROLLBACK_ON_CONFLICT + "")
    public boolean retryPolicyBranchRollbackOnConflict;

}
