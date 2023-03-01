package io.quarkiverse.seata.consumer.client;

import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * AccountClient
 *
 * @author nayan
 * @date 2022/8/2 16:52
 */
@Path("/")
@RegisterRestClient(baseUri = "http://127.0.0.1:8082")
public interface AccountClient {

    @GET
    @Path("/debit")
    void debit(@QueryParam("userId") String userId, @QueryParam("money") BigDecimal money);
}
