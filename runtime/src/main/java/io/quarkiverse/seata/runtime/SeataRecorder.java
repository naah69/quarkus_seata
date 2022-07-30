package io.quarkiverse.seata.runtime;

import io.agroal.api.AgroalDataSource;
import io.quarkiverse.seata.config.SeataConfig;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.List;
import java.util.function.Supplier;


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

    public void initClient(SeataConfig config){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Initializing Global Transaction Clients ... ");
        }
        String applicationName = applicationConfig.name.get();

        if (StringUtils.isNullOrEmpty(applicationName) || StringUtils.isNullOrEmpty(txServiceGroup)) {
            throw new IllegalArgumentException(String.format("applicationName: %s, txServiceGroup: %s", applicationName, txServiceGroup));
        }
        //init TM
        TMClient.init(applicationName, txServiceGroup, accessKey, secretKey);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Transaction Manager Client is initialized. applicationName[{}] txServiceGroup[{}]", applicationName, txServiceGroup);
        }
        //init RM
        RMClient.init(applicationName, txServiceGroup);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Resource Manager is initialized. applicationName[{}] txServiceGroup[{}]", applicationName, txServiceGroup);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Global Transaction Clients are initialized. ");
        }
        registerSpringShutdownHook(applicationName,txServiceGroup);
    }

    private void registerSpringShutdownHook(String applicationName,String txServiceGroup) {
        ShutdownHook.getInstance().addDisposable(TmNettyRemotingClient.getInstance(applicationName, txServiceGroup));
        ShutdownHook.getInstance().addDisposable(RmNettyRemotingClient.getInstance(applicationName, txServiceGroup));
    }



}
