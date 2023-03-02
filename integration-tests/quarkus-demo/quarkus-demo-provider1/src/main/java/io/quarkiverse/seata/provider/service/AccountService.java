package io.quarkiverse.seata.provider.service;

import java.math.BigDecimal;

import javax.enterprise.context.ApplicationScoped;

import io.quarkiverse.seata.provider.entity.Account;
import io.quarkus.narayana.jta.QuarkusTransaction;

@ApplicationScoped
public class AccountService {

    private static final String ERROR_USER_ID = "1002";

    // @Transactional(rollbackOn = Exception.class)
    public void debit(String userId, BigDecimal num) {
        // 使用上方注解 或 使用 QuarkusTransaction
        QuarkusTransaction.run(() -> {
            Account account = Account.find("userId", userId).firstResult();
            account.money = account.money.subtract(num);
            account.persistAndFlush();
            if (ERROR_USER_ID.equals(userId)) {
                throw new RuntimeException("account branch exception");
            }
        });

    }
}
