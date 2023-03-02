package io.quarkiverse.seata.config.core;

import java.util.Optional;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@ConfigRoot(name = StarterConstants.REGISTRY_PREFIX, phase = ConfigPhase.RUN_TIME)
public class SeataRegistryConfig {

    /**
     * type
     */
    @ConfigItem(defaultValue = "file")
    public String type;

    public Optional<String> preferredNetworks;
}
