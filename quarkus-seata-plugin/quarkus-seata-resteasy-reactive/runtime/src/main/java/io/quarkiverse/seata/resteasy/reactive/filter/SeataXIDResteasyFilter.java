package io.quarkiverse.seata.resteasy.reactive.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.seata.common.util.StringUtils;
import io.seata.core.context.RootContext;
import io.vertx.core.http.HttpServerRequest;

/**
 * SeataXIDResteasyFilter
 *
 * @author nayan
 * @date 2022/7/30 1:47 PM
 */
@Priority(Priorities.USER)
public class SeataXIDResteasyFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeataXIDResteasyFilter.class);

    @Context
    HttpServerRequest request;

    /**
     * request filter
     *
     * @param requestContext request context.
     * @throws IOException
     */
    @ServerRequestFilter(priority = Priorities.HEADER_DECORATOR)
    public void filterRequest(ContainerRequestContext requestContext) throws IOException {
        String xid = RootContext.getXID();
        String rpcXid = getRequestXID(requestContext);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("xid in RootContext[{}] xid in HttpContext[{}]", xid, rpcXid);
        }
        if (StringUtils.isBlank(xid) && StringUtils.isNotBlank(rpcXid)) {
            RootContext.bind(rpcXid);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("bind[{}] to RootContext", rpcXid);
            }
        }
    }

    /**
     * response filter
     *
     * @param requestContext request context.
     * @throws IOException
     */
    @ServerResponseFilter
    public void filterResponse(ContainerRequestContext requestContext) throws IOException {
        //        todo test if exception
        if (RootContext.inGlobalTransaction()) {
            cleanXid(getRequestXID(requestContext));
        }
    }

    public String getRequestXID(ContainerRequestContext requestContext) {
        return requestContext.getHeaderString(RootContext.KEY_XID);
    }

    public static void cleanXid(String rpcXid) {
        String xid = RootContext.getXID();
        if (StringUtils.isNotBlank(xid)) {
            String unbindXid = RootContext.unbind();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("unbind[{}] from RootContext", unbindXid);
            }
            if (!StringUtils.equalsIgnoreCase(rpcXid, unbindXid)) {
                LOGGER.warn("xid in change during RPC from {} to {}", rpcXid, unbindXid);
                if (StringUtils.isNotBlank(unbindXid)) {
                    RootContext.bind(unbindXid);
                    LOGGER.warn("bind [{}] back to RootContext", unbindXid);
                }
            }
        }
    }
}
