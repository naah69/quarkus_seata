package io.quarkiverse.seata.provider.service;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import io.quarkiverse.seata.provider.entity.Stock;
import io.quarkus.narayana.jta.QuarkusTransaction;

@ApplicationScoped
public class StockService {

    // @Transactional(rollbackOn = Exception.class)
    public void deduct(String commodityCode, int count) {
        // 使用上方注解 或 使用 QuarkusTransaction
        QuarkusTransaction.run(()->{
            Stock stock = Stock.find("commodityCode", commodityCode).firstResult();
            stock.count = stock.count - count;
            stock.persistAndFlush();
        });
    }
}
