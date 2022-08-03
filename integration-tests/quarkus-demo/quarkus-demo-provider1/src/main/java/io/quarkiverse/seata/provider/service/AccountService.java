package io.quarkiverse.seata.provider.service;

import java.math.BigDecimal;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import io.quarkiverse.seata.provider.entity.Account;

@ApplicationScoped
public class AccountService {

    private static final String ERROR_USER_ID = "1002";

    @Transactional(rollbackOn = Exception.class)
    public void debit(String userId, BigDecimal num) {
        Account account = Account.find("userId", userId).firstResult();
        account.money = account.money.subtract(num);
        account.persistAndFlush();
        if (ERROR_USER_ID.equals(userId)) {
            throw new RuntimeException("account branch exception");
        }
    }
}
