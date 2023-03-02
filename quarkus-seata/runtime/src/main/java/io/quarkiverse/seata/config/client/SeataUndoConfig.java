package io.quarkiverse.seata.config.client;

import static io.seata.common.DefaultValues.*;
import static io.seata.common.DefaultValues.DEFAULT_ONLY_CARE_UPDATE_COLUMNS;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.UNDO_PREFIX, phase = ConfigPhase.RUN_TIME)
public class SeataUndoConfig {
    /**
     * dataValidation
     */
    @ConfigItem(defaultValue = DEFAULT_TRANSACTION_UNDO_DATA_VALIDATION + "")
    public boolean dataValidation;
    /**
     * logSerialization
     */
    @ConfigItem(defaultValue = DEFAULT_TRANSACTION_UNDO_LOG_SERIALIZATION)
    public String logSerialization;
    /**
     * logTable
     */
    @ConfigItem(defaultValue = DEFAULT_TRANSACTION_UNDO_LOG_TABLE)
    public String logTable;
    /**
     * onlyCareUpdateColumns
     */
    @ConfigItem(defaultValue = DEFAULT_ONLY_CARE_UPDATE_COLUMNS + "")
    public boolean onlyCareUpdateColumns;
}
