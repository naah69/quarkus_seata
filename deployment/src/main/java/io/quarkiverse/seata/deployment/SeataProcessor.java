package io.quarkiverse.seata.deployment;

import java.util.List;

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
import io.quarkiverse.seata.filter.SeataXIDResteasyFilter;
import io.quarkiverse.seata.interceptor.GlobalTransactionalInterceptor;
import io.quarkiverse.seata.jdbc.SeataAgroalConnectionConfigurer;
import io.quarkiverse.seata.runtime.SeataRecorder;
import io.quarkus.agroal.spi.JdbcDriverBuildItem;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.InterceptorBindingRegistrarBuildItem;
import io.quarkus.arc.processor.BuiltinScope;
import io.quarkus.arc.processor.InterceptorBindingRegistrar;
import io.quarkus.datasource.deployment.spi.DefaultDataSourceDbKindBuildItem;
import io.quarkus.datasource.deployment.spi.DevServicesDatasourceConfigurationHandlerBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.SslNativeConfigBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerRequestFilterBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerResponseFilterBuildItem;
import io.quarkus.runtime.ApplicationConfig;

public class SeataProcessor {

    private static final String FEATURE = "seata";
    private static final String SEATA_DB_KIND = "seata";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerDriver(BuildProducer<JdbcDriverBuildItem> jdbcDriver,
            SslNativeConfigBuildItem sslNativeConfigBuildItem) {
        jdbcDriver.produce(
                new JdbcDriverBuildItem(SEATA_DB_KIND, "io.quarkiverse.seata.jdbc.driver.SeataDriver"));
    }

    @BuildStep
    void configureAgroalConnection(BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            Capabilities capabilities) {
        if (capabilities.isPresent(Capability.AGROAL)) {
            additionalBeans
                    .produce(new AdditionalBeanBuildItem.Builder().addBeanClass(SeataAgroalConnectionConfigurer.class)
                            .setDefaultScope(BuiltinScope.APPLICATION.getName())
                            .setUnremovable()
                            .build());
        }
    }

    @BuildStep
    DevServicesDatasourceConfigurationHandlerBuildItem devDbHandler() {
        return DevServicesDatasourceConfigurationHandlerBuildItem.jdbc(SEATA_DB_KIND);
    }

    @BuildStep
    void registerDefaultDbType(BuildProducer<DefaultDataSourceDbKindBuildItem> dbKind) {
        dbKind.produce(new DefaultDataSourceDbKindBuildItem(SEATA_DB_KIND));
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

    @BuildStep
    void registerXidFilter(Capabilities capabilities,
            BuildProducer<CustomContainerRequestFilterBuildItem> requestFilterProducer,
            BuildProducer<CustomContainerResponseFilterBuildItem> responseFilterProducer,
            SeataConfig config) {
        if (config.enabled && capabilities.isCapabilityWithPrefixPresent(Capability.RESTEASY)) {
            requestFilterProducer.produce(new CustomContainerRequestFilterBuildItem(SeataXIDResteasyFilter.class.getName()));
            responseFilterProducer.produce(new CustomContainerResponseFilterBuildItem(SeataXIDResteasyFilter.class.getName()));
        }
    }

    @BuildStep
    void registerXidClientRequestFilter(Capabilities capabilities,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<AdditionalIndexedClassesBuildItem> additionalIndexedClassesBuildItem,
            SeataConfig config) {

        if (config.enabled && capabilities.isCapabilityWithPrefixPresent(Capability.REST_CLIENT)) {
            String filterClass = "io.quarkiverse.seata.filter.SeataXIDClientRequestFilter";
            additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(filterClass));
            reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, filterClass));
            additionalIndexedClassesBuildItem.produce(new AdditionalIndexedClassesBuildItem(filterClass));
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
            BuildProducer<AdditionalBeanBuildItem> additionalBeans, SeataConfig config,
            SeataServiceConfig seataServiceConfig) {
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
