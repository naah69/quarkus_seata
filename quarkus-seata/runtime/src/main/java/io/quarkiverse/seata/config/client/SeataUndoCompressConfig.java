package io.quarkiverse.seata.config.client;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.seata.common.DefaultValues;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.COMPRESS_PREFIX, phase = ConfigPhase.RUN_TIME)
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
