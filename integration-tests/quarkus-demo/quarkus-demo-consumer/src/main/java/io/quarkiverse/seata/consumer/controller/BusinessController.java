package io.quarkiverse.seata.consumer.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.quarkiverse.seata.consumer.service.BusinessService;

@Path("")
public class BusinessController {

    @Inject
    BusinessService businessService;

    /**
     * 购买下单，模拟全局事务提交
     *
     * @return
     */
    @GET
    @Path("/purchase/commit")
    public Boolean purchaseCommit() {
        businessService.purchase("1001", "2001", 1);
        return true;
    }

    /**
     * 购买下单，模拟全局事务回滚
     *
     * @return
     */
    @GET
    @Path("/purchase/rollback")
    public Boolean purchaseRollback() {
        try {
            businessService.purchase("1002", "2001", 1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
