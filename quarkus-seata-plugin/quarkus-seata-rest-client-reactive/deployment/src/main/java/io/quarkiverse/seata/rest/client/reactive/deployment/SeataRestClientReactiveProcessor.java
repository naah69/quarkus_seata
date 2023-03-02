package io.quarkiverse.seata.rest.client.reactive.deployment;

import io.quarkiverse.seata.rest.client.reactive.filter.SeataXIDClientRequestFilter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class SeataRestClientReactiveProcessor {

    private static final String FEATURE = "seata-rest-client-reactive";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerXidClientRequestFilter(BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<AdditionalIndexedClassesBuildItem> additionalIndexedClassesBuildItem) {
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(SeataXIDClientRequestFilter.class));
        reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, SeataXIDClientRequestFilter.class));
        additionalIndexedClassesBuildItem
                .produce(new AdditionalIndexedClassesBuildItem(SeataXIDClientRequestFilter.class.getName()));
    }

}
