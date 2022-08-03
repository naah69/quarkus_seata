package io.quarkiverse.seata.provider.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import io.quarkiverse.seata.provider.service.StockService;

/**
 * StockController
 *
 * @author nayan
 * @date 2022/8/2 16:47
 */
@Path("")
public class StockController {
    @Inject
    StockService stockService;

    @GET
    @Path("/deduct")
    public Boolean deduct(@QueryParam("commodityCode") String commodityCode, @QueryParam("count") Integer count) {
        stockService.deduct(commodityCode, count);
        return true;
    }
}
