package io.quarkiverse.seata.provider.controller;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import io.quarkiverse.seata.provider.service.AccountService;

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
    public Boolean debit(@QueryParam("userId") String userId, @QueryParam("money") BigDecimal money) {
        accountService.debit(userId, money);
        return true;
    }
}
