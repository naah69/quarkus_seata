package io.quarkiverse.seata.rest.client.reactive.filter;

import java.util.Objects;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

import io.seata.core.context.RootContext;

@Priority(Priorities.USER)
@Provider
public class SeataXIDClientRequestFilter implements ResteasyReactiveClientRequestFilter {
    @Override
    public void filter(ResteasyReactiveClientRequestContext requestContext) {

        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        String xid = RootContext.getXID();
        if (Objects.nonNull(xid)) {
            headers.add(RootContext.KEY_XID, xid);
        }
    }

}
