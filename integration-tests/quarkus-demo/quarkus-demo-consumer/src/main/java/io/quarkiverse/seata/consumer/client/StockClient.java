package io.quarkiverse.seata.consumer.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * StockClient
 *
 * @author nayan
 * @date 2022/8/2 16:52
 */

@Path("/")
@RegisterRestClient(baseUri = "http://127.0.0.1:8083")
public interface StockClient {

    @GET
    @Path("/deduct")
    void deduct(@QueryParam("commodityCode") String commodityCode, @QueryParam("count") Integer count);
}
