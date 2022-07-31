package io.quarkiverse.seata.config.client;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.seata.common.DefaultValues;

@ConfigRoot(name = StarterConstants.COMPRESS_PREFIX, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataUndoCompressConfig {
    /**
     * enable
     */
    @ConfigItem(defaultValue = DefaultValues.DEFAULT_CLIENT_UNDO_COMPRESS_ENABLE + "")
    public boolean enable;
    /**
     * type
     */
    @ConfigItem(defaultValue = DefaultValues.DEFAULT_CLIENT_UNDO_COMPRESS_TYPE)
    public String type;

    /**
     * threshold
     */
    @ConfigItem(defaultValue = DefaultValues.DEFAULT_CLIENT_UNDO_COMPRESS_THRESHOLD)
    public String threshold;
}
