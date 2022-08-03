package io.quarkiverse.seata.runtime;

import static io.quarkiverse.seata.config.StarterConstants.*;

import java.util.List;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agroal.api.AgroalDataSource;
import io.quarkiverse.seata.config.SeataConfig;
import io.quarkiverse.seata.config.client.*;
import io.quarkiverse.seata.config.core.*;
import io.quarkiverse.seata.datasource.DataSourceProxyHolder;
import io.quarkus.agroal.runtime.DataSources;
import io.quarkus.runtime.ApplicationConfig;
import io.quarkus.runtime.annotations.Recorder;
import io.seata.common.util.StringUtils;
import io.seata.core.rpc.ShutdownHook;
import io.seata.core.rpc.netty.RmNettyRemotingClient;
import io.seata.core.rpc.netty.TmNettyRemotingClient;
import io.seata.rm.RMClient;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.tm.TMClient;

/**
 * SeataRecorder
 *
 * @author nayan
 * @date 2022/7/27 7:36 PM
 */
@Recorder
public class SeataRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeataRecorder.class);

    public Supplier<DataSource> seataDataSourceProxySupplier(List<String> dataSources) {
        return () -> {
            //todo 目前仅支持at模式代理，未支持xa模式
            AgroalDataSource datasource = DataSources.fromName(dataSources.get(0));
            DataSourceProxy dataSourceProxy = new DataSourceProxy(datasource);
            DataSourceProxyHolder.put(datasource, dataSourceProxy);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("seata proxy datasource by jdk dynamic proxy");
            }
            return dataSourceProxy;
        };
    }

    public void initPropertyMap(SeataConfig config,
            SeataConfigGroupConfig configGroupConfig,
            SeataRegistryConfig registryConfig,
            SeataThreadFactoryConfig threadFactoryConfig,
            SeataTransportConfig transportConfig,
            SeataShutdownConfig shutdownConfig,
            SeataLogConfig logConfig,
            SeataRmConfig rmConfig,
            SeataTmConfig tmConfig,
            SeataLockConfig lockConfig,
            SeataServiceConfig serviceConfig,
            SeataUndoConfig undoConfig,
            SeataUndoCompressConfig undoCompressConfig,
            SeataLoadBalanceConfig loadBalanceConfig) {

        PROPERTY_BEAN_INSTANCE_MAP.put(SEATA_PREFIX, config);
        PROPERTY_BEAN_MAP.put(SEATA_PREFIX, config.getClass());

        //        core
        PROPERTY_BEAN_INSTANCE_MAP.put(CONFIG_PREFIX, configGroupConfig);
        PROPERTY_BEAN_MAP.put(CONFIG_PREFIX, configGroupConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(CONFIG_FILE_PREFIX, configGroupConfig.file);
        PROPERTY_BEAN_MAP.put(CONFIG_FILE_PREFIX, configGroupConfig.file.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(REGISTRY_PREFIX, registryConfig);
        PROPERTY_BEAN_MAP.put(REGISTRY_PREFIX, registryConfig.getClass());
        //        todo support other config
        //        PROPERTY_BEAN_MAP.put(CONFIG_NACOS_PREFIX, ConfigNacosProperties.class);
        //        PROPERTY_BEAN_MAP.put(CONFIG_CONSUL_PREFIX, ConfigConsulProperties.class);
        //        PROPERTY_BEAN_MAP.put(CONFIG_ZK_PREFIX, ConfigZooKeeperProperties.class);
        //        PROPERTY_BEAN_MAP.put(CONFIG_APOLLO_PREFIX, ConfigApolloProperties.class);
        //        PROPERTY_BEAN_MAP.put(CONFIG_ETCD3_PREFIX, ConfigEtcd3Properties.class);
        //        PROPERTY_BEAN_MAP.put(CONFIG_CUSTOM_PREFIX, ConfigCustomProperties.class);
        //        todo support other registry
        //        PROPERTY_BEAN_MAP.put(REGISTRY_CONSUL_PREFIX, RegistryConsulProperties.class);
        //        PROPERTY_BEAN_MAP.put(REGISTRY_ETCD3_PREFIX, RegistryEtcd3Properties.class);
        //        PROPERTY_BEAN_MAP.put(REGISTRY_EUREKA_PREFIX, RegistryEurekaProperties.class);
        //        PROPERTY_BEAN_MAP.put(REGISTRY_NACOS_PREFIX, RegistryNacosProperties.class);
        //        PROPERTY_BEAN_MAP.put(REGISTRY_REDIS_PREFIX, RegistryRedisProperties.class);
        //        PROPERTY_BEAN_MAP.put(REGISTRY_SOFA_PREFIX, RegistrySofaProperties.class);
        //        PROPERTY_BEAN_MAP.put(REGISTRY_ZK_PREFIX, RegistryZooKeeperProperties.class);
        //        PROPERTY_BEAN_MAP.put(REGISTRY_CUSTOM_PREFIX, RegistryCustomProperties.class);

        PROPERTY_BEAN_INSTANCE_MAP.put(THREAD_FACTORY_PREFIX, threadFactoryConfig);
        PROPERTY_BEAN_MAP.put(THREAD_FACTORY_PREFIX, threadFactoryConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(TRANSPORT_PREFIX, transportConfig);
        PROPERTY_BEAN_MAP.put(TRANSPORT_PREFIX, transportConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(SHUTDOWN_PREFIX, shutdownConfig);
        PROPERTY_BEAN_MAP.put(SHUTDOWN_PREFIX, shutdownConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(LOG_PREFIX, logConfig);
        PROPERTY_BEAN_MAP.put(LOG_PREFIX, logConfig.getClass());

        //        client
        PROPERTY_BEAN_INSTANCE_MAP.put(CLIENT_RM_PREFIX, rmConfig);
        PROPERTY_BEAN_MAP.put(CLIENT_RM_PREFIX, rmConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(CLIENT_TM_PREFIX, tmConfig);
        PROPERTY_BEAN_MAP.put(CLIENT_TM_PREFIX, tmConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(LOCK_PREFIX, lockConfig);
        PROPERTY_BEAN_MAP.put(LOCK_PREFIX, lockConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(SERVICE_PREFIX, serviceConfig);
        PROPERTY_BEAN_MAP.put(SERVICE_PREFIX, serviceConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(UNDO_PREFIX, undoConfig);
        PROPERTY_BEAN_MAP.put(UNDO_PREFIX, undoConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(COMPRESS_PREFIX, undoCompressConfig);
        PROPERTY_BEAN_MAP.put(COMPRESS_PREFIX, undoCompressConfig.getClass());
        PROPERTY_BEAN_INSTANCE_MAP.put(LOAD_BALANCE_PREFIX, loadBalanceConfig);
        PROPERTY_BEAN_MAP.put(LOAD_BALANCE_PREFIX, loadBalanceConfig.getClass());
        //        PROPERTY_BEAN_MAP.put(TCC_FENCE_PREFIX, TCCFenceConfig.class);
        //        PROPERTY_BEAN_MAP.put(SAGA_STATE_MACHINE_PREFIX, StateMachineConfig.class);
        //        PROPERTY_BEAN_MAP.put(SAGA_ASYNC_THREAD_POOL_PREFIX, SagaAsyncThreadPoolProperties.class);
    }

    public void initClient(SeataConfig config, ApplicationConfig applicationConfig) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Initializing Global Transaction Clients ... ");
        }
        String applicationName = applicationConfig.name.get();

        String txServiceGroup = config.txServiceGroup;
        if (StringUtils.isNullOrEmpty(applicationName) || StringUtils.isNullOrEmpty(txServiceGroup)) {
            throw new IllegalArgumentException(
                    String.format("applicationName: %s, txServiceGroup: %s", applicationName, txServiceGroup));
        }
        //init TM
        TMClient.init(applicationName, txServiceGroup, config.accessKey.orElse(null), config.secretKey.orElse(null));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Transaction Manager Client is initialized. applicationName[{}] txServiceGroup[{}]", applicationName,
                    txServiceGroup);
        }
        //init RM
        RMClient.init(applicationName, txServiceGroup);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Resource Manager is initialized. applicationName[{}] txServiceGroup[{}]", applicationName,
                    txServiceGroup);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Global Transaction Clients are initialized. ");
        }
        registerSpringShutdownHook(applicationName, txServiceGroup);
    }

    private void registerSpringShutdownHook(String applicationName, String txServiceGroup) {
        ShutdownHook.getInstance().addDisposable(TmNettyRemotingClient.getInstance(applicationName, txServiceGroup));
        ShutdownHook.getInstance().addDisposable(RmNettyRemotingClient.getInstance(applicationName, txServiceGroup));
    }

}
