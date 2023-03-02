package io.quarkiverse.seata.dapr.rest.client.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.dapr.client.domain.InvokeMethodRequest;
import io.quarkiverse.dapr.restclient.filter.DaprRequestFilter;
import io.seata.core.context.RootContext;

/**
 * SeataXIDDaprRequestFilter
 *
 * @author nayan
 * @date 2023/3/2 13:56
 */
public class SeataXIDDaprRequestFilter implements DaprRequestFilter {

    @Override
    public void filter(InvokeMethodRequest invokeMethodRequest) {
        String xid = RootContext.getXID();
        if (Objects.nonNull(xid)) {
            Map<String, String> headers = Optional.ofNullable(invokeMethodRequest.getMetadata())
                    .orElse(new HashMap<>());
            headers.put(RootContext.KEY_XID, xid);
            invokeMethodRequest.setMetadata(headers);
        }
    }

}
