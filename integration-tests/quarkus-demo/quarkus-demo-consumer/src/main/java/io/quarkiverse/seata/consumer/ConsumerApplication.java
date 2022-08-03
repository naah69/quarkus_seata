package io.quarkiverse.seata.consumer;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * ConsumerApplication
 *
 * @author nayan
 * @date 2022/8/2 16:05
 */
@QuarkusMain
public class ConsumerApplication {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
