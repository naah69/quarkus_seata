package io.quarkiverse.seata.filter;

import io.seata.core.context.RootContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Objects;

@Priority(Priorities.USER)
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
