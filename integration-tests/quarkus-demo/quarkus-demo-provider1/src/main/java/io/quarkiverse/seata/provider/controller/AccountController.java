package io.quarkiverse.seata.provider.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.quarkiverse.seata.provider.service.AccountService;

import java.math.BigDecimal;

/**
 * AccountController
 *
 * @author nayan
 * @date 2022/8/2 16:18
 */
@Path("")
public class AccountController {

    @Inject
    AccountService accountService;

    @Path("/debit")
    @GET
    public Boolean debit(String userId) {
        accountService.debit(userId, new BigDecimal(100));
        return true;
    }
}
