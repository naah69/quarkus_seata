package io.quarkiverse.seata.config.core;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigGroup
public class SeataConfigGroupFileConfig {
    /**
     * name
     */
    @ConfigItem(defaultValue = "file.conf")
    public String name;
}