package io.quarkiverse.seata.consumer.service;

import java.math.BigDecimal;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkiverse.seata.annotation.GlobalTransactional;
import io.quarkiverse.seata.consumer.client.AccountClient;
import io.quarkiverse.seata.consumer.client.StockClient;

@ApplicationScoped
public class BusinessService {

    @RestClient
    StockClient stockClient;
    @RestClient
    AccountClient accountClient;

    /**
     * 减库存，下订单
     *
     * @param userId
     * @param commodityCode
     * @param orderCount
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public void purchase(String userId, String commodityCode, int orderCount) {
        stockClient.deduct(commodityCode, orderCount);

        BigDecimal orderMoney = new BigDecimal(orderCount).multiply(new BigDecimal(5));
        accountClient.debit(userId, orderMoney);
    }
}
