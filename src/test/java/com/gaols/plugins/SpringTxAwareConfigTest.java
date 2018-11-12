package com.gaols.plugins;

import com.gaols.plugins.model.Account;
import com.gaols.plugins.service.AccountService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class SpringTxAwareConfigTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    AccountService accountService;

    @Test
    public void contextLoad() {
        Assert.assertNotNull(context);
        Assert.assertNotNull(context.getBean(DataSource.class));
    }

    @Test
    public void findUserByName() {
        Account account = accountService.findByName("gaols");
        Assert.assertNotNull(account);
    }

    @Test
    public void findUserByNameTx() {
        Account account = accountService.findByNameTx("gaols");
        Assert.assertNotNull(account);
    }

    @Test
    public void transfer() {
        int balanceFrom = accountService.getBalance("gaols");
        int balanceTo = accountService.getBalance("here");
        accountService.transfer("gaols", "here", 5);
        Assert.assertEquals(balanceFrom - 5, accountService.getBalance("gaols"));
        Assert.assertEquals(balanceTo + 5, accountService.getBalance("here"));
    }

    @Test
    public void transferEx() {
        int balanceFrom = accountService.getBalance("here");
        int balanceTo = accountService.getBalance("gaols");
        try {
            accountService.transfer("here", "gaols", 5);
        } catch (Exception ex) {
            Assert.assertEquals(balanceFrom, accountService.getBalance("here"));
            Assert.assertEquals(balanceTo, accountService.getBalance("gaols"));
        }
    }

    @Test
    public void batchInc() {
        int balanceFrom = accountService.getBalance("gaols");
        int balanceTo = accountService.getBalance("here");
        accountService.batchInc(10);
        Assert.assertEquals(balanceFrom + 10, accountService.getBalance("gaols"));
        Assert.assertEquals(balanceTo + 10, accountService.getBalance("here"));
    }

    @Test
    public void batchIncEx() {
        int balanceFrom = accountService.getBalance("gaols");
        int balanceTo = accountService.getBalance("here");
        try {
            accountService.batchInc(1);
        } catch (Exception ex) {
            Assert.assertEquals(balanceFrom, accountService.getBalance("gaols"));
            Assert.assertEquals(balanceTo, accountService.getBalance("here"));
        }
    }

}
