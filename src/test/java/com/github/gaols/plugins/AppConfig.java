package com.github.gaols.plugins;

import com.github.gaols.plugins.model.Account;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ImportResource(locations = "/spring-context.xml")
public class AppConfig {

    @Bean
    public ActiveRecordPlugin activeRecord(DataSource ds) {
        ActiveRecordPlugin activeRecord = new SpringTxAwareActiveRecordPlugin(ds);
        activeRecord.setShowSql(true);
        activeRecord.addMapping("Account", "id", Account.class);
        activeRecord.start();
        return activeRecord;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

}
