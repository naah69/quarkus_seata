package io.quarkiverse.seata.dapr.rest.client.deployment;

import io.quarkiverse.seata.dapr.rest.client.filter.SeataXIDDaprRequestFilter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class SeataDaprRestClientProcessor {

    private static final String FEATURE = "seata-dapr-rest-client";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerDaprRequestFilter(Capabilities capabilities,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {

        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(SeataXIDDaprRequestFilter.class));
        reflectiveClass.produce(new ReflectiveClassBuildItem(true, true, SeataXIDDaprRequestFilter.class));

    }

}
