package io.quarkiverse.seata.config.core;

import static io.seata.common.DefaultValues.*;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = StarterConstants.THREAD_FACTORY_PREFIX_KEBAB_STYLE, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataThreadFactoryConfig {
    /**
     * bossThreadPrefix
     */
    @ConfigItem(defaultValue = DEFAULT_BOSS_THREAD_PREFIX)
    public String bossThreadPrefix;

    /**
     * workerThreadPrefix
     */
    @ConfigItem(defaultValue = DEFAULT_NIO_WORKER_THREAD_PREFIX)
    public String workerThreadPrefix;

    /**
     * serverExecutorThreadPrefix
     */
    @ConfigItem(defaultValue = DEFAULT_EXECUTOR_THREAD_PREFIX)
    public String serverExecutorThreadPrefix;

    /**
     * shareBossWorker
     */
    @ConfigItem(defaultValue = "false")
    public boolean shareBossWorker;

    /**
     * clientSelectorThreadPrefix
     */
    @ConfigItem(defaultValue = DEFAULT_SELECTOR_THREAD_PREFIX)
    public String clientSelectorThreadPrefix;

    /**
     * clientSelectorThreadSize
     */
    @ConfigItem(defaultValue = DEFAULT_SELECTOR_THREAD_SIZE + "")
    public int clientSelectorThreadSize;

    /**
     * clientWorkerThreadPrefix
     */
    @ConfigItem(defaultValue = DEFAULT_WORKER_THREAD_PREFIX)
    public String clientWorkerThreadPrefix;

    /**
     * netty boss thread size
     */
    @ConfigItem(defaultValue = DEFAULT_BOSS_THREAD_SIZE + "")
    public int bossThreadSize;
    /**
     * auto default pin or 8
     * todo fix NettyBaseConfig.WorkThreadMode.Default.name() is not support on field annotation
     */
    @ConfigItem(defaultValue = "8")
    public String workerThreadSize;
}
