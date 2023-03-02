package io.quarkiverse.seata.resteasy.reactive.deployment;

import io.quarkiverse.seata.resteasy.reactive.filter.SeataXIDResteasyFilter;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerRequestFilterBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerResponseFilterBuildItem;

class SeataResteasyReactiveProcessor {

    private static final String FEATURE = "seata-resteasy-reactive";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerXidFilter(BuildProducer<CustomContainerRequestFilterBuildItem> requestFilterProducer,
            BuildProducer<CustomContainerResponseFilterBuildItem> responseFilterProducer) {
        String filterClass = SeataXIDResteasyFilter.class.getName();
        requestFilterProducer.produce(new CustomContainerRequestFilterBuildItem(filterClass));
        responseFilterProducer.produce(new CustomContainerResponseFilterBuildItem(filterClass));
    }

}
