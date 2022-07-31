package io.quarkiverse.seata.config.client;

import static io.seata.common.DefaultValues.*;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.seata.sqlparser.SqlParserType;

@ConfigRoot(name = StarterConstants.CLIENT_RM_PREFIX, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataRmConfig {
    /**
     * asyncCommitBufferLimit
     */
    @ConfigItem(defaultValue = DEFAULT_CLIENT_ASYNC_COMMIT_BUFFER_LIMIT + "")
    public int asyncCommitBufferLimit;
    /**
     * reportRetryCount
     */
    @ConfigItem(defaultValue = DEFAULT_CLIENT_REPORT_RETRY_COUNT + "")
    public int reportRetryCount;
    /**
     * tableMetaCheckEnable
     */
    @ConfigItem(defaultValue = DEFAULT_CLIENT_TABLE_META_CHECK_ENABLE + "")
    public boolean tableMetaCheckEnable;
    /**
     * tableMetaCheckerInterval
     */
    @ConfigItem(defaultValue = DEFAULT_TABLE_META_CHECKER_INTERVAL + "")
    public long tableMetaCheckerInterval;
    /**
     * reportSuccessEnable
     */
    @ConfigItem(defaultValue = DEFAULT_CLIENT_REPORT_SUCCESS_ENABLE + "")
    public boolean reportSuccessEnable;
    //    public boolean sagaBranchRegisterEnable = DEFAULT_CLIENT_SAGA_BRANCH_REGISTER_ENABLE;
    //    public String sagaJsonParser = DEFAULT_SAGA_JSON_PARSER;
    //    public boolean sagaRetryPersistModeUpdate = DEFAULT_CLIENT_SAGA_RETRY_PERSIST_MODE_UPDATE;
    //    public boolean sagaCompensatePersistModeUpdate = DEFAULT_CLIENT_SAGA_COMPENSATE_PERSIST_MODE_UPDATE;
    //    public int tccActionInterceptorOrder = TCC_ACTION_INTERCEPTOR_ORDER;
    //    public int branchExecutionTimeoutXA = DEFAULT_XA_BRANCH_EXECUTION_TIMEOUT;
    //    public int connectionTwoPhaseHoldTimeoutXA = DEFAULT_XA_CONNECTION_TWO_PHASE_HOLD_TIMEOUT;
    /**
     * sqlParserType
     */
    @ConfigItem(defaultValue = SqlParserType.SQL_PARSER_TYPE_DRUID)
    public String sqlParserType;

}
