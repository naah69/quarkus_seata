package io.quarkiverse.seata.deployment;

import io.quarkiverse.seata.annotation.GlobalTransactional;
import io.quarkiverse.seata.config.SeataConfig;
import io.quarkiverse.seata.filter.SeataXIDClientRequestFilter;
import io.quarkiverse.seata.filter.SeataXIDResteasyFilter;
import io.quarkiverse.seata.interceptor.GlobalTransactionalInterceptor;
import io.quarkiverse.seata.runtime.SeataRecorder;
import io.quarkus.agroal.spi.JdbcDataSourceBuildItem;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.InterceptorBindingRegistrarBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.InterceptorBindingRegistrar;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerRequestFilterBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerResponseFilterBuildItem;
import io.quarkus.resteasy.reactive.spi.ExceptionMapperBuildItem;
import io.seata.rm.datasource.DataSourceProxy;
import org.jboss.jandex.DotName;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

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
                   SeataConfig config) {
        recorder.initPropertyMap();
        recorder.initClient(config);

    }

    /**
     * proxy datasource
     *
     * @param recorder
     * @param dataSourceBuildItems
     * @param syntheticBeanBuildItemBuildProducer
     */
    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void generateSeataProxyDataSource(SeataRecorder recorder,
                                      List<JdbcDataSourceBuildItem> dataSourceBuildItems,
                                      BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer) {
        List<String> dataSources = dataSourceBuildItems
                .stream().filter(ds -> !ds.isDefault()).map(ds -> ds.getName()).collect(Collectors.toList());

        SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
                .configure(DataSourceProxy.class)
                .addType(DATA_SOURCE)
                .scope(Singleton.class)
                .priority(Integer.MAX_VALUE)
                .setRuntimeInit()
                .unremovable()
                .supplier(recorder.seataDataSourceSupplier(dataSources));

        syntheticBeanBuildItemBuildProducer.produce(configurator.done());
    }

    @BuildStep
    void registerXidFilter(BuildProducer<CustomContainerRequestFilterBuildItem> requestFilterProducer, BuildProducer<CustomContainerResponseFilterBuildItem> responseFilterProducer) {
        requestFilterProducer.produce(new CustomContainerRequestFilterBuildItem(SeataXIDResteasyFilter.class.getName()));
        responseFilterProducer.produce(new CustomContainerResponseFilterBuildItem(SeataXIDResteasyFilter.class.getName()));
    }

    @BuildStep
    void registerXidClientRequestFilter(BuildProducer<AdditionalBeanBuildItem> additionalBeans,
                                        BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
                                        BuildProducer<AdditionalIndexedClassesBuildItem> additionalIndexedClassesBuildItem){
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(SeataXIDClientRequestFilter.class));
        reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, SeataXIDClientRequestFilter.class));
        additionalIndexedClassesBuildItem
                .produce(new AdditionalIndexedClassesBuildItem(SeataXIDClientRequestFilter.class.getName()));
    }


    /**
     * GlobalTransactional Interceptor
     *
     * @param interceptorRegister
     * @param additionalBeans
     */
    @BuildStep
    void addGlobalTransactionalInterceptor(BuildProducer<InterceptorBindingRegistrarBuildItem> interceptorRegister,
                                           BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        interceptorRegister.produce(new InterceptorBindingRegistrarBuildItem(
                new InterceptorBindingRegistrar() {
                    @Override
                    public List<InterceptorBinding> getAdditionalBindings() {
                        InterceptorBinding globalTransactional = InterceptorBinding.of(GlobalTransactional.class);
                        return List.of(globalTransactional);
                    }
                }));

        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(GlobalTransactionalInterceptor.class));
    }
}
