package io.quarkiverse.seata.config.core;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SeataConfigGroupFileConfig {
    /**
     * name
     */
    @ConfigItem(defaultValue = "file.conf")
    String name;
}