package com.gaols.plugins;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;

import javax.sql.DataSource;

public class SpringTxAwareActiveRecordPlugin extends ActiveRecordPlugin {
    public SpringTxAwareActiveRecordPlugin(String configName, DataSource dataSource, int transactionLevel) {
        this(new SpringTxAwareConfig(configName, dataSource, transactionLevel));
    }

    public SpringTxAwareActiveRecordPlugin(DataSource dataSource) {
        this(DbKit.MAIN_CONFIG_NAME, dataSource);
    }

    public SpringTxAwareActiveRecordPlugin(String configName, DataSource dataSource) {
        this(configName, dataSource, DbKit.DEFAULT_TRANSACTION_LEVEL);
    }

    public SpringTxAwareActiveRecordPlugin(DataSource dataSource, int transactionLevel) {
        this(DbKit.MAIN_CONFIG_NAME, dataSource, transactionLevel);
    }

    public SpringTxAwareActiveRecordPlugin(Config config) {
        super(config);
    }
}
