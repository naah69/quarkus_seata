package io.quarkiverse.seata.deployment;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.DotName;

import io.quarkiverse.seata.annotation.GlobalTransactional;
import io.quarkiverse.seata.config.SeataConfig;
import io.quarkiverse.seata.config.client.SeataLoadBalanceConfig;
import io.quarkiverse.seata.config.client.SeataLockConfig;
import io.quarkiverse.seata.config.client.SeataRmConfig;
import io.quarkiverse.seata.config.client.SeataServiceConfig;
import io.quarkiverse.seata.config.client.SeataTmConfig;
import io.quarkiverse.seata.config.client.SeataUndoCompressConfig;
import io.quarkiverse.seata.config.client.SeataUndoConfig;
import io.quarkiverse.seata.config.core.SeataConfigGroupConfig;
import io.quarkiverse.seata.config.core.SeataLogConfig;
import io.quarkiverse.seata.config.core.SeataRegistryConfig;
import io.quarkiverse.seata.config.core.SeataShutdownConfig;
import io.quarkiverse.seata.config.core.SeataThreadFactoryConfig;
import io.quarkiverse.seata.config.core.SeataTransportConfig;
import io.quarkiverse.seata.filter.SeataXIDClientRequestFilter;
import io.quarkiverse.seata.filter.SeataXIDResteasyFilter;
import io.quarkiverse.seata.interceptor.GlobalTransactionalInterceptor;
import io.quarkiverse.seata.runtime.SeataRecorder;
import io.quarkus.agroal.spi.JdbcDataSourceBuildItem;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.InterceptorBindingRegistrarBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.InterceptorBindingRegistrar;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerRequestFilterBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerResponseFilterBuildItem;
import io.quarkus.runtime.ApplicationConfig;
import io.seata.rm.datasource.DataSourceProxy;

class SeataProcessor {

    private static final String FEATURE = "seata";
    private static final DotName DATA_SOURCE = DotName.createSimple(javax.sql.DataSource.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void initSeata(SeataRecorder recorder,
            SeataConfig config,
            ApplicationConfig applicationConfig,
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
        if (config.enabled) {
            recorder.initPropertyMap(config,
                    configGroupConfig,
                    registryConfig,
                    threadFactoryConfig,
                    transportConfig,
                    shutdownConfig,
                    logConfig,
                    rmConfig,
                    tmConfig,
                    lockConfig,
                    serviceConfig,
                    undoConfig,
                    undoCompressConfig,
                    loadBalanceConfig);

            recorder.initClient(config, applicationConfig);
        }
    }

    /**
     * proxy datasource
     *
     * @param recorder
     * @param dataSourceBuildItems
     * @param syntheticBeanBuildItemBuildProducer
     */
    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void generateSeataProxyDataSource(Capabilities capabilities,
            SeataRecorder recorder,
            List<JdbcDataSourceBuildItem> dataSourceBuildItems,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            SeataConfig config) {
        if (config.enabled && config.enableAutoDataSourceProxy && capabilities.isPresent(Capability.AGROAL)) {
            List<String> dataSources = dataSourceBuildItems.stream().filter(ds -> !ds.isDefault()).map(ds -> ds.getName())
                    .collect(Collectors.toList());
            SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
                    .configure(DataSourceProxy.class).name("seata").addType(DATA_SOURCE).scope(Singleton.class)
                    .priority(Integer.MAX_VALUE)
                    .setRuntimeInit().unremovable().supplier(recorder.seataDataSourceProxySupplier(dataSources));

            syntheticBeanBuildItemBuildProducer.produce(configurator.done());

        }

    }

    @BuildStep
    void generateSeataProxyDataSource2(Capabilities capabilities,
            BuildProducer<JdbcDataSourceBuildItem> jdbcDataSourceBuildItemBuildProducer,
            SeataConfig config) {
        if (config.enabled && config.enableAutoDataSourceProxy && capabilities.isPresent(Capability.AGROAL)) {
            jdbcDataSourceBuildItemBuildProducer
                    .produce(new JdbcDataSourceBuildItem("seata",
                            ConfigProvider.getConfig().getValue("quarkus.datasource.db-kind", String.class), true));
        }
    }

    @BuildStep
    void registerXidFilter(BuildProducer<CustomContainerRequestFilterBuildItem> requestFilterProducer,
            BuildProducer<CustomContainerResponseFilterBuildItem> responseFilterProducer, SeataConfig config) {
        if (config.enabled) {
            requestFilterProducer.produce(new CustomContainerRequestFilterBuildItem(SeataXIDResteasyFilter.class.getName()));
            responseFilterProducer.produce(new CustomContainerResponseFilterBuildItem(SeataXIDResteasyFilter.class.getName()));
        }
    }

    @BuildStep
    void registerXidClientRequestFilter(Capabilities capabilities, BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<AdditionalIndexedClassesBuildItem> additionalIndexedClassesBuildItem, SeataConfig config) {

        if (config.enabled && (capabilities.isPresent(Capability.REST_CLIENT)
                || capabilities.isPresent(Capability.RESTEASY_JSON_JACKSON_CLIENT)
                || capabilities.isPresent(Capability.RESTEASY_JSON_JSONB_CLIENT))) {
            additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(SeataXIDClientRequestFilter.class));
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, SeataXIDClientRequestFilter.class));
            additionalIndexedClassesBuildItem
                    .produce(new AdditionalIndexedClassesBuildItem(SeataXIDClientRequestFilter.class.getName()));
        }
    }

    /**
     * GlobalTransactional Interceptor
     *
     * @param interceptorRegister
     * @param additionalBeans
     */
    @BuildStep
    void addGlobalTransactionalInterceptor(BuildProducer<InterceptorBindingRegistrarBuildItem> interceptorRegister,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans, SeataConfig config, SeataServiceConfig seataServiceConfig) {
        if (config.enabled && !seataServiceConfig.disableGlobalTransaction) {
            interceptorRegister.produce(new InterceptorBindingRegistrarBuildItem(new InterceptorBindingRegistrar() {
                @Override
                public List<InterceptorBinding> getAdditionalBindings() {
                    InterceptorBinding globalTransactional = InterceptorBinding.of(GlobalTransactional.class);
                    return List.of(globalTransactional);
                }
            }));

            additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(GlobalTransactionalInterceptor.class));
        }

    }
}
