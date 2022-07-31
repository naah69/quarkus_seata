package io.quarkiverse.seata.config.core;

import static io.seata.common.DefaultValues.*;

import io.quarkiverse.seata.config.StarterConstants;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = StarterConstants.TRANSPORT_PREFIX, phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SeataTransportConfig {

    /**
     * tcp, unix-domain-socket
     */
    @ConfigItem(defaultValue = "TCP")
    public String type;
    /**
     * NIO, NATIVE
     */
    @ConfigItem(defaultValue = "NIO")
    public String server;
    /**
     * enable heartbeat
     */
    @ConfigItem(defaultValue = DEFAULT_TRANSPORT_HEARTBEAT + "")
    public boolean heartbeat;
    /**
     * serialization
     */
    @ConfigItem(defaultValue = "seata")
    public String serialization;
    /**
     * compressor
     */
    @ConfigItem(defaultValue = "none")
    public String compressor;

    /**
     * enable client batch send request
     */
    @ConfigItem(defaultValue = DEFAULT_ENABLE_CLIENT_BATCH_SEND_REQUEST + "")
    public boolean enableClientBatchSendRequest;

    /**
     * enable TM client batch send request
     */
    @ConfigItem(defaultValue = DEFAULT_ENABLE_TM_CLIENT_BATCH_SEND_REQUEST + "")
    public boolean enableTmClientBatchSendRequest;

    /**
     * enable RM client batch send request
     */
    @ConfigItem(defaultValue = DEFAULT_ENABLE_RM_CLIENT_BATCH_SEND_REQUEST + "")
    public boolean enableRmClientBatchSendRequest;

    /**
     * enable TC server batch send response
     */
    @ConfigItem(defaultValue = DEFAULT_ENABLE_TC_SERVER_BATCH_SEND_RESPONSE + "")
    public boolean enableTcServerBatchSendResponse;

    /**
     * rpcRmRequestTimeout
     */
    @ConfigItem(defaultValue = "30000")
    public long rpcRmRequestTimeout;

    /**
     * rpcRmRequestTimeout
     */
    @ConfigItem(defaultValue = "30000")
    public long rpcTmRequestTimeout;

    /**
     * rpcTcRequestTimeout
     */
    @ConfigItem(defaultValue = "30000")
    public long rpcTcRequestTimeout;
}
