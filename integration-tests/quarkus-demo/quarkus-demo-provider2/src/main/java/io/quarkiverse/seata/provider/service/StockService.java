package io.quarkiverse.seata.provider.service;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import io.quarkiverse.seata.provider.entity.Stock;

@ApplicationScoped
public class StockService {

    @Transactional
    public void deduct(String commodityCode, int count) {
        Stock stock = Stock.find("commodityCode", commodityCode).firstResult();
        stock.count = stock.count - count;
        stock.persistAndFlush();
    }
}
