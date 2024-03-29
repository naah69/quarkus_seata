package io.quarkiverse.seata.config.client;

import static io.seata.common.DefaultValues.*;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.LOCK_PREFIX, phase = ConfigPhase.RUN_TIME)
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
