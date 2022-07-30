package io.quarkiverse.seata.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * SeataConfig
 *
 * @author nayan
 * @date 2022/7/30 10:30 AM
 */
@ConfigRoot(prefix = "seata", name = "", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataConfig {
}
