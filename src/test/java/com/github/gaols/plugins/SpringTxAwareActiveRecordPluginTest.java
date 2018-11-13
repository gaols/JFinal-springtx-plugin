package com.github.gaols.plugins;


import com.github.gaols.plugins.service.AccountService;
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
public class SpringTxAwareActiveRecordPluginTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    AccountService accountService;

    @Test
    public void contextLoad() {
        Assert.assertNotNull(context);
        Assert.assertNotNull(context.getBean(DataSource.class));
    }

}
