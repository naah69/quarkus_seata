package io.quarkiverse.seata.runtime;

import static io.quarkiverse.seata.config.StarterConstants.*;

import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;
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

    @Inject
    public ApplicationConfig applicationConfig;

    public Supplier<DataSource> seataDataSourceSupplier(List<String> dataSources) {
        return () -> {
            //todo 目前仅支持at模式代理，未支持xa模式
            AgroalDataSource datasource = DataSources.fromName(dataSources.get(0));
            DataSourceProxy dataSourceProxy = new DataSourceProxy(datasource);
            DataSourceProxyHolder.put(datasource, dataSourceProxy);
            return dataSourceProxy;
        };
    }

    public void initPropertyMap() {

        //        core
        PROPERTY_BEAN_MAP.put(CONFIG_PREFIX, SeataConfigGroupConfig.class);
        PROPERTY_BEAN_MAP.put(CONFIG_FILE_PREFIX, SeataConfigGroupConfig.SeataConfigGroupFileConfig.class);
        PROPERTY_BEAN_MAP.put(REGISTRY_PREFIX, SeataRegistryConfig.class);
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

        PROPERTY_BEAN_MAP.put(THREAD_FACTORY_PREFIX, SeataThreadFactoryConfig.class);
        PROPERTY_BEAN_MAP.put(TRANSPORT_PREFIX, SeataTransportConfig.class);
        PROPERTY_BEAN_MAP.put(SHUTDOWN_PREFIX, SeataShutdownConfig.class);
        PROPERTY_BEAN_MAP.put(LOG_PREFIX, SeataLogConfig.class);

        //        client
        PROPERTY_BEAN_MAP.put(SEATA_PREFIX, SeataConfig.class);

        PROPERTY_BEAN_MAP.put(CLIENT_RM_PREFIX, SeataRmConfig.class);
        PROPERTY_BEAN_MAP.put(CLIENT_TM_PREFIX, SeataTmConfig.class);
        PROPERTY_BEAN_MAP.put(LOCK_PREFIX, SeataLockConfig.class);
        PROPERTY_BEAN_MAP.put(SERVICE_PREFIX, SeataServiceConfig.class);
        PROPERTY_BEAN_MAP.put(UNDO_PREFIX, SeataUndoConfig.class);
        PROPERTY_BEAN_MAP.put(COMPRESS_PREFIX, SeataUndoCompressConfig.class);
        PROPERTY_BEAN_MAP.put(LOAD_BALANCE_PREFIX, SeataLoadBalanceConfig.class);
        //        PROPERTY_BEAN_MAP.put(TCC_FENCE_PREFIX, TCCFenceConfig.class);
        //        PROPERTY_BEAN_MAP.put(SAGA_STATE_MACHINE_PREFIX, StateMachineConfig.class);
        //        PROPERTY_BEAN_MAP.put(SAGA_ASYNC_THREAD_POOL_PREFIX, SagaAsyncThreadPoolProperties.class);
    }

    public void initClient(SeataConfig config) {
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
